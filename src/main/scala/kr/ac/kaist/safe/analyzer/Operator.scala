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
    oldValue.gamma match {
      case ConSingleCon(b) => Value(utils.PValueBot.copyWith(utils.absBool.alpha(b)))
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
    (primLPV.strval.gamma, primRPV.strval.gamma) match {
      case (ConSetBot(), ConSetBot()) =>
        val (lAbsNum, rAbsNum) = (helper.toNumber(primLPV), helper.toNumber(primRPV))
        val resAbsNum = lAbsNum add rAbsNum
        Value(utils.PValueBot.copyWith(resAbsNum))
      case (_, ConSetBot()) =>
        val (undefPV, nullPV, boolPV, numPV, strPV) = PValue2Tpl(primRPV)
        val res1 = primLPV.strval.concat(helper.toString(undefPV))
        val res2 = primLPV.strval.concat(helper.toString(nullPV))
        val res3 = primLPV.strval.concat(helper.toString(boolPV))
        val res4 = primLPV.strval.concat(helper.toString(numPV))
        val res5 = primLPV.strval.concat(helper.toString(strPV))
        val resVal = Value(utils.PValueBot.copyWith(res1 + res2 + res3 + res4 + res5))
        resVal + bopPlus(Value(primLPV.copyWith(utils.absString.Bot)), Value(primRPV))
      case (ConSetBot(), _) =>
        val (undefPV, nullPV, boolPV, numPV, strPV) = PValue2Tpl(primLPV)
        val res1 = helper.toString(undefPV).concat(primRPV.strval)
        val res2 = helper.toString(nullPV).concat(primRPV.strval)
        val res3 = helper.toString(boolPV).concat(primRPV.strval)
        val res4 = helper.toString(numPV).concat(primRPV.strval)
        val res5 = helper.toString(strPV).concat(primRPV.strval)
        val resVal = Value(utils.PValueBot.copyWith(res1 + res2 + res3 + res4 + res5))
        resVal + bopPlus(Value(primLPV), Value(primRPV.copyWith(utils.absString.Bot)))
      case (_, _) =>
        val (undefLPV, nullLPV, boolLPV, numLPV, strLPV) = PValue2Tpl(primLPV)
        val resR1 = helper.toString(undefLPV).concat(primRPV.strval)
        val resR2 = helper.toString(nullLPV).concat(primRPV.strval)
        val resR3 = helper.toString(boolLPV).concat(primRPV.strval)
        val resR4 = helper.toString(numLPV).concat(primRPV.strval)
        val resR5 = helper.toString(strLPV).concat(primRPV.strval)

        val (undefRPV, nullRPV, boolRPV, numRPV, strRPV) = PValue2Tpl(primRPV)
        val resL1 = primLPV.strval.concat(helper.toString(undefRPV))
        val resL2 = primLPV.strval.concat(helper.toString(nullRPV))
        val resL3 = primLPV.strval.concat(helper.toString(boolRPV))
        val resL4 = primLPV.strval.concat(helper.toString(numRPV))
        val resL5 = primLPV.strval.concat(helper.toString(strRPV))

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
    val b2 = (leftPV.nullval.gamma, rightPV.undefval.gamma) match {
      case (ConSimpleTop, ConSimpleTop) => utils.absBool.True
      case _ => utils.absBool.Bot
    }
    val b3 = (leftPV.undefval.gamma, rightPV.nullval.gamma) match {
      case (ConSimpleTop, ConSimpleTop) => utils.absBool.True
      case _ => utils.absBool.Bot
    }
    val b4 = (leftPV.numval.gammaSimple, rightPV.strval.gammaSimple) match {
      case (ConSimpleBot, _) | (_, ConSimpleBot) => utils.absBool.Bot
      case _ =>
        val rightNumVal = helper.toNumber(utils.PValueBot.copyWith(rightPV.strval))
        leftPV.numval === (rightNumVal, utils.absBool)
    }
    val b5 = (leftPV.strval.gammaSimple, rightPV.numval.gammaSimple) match {
      case (ConSimpleBot, _) | (_, ConSimpleBot) => utils.absBool.Bot
      case _ =>
        val leftNumVal = helper.toNumber(utils.PValueBot.copyWith(leftPV.strval))
        leftNumVal === (rightPV.numval, utils.absBool)
    }
    val b6 = leftPV.boolval.gammaSimple match {
      case ConSimpleBot =>
        val leftNumVal = helper.toNumber(utils.PValueBot.copyWith(leftPV.boolval))
        val b61 = rightPV.numval.fold(utils.absBool.Bot)(leftNumVal === (_, utils.absBool))
        val b62 = rightPV.strval.fold(utils.absBool.Bot)(rightStrVal => {
          val rightNumVal = helper.toNumber(utils.PValueBot.copyWith(rightStrVal))
          leftNumVal === (rightNumVal, utils.absBool)
        })
        val b63 = right.locset.size match {
          case 0 => utils.absBool.Bot
          case _ =>
            val rightNumVal = objToPrimitive(right.locset, "Number").numval
            leftNumVal === (rightNumVal, utils.absBool)
        }
        val b64 = rightPV.undefval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        val b65 = rightPV.nullval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        b61 + b62 + b63 + b64 + b65
      case ConSimpleTop => utils.absBool.Bot
    }

    val b7 = rightPV.boolval.gammaSimple match {
      case ConSimpleTop =>
        val rightNumVal = helper.toNumber(utils.PValueBot.copyWith(rightPV.boolval))
        val b71 = leftPV.numval.fold(utils.absBool.Bot)(_ === (rightNumVal, utils.absBool))
        val b72 = leftPV.strval.fold(utils.absBool.Bot)(leftStrVal => {
          val leftNumVal = helper.toNumber(utils.PValueBot.copyWith(leftStrVal))
          leftNumVal === (rightNumVal, utils.absBool)
        })
        val b73 = left.locset.size match {
          case 0 => utils.absBool.Bot
          case _ =>
            val leftNumVal = objToPrimitive(left.locset, "Number").numval
            leftNumVal === (rightNumVal, utils.absBool)
        }
        val b74 = leftPV.undefval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        val b75 = leftPV.undefval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        b71 + b72 + b73 + b74 + b75
      case ConSimpleBot => utils.absBool.Bot
    }

    val b8 = right.locset.size match {
      case 0 => utils.absBool.Bot
      case _ =>
        val b81 = leftPV.numval.fold(utils.absBool.Bot)(leftNumVal => {
          val rightNumVal = objToPrimitive(right.locset, "Number").numval
          leftNumVal === (rightNumVal, utils.absBool)
        })
        val b82 = leftPV.strval.fold(utils.absBool.Bot)(leftStrVal => {
          val rightStrVal = objToPrimitive(right.locset, "String").strval
          leftStrVal === (rightStrVal, utils.absBool)
        })
        b81 + b82
    }

    val b9 = left.locset.size match {
      case 0 => utils.absBool.Bot
      case _ =>
        val b91 = rightPV.numval.fold(utils.absBool.Bot)(rightNumVal => {
          val leftNumVal = objToPrimitive(left.locset, "Number").numval
          leftNumVal === (rightNumVal, utils.absBool)
        })
        val b92 = rightPV.strval.fold(utils.absBool.Bot)(rightStrVal => {
          val leftStrVal = objToPrimitive(left.locset, "String").strval
          leftStrVal === (rightStrVal, utils.absBool)
        })
        b91 + b92
    }

    def testUndefNull(pv: PValue, locset: Set[Loc]): Boolean = (pv.undefval.gamma, pv.nullval.gamma) match {
      case (ConSimpleBot, ConSimpleBot) => false
      case _ => (pv.numval.gammaSimple, pv.strval.gammaSimple, locset.size) match {
        case (ConSimpleBot, ConSimpleBot, 0) => false
        case _ => true
      }
    }

    val b10 = (testUndefNull(leftPV, left.locset), testUndefNull(rightPV, right.locset)) match {
      case (false, false) => utils.absBool.Bot
      case _ => utils.absBool.False
    }

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
    val resAbsBool = bopEq(left, right).pvalue.boolval.gamma match {
      case ConSingleCon(true) => utils.absBool.False
      case ConSingleCon(false) => utils.absBool.True
      case ConSingleTop() => utils.absBool.Top
      case ConSingleBot() => utils.absBool.Bot
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
    val resAbsBool = bopSEq(left, right).pvalue.boolval.gamma match {
      case ConSingleCon(true) => utils.absBool.False
      case ConSingleCon(false) => utils.absBool.True
      case ConSingleTop() => utils.absBool.Top
      case ConSingleBot() => utils.absBool.Bot
    }
    Value(utils.PValueBot.copyWith(resAbsBool))
  }

  private def bopCompareHelp(
    leftPV: PValue,
    rightPV: PValue,
    cmpAbsNum: (AbsNumber, AbsNumber) => AbsBool,
    cmpAbsStr: (AbsString, AbsString) => AbsBool
  ): Value = {
    (leftPV.strval.gammaSimple, rightPV.strval.gammaSimple) match {
      case (ConSimpleBot, _) | (_, ConSimpleBot) =>
        val leftAbsNum = helper.toNumber(leftPV)
        val rightAbsNum = helper.toNumber(rightPV)
        Value(utils.PValueBot.copyWith(cmpAbsNum(leftAbsNum, rightAbsNum)))
      case _ =>
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
