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
  private val afalse = AbsBool.False
  private val atrue = AbsBool.True

  def arrayLenghtStore(heap: Heap, idxAbsStr: AbsString, storeV: AbsValue, l: Loc): (Heap, Set[Exception]) = {
    if (AbsString("length") <= idxAbsStr) {
      val nOldLen = heap.get(l)("length").value.pvalue.numval
      val nNewLen = typeHelper.ToUInt32(storeV)
      val nValue = typeHelper.ToNumber(storeV)
      val bCanPut = heap.get(l).CanPut("length", heap)

      val arrLengthHeap2 =
        if ((atrue <= (nOldLen < nNewLen)
          || atrue <= (nOldLen === nNewLen))
          && (atrue <= bCanPut))
          heap.propStore(l, AbsString("length"), storeV)
        else
          Heap.Bot

      val arrLengthHeap3 =
        if (afalse <= bCanPut) heap
        else Heap.Bot

      val arrLengthHeap4 =
        if ((atrue <= (nNewLen < nOldLen)) && (atrue <= bCanPut)) {
          val hi = heap.propStore(l, AbsString("length"), storeV)
          (nNewLen.getSingle, nOldLen.getSingle) match {
            case (ConOne(n1), ConOne(n2)) =>
              (n1.toInt until n2.toInt).foldLeft(hi)((hj, i) => {
                val (tmpHeap, _) = hj.delete(l, AbsString(i.toString))
                tmpHeap
              })
            case (ConZero(), _) | (_, ConZero()) => Heap.Bot
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
        else ExcSetEmpty
      (arrLengthHeap1, lenExcSet1)
    } else {
      (Heap.Bot, ExcSetEmpty)
    }
  }

  def arrayIdxStore(heap: Heap, idxAbsStr: AbsString, storeV: AbsValue, l: Loc): Heap = {
    if (atrue <= idxAbsStr.isArrayIndex) {
      val nOldLen = heap.get(l)("length").value.pvalue.numval
      val num = typeHelper.ToNumber(idxAbsStr)
      val nIndex = typeHelper.ToUInt32(num)
      val bGtEq = atrue <= (nOldLen < nIndex) ||
        atrue <= (nOldLen === nIndex)
      val bCanPutLen = heap.get(l).CanPut("length", heap)
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
          val idxVal = AbsValue(nIndex)
          val vNewIndex = bopPlus(idxVal, AbsValue(1))
          hi.propStore(l, AbsString("length"), vNewIndex)
        } else Heap.Bot
      arrIndexHeap1 + arrIndexHeap2 + arrIndexHeap3
    } else
      Heap.Bot
  }

  def storeHelp(objLocSet: AbsLoc, idxAbsStr: AbsString, storeV: AbsValue, heap: Heap): (Heap, Set[Exception]) = {
    // non-array objects
    val locSetNArr = objLocSet.filter(l =>
      (afalse <= heap.isArray(l)) && atrue <= heap.get(l).CanPut(idxAbsStr, heap))
    // array objects
    val locSetArr = objLocSet.filter(l =>
      (atrue <= heap.isArray(l)) && atrue <= heap.get(l).CanPut(idxAbsStr, heap))

    // can not store
    val cantPutHeap =
      if (objLocSet.exists((l) => afalse <= heap.get(l).CanPut(idxAbsStr, heap))) heap
      else Heap.Bot

    // store for non-array object
    val nArrHeap = locSetNArr.foldLeft(Heap.Bot)((iHeap, l) => {
      iHeap + heap.propStore(l, idxAbsStr, storeV)
    })

    // 15.4.5.1 [[DefineOwnProperty]] of Array
    val (arrHeap, arrExcSet) = locSetArr.foldLeft((Heap.Bot, ExcSetEmpty))((res2, l) => {
      // 3. s is length
      val (lengthHeap, lengthExcSet) = arrayLenghtStore(heap, idxAbsStr, storeV, l)
      // 4. s is array index
      val arrIndexHeap = arrayIdxStore(heap, idxAbsStr, storeV, l)

      // 5. other
      val otherHeap =
        if (idxAbsStr != AbsString("length") && afalse <= idxAbsStr.isArrayIndex)
          heap.propStore(l, idxAbsStr, storeV)
        else
          Heap.Bot
      val (tmpHeap2, tmpExcSet2) = res2
      (tmpHeap2 + lengthHeap + arrIndexHeap + otherHeap, tmpExcSet2 ++ lengthExcSet)
    })

    (cantPutHeap + nArrHeap + arrHeap, arrExcSet)
  }

  def inherit(h: Heap, loc1: Loc, loc2: Loc): AbsValue = {
    var visited = AbsLoc.Bot
    val locVal2 = AbsValue(loc2)
    val boolBotVal = AbsValue(AbsPValue.Bot)
    val boolTrueVal = AbsValue(true)
    val boolFalseVal = AbsValue(false)

    def iter(l1: Loc): AbsValue = {
      if (visited.contains(l1)) AbsValue.Bot
      else {
        visited += l1
        val locVal1 = AbsValue(l1)
        val eqVal = bopSEq(locVal1, locVal2)
        eqVal.pvalue.boolval.map(
          thenV = boolTrueVal,
          elseV = {
          val protoVal = h.get(l1)(IPrototype).value
          val v1 = protoVal.pvalue.nullval.fold(boolBotVal) { _ => boolFalseVal }
          v1 + protoVal.locset.foldLeft(AbsValue.Bot)((tmpVal, protoLoc) => tmpVal + iter(protoLoc))
        }
        )(AbsValue)
      }
    }

    iter(loc1)
  }

  /* unary operator */
  /* void */
  def uVoid(value: AbsValue): AbsValue = AbsValue(Undef)
  /* + */
  def uopPlus(value: AbsValue): AbsValue =
    AbsValue(typeHelper.ToNumber(value))

  /* - */
  def uopMinus(value: AbsValue): AbsValue =
    AbsValue(typeHelper.ToNumber(value).negate)

  /* - */
  def uopMinusBetter(h: Heap, value: AbsValue): AbsValue =
    AbsValue(typeHelper.ToNumber(value, h).negate)

  /* ~ */
  def uopBitNeg(value: AbsValue): AbsValue =
    AbsValue(typeHelper.ToInt32(value).bitNegate)

  /* ! */
  def uopNeg(value: AbsValue): AbsValue =
    AbsValue(typeHelper.ToBoolean(value).negate)

  /* binary operator */
  /* | */
  def bopBitOr(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitOr(rAbsNum)
    AbsValue(resAbsNum)
  }

  /* & */
  def bopBitAnd(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitAnd(rAbsNum)
    AbsValue(resAbsNum)
  }

  /* ^ */
  def bopBitXor(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitXor(rAbsNum)
    AbsValue(resAbsNum)
  }

  /* << */
  def bopLShift(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitLShift(rAbsNum)
    AbsValue(resAbsNum)
  }

  /* >> */
  def bopRShift(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitRShift(rAbsNum)
    AbsValue(resAbsNum)
  }

  /* >>> */
  def bopURShift(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitURShift(rAbsNum)
    AbsValue(resAbsNum)
  }

  /* + */
  def bopPlus(left: AbsValue, right: AbsValue): AbsValue = {
    def PValue2Tpl(pv: AbsPValue): (AbsPValue, AbsPValue, AbsPValue, AbsPValue, AbsPValue) = {
      (
        pv.undefval,
        pv.nullval,
        pv.boolval,
        pv.numval,
        pv.strval
      )
    }

    val primLPV = typeHelper.ToPrimitive(left)
    val primRPV = typeHelper.ToPrimitive(right)
    (primLPV.strval.isBottom, primRPV.strval.isBottom) match {
      case (true, true) =>
        val (lAbsNum, rAbsNum) = (typeHelper.ToNumber(primLPV), typeHelper.ToNumber(primRPV))
        val resAbsNum = lAbsNum add rAbsNum
        AbsValue(resAbsNum)
      case (false, true) =>
        val resVal = AbsValue(primLPV.strval.concat(TypeConversionHelper.ToString(primRPV)))
        resVal + bopPlus(AbsValue(primLPV.copyWith(strval = AbsString.Bot)), AbsValue(primRPV))
      case (true, false) =>
        val resVal = AbsValue(TypeConversionHelper.ToString(primLPV).concat(primRPV.strval))
        resVal + bopPlus(AbsValue(primLPV), AbsValue(primRPV.copyWith(strval = AbsString.Bot)))
      case (false, false) =>
        val lStr = TypeConversionHelper.ToString(primLPV).concat(primRPV.strval)
        val rStr = primLPV.strval.concat(TypeConversionHelper.ToString(primRPV))
        val resVal = AbsValue(lStr + rStr)

        resVal + bopPlus(AbsValue(primLPV.copyWith(strval = AbsString.Bot)), AbsValue(primRPV.copyWith(strval = AbsString.Bot)))
    }
  }

  /* - */
  def bopMinus(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) sub typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* * */
  def bopMul(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) mul typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* / */
  def bopDiv(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) div typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* % */
  def bopMod(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) mod typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* == */
  private def bopEqHelp(left: AbsValue, right: AbsValue, objToPrimitive: (AbsValue, String) => AbsPValue): AbsValue = {
    val leftPV = left.pvalue
    val rightPV = right.pvalue
    val locsetTest =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset <> right.locset
        (left.locset.getSingle, right.locset.getSingle, intersect.getSingle) match {
          case (_, _, ConZero()) => afalse
          case (ConOne(_), ConOne(_), ConOne(loc)) if loc.recency == Recent => atrue
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot
    val b1 = (leftPV.undefval === rightPV.undefval) +
      (leftPV.nullval === rightPV.nullval) +
      (leftPV.numval === rightPV.numval) +
      (leftPV.strval === rightPV.strval) +
      (leftPV.boolval === rightPV.boolval) +
      locsetTest
    val b2 = (leftPV.nullval.isBottom, rightPV.undefval.isBottom) match {
      case (false, false) => atrue
      case _ => AbsBool.Bot
    }
    val b3 = (leftPV.undefval.isBottom, rightPV.nullval.isBottom) match {
      case (false, false) => atrue
      case _ => AbsBool.Bot
    }
    val b4 = (leftPV.numval.isBottom, rightPV.strval.isBottom) match {
      case (true, _) | (_, true) => AbsBool.Bot
      case _ =>
        val rightNumVal = rightPV.strval.toAbsNumber
        (leftPV.numval === rightNumVal)
    }
    val b5 = (leftPV.strval.isBottom, rightPV.numval.isBottom) match {
      case (true, _) | (_, true) => AbsBool.Bot
      case _ =>
        val leftNumVal = leftPV.strval.toAbsNumber
        (leftNumVal === rightPV.numval)
    }
    val b6 = leftPV.boolval.isBottom match {
      case true => AbsBool.Bot
      case false =>
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
    }

    val b7 = rightPV.boolval.isBottom match {
      case true => AbsBool.Bot
      case false =>
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

    def testUndefNull(value: AbsValue): AbsBool = {
      val pvalue = value.pvalue
      val locset = value.locset
      val trueV =
        if (pvalue.undefval.isTop || pvalue.nullval.isTop) atrue
        else AbsBool.Bot
      val falseV =
        if (!pvalue.copyWith(undefval = AbsUndef.Bot, nullval = AbsNull.Bot).isBottom || !locset.isBottom) afalse
        else AbsBool.Bot
      trueV + falseV
    }

    val b10 =
      if (AbsBool.True <= (testUndefNull(left) xor testUndefNull(right))) afalse
      else AbsBool.Bot

    AbsValue(b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 + b9 + b10)
  }

  def bopEqBetter(h: Heap, left: AbsValue, right: AbsValue): AbsValue = {
    bopEqHelp(left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, h, hint))
  }

  def bopEq(left: AbsValue, right: AbsValue): AbsValue = {
    bopEqHelp(left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, hint))
  }

  /* != */
  def bopNeq(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsBool = bopEq(left, right).pvalue.boolval.negate
    AbsValue(resAbsBool)
  }

  /* === */
  def bopSEq(left: AbsValue, right: AbsValue): AbsValue = {
    val isMultiType =
      if ((left + right).typeCount > 1) afalse
      else AbsBool.Bot
    val isLocsetSame =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset <> right.locset
        (left.locset.getSingle, right.locset.getSingle, intersect.getSingle) match {
          case (_, _, ConZero()) => afalse
          case (ConOne(_), ConOne(_), ConOne(loc)) if loc.recency == Recent => atrue
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
    AbsValue(isMultiType + isSame)
  }

  /* !== */
  def bopSNeq(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsBool = bopSEq(left, right).pvalue.boolval.negate
    AbsValue(resAbsBool)
  }

  private def bopCompareHelp(
    leftPV: AbsPValue,
    rightPV: AbsPValue,
    cmpAbsNum: (AbsNumber, AbsNumber) => AbsBool,
    cmpAbsStr: (AbsString, AbsString) => AbsBool
  ): AbsValue = {
    (leftPV.strval.isBottom, rightPV.strval.isBottom) match {
      case (true, _) | (_, true) =>
        val leftAbsNum = typeHelper.ToNumber(leftPV)
        val rightAbsNum = typeHelper.ToNumber(rightPV)
        AbsValue(cmpAbsNum(leftAbsNum, rightAbsNum))
      case _ =>
        val leftPV2 = leftPV.copyWith(strval = AbsString.Bot)
        val rightPV2 = rightPV.copyWith(strval = AbsString.Bot)
        val resAbsBool = cmpAbsStr(leftPV.strval, rightPV.strval)
        AbsValue(resAbsBool) +
          bopCompareHelp(leftPV, rightPV2, cmpAbsNum, cmpAbsStr) +
          bopCompareHelp(leftPV2, rightPV, cmpAbsNum, cmpAbsStr)
    }
  }

  /* < */
  def bopLess(left: AbsValue, right: AbsValue): AbsValue = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => (leftAbsNum < rightAbsNum),
      (leftAbsStr, rightAbsStr) => (leftAbsStr < rightAbsStr))
  }

  /* > */
  def bopGreater(left: AbsValue, right: AbsValue): AbsValue = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => (rightAbsNum < leftAbsNum),
      (leftAbsStr, rightAbsStr) => (rightAbsStr < leftAbsStr))
  }

  /* <= */
  def bopLessEq(left: AbsValue, right: AbsValue): AbsValue = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        (leftAbsNum.getSingle, rightAbsNum.getSingle) match {
          case (ConOne(Num(n1)), ConOne(Num(n2))) if n1.isNaN || n2.isNaN => afalse
          case (ConOne(Num(n1)), _) if n1.isNaN => afalse
          case (_, ConOne(Num(n2))) if n2.isNaN => afalse
          case _ => (rightAbsNum < leftAbsNum).negate
        }
      },
      (leftAbsStr, rightAbsStr) => (rightAbsStr < leftAbsStr).negate)
  }

  /* >= */
  def bopGreaterEq(left: AbsValue, right: AbsValue): AbsValue = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        (leftAbsNum.getSingle, rightAbsNum.getSingle) match {
          case (ConOne(Num(n1)), ConOne(Num(n2))) if n1.isNaN || n2.isNaN => afalse
          case (ConOne(Num(n1)), _) if n1.isNaN => afalse
          case (_, ConOne(Num(n2))) if n2.isNaN => afalse
          case _ => (leftAbsNum < rightAbsNum).negate
        }
      },
      (leftAbsStr, rightAbsStr) => (leftAbsStr < rightAbsStr).negate)
  }

  def propLoad(objV: AbsValue, absStrSet: Set[AbsString], h: Heap): AbsValue = {
    val objLocSet = objV.locset
    val v1 = objLocSet.foldLeft(AbsValue.Bot)((tmpVal1, loc) => {
      val tmpObj = h.get(loc)
      absStrSet.foldLeft(tmpVal1)((tmpVal2, absStr) => {
        tmpVal2 + tmpObj.Get(absStr, h)
      })
    })
    v1
  }
}
