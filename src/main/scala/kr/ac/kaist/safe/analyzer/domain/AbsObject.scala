/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.util.Address

import scala.collection.immutable.HashSet

class AbsObject(
    val amap: AbsMap,
    val imap: ObjInternalMap
) {
  override def toString: String = {
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

  /* partial order */
  def <=(that: AbsObject): Boolean = {
    if (this.isEmpty) true
    else if (that.isEmpty) false
    else if (!(this.imap.keySet subsetOf that.imap.keySet)) false
    else (this.amap <= that.amap) &&
      that.imap.forall {
        case (key, thatIV) => {
          this.imap.get(key) match {
            case None => false
            case Some(thisIV) => thisIV <= thatIV
          }
        }
      }
  }

  /* not a partial order */
  def </(that: AbsObject): Boolean = !(this <= that)

  /* join */
  def +(that: AbsObject): AbsObject = {
    val newAMap = this.amap + that.amap

    val ikeys = this.imap.keySet ++ that.imap.keySet
    val newIMap = ikeys.foldLeft(ObjEmptyIMap)((im, key) => {
      val thisIVal = this.imap.get(key)
      val thatIVal = that.imap.get(key)
      (thisIVal, thatIVal) match {
        case (None, None) => im
        case (None, Some(iv)) => im + (key -> iv)
        case (Some(iv), None) => im + (key -> iv)
        case (Some(iv1), Some(iv2)) => im + (key -> (iv1 + iv2))
      }
    })

    new AbsObject(newAMap, newIMap)
  }

  /* meet */
  def <>(that: AbsObject): AbsObject = {
    if (this.amap eq that.amap) this
    else new AbsObject(AbsMapEmpty, ObjEmptyIMap)
  }

  def isBottom: Boolean = {
    if (this.isEmpty) true
    else
      this.amap.isBottom &&
        this.imap.foldLeft(true)((b, kv) => {
          val (_, iv) = kv
          b && iv.isBottom
        })
  }

  ////////////////////////////////////////////////////////////////
  // Additional untility functions
  ///////////////////////////////////////////////////////////////
  def isEmpty: Boolean = this.amap.isEmpty && this.imap.isEmpty

  def oldify(addr: Address): AbsObject = subsLoc(Loc(addr, Recent), Loc(addr, Old))

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): AbsObject = {
    if (this.isEmpty) this
    else {
      val newAMap = this.amap.mapValue { dp => dp.copyWith(dp.value.subsLoc(locR, locO)) }
      val newIMap = this.imap.foldLeft(ObjEmptyIMap)((im, kv) => {
        val (key, iv) = kv
        val newV = iv.value.subsLoc(locR, locO)
        im + (key -> InternalValue(newV, iv.fidset))
      })
      new AbsObject(newAMap, newIMap)
    }
  }

  def weakSubsLoc(locR: Loc, locO: Loc): AbsObject = {
    if (this.isEmpty) this
    else {
      val newAMap = this.amap.mapValue { dp => dp.copyWith(dp.value.weakSubsLoc(locR, locO)) }
      val newIMap = this.imap.foldLeft(ObjEmptyIMap)((im, kv) => {
        val (key, iv) = kv
        val newV = iv.value.weakSubsLoc(locR, locO)
        im + (key -> InternalValue(newV, iv.fidset))
      })
      new AbsObject(newAMap, newIMap)
    }
  }

  def apply(str: String): AbsDataProp = {
    val (dpset, success) = amap.lookup(str)
    if (dpset.nonEmpty) dpset.reduce(_ + _)
    else AbsDataProp.Bot
  }

  def apply(astr: AbsString): AbsDataProp = {
    val (dpset, success) = amap.lookup(astr)
    if (dpset.nonEmpty) dpset.reduce(_ + _)
    else AbsDataProp.Bot
  }

  def apply(in: InternalName): InternalValue =
    this.imap.get(in) match {
      case Some(iv) => iv
      case None => InternalValueUtil.Bot
    }

  def initializeUpdate(str: String, dp: AbsDataProp): AbsObject = {
    new AbsObject(amap.initializeUpdate(str, dp), imap)
  }

  // absent value is set to AbsentBot because it is strong update.
  def update(str: String, dp: AbsDataProp, weak: Boolean = false): AbsObject = {
    if (this.isBottom) this
    else new AbsObject(amap.update(str, dp, weak), imap)
  }

  def update(astr: AbsString, dp: AbsDataProp): AbsObject =
    new AbsObject(amap.update(astr, dp), imap)

  def update(in: InternalName, iv: InternalValue): AbsObject = {
    //val newIv = iv + this(in)
    val newIv = iv
    new AbsObject(amap, imap + (in -> newIv))
  }

  def contains(str: String): AbsBool = this.amap contains str

  def contains(strSet: Set[String]): AbsBool =
    strSet.foldLeft(AbsBool.Bot)((absB, str) => absB + (this contains str))

  def contains(astr: AbsString): AbsBool = this.amap contains astr

  def contains(in: InternalName): AbsBool = {
    imap.get(in) match {
      case None => AbsBool.False
      case Some(_) => AbsBool.Top
    }
  }

  def getOwnPropertyNames: Set[String] = this.amap.concreteKeySet
  def abstractKeySet: Set[AbsString] = this.amap.abstractKeySet
  def collectKeySet(prefix: String): Set[String] = this.amap.collectKeySet(prefix)

  ////////////////////////////////////////////////////////////////
  // internal methods of ECMAScript Object
  ///////////////////////////////////////////////////////////////
  // Section 8.12.1 [[GetOwnProperty]](P)
  def GetOwnProperty(P: String): (AbsDesc, AbsUndef) =
    GetOwnProperty(AbsString(P))

  def GetOwnProperty(P: AbsString): (AbsDesc, AbsUndef) = {
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
  def GetProperty(P: String, h: Heap): (AbsDesc, AbsUndef) =
    GetProperty(AbsString(P), h)

  def GetProperty(P: AbsString, h: Heap): (AbsDesc, AbsUndef) = {
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
    visit(this)
  }

  // Section 8.12.3 [[Get]](P)
  def Get(str: String, h: Heap): AbsValue = {
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
    visit(this)
  }

  def Get(astr: AbsString, h: Heap): AbsValue = {
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
    visit(this)
  }

  // Section 8.12.4 [[CanPut]](P)
  def CanPut(P: String, h: Heap): AbsBool = this.CanPut(AbsString(P), h)

  def CanPut(P: AbsString, h: Heap): AbsBool = {
    val (desc, undef) = this.GetOwnProperty(P)
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
            val (newDesc, newUndef) = this.GetProperty(P, h)
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
  def Put(P: AbsString, V: AbsValue, Throw: Boolean = true, h: Heap): (AbsObject, Set[Exception]) = {
    val canPut = this.CanPut(P, h)
    val (falseObj: AbsObject, falseExcSet: Set[Exception]) =
      if (AbsBool.False <= canPut) {
        if (Throw) (AbsObjectUtil.Bot, HashSet(TypeError))
        else (this, ExcSetEmpty)
      } else (AbsObjectUtil.Bot, ExcSetEmpty)
    val (trueObj, trueExcSet) =
      if (AbsBool.True <= canPut) {
        val (ownDesc, ownUndef) = this.GetOwnProperty(P)
        val (ownObj, ownExcSet) =
          if (ownDesc.isBottom) (AbsObjectUtil.Bot, ExcSetEmpty)
          else {
            val valueDesc = AbsDesc((V, AbsAbsent.Bot))
            val (o, _, e) = this.DefineOwnProperty(P, valueDesc, Throw)
            (o, e)
          }
        val (undefObj, undefExcSet) =
          if (ownUndef.isBottom) (AbsObjectUtil.Bot, ExcSetEmpty)
          else {
            val desc = this.GetProperty(P, h)
            val newDesc = AbsDesc(
              (V, AbsAbsent.Bot),
              (AbsBool.True, AbsAbsent.Bot),
              (AbsBool.True, AbsAbsent.Bot),
              (AbsBool.True, AbsAbsent.Bot)
            )
            val (o, _, e) = this.DefineOwnProperty(P, newDesc, Throw)
            (o, e)
          }
        (ownObj + undefObj, ownExcSet ++ undefExcSet)
      } else (AbsObjectUtil.Bot, ExcSetEmpty)
    (falseObj + trueObj, falseExcSet ++ trueExcSet)
  }

  // Section 8.12.6 [[HasProperty]](P)
  def HasProperty(P: AbsString, h: Heap): AbsBool = {
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
    visit(this)
  }

  // Section 8.12.7 [[Delete]](P, Throw)
  def Delete(str: String): (AbsObject, AbsBool) = {
    if (this.isBottom) (this, AbsBool.Bot)
    else {
      val (newAMap, asuccess) = this.amap.delete(str)
      (new AbsObject(newAMap, this.imap), asuccess)
    }
  }

  def Delete(astr: AbsString): (AbsObject, AbsBool) = {
    if (this.isBottom) (this, AbsBool.Bot)
    else {
      val (newAMap, asuccess) = this.amap.delete(astr)
      (new AbsObject(newAMap, this.imap), asuccess)
    }
  }

  // Section 8.12.8 [[DefaultValue]](hint)
  def DefaultValue(hint: String, h: Heap): AbsPValue = {
    hint match {
      case "String" => DefaultValueAsString(h)
      case "Number" => DefaultValueAsNumber(h)
      case _ => DefaultValue(h)
    }
  }

  def DefaultValue(h: Heap): AbsPValue = {
    val className = this(IClass)
    val isDateClass = className.value.pvalue.strval === AbsString("Date")
    isDateClass.map(this.DefaultValueAsString(h), this.DefaultValueAsNumber(h))(AbsPValue)
  }

  private def DefaultValueAsString(h: Heap): AbsPValue = {
    val toString = this.Get("toString", h)
    val isCallable = TypeConversionHelper.IsCallable(toString, h)
    val str =
      if (AbsBool.True <= isCallable) AbsPValue(strval = AbsString.Top)
      else AbsPValue.Bot
    if (AbsBool.False <= isCallable) {
      val valueOf = this.Get("valueOf", h)
      val value =
        if (AbsBool.True <= TypeConversionHelper.IsCallable(valueOf, h)) AbsPValue.Top
        else AbsPValue.Bot
      str + value
    } else str
  }

  private def DefaultValueAsNumber(h: Heap): AbsPValue = {
    val valueOf = this.Get("valueOf", h)
    val isCallable = TypeConversionHelper.IsCallable(valueOf, h)
    val value =
      if (AbsBool.True <= isCallable) AbsPValue.Top
      else AbsPValue.Bot
    if (AbsBool.False <= isCallable) {
      val toString = this.Get("toString", h)
      val str =
        if (AbsBool.True <= TypeConversionHelper.IsCallable(toString, h)) AbsPValue(strval = AbsString.Top)
        else AbsPValue.Bot
      value + str
    } else value
  }

  //Section 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
  def DefineOwnProperty(P: AbsString, Desc: AbsDesc, Throw: Boolean = true): (AbsObject, AbsBool, Set[Exception]) = {
    val Reject =
      if (Throw) (AbsObjectUtil.Bot, AbsBool.Bot, HashSet(TypeError))
      else (AbsObjectUtil.Bot, AbsBool.False, ExcSetEmpty)
    val BotTriple = (AbsObjectUtil.Bot, AbsBool.Bot, ExcSetEmpty)
    val (current, curUndef) = this.GetOwnProperty(P)
    val extensible = this(IExtensible).value.pvalue.boolval

    val (obj1, b1, excSet1) = if (curUndef.isTop) {
      val (obj1, b1, excSet1) =
        if (AbsBool.True <= extensible) {
          val changedObj = this.update(P, AbsDataProp(Desc))
          (changedObj, AbsBool.True, ExcSetEmpty)
        } else BotTriple
      val (obj2: AbsObject, b2: AbsBool, excSet2: Set[Exception]) =
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
      if (dva.isTop && dwa.isTop && dea.isTop && dca.isTop) (this, AbsBool.True)
      else (AbsObjectUtil.Bot, AbsBool.Bot)
    // 6. Return true, if every field in Desc also occurs in current and same
    val (obj6, b6) =
      if ((dva.isTop || (!dv.isBottom && AbsBool.True <= (TypeConversionHelper.SameValue(dv, cv)))) &&
        (dwa.isTop || (!dw.isBottom && AbsBool.True <= (dw === cw))) &&
        (dea.isTop || (!de.isBottom && AbsBool.True <= (de === ce))) &&
        (dca.isTop || (!dc.isBottom && AbsBool.True <= (dc === cc)))) (this, AbsBool.True)
      else (AbsObjectUtil.Bot, AbsBool.Bot)

    val (obj2: AbsObject, b2: AbsBool, excSet2: Set[Exception]) =
      if (AbsBool.False <= cc && AbsBool.False <= cw) Reject
      else BotTriple

    val (obj3, b3, excSet3) =
      if (AbsBool.True <= cc || AbsBool.True <= cw) {
        var newDP = this(P)
        if (!dv.isBottom) newDP = newDP.copyWith(value = dv)
        if (!dw.isBottom) newDP = newDP.copyWith(writable = dw)
        if (!de.isBottom) newDP = newDP.copyWith(enumerable = de)
        if (!dc.isBottom) newDP = newDP.copyWith(configurable = dc)
        val changedObj = this.update(P, newDP)
        (changedObj, AbsBool.True, ExcSetEmpty)
      } else BotTriple

    val excSet4 =
      if (AbsString("Array") <= this(IClass).value.pvalue.strval &&
        AbsString("length") <= P &&
        AbsBool.False <= (TypeConversionHelper.ToNumber(dv) === TypeConversionHelper.ToUInt32(dv))) HashSet(RangeError)
      else ExcSetEmpty
    (obj1 + obj2 + obj3 + obj5 + obj6, b1 + b2 + b3 + b5 + b6, excSet1 ++ excSet2 ++ excSet3 ++ excSet4)
  }
}
