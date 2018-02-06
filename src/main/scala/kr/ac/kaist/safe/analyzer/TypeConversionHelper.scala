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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.util._

import scala.collection.immutable.HashSet

////////////////////////////////////////////////////////////////
// Abstract helper functions of
// Section 9. Type Conversion and Testing, ECMASCript 5.1
////////////////////////////////////////////////////////////////
object TypeConversionHelper {

  ////////////////////////////////////////////////////////////////
  // 9.1 ToPrimitive
  ////////////////////////////////////////////////////////////////
  def ToPrimitive(value: AbsValue): AbsPValue =
    value.pvalue ⊔ AbsObj.defaultValue(value.locset)

  def ToPrimitive(value: AbsValue, preferredType: String): AbsPValue =
    value.pvalue ⊔ AbsObj.defaultValue(value.locset, preferredType)

  def ToPrimitive(locSet: AbsLoc, preferredType: String): AbsPValue =
    AbsObj.defaultValue(locSet, preferredType)

  def ToPrimitive(value: AbsValue, h: AbsHeap, preferredType: String = "String"): AbsPValue =
    value.pvalue ⊔ AbsObj.defaultValue(value.locset, h, preferredType)

  def ToPrimitive(locSet: AbsLoc, h: AbsHeap, preferredType: String): AbsPValue =
    AbsObj.defaultValue(locSet, h, preferredType)

  ////////////////////////////////////////////////////////////////
  // 9.2 ToBoolean
  // Detailed abstract meaning of Table 11 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToBoolean(value: AbsValue): AbsBool = {
    val abool6 = if (value.locset.isBottom) AbsBool.Bot else AbsBool.True
    ToBoolean(value.pvalue) ⊔ abool6
  }

  def ToBoolean(pvalue: AbsPValue): AbsBool =
    ToBoolean(pvalue.undefval) ⊔
      ToBoolean(pvalue.nullval) ⊔
      ToBoolean(pvalue.boolval) ⊔
      ToBoolean(pvalue.numval) ⊔
      ToBoolean(pvalue.strval)

  def ToBoolean(undef: AbsUndef): AbsBool =
    undef.fold(AbsBool.Bot)(_ => AbsBool.False)

  def ToBoolean(x: AbsNull): AbsBool =
    x.fold(AbsBool.Bot)(_ => AbsBool.False)

  def ToBoolean(bool: AbsBool): AbsBool = bool

  def ToBoolean(num: AbsNum): AbsBool = num.ToBoolean

  def ToBoolean(str: AbsStr): AbsBool = str.ToBoolean

  ////////////////////////////////////////////////////////////////
  // 9.3. ToNumber
  // Detailed abstract meaning of Table 12 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToNumber(value: AbsValue): AbsNum = {
    val anum6 = ToNumber(ToPrimitive(value.locset, preferredType = "Number"))
    ToNumber(value.pvalue) ⊔ anum6
  }

  def ToNumber(value: AbsValue, h: AbsHeap): AbsNum = {
    val anum6 = ToNumber(ToPrimitive(value.locset, h, preferredType = "Number"))
    ToNumber(value.pvalue) ⊔ anum6
  }

  def ToNumber(pvalue: AbsPValue): AbsNum =
    ToNumber(pvalue.undefval) ⊔
      ToNumber(pvalue.nullval) ⊔
      ToNumber(pvalue.boolval) ⊔
      ToNumber(pvalue.numval) ⊔
      ToNumber(pvalue.strval)

  def ToNumber(undef: AbsUndef): AbsNum =
    undef.fold(AbsNum.Bot)(_ => AbsNum.NaN)

  def ToNumber(x: AbsNull): AbsNum =
    x.fold(AbsNum.Bot)(_ => AbsNum(+0))

  def ToNumber(bool: AbsBool): AbsNum = bool.ToNumber

  def ToNumber(num: AbsNum): AbsNum = num

  def ToNumber(str: AbsStr): AbsNum = str.ToNumber

  ////////////////////////////////////////////////////////////////
  // 9.4 ToInteger
  // Detailed abstract meaning of ToInteger relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToInteger(value: AbsValue): AbsNum =
    ToNumber(value).ToInteger

  def ToInteger(value: AbsValue, h: AbsHeap): AbsNum =
    ToNumber(value, h).ToInteger

  ////////////////////////////////////////////////////////////////
  // 9.5 ToInt32
  // Detailed abstract meaning of ToInt32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToInt32(value: AbsValue): AbsNum =
    ToNumber(value).ToInt32

  def ToInt32(value: AbsValue, h: AbsHeap): AbsNum =
    ToNumber(value, h).ToInt32

  ////////////////////////////////////////////////////////////////
  // 9.6 ToUint32
  // Detailed abstract meaning of ToUint32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUint32(value: AbsValue): AbsNum =
    ToNumber(value).ToUint32

  def ToUint32(value: AbsValue, h: AbsHeap): AbsNum =
    ToNumber(value, h).ToUint32

  ////////////////////////////////////////////////////////////////
  // 9.7 ToUint16
  // Detailed abstract meaning of ToUint16 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUint16(value: AbsValue): AbsNum =
    ToNumber(value).ToUint16

  def ToUint16(value: AbsValue, h: AbsHeap): AbsNum =
    ToNumber(value, h).ToUint16

  ////////////////////////////////////////////////////////////////
  // 9.8 ToString
  // Detailed abstract meaning of Table 13 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToString(value: AbsValue): AbsStr = {
    val astr6 = ToString(ToPrimitive(value.locset, preferredType = "String"))
    ToString(value.pvalue) ⊔ astr6
  }

  def ToString(value: AbsValue, h: AbsHeap): AbsStr = {
    val astr6 = ToString(ToPrimitive(value.locset, h, preferredType = "String"))
    ToString(value.pvalue) ⊔ astr6
  }

  def ToString(pvalue: AbsPValue): AbsStr =
    ToString(pvalue.undefval) ⊔
      ToString(pvalue.nullval) ⊔
      ToString(pvalue.boolval) ⊔
      ToString(pvalue.numval) ⊔
      ToString(pvalue.strval)

  def ToString(undef: AbsUndef): AbsStr =
    undef.fold(AbsStr.Bot)(_ => AbsStr("undefined"))

  def ToString(x: AbsNull): AbsStr =
    x.fold(AbsStr.Bot)(_ => AbsStr("null"))

  def ToString(bool: AbsBool): AbsStr = bool.ToString

  def ToString(num: AbsNum): AbsStr = num.ToString

  def ToString(str: AbsStr): AbsStr = str

  ////////////////////////////////////////////////////////////////
  // 9.9 ToObject, Table 14
  // Detailed abstract meaning of ToObject relies on
  // implementation of abstract object and builtin modeling.
  ////////////////////////////////////////////////////////////////
  def ToObject(pvalue: AbsPValue): (AbsObj, Set[Exception]) = {
    val excSet = CheckObjectCoercible(pvalue)
    val obj3 = pvalue.numval.fold(AbsObj.Bot) { AbsObj.newNumberObj(_) }
    val obj4 = pvalue.boolval.fold(AbsObj.Bot) { AbsObj.newBooleanObj(_) }
    val obj5 = pvalue.strval.fold(AbsObj.Bot) { AbsObj.newStringObj(_) }
    (obj3 ⊔ obj4 ⊔ obj5, excSet)
  }

  def ToObject(value: AbsValue, st: AbsState, asite: AllocSite): (AbsLoc, AbsState, Set[Exception]) = {
    val locSet = value.locset
    val (obj, excSet) = ToObject(value.pvalue)

    val (locSet1, st1) =
      if (!obj.isBottom) {
        val loc = Loc(asite)
        val state = st.oldify(loc)
        (AbsLoc(loc), AbsState(state.heap.update(loc, obj), state.context))
      } else (AbsLoc.Bot, AbsState.Bot)
    val (locSet2, st2) =
      if (!locSet.isBottom) (locSet, st)
      else (AbsLoc.Bot, AbsState.Bot)

    (locSet1 ⊔ locSet2, st1 ⊔ st2, excSet)
  }

  ////////////////////////////////////////////////////////////////
  // 9.10 CheckObjectCoercible, Table 15
  ////////////////////////////////////////////////////////////////
  def CheckObjectCoercible(value: AbsValue): Set[Exception] =
    CheckObjectCoercible(value.pvalue)

  def CheckObjectCoercible(pvalue: AbsPValue): Set[Exception] = {
    (pvalue.undefval.isBottom, pvalue.nullval.isBottom) match {
      case (false, _) | (_, false) => HashSet(TypeError)
      case (true, true) => HashSet[Exception]()
    }
  }

  ////////////////////////////////////////////////////////////////
  // 9.11 IsCallable, Table 16
  ////////////////////////////////////////////////////////////////
  def IsCallable(value: AbsValue): AbsBool = {
    val abool1 = value.pvalue.undefval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool2 = value.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool3 = value.pvalue.boolval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool4 = value.pvalue.numval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool5 = value.pvalue.strval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool6 = if (value.locset.isBottom) AbsBool.Bot else AbsBool.Top
    abool1 ⊔ abool2 ⊔ abool3 ⊔ abool4 ⊔ abool5 ⊔ abool6
  }

  def IsCallable(value: AbsValue, h: AbsHeap): AbsBool = {
    val abool1 = value.pvalue.undefval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool2 = value.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool3 = value.pvalue.boolval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool4 = value.pvalue.numval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool5 = value.pvalue.strval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool6 = value.locset.foldLeft(AbsBool.Bot)((tmpAbool, l) =>
      tmpAbool ⊔ IsCallable(l, h))
    abool1 ⊔ abool2 ⊔ abool3 ⊔ abool4 ⊔ abool5 ⊔ abool6
  }

  def IsCallable(loc: Loc, h: AbsHeap): AbsBool = {
    val isDomIn = h.get(loc).fold(AbsBool.False) { obj => (obj contains ICall) }
    val b1 =
      if (AbsBool.True ⊑ isDomIn) AbsBool.True
      else AbsBool.Bot
    val b2 =
      if (AbsBool.False ⊑ isDomIn) AbsBool.False
      else AbsBool.Bot
    b1 ⊔ b2
  }

  ////////////////////////////////////////////////////////////////
  // 9.12 The SameValue Algorithm
  // This algorithm differs from the strict equal(StrictEquals) in its
  // treatment of signed zeros and NaN
  ////////////////////////////////////////////////////////////////
  def SameValue(h: AbsHeap, left: AbsValue, right: AbsValue): AbsBool = {
    val isMultiType =
      if ((left ⊔ right).typeCount > 1) AbsBool.False
      else AbsBool.Bot

    val isSame1 = (left.pvalue.undefval StrictEquals right.pvalue.undefval)
    val isSame2 = (left.pvalue.nullval StrictEquals right.pvalue.nullval)
    val isSame3 = (left.pvalue.boolval StrictEquals right.pvalue.boolval)
    val isSame4 = (left.pvalue.numval SameValue right.pvalue.numval)
    val isSame5 = (left.pvalue.strval StrictEquals right.pvalue.strval)
    val isSame6 =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset ⊓ right.locset
        (left.locset.getSingle, right.locset.getSingle, intersect.getSingle) match {
          case (_, _, ConZero()) => AbsBool.False
          case (ConOne(_), ConOne(_), ConOne(loc)) if h.isConcrete(loc) => AbsBool.True
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot

    isMultiType ⊔ isSame1 ⊔ isSame2 ⊔ isSame3 ⊔ isSame4 ⊔ isSame5 ⊔ isSame6
  }

  ////////////////////////////////////////////////////////////////
  // Additional type-related helper functions
  ////////////////////////////////////////////////////////////////
  def typeTag(value: AbsValue, h: AbsHeap): AbsStr = {
    val s1 = value.pvalue.undefval.fold(AbsStr.Bot)(_ => {
      AbsStr("undefined")
    })
    val s2 = value.pvalue.nullval.fold(AbsStr.Bot)(_ => {
      AbsStr("object") //TODO: check null type?
    })
    val s3 = value.pvalue.numval.fold(AbsStr.Bot)(_ => {
      AbsStr("number")
    })
    val s4 = value.pvalue.boolval.fold(AbsStr.Bot)(_ => {
      AbsStr("boolean")
    })
    val s5 = value.pvalue.strval.fold(AbsStr.Bot)(_ => {
      AbsStr("string")
    })

    val isCallableLocSet = value.locset.foldLeft(AbsBool.Bot)((tmpAbsB, l) => tmpAbsB ⊔ IsCallable(l, h))
    val s6 =
      if (!value.locset.isBottom && (AbsBool.False ⊑ isCallableLocSet))
        AbsStr("object")
      else AbsStr.Bot
    val s7 =
      if (!value.locset.isBottom && (AbsBool.True ⊑ isCallableLocSet))
        AbsStr("function")
      else AbsStr.Bot

    s1 ⊔ s2 ⊔ s3 ⊔ s4 ⊔ s5 ⊔ s6 ⊔ s7
  }
}
