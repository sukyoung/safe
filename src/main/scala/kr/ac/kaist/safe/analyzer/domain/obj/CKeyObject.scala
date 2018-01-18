/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap, HashSet }
import spray.json._

////////////////////////////////////////////////////////////////////////////////
// object abstract domain with concrete keys
////////////////////////////////////////////////////////////////////////////////
object CKeyObject extends ObjDomain {
  // bottom / top / empty object
  lazy val Bot = Elem(AbsMap.Bot(AbsDataProp.Bot), AbsMap.Bot(AbsIValue.Bot))
  lazy val Top = Elem(AbsMap.Top(AbsDataProp.Top), AbsMap.Top(AbsIValue.Top))
  lazy val Empty = Elem(AbsMap.Empty(AbsDataProp.Bot), AbsMap.Empty(AbsIValue.Bot))

  // abstraction function
  def alpha(obj: Obj): Elem = Elem(
    nmap = AbsMap(obj.nmap.foldLeft[Map[String, AbsOpt[AbsDataProp]]](HashMap()) {
      case (map, (str, dp)) => map + (str -> AbsOpt(AbsDataProp(dp)))
    }, AbsOpt(AbsDataProp.Bot, AbsAbsent.Top)),
    imap = AbsMap(obj.imap.foldLeft[Map[IName, AbsOpt[AbsIValue]]](HashMap()) {
      case (map, (iname, iv)) => map + (iname -> AbsOpt(AbsIValue(iv)))
    }, AbsOpt(AbsIValue.Bot, AbsAbsent.Top))
  )

  // from json value to abstract object
  def fromJson(v: JsValue): Elem = v match {
    case JsObject(m) => (
      m.get("nmap").map(AbsMap.fromJson(json2str, AbsDataProp.fromJson)),
      m.get("imap").map(AbsMap.fromJson(IName.fromJson, AbsIValue.fromJson))
    ) match {
        case (Some(n), Some(i)) => Elem(n, i)
        case _ => throw AbsObjParseError(v)
      }
    case _ => throw AbsObjParseError(v)
  }

  // abstract object element
  case class Elem(
      nmap: AbsMap[String, AbsDataProp],
      imap: AbsMap[IName, AbsIValue]
  ) extends ElemTrait {
    // concretization function
    def gamma: ConSet[Obj] = ConInf // TODO more precise

    // get single element
    def getSingle: ConSingle[Obj] = ConMany() // TODO more precise

    // partial order
    def ⊑(that: Elem): Boolean =
      this.nmap.⊑(_ ⊑ _)(that.nmap) && this.imap.⊑(_ ⊑ _)(that.imap)

    // join
    def ⊔(that: Elem): Elem =
      Elem(this.nmap.⊔(_ ⊔ _)(that.nmap), this.imap.⊔(_ ⊔ _)(that.imap))

    // meet
    def ⊓(that: Elem): Elem =
      Elem(this.nmap.⊓(_ ⊓ _)(that.nmap), this.imap.⊓(_ ⊓ _)(that.imap))

    override def toString: String = {
      val s = new StringBuilder
      s.append("Internal Properties:").append(LINE_SEP)
        .append(imap)
        .append("Normal Properties:").append(LINE_SEP)
        .append(nmap)
      s.toString
    }

    ////////////////////////////////////////////////////////////////
    // Additional untility functions
    ///////////////////////////////////////////////////////////////
    def isEmpty: Boolean = this == Empty

    // oldify locations
    def oldify(loc: Loc): Elem = loc match {
      case locR @ Recency(subLoc, Recent) => subsLoc(locR, Recency(subLoc, Old))
      case _ => this
    }

    // substitute locR by locO
    def subsLoc(locR: Recency, locO: Recency): Elem = Elem(
      nmap = nmap.mapCValues { dp => dp.copy(dp.value.subsLoc(locR, locO)) },
      imap = imap.mapCValues { iv => iv.copy(iv.value.subsLoc(locR, locO)) }
    )

    // weakly substitute locR by locO
    def weakSubsLoc(locR: Recency, locO: Recency): Elem = Elem(
      nmap = nmap.mapCValues { dp => dp.copy(dp.value.weakSubsLoc(locR, locO)) },
      imap = imap.mapCValues { iv => iv.copy(iv.value.weakSubsLoc(locR, locO)) }
    )

    // lookup
    private def lookup(astr: AbsStr): AbsOpt[AbsDataProp] = astr.gamma match {
      case ConInf => nmap.map.foldLeft(nmap.default) {
        case (res, (str, value)) if astr.isRelated(str) => res.⊔(_ ⊔ _)(value)
        case (res, _) => res
      }
      case ConFin(set) => set.foldLeft(AbsOpt.Bot(AbsDataProp.Bot)) {
        case (res, key) => res.⊔(_ ⊔ _)(nmap(key))
      }
    }
    def apply(str: String): AbsDataProp = nmap(str).content
    def apply(astr: AbsStr): AbsDataProp = lookup(astr).content
    def apply(iname: IName): AbsIValue = imap(iname).content

    // strong update for normal properties
    def update(str: String, dp: AbsDataProp): Elem = {
      if (dp.isBottom) Bot
      else copy(nmap = nmap.update(str, AbsOpt(dp, AbsAbsent.Bot)))
    }

    // weak update for normal properties
    def weakUpdate(astr: AbsStr, dp: AbsDataProp): Elem = {
      if (dp.isBottom) Bot
      else astr.gamma match {
        case ConInf => copy(nmap = nmap.mapCValues(_ ⊔ dp, (str, _) => astr.isRelated(str)))
        case ConFin(set) if set.size == 1 => update(set.head, dp) // TODO it is unsound in some cases
        case ConFin(set) => copy(nmap = set.foldLeft(nmap) {
          case (map, str) => map.update(str, map(str).⊔(_ ⊔ _)(AbsOpt(dp, AbsAbsent.Bot)))
        })
      }
    }

    // strong update for internal properties
    def update(iname: IName, iv: AbsIValue): Elem = {
      if (iv.isBottom) Bot
      else copy(imap = imap.update(iname, AbsOpt(iv, AbsAbsent.Bot)))
    }

    // strong delete
    def delete(str: String): Elem = copy(nmap = nmap.update(str, AbsOpt(AbsDataProp.Bot, AbsAbsent.Top)))

    // weak delete
    def weakDelete(astr: AbsStr): Elem = astr.gamma match {
      case ConInf => copy(nmap = nmap.mapValues(_.⊔(_ ⊔ _)(AbsOpt(AbsDataProp.Bot, AbsAbsent.Top)), (str, _) => astr.isRelated(str)))
      case ConFin(set) if set.size == 1 => delete(set.head)
      case ConFin(set) => copy(nmap = set.foldLeft(nmap) {
        case (map, str) => map.update(str, map(str).⊔(_ ⊔ _)(AbsOpt(AbsDataProp.Bot, AbsAbsent.Top)))
      })
    }

    // contain check
    def contains(str: String): AbsBool = nmap.contains(_.isBottom)(str)
    def contains(astr: AbsStr): AbsBool = astr.gamma match {
      case ConInf => nmap.map.foldLeft(nmap.default.exists(_.isBottom)) {
        case (res, (str, opt)) =>
          if (astr.isRelated(str)) res ⊔ opt.exists(_.isBottom)
          else res
      }
      case ConFin(set) => set.foldLeft(AbsBool.Bot) {
        case (res, str) => res ⊔ contains(str)
      }
    }
    def contains(iname: IName): AbsBool = imap.contains(_.isBottom)(iname)

    // abstract key set
    def abstractKeySet: ConSet[AbsStr] = ConFin(nmap.map.keySet.map(AbsStr(_)) + AbsStr.Top)

    // abstract key set with filtering
    def abstractKeySet(filter: (AbsStr, AbsDataProp) => Boolean): ConSet[AbsStr] = {
      val initial: Set[AbsStr] =
        if (filter(AbsStr.Top, nmap.default.content)) HashSet(AbsStr.Top)
        else HashSet()
      ConFin(nmap.map.keySet.foldLeft(initial) {
        case (set, str) =>
          val astr = AbsStr(str)
          if (filter(astr, nmap(str).content)) set + astr
          else set
      })
    }

    // collect key set with prefix
    def collectKeySet(prefix: String): ConSet[String] = ConFin(nmap.map.keySet.filter(_ startsWith prefix))

    // key set pair
    def keySetPair(h: AbsHeap): (List[String], AbsStr) = {
      var visited = HashSet[Loc]()
      def visit(currObj: Elem): (Set[String], AbsStr) = {
        val pair = currObj.ownKeySetPair
        val proto = currObj(IPrototype)
        proto.value.locset.foldLeft(pair) {
          case ((strSet, astr), loc) => {
            if (visited contains loc) (strSet, astr)
            else {
              visited += loc
              val (newStrSet, newAStr) = visit(h.get(loc))
              (strSet ++ newStrSet, astr ⊔ newAStr)
            }
          }
        }
      }
      val (strSet, astr) = visit(this)
      (strSet.toList.sortBy { _.toString }, astr) // TODO for-in order
    }
    private def ownKeySetPair: (Set[String], AbsStr) = nmap.map.keySet.foldLeft((HashSet[String](), AbsStr.Bot)) {
      case ((strSet, astr), key) => {
        val AbsOpt(content, absent) = nmap(key)
        val isEnum = content.enumerable
        if (AT ⊑ isEnum) {
          val isDef = absent.isBottom
          if (isDef && (AbsBool.Top != isEnum)) (strSet + key, astr)
          else (strSet, astr ⊔ AbsStr(key))
        } else (strSet, astr)
      }
    }

    def isDefinite(astr: AbsStr): Boolean = astr.gamma match {
      case ConInf => false
      case ConFin(set) => set.foldLeft(true) {
        case (res, str) =>
          val AbsOpt(content, absent) = nmap(str)
          res && !content.isBottom && absent.isBottom
      }
    }

    ////////////////////////////////////////////////////////////////
    // internal methods of ECMAScript Object
    ///////////////////////////////////////////////////////////////
    // Section 8.12.1 [[GetOwnProperty]](P)
    def GetOwnProperty(P: AbsStr): (AbsDesc, AbsUndef) = {
      val AbsOpt(dp, absent) = lookup(P)
      val undef =
        if (absent.isBottom) AbsUndef.Bot else AbsUndef.Top
      val desc = AbsDesc(
        (dp.value, AbsAbsent.Bot),
        (dp.writable, AbsAbsent.Bot),
        (dp.enumerable, AbsAbsent.Bot),
        (dp.configurable, AbsAbsent.Bot)
      )
      (desc, undef)
    }

    // Section 8.12.2 [[GetProperty]](P)
    def GetProperty(P: AbsStr, h: AbsHeap): (AbsDesc, AbsUndef) = {
      var visited = HashSet[Loc]()
      def visit(currObj: Elem): (AbsDesc, AbsUndef) = {
        val (desc, undef) = currObj.GetOwnProperty(P)
        val (parentDesc, parentUndef) =
          if (undef.isBottom) (AbsDesc.Bot, AbsUndef.Bot)
          else {
            val proto = currObj(IPrototype)
            val undef =
              if (proto.value.pvalue.nullval.isBottom) AbsUndef.Top
              else AbsUndef.Bot
            proto.value.locset.foldLeft((AbsDesc.Bot, AbsUndef.Bot)) {
              case ((desc, undef), loc) => {
                if (visited contains loc) (desc, undef)
                else {
                  visited += loc
                  val (newDesc, newUndef) = visit(h.get(loc))
                  (desc ⊔ newDesc, undef ⊔ newUndef)
                }
              }
            }
          }
        (desc ⊔ parentDesc, undef ⊔ parentUndef)
      }
      visit(this)
    }

    // Section 8.12.3 [[Get]](P)
    def Get(astr: AbsStr, h: AbsHeap): AbsValue = {
      var visited = HashSet[Loc]()
      val valueBot = AbsValue.Bot
      def visit(currentObj: Elem): AbsValue = {
        val test = currentObj contains astr
        val v1 =
          if (AbsBool.True ⊑ test) currentObj(astr).value
          else valueBot
        val v2 =
          if (AbsBool.False ⊑ test) {
            val protoV = currentObj(IPrototype).value
            val v3 = protoV.pvalue.nullval.fold(valueBot)({ _ =>
              AbsUndef.Top
            })
            v3 ⊔ protoV.locset.foldLeft(valueBot)((v, protoLoc) => {
              if (visited contains protoLoc) v
              else {
                visited += protoLoc
                v ⊔ visit(h.get(protoLoc))
              }
            })
          } else valueBot
        v1 ⊔ v2
      }
      visit(this)
    }

    // Section 8.12.4 [[CanPut]](P)
    def CanPut(P: AbsStr, h: AbsHeap): AbsBool = {
      val (desc, undef) = GetOwnProperty(P)
      val (b, _) = desc.writable
      val newB = {
        if (undef.isBottom) AbsBool.Bot
        else {
          val proto = this(IPrototype).value
          val extensible = this(IExtensible).value.pvalue.boolval
          val nullProtoCase =
            if (proto.pvalue.nullval.isBottom) AbsBool.Bot
            else extensible
          val (inheritDesc, inheritUndef) = proto.locset.foldLeft((AbsDesc.Bot, AbsUndef.Bot)) {
            case ((desc, undef), loc) => {
              val (newDesc, newUndef) = GetProperty(P, h)
              (desc ⊔ newDesc, undef ⊔ newUndef)
            }
          }
          val undefInheritCase =
            if (inheritUndef.isBottom) AbsBool.Bot
            else extensible
          val b = extensible
          val f =
            if (AF ⊑ b) {
              AbsBool.False
            } else AbsBool.Bot
          val t =
            if (AT ⊑ b) {
              val (b, _) = inheritDesc.writable
              b
            } else AbsBool.Bot
          val inheritCase = t ⊔ f
          nullProtoCase ⊔ undefInheritCase ⊔ inheritCase
        }
      }
      b ⊔ newB
    }

    // Section 8.12.5 [[Put]](P, V, Throw)
    def Put(P: AbsStr, V: AbsValue, Throw: Boolean = true, h: AbsHeap): (Elem, Set[Exception]) = {
      val canPut = CanPut(P, h)
      val (falseObj: Elem, falseExcSet: Set[Exception]) =
        if (AbsBool.False ⊑ canPut) {
          if (Throw) (Bot, HashSet(TypeError))
          else (this, ExcSetEmpty)
        } else (Bot, ExcSetEmpty)
      val (trueObj, trueExcSet) =
        if (AbsBool.True ⊑ canPut) {
          val (ownDesc, ownUndef) = GetOwnProperty(P)
          val (ownObj, ownExcSet) =
            if (ownDesc.isBottom) (Bot, ExcSetEmpty)
            else {
              val valueDesc = AbsDesc((V, AbsAbsent.Bot))
              val (o, _, e) = DefineOwnProperty(P, valueDesc, Throw, h)
              (o, e)
            }
          val (undefObj, undefExcSet) =
            if (ownUndef.isBottom) (Bot, ExcSetEmpty)
            else {
              val desc = GetProperty(P, h)
              val newDesc = AbsDesc(
                (V, AbsAbsent.Bot),
                (AbsBool.True, AbsAbsent.Bot),
                (AbsBool.True, AbsAbsent.Bot),
                (AbsBool.True, AbsAbsent.Bot)
              )
              val (o, _, e) = DefineOwnProperty(P, newDesc, Throw, h)
              (o, e)
            }
          (ownObj ⊔ undefObj, ownExcSet ++ undefExcSet)
        } else (Bot, ExcSetEmpty)
      (falseObj ⊔ trueObj, falseExcSet ++ trueExcSet)
    }

    // Section 8.12.6 [[HasProperty]](P)
    def HasProperty(P: AbsStr, h: AbsHeap): AbsBool = {
      var visited = AbsLoc.Bot
      def visit(currObj: Elem): AbsBool = {
        val test = currObj contains P
        val b1 =
          if (AbsBool.True ⊑ test) AbsBool.True
          else AbsBool.Bot
        val b2 =
          if (AbsBool.False ⊑ test) {
            val protoV = currObj(IPrototype).value
            val b3 = protoV.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
            b3 ⊔ protoV.locset.foldLeft[AbsBool](AbsBool.Bot)((b, protoLoc) =>
              if (visited contains protoLoc) b
              else {
                visited += protoLoc
                b ⊔ visit(h.get(protoLoc))
              })
          } else AbsBool.Bot
        b1 ⊔ b2
      }
      visit(this)
    }

    // Section 8.12.7 [[Delete]](P, Throw)
    def Delete(astr: AbsStr, Throw: Boolean): (Elem, AbsBool, Set[Exception]) = {
      val BOT = (Bot, AbsBool.Bot, ExcSetEmpty)
      val (desc, undef) = GetOwnProperty(astr)
      val (undefO, undefB, undefE) =
        if (undef.isBottom) BOT
        else (this, AT, ExcSetEmpty)
      val (descO, descB, descE) =
        if (desc.isBottom) BOT
        else {
          val (configurable, _) = desc.configurable
          val (confO, conB, confE) =
            if (AT ⊑ configurable) (weakDelete(astr), AT, ExcSetEmpty)
            else BOT
          val (otherO, otherB, otherE: Set[Exception]) =
            if (AF ⊑ configurable) if (Throw) (Bot, AbsBool.Bot, HashSet(TypeError)) else (this, AF, ExcSetEmpty)
            else BOT
          (confO ⊔ otherO, conB ⊔ otherB, confE ++ otherE)
        }
      (undefO ⊔ descO, undefB ⊔ descB, undefE ++ descE)
    }

    // Section 8.12.8 [[DefaultValue]](hint)
    def DefaultValue(hint: String, h: AbsHeap): AbsPValue = {
      hint match {
        case "String" => DefaultValueAsString(h)
        case "Number" => DefaultValueAsNumber(h)
        case _ => DefaultValue(h)
      }
    }

    def DefaultValue(h: AbsHeap): AbsPValue = {
      val className = this(IClass)
      val isDateClass = className.value.pvalue.strval StrictEquals AbsStr("Date")
      val b = isDateClass
      val t =
        if (AT ⊑ b) {
          DefaultValueAsString(h)
        } else AbsPValue.Bot
      val f =
        if (AF ⊑ b) {
          DefaultValueAsNumber(h)
        } else AbsPValue.Bot
      t ⊔ f
    }

    private def DefaultValueAsString(h: AbsHeap): AbsPValue = {
      val toString = Get("toString", h)
      val isCallable = TypeConversionHelper.IsCallable(toString, h)
      val str =
        if (AbsBool.True ⊑ isCallable) AbsPValue(strval = AbsStr.Top)
        else AbsPValue.Bot
      if (AbsBool.False ⊑ isCallable) {
        val valueOf = Get("valueOf", h)
        val value =
          if (AbsBool.True ⊑ TypeConversionHelper.IsCallable(valueOf, h)) AbsPValue.Top
          else AbsPValue.Bot
        str ⊔ value
      } else str
    }

    private def DefaultValueAsNumber(h: AbsHeap): AbsPValue = {
      val valueOf = Get("valueOf", h)
      val isCallable = TypeConversionHelper.IsCallable(valueOf, h)
      val value =
        if (AbsBool.True ⊑ isCallable) AbsPValue.Top
        else AbsPValue.Bot
      if (AbsBool.False ⊑ isCallable) {
        val toString = Get("toString", h)
        val str =
          if (AbsBool.True ⊑ TypeConversionHelper.IsCallable(toString, h)) AbsPValue(strval = AbsStr.Top)
          else AbsPValue.Bot
        value ⊔ str
      } else value
    }

    //Section 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
    def DefineOwnProperty(P: AbsStr, Desc: AbsDesc, Throw: Boolean = true, h: AbsHeap): (Elem, AbsBool, Set[Exception]) = {
      val obj = this
      val Reject =
        if (Throw) (obj, AbsBool.Bot, HashSet(TypeError))
        else (obj, AbsBool.False, ExcSetEmpty)
      val BotTriple = (Bot, AbsBool.Bot, ExcSetEmpty)
      // 1. Let current be the result of calling the [[GetOwnProperty]] internal method of O with property name P.
      val (current, curUndef) = GetOwnProperty(P)
      // 2. Let extensible be the value of the [[Extensible]] internal property of O.
      val extensible = obj(IExtensible).value.pvalue.boolval

      val (obj1, b1, excSet1) = if (curUndef.isTop) {
        val (obj1, b1, excSet1) =
          // 4. If current is undefined and extensible is true, then
          if (AbsBool.True ⊑ extensible) {
            // i. Create an own data property named P of object O whose [[Value]], [[Writable]],
            // [[Enumerable]] and [[Configurable]] attribute values are described by Desc.
            val changedObj = weakUpdate(P, AbsDataProp(Desc))
            (changedObj, AbsBool.True, ExcSetEmpty)
          } else BotTriple
        val (obj2: Elem, b2, excSet2: Set[Exception]) =
          // 3. If current is undefined and extensible is false, then Reject.
          if (AbsBool.False ⊑ extensible) Reject
          else BotTriple
        (obj1 ⊔ obj2, b1 ⊔ b2, excSet1 ++ excSet2)
      } else BotTriple

      val (cv, cva) = current.value
      val (cw, cwa) = current.writable
      val (ce, cea) = current.enumerable
      val (cc, cca) = current.configurable
      val (dv, dva) = Desc.value
      val (dw, dwa) = Desc.writable
      val (de, dea) = Desc.enumerable
      val (dc, dca) = Desc.configurable

      // 5. Return true, if every field in Desc is absent.
      val (obj5, b5) =
        if (dva.isTop && dwa.isTop && dea.isTop && dca.isTop) (obj, AbsBool.True)
        else (Bot, AbsBool.Bot)
      // 6. Return true, if every field in Desc also occurs in current and the value of every field in Desc is the
      // same value as the corresponding field in current when compared using the SameValue algorithm (9.12).
      val (obj6, b6) =
        if ((dva.isTop || (!dv.isBottom && AbsBool.True ⊑ (TypeConversionHelper.SameValue(h, dv, cv)))) &&
          (dwa.isTop || (!dw.isBottom && AbsBool.True ⊑ (dw StrictEquals cw))) &&
          (dea.isTop || (!de.isBottom && AbsBool.True ⊑ (de StrictEquals ce))) &&
          (dca.isTop || (!dc.isBottom && AbsBool.True ⊑ (dc StrictEquals cc)))) (obj, AbsBool.True)
        else (Bot, AbsBool.Bot)

      // 7. If the [[Configurable]] field of current is false then
      val (obj2: Elem, b2, excSet2: Set[Exception]) =
        if (AbsBool.False ⊑ cc) {
          // a. Reject, if the [[Configurable]] field of Desc is true.
          if (AbsBool.True ⊑ dc) Reject
          // b. Reject, if the [[Enumerable]] field of Desc is present and the [[Enumerable]] fields of current and
          // Desc are the Boolean negation of each other.
          else if (!de.isBottom &&
            (AbsBool.True ⊑ de && AbsBool.False ⊑ ce || AbsBool.True ⊑ ce && AbsBool.False ⊑ de)) Reject
          else BotTriple
        } else BotTriple

      // 10. Else, if IsDataDescriptor(current) and IsDataDescriptor(Desc) are both true, then
      val (obj7: Elem, b7, excSet7: Set[Exception]) =
        // a. If the [[Configurable]] field of current is false, then
        if (AbsBool.False ⊑ cc) {
          // i. Reject, if the [[Writable]] field of current is false and the [[Writable]] field of Desc is true.
          if (AbsBool.False ⊑ cw && AbsBool.True ⊑ dw) Reject
          // ii. If the [[Writable]] field of current is false, then
          else if (AbsBool.False ⊑ cw &&
            // 1. Reject, if the [[Value]] field of Desc is present and SameValue(Desc.[[Value]],
            // current.[[Value]]) is false.
            !dv.isBottom && AbsBool.False ⊑ (TypeConversionHelper.SameValue(h, dv, cv))) Reject
          else BotTriple
        } else BotTriple

      // 12. For each attribute field of Desc that is present, set the correspondingly named attribute of the
      // property named P of object O to the value of the field.
      val (obj3, b3, excSet3) =
        if (AbsBool.True ⊑ cc || AbsBool.True ⊑ cw) {
          var newDP = obj(P)
          if (!dv.isBottom) newDP = newDP.copy(value = dv)
          if (!dw.isBottom) newDP = newDP.copy(writable = dw)
          if (!de.isBottom) newDP = newDP.copy(enumerable = de)
          if (!dc.isBottom) newDP = newDP.copy(configurable = dc)
          val changedObj = weakUpdate(P, newDP)
          (changedObj, AbsBool.True, ExcSetEmpty)
        } else BotTriple

      val excSet4 =
        if (AbsStr("Array") ⊑ obj(IClass).value.pvalue.strval &&
          AbsStr("length") ⊑ P &&
          AbsBool.False ⊑ (TypeConversionHelper.ToNumber(dv) StrictEquals TypeConversionHelper.ToUint32(dv))) HashSet(RangeError)
        else ExcSetEmpty

      val excSet5 =
        if (AbsStr("Array") ⊑ obj(IClass).value.pvalue.strval &&
          AbsStr("length") ⊑ P &&
          AbsBool.False ⊑ obj(AbsStr.Number).configurable) HashSet(TypeError)
        else ExcSetEmpty

      val excSet6 =
        if (AbsStr("Array") ⊑ obj(IClass).value.pvalue.strval &&
          AT ⊑ (P StrictEquals AbsStr.Number) &&
          AbsBool.False ⊑ obj("length").writable) HashSet(TypeError)
        else ExcSetEmpty

      // TODO: unsound. Should Reject if an array element could not be deleted, i.e., it's not configurable.
      // TODO: Implement DefineOwnProperty for Array objects (15.4.5.1).

      (
        obj1 ⊔ obj2 ⊔ obj3 ⊔ obj5 ⊔ obj6 ⊔ obj7, b1 ⊔ b2 ⊔ b3 ⊔ b5 ⊔ b6 ⊔ b7,
        excSet1 ++ excSet2 ++ excSet3 ++ excSet4 ++ excSet5 ++ excSet6 ++ excSet7
      )
    }

    def toJson: JsValue = JsObject(
      ("nmap", nmap.toJson(JsString(_), _.toJson)),
      ("imap", imap.toJson(_.toJson, _.toJson))
    )
  }

  ////////////////////////////////////////////////////////////////
  // new Object constructors
  ////////////////////////////////////////////////////////////////
  def newObject: Elem = newObject(BuiltinObjectProto.loc)

  def newObject(loc: Loc): Elem = newObject(AbsLoc(loc))

  def newObject(locSet: AbsLoc): Elem = {
    Empty
      .update(IClass, AbsIValue(AbsStr("Object")))
      .update(IPrototype, AbsIValue(locSet))
      .update(IExtensible, AbsIValue(AT))
  }

  def newArgObject(absLength: AbsNum = AbsNum(0)): Elem = {
    Empty
      .update(IClass, AbsIValue(AbsStr("Arguments")))
      .update(IPrototype, AbsIValue(BuiltinObjectProto.loc))
      .update(IExtensible, AbsIValue(AT))
      .update("length", AbsDataProp(absLength, AT, AF, AT))
  }

  def newArrayObject(absLength: AbsNum = AbsNum(0)): Elem = {
    Empty
      .update(IClass, AbsIValue(AbsStr("Array")))
      .update(IPrototype, AbsIValue(BuiltinArrayProto.loc))
      .update(IExtensible, AbsIValue(AT))
      .update("length", AbsDataProp(absLength, AT, AF, AF))
  }

  def newFunctionObject(fid: FunctionId, env: AbsValue, l: Loc, n: AbsNum): Elem = {
    newFunctionObject(Some(fid), Some(fid), env, Some(l), n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], n: AbsNum): Elem = {
    newFunctionObject(fidOpt, constructIdOpt, env,
      locOpt, AT, AF, AF, n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNum): Elem = {
    val obj1 =
      Empty
        .update(IClass, AbsIValue(AbsStr("Function")))
        .update(IPrototype, AbsIValue(BuiltinFunctionProto.loc))
        .update(IExtensible, AbsIValue(AT))
        .update(IScope, AbsIValue(env))
        .update("length", AbsDataProp(absLength, AF, AF, AF))

    val obj2 = fidOpt match {
      case Some(fid) => obj1.update(ICall, AbsFId(fid))
      case None => obj1
    }
    val obj3 = constructIdOpt match {
      case Some(cid) => obj2.update(IConstruct, AbsFId(cid))
      case None => obj2
    }
    val obj4 = locOpt match {
      case Some(loc) =>
        val prototypeVal = AbsValue(loc)
        obj3.update(IHasInstance, AbsIValue(AbsNull.Top))
          .update("prototype", AbsDataProp(prototypeVal, writable, enumerable, configurable))
      case None => obj3
    }
    obj4
  }

  def newBooleanObj(absB: AbsBool): Elem = {
    val newObj = newObject(BuiltinBooleanProto.loc)
    newObj.update(IClass, AbsIValue(AbsStr("Boolean")))
      .update(IPrimitiveValue, AbsIValue(absB))
  }

  def newNumberObj(absNum: AbsNum): Elem = {
    val newObj = newObject(BuiltinNumberProto.loc)
    newObj.update(IClass, AbsIValue(AbsStr("Number")))
      .update(IPrimitiveValue, AbsIValue(absNum))
  }

  def newStringObj(absStr: AbsStr): Elem = {
    val newObj = newObject(BuiltinStringProto.loc)

    val newObj2 = newObj
      .update(IClass, AbsIValue(AbsStr("String")))
      .update(IPrimitiveValue, AbsIValue(absStr))

    absStr.gamma match {
      case ConFin(strSet) =>
        strSet.foldLeft(Bot)((obj, str) => {
          val length = str.length
          val newObj3 = (0 until length).foldLeft(newObj2)((tmpObj, tmpIdx) => {
            val charAbsStr = AbsStr(str.charAt(tmpIdx).toString)
            val charVal = AbsValue(charAbsStr)
            tmpObj.update(tmpIdx.toString, AbsDataProp(charVal, AF, AT, AF))
          })
          val lengthVal = AbsValue(length)
          obj ⊔ newObj3.update("length", AbsDataProp(lengthVal, AF, AF, AF))
        })
      case _ =>
        newObj2
          .weakUpdate(AbsStr.Number, AbsDataProp(AbsValue(AbsStr.Top), AF, AT, AF))
          .update("length", AbsDataProp(absStr.length, AF, AF, AF))
    }
  }

  def newErrorObj(errorName: String, protoLoc: Loc): Elem = {
    Empty
      .update(IClass, AbsIValue(AbsStr(errorName)))
      .update(IPrototype, AbsIValue(protoLoc))
      .update(IExtensible, AbsIValue(AbsBool.True))
  }

  def defaultValue(locSet: AbsLoc): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else AbsPValue.Top
  }

  def defaultValue(locSet: AbsLoc, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else {
      preferredType match {
        case "Number" => AbsPValue(numval = AbsNum.Top)
        case "String" => AbsPValue(strval = AbsStr.Top)
        case _ => AbsPValue.Top
      }
    }
  }

  def defaultValue(locSet: AbsLoc, h: AbsHeap, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else locSet.foldLeft(AbsPValue.Bot)((pv, loc) => h.get(loc).DefaultValue(preferredType, h))
  }

  // 8.10.4 FromPropertyDescriptor ( Desc )
  def FromPropertyDescriptor(h: AbsHeap, desc: AbsDesc): (Elem, Set[Exception]) = {
    def put(
      obj: Elem,
      name: String,
      pair: (AbsValue, AbsAbsent)
    ): (Elem, AbsBool, Set[Exception]) = {
      val T = (AbsBool.True, AbsAbsent.Bot)
      obj.DefineOwnProperty(
        AbsStr(name),
        AbsDesc(pair, T, T, T),
        false,
        h
      )
    }
    def toValue(pair: (AbsBool, AbsAbsent)): (AbsValue, AbsAbsent) = {
      val (b, a) = pair
      (AbsValue(b), a)
    }
    val (obj1, _, excSet1) = put(newObject, "value", desc.value)
    val (obj2, _, excSet2) = put(obj1, "writable", toValue(desc.writable))
    val (obj3, _, excSet3) = put(obj2, "enumerable", toValue(desc.enumerable))
    val (obj4, _, excSet4) = put(obj3, "configurable", toValue(desc.configurable))
    (obj4, excSet1 ++ excSet2 ++ excSet3 ++ excSet4)
  }
}
