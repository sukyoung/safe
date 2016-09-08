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
import kr.ac.kaist.safe.util.Address

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
    value.pvalue + AbsObjectUtil.defaultValue(value.locset)

  def ToPrimitive(value: AbsValue, preferredType: String): AbsPValue =
    value.pvalue + AbsObjectUtil.defaultValue(value.locset, preferredType)

  def ToPrimitive(locSet: AbsLoc, preferredType: String): AbsPValue =
    AbsObjectUtil.defaultValue(locSet, preferredType)

  def ToPrimitive(value: AbsValue, h: Heap, preferredType: String = "String"): AbsPValue =
    value.pvalue + AbsObjectUtil.defaultValue(value.locset, h, preferredType)

  def ToPrimitive(locSet: AbsLoc, h: Heap, preferredType: String): AbsPValue =
    AbsObjectUtil.defaultValue(locSet, h, preferredType)

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

  def ToNumber(value: AbsValue, h: Heap): AbsNumber = {
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

  def ToInteger(value: AbsValue, h: Heap): AbsNumber =
    ToNumber(value, h).toInteger

  ////////////////////////////////////////////////////////////////
  // 9.5 ToInt32
  // Detailed abstract meaning of ToInt32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToInt32(value: AbsValue): AbsNumber =
    ToNumber(value).toInt32

  def ToInt32(value: AbsValue, h: Heap): AbsNumber =
    ToNumber(value, h).toInt32

  ////////////////////////////////////////////////////////////////
  // 9.6 ToUInt32
  // Detailed abstract meaning of ToUInt32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUInt32(value: AbsValue): AbsNumber =
    ToNumber(value).toUInt32

  def ToUInt32(value: AbsValue, h: Heap): AbsNumber =
    ToNumber(value, h).toUInt32

  ////////////////////////////////////////////////////////////////
  // 9.7 ToUInt16
  // Detailed abstract meaning of ToUInt16 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUInt16(value: AbsValue): AbsNumber =
    ToNumber(value).toUInt16

  def ToUInt16(value: AbsValue, h: Heap): AbsNumber =
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

  def ToString(value: AbsValue, h: Heap): AbsString = {
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
  def ToObject(pvalue: AbsPValue): (Obj, Set[Exception]) = {
    val excSet = CheckObjectCoercible(pvalue)
    val obj3 = pvalue.numval.fold(AbsObjectUtil.Bot) { AbsObjectUtil.newNumberObj(_) }
    val obj4 = pvalue.boolval.fold(AbsObjectUtil.Bot) { AbsObjectUtil.newBooleanObj(_) }
    val obj5 = pvalue.strval.fold(AbsObjectUtil.Bot) { AbsObjectUtil.newStringObj(_) }
    (obj3 + obj4 + obj5, excSet)
  }

  def ToObject(value: AbsValue, st: State, addr: Address): (AbsValue, State, Set[Exception]) = {
    val locSet = value.locset
    val (obj, excSet) = ToObject(value.pvalue)

    val (locSet1, h1) =
      if (!obj.isBottom) {
        val loc = Loc(addr, Recent)
        (AbsLoc(loc), st.oldify(addr).heap.update(loc, obj))
      } else (AbsLoc.Bot, Heap.Bot)
    val (locSet2, h2) =
      if (!locSet.isBottom) (locSet, st.heap)
      else (AbsLoc.Bot, Heap.Bot)

    (AbsValue(locSet1 + locSet2), State(h1 + h2, st.context), excSet)
  }

  ////////////////////////////////////////////////////////////////
  // 9.10 CheckObjectCoercible, Table 15
  ////////////////////////////////////////////////////////////////
  def CheckObjectCoercible(value: AbsValue): Set[Exception] =
    CheckObjectCoercible(value.pvalue)

  def CheckObjectCoercible(pvalue: AbsPValue): Set[Exception] = {
    (pvalue.undefval.gamma, pvalue.nullval.gamma) match {
      case (ConSimpleTop(), _) | (_, ConSimpleTop()) => HashSet(TypeError)
      case (ConSimpleBot(), ConSimpleBot()) => HashSet[Exception]()
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

  def IsCallable(value: AbsValue, h: Heap): AbsBool = {
    val abool1 = value.pvalue.undefval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool2 = value.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool3 = value.pvalue.boolval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool4 = value.pvalue.numval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool5 = value.pvalue.strval.fold(AbsBool.Bot) { _ => AbsBool.False }
    val abool6 = value.locset.foldLeft(AbsBool.Bot)((tmpAbool, l) =>
      tmpAbool + IsCallable(l, h))
    abool1 + abool2 + abool3 + abool4 + abool5 + abool6
  }

  def IsCallable(loc: Loc, h: Heap): AbsBool = {
    val isDomIn = h.getOrElse(loc)(AbsBool.False) { obj => (obj domIn ICall) }
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
        (left.locset.gammaSingle, right.locset.gammaSingle, intersect.gammaSingle) match {
          case (_, _, ConSingleBot()) => AbsBool.False
          case (ConSingleCon(_), ConSingleCon(_), ConSingleCon(loc)) if loc.recency == Recent => AbsBool.True
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot

    isMultiType + isSame1 + isSame2 + isSame3 + isSame4 + isSame5 + isSame6
  }

  ////////////////////////////////////////////////////////////////
  // Additional type-related helper functions
  ////////////////////////////////////////////////////////////////
  def typeTag(value: AbsValue, h: Heap): AbsString = {
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
