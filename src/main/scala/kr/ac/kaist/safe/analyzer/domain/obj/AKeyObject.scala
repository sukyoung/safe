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
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap, HashSet }

////////////////////////////////////////////////////////////////////////////////
// object abstract domain with abstract keys
////////////////////////////////////////////////////////////////////////////////
object AKeyObject extends AbsObjectUtil {
  private lazy val atrue = AbsBool.True
  private lazy val afalse = AbsBool.False

  case object Top extends Dom
  case class ObjMap(
    val amap: APropMap,
    val imap: ObjInternalMap = ObjEmptyIMap
  ) extends Dom
  lazy val Bot: AbsObject = ObjMap(APropMapBot)
  lazy val Empty: AbsObject = ObjMap(APropMapEmpty)

  def alpha(obj: Object): Dom = {
    val amap: APropMap = obj.amap.foldLeft[APropMap](APropMapEmpty) {
      case (map, (key, dp)) => map.update(key, AbsDataProp(dp))
    }
    val imap: ObjInternalMap = obj.imap.foldLeft[ObjInternalMap](ObjEmptyIMap) {
      case (map, (iname, ivalue)) =>
        map + (iname -> (ivalue match {
          case FId(fid) => AbsIValue(AbsValue.Bot, AbsFId(fid))
          case v: Value => AbsIValue(AbsValue(v), AbsFId())
        }))
    }
    ObjMap(amap, imap)
  }

  sealed abstract class Dom extends AbsObject {
    def gamma: ConSet[Object] = ConInf() // TODO more precise

    def getSingle: ConSingle[Object] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsObject): Boolean = (this, check(that)) match {
      case (_, Top) => true
      case (Top, _) => false
      case (left: ObjMap, right: ObjMap) =>
        if (left.isEmpty) true
        else if (right.isEmpty) false
        else if (!(left.imap.keySet subsetOf right.imap.keySet)) false
        else (left.amap <= right.amap) &&
          right.imap.forall {
            case (key, rightIV) => {
              left.imap.get(key) match {
                case None => false
                case Some(leftIV) => leftIV <= rightIV
              }
            }
          }
    }

    def +(that: AbsObject): AbsObject = (this, check(that)) match {
      case (Top, _) | (_, Top) => Top
      case (left: ObjMap, right: ObjMap) =>
        val newAMap = left.amap + right.amap

        val ikeys = left.imap.keySet ++ right.imap.keySet
        val newIMap = ikeys.foldLeft(ObjEmptyIMap)((im, key) => {
          val leftIVal = left.imap.get(key)
          val rightIVal = right.imap.get(key)
          (leftIVal, rightIVal) match {
            case (None, None) => im
            case (None, Some(iv)) => im + (key -> iv)
            case (Some(iv), None) => im + (key -> iv)
            case (Some(iv1), Some(iv2)) => im + (key -> (iv1 + iv2))
          }
        })

        ObjMap(newAMap, newIMap)
    }

    def <>(that: AbsObject): AbsObject = (this, check(that)) match {
      case (Top, right) => right
      case (left, Top) => left
      case (left: ObjMap, right: ObjMap) =>
        if (left.amap eq right.amap) this
        else ObjMap(APropMapEmpty, ObjEmptyIMap) // TODO is really sound?
    }

    override def toString: String = this match {
      case Top => "Top"
      case ObjMap(amap, imap) =>
        val sortedIMap = imap.toSeq.sortBy {
          case (key, _) => key.toString()
        }

        val s = new StringBuilder
        sortedIMap.map {
          case (key, iv) => {
            s.append(key.toString)
              .append(s" : ")
              .append(iv.toString)
              .append(LINE_SEP)
          }
        }
        s.append(amap.toString)

        s.toString
    }

    ////////////////////////////////////////////////////////////////
    // Additional untility functions
    ///////////////////////////////////////////////////////////////
    def isEmpty: Boolean = this == Empty

    def oldify(loc: Loc): AbsObject = loc match {
      case locR @ Recency(subLoc, Recent) => subsLoc(locR, Recency(subLoc, Old))
      case _ => this
    }

    /* substitute locR by locO */
    def subsLoc(locR: Recency, locO: Recency): AbsObject = this match {
      case Top => Top
      case obj @ ObjMap(amap, imap) =>
        if (obj.isEmpty) obj
        else {
          val newAMap = amap.mapValue { dp => dp.copyWith(dp.value.subsLoc(locR, locO)) }
          val newIMap = imap.foldLeft(ObjEmptyIMap)((im, kv) => {
            val (key, iv) = kv
            val newV = iv.value.subsLoc(locR, locO)
            im + (key -> AbsIValue(newV, iv.fidset))
          })
          ObjMap(newAMap, newIMap)
        }
    }

    def weakSubsLoc(locR: Recency, locO: Recency): AbsObject = this match {
      case Top => Top
      case obj @ ObjMap(amap, imap) =>
        if (obj.isEmpty) obj
        else {
          val newAMap = amap.mapValue { dp => dp.copyWith(dp.value.weakSubsLoc(locR, locO)) }
          val newIMap = imap.foldLeft(ObjEmptyIMap)((im, kv) => {
            val (key, iv) = kv
            val newV = iv.value.weakSubsLoc(locR, locO)
            im + (key -> AbsIValue(newV, iv.fidset))
          })
          ObjMap(newAMap, newIMap)
        }
    }

    def apply(str: String): AbsDataProp = this match {
      case Top => AbsDataProp.Top
      case ObjMap(amap, imap) =>
        val (dpset, success) = amap.lookup(str)
        if (dpset.nonEmpty) dpset.reduce(_ + _)
        else AbsDataProp.Bot
    }

    def apply(astr: AbsString): AbsDataProp = this match {
      case Top => AbsDataProp.Top
      case ObjMap(amap, imap) =>
        val (dpset, success) = amap.lookup(astr)
        if (dpset.nonEmpty) dpset.reduce(_ + _)
        else AbsDataProp.Bot
    }

    def apply(in: IName): AbsIValue = this match {
      case Top => AbsIValue.Top
      case ObjMap(amap, imap) => imap.get(in) match {
        case Some(iv) => iv
        case None => AbsIValue.Bot
      }
    }

    def initializeUpdate(str: String, dp: AbsDataProp): AbsObject = this match {
      case Top => Top
      case ObjMap(amap, imap) => ObjMap(amap.initializeUpdate(str, dp), imap)
    }

    // absent value is set to AbsentBot because it is strong update.
    def update(str: String, dp: AbsDataProp, weak: Boolean = false): AbsObject = this match {
      case Top => Top
      case obj @ ObjMap(amap, imap) =>
        if (obj.isBottom) obj
        else ObjMap(amap.update(str, dp, weak), imap)
    }

    def update(astr: AbsString, dp: AbsDataProp): AbsObject = this match {
      case Top => Top
      case ObjMap(amap, imap) =>
        ObjMap(amap.update(astr, dp), imap)
    }

    def update(in: IName, iv: AbsIValue): AbsObject = this match {
      case Top => Top
      case ObjMap(amap, imap) =>
        // val newIv = iv + this(in)
        val newIv = iv
        ObjMap(amap, imap + (in -> newIv))
    }

    def contains(str: String): AbsBool = this match {
      case Top => AbsBool.Top
      case ObjMap(amap, imap) => amap contains str
    }

    def contains(strSet: Set[String]): AbsBool = this match {
      case Top => AbsBool.Top
      case obj @ ObjMap(amap, imap) =>
        strSet.foldLeft(AbsBool.Bot)((absB, str) => absB + (obj contains str))
    }

    def contains(astr: AbsString): AbsBool = this match {
      case Top => AbsBool.Top
      case ObjMap(amap, imap) => amap contains astr
    }

    def contains(in: IName): AbsBool = this match {
      case Top => AbsBool.Top
      case ObjMap(amap, imap) =>
        imap.get(in) match {
          case None => AbsBool.False
          case Some(_) => AbsBool.Top
        }
    }

    def abstractKeySet: ConSet[AbsString] = this match {
      case Top => ConInf()
      case ObjMap(amap, _) => ConFin(amap.abstractKeySet)
    }
    def abstractKeySet(filter: (AbsString, AbsDataProp) => Boolean): ConSet[AbsString] = this match {
      case Top => ConInf()
      case ObjMap(amap, _) => ConFin(amap.abstractKeySet(filter))
    }
    def collectKeySet(prefix: String): ConSet[String] = this match {
      case Top => ConInf()
      case ObjMap(amap, _) => amap.collectKeySet(prefix)
    }
    def keySetPair: (List[String], AbsString) = this match {
      case Top => (Nil, AbsString.Top)
      case ObjMap(amap, _) => {
        val (strSet, astr) = amap.keySetPair
        (strSet.toList.sortBy { _.toString }, astr) // TODO for-in order
      }
    }
    def isDefinite(str: AbsString): Boolean = this match {
      case Top => false
      case ObjMap(amap, _) => amap.isDefinite(str)
    }

    ////////////////////////////////////////////////////////////////
    // internal methods of ECMAScript Object
    ///////////////////////////////////////////////////////////////
    // Section 8.12.1 [[GetOwnProperty]](P)
    def GetOwnProperty(P: String): (AbsDesc, AbsUndef) =
      GetOwnProperty(AbsString(P))

    def GetOwnProperty(P: AbsString): (AbsDesc, AbsUndef) = this match {
      case Top => (AbsDesc.Top, AbsUndef.Top)
      case ObjMap(amap, imap) =>
        val (dpset, definite) = amap.lookup(P)
        val dp = dpset.foldLeft(AbsDataProp.Bot)(_ + _)
        val undef =
          if (definite) AbsUndef.Bot else AbsUndef.Top
        val desc = AbsDesc(
          (dp.value, AbsAbsent.Bot),
          (dp.writable, AbsAbsent.Bot),
          (dp.enumerable, AbsAbsent.Bot),
          (dp.configurable, AbsAbsent.Bot)
        )
        (desc, undef)
    }

    // Section 8.12.2 [[GetProperty]](P)
    def GetProperty(P: String, h: AbsHeap): (AbsDesc, AbsUndef) =
      GetProperty(AbsString(P), h)

    def GetProperty(P: AbsString, h: AbsHeap): (AbsDesc, AbsUndef) = this match {
      case Top => (AbsDesc.Top, AbsUndef.Top)
      case obj @ ObjMap(amap, imap) =>
        var visited = HashSet[Loc]()
        def visit(currObj: AbsObject): (AbsDesc, AbsUndef) = {
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
                    (desc + newDesc, undef + newUndef)
                  }
                }
              }
            }
          (desc + parentDesc, undef + parentUndef)
        }
        visit(obj)
    }

    // Section 8.12.3 [[Get]](P)
    def Get(str: String, h: AbsHeap): AbsValue = this match {
      case Top => AbsValue.Top
      case obj @ ObjMap(amap, imap) =>
        var visited = HashSet[Loc]()
        val valueBot = AbsValue.Bot
        def visit(currentObj: AbsObject): AbsValue = {
          val test = currentObj contains str
          val v1 =
            if (AbsBool.True <= test) currentObj(str).value
            else valueBot
          val v2 =
            if (AbsBool.False <= test) {
              val protoV = currentObj(IPrototype).value
              val v3 = protoV.pvalue.nullval.fold(valueBot)(_ => {
                AbsUndef.Top
              })
              v3 + protoV.locset.foldLeft(valueBot)((v, protoLoc) => {
                if (visited contains protoLoc) v
                else {
                  visited += protoLoc
                  v + visit(h.get(protoLoc))
                }
              })
            } else valueBot
          v1 + v2
        }
        visit(obj)
    }

    def Get(astr: AbsString, h: AbsHeap): AbsValue = this match {
      case Top => AbsValue.Top
      case obj @ ObjMap(amap, imap) =>
        var visited = HashSet[Loc]()
        val valueBot = AbsValue.Bot
        def visit(currentObj: AbsObject): AbsValue = {
          val test = currentObj contains astr
          val v1 =
            if (AbsBool.True <= test) currentObj(astr).value
            else valueBot
          val v2 =
            if (AbsBool.False <= test) {
              val protoV = currentObj(IPrototype).value
              val v3 = protoV.pvalue.nullval.fold(valueBot)({ _ =>
                AbsUndef.Top
              })
              v3 + protoV.locset.foldLeft(valueBot)((v, protoLoc) => {
                if (visited contains protoLoc) v
                else {
                  visited += protoLoc
                  v + visit(h.get(protoLoc))
                }
              })
            } else valueBot
          v1 + v2
        }
        visit(obj)
    }

    // Section 8.12.4 [[CanPut]](P)
    def CanPut(P: String, h: AbsHeap): AbsBool = CanPut(AbsString(P), h)

    def CanPut(P: AbsString, h: AbsHeap): AbsBool = this match {
      case Top => AbsBool.Top
      case obj @ ObjMap(amap, imap) =>
        val (desc, undef) = GetOwnProperty(P)
        val (b, _) = desc.writable
        val newB = {
          if (undef.isBottom) AbsBool.Bot
          else {
            val proto = obj(IPrototype).value
            val extensible = obj(IExtensible).value.pvalue.boolval
            val nullProtoCase =
              if (proto.pvalue.nullval.isBottom) AbsBool.Bot
              else extensible
            val (inheritDesc, inheritUndef) = proto.locset.foldLeft((AbsDesc.Bot, AbsUndef.Bot)) {
              case ((desc, undef), loc) => {
                val (newDesc, newUndef) = GetProperty(P, h)
                (desc + newDesc, undef + newUndef)
              }
            }
            val undefInheritCase =
              if (inheritUndef.isBottom) AbsBool.Bot
              else extensible
            val inheritCase = extensible.map[AbsBool](
              elseV = AbsBool.False,
              thenV = { val (b, _) = inheritDesc.writable; b }
            )(AbsBool)
            nullProtoCase + undefInheritCase + inheritCase
          }
        }
        b + newB
    }

    // Section 8.12.5 [[Put]](P, V, Throw)
    def Put(P: AbsString, V: AbsValue, Throw: Boolean = true, h: AbsHeap): (AbsObject, Set[Exception]) = this match {
      case Top => (Top, HashSet(TypeError, RangeError))
      case obj @ ObjMap(amap, imap) =>
        val canPut = CanPut(P, h)
        val (falseObj: AbsObject, falseExcSet: Set[Exception]) =
          if (AbsBool.False <= canPut) {
            if (Throw) (Bot, HashSet(TypeError))
            else (obj, ExcSetEmpty)
          } else (Bot, ExcSetEmpty)
        val (trueObj, trueExcSet) =
          if (AbsBool.True <= canPut) {
            val (ownDesc, ownUndef) = GetOwnProperty(P)
            val (ownObj, ownExcSet) =
              if (ownDesc.isBottom) (Bot, ExcSetEmpty)
              else {
                val valueDesc = AbsDesc((V, AbsAbsent.Bot))
                val (o, _, e) = DefineOwnProperty(h, P, valueDesc, Throw)
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
                val (o, _, e) = DefineOwnProperty(h, P, newDesc, Throw)
                (o, e)
              }
            (ownObj + undefObj, ownExcSet ++ undefExcSet)
          } else (Bot, ExcSetEmpty)
        (falseObj + trueObj, falseExcSet ++ trueExcSet)
    }

    // Section 8.12.6 [[HasProperty]](P)
    def HasProperty(P: AbsString, h: AbsHeap): AbsBool = this match {
      case Top => AbsBool.Top
      case obj @ ObjMap(amap, imap) =>
        var visited = AbsLoc.Bot
        def visit(currObj: AbsObject): AbsBool = {
          val test = currObj contains P
          val b1 =
            if (AbsBool.True <= test) AbsBool.True
            else AbsBool.Bot
          val b2 =
            if (AbsBool.False <= test) {
              val protoV = currObj(IPrototype).value
              val b3 = protoV.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
              b3 + protoV.locset.foldLeft[AbsBool](AbsBool.Bot)((b, protoLoc) =>
                if (visited contains protoLoc) b
                else {
                  visited += protoLoc
                  b + visit(h.get(protoLoc))
                })
            } else AbsBool.Bot
          b1 + b2
        }
        visit(obj)
    }

    // Section 8.12.7 [[Delete]](P, Throw)
    def Delete(str: String): (AbsObject, AbsBool) = this match {
      case Top => (Top, AbsBool.Top)
      case obj @ ObjMap(amap, imap) =>
        if (obj.isBottom) (obj, AbsBool.Bot)
        else {
          val (newAMap, asuccess) = amap.delete(str)
          (ObjMap(newAMap, imap), asuccess)
        }
    }

    def Delete(astr: AbsString): (AbsObject, AbsBool) = this match {
      case Top => (Top, AbsBool.Top)
      case obj @ ObjMap(amap, imap) =>
        if (obj.isBottom) (obj, AbsBool.Bot)
        else {
          val (newAMap, asuccess) = amap.delete(astr)
          (ObjMap(newAMap, imap), asuccess)
        }
    }

    // Section 8.12.8 [[DefaultValue]](hint)
    def DefaultValue(hint: String, h: AbsHeap): AbsPValue = {
      hint match {
        case "String" => DefaultValueAsString(h)
        case "Number" => DefaultValueAsNumber(h)
        case _ => DefaultValue(h)
      }
    }

    def DefaultValue(h: AbsHeap): AbsPValue = this match {
      case Top => AbsPValue(numval = AbsNumber.Top, strval = AbsString.Top)
      case obj @ ObjMap(amap, imap) =>
        val className = obj(IClass)
        val isDateClass = className.value.pvalue.strval === AbsString("Date")
        isDateClass.map(DefaultValueAsString(h), DefaultValueAsNumber(h))(AbsPValue)
    }

    private def DefaultValueAsString(h: AbsHeap): AbsPValue = this match {
      case Top => AbsString.Top
      case ObjMap(_, _) =>
        val toString = Get("toString", h)
        val isCallable = TypeConversionHelper.IsCallable(toString, h)
        val str =
          if (AbsBool.True <= isCallable) AbsPValue(strval = AbsString.Top)
          else AbsPValue.Bot
        if (AbsBool.False <= isCallable) {
          val valueOf = Get("valueOf", h)
          val value =
            if (AbsBool.True <= TypeConversionHelper.IsCallable(valueOf, h)) AbsPValue.Top
            else AbsPValue.Bot
          str + value
        } else str
    }

    private def DefaultValueAsNumber(h: AbsHeap): AbsPValue = this match {
      case Top => AbsNumber.Top
      case ObjMap(_, _) =>
        val valueOf = Get("valueOf", h)
        val isCallable = TypeConversionHelper.IsCallable(valueOf, h)
        val value =
          if (AbsBool.True <= isCallable) AbsPValue.Top
          else AbsPValue.Bot
        if (AbsBool.False <= isCallable) {
          val toString = Get("toString", h)
          val str =
            if (AbsBool.True <= TypeConversionHelper.IsCallable(toString, h)) AbsPValue(strval = AbsString.Top)
            else AbsPValue.Bot
          value + str
        } else value
    }

    //Section 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
    def DefineOwnProperty(h: AbsHeap, P: AbsString, Desc: AbsDesc, Throw: Boolean = true): (AbsObject, AbsBool, Set[Exception]) = this match {
      case Top => (Top, AbsBool.Top, HashSet(TypeError, RangeError))
      case obj @ ObjMap(_, _) =>
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
            if (AbsBool.True <= extensible) {
              // i. Create an own data property named P of object O whose [[Value]], [[Writable]],
              // [[Enumerable]] and [[Configurable]] attribute values are described by Desc.
              val changedObj = update(P, AbsDataProp(Desc))
              (changedObj, AbsBool.True, ExcSetEmpty)
            } else BotTriple
          val (obj2: AbsObject, b2: AbsBool, excSet2: Set[Exception]) =
            // 3. If current is undefined and extensible is false, then Reject.
            if (AbsBool.False <= extensible) Reject
            else BotTriple
          (obj1 + obj2, b1 + b2, excSet1 ++ excSet2)
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
          if ((dva.isTop || (!dv.isBottom && AbsBool.True <= (TypeConversionHelper.SameValue(h, dv, cv)))) &&
            (dwa.isTop || (!dw.isBottom && AbsBool.True <= (dw === cw))) &&
            (dea.isTop || (!de.isBottom && AbsBool.True <= (de === ce))) &&
            (dca.isTop || (!dc.isBottom && AbsBool.True <= (dc === cc)))) (obj, AbsBool.True)
          else (Bot, AbsBool.Bot)

        // 7. If the [[Configurable]] field of current is false then
        val (obj2: AbsObject, b2: AbsBool, excSet2: Set[Exception]) =
          if (AbsBool.False <= cc) {
            // a. Reject, if the [[Configurable]] field of Desc is true.
            if (AbsBool.True <= dc) Reject
            // b. Reject, if the [[Enumerable]] field of Desc is present and the [[Enumerable]] fields of current and
            // Desc are the Boolean negation of each other.
            else if (!de.isBottom &&
              (AbsBool.True <= de && AbsBool.False <= ce || AbsBool.True <= ce && AbsBool.False <= de)) Reject
            else BotTriple
          } else BotTriple

        // 10. Else, if IsDataDescriptor(current) and IsDataDescriptor(Desc) are both true, then
        val (obj7: AbsObject, b7: AbsBool, excSet7: Set[Exception]) =
          // a. If the [[Configurable]] field of current is false, then
          if (AbsBool.False <= cc) {
            // i. Reject, if the [[Writable]] field of current is false and the [[Writable]] field of Desc is true.
            if (AbsBool.False <= cw && AbsBool.True <= dw) Reject
            // ii. If the [[Writable]] field of current is false, then
            else if (AbsBool.False <= cw &&
              // 1. Reject, if the [[Value]] field of Desc is present and SameValue(Desc.[[Value]],
              // current.[[Value]]) is false.
              !dv.isBottom && AbsBool.False <= (TypeConversionHelper.SameValue(h, dv, cv))) Reject
            else BotTriple
          } else BotTriple

        // 12. For each attribute field of Desc that is present, set the correspondingly named attribute of the
        // property named P of object O to the value of the field.
        val (obj3, b3, excSet3) =
          if (AbsBool.True <= cc || AbsBool.True <= cw) {
            var newDP = obj(P)
            if (!dv.isBottom) newDP = newDP.copyWith(value = dv)
            if (!dw.isBottom) newDP = newDP.copyWith(writable = dw)
            if (!de.isBottom) newDP = newDP.copyWith(enumerable = de)
            if (!dc.isBottom) newDP = newDP.copyWith(configurable = dc)
            val changedObj = update(P, newDP)
            (changedObj, AbsBool.True, ExcSetEmpty)
          } else BotTriple

        val excSet4 =
          if (AbsString("Array") <= obj(IClass).value.pvalue.strval &&
            AbsString("length") <= P &&
            AbsBool.False <= (TypeConversionHelper.ToNumber(dv) === TypeConversionHelper.ToUInt32(dv))) HashSet(RangeError)
          else ExcSetEmpty

        // TODO: unsound. Should Reject if an array element could not be deleted, i.e., it's not configurable.
        // TODO: Implement DefineOwnProperty for Array objects (15.4.5.1).

        (obj1 + obj2 + obj3 + obj5 + obj6 + obj7, b1 + b2 + b3 + b5 + b6 + b7, excSet1 ++ excSet2 ++ excSet3 ++ excSet4 ++ excSet7)
    }
  }

  ////////////////////////////////////////////////////////////////
  // new Object constructors
  ////////////////////////////////////////////////////////////////
  def newObject: AbsObject = newObject(BuiltinObjectProto.loc)

  def newObject(loc: Loc): AbsObject = newObject(AbsLoc(loc))

  def newObject(locSet: AbsLoc): AbsObject = {
    Empty
      .update(IClass, AbsIValue(AbsString("Object")))
      .update(IPrototype, AbsIValue(locSet))
      .update(IExtensible, AbsIValue(atrue))
  }

  def newArgObject(absLength: AbsNumber = AbsNumber(0)): AbsObject = {
    Empty
      .update(IClass, AbsIValue(AbsString("Arguments")))
      .update(IPrototype, AbsIValue(BuiltinObjectProto.loc))
      .update(IExtensible, AbsIValue(atrue))
      .update("length", AbsDataProp(absLength, atrue, afalse, atrue))
  }

  def newArrayObject(absLength: AbsNumber = AbsNumber(0)): AbsObject = {
    Empty
      .update(IClass, AbsIValue(AbsString("Array")))
      .update(IPrototype, AbsIValue(BuiltinArrayProto.loc))
      .update(IExtensible, AbsIValue(atrue))
      .update("length", AbsDataProp(absLength, atrue, afalse, afalse))
  }

  def newFunctionObject(fid: FunctionId, env: AbsValue, l: Loc, n: AbsNumber): AbsObject = {
    newFunctionObject(Some(fid), Some(fid), env, Some(l), n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], n: AbsNumber): AbsObject = {
    newFunctionObject(fidOpt, constructIdOpt, env,
      locOpt, atrue, afalse, afalse, n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNumber): AbsObject = {
    val obj1 =
      Empty
        .update(IClass, AbsIValue(AbsString("Function")))
        .update(IPrototype, AbsIValue(BuiltinFunctionProto.loc))
        .update(IExtensible, AbsIValue(atrue))
        .update(IScope, AbsIValue(env))
        .update("length", AbsDataProp(absLength, afalse, afalse, afalse))

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

  def newBooleanObj(absB: AbsBool): AbsObject = {
    val newObj = newObject(BuiltinBooleanProto.loc)
    newObj.update(IClass, AbsIValue(AbsString("Boolean")))
      .update(IPrimitiveValue, AbsIValue(absB))
  }

  def newNumberObj(absNum: AbsNumber): AbsObject = {
    val newObj = newObject(BuiltinNumberProto.loc)
    newObj.update(IClass, AbsIValue(AbsString("Number")))
      .update(IPrimitiveValue, AbsIValue(absNum))
  }

  def newStringObj(absStr: AbsString): AbsObject = {
    val newObj = newObject(BuiltinStringProto.loc)

    val newObj2 = newObj
      .update(IClass, AbsIValue(AbsString("String")))
      .update(IPrimitiveValue, AbsIValue(absStr))

    absStr.gamma match {
      case ConFin(strSet) =>
        strSet.foldLeft(Bot)((obj, str) => {
          val length = str.length
          val newObj3 = (0 until length).foldLeft(newObj2)((tmpObj, tmpIdx) => {
            val charAbsStr = AbsString(str.charAt(tmpIdx).toString)
            val charVal = AbsValue(charAbsStr)
            tmpObj.update(tmpIdx.toString, AbsDataProp(charVal, afalse, atrue, afalse))
          })
          val lengthVal = AbsValue(length)
          obj + newObj3.update("length", AbsDataProp(lengthVal, afalse, afalse, afalse))
        })
      case _ =>
        newObj2
          .update(AbsString.Number, AbsDataProp(AbsValue(AbsString.Top), afalse, atrue, afalse))
          .update("length", AbsDataProp(absStr.length, afalse, afalse, afalse))
    }
  }

  def newErrorObj(errorName: String, protoLoc: Loc): AbsObject = {
    Empty
      .update(IClass, AbsIValue(AbsString(errorName)))
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
        case "Number" => AbsPValue(numval = AbsNumber.Top)
        case "String" => AbsPValue(strval = AbsString.Top)
        case _ => AbsPValue.Top
      }
    }
  }

  def defaultValue(locSet: AbsLoc, h: AbsHeap, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else locSet.foldLeft(AbsPValue.Bot)((pv, loc) => h.get(loc).DefaultValue(preferredType, h))
  }

  // 8.10.4 FromPropertyDescriptor ( Desc )
  def FromPropertyDescriptor(h: AbsHeap, desc: AbsDesc): (AbsObject, Set[Exception]) = {
    def put(
      obj: AbsObject,
      name: String,
      pair: (AbsValue, AbsAbsent)
    ): (AbsObject, AbsBool, Set[Exception]) = {
      val T = (AbsBool.True, AbsAbsent.Bot)
      obj.DefineOwnProperty(
        h,
        AbsString(name),
        AbsDesc(pair, T, T, T),
        false
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

////////////////////////////////////////////////////////////////////////////////
// abstract map with abstract key
////////////////////////////////////////////////////////////////////////////////
sealed abstract class APropMap(
    private val map: Map[AbsString, AbsDataProp],
    private val defset: DefSet
) {
  override def toString: String = {
    val sortedMap = map.toSeq.sortBy {
      case (key, _) => key.toString
    }

    val s = new StringBuilder
    sortedMap.map {
      case (k, v) => {
        s.append(k)
          .append(s" -> ")
          .append(v.toString)
          .append(LINE_SEP)
      }
    }
    s.append(defset.toString)

    s.toString
  }

  /* partial order */
  def <=(that: APropMap): Boolean =
    (this, that) match {
      case (APropMapBot, _) => true
      case (_, APropMapBot) => false
      case _ if !(that.defset subsetOf this.defset) => false
      case _ if that.defset subsetOf this.defset =>
        this.map.forall(kv1 => {
          val (key1, value1) = kv1
          val test = that.map.exists(kv2 => {
            val (key2, value2) = kv2
            (key1 <= key2) && (value1 <= value2)
          })
          if (!test) {
            val value2 = that.map.foldLeft(AbsDataProp.Bot)((adp, kv2) => {
              val (key2, value2) = kv2
              if (key1 isRelated key2) adp + value2
              else adp
            })
            value1 <= value2
          } else test
        })
    }

  /* join */
  def +(that: APropMap): APropMap =
    (this, that) match {
      case (APropMapBot, _) => that
      case (_, APropMapBot) => this
      case _ =>
        val thisKeys = this.map.keySet
        val thatKeys = that.map.keySet
        val map1 = (thisKeys -- thatKeys).foldLeft(HashMap[AbsString, AbsDataProp]())((m, key) => {
          m + (key -> this.map(key))
        })
        val map2 = (thatKeys -- thisKeys).foldLeft(map1)((m, key) => {
          m + (key -> that.map(key))
        })
        val mapCap = (thisKeys intersect thatKeys).foldLeft(map2)((m, key) => {
          m + (key -> (this.map(key) + that.map(key)))
        })
        APropMapFin(mapCap, this.defset intersect that.defset)
    }

  /* property read */
  def lookup(str: String): (Set[AbsDataProp], Boolean) = {
    val emptySet = HashSet[AbsDataProp]()
    val astr = AbsString(str)
    val domIn = (this.map.keySet contains astr) && (this.defset contains str)
    this match {
      case APropMapBot => (emptySet, false)
      case _ if domIn => (HashSet(this.map(astr)), true)
      case _ =>
        val dpset = this.map.foldLeft(emptySet)((dps, kv) => {
          val (key, dp) = kv
          if (key isRelated str) dps + dp
          else dps
        })
        (dpset, defset contains str)
    }
  }

  def lookup(astr: AbsString): (Set[AbsDataProp], Boolean) = {
    val emptySet = HashSet[AbsDataProp]()
    (this, astr.gamma) match {
      case (APropMapBot, _) => (emptySet, false)
      case (_, conset) if conset.isBottom => (emptySet, false)
      case _ =>
        val dpset = this.map.foldLeft(emptySet)((dps, kv) => {
          val (key, dp) = kv
          if (key isRelated astr) dps + dp
          else dps
        })
        (dpset, defset contains astr)
    }
  }

  /* property write */
  def initializeUpdate(str: String, dp: AbsDataProp): APropMap = {
    val astr = AbsString(str)
    val domIn = this.map.keySet contains astr

    this match {
      case APropMapBot => APropMapBot
      case _ if !domIn => APropMapFin(this.map + (astr -> dp), this.defset + str)
      case _ if domIn =>
        val old = this.map(astr)
        val newMap = this.map + (astr -> (old + dp))
        APropMapFin(newMap, this.defset + str)
    }
  }

  def update(str: String, dp: AbsDataProp, weak: Boolean = false): APropMap = {
    // TODO: add Map[String, AbsDataProp] for performance
    val astr = AbsString(str)
    val domIn = this.map.keySet contains astr

    this match {
      case APropMapBot => APropMapBot
      case _ if dp.isBottom => APropMapBot
      case _ if !domIn && !weak => APropMapFin(this.map + (astr -> dp), this.defset + str)
      case _ if !domIn && weak => APropMapFin(this.map + (astr -> dp), this.defset)
      case _ if domIn && !weak => // Strong update
        APropMapFin(this.map + (astr -> dp), this.defset + str)
      case _ if domIn && weak => // Weak update
        val old = this.map(astr)
        val newMap = this.map + (astr -> (old + dp))
        APropMapFin(newMap, this.defset)
    }
  }

  def update(astr: AbsString, dp: AbsDataProp): APropMap = {
    val domIn = this.map.keySet contains astr

    (this, astr.gamma) match {
      case (APropMapBot, _) => APropMapBot
      case (_, conset) if conset.isBottom => APropMapBot
      case _ if dp.isBottom => APropMapBot

      case (_, ConFin(strSet)) if strSet.size == 1 => this.update(strSet.head, dp)
      case (_, ConFin(strSet)) => strSet.foldLeft(this)((am, str) => am.update(str, dp, true))
      case (_, ConInf()) if !domIn => APropMapFin(this.map + (astr -> dp), this.defset)
      case (_, ConInf()) if domIn =>
        val old = this.map(astr)
        val newMap = this.map + (astr -> (old + dp))
        APropMapFin(newMap, this.defset)
    }
  }

  /* property delete */
  def delete(str: String): (APropMap, AbsBool) = {
    val astr = AbsString(str)
    val (domIn, configurable) =
      if (this.map.keySet contains astr) (true, this.map(astr).configurable)
      else (false, AbsBool.Bot)
    val newDefSet = this.defset - str

    this match {
      case APropMapBot => (APropMapBot, AbsBool.Bot)
      case _ if !domIn =>
        val newAPropMap = APropMapFin(this.map, newDefSet)
        (newAPropMap, AbsBool.Top)
      case _ if domIn => {
        val (falseMap, falseB) =
          if (AbsBool.False <= configurable) (this, AbsBool.False)
          else (APropMapBot, AbsBool.Bot)
        val (trueMap, trueB) =
          if (AbsBool.True <= configurable) {
            val newAPropMap = APropMapFin(this.map - astr, newDefSet)
            (newAPropMap, AbsBool.True)
          } else (APropMapBot, AbsBool.Bot)
        (falseMap + trueMap, falseB + trueB)
      }
    }
  }

  def delete(astr: AbsString): (APropMap, AbsBool) = {
    val (domIn, configurable) =
      if (this.map.keySet contains astr) (true, this.map(astr).configurable)
      else (false, AbsBool.Bot)
    val defSetEmpty = DefSetFin(HashSet[String]())

    (this, astr.gamma) match {
      case (APropMapBot, _) => (APropMapBot, AbsBool.Bot)
      case (_, conset) if conset.isBottom => (APropMapBot, AbsBool.Bot)

      case (_, ConFin(strSet)) =>
        strSet.foldLeft((this, AbsBool.Bot))((tpl, str) => {
          val (am, ab) = tpl
          am.delete(str)
        })
      case (_, ConInf()) if !domIn =>
        val newAPropMap = APropMapFin(this.map, defSetEmpty)
        (newAPropMap, AbsBool.Top)
      case (_, ConInf()) if domIn => {
        val (falseMap, falseB) =
          if (AbsBool.False <= configurable) (this, AbsBool.Top)
          else (APropMapBot, AbsBool.Bot)
        val (trueMap, trueB) =
          if (AbsBool.True <= configurable) {
            val newAPropMap = APropMapFin(this.map - astr, defSetEmpty)
            (newAPropMap, AbsBool.Top)
          } else (APropMapBot, AbsBool.Bot)
        (falseMap + trueMap, falseB + trueB)
      }
    }
  }

  /* has property */
  def contains(str: String): AbsBool = {
    if (this.isBottom) AbsBool.Bot
    else {
      val domIn = this.map.keySet.exists(_.isRelated(str))
      val defsetIn = this.defset contains str
      (domIn, defsetIn) match {
        case (true, true) => AbsBool.True
        case (true, false) => AbsBool.Top
        case (false, true) => AbsBool.Bot // Impossible case
        case (false, false) => AbsBool.False
      }
    }
  }

  def contains(astr: AbsString): AbsBool = {
    if (this.isBottom) AbsBool.Bot
    else {
      val domIn = this.map.keySet.exists(_.isRelated(astr))
      val defsetIn = this.defset contains astr
      (domIn, defsetIn) match {
        case (true, true) => AbsBool.True
        case (true, false) => AbsBool.Top
        case (false, true) => AbsBool.Bot // Impossible case
        case (false, false) => AbsBool.False
      }
    }
  }

  /* other utilities */
  def isBottom: Boolean =
    defset match {
      case DefSetTop => map.isEmpty
      case _ => false
    }

  def isEmpty: Boolean =
    defset match {
      case DefSetFin(s) if s.isEmpty => map.isEmpty
      case _ => false
    }

  def mapValue(f: AbsDataProp => AbsDataProp): APropMap = {
    val newMap = map.foldLeft(HashMap[AbsString, AbsDataProp]())((tmp, kv) => {
      val (k, v) = kv
      tmp + (k -> f(v))
    })
    APropMapFin(newMap, defset)
  }

  def abstractKeySet: Set[AbsString] = map.keySet

  def abstractKeySet(filter: (AbsString, AbsDataProp) => Boolean): Set[AbsString] = {
    map.foldLeft(HashSet[AbsString]()) {
      case (set, (key, dp)) => {
        if (filter(key, dp)) set + key
        else set
      }
    }
  }

  def concreteKeySet: ConSet[String] = map.keySet.foldLeft[ConSet[String]](ConFin()) {
    case (ConInf(), _) => ConInf()
    case (ConFin(keyset), astr) => astr.gamma match {
      case ConInf() => ConInf()
      case ConFin(set) => ConFin(keyset ++ set.map(_.str))
    }
  }

  def collectKeySet(prefix: String): ConSet[String] = concreteKeySet match {
    case ConInf() => ConInf()
    case ConFin(set) => ConFin(set.filter(_.startsWith(prefix)))
  }

  def isDefinite(str: AbsString): Boolean = str.gamma match {
    case ConFin(set) if set.forall(defset contains _) => true
    case _ => false
  }

  def keySetPair: (Set[String], AbsString) = map.keySet.foldLeft(
    (HashSet[String](), AbsString.Bot)
  ) {
      case ((strSet, astr), akey) => akey.gamma match {
        case ConInf() => (strSet, astr + akey)
        case ConFin(keySet) => keySet.foldLeft((strSet, astr)) {
          case ((strSet, astr), key) => {
            val isEnum = map(akey).enumerable
            if (AbsBool.True <= isEnum) {
              val isDef = defset contains key
              if (isDef && (AbsBool.Top != isEnum)) (strSet + key, astr)
              else (strSet, astr + AbsString(key))
            } else (strSet, astr)
          }
        }
      }
    }
}
case class APropMapFin(private val map: Map[AbsString, AbsDataProp], private val defset: DefSet) extends APropMap(map, defset)
case object APropMapEmpty extends APropMap(HashMap[AbsString, AbsDataProp](), DefSet.Empty)
case object APropMapBot extends APropMap(HashMap[AbsString, AbsDataProp](), DefSet.Top)

////////////////////////////////////////////////////////////////////////////////
// definite key set
////////////////////////////////////////////////////////////////////////////////
object DefSet {
  val Empty: DefSet = DefSetFin(HashSet[String]())
  val Top: DefSet = DefSetTop
}

sealed abstract class DefSet {
  override def toString: String =
    this match {
      case DefSetTop => "⊤String"
      case DefSetFin(s) => s.toString
    }
  def ++(that: DefSet): DefSet =
    (this, that) match {
      case (DefSetTop, _) | (_, DefSetTop) => DefSetTop
      case (DefSetFin(a), DefSetFin(b)) => DefSetFin(a ++ b)
    }

  def --(that: DefSet): DefSet =
    (this, that) match {
      case (DefSetTop, _) | (_, DefSetTop) => DefSetFin(HashSet())
      case (DefSetFin(a), DefSetFin(b)) => DefSetFin(a -- b)
    }

  def +(elem: String): DefSet =
    this match {
      case DefSetTop => DefSetTop
      case DefSetFin(a) => DefSetFin(a + elem)
    }

  def -(elem: String): DefSet =
    this match {
      case DefSetTop => DefSetFin(HashSet())
      case DefSetFin(a) => DefSetFin(a - elem)
    }

  def subsetOf(that: DefSet): Boolean =
    (this, that) match {
      case (_, DefSetTop) => true
      case (DefSetTop, DefSetFin(_)) => false
      case (DefSetFin(a), DefSetFin(b)) => a subsetOf b
    }

  def contains(elem: String): Boolean =
    this match {
      case DefSetTop => true
      case DefSetFin(a) => a contains elem
    }

  def contains(elem: AbsString): Boolean =
    (this, elem.gamma) match {
      case (DefSetTop, _) => true
      case (DefSetFin(_), conset) if conset.isBottom => true
      case (DefSetFin(_), ConInf()) => false
      case (DefSetFin(a), ConFin(b)) => b subsetOf a
    }

  def intersect(that: DefSet): DefSet =
    (this, that) match {
      case (DefSetTop, _) => that
      case (_, DefSetTop) => this
      case (DefSetFin(a), DefSetFin(b)) => DefSetFin(a intersect b)
    }
}
case class DefSetFin(set: Set[String]) extends DefSet
case object DefSetTop extends DefSet
