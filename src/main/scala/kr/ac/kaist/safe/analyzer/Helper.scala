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
import kr.ac.kaist.safe.analyzer.domain.{ PValue, Value, _ }
import kr.ac.kaist.safe.util.{ Address, Loc, Recent }

////////////////////////////////////////////////////////////////
// Collection of Semantics helper functions
////////////////////////////////////////////////////////////////
case class Helper(utils: Utils) {
  val typeHelper = TypeConversionHelper(utils)
  private val absNumber: AbsNumberUtil = utils.absNumber
  private val afalse = utils.absBool.False
  private val atrue = utils.absBool.True
  private val pvalueU = utils.pvalue
  private val valueU = utils.value

  def arrayLenghtStore(heap: Heap, idxAbsStr: AbsString, storeV: Value, l: Loc): (Heap, Set[Exception]) = {
    if (utils.absString.alpha("length") <= idxAbsStr) {
      val nOldLen = heap.getOrElse(l, Obj.Bot(utils))
        .getOrElse("length")(absNumber.Bot) { _.objval.value.pvalue.numval }
      val nNewLen = typeHelper.ToUInt32(storeV)
      val nValue = typeHelper.ToNumber(storeV)
      val bCanPut = heap.canPut(l, utils.absString.alpha("length"))(utils)

      val arrLengthHeap2 =
        if ((atrue <= (nOldLen < nNewLen)(utils.absBool)
          || atrue <= (nOldLen === nNewLen)(utils.absBool))
          && (atrue <= bCanPut))
          heap.propStore(l, utils.absString.alpha("length"), storeV)(utils)
        else
          Heap.Bot

      val arrLengthHeap3 =
        if (afalse <= bCanPut) heap
        else Heap.Bot

      val arrLengthHeap4 =
        if ((atrue <= (nNewLen < nOldLen)(utils.absBool)) && (atrue <= bCanPut)) {
          val hi = heap.propStore(l, utils.absString.alpha("length"), storeV)(utils)
          (nNewLen.gammaSingle, nOldLen.gammaSingle) match {
            case (ConSingleCon(n1), ConSingleCon(n2)) =>
              (n1.toInt until n2.toInt).foldLeft(hi)((hj, i) => {
                val (tmpHeap, _) = hj.delete(l, utils.absString.alpha(i.toString))(utils)
                tmpHeap
              })
            case (ConSingleBot(), _) | (_, ConSingleBot()) => Heap.Bot
            case _ =>
              val (tmpHeap, _) = hi.delete(l, utils.absString.NumStr)(utils)
              tmpHeap
          }
        } else {
          Heap.Bot
        }

      val arrLengthHeap1 =
        if (atrue <= (nValue === nNewLen)(utils.absBool))
          arrLengthHeap2 + arrLengthHeap3 + arrLengthHeap4
        else
          Heap.Bot

      val lenExcSet1 =
        if (afalse <= (nValue === nNewLen)(utils.absBool)) HashSet[Exception](RangeError)
        else ExceptionSetEmpty
      (arrLengthHeap1, lenExcSet1)
    } else {
      (Heap.Bot, ExceptionSetEmpty)
    }
  }

  def arrayIdxStore(heap: Heap, idxAbsStr: AbsString, storeV: Value, l: Loc): Heap = {
    if (atrue <= idxAbsStr.isArrayIndex(utils.absBool)) {
      val nOldLen = heap.getOrElse(l, Obj.Bot(utils))
        .getOrElse("length")(absNumber.Bot) { _.objval.value.pvalue.numval }
      val idxPV = pvalueU(idxAbsStr)
      val numPV = pvalueU(typeHelper.ToNumber(idxPV))
      val nIndex = typeHelper.ToUInt32(valueU(numPV))
      val bGtEq = atrue <= (nOldLen < nIndex)(utils.absBool) ||
        atrue <= (nOldLen === nIndex)(utils.absBool)
      val bCanPutLen = heap.canPut(l, utils.absString.alpha("length"))(utils)
      // 4.b
      val arrIndexHeap1 =
        if (bGtEq && afalse <= bCanPutLen) heap
        else Heap.Bot
      // 4.c
      val arrIndexHeap2 =
        if (atrue <= (nIndex < nOldLen)(utils.absBool))
          heap.propStore(l, idxAbsStr, storeV)(utils)
        else Heap.Bot
      // 4.e
      val arrIndexHeap3 =
        if (bGtEq && atrue <= bCanPutLen) {
          val hi = heap.propStore(l, idxAbsStr, storeV)(utils)
          val idxVal = valueU(nIndex)
          val absNum1PV = pvalueU.alpha(1)
          val vNewIndex = bopPlus(idxVal, valueU(absNum1PV))
          hi.propStore(l, utils.absString.alpha("length"), vNewIndex)(utils)
        } else Heap.Bot
      arrIndexHeap1 + arrIndexHeap2 + arrIndexHeap3
    } else
      Heap.Bot
  }

  def storeHelp(objLocSet: Set[Loc], idxAbsStr: AbsString, storeV: Value, heap: Heap): (Heap, Set[Exception]) = {
    // non-array objects
    val locSetNArr = objLocSet.filter(l =>
      (afalse <= heap.isArray(l)(utils)) && atrue <= heap.canPut(l, idxAbsStr)(utils))
    // array objects
    val locSetArr = objLocSet.filter(l =>
      (atrue <= heap.isArray(l)(utils)) && atrue <= heap.canPut(l, idxAbsStr)(utils))

    // can not store
    val cantPutHeap =
      if (objLocSet.exists((l) => afalse <= heap.canPut(l, idxAbsStr)(utils))) heap
      else Heap.Bot

    // store for non-array object
    val nArrHeap = locSetNArr.foldLeft(Heap.Bot)((iHeap, l) => {
      iHeap + heap.propStore(l, idxAbsStr, storeV)(utils)
    })

    // 15.4.5.1 [[DefineOwnProperty]] of Array
    val (arrHeap, arrExcSet) = locSetArr.foldLeft((Heap.Bot, ExceptionSetEmpty))((res2, l) => {
      // 3. s is length
      val (lengthHeap, lengthExcSet) = arrayLenghtStore(heap, idxAbsStr, storeV, l)
      // 4. s is array index
      val arrIndexHeap = arrayIdxStore(heap, idxAbsStr, storeV, l)

      // 5. other
      val otherHeap =
        if (idxAbsStr != utils.absString.alpha("length") && afalse <= idxAbsStr.isArrayIndex(utils.absBool))
          heap.propStore(l, idxAbsStr, storeV)(utils)
        else
          Heap.Bot
      val (tmpHeap2, tmpExcSet2) = res2
      (tmpHeap2 + lengthHeap + arrIndexHeap + otherHeap, tmpExcSet2 ++ lengthExcSet)
    })

    (cantPutHeap + nArrHeap + arrHeap, arrExcSet)
  }

  def inherit(h: Heap, loc1: Loc, loc2: Loc): Value = {
    var visited = LocSetEmpty
    val locVal2 = valueU(loc2)
    val boolBotVal = valueU(pvalueU.Bot)
    val boolTrueVal = valueU.alpha(true)
    val boolFalseVal = valueU.alpha(false)

    def iter(l1: Loc): Value = {
      if (visited.contains(l1)) valueU.Bot
      else {
        visited += l1
        val locVal1 = valueU(l1)
        val eqVal = bopSEq(locVal1, locVal2)
        val v1 =
          if (utils.absBool.True <= eqVal.pvalue.boolval) boolTrueVal
          else boolBotVal
        val v2 =
          if (utils.absBool.False <= eqVal.pvalue.boolval) {
            val protoVal = h.getOrElse(l1, Obj.Bot(utils)).getOrElse("@proto")(valueU.Bot) { _.objval.value }
            val v1 = protoVal.pvalue.nullval.fold(boolBotVal) { _ => boolFalseVal }
            v1 + protoVal.locset.foldLeft(valueU.Bot)((tmpVal, protoLoc) => tmpVal + iter(protoLoc))
          } else boolBotVal
        v1 + v2
      }
    }

    iter(loc1)
  }

  /* unary operator */
  /* void */
  def uVoid(value: Value): Value = valueU.alpha()
  /* + */
  def uopPlus(value: Value): Value =
    valueU(typeHelper.ToNumber(value))

  /* - */
  def uopMinus(value: Value): Value =
    valueU(typeHelper.ToNumber(value).negate)

  /* - */
  def uopMinusBetter(h: Heap, value: Value): Value =
    valueU(typeHelper.ToNumber(value, h).negate)

  /* ~ */
  def uopBitNeg(value: Value): Value =
    valueU(typeHelper.ToInt32(value).bitNegate)

  /* ! */
  def uopNeg(value: Value): Value =
    valueU(typeHelper.ToBoolean(value).negate)

  /* binary operator */
  /* | */
  def bopBitOr(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitOr(rAbsNum)
    valueU(resAbsNum)
  }

  /* & */
  def bopBitAnd(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitAnd(rAbsNum)
    valueU(resAbsNum)
  }

  /* ^ */
  def bopBitXor(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToInt32(right)
    val resAbsNum = lAbsNum.bitXor(rAbsNum)
    valueU(resAbsNum)
  }

  /* << */
  def bopLShift(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitLShift(rAbsNum)
    valueU(resAbsNum)
  }

  /* >> */
  def bopRShift(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitRShift(rAbsNum)
    valueU(resAbsNum)
  }

  /* >>> */
  def bopURShift(left: Value, right: Value): Value = {
    val lAbsNum = typeHelper.ToInt32(left)
    val rAbsNum = typeHelper.ToUInt32(right)
    val resAbsNum = lAbsNum.bitURShift(rAbsNum)
    valueU(resAbsNum)
  }

  /* + */
  def bopPlus(left: Value, right: Value): Value = {
    def PValue2Tpl(pv: PValue): (PValue, PValue, PValue, PValue, PValue) = {
      (
        pvalueU(pv.undefval),
        pvalueU(pv.nullval),
        pvalueU(pv.boolval),
        pvalueU(pv.numval),
        pvalueU(pv.strval)
      )
    }

    val primLPV = typeHelper.ToPrimitive(left)
    val primRPV = typeHelper.ToPrimitive(right)
    (primLPV.strval.gamma, primRPV.strval.gamma) match {
      case (ConSetBot(), ConSetBot()) =>
        val (lAbsNum, rAbsNum) = (typeHelper.ToNumber(primLPV), typeHelper.ToNumber(primRPV))
        val resAbsNum = lAbsNum add rAbsNum
        valueU(resAbsNum)
      case (_, ConSetBot()) =>
        val (res1, res2, res3, res4, res5) = primRPV.foreach((abs: AbsDomain) => {
          primLPV.strval.concat(abs.toAbsString(utils.absString))
        })
        val resVal = valueU(res1 + res2 + res3 + res4 + res5)
        resVal + bopPlus(valueU(primLPV.copyWith(utils.absString.Bot)), valueU(primRPV))
      case (ConSetBot(), _) =>
        val (res1, res2, res3, res4, res5) = primLPV.foreach((abs: AbsDomain) => {
          abs.toAbsString(utils.absString).concat(primRPV.strval)
        })
        val resVal = valueU(pvalueU(res1 + res2 + res3 + res4 + res5))
        resVal + bopPlus(valueU(primLPV), valueU(primRPV.copyWith(utils.absString.Bot)))
      case (_, _) =>
        val (resR1, resR2, resR3, resR4, resR5) = primLPV.foreach((abs: AbsDomain) => {
          abs.toAbsString(utils.absString).concat(primRPV.strval)
        })
        val (resL1, resL2, resL3, resL4, resL5) = primRPV.foreach((abs: AbsDomain) => {
          primLPV.strval.concat(abs.toAbsString(utils.absString))
        })
        val resAbsStr = resR1 + resR2 + resR3 + resR4 + resR5 + resL1 + resL2 + resL3 + resL4 + resL5
        val resVal = valueU(resAbsStr)

        resVal + bopPlus(valueU(primLPV.copyWith(utils.absString.Bot)), valueU(primRPV.copyWith(utils.absString.Bot)))
    }
  }

  /* - */
  def bopMinus(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) sub typeHelper.ToNumber(right)
    valueU(resAbsNum)
  }

  /* * */
  def bopMul(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) mul typeHelper.ToNumber(right)
    valueU(resAbsNum)
  }

  /* / */
  def bopDiv(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) div typeHelper.ToNumber(right)
    valueU(resAbsNum)
  }

  /* % */
  def bopMod(left: Value, right: Value): Value = {
    val resAbsNum = typeHelper.ToNumber(left) mod typeHelper.ToNumber(right)
    valueU(resAbsNum)
  }

  /* == */
  private def bopEqHelp(left: Value, right: Value, objToPrimitive: (Value, String) => PValue): Value = {
    val leftPV = left.pvalue
    val rightPV = right.pvalue
    val locsetTest =
      if (!left.locset.isEmpty && !right.locset.isEmpty) {
        val intersect = left.locset.intersect(right.locset)
        if (intersect.isEmpty) utils.absBool.False
        else if (left.locset.size == 1 && right.locset.size == 1 && intersect.head.recency == Recent) utils.absBool.True
        else utils.absBool.Top
      } else utils.absBool.Bot
    val b1 = (leftPV.undefval === rightPV.undefval)(utils.absBool) +
      (leftPV.nullval === rightPV.nullval)(utils.absBool) +
      (leftPV.numval === rightPV.numval)(utils.absBool) +
      (leftPV.strval === rightPV.strval)(utils.absBool) +
      (leftPV.boolval === rightPV.boolval)(utils.absBool) +
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
        val rightNumVal = rightPV.strval.toAbsNumber(absNumber)
        (leftPV.numval === rightNumVal)(utils.absBool)
    }
    val b5 = (leftPV.strval.gammaSimple, rightPV.numval.gammaSimple) match {
      case (ConSimpleBot, _) | (_, ConSimpleBot) => utils.absBool.Bot
      case _ =>
        val leftNumVal = leftPV.strval.toAbsNumber(absNumber)
        (leftNumVal === rightPV.numval)(utils.absBool)
    }
    val b6 = leftPV.boolval.gammaSimple match {
      case ConSimpleBot =>
        val leftNumVal = leftPV.boolval.toAbsNumber(absNumber)
        val b61 = rightPV.numval.fold(utils.absBool.Bot)(rightNumVal => {
          (leftNumVal === rightNumVal)(utils.absBool)
        })
        val b62 = rightPV.strval.fold(utils.absBool.Bot)(rightStrVal => {
          val rightNumVal = rightStrVal.toAbsNumber(absNumber)
          (leftNumVal === rightNumVal)(utils.absBool)
        })
        val b63 = right.locset.size match {
          case 0 => utils.absBool.Bot
          case _ =>
            val rightNumVal = objToPrimitive(right, "Number").numval
            (leftNumVal === rightNumVal)(utils.absBool)
        }
        val b64 = rightPV.undefval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        val b65 = rightPV.nullval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        b61 + b62 + b63 + b64 + b65
      case ConSimpleTop => utils.absBool.Bot
    }

    val b7 = rightPV.boolval.gammaSimple match {
      case ConSimpleBot =>
        val rightNumVal = rightPV.boolval.toAbsNumber(absNumber)
        val b71 = leftPV.numval.fold(utils.absBool.Bot)(leftNumVal => {
          (leftNumVal === rightNumVal)(utils.absBool)
        })
        val b72 = leftPV.strval.fold(utils.absBool.Bot)(leftStrVal => {
          val leftNumVal = leftStrVal.toAbsNumber(absNumber)
          (leftNumVal === rightNumVal)(utils.absBool)
        })
        val b73 = left.locset.size match {
          case 0 => utils.absBool.Bot
          case _ =>
            val leftNumVal = objToPrimitive(left, "Number").numval
            (leftNumVal === rightNumVal)(utils.absBool)
        }
        val b74 = leftPV.undefval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        val b75 = leftPV.undefval.fold(utils.absBool.Bot)(_ => utils.absBool.False)
        b71 + b72 + b73 + b74 + b75
      case ConSimpleTop => utils.absBool.Bot
    }

    val b8 = right.locset.size match {
      case 0 => utils.absBool.Bot
      case _ =>
        val b81 = leftPV.numval.fold(utils.absBool.Bot)(leftNumVal => {
          val rightNumVal = objToPrimitive(right, "Number").numval
          (leftNumVal === rightNumVal)(utils.absBool)
        })
        val b82 = leftPV.strval.fold(utils.absBool.Bot)(leftStrVal => {
          val rightStrVal = objToPrimitive(right, "String").strval
          (leftStrVal === rightStrVal)(utils.absBool)
        })
        b81 + b82
    }

    val b9 = left.locset.size match {
      case 0 => utils.absBool.Bot
      case _ =>
        val b91 = rightPV.numval.fold(utils.absBool.Bot)(rightNumVal => {
          val leftNumVal = objToPrimitive(left, "Number").numval
          (leftNumVal === rightNumVal)(utils.absBool)
        })
        val b92 = rightPV.strval.fold(utils.absBool.Bot)(rightStrVal => {
          val leftStrVal = objToPrimitive(left, "String").strval
          (leftStrVal === rightStrVal)(utils.absBool)
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

    valueU(b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 + b9 + b10)
  }

  def bopEqBetter(h: Heap, left: Value, right: Value): Value = {
    bopEqHelp(left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, h, hint))
  }

  def bopEq(left: Value, right: Value): Value = {
    bopEqHelp(left, right, (value, hint) => typeHelper.ToPrimitive(value.locset, hint))
  }

  /* != */
  def bopNeq(left: Value, right: Value): Value = {
    val resAbsBool = bopEq(left, right).pvalue.boolval.gamma match {
      case ConSingleCon(true) => utils.absBool.False
      case ConSingleCon(false) => utils.absBool.True
      case ConSingleTop() => utils.absBool.Top
      case ConSingleBot() => utils.absBool.Bot
    }
    valueU(resAbsBool)
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
        else if (left.locset.size == 1 && right.locset.size == 1 && intersect.head.recency == Recent) utils.absBool.True
        else utils.absBool.Top
      } else utils.absBool.Bot
    val isSame =
      (left.pvalue.undefval === right.pvalue.undefval)(utils.absBool) +
        (left.pvalue.nullval === right.pvalue.nullval)(utils.absBool) +
        (left.pvalue.numval === right.pvalue.numval)(utils.absBool) +
        (left.pvalue.strval === right.pvalue.strval)(utils.absBool) +
        (left.pvalue.boolval === right.pvalue.boolval)(utils.absBool) +
        isLocsetSame
    valueU(isMultiType + isSame)
  }

  /* !== */
  def bopSNeq(left: Value, right: Value): Value = {
    val resAbsBool = bopSEq(left, right).pvalue.boolval.gamma match {
      case ConSingleCon(true) => utils.absBool.False
      case ConSingleCon(false) => utils.absBool.True
      case ConSingleTop() => utils.absBool.Top
      case ConSingleBot() => utils.absBool.Bot
    }
    valueU(resAbsBool)
  }

  private def bopCompareHelp(
    leftPV: PValue,
    rightPV: PValue,
    cmpAbsNum: (AbsNumber, AbsNumber) => AbsBool,
    cmpAbsStr: (AbsString, AbsString) => AbsBool
  ): Value = {
    (leftPV.strval.gammaSimple, rightPV.strval.gammaSimple) match {
      case (ConSimpleBot, _) | (_, ConSimpleBot) =>
        val leftAbsNum = typeHelper.ToNumber(leftPV)
        val rightAbsNum = typeHelper.ToNumber(rightPV)
        valueU(cmpAbsNum(leftAbsNum, rightAbsNum))
      case _ =>
        val leftPV2 = leftPV.copyWith(utils.absString.Bot)
        val rightPV2 = rightPV.copyWith(utils.absString.Bot)
        val resAbsBool = cmpAbsStr(leftPV.strval, rightPV.strval)
        valueU(resAbsBool) +
          bopCompareHelp(leftPV, rightPV2, cmpAbsNum, cmpAbsStr) +
          bopCompareHelp(leftPV2, rightPV, cmpAbsNum, cmpAbsStr)
    }
  }

  /* < */
  def bopLess(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => (leftAbsNum < rightAbsNum)(utils.absBool),
      (leftAbsStr, rightAbsStr) => (leftAbsStr < rightAbsStr)(utils.absBool))
  }

  /* > */
  def bopGreater(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => (rightAbsNum < leftAbsNum)(utils.absBool),
      (leftAbsStr, rightAbsStr) => (rightAbsStr < leftAbsStr)(utils.absBool))
  }

  /* <= */
  def bopLessEq(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        (leftAbsNum.gammaSingle, rightAbsNum.gammaSingle) match {
          case (ConSingleCon(n1), ConSingleCon(n2)) if n1.isNaN & n2.isNaN => utils.absBool.False
          case _ => (rightAbsNum < leftAbsNum)(utils.absBool).negate
        }
      },
      (leftAbsStr, rightAbsStr) => (rightAbsStr < leftAbsStr)(utils.absBool).negate)
  }

  /* >= */
  def bopGreaterEq(left: Value, right: Value): Value = {
    val leftPV = typeHelper.ToPrimitive(left)
    val rightPV = typeHelper.ToPrimitive(right)
    bopCompareHelp(leftPV, rightPV,
      (leftAbsNum, rightAbsNum) => {
        (leftAbsNum.gammaSingle, rightAbsNum.gammaSingle) match {
          case (ConSingleCon(n1), ConSingleCon(n2)) if n1.isNaN & n2.isNaN => utils.absBool.False
          case _ => (leftAbsNum < rightAbsNum)(utils.absBool).negate
        }
      },
      (leftAbsStr, rightAbsStr) => (leftAbsStr < rightAbsStr)(utils.absBool).negate)
  }
}
