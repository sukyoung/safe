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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
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
    value.pvalue + AbsObject.defaultValue(value.locset)

  def ToPrimitive(value: AbsValue, preferredType: String): AbsPValue =
    value.pvalue + AbsObject.defaultValue(value.locset, preferredType)

  def ToPrimitive(locSet: AbsLoc, preferredType: String): AbsPValue =
    AbsObject.defaultValue(locSet, preferredType)

  def ToPrimitive(value: AbsValue, h: AbsHeap, preferredType: String = "String"): AbsPValue =
    value.pvalue + AbsObject.defaultValue(value.locset, h, preferredType)

  def ToPrimitive(locSet: AbsLoc, h: AbsHeap, preferredType: String): AbsPValue =
    AbsObject.defaultValue(locSet, h, preferredType)

  ////////////////////////////////////////////////////////////////
  // 9.2 ToBoolean
  // Detailed abstract meaning of Table 11 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToBoolean(value: AbsValue): AbsBool = {
    val abool6 = if (value.locset.isBottom) AbsBool.Bot else AbsBool.True
    ToBoolean(value.pvalue) + abool6
  }

  def ToBoolean(pvalue: AbsPValue): AbsBool =
    ToBoolean(pvalue.undefval) +
      ToBoolean(pvalue.nullval) +
      ToBoolean(pvalue.boolval) +
      ToBoolean(pvalue.numval) +
      ToBoolean(pvalue.strval)

  def ToBoolean(undef: AbsUndef): AbsBool =
    undef.fold(AbsBool.Bot)(_ => AbsBool.False)

  def ToBoolean(x: AbsNull): AbsBool =
    x.fold(AbsBool.Bot)(_ => AbsBool.False)

  def ToBoolean(bool: AbsBool): AbsBool = bool

  def ToBoolean(num: AbsNumber): AbsBool = num.toAbsBoolean

  def ToBoolean(str: AbsString): AbsBool = str.toAbsBoolean

  ////////////////////////////////////////////////////////////////
  // 9.3. ToNumber
  // Detailed abstract meaning of Table 12 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToNumber(value: AbsValue): AbsNumber = {
    val anum6 = ToNumber(ToPrimitive(value.locset, preferredType = "Number"))
    ToNumber(value.pvalue) + anum6
  }

  def ToNumber(value: AbsValue, h: AbsHeap): AbsNumber = {
    val anum6 = ToNumber(ToPrimitive(value.locset, h, preferredType = "Number"))
    ToNumber(value.pvalue) + anum6
  }

  def ToNumber(pvalue: AbsPValue): AbsNumber =
    ToNumber(pvalue.undefval) +
      ToNumber(pvalue.nullval) +
      ToNumber(pvalue.boolval) +
      ToNumber(pvalue.numval) +
      ToNumber(pvalue.strval)

  def ToNumber(undef: AbsUndef): AbsNumber =
    undef.fold(AbsNumber.Bot)(_ => AbsNumber.NaN)

  def ToNumber(x: AbsNull): AbsNumber =
    x.fold(AbsNumber.Bot)(_ => AbsNumber(+0))

  def ToNumber(bool: AbsBool): AbsNumber = bool.toAbsNumber

  def ToNumber(num: AbsNumber): AbsNumber = num

  def ToNumber(str: AbsString): AbsNumber = str.toAbsNumber

  ////////////////////////////////////////////////////////////////
  // 9.4 ToInteger
  // Detailed abstract meaning of ToInteger relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToInteger(value: AbsValue): AbsNumber =
    ToNumber(value).toInteger

  def ToInteger(value: AbsValue, h: AbsHeap): AbsNumber =
    ToNumber(value, h).toInteger

  ////////////////////////////////////////////////////////////////
  // 9.5 ToInt32
  // Detailed abstract meaning of ToInt32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToInt32(value: AbsValue): AbsNumber =
    ToNumber(value).toInt32

  def ToInt32(value: AbsValue, h: AbsHeap): AbsNumber =
    ToNumber(value, h).toInt32

  ////////////////////////////////////////////////////////////////
  // 9.6 ToUInt32
  // Detailed abstract meaning of ToUInt32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUInt32(value: AbsValue): AbsNumber =
    ToNumber(value).toUInt32

  def ToUInt32(value: AbsValue, h: AbsHeap): AbsNumber =
    ToNumber(value, h).toUInt32

  ////////////////////////////////////////////////////////////////
  // 9.7 ToUInt16
  // Detailed abstract meaning of ToUInt16 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUInt16(value: AbsValue): AbsNumber =
    ToNumber(value).toUInt16

  def ToUInt16(value: AbsValue, h: AbsHeap): AbsNumber =
    ToNumber(value, h).toUInt16

  ////////////////////////////////////////////////////////////////
  // 9.8 ToString
  // Detailed abstract meaning of Table 13 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToString(value: AbsValue): AbsString = {
    val astr6 = ToString(ToPrimitive(value.locset, preferredType = "String"))
    ToString(value.pvalue) + astr6
  }

  def ToString(value: AbsValue, h: AbsHeap): AbsString = {
    val astr6 = ToString(ToPrimitive(value.locset, h, preferredType = "String"))
    ToString(value.pvalue) + astr6
  }

  def ToString(pvalue: AbsPValue): AbsString =
    ToString(pvalue.undefval) +
      ToString(pvalue.nullval) +
      ToString(pvalue.boolval) +
      ToString(pvalue.numval) +
      ToString(pvalue.strval)

  def ToString(undef: AbsUndef): AbsString =
    undef.fold(AbsString.Bot)(_ => AbsString("undefined"))

  def ToString(x: AbsNull): AbsString =
    x.fold(AbsString.Bot)(_ => AbsString("null"))

  def ToString(bool: AbsBool): AbsString = bool.toAbsString

  def ToString(num: AbsNumber): AbsString = num.toAbsString

  def ToString(str: AbsString): AbsString = str

  ////////////////////////////////////////////////////////////////
  // 9.9 ToObject, Table 14
  // Detailed abstract meaning of ToObject relies on
  // implementation of abstract object and builtin modeling.
  ////////////////////////////////////////////////////////////////
  def ToObject(pvalue: AbsPValue): (AbsObject, Set[Exception]) = {
    val excSet = CheckObjectCoercible(pvalue)
    val obj3 = pvalue.numval.fold(AbsObject.Bot) { AbsObject.newNumberObj(_) }
    val obj4 = pvalue.boolval.fold(AbsObject.Bot) { AbsObject.newBooleanObj(_) }
    val obj5 = pvalue.strval.fold(AbsObject.Bot) { AbsObject.newStringObj(_) }
    (obj3 + obj4 + obj5, excSet)
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

    (locSet1 + locSet2, st1 + st2, excSet)
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
    abool1 + abool2 + abool3 + abool4 + abool5 + abool6
  }

  def IsCallable(value: AbsValue, h: AbsHeap): AbsBool = {
    val abool1 = value.pvalue.undefval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool2 = value.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool3 = value.pvalue.boolval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool4 = value.pvalue.numval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool5 = value.pvalue.strval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool6 = value.locset.foldLeft(AbsBool.Bot)((tmpAbool, l) =>
      tmpAbool + IsCallable(l, h))
    abool1 + abool2 + abool3 + abool4 + abool5 + abool6
  }

  def IsCallable(loc: Loc, h: AbsHeap): AbsBool = {
    val isDomIn = h.get(loc).fold(AbsBool.False) { obj => (obj contains ICall) }
    val b1 =
      if (AbsBool.True <= isDomIn) AbsBool.True
      else AbsBool.Bot
    val b2 =
      if (AbsBool.False <= isDomIn) AbsBool.False
      else AbsBool.Bot
    b1 + b2
  }

  ////////////////////////////////////////////////////////////////
  // 9.12 The SameValue Algorithm
  // This algorithm differs from the strict equal(===) in its
  // treatment of signed zeros and NaN
  ////////////////////////////////////////////////////////////////
  def SameValue(left: AbsValue, right: AbsValue): AbsBool = {
    val isMultiType =
      if ((left + right).typeCount > 1) AbsBool.False
      else AbsBool.Bot

    val isSame1 = (left.pvalue.undefval === right.pvalue.undefval)
    val isSame2 = (left.pvalue.nullval === right.pvalue.nullval)
    val isSame3 = (left.pvalue.boolval === right.pvalue.boolval)
    val isSame4 = (left.pvalue.numval sameValue right.pvalue.numval)
    val isSame5 = (left.pvalue.strval === right.pvalue.strval)
    val isSame6 =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset <> right.locset
        (left.locset.getSingle, right.locset.getSingle, intersect.getSingle) match {
          case (_, _, ConZero()) => AbsBool.False
          case (ConOne(_), ConOne(_), ConOne(loc)) if loc.isConcrete => AbsBool.True
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot

    isMultiType + isSame1 + isSame2 + isSame3 + isSame4 + isSame5 + isSame6
  }

  ////////////////////////////////////////////////////////////////
  // Additional type-related helper functions
  ////////////////////////////////////////////////////////////////
  def typeTag(value: AbsValue, h: AbsHeap): AbsString = {
    val s1 = value.pvalue.undefval.fold(AbsString.Bot)(_ => {
      AbsString("undefined")
    })
    val s2 = value.pvalue.nullval.fold(AbsString.Bot)(_ => {
      AbsString("object") //TODO: check null type?
    })
    val s3 = value.pvalue.numval.fold(AbsString.Bot)(_ => {
      AbsString("number")
    })
    val s4 = value.pvalue.boolval.fold(AbsString.Bot)(_ => {
      AbsString("boolean")
    })
    val s5 = value.pvalue.strval.fold(AbsString.Bot)(_ => {
      AbsString("string")
    })

    val isCallableLocSet = value.locset.foldLeft(AbsBool.Bot)((tmpAbsB, l) => tmpAbsB + IsCallable(l, h))
    val s6 =
      if (!value.locset.isBottom && (AbsBool.False <= isCallableLocSet))
        AbsString("object")
      else AbsString.Bot
    val s7 =
      if (!value.locset.isBottom && (AbsBool.True <= isCallableLocSet))
        AbsString("function")
      else AbsString.Bot

    s1 + s2 + s3 + s4 + s5 + s6 + s7
  }
}
