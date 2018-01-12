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

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.util._

////////////////////////////////////////////////////////////////
// Collection of Semantics helper functions
////////////////////////////////////////////////////////////////
object Helper {
  val typeHelper = TypeConversionHelper
  private val afalse = AbsBool.False
  private val atrue = AbsBool.True

  def arrayLenghtStore(heap: AbsHeap, idxAbsStr: AbsStr, storeV: AbsValue, l: Loc): (AbsHeap, Set[Exception]) = {
    if (AbsStr("length") ⊑ idxAbsStr) {
      val nOldLen = heap.get(l)("length").value.pvalue.numval
      val nNewLen = typeHelper.ToUint32(storeV)
      val nValue = typeHelper.ToNumber(storeV)
      val bCanPut = heap.get(l).CanPut("length", heap)

      val arrLengthHeap2 =
        if ((atrue ⊑ (nOldLen < nNewLen)
          || atrue ⊑ (nOldLen StrictEquals nNewLen))
          && (atrue ⊑ bCanPut))
          heap.propStore(l, AbsStr("length"), storeV)
        else
          AbsHeap.Bot

      val arrLengthHeap3 =
        if (afalse ⊑ bCanPut) heap
        else AbsHeap.Bot

      val arrLengthHeap4 =
        if ((atrue ⊑ (nNewLen < nOldLen)) && (atrue ⊑ bCanPut)) {
          val hi = heap.propStore(l, AbsStr("length"), storeV)
          (nNewLen.getSingle, nOldLen.getSingle) match {
            case (ConOne(n1), ConOne(n2)) =>
              (n1.toInt until n2.toInt).foldLeft(hi)((hj, i) => {
                val (tmpHeap, _) = hj.delete(l, AbsStr(i.toString))
                tmpHeap
              })
            case (ConZero(), _) | (_, ConZero()) => AbsHeap.Bot
            case _ =>
              val (tmpHeap, _) = hi.delete(l, AbsStr.Number)
              tmpHeap
          }
        } else {
          AbsHeap.Bot
        }

      val arrLengthHeap1 =
        if (atrue ⊑ (nValue StrictEquals nNewLen))
          arrLengthHeap2 ⊔ arrLengthHeap3 ⊔ arrLengthHeap4
        else
          AbsHeap.Bot

      val lenExcSet1 =
        if (afalse ⊑ (nValue StrictEquals nNewLen)) HashSet[Exception](RangeError)
        else ExcSetEmpty
      (arrLengthHeap1, lenExcSet1)
    } else {
      (AbsHeap.Bot, ExcSetEmpty)
    }
  }

  def arrayIdxStore(heap: AbsHeap, idxAbsStr: AbsStr, storeV: AbsValue, l: Loc): AbsHeap = {
    if (atrue ⊑ idxAbsStr.isArrayIndex) {
      val nOldLen = heap.get(l)("length").value.pvalue.numval
      val num = typeHelper.ToNumber(idxAbsStr)
      val nIndex = typeHelper.ToUint32(num)
      val bGtEq = atrue ⊑ (nOldLen < nIndex) ||
        atrue ⊑ (nOldLen StrictEquals nIndex)
      val bCanPutLen = heap.get(l).CanPut("length", heap)
      // 4.b
      val arrIndexHeap1 =
        if (bGtEq && afalse ⊑ bCanPutLen) heap
        else AbsHeap.Bot
      // 4.c
      val arrIndexHeap2 =
        if (atrue ⊑ (nIndex < nOldLen))
          heap.propStore(l, idxAbsStr, storeV)
        else AbsHeap.Bot
      // 4.e
      val arrIndexHeap3 =
        if (bGtEq && atrue ⊑ bCanPutLen) {
          val hi = heap.propStore(l, idxAbsStr, storeV)
          val idxVal = AbsValue(nIndex)
          val vNewIndex = bopPlus(idxVal, AbsValue(1))
          hi.propStore(l, AbsStr("length"), vNewIndex)
        } else AbsHeap.Bot
      arrIndexHeap1 ⊔ arrIndexHeap2 ⊔ arrIndexHeap3
    } else
      AbsHeap.Bot
  }

  def storeHelp(objLocSet: AbsLoc, idxAbsStr: AbsStr, storeV: AbsValue, heap: AbsHeap): (AbsHeap, Set[Exception]) = {
    // non-array objects
    val locSetNArr = objLocSet.filter(l =>
      (afalse ⊑ heap.isArray(l)) && atrue ⊑ heap.get(l).CanPut(idxAbsStr, heap))
    // array objects
    val locSetArr = objLocSet.filter(l =>
      (atrue ⊑ heap.isArray(l)) && atrue ⊑ heap.get(l).CanPut(idxAbsStr, heap))

    // can not store
    val cantPutHeap =
      if (objLocSet.exists((l) => afalse ⊑ heap.get(l).CanPut(idxAbsStr, heap))) heap
      else AbsHeap.Bot

    // store for non-array object
    val nArrHeap = locSetNArr.foldLeft[AbsHeap](AbsHeap.Bot)((iHeap, l) => {
      iHeap ⊔ heap.propStore(l, idxAbsStr, storeV)
    })

    // 15.4.5.1 [[DefineOwnProperty]] of Array
    val (arrHeap, arrExcSet) = locSetArr.foldLeft[(AbsHeap, Set[Exception])]((AbsHeap.Bot, ExcSetEmpty))((res2, l) => {
      // 3. s is length
      val (lengthHeap, lengthExcSet) = arrayLenghtStore(heap, idxAbsStr, storeV, l)
      // 4. s is array index
      val arrIndexHeap = arrayIdxStore(heap, idxAbsStr, storeV, l)

      // 5. other
      val otherHeap =
        if (idxAbsStr != AbsStr("length") && afalse ⊑ idxAbsStr.isArrayIndex)
          heap.propStore(l, idxAbsStr, storeV)
        else
          AbsHeap.Bot
      val (tmpHeap2, tmpExcSet2) = res2
      (tmpHeap2 ⊔ lengthHeap ⊔ arrIndexHeap ⊔ otherHeap, tmpExcSet2 ++ lengthExcSet)
    })

    (cantPutHeap ⊔ nArrHeap ⊔ arrHeap, arrExcSet)
  }

  def inherit(h: AbsHeap, loc1: Loc, loc2: Loc): AbsValue = {
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
        val eqVal = bopSEq(h, locVal1, locVal2)
        val b = eqVal.pvalue.boolval
        val t =
          if (AT ⊑ b) boolTrueVal
          else AbsValue.Bot
        val f =
          if (AF ⊑ b) {
            val protoVal = h.get(l1)(IPrototype).value
            val v1 = protoVal.pvalue.nullval.fold(boolBotVal) { _ => boolFalseVal }
            v1 ⊔ protoVal.locset.foldLeft(AbsValue.Bot)((tmpVal, protoLoc) => tmpVal ⊔ iter(protoLoc))
          } else AbsValue.Bot
        t ⊔ f
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
    AbsValue(-typeHelper.ToNumber(value))

  /* - */
  def uopMinusBetter(h: AbsHeap, value: AbsValue): AbsValue =
    AbsValue(-typeHelper.ToNumber(value, h))

  /* ~ */
  def uopBitNeg(value: AbsValue): AbsValue =
    AbsValue(~typeHelper.ToInt32(value))

  /* ! */
  def uopNeg(value: AbsValue): AbsValue =
    AbsValue(typeHelper.ToBoolean(value).negate)

  /* binary operator */
  /* | */
  def bopBitOr(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum | rAbsNum
    AbsValue(resAbsNum)
  }

  /* & */
  def bopBitAnd(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum & rAbsNum
    AbsValue(resAbsNum)
  }

  /* ^ */
  def bopBitXor(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum ^ rAbsNum
    AbsValue(resAbsNum)
  }

  /* << */
  def bopLShift(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUint32(right)
    val resAbsNum = lAbsNum << rAbsNum
    AbsValue(resAbsNum)
  }

  /* >> */
  def bopRShift(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUint32(right)
    val resAbsNum = lAbsNum >> rAbsNum
    AbsValue(resAbsNum)
  }

  /* >>> */
  def bopURShift(left: AbsValue, right: AbsValue): AbsValue = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUint32(right)
    val resAbsNum = lAbsNum >>> rAbsNum
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
        val resAbsNum = lAbsNum + rAbsNum
        AbsValue(resAbsNum)
      case (false, true) =>
        val resVal = AbsValue(primLPV.strval.concat(TypeConversionHelper.ToString(primRPV)))
        resVal ⊔ bopPlus(AbsValue(primLPV.copy(strval = AbsStr.Bot)), AbsValue(primRPV))
      case (true, false) =>
        val resVal = AbsValue(TypeConversionHelper.ToString(primLPV).concat(primRPV.strval))
        resVal ⊔ bopPlus(AbsValue(primLPV), AbsValue(primRPV.copy(strval = AbsStr.Bot)))
      case (false, false) =>
        val lStr = TypeConversionHelper.ToString(primLPV).concat(primRPV.strval)
        val rStr = primLPV.strval.concat(TypeConversionHelper.ToString(primRPV))
        val resVal = AbsValue(lStr ⊔ rStr)

        resVal ⊔ bopPlus(AbsValue(primLPV.copy(strval = AbsStr.Bot)), AbsValue(primRPV.copy(strval = AbsStr.Bot)))
    }
  }

  /* - */
  def bopMinus(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) - typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* * */
  def bopMul(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) * typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* / */
  def bopDiv(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) / typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* % */
  def bopMod(left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsNum = typeHelper.ToNumber(left) % typeHelper.ToNumber(right)
    AbsValue(resAbsNum)
  }

  /* == */
  private def bopEqHelp(h: AbsHeap, left: AbsValue, right: AbsValue, objToPrimitive: (AbsValue, String) => AbsPValue): AbsValue = {
    val leftPV = left.pvalue
    val rightPV = right.pvalue
    val locsetTest =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset ⊓ right.locset
        (left.locset.getSingle, right.locset.getSingle, intersect.getSingle) match {
          case (_, _, ConZero()) => afalse
          case (ConOne(_), ConOne(_), ConOne(loc)) if h.isConcrete(loc) => atrue
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot
    val b1 = (leftPV.undefval StrictEquals rightPV.undefval) ⊔
      (leftPV.nullval StrictEquals rightPV.nullval) ⊔
      (leftPV.numval StrictEquals rightPV.numval) ⊔
      (leftPV.strval StrictEquals rightPV.strval) ⊔
      (leftPV.boolval StrictEquals rightPV.boolval) ⊔
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
        val rightNumVal = rightPV.strval.ToNumber
        (leftPV.numval StrictEquals rightNumVal)
    }
    val b5 = (leftPV.strval.isBottom, rightPV.numval.isBottom) match {
      case (true, _) | (_, true) => AbsBool.Bot
      case _ =>
        val leftNumVal = leftPV.strval.ToNumber
        (leftNumVal StrictEquals rightPV.numval)
    }
    val b6 = leftPV.boolval.isBottom match {
      case true => AbsBool.Bot
      case false =>
        val leftNumVal = leftPV.boolval.ToNumber
        val b61 = rightPV.numval.fold(AbsBool.Bot)(rightNumVal => {
          (leftNumVal StrictEquals rightNumVal)
        })
        val b62 = rightPV.strval.fold(AbsBool.Bot)(rightStrVal => {
          val rightNumVal = rightStrVal.ToNumber
          (leftNumVal StrictEquals rightNumVal)
        })
        val b63 = right.locset.fold(AbsBool.Bot) {
          case _ =>
            val rightNumVal = objToPrimitive(right, "Number").numval
            (leftNumVal StrictEquals rightNumVal)
        }
        val b64 = rightPV.undefval.fold(AbsBool.Bot)(_ => afalse)
        val b65 = rightPV.nullval.fold(AbsBool.Bot)(_ => afalse)
        b61 ⊔ b62 ⊔ b63 ⊔ b64 ⊔ b65
    }

    val b7 = rightPV.boolval.isBottom match {
      case true => AbsBool.Bot
      case false =>
        val rightNumVal = rightPV.boolval.ToNumber
        val b71 = leftPV.numval.fold(AbsBool.Bot)(leftNumVal => {
          (leftNumVal StrictEquals rightNumVal)
        })
        val b72 = leftPV.strval.fold(AbsBool.Bot)(leftStrVal => {
          val leftNumVal = leftStrVal.ToNumber
          (leftNumVal StrictEquals rightNumVal)
        })
        val b73 = left.locset.fold(AbsBool.Bot) {
          case _ =>
            val leftNumVal = objToPrimitive(left, "Number").numval
            (leftNumVal StrictEquals rightNumVal)
        }
        val b74 = leftPV.undefval.fold(AbsBool.Bot)(_ => afalse)
        val b75 = leftPV.undefval.fold(AbsBool.Bot)(_ => afalse)
        b71 ⊔ b72 ⊔ b73 ⊔ b74 ⊔ b75
    }

    val b8 = right.locset.fold(AbsBool.Bot) {
      case _ =>
        val b81 = leftPV.numval.fold(AbsBool.Bot)(leftNumVal => {
          val rightNumVal = objToPrimitive(right, "Number").numval
          (leftNumVal StrictEquals rightNumVal)
        })
        val b82 = leftPV.strval.fold(AbsBool.Bot)(leftStrVal => {
          val rightStrVal = objToPrimitive(right, "String").strval
          (leftStrVal StrictEquals rightStrVal)
        })
        b81 ⊔ b82
    }

    val b9 = left.locset.fold(AbsBool.Bot) {
      case _ =>
        val b91 = rightPV.numval.fold(AbsBool.Bot)(rightNumVal => {
          val leftNumVal = objToPrimitive(left, "Number").numval
          (leftNumVal StrictEquals rightNumVal)
        })
        val b92 = rightPV.strval.fold(AbsBool.Bot)(rightStrVal => {
          val leftStrVal = objToPrimitive(left, "String").strval
          (leftStrVal StrictEquals rightStrVal)
        })
        b91 ⊔ b92
    }

    def testUndefNull(value: AbsValue): AbsBool = {
      val pvalue = value.pvalue
      val locset = value.locset
      val trueV =
        if (pvalue.undefval.isTop || pvalue.nullval.isTop) atrue
        else AbsBool.Bot
      val falseV =
        if (!pvalue.copy(undefval = AbsUndef.Bot, nullval = AbsNull.Bot).isBottom || !locset.isBottom) afalse
        else AbsBool.Bot
      trueV ⊔ falseV
    }

    val b10 =
      if (AbsBool.True ⊑ (testUndefNull(left) xor testUndefNull(right))) afalse
      else AbsBool.Bot

    AbsValue(b1 ⊔ b2 ⊔ b3 ⊔ b4 ⊔ b5 ⊔ b6 ⊔ b7 ⊔ b8 ⊔ b9 ⊔ b10)
  }

  def bopEqBetter(h: AbsHeap, left: AbsValue, right: AbsValue): AbsValue = {
    bopEqHelp(h, left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, h, hint))
  }

  def bopEq(h: AbsHeap, left: AbsValue, right: AbsValue): AbsValue = {
    bopEqHelp(h, left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, hint))
  }

  /* != */
  def bopNeq(h: AbsHeap, left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsBool = bopEq(h, left, right).pvalue.boolval.negate
    AbsValue(resAbsBool)
  }

  /* StrictEquals */
  def bopSEq(h: AbsHeap, left: AbsValue, right: AbsValue): AbsValue = {
    val isMultiType =
      if ((left ⊔ right).typeCount > 1) afalse
      else AbsBool.Bot
    val isLocsetSame =
      if (!left.locset.isBottom && !right.locset.isBottom) {
        val intersect = left.locset ⊓ right.locset
        (left.locset.getSingle, right.locset.getSingle, intersect.getSingle) match {
          case (_, _, ConZero()) => afalse
          case (ConOne(_), ConOne(_), ConOne(loc)) if h.isConcrete(loc) => atrue
          case _ => AbsBool.Top
        }
      } else AbsBool.Bot
    val isSame =
      (left.pvalue.undefval StrictEquals right.pvalue.undefval) ⊔
        (left.pvalue.nullval StrictEquals right.pvalue.nullval) ⊔
        (left.pvalue.numval StrictEquals right.pvalue.numval) ⊔
        (left.pvalue.strval StrictEquals right.pvalue.strval) ⊔
        (left.pvalue.boolval StrictEquals right.pvalue.boolval) ⊔
        isLocsetSame
    AbsValue(isMultiType ⊔ isSame)
  }

  /* !== */
  def bopSNeq(h: AbsHeap, left: AbsValue, right: AbsValue): AbsValue = {
    val resAbsBool = bopSEq(h, left, right).pvalue.boolval.negate
    AbsValue(resAbsBool)
  }

  private def bopCompareHelp(
    leftPV: AbsPValue,
    rightPV: AbsPValue,
    cmpAbsNum: (AbsNum, AbsNum) => AbsBool,
    cmpAbsStr: (AbsStr, AbsStr) => AbsBool
  ): AbsValue = {
    (leftPV.strval.isBottom, rightPV.strval.isBottom) match {
      case (true, _) | (_, true) =>
        val leftAbsNum = typeHelper.ToNumber(leftPV)
        val rightAbsNum = typeHelper.ToNumber(rightPV)
        AbsValue(cmpAbsNum(leftAbsNum, rightAbsNum))
      case _ =>
        val leftPV2 = leftPV.copy(strval = AbsStr.Bot)
        val rightPV2 = rightPV.copy(strval = AbsStr.Bot)
        val resAbsBool = cmpAbsStr(leftPV.strval, rightPV.strval)
        AbsValue(resAbsBool) ⊔
          bopCompareHelp(leftPV, rightPV2, cmpAbsNum, cmpAbsStr) ⊔
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

  def propLoad(objV: AbsValue, absStrSet: Set[AbsStr], h: AbsHeap): AbsValue = {
    val objLocSet = objV.locset
    val v1 = objLocSet.foldLeft(AbsValue.Bot)((tmpVal1, loc) => {
      val tmpObj = h.get(loc)
      absStrSet.foldLeft(tmpVal1)((tmpVal2, absStr) => {
        tmpVal2 ⊔ tmpObj.Get(absStr, h)
      })
    })
    v1
  }
}
