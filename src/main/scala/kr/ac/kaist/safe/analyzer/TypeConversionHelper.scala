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
import kr.ac.kaist.safe.util.{ Address, Loc, Recent }

import scala.collection.immutable.HashSet

////////////////////////////////////////////////////////////////
// Abstract helper functions of
// Section 9. Type Conversion and Testing, ECMASCript 5.1
////////////////////////////////////////////////////////////////
case class TypeConversionHelper(utils: Utils) {
  val absUndefU: AbsUndefUtil = utils.absUndef
  val absNullU: AbsNullUtil = utils.absNull
  val absBoolU: AbsBoolUtil = utils.absBool
  val absNumberU: AbsNumberUtil = utils.absNumber
  val absStringU: AbsStringUtil = utils.absString
  val pvalueU: PValueUtil = utils.pvalue
  val objU: AbsObjectUtil = utils.absObject

  ////////////////////////////////////////////////////////////////
  // 9.1 ToPrimitive
  ////////////////////////////////////////////////////////////////
  def ToPrimitive(value: Value): PValue =
    value.pvalue + objU.defaultValue(value.locset)

  def ToPrimitive(value: Value, preferredType: String): PValue =
    value.pvalue + objU.defaultValue(value.locset, preferredType)

  def ToPrimitive(locSet: Set[Loc], preferredType: String): PValue =
    objU.defaultValue(locSet, preferredType)

  def ToPrimitive(value: Value, h: Heap, preferredType: String = "String"): PValue =
    value.pvalue + objU.defaultValue(value.locset, h, preferredType)

  def ToPrimitive(locSet: Set[Loc], h: Heap, preferredType: String): PValue =
    objU.defaultValue(locSet, h, preferredType)

  ////////////////////////////////////////////////////////////////
  // 9.2 ToBoolean
  // Detailed abstract meaning of Table 11 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToBoolean(value: Value): AbsBool = {
    val abool1 = value.pvalue.undefval.toAbsBoolean(absBoolU)
    val abool2 = value.pvalue.nullval.toAbsBoolean(absBoolU)
    val abool3 = value.pvalue.boolval.toAbsBoolean(absBoolU)
    val abool4 = value.pvalue.numval.toAbsBoolean(absBoolU)
    val abool5 = value.pvalue.strval.toAbsBoolean(absBoolU)
    val abool6 = if (value.locset.isEmpty) absBoolU.Bot else absBoolU.True
    abool1 + abool2 + abool3 + abool4 + abool5 + abool6
  }

  ////////////////////////////////////////////////////////////////
  // 9.3. ToNumber
  // Detailed abstract meaning of Table 12 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToNumber(value: Value): AbsNumber = {
    val anum6 = ToNumber(ToPrimitive(value.locset, preferredType = "Number"))
    ToNumber(value.pvalue) + anum6
  }

  def ToNumber(value: Value, h: Heap): AbsNumber = {
    val anum6 = ToNumber(ToPrimitive(value.locset, h, preferredType = "Number"))
    ToNumber(value.pvalue) + anum6
  }

  def ToNumber(pvalue: PValue): AbsNumber = {
    val anum1 = pvalue.undefval.toAbsNumber(absNumberU)
    val anum2 = pvalue.nullval.toAbsNumber(absNumberU)
    val anum3 = pvalue.boolval.toAbsNumber(absNumberU)
    val anum4 = pvalue.numval.toAbsNumber(absNumberU)
    val anum5 = pvalue.strval.toAbsNumber(absNumberU)
    anum1 + anum2 + anum3 + anum4 + anum5
  }

  ////////////////////////////////////////////////////////////////
  // 9.4 ToInteger
  // Detailed abstract meaning of ToInteger relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToInteger(value: Value): AbsNumber =
    ToNumber(value).toInteger

  def ToInteger(value: Value, h: Heap): AbsNumber =
    ToNumber(value, h).toInteger

  ////////////////////////////////////////////////////////////////
  // 9.5 ToInt32
  // Detailed abstract meaning of ToInt32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToInt32(value: Value): AbsNumber =
    ToNumber(value).toInt32

  def ToInt32(value: Value, h: Heap): AbsNumber =
    ToNumber(value, h).toInt32

  ////////////////////////////////////////////////////////////////
  // 9.6 ToUInt32
  // Detailed abstract meaning of ToUInt32 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUInt32(value: Value): AbsNumber =
    ToNumber(value).toUInt32

  def ToUInt32(value: Value, h: Heap): AbsNumber =
    ToNumber(value, h).toUInt32

  ////////////////////////////////////////////////////////////////
  // 9.7 ToUInt16
  // Detailed abstract meaning of ToUInt16 relies on
  // implementation of abstract number domain.
  ////////////////////////////////////////////////////////////////
  def ToUInt16(value: Value): AbsNumber =
    ToNumber(value).toUInt16

  def ToUInt16(value: Value, h: Heap): AbsNumber =
    ToNumber(value, h).toUInt16

  ////////////////////////////////////////////////////////////////
  // 9.8 ToString
  // Detailed abstract meaning of Table 13 relies on
  // implementation of each abstract domains.
  ////////////////////////////////////////////////////////////////
  def ToString(value: Value): AbsString = {
    val astr6 = ToString(ToPrimitive(value.locset, preferredType = "String"))
    ToString(value.pvalue) + astr6
  }

  def ToString(value: Value, h: Heap): AbsString = {
    val astr6 = ToString(ToPrimitive(value.locset, h, preferredType = "String"))
    ToString(value.pvalue) + astr6
  }

  def ToString(pvalue: PValue): AbsString = {
    val astr1 = pvalue.undefval.toAbsString(absStringU)
    val astr2 = pvalue.nullval.toAbsString(absStringU)
    val astr3 = pvalue.boolval.toAbsString(absStringU)
    val astr4 = pvalue.numval.toAbsString(absStringU)
    val astr5 = pvalue.strval.toAbsString(absStringU)
    astr1 + astr2 + astr3 + astr4 + astr5
  }

  ////////////////////////////////////////////////////////////////
  // 9.9 ToObject, Table 14
  // Detailed abstract meaning of ToObject relies on
  // implementation of abstract object and builtin modeling.
  ////////////////////////////////////////////////////////////////
  def ToObject(pvalue: PValue): (Obj, Set[Exception]) = {
    val excSet = CheckObjectCoercible(pvalue)
    val obj3 = pvalue.numval.fold(objU.Bot) { objU.newNumberObj(_) }
    val obj4 = pvalue.boolval.fold(objU.Bot) { objU.newBooleanObj(_) }
    val obj5 = pvalue.strval.fold(objU.Bot) { objU.newStringObj(_) }
    (obj3 + obj4 + obj5, excSet)
  }

  def ToObject(value: Value, st: State, addr: Address): (Value, State, Set[Exception]) = {
    val locSet = value.locset
    val (obj, excSet) = ToObject(value.pvalue)

    val (locSet1, h1) =
      if (!obj.isBottom) {
        val loc = Loc(addr, Recent)
        (HashSet(loc), st.oldify(addr)(utils).heap.update(loc, obj))
      } else (LocSetEmpty, Heap.Bot)
    val (locSet2, h2) =
      if (!locSet.isEmpty) (locSet, st.heap)
      else (LocSetEmpty, Heap.Bot)

    (utils.value(locSet1 ++ locSet2), State(h1 + h2, st.context), excSet)
  }

  ////////////////////////////////////////////////////////////////
  // 9.10 CheckObjectCoercible, Table 15
  ////////////////////////////////////////////////////////////////
  def CheckObjectCoercible(value: Value): Set[Exception] =
    CheckObjectCoercible(value.pvalue)

  def CheckObjectCoercible(pvalue: PValue): Set[Exception] = {
    (pvalue.undefval.gamma, pvalue.nullval.gamma) match {
      case (ConSimpleTop, _) | (_, ConSimpleTop) => HashSet(TypeError)
      case (ConSimpleBot, ConSimpleBot) => HashSet[Exception]()
    }
  }

  ////////////////////////////////////////////////////////////////
  // 9.11 IsCallable, Table 16
  ////////////////////////////////////////////////////////////////
  def IsCallable(value: Value): AbsBool = {
    val abool1 = value.pvalue.undefval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool2 = value.pvalue.nullval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool3 = value.pvalue.boolval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool4 = value.pvalue.numval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool5 = value.pvalue.strval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool6 = if (value.locset.isEmpty) absBoolU.Bot else absBoolU.Top
    abool1 + abool2 + abool3 + abool4 + abool5 + abool6
  }

  def IsCallable(value: Value, h: Heap): AbsBool = {
    val abool1 = value.pvalue.undefval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool2 = value.pvalue.nullval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool3 = value.pvalue.boolval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool4 = value.pvalue.numval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool5 = value.pvalue.strval.fold(absBoolU.Bot) { _ => absBoolU.False }
    val abool6 = value.locset.foldLeft(absBoolU.Bot)((tmpAbool, l) =>
      tmpAbool + IsCallable(l, h))
    abool1 + abool2 + abool3 + abool4 + abool5 + abool6
  }

  def IsCallable(loc: Loc, h: Heap): AbsBool = {
    val isDomIn = h.getOrElse(loc)(absBoolU.False) { obj => (obj domIn ICall)(absBoolU) }
    val b1 =
      if (absBoolU.True <= isDomIn) absBoolU.True
      else absBoolU.Bot
    val b2 =
      if (absBoolU.False <= isDomIn) absBoolU.False
      else absBoolU.Bot
    b1 + b2
  }

  ////////////////////////////////////////////////////////////////
  // 9.12 The SameValue Algorithm
  // This algorithm differs from the strict equal(===) in its
  // treatment of signed zeros and NaN
  ////////////////////////////////////////////////////////////////
  def SameValue(left: Value, right: Value): AbsBool = {
    val isMultiType =
      if ((left + right).typeCount > 1) absBoolU.False
      else absBoolU.Bot

    val isSame1 = (left.pvalue.undefval === right.pvalue.undefval)(absBoolU)
    val isSame2 = (left.pvalue.nullval === right.pvalue.nullval)(absBoolU)
    val isSame3 = (left.pvalue.boolval === right.pvalue.boolval)(absBoolU)
    val isSame4 = (left.pvalue.numval sameValue right.pvalue.numval)(absBoolU)
    val isSame5 = (left.pvalue.strval === right.pvalue.strval)(absBoolU)
    val isSame6 =
      if (!left.locset.isEmpty && !right.locset.isEmpty) {
        val intersect = left.locset.intersect(right.locset)
        if (intersect.isEmpty) absBoolU.False
        else if (left.locset.size == 1 && right.locset.size == 1 && intersect.head.recency == Recent) absBoolU.True
        else absBoolU.Top
      } else absBoolU.Bot

    isMultiType + isSame1 + isSame2 + isSame3 + isSame4 + isSame5 + isSame6
  }

  ////////////////////////////////////////////////////////////////
  // Additional type-related helper functions
  ////////////////////////////////////////////////////////////////
  def typeTag(value: Value, h: Heap): AbsString = {
    val s1 = value.pvalue.undefval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("undefined")
    })
    val s2 = value.pvalue.nullval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("object") //TODO: check null type?
    })
    val s3 = value.pvalue.numval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("number")
    })
    val s4 = value.pvalue.boolval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("boolean")
    })
    val s5 = value.pvalue.strval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("string")
    })

    val isCallableLocSet = value.locset.foldLeft(utils.absBool.Bot)((tmpAbsB, l) => tmpAbsB + IsCallable(l, h))
    val s6 =
      if (value.locset.nonEmpty && (utils.absBool.False <= isCallableLocSet))
        utils.absString.alpha("object")
      else utils.absString.Bot
    val s7 =
      if (value.locset.nonEmpty && (utils.absBool.True <= isCallableLocSet))
        utils.absString.alpha("function")
      else utils.absString.Bot

    s1 + s2 + s3 + s4 + s5 + s6 + s7
  }
}