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

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.util.Address

////////////////////////////////////////////////////////////////
// Collection of Semantics helper functions
////////////////////////////////////////////////////////////////
object Helper {
  val typeHelper = TypeConversionHelper
  private val afalse = AbsBool.alpha(false)
  private val atrue = AbsBool.alpha(true)

  def arrayLenghtStore(heap: Heap, idxAbsStr: AbsString, storeV: Value, l: Loc): (Heap, Set[Exception]) = {
    if (AbsString.alpha("length") <= idxAbsStr) {
      val nOldLen = heap.getOrElse(l, AbsObjectUtil.Bot)
        .getOrElse("length")(AbsNumber.Bot) { _.objval.value.pvalue.numval }
      val nNewLen = typeHelper.ToUInt32(storeV)
      val nValue = typeHelper.ToNumber(storeV)
      val bCanPut = heap.canPut(l, AbsString.alpha("length"))

      val arrLengthHeap2 =
        if ((atrue <= (nOldLen < nNewLen)
          || atrue <= (nOldLen === nNewLen))
          && (atrue <= bCanPut))
          heap.propStore(l, AbsString.alpha("length"), storeV)
        else
          Heap.Bot

      val arrLengthHeap3 =
        if (afalse <= bCanPut) heap
        else Heap.Bot

      val arrLengthHeap4 =
        if ((atrue <= (nNewLen < nOldLen)) && (atrue <= bCanPut)) {
          val hi = heap.propStore(l, AbsString.alpha("length"), storeV)
          (nNewLen.gammaSingle, nOldLen.gammaSingle) match {
            case (ConSingleCon(Num(n1)), ConSingleCon(Num(n2))) =>
              (n1.toInt until n2.toInt).foldLeft(hi)((hj, i) => {
                val (tmpHeap, _) = hj.delete(l, AbsString.alpha(i.toString))
                tmpHeap
              })
            case (ConSingleBot(), _) | (_, ConSingleBot()) => Heap.Bot
            case _ =>
              val (tmpHeap, _) = hi.delete(l, AbsString.Number)
              tmpHeap
          }
        } else {
          Heap.Bot
        }

      val arrLengthHeap1 =
        if (atrue <= (nValue === nNewLen))
          arrLengthHeap2 + arrLengthHeap3 + arrLengthHeap4
        else
          Heap.Bot

      val lenExcSet1 =
        if (afalse <= (nValue === nNewLen)) HashSet[Exception](RangeError)
        else ExceptionSetEmpty
      (arrLengthHeap1, lenExcSet1)
    } else {
      (Heap.Bot, ExceptionSetEmpty)
    }
  }

  def arrayIdxStore(heap: Heap, idxAbsStr: AbsString, storeV: Value, l: Loc): Heap = {
    if (atrue <= idxAbsStr.isArrayIndex) {
      val nOldLen = heap.getOrElse(l, AbsObjectUtil.Bot)
        .getOrElse("length")(AbsNumber.Bot) { _.objval.value.pvalue.numval }
      val idxPV = AbsPValue(idxAbsStr)
      val numPV = AbsPValue(typeHelper.ToNumber(idxPV))
      val nIndex = typeHelper.ToUInt32(ValueUtil(numPV))
      val bGtEq = atrue <= (nOldLen < nIndex) ||
        atrue <= (nOldLen === nIndex)
      val bCanPutLen = heap.canPut(l, AbsString.alpha("length"))
      // 4.b
      val arrIndexHeap1 =
        if (bGtEq && afalse <= bCanPutLen) heap
        else Heap.Bot
      // 4.c
      val arrIndexHeap2 =
        if (atrue <= (nIndex < nOldLen))
          heap.propStore(l, idxAbsStr, storeV)
        else Heap.Bot
      // 4.e
      val arrIndexHeap3 =
        if (bGtEq && atrue <= bCanPutLen) {
          val hi = heap.propStore(l, idxAbsStr, storeV)
          val idxVal = ValueUtil(nIndex)
          val vNewIndex = bopPlus(idxVal, ValueUtil.alpha(1))
          hi.propStore(l, AbsString.alpha("length"), vNewIndex)
        } else Heap.Bot
      arrIndexHeap1 + arrIndexHeap2 + arrIndexHeap3
    } else
      Heap.Bot
  }

  def storeHelp(objLocSet: AbsLoc, idxAbsStr: AbsString, storeV: Value, heap: Heap): (Heap, Set[Exception]) = {
    // non-array objects
    val locSetNArr = objLocSet.filter(l =>
      (afalse <= heap.isArray(l)) && atrue <= heap.canPut(l, idxAbsStr))
    // array objects
    val locSetArr = objLocSet.filter(l =>
      (atrue <= heap.isArray(l)) && atrue <= heap.canPut(l, idxAbsStr))

    // can not store
    val cantPutHeap =
      if (objLocSet.exists((l) => afalse <= heap.canPut(l, idxAbsStr))) heap
      else Heap.Bot

    // store for non-array object
    val nArrHeap = locSetNArr.foldLeft(Heap.Bot)((iHeap, l) => {
      iHeap + heap.propStore(l, idxAbsStr, storeV)
    })

    // 15.4.5.1 [[DefineOwnProperty]] of Array
    val (arrHeap, arrExcSet) = locSetArr.foldLeft((Heap.Bot, ExceptionSetEmpty))((res2, l) => {
      // 3. s is length
      val (lengthHeap, lengthExcSet) = arrayLenghtStore(heap, idxAbsStr, storeV, l)
      // 4. s is array index
      val arrIndexHeap = arrayIdxStore(heap, idxAbsStr, storeV, l)

      // 5. other
      val otherHeap =
        if (idxAbsStr != AbsString.alpha("length") && afalse <= idxAbsStr.isArrayIndex)
          heap.propStore(l, idxAbsStr, storeV)
        else
          Heap.Bot
      val (tmpHeap2, tmpExcSet2) = res2
      (tmpHeap2 + lengthHeap + arrIndexHeap + otherHeap, tmpExcSet2 ++ lengthExcSet)
    })

    (cantPutHeap + nArrHeap + arrHeap, arrExcSet)
  }

  def inherit(h: Heap, loc1: Loc, loc2: Loc): Value = {
    var visited = AbsLoc.Bot
    val locVal2 = ValueUtil(loc2)
    val boolBotVal = ValueUtil(AbsPValue.Bot)
    val boolTrueVal = ValueUtil.alpha(true)
    val boolFalseVal = ValueUtil.alpha(false)

    def iter(l1: Loc): Value = {
      if (visited.contains(l1)) ValueUtil.Bot
      else {
        visited += l1
        val locVal1 = ValueUtil(l1)
        val eqVal = bopSEq(locVal1, locVal2)
        val v1 =
          if (atrue <= eqVal.pvalue.boolval) boolTrueVal
          else boolBotVal
        val v2 =
          if (afalse <= eqVal.pvalue.boolval) {
            val protoVal = h.getOrElse(l1, AbsObjectUtil.Bot).getOrElse(IPrototype)(ValueUtil.Bot) { _.value }
            val v1 = protoVal.pvalue.nullval.fold(boolBotVal) { _ => boolFalseVal }
            v1 + protoVal.locset.foldLeft(ValueUtil.Bot)((tmpVal, protoLoc) => tmpVal + iter(protoLoc))
          } else boolBotVal
        v1 + v2
      }
    }

    iter(loc1)
  }

  /* unary operator */
  /* void */
  def uVoid(value: Value): Value = ValueUtil.alpha(Undef)
  /* + */
  def uopPlus(value: Value): Value =
    ValueUtil(typeHelper.ToNumber(value))

  /* - */
  def uopMinus(value: Value): Value =
    ValueUtil(typeHelper.ToNumber(value).negate)

  /* - */
  def uopMinusBetter(h: Heap, value: Value): Value =
    ValueUtil(typeHelper.ToNumber(value, h).negate)

  /* ~ */
  def uopBitNeg(value: Value): Value =
    ValueUtil(typeHelper.ToInt32(value).bitNegate)

  /* ! */
  def uopNeg(value: Value): Value =
    ValueUtil(typeHelper.ToBoolean(value).negate)

  /* binary operator */
  /* | */
  def bopBitOr(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitOr(rAbsNum)
    ValueUtil(resAbsNum)
  }

  /* & */
  def bopBitAnd(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitAnd(rAbsNum)
    ValueUtil(resAbsNum)
  }

  /* ^ */
  def bopBitXor(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitXor(rAbsNum)
    ValueUtil(resAbsNum)
  }

  /* << */
  def bopLShift(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitLShift(rAbsNum)
    ValueUtil(resAbsNum)
  }

  /* >> */
  def bopRShift(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitRShift(rAbsNum)
    ValueUtil(resAbsNum)
  }

  /* >>> */
  def bopURShift(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitURShift(rAbsNum)
    ValueUtil(resAbsNum)
  }

  /* + */
  def bopPlus(left: Value, right: Value): Value = {
    def PValue2Tpl(pv: AbsPValue): (AbsPValue, AbsPValue, AbsPValue, AbsPValue, AbsPValue) = {
      (
        AbsPValue(pv.undefval),
        AbsPValue(pv.nullval),
        AbsPValue(pv.boolval),
        AbsPValue(pv.numval),
        AbsPValue(pv.strval)
      )
    }

    val primLPV = typeHelper.ToPrimitive(left)
    val primRPV = typeHelper.ToPrimitive(right)
    (primLPV.strval.gamma, primRPV.strval.gamma) match {
      case (ConSetBot(), ConSetBot()) =>
        val (lAbsNum, rAbsNum) = (typeHelper.ToNumber(primLPV), typeHelper.ToNumber(primRPV))
        val resAbsNum = lAbsNum add rAbsNum
        ValueUtil(resAbsNum)
      case (_, ConSetBot()) =>
        val resVal = ValueUtil(primLPV.strval.concat(TypeConversionHelper.ToString(primRPV)))
        resVal + bopPlus(ValueUtil(primLPV.copyWith(strval = AbsString.Bot)), ValueUtil(primRPV))
      case (ConSetBot(), _) =>
        val resVal = ValueUtil(TypeConversionHelper.ToString(primLPV).concat(primRPV.strval))
        resVal + bopPlus(ValueUtil(primLPV), ValueUtil(primRPV.copyWith(strval = AbsString.Bot)))
      case (_, _) =>
        val lStr = TypeConversionHelper.ToString(primLPV).concat(primRPV.strval)
        val rStr = primLPV.strval.concat(TypeConversionHelper.ToString(primRPV))
        val resVal = ValueUtil(lStr + rStr)

        resVal + bopPlus(ValueUtil(primLPV.copyWith(strval = AbsString.Bot)), ValueUtil(primRPV.copyWith(strval = AbsString.Bot)))
    }
  }

  /* - */
  def bopMinus(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) sub typeHelper.ToNumber(right)
    ValueUtil(resAbsNum)
  }

  /* * */
  def bopMul(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) mul typeHelper.ToNumber(right)
    ValueUtil(resAbsNum)
  }

  /* / */
  def bopDiv(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) div typeHelper.ToNumber(right)
    ValueUtil(resAbsNum)
  }

  /* % */
  def bopMod(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) mod typeHelper.ToNumber(right)
    ValueUtil(resAbsNum)
  }

  /* == */
  private def bopEqHelp(left: Value, right: Value, objToPrimitive: (Value, String) => AbsPValue): Value = {
    val leftPV = left.pvalue
    val rightPV = right.pvalue
    val locsetTest =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset <> right.locset
        (left.locset.gammaSingle, right.locset.gammaSingle, intersect.gammaSingle) match {
          case (_, _, ConSingleBot()) => afalse
          case (ConSingleCon(_), ConSingleCon(_), ConSingleCon(loc)) if loc.recency == Recent => atrue
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot
    val b1 = (leftPV.undefval === rightPV.undefval) +
      (leftPV.nullval === rightPV.nullval) +
      (leftPV.numval === rightPV.numval) +
      (leftPV.strval === rightPV.strval) +
      (leftPV.boolval === rightPV.boolval) +
      locsetTest
    val b2 = (leftPV.nullval.gamma, rightPV.undefval.gamma) match {
      case (ConSimpleTop(), ConSimpleTop()) => atrue
      case _ => AbsBool.Bot
    }
    val b3 = (leftPV.undefval.gamma, rightPV.nullval.gamma) match {
      case (ConSimpleTop(), ConSimpleTop()) => atrue
      case _ => AbsBool.Bot
    }
    val b4 = (leftPV.numval.gammaSimple, rightPV.strval.gammaSimple) match {
      case (ConSimpleBot(), _) | (_, ConSimpleBot()) => AbsBool.Bot
      case _ =>
        val rightNumVal = rightPV.strval.toAbsNumber
        (leftPV.numval === rightNumVal)
    }
    val b5 = (leftPV.strval.gammaSimple, rightPV.numval.gammaSimple) match {
      case (ConSimpleBot(), _) | (_, ConSimpleBot()) => AbsBool.Bot
      case _ =>
        val leftNumVal = leftPV.strval.toAbsNumber
        (leftNumVal === rightPV.numval)
    }
    val b6 = leftPV.boolval.gammaSimple match {
      case ConSimpleBot() =>
        val leftNumVal = leftPV.boolval.toAbsNumber
        val b61 = rightPV.numval.fold(AbsBool.Bot)(rightNumVal => {
          (leftNumVal === rightNumVal)
        })
        val b62 = rightPV.strval.fold(AbsBool.Bot)(rightStrVal => {
          val rightNumVal = rightStrVal.toAbsNumber
          (leftNumVal === rightNumVal)
        })
        val b63 = right.locset.fold(AbsBool.Bot) {
          case _ =>
            val rightNumVal = objToPrimitive(right, "Number").numval
            (leftNumVal === rightNumVal)
        }
        val b64 = rightPV.undefval.fold(AbsBool.Bot)(_ => afalse)
        val b65 = rightPV.nullval.fold(AbsBool.Bot)(_ => afalse)
        b61 + b62 + b63 + b64 + b65
      case ConSimpleTop() => AbsBool.Bot
    }

    val b7 = rightPV.boolval.gammaSimple match {
      case ConSimpleBot() =>
        val rightNumVal = rightPV.boolval.toAbsNumber
        val b71 = leftPV.numval.fold(AbsBool.Bot)(leftNumVal => {
          (leftNumVal === rightNumVal)
        })
        val b72 = leftPV.strval.fold(AbsBool.Bot)(leftStrVal => {
          val leftNumVal = leftStrVal.toAbsNumber
          (leftNumVal === rightNumVal)
        })
        val b73 = left.locset.fold(AbsBool.Bot) {
          case _ =>
            val leftNumVal = objToPrimitive(left, "Number").numval
            (leftNumVal === rightNumVal)
        }
        val b74 = leftPV.undefval.fold(AbsBool.Bot)(_ => afalse)
        val b75 = leftPV.undefval.fold(AbsBool.Bot)(_ => afalse)
        b71 + b72 + b73 + b74 + b75
      case ConSimpleTop() => AbsBool.Bot
    }

    val b8 = right.locset.fold(AbsBool.Bot) {
      case _ =>
        val b81 = leftPV.numval.fold(AbsBool.Bot)(leftNumVal => {
          val rightNumVal = objToPrimitive(right, "Number").numval
          (leftNumVal === rightNumVal)
        })
        val b82 = leftPV.strval.fold(AbsBool.Bot)(leftStrVal => {
          val rightStrVal = objToPrimitive(right, "String").strval
          (leftStrVal === rightStrVal)
        })
        b81 + b82
    }

    val b9 = left.locset.fold(AbsBool.Bot) {
      case _ =>
        val b91 = rightPV.numval.fold(AbsBool.Bot)(rightNumVal => {
          val leftNumVal = objToPrimitive(left, "Number").numval
          (leftNumVal === rightNumVal)
        })
        val b92 = rightPV.strval.fold(AbsBool.Bot)(rightStrVal => {
          val leftStrVal = objToPrimitive(left, "String").strval
          (leftStrVal === rightStrVal)
        })
        b91 + b92
    }

    def testUndefNull(pv: AbsPValue, locset: AbsLoc): Boolean = (pv.undefval.gamma, pv.nullval.gamma) match {
      case (ConSimpleBot(), ConSimpleBot()) => false
      case _ => (pv.numval.gammaSimple, pv.strval.gammaSimple, locset.isBottom) match {
        case (ConSimpleBot(), ConSimpleBot(), true) => false
        case _ => true
      }
    }

    val b10 = (testUndefNull(leftPV, left.locset), testUndefNull(rightPV, right.locset)) match {
      case (false, false) => AbsBool.Bot
      case _ => afalse
    }

    ValueUtil(b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 + b9 + b10)
  }

  def bopEqBetter(h: Heap, left: Value, right: Value): Value = {
    bopEqHelp(left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, h, hint))
  }

  def bopEq(left: Value, right: Value): Value = {
    bopEqHelp(left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, hint))
  }

  /* != */
  def bopNeq(left: Value, right: Value): Value = {
    val resAbsBool = bopEq(left, right).pvalue.boolval.negate
    ValueUtil(resAbsBool)
  }

  /* === */
  def bopSEq(left: Value, right: Value): Value = {
    val isMultiType =
      if ((left + right).typeCount > 1) afalse
      else AbsBool.Bot
    val isLocsetSame =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset <> right.locset
        (left.locset.gammaSingle, right.locset.gammaSingle, intersect.gammaSingle) match {
          case (_, _, ConSingleBot()) => afalse
          case (ConSingleCon(_), ConSingleCon(_), ConSingleCon(loc)) if loc.recency == Recent => atrue
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot
    val isSame =
      (left.pvalue.undefval === right.pvalue.undefval) +
        (left.pvalue.nullval === right.pvalue.nullval) +
        (left.pvalue.numval === right.pvalue.numval) +
        (left.pvalue.strval === right.pvalue.strval) +
        (left.pvalue.boolval === right.pvalue.boolval) +
        isLocsetSame
    ValueUtil(isMultiType + isSame)
  }

  /* !== */
  def bopSNeq(left: Value, right: Value): Value = {
    val resAbsBool = bopSEq(left, right).pvalue.boolval.negate
    ValueUtil(resAbsBool)
  }

  private def bopCompareHelp(
    leftPV: AbsPValue,
    rightPV: AbsPValue,
    cmpAbsNum: (AbsNumber, AbsNumber) => AbsBool,
    cmpAbsStr: (AbsString, AbsString) => AbsBool
  ): Value = {
    (leftPV.strval.gammaSimple, rightPV.strval.gammaSimple) match {
      case (ConSimpleBot(), _) | (_, ConSimpleBot()) =>
        val leftAbsNum = typeHelper.ToNumber(leftPV)
        val rightAbsNum = typeHelper.ToNumber(rightPV)
        ValueUtil(cmpAbsNum(leftAbsNum, rightAbsNum))
      case _ =>
        val leftPV2 = leftPV.copyWith(strval = AbsString.Bot)
        val rightPV2 = rightPV.copyWith(strval = AbsString.Bot)
        val resAbsBool = cmpAbsStr(leftPV.strval, rightPV.strval)
        ValueUtil(resAbsBool) +
          bopCompareHelp(leftPV, rightPV2, cmpAbsNum, cmpAbsStr) +
          bopCompareHelp(leftPV2, rightPV, cmpAbsNum, cmpAbsStr)
    }
  }

  /* < */
  def bopLess(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => (leftAbsNum < rightAbsNum),
      (leftAbsStr, rightAbsStr) => (leftAbsStr < rightAbsStr))
  }

  /* > */
  def bopGreater(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => (rightAbsNum < leftAbsNum),
      (leftAbsStr, rightAbsStr) => (rightAbsStr < leftAbsStr))
  }

  /* <= */
  def bopLessEq(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        (leftAbsNum.gammaSingle, rightAbsNum.gammaSingle) match {
          case (ConSingleCon(Num(n1)), ConSingleCon(Num(n2))) if n1.isNaN & n2.isNaN => afalse
          case _ => (rightAbsNum < leftAbsNum).negate
        }
      },
      (leftAbsStr, rightAbsStr) => (rightAbsStr < leftAbsStr).negate)
  }

  /* >= */
  def bopGreaterEq(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        (leftAbsNum.gammaSingle, rightAbsNum.gammaSingle) match {
          case (ConSingleCon(Num(n1)), ConSingleCon(Num(n2))) if n1.isNaN & n2.isNaN => afalse
          case _ => (leftAbsNum < rightAbsNum).negate
        }
      },
      (leftAbsStr, rightAbsStr) => (leftAbsStr < rightAbsStr).negate)
  }
}
