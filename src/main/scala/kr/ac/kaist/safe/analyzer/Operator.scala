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
import kr.ac.kaist.safe.cfg_builder.AddressManager

case class Operator(helper: Helper) { //TODO
  private val utils: Utils = helper.utils
  private val addrManager: AddressManager = helper.addrManager
  private val absNumber: AbsNumberUtil = utils.absNumber

  def toUInt32(v: Value): AbsNumber = absNumber.Bot
  def toInt32(v: Value): AbsNumber = absNumber.Bot

  /* unary operator */
  /* void */
  def uVoid(value: Value): Value = Value(utils.PValueBot.copyWith(utils.absUndef.Top))
  /* + */
  def uopPlus(value: Value): Value = {
    val absNum = helper.toNumber(value.pvalue) + helper.toNumber(helper.objToPrimitive(value.locset, "Number"))
    Value(utils.PValueBot.copyWith(absNum))
  }

  /* - */
  def uopMinus(value: Value): Value = {
    val oldAbsNum = helper.toNumber(value.pvalue) + helper.toNumber(helper.objToPrimitive(value.locset, "Number"))
    val newAbsNum = oldAbsNum.negate
    Value(utils.PValueBot.copyWith(newAbsNum))
  }

  /* - */
  def uopMinusBetter(h: Heap, value: Value): Value = {
    val oldAbsNum = helper.toNumber(value.pvalue) + helper.toNumber(helper.objToPrimitiveBetter(h, value.locset, "Number"))
    val newAbsNum = oldAbsNum.negate
    Value(utils.PValueBot.copyWith(newAbsNum))
  }

  /* ~ */
  def uopBitNeg(value: Value): Value = {
    val oldAbsNum = toInt32(value)
    val newAbsNum = oldAbsNum.bitNegate
    Value(utils.PValueBot.copyWith(newAbsNum))
  }

  /* ! */
  def uopNeg(value: Value): Value = {
    val oldValue = helper.toBoolean(value)
    oldValue.getPair match {
      case (AbsSingle, Some(true)) => Value(utils.PValueBot.copyWith(utils.absBool.False))
      case (AbsSingle, Some(false)) => Value(utils.PValueBot.copyWith(utils.absBool.True))
      case _ => Value(utils.PValueBot.copyWith(oldValue))
    }
  }

  /* binary operator */
  /* | */
  def bopBitOr(left: Value, right: Value): Value = {
    val lAbsNum = toInt32(left)
    val rAbsNum = toInt32(right)
    val resAbsNum = lAbsNum.bitOr(rAbsNum)
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* & */
  def bopBitAnd(left: Value, right: Value): Value = {
    val lAbsNum = toInt32(left)
    val rAbsNum = toInt32(right)
    val resAbsNum = lAbsNum.bitAnd(rAbsNum)
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* ^ */
  def bopBitXor(left: Value, right: Value): Value = {
    val lAbsNum = toInt32(left)
    val rAbsNum = toInt32(right)
    val resAbsNum = lAbsNum.bitXor(rAbsNum)
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* << */
  def bopLShift(left: Value, right: Value): Value = {
    val lAbsNum = toInt32(left)
    val rAbsNum = toUInt32(right)
    val resAbsNum = lAbsNum.bitLShift(rAbsNum)
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* >> */
  def bopRShift(left: Value, right: Value): Value = {
    val lAbsNum = toInt32(left)
    val rAbsNum = toUInt32(right)
    val resAbsNum = lAbsNum.bitRShift(rAbsNum)
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* >>> */
  def bopURShift(left: Value, right: Value): Value = {
    val lAbsNum = toInt32(left)
    val rAbsNum = toUInt32(right)
    val resAbsNum = lAbsNum.bitURShift(rAbsNum)
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* + */
  def bopPlus(left: Value, right: Value): Value = {
    def PValue2Tpl(pv: PValue): (PValue, PValue, PValue, PValue, PValue) = {
      val bot = utils.PValueBot
      (
        bot.copyWith(pv.undefval),
        bot.copyWith(pv.nullval),
        bot.copyWith(pv.boolval),
        bot.copyWith(pv.numval),
        bot.copyWith(pv.strval)
      )
    }

    val primLPV = helper.toPrimitive(left)
    val primRPV = helper.toPrimitive(right)
    (primLPV.strval, primRPV.strval) match {
      case (lAbsStr, rAbsStr) if lAbsStr.isBottom & rAbsStr.isBottom =>
        val (lAbsNum, rAbsNum) = (helper.toNumber(primLPV), helper.toNumber(primRPV))
        val resAbsNum = lAbsNum add rAbsNum
        Value(utils.PValueBot.copyWith(resAbsNum))
      case (lAbsStr, rAbsStr) if rAbsStr.isBottom =>
        val (undefPV, nullPV, boolPV, numPV, strPV) = PValue2Tpl(primRPV)
        val res1 = lAbsStr.concat(helper.toString(undefPV))
        val res2 = lAbsStr.concat(helper.toString(nullPV))
        val res3 = lAbsStr.concat(helper.toString(boolPV))
        val res4 = lAbsStr.concat(helper.toString(numPV))
        val res5 = lAbsStr.concat(helper.toString(strPV))
        val resVal = Value(utils.PValueBot.copyWith(res1 + res2 + res3 + res4 + res5))
        resVal + bopPlus(Value(primLPV.copyWith(utils.absString.Bot)), Value(primRPV))
      case (lAbsStr, rAbsStr) if lAbsStr.isBottom =>
        val (undefPV, nullPV, boolPV, numPV, strPV) = PValue2Tpl(primLPV)
        val res1 = helper.toString(undefPV).concat(rAbsStr)
        val res2 = helper.toString(nullPV).concat(rAbsStr)
        val res3 = helper.toString(boolPV).concat(rAbsStr)
        val res4 = helper.toString(numPV).concat(rAbsStr)
        val res5 = helper.toString(strPV).concat(rAbsStr)
        val resVal = Value(utils.PValueBot.copyWith(res1 + res2 + res3 + res4 + res5))
        resVal + bopPlus(Value(primLPV), Value(primRPV.copyWith(utils.absString.Bot)))
      case (lAbsStr, rAbsStr) =>
        val (undefLPV, nullLPV, boolLPV, numLPV, strLPV) = PValue2Tpl(primLPV)
        val resR1 = helper.toString(undefLPV).concat(rAbsStr)
        val resR2 = helper.toString(nullLPV).concat(rAbsStr)
        val resR3 = helper.toString(boolLPV).concat(rAbsStr)
        val resR4 = helper.toString(numLPV).concat(rAbsStr)
        val resR5 = helper.toString(strLPV).concat(rAbsStr)

        val (undefRPV, nullRPV, boolRPV, numRPV, strRPV) = PValue2Tpl(primRPV)
        val resL1 = lAbsStr.concat(helper.toString(undefRPV))
        val resL2 = lAbsStr.concat(helper.toString(nullRPV))
        val resL3 = lAbsStr.concat(helper.toString(boolRPV))
        val resL4 = lAbsStr.concat(helper.toString(numRPV))
        val resL5 = lAbsStr.concat(helper.toString(strRPV))

        val resAbsStr = resR1 + resR2 + resR3 + resR4 + resR5 + resL1 + resL2 + resL3 + resL4 + resL5
        val resVal = Value(utils.PValueBot.copyWith(resAbsStr))

        resVal + bopPlus(Value(primLPV.copyWith(utils.absString.Bot)), Value(primRPV.copyWith(utils.absString.Bot)))
    }
  }

  /* - */
  def bopMinus(left: Value, right: Value): Value = {
    val lAbsNum = helper.toNumber(left.pvalue) + helper.toNumber(helper.objToPrimitive(left.locset, "Number"))
    val rAbsNum = helper.toNumber(right.pvalue) + helper.toNumber(helper.objToPrimitive(right.locset, "Number"))
    val resAbsNum = lAbsNum sub rAbsNum
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* * */
  def bopMul(left: Value, right: Value): Value = {
    val lAbsNum = helper.toNumber(left.pvalue) + helper.toNumber(helper.objToPrimitive(left.locset, "Number"))
    val rAbsNum = helper.toNumber(right.pvalue) + helper.toNumber(helper.objToPrimitive(right.locset, "Number"))
    val resAbsNum = lAbsNum mul rAbsNum
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* / */
  def bopDiv(left: Value, right: Value): Value = {
    val lAbsNum = helper.toNumber(left.pvalue) + helper.toNumber(helper.objToPrimitive(left.locset, "Number"))
    val rAbsNum = helper.toNumber(right.pvalue) + helper.toNumber(helper.objToPrimitive(right.locset, "Number"))
    val resAbsNum = lAbsNum div rAbsNum
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* % */
  def bopMod(left: Value, right: Value): Value = {
    val lAbsNum = helper.toNumber(left.pvalue) + helper.toNumber(helper.objToPrimitive(left.locset, "Number"))
    val rAbsNum = helper.toNumber(right.pvalue) + helper.toNumber(helper.objToPrimitive(right.locset, "Number"))
    val resAbsNum = lAbsNum mod rAbsNum
    Value(utils.PValueBot.copyWith(resAbsNum))
  }

  /* == */
  private def bopEqHelp(left: Value, right: Value, objToPrimitive: (Set[Loc], String) => PValue): Value = {
    val leftPV = left.pvalue
    val rightPV = right.pvalue
    val locsetTest =
      if (!left.locset.isEmpty && !right.locset.isEmpty) {
        val intersect = left.locset.intersect(right.locset)
        if (intersect.isEmpty) utils.absBool.False
        else if (left.locset.size == 1 && right.locset.size == 1 && addrManager.isRecentLoc(intersect.head)) utils.absBool.True
        else utils.absBool.Top
      } else utils.absBool.Bot
    val b1 = (leftPV.undefval === (rightPV.undefval, utils.absBool)) +
      (leftPV.nullval === (rightPV.nullval, utils.absBool)) +
      (leftPV.numval === (rightPV.numval, utils.absBool)) +
      (leftPV.strval === (rightPV.strval, utils.absBool)) +
      (leftPV.boolval === (rightPV.boolval, utils.absBool)) +
      locsetTest
    val b2 =
      if (leftPV.nullval.isTop & rightPV.undefval.isTop) utils.absBool.True
      else utils.absBool.Bot
    val b3 =
      if (leftPV.undefval.isTop & rightPV.nullval.isTop) utils.absBool.True
      else utils.absBool.Bot
    val b4 =
      if (!leftPV.numval.isBottom & !rightPV.strval.isBottom) {
        val rightNumVal = helper.toNumber(utils.PValueBot.copyWith(rightPV.strval))
        leftPV.numval === (rightNumVal, utils.absBool)
      } else utils.absBool.Bot
    val b5 =
      if (!leftPV.strval.isBottom & !rightPV.numval.isBottom) {
        val leftNumVal = helper.toNumber(utils.PValueBot.copyWith(leftPV.strval))
        leftNumVal === (rightPV.numval, utils.absBool)
      } else utils.absBool.Bot

    val b6 =
      if (!leftPV.boolval.isBottom) {
        val leftNumVal = helper.toNumber(utils.PValueBot.copyWith(leftPV.boolval))
        val b61 =
          if (!rightPV.numval.isBottom) leftNumVal === (rightPV.numval, utils.absBool)
          else utils.absBool.Bot
        val b62 =
          if (!rightPV.strval.isBottom) {
            val rightNumVal = helper.toNumber(utils.PValueBot.copyWith(rightPV.strval))
            leftNumVal === (rightNumVal, utils.absBool)
          } else utils.absBool.Bot
        val b63 =
          if (!right.locset.isEmpty) {
            val rightNumVal = objToPrimitive(right.locset, "Number").numval
            leftNumVal === (rightNumVal, utils.absBool)
          } else utils.absBool.Bot
        val b64 =
          if (!rightPV.undefval.isBottom || !rightPV.nullval.isBottom) utils.absBool.False
          else utils.absBool.Bot
        b61 + b62 + b63 + b64
      } else utils.absBool.Bot

    val b7 =
      if (!rightPV.boolval.isBottom) {
        val rightNumVal = helper.toNumber(utils.PValueBot.copyWith(rightPV.boolval))
        val b71 =
          if (!leftPV.numval.isBottom) leftPV.numval === (rightNumVal, utils.absBool)
          else utils.absBool.Bot
        val b72 =
          if (!leftPV.strval.isBottom) {
            val leftNumVal = helper.toNumber(utils.PValueBot.copyWith(leftPV.strval))
            leftNumVal === (rightNumVal, utils.absBool)
          } else utils.absBool.Bot
        val b73 =
          if (!left.locset.isEmpty) {
            val leftNumVal = objToPrimitive(left.locset, "Number").numval
            leftNumVal === (rightNumVal, utils.absBool)
          } else utils.absBool.Bot
        val b74 =
          if (!leftPV.undefval.isBottom || !leftPV.nullval.isBottom) utils.absBool.False
          else utils.absBool.Bot
        b71 + b72 + b73 + b74
      } else utils.absBool.Bot

    val b8 =
      if (!right.locset.isEmpty) {
        val b81 =
          if (!leftPV.numval.isBottom) {
            val rightNumVal = objToPrimitive(right.locset, "Number").numval
            leftPV.numval === (rightNumVal, utils.absBool)
          } else utils.absBool.Bot
        val b82 =
          if (!leftPV.strval.isBottom) {
            val rightNumVal = objToPrimitive(right.locset, "String").strval
            leftPV.strval === (rightNumVal, utils.absBool)
          } else utils.absBool.Bot
        b81 + b82
      } else utils.absBool.Bot

    val b9 =
      if (!left.locset.isEmpty) {
        val b91 =
          if (!rightPV.numval.isBottom) {
            val leftNumVal = objToPrimitive(left.locset, "Number").numval
            leftNumVal === (rightPV.numval, utils.absBool)
          } else utils.absBool.Bot
        val b92 =
          if (!rightPV.strval.isBottom) {
            val leftNumVal = objToPrimitive(left.locset, "String").strval
            leftNumVal === (rightPV.strval, utils.absBool)
          } else utils.absBool.Bot
        b91 + b92
      } else utils.absBool.Bot

    val testUndefNullLeft = (!leftPV.undefval.isBottom || !leftPV.nullval.isBottom) &&
      (!rightPV.numval.isBottom || !rightPV.strval.isBottom || !right.locset.isEmpty)
    val testUndefNullRight = (!rightPV.undefval.isBottom || !rightPV.nullval.isBottom) &&
      (!leftPV.numval.isBottom || !leftPV.strval.isBottom || !left.locset.isEmpty)
    val b10 =
      if (testUndefNullLeft || testUndefNullRight) utils.absBool.False
      else utils.absBool.Bot

    Value(utils.PValueBot.copyWith(b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 + b9 + b10))
  }

  def bopEqBetter(h: Heap, left: Value, right: Value): Value = {
    bopEqHelp(left, right, (locset, hint) => helper.objToPrimitiveBetter(h, locset, hint))
  }

  def bopEq(left: Value, right: Value): Value = {
    bopEqHelp(left, right, (locset, hint) => helper.objToPrimitive(locset, hint))
  }

  /* != */
  def bopNeq(left: Value, right: Value): Value = {
    val resAbsBool = bopEq(left, right).pvalue.boolval.getPair match {
      case (AbsSingle, Some(true)) => utils.absBool.False
      case (AbsSingle, Some(false)) => utils.absBool.True
      case (AbsTop, _) => utils.absBool.Top
      case (AbsBot, _) => utils.absBool.Bot
      case _ => utils.absBool.Bot //TODO: Internal Error No multi
    }
    Value(utils.PValueBot.copyWith(resAbsBool))
  }

  /* === */
  def bopSEq(left: Value, right: Value): Value = {
    val isMultiType =
      if ((left + right).typeCount > 1) utils.absBool.False
      else utils.absBool.Bot
    val isLocsetSame =
      if (!left.locset.isEmpty && !right.locset.isEmpty) {
        val intersect = left.locset.intersect(right.locset)
        if (intersect.isEmpty) utils.absBool.False
        else if (left.locset.size == 1 && right.locset.size == 1 && addrManager.isRecentLoc(intersect.head)) utils.absBool.True
        else utils.absBool.Top
      } else utils.absBool.Bot
    val isSame =
      (left.pvalue.undefval === (right.pvalue.undefval, utils.absBool)) +
        (left.pvalue.nullval === (right.pvalue.nullval, utils.absBool)) +
        (left.pvalue.numval === (right.pvalue.numval, utils.absBool)) +
        (left.pvalue.strval === (right.pvalue.strval, utils.absBool)) +
        (left.pvalue.boolval === (right.pvalue.boolval, utils.absBool)) +
        isLocsetSame
    Value(utils.PValueBot.copyWith(isMultiType + isSame))
  }

  /* !== */
  def bopSNeq(left: Value, right: Value): Value = {
    val resAbsBool = bopSEq(left, right).pvalue.boolval.getPair match {
      case (AbsSingle, Some(true)) => utils.absBool.False
      case (AbsSingle, Some(false)) => utils.absBool.True
      case (AbsTop, _) => utils.absBool.Top
      case (AbsBot, _) => utils.absBool.Bot
      case _ => utils.absBool.Bot //TODO: Internal Error No multi
    }
    Value(utils.PValueBot.copyWith(resAbsBool))
  }

  private def bopCompareHelp(leftPV: PValue, rightPV: PValue,
    cmpAbsNum: (AbsNumber, AbsNumber) => AbsBool,
    cmpAbsStr: (AbsString, AbsString) => AbsBool): Value = {
    if (leftPV.strval.isBottom | rightPV.strval.isBottom) {
      val leftAbsNum = helper.toNumber(leftPV)
      val rightAbsNum = helper.toNumber(rightPV)
      Value(utils.PValueBot.copyWith(cmpAbsNum(leftAbsNum, rightAbsNum)))
    } else {
      val leftPV2 = leftPV.copyWith(utils.absString.Bot)
      val rightPV2 = rightPV.copyWith(utils.absString.Bot)
      val resAbsBool = cmpAbsStr(leftPV.strval, rightPV.strval)
      Value(utils.PValueBot.copyWith(resAbsBool)) +
        bopCompareHelp(leftPV, rightPV2, cmpAbsNum, cmpAbsStr) +
        bopCompareHelp(leftPV2, rightPV, cmpAbsNum, cmpAbsStr)
    }
  }

  /* < */
  def bopLess(left: Value, right: Value): Value = {
    val leftPV = helper.toPrimitive(left)
    val rightPV = helper.toPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => leftAbsNum < (rightAbsNum, utils.absBool),
      (leftAbsStr, rightAbsStr) => leftAbsStr < (rightAbsStr, utils.absBool))
  }

  /* > */
  def bopGreater(left: Value, right: Value): Value = {
    val leftPV = helper.toPrimitive(left)
    val rightPV = helper.toPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => rightAbsNum < (leftAbsNum, utils.absBool),
      (leftAbsStr, rightAbsStr) => rightAbsStr < (leftAbsStr, utils.absBool))
  }

  /* <= */
  def bopLessEq(left: Value, right: Value): Value = {
    val leftPV = helper.toPrimitive(left)
    val rightPV = helper.toPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        if (leftAbsNum.isNaN | rightAbsNum.isNaN) utils.absBool.False
        else (rightAbsNum < (leftAbsNum, utils.absBool)).negate
      },
      (leftAbsStr, rightAbsStr) => (rightAbsStr < (leftAbsStr, utils.absBool)).negate)
  }

  /* >= */
  def bopGreaterEq(left: Value, right: Value): Value = {
    val leftPV = helper.toPrimitive(left)
    val rightPV = helper.toPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        if (leftAbsNum.isNaN | rightAbsNum.isNaN) utils.absBool.False
        else (leftAbsNum < (rightAbsNum, utils.absBool)).negate
      },
      (leftAbsStr, rightAbsStr) => (leftAbsStr < (rightAbsStr, utils.absBool)).negate)
  }
}
