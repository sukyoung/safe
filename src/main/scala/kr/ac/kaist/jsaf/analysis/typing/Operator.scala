/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object Operator {
  sealed abstract class AbsNumberPattern
  case object NumTopPattern extends AbsNumberPattern
  case object NumBotPattern extends AbsNumberPattern
  case object InfinityPattern extends AbsNumberPattern
  case object PosInfPattern extends AbsNumberPattern
  case object NegInfPattern extends AbsNumberPattern
  case object NaNPattern extends AbsNumberPattern
  case object UIntPattern extends AbsNumberPattern
  case object NUIntPattern extends AbsNumberPattern
  case class UIntSinglePattern(value : Double) extends AbsNumberPattern {
    override def toString() = value.toLong.toString
  }
  case class NUIntSinglePattern(value : Double) extends AbsNumberPattern {
    override def toString() = value.toString
  }

  def forMatch(v: AbsNumber): AbsNumberPattern = v.getAbsCase match {
    case AbsTop => NumTopPattern
    case AbsBot => NumBotPattern
    case AbsSingle => v.getSingle match {
      case _ if AbsNumber.isPosInf(v) => PosInfPattern
      case _ if AbsNumber.isNegInf(v) => NegInfPattern
      case _ if AbsNumber.isNaN(v) => NaNPattern
      case Some(n) if AbsNumber.isUIntSingle(v) => UIntSinglePattern(n)
      case Some(n) => NUIntSinglePattern(n)
      case _ => throw new InternalError("Impossible case.")
    }
    case AbsMulti =>
      if (AbsNumber.isInfinity(v)) InfinityPattern
      else if (AbsNumber.isUInt(v)) UIntPattern
      else NUIntPattern
  }

  /* unary operator */
  /* void */
  def uVoid(value:Value): Value = {
    Value(PValue(UndefTop), LocSetBot)
  }
  /* + */
  def uopPlus(value:Value): Value = {
    Value(Helper.toNumber(value.pvalue) + Helper.toNumber(Helper.objToPrimitive(value.locset, "Number")))
  }
  /* - */
  def uopMinus(value:Value): Value = {
    val oldValue = Helper.toNumber(value.pvalue) + Helper.toNumber(Helper.objToPrimitive(value.locset, "Number"))
    forMatch(oldValue) match {
      case NaNPattern =>  Value(NaN)
      case UIntSinglePattern(0) =>  Value(oldValue)
      case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(-n))
      case NUIntSinglePattern(n)=>
        val neg = -n
        val intnum = neg.toInt
        val diff:Double = neg - intnum.toDouble
        if ((diff == 0) && neg >= 0) Value(AbsNumber.alpha(neg))
        else Value(AbsNumber.alpha(neg))
      case UIntPattern =>  Value(NUInt)
      case NUIntPattern =>  Value(NumTop)
      case PosInfPattern =>  Value(NegInf)
      case NegInfPattern =>  Value(PosInf)
      case _ =>  Value(oldValue)
    }
  }
  /* ~ */
  def uopBitNeg(value:Value): Value = {
    val oldValue =	ToInt32(value) 
    forMatch(oldValue) match {
      case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(~(n.toInt)))
      case NUIntSinglePattern(n)=>  Value(AbsNumber.alpha(~(n.toInt)))
      case UIntPattern =>  Value(NUInt)
      case NUIntPattern =>  Value(UInt)
      case _ =>  Value(oldValue)
    }
  }
  /* ! */
  def uopNeg(value:Value): Value = {
    val oldValue = Helper.toBoolean(value)
    oldValue.getPair match {
      case (AbsSingle, Some(b)) => if (b) Value(BoolFalse) else Value(BoolTrue)
      case _ =>  Value(oldValue)
    }
  }
  /* binary operator */
  /* | */
  def bopBitOr (left: Value, right: Value): Value = {
    val lnum = ToInt32(left)
    val rnum = ToInt32(right)
    (forMatch(lnum), forMatch(rnum)) match {
      case (UIntSinglePattern(0), _) =>  Value(rnum)
      case (_, UIntSinglePattern(0)) =>  Value(lnum)
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1.toInt|n2.toInt))
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        if (n2>0) Value(AbsNumber.alpha(n1.toInt|n2.toInt))
        else Value(AbsNumber.alpha(n1.toInt|n2.toInt))
      case (UIntSinglePattern(n1), UIntPattern) =>  Value(UInt)
      case (UIntSinglePattern(n1), NUIntPattern) =>  Value(NumTop)
      case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>
        if (n1>0) Value(AbsNumber.alpha(n1.toInt|n2.toInt))
        else Value(AbsNumber.alpha(n1.toInt|n2.toInt))
      case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        if (n1>0 && n2>0) Value(AbsNumber.alpha(n1.toInt|n2.toInt))
        else Value(AbsNumber.alpha(n1.toInt|n2.toInt))
      case (NUIntSinglePattern(n1), UIntPattern) =>
        if (n1>0) Value(UInt)
        else Value(NUInt)
      case (NUIntSinglePattern(n1), NUIntPattern) => Value(NumTop)
      case (UIntPattern, UIntSinglePattern(n2)) =>  Value(UInt)
      case (UIntPattern, NUIntSinglePattern(n2)) =>
        if (n2>0) Value(UInt)
        else Value(NUInt)
      case (UIntPattern, UIntPattern) =>  Value(UInt)
      case _ =>  Value(NumTop)
    }
  }
  /* & */
  def bopBitAnd (left: Value, right: Value): Value = {
    val lnum = ToInt32(left)
    val rnum = ToInt32(right)
    (forMatch(lnum), forMatch(rnum)) match {
      case (_, UIntSinglePattern(0)) =>  Value(AbsNumber.alpha(0))
      case (UIntSinglePattern(0), _) =>  Value(AbsNumber.alpha(0))
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1.toInt&n2.toInt))
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1.toInt&n2.toInt))
      case (UIntSinglePattern(n1), _) =>  Value(UInt)
      case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1.toInt&n2.toInt))
      case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        if (n1<0 && n2<0) Value(AbsNumber.alpha(n1.toInt&n2.toInt))
        else Value(AbsNumber.alpha(n1.toInt&n2.toInt))
      case (NUIntSinglePattern(n1), UIntPattern) =>  Value(UInt)
      case (NUIntSinglePattern(n1), NUIntPattern) =>
        if (n1>0) Value(UInt)
        else Value(NumTop)
      case (UIntPattern, _) =>  Value(UInt)
      case (NUIntPattern, UIntSinglePattern(n2)) =>  Value(UInt)
      case (NUIntPattern, UIntPattern) =>  Value(UInt)
      case (NUIntPattern, NUIntSinglePattern(n2)) =>
        if (n2>0) Value(UInt)
        else Value(NumTop)
      case (NUIntPattern, NUIntPattern) =>  Value(NumTop)
      case _ =>  Value(NumTop)
    }
  }
  /* ^ */
  def bopBitXor (left: Value, right: Value): Value = {
    val lnum = ToInt32(left)
    val rnum = ToInt32(right)
    (forMatch(lnum), forMatch(rnum)) match {
      case (UIntSinglePattern(0), _) =>  Value(rnum)
      case (_, UIntSinglePattern(0)) =>  Value(lnum)
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1.toInt^n2.toInt))
      case (UIntSinglePattern(n1), UIntPattern) => Value(UInt)
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        if (n2>0) Value(AbsNumber.alpha(n1.toInt^n2.toInt))
        else Value(AbsNumber.alpha(n1.toInt^n2.toInt))
      case (UIntSinglePattern(n1), NUIntPattern) =>  Value(NumTop)
      case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>
        if (n1>0) Value(AbsNumber.alpha(n1.toInt^n2.toInt))
        else Value(AbsNumber.alpha(n1.toInt^n2.toInt))
      case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        if ((n1>0&&n2>0) || (n1<0&&n2<0)) Value(AbsNumber.alpha(n1.toInt^n2.toInt))
        else Value(AbsNumber.alpha(n1.toInt^n2.toInt))
      case (NUIntSinglePattern(n1), UIntPattern) =>
        if (n1>0) Value(UInt)
        else Value(NUInt)
      case (NUIntSinglePattern(n1), NUIntPattern) =>  Value(NumTop)
      case (UIntPattern, UIntSinglePattern(n2)) =>  Value(UInt)
      case (UIntPattern, UIntPattern) =>  Value(UInt)
      case (UIntPattern, NUIntSinglePattern(n2)) =>
        if (n2>0) Value(UInt)
        else Value(NUInt)
      case (UIntPattern, NUIntPattern) =>  Value(NumTop)
      case (NUIntPattern, _) =>  Value(NumTop)
      case _ =>  Value(NumTop)
    }
  }
  /* << */
  def bopLShift (left: Value, right: Value): Value = {
    val lnum = ToInt32(left)
    val rnum = ToUInt32(right)
    val shiftCount = forMatch(rnum) match {
      case UIntSinglePattern(n) =>  n.toInt&0x1F	// one shift count
      case NUIntSinglePattern(n) =>  n.toInt&0x1F	// one shift count
      case _ =>  0x20  // multi shift count
    }
    forMatch(lnum) match {
      case UIntSinglePattern(n) =>
        if (shiftCount!=0x20) {	// one shift count
          val res = n.toInt<<shiftCount
          if (res>=0) Value(AbsNumber.alpha(res))
          else Value(AbsNumber.alpha(res))
        }
        else Value(NumTop)	// multi shift count
      case NUIntSinglePattern(n) =>
        if (shiftCount!=0x20) {	// one shift count
          val res = n.toInt<<shiftCount
          if (res>=0) Value(AbsNumber.alpha(res))
          else Value(AbsNumber.alpha(res))          
        }
        else Value(NumTop)  // multi shift count
      case _ =>  Value(NumTop)
    }
  }
  /* >> */
  def bopRShift (left: Value, right: Value): Value = {
    val lnum = ToInt32(left)
    val rnum = ToUInt32(right)
    val shiftCount = forMatch(rnum) match {
      case UIntSinglePattern(n) =>	 n.toInt&0x1F  // one shift count
      case NUIntSinglePattern(n) =>  n.toInt&0x1F  // one shift count
      case _ =>  0x20  // multi shift count
    }
    forMatch(lnum) match {
      case UIntSinglePattern(n) =>
        if(shiftCount!=0x20) Value(AbsNumber.alpha(n.toInt>>shiftCount))	// one shift count
        else Value(NumTop)  // multi shift count
      case NUIntSinglePattern(n) =>
        if(shiftCount!=0x20) Value(AbsNumber.alpha(n.toInt>>shiftCount)) // one shift count
        else Value(NumTop)  // multi shift count        
      case UIntPattern|NUIntPattern =>  Value(lnum)
      case _ =>  Value(NumTop)
    }    
  }
  /* >>> */
  def bopURShift (left: Value, right: Value): Value = {
    val lnum = ToUInt32(left)
    val rnum = ToUInt32(right)
    val shiftCount = forMatch(rnum) match {
      case UIntSinglePattern(n) =>  n.toInt&0x1F  // one shift count
      case NUIntSinglePattern(n) =>  n.toInt&0x1F  // one shift count
      case _ =>  0x20  // multi shift count
    }
    forMatch(lnum) match {
      case UIntSinglePattern(n) =>
        if(shiftCount != 0x20) Value(AbsNumber.alpha(n.toInt>>>shiftCount))  // one shift count
        else Value(NumTop)  // multi shift count
      case NUIntSinglePattern(n) =>
        if(shiftCount != 0x20) Value(AbsNumber.alpha(n.toInt>>>shiftCount))  // one shift count
        else Value(NumTop)  // multi shift count
      case UIntPattern|NUIntPattern =>  Value(lnum)
      case _ =>  Value(NumTop)
    }
  }
  /* + */
  def bopPlus (left: Value, right: Value): Value = {
    val lpval = left.pvalue
    val rpval = right.pvalue
    val lprim = Helper.toPrimitive(left)
    val rprim = Helper.toPrimitive(right)

    def concatAbsStr(left:AbsString, right:AbsString):AbsString = {
      val str1 = left.gamma
      val str2 = right.gamma
      (str1, str2) match {
        case (Some(s1), Some(s2)) =>
          left.concat(right)
        case (Some(s1), None) =>
          if (right <= StrBot)
            StrBot
          else if (s1.size == 1 && s1.head == "")
            right
          else
            StrTop
        case (None, Some(s2)) =>
          if (left <= StrBot)
            StrBot
          else if (s2.size == 1 && s2.head == "")
            left
          else
            StrTop
        case _ =>
          if (left <= StrBot || right <= StrBot)
            StrBot
          else
            StrTop
      }
        /*
        case (NumStrSingle(s1), NumStrSingle(s2)) =>
          if (s1.equals("0")) AbsString.alpha(s1.concat(s2))
          else AbsString.alpha(s1.concat(s2))
        case (NumStrSingle(s1), OtherStrSingle(s2)) =>
          if (s2.equals("")) AbsString.alpha(s1)	// "1" + ""
          else AbsString.alpha(s1.concat(s2))
        case (NumStrSingle(s1), NumStr) =>  NumStr
        case (NumStrSingle(s1), OtherStr) =>  OtherStr
        case (OtherStrSingle(s1), NumStrSingle(s2)) =>
          if (s1.equals("")) AbsString.alpha(s2)	// " + "1"
          else AbsString.alpha(s1.concat(s2))
        case (OtherStrSingle(s1), OtherStrSingle(s2))=>  AbsString.alpha(s1.concat(s2))
        case (OtherStrSingle(s1), OtherStr) =>  OtherStr
        case (OtherStrSingle(s1), NumStr) =>
          if (s1.equals("")) NumStr
          else OtherStr
        case (OtherStr, OtherStrSingle(s2)) =>  OtherStr
        case (OtherStr, OtherStr) =>  OtherStr
        case (NumStr, _) =>  StrTop	// NumStr={0, ...}, s2={""}
        case (_, _) =>  StrTop */
    }
    
    (lprim._5, rprim._5) match {
      case (StrBot, StrBot) =>
        val (lnum, rnum) = (Helper.toNumber(lprim), Helper.toNumber(rprim))
        (forMatch(lnum), forMatch(rnum)) match {
          case (NumBotPattern, _)|(_, NumBotPattern) =>  Value(NumBot)
          case (NaNPattern, _) | (_, NaNPattern) =>  Value(NaN)
          case (InfinityPattern, InfinityPattern) =>  Value(NumTop)	// {NaN, PosInf, NegInf}
          case (InfinityPattern, _) | (_, InfinityPattern) =>  Value(Infinity)
          case (PosInfPattern, NegInfPattern)|(NegInfPattern, PosInfPattern) =>  Value(NaN)
          case (PosInfPattern, _) | (_, PosInfPattern) =>  Value(PosInf)
          case (NegInfPattern, _) | (_, NegInfPattern) =>  Value(NegInf)
          case (UIntSinglePattern(0), _) =>  Value(rnum)
          case (_, UIntSinglePattern(0)) =>  Value(lnum)
          case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1+n2))
          case (UIntSinglePattern(n), UIntPattern) =>  Value(UInt)
          case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
            val sum = n1+n2
            val sumInt = sum.toInt
            if (sum >= 0 && sum-sumInt==0.0) Value(AbsNumber.alpha(sum))
            else Value(AbsNumber.alpha(sum))
          case (UIntPattern, UIntSinglePattern(n2)) =>  Value(UInt)
          case (UIntPattern, NUIntSinglePattern(n2)) =>  Value(NumTop)
          case (UIntPattern, UIntPattern) =>  Value(UInt)
          case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
            val sum = n1+n2
            val sumInt = sum.toInt
            if (sum >= 0 && sum-sumInt==0.0) Value(AbsNumber.alpha(sum))
            else Value(AbsNumber.alpha(sum))
          case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>
            val sum = n1+n2
            val sumInt = sum.toInt
            if (sum >= 0 && sum-sumInt==0.0) Value(AbsNumber.alpha(sum))
            else Value(AbsNumber.alpha(sum))
          case (NUIntSinglePattern(n1), UIntPattern) =>
            if (n1>=0 && (n1-n1.toInt)==0.0) Value(UInt)
            else Value(NumTop)
          case (_, _) =>  Value(NumTop)
        }
      case (l, StrBot) =>
        val res1 = concatAbsStr(l, Helper.toString(PValue(rprim._1)))
        val res2 = concatAbsStr(l, Helper.toString(PValue(rprim._2)))
        val res3 = concatAbsStr(l, Helper.toString(PValue(rprim._3)))
        val res4 = concatAbsStr(l, Helper.toString(PValue(rprim._4)))
        val res5 = concatAbsStr(l, Helper.toString(PValue(rprim._5))) 
        Value(res1 + res2 + res3 + res4 + res5) + bopPlus(Value(PValue(lprim._1, lprim._2, lprim._3, lprim._4, StrBot)), Value(rprim))
      case (StrBot, r) =>
        val res1 = concatAbsStr(Helper.toString(PValue(lprim._1)), r)
        val res2 = concatAbsStr(Helper.toString(PValue(lprim._2)), r)
        val res3 = concatAbsStr(Helper.toString(PValue(lprim._3)), r)
        val res4 = concatAbsStr(Helper.toString(PValue(lprim._4)), r)
        val res5 = concatAbsStr(Helper.toString(PValue(lprim._5)), r)
        Value(res1 + res2 + res3 + res4 + res5) + bopPlus(Value(lprim), Value(PValue(rprim._1, rprim._2, rprim._3, rprim._4, StrBot)))
      case (l, r) =>
        val resR1 = concatAbsStr(Helper.toString(PValue(lprim._1)), r)
        val resR2 = concatAbsStr(Helper.toString(PValue(lprim._2)), r)
        val resR3 = concatAbsStr(Helper.toString(PValue(lprim._3)), r)
        val resR4 = concatAbsStr(Helper.toString(PValue(lprim._4)), r)
        val resR5 = concatAbsStr(Helper.toString(PValue(lprim._5)), r)
        
        val resL1 = concatAbsStr(l, Helper.toString(PValue(rprim._1)))
        val resL2 = concatAbsStr(l, Helper.toString(PValue(rprim._2)))
        val resL3 = concatAbsStr(l, Helper.toString(PValue(rprim._3)))
        val resL4 = concatAbsStr(l, Helper.toString(PValue(rprim._4)))
        val resL5 = concatAbsStr(l, Helper.toString(PValue(rprim._5)))
        Value(resR1 + resR2 + resR3 + resR4 + resR5 + resL1 + resL2 + resL3 + resL4 + resL5) + bopPlus(Value(PValue(lprim._1, lprim._2, lprim._3, lprim._4, StrBot)), Value(PValue(rprim._1, rprim._2, rprim._3, rprim._4, StrBot))) 
    }
  }
  /* - */
  def bopMinus (left: Value, right: Value): Value = {
    val lnum = Helper.toNumber(left.pvalue) + Helper.toNumber(Helper.objToPrimitive(left.locset, "Number"))  // TODO : toNumber function for obj was not implemented yet.
    val rnum = Helper.toNumber(right.pvalue) + Helper.toNumber(Helper.objToPrimitive(right.locset, "Number"))
    // bopPlus(Value(lnum), uopMinus(Value(rnum)))
    (forMatch(lnum), forMatch(rnum)) match {
      case (NumBotPattern, _)|(_, NumBotPattern) =>  Value(NumBot)
      case (NaNPattern, _)|(_, NaNPattern) =>  Value(NaN)
      case (InfinityPattern, _)|(_, InfinityPattern) =>  Value(NumTop)
      case (PosInfPattern, PosInfPattern)|(NegInfPattern, NegInfPattern) =>  Value(NaN)
      case (PosInfPattern, _)|(_, NegInfPattern) =>  Value(PosInf)
      case (NegInfPattern, _)|(_, PosInfPattern) =>  Value(NegInf)
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>
        val res = n1-n2
        if (res >= 0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1-n2
        if (((res-res.toInt)==0.0) && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1-n2))
      case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1-n2
        if (((res-res.toInt)==0.0) && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (NUIntSinglePattern(n1), UIntPattern) =>  Value(NUInt)
      case (UIntPattern, NUIntSinglePattern(n2)) =>
        if (n2<0 && (n2-n2.toInt)==0.0) Value(UInt)
        else Value(NUInt)
      case (UIntPattern, NUIntPattern) =>  Value(NumTop)
      case (NUIntPattern, UIntSinglePattern(n2)) =>  Value(NUInt)
      case (NUIntPattern, UIntPattern) =>  Value(NUInt)
      case (_, _) =>  Value(NumTop)
    }
  }
  /* * */
  def bopMul (left: Value, right: Value) = {
    val leftNum = Helper.toNumber(left.pvalue) + Helper.toNumber(Helper.objToPrimitive(left.locset, "Number"))  // TODO : toNumber function for obj was not implemented yet.
    val rightNum = Helper.toNumber(right.pvalue) + Helper.toNumber(Helper.objToPrimitive(right.locset, "Number"))
    (forMatch(leftNum), forMatch(rightNum)) match {
      case (NumBotPattern, _)|(_, NumBotPattern) =>  Value(NumBot)
      /* 11.5.1 first */
      case (NaNPattern, _)|(_, NaNPattern) =>  Value(NaN)
      /* 11.5.1 third */
      case (PosInfPattern|NegInfPattern|InfinityPattern, UIntSinglePattern(0)) =>  Value(NaN)
      case (UIntSinglePattern(0), PosInfPattern|NegInfPattern|InfinityPattern) =>  Value(NaN)
      /* 11.5.1 fourth */
      case (PosInfPattern, PosInfPattern)|(NegInfPattern, NegInfPattern) =>  Value(PosInf)
      case (PosInfPattern, NegInfPattern)|(NegInfPattern, PosInfPattern) =>  Value(NegInf)
      case (InfinityPattern, PosInfPattern|NegInfPattern) =>  Value(Infinity)
      case (InfinityPattern, InfinityPattern) =>  Value(Infinity)
      case (PosInfPattern|NegInfPattern, InfinityPattern) =>  Value(Infinity)
      /* 11.5.1 fifth */
      case (PosInfPattern, UIntSinglePattern(_)) =>  Value(PosInf)
      case (PosInfPattern, UIntPattern)|(UIntPattern, PosInfPattern) =>  Value(NumTop)	// NaN or PosInf
      case (PosInfPattern, NUIntSinglePattern(n)) =>
      	if (n>0) Value(PosInf)
      	else Value(NegInf)
      case (PosInfPattern, NUIntPattern)|(NUIntPattern, PosInfPattern) =>  Value(Infinity)
      case (UIntSinglePattern(_), PosInfPattern) =>  Value(PosInf)
      case (NUIntSinglePattern(n), PosInfPattern) =>
      	if (n>0) Value(PosInf)
      	else Value(NegInf)
      case (NegInfPattern, UIntSinglePattern(_)) =>  Value(NegInf)
      case (NegInfPattern, UIntPattern)|(UInt, NegInfPattern) =>  Value(NumTop)	// NaN or NegInf
      case (NegInfPattern, NUIntSinglePattern(n)) =>
      	if (n>0) Value(NegInf)
      	else Value(PosInf)
      case (NegInfPattern, NUIntPattern)|(NUIntPattern, NegInfPattern) =>  Value(Infinity)
      case (UIntSinglePattern(_), NegInfPattern) =>  Value(NegInf)
      case (NUIntSinglePattern(n), NegInfPattern) =>
      	if (n>0) Value(NegInf)
      	else Value(PosInf)
      case (InfinityPattern, UIntPattern)|(UIntPattern, InfinityPattern) =>  Value(NumTop)	// NaN or Infinity
      case (InfinityPattern, UIntSinglePattern(_)) =>  Value(Infinity) // 0 was filtered
      case (InfinityPattern, NUIntSinglePattern(_)) =>  Value(Infinity)
      case (InfinityPattern, NUIntPattern)|(NUIntPattern, InfinityPattern) =>  Value(Infinity)
      case (UIntSinglePattern(_), InfinityPattern) =>  Value(Infinity) // 0 was filtered
      case (NUIntSinglePattern(_), InfinityPattern) =>  Value(Infinity)

      /* 11.5.1 sixth */
      case (UIntSinglePattern(0), _) =>  Value(AbsNumber.alpha(0))
      case (_, UIntSinglePattern(0)) =>  Value(AbsNumber.alpha(0))
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1*n2))
      case (UIntSinglePattern(n1), UIntPattern) =>  Value(UInt)
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1*n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (UIntSinglePattern(n1), NUIntPattern) =>  Value(NUInt)
      case (UIntPattern, UIntSinglePattern(n2)) =>  Value(UInt)
      case (UIntPattern, UIntPattern) =>  Value(UInt)
      case (UIntPattern, NUIntSinglePattern(n2)) =>  Value(NumTop) // UInt(0) or NUInt
      case (UIntPattern, NUIntPattern) =>  Value(NumTop) // UInt(0) or NUInt      
      case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>
        val res = n1*n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (NUIntSinglePattern(n1), UIntPattern) =>  Value(NumTop)	// UInt(0) of NUInt
      case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1*n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (NUIntSinglePattern(n1), NUIntPattern) =>  Value(NumTop)      
      case (NUIntPattern, _) =>  Value(NumTop)
      case _ =>  Value(NumTop)
    }
  }
  /* / */
  def bopDiv(left: Value, right: Value): Value = {
    val leftNum = Helper.toNumber(left.pvalue) + Helper.toNumber(Helper.objToPrimitive(left.locset, "Number"))  // TODO : toNumber function for obj was not implemented yet.
    val rightNum = Helper.toNumber(right.pvalue) + Helper.toNumber(Helper.objToPrimitive(right.locset, "Number"))
    (forMatch(leftNum), forMatch(rightNum)) match {
      case (NumBotPattern, _)|(_, NumBotPattern) =>  Value(NumBot)
      /* 11.5.2 first */
      case (NaNPattern, _)|(_, NaNPattern) =>  Value(NaN)
      /* 11.5.2 third */
      case (PosInfPattern|NegInfPattern|InfinityPattern, PosInfPattern|NegInfPattern|InfinityPattern) =>  Value(NaN)
      /* 11.5.2 fourth */
      case (PosInfPattern, UIntSinglePattern(0)) =>  Value(PosInf)
      case (NegInfPattern, UIntSinglePattern(0)) =>  Value(NegInf)
      case (InfinityPattern, UIntSinglePattern(0)) =>  Value(Infinity)
      /* 11.5.2 fifth */
      case (PosInfPattern, UIntSinglePattern(_)) =>  Value(PosInf)
      case (PosInfPattern, UIntPattern) =>  Value(PosInf)
      case (PosInfPattern, NUIntSinglePattern(n)) =>
      	if (n>0) Value(PosInf)
      	else Value(NegInf)
      case (PosInfPattern, NUIntPattern) =>  Value(Infinity)
      case (NegInfPattern, UIntSinglePattern(_)) =>  Value(NegInf)
      case (NegInfPattern, UIntPattern) =>  Value(NegInf)
      case (NegInfPattern, NUIntSinglePattern(n)) =>
      	if (n>0) Value(NegInf)
      	else Value(PosInf)
      case (NegInfPattern, NUIntPattern) =>  Value(Infinity)
      case (InfinityPattern, _) =>  Value(Infinity)
      /* 11.5.2 sixth */
      case (_, PosInfPattern) =>  Value(AbsNumber.alpha(0))
      case (_, NegInfPattern) =>  Value(AbsNumber.alpha(0))
      case (_, InfinityPattern) =>  Value(AbsNumber.alpha(0))
      /* 11.5.2  seventh */
      case (UIntSinglePattern(0), UIntSinglePattern(0)) =>  Value(NaN)
      case (UIntSinglePattern(0), _) =>  Value(AbsNumber.alpha(0))
      /* 11.5.2  eighth */
      case (UIntSinglePattern(n), UIntSinglePattern(0)) =>  Value(PosInf)
      case (UIntPattern, UIntSinglePattern(0)) =>  Value(NumTop)	// UInt may have 0
      case (NUIntSinglePattern(n), UIntSinglePattern(0)) =>
      	if (n>0) Value(PosInf)
      	else Value(NegInf)
      case (NUIntPattern, UIntSinglePattern(0)) =>  Value(Infinity)
      /* 11.5.2  ninth */
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>
        val res = n1/n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (UIntSinglePattern(n1), UIntPattern) =>  Value(NumTop)	// UInt may have 0
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1/n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (UIntSinglePattern(n1), NUIntPattern) =>  Value(NumTop)      
      case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1/n2))
      case (NUIntSinglePattern(n1), UIntPattern) =>  Value(NumTop)	// UInt may have 0 : Infinity
      case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1/n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (NUIntSinglePattern(n1), NUIntPattern) =>  Value(NumTop)      
      case (UIntPattern, _) =>  Value(NumTop)	// UInt may have 0      
      case (NUIntPattern, UIntSinglePattern(n2)) =>  Value(NUInt)
      case (NUIntPattern, _) =>  Value(NumTop)	// UInt may have 0
      case _ =>  Value(NumTop)
    }
  }
  /* % */
  def bopMod(left: Value, right: Value): Value = {
    val leftNum = Helper.toNumber(left.pvalue) + Helper.toNumber(Helper.objToPrimitive(left.locset, "Number"))  // TODO : toNumber function for obj was not implemented yet.
    val rightNum = Helper.toNumber(right.pvalue) + Helper.toNumber(Helper.objToPrimitive(right.locset, "Number"))
    (forMatch(leftNum), forMatch(rightNum)) match {
      case (NumBotPattern, _)|(_, NumBotPattern) =>  Value(NumBot)
       /* 11.5.3 first */
      case (NaNPattern, _)|(_, NaNPattern) =>  Value(NaN)
      /* 11.5.3 third */
      case (PosInfPattern|NegInfPattern|InfinityPattern, _) =>  Value(NaN)
      case (_, UIntSinglePattern(0)) =>  Value(NaN)
      /* 11.5.3 fifth */
      case (UIntSinglePattern(0), _) =>  Value(AbsNumber.alpha(0))
      /* 11.5.3 fourth */
      case (_, PosInfPattern|NegInfPattern|InfinityPattern) =>  Value(leftNum)
      /* 11.5.3 sixth */
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(n1%n2))
      case (UIntSinglePattern(n1), UIntPattern) =>  Value(NumTop)	// UInt may have 0
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1%n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (UIntSinglePattern(n1), NUIntPattern) =>  Value(NumTop)
      case (UIntPattern, UIntSinglePattern(n2)) =>  Value(UInt)
      case (UIntPattern, _) =>  Value(NumTop)	// 0%0 = NaN
      case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>
        val res = n1%n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        val res = n1%n2
        if ((res-res.toInt)==0.0 && res>=0) Value(AbsNumber.alpha(res))
        else Value(AbsNumber.alpha(res))
      case (NUIntSinglePattern(n1), _) =>  Value(NumTop)
      case (NUIntPattern, UIntSinglePattern(n2)) =>  Value(NUInt)
      case (NUIntPattern, _) =>  Value(NumTop)
      case _ =>  Value(NumTop)
    }
  }
  /* == */
  def bopEq(left: Value, right: Value): Value = {
    // 1
    val b1 =
      // a 
      (left._1._1 === right._1._1) +
      // b
      (left._1._2 === right._1._2) +
      // c
      (left._1._4 === right._1._4) +
      // d
      (left._1._5 === right._1._5) +
      // e
      (left._1._3 === right._1._3) +
      // f
      (if (!left._2.isEmpty && !right._2.isEmpty) {
        val intersect = left._2.intersect(right._2) 
        if (intersect.isEmpty) BoolFalse
        else if (left._2.size == 1 && right._2.size == 1 && isRecentLoc(intersect.head)) BoolTrue
        else BoolTop}
      else BoolBot)
    // 2
    val b2 = 
      if (NullTop <= left._1._2 && UndefTop <= right._1._1) BoolTrue
      else BoolBot
    // 3
    val b3 = 
      if (UndefTop <= left._1._1 && NullTop <= right._1._2) BoolTrue
      else BoolBot
    // 4
    val b4 = 
      if (left._1._4 </ NumBot && right._1._5 </ StrBot)
        left._1._4 === Helper.toNumber(PValue(right._1._5))
      else BoolBot
    // 5
    val b5 = 
      if (left._1._5 </ StrBot && right._1._4 </ NumBot)
        Helper.toNumber(PValue(left._1._5)) === right._1._4
      else BoolBot
    // 6
    val b6 = 
      if (left._1._3 </ BoolBot) {
        val num = Helper.toNumber(PValue(left._1._3))
        val b6_1 =
          if (right._1._4 </ NumBot)
            num === right._1._4
          else
            BoolBot
        val b6_4 =
          if (right._1._5 </ StrBot)
            num === Helper.toNumber(PValue(right._1._5))
          else
            BoolBot
        val b6_8 =
          if (!right._2.isEmpty)
            num === Helper.objToPrimitive(right._2, "Number")._4
          else
            BoolBot
        val b6_10 =
          if (right._1._1 </ UndefBot || right._1._2 </ NullBot)
            BoolFalse
          else
            BoolBot
        b6_1 + b6_4 + b6_8 + b6_10
      }
      else
        BoolBot
    // 7
    val b7 = 
      if (right._1._3 </ BoolBot) {
        val num = Helper.toNumber(PValue(right._1._3))
        val b7_1 =
          if (left._1._4 </ NumBot)
            left._1._4 === num
          else
            BoolBot
        val b7_4 =
          if (left._1._5 </ StrBot)
            Helper.toNumber(PValue(left._1._5)) === num
          else
            BoolBot
        val b7_8 =
          if (!left._2.isEmpty)
            Helper.objToPrimitive(left._2, "Number")._4 === num
          else
            BoolBot
        val b7_10 =
          if (left._1._1 </ UndefBot || left._1._2 </ NullBot)
            BoolFalse
          else
            BoolBot
        b7_1 + b7_4 + b7_8 + b7_10
      }
      else
        BoolBot
    // 8
    val b8 =
      if (!right._2.isEmpty) {
        val b8_num =
          if (left._1._4 </ NumBot)
            left._1._4 === Helper.objToPrimitive(right._2, "Number")._4
          else
            BoolBot
        val b8_str =
          if (left._1._5 </ StrBot)
            left._1._5 === Helper.objToPrimitive(right._2, "String")._5
          else
            BoolBot
        b8_num + b8_str
      }
      else
        BoolBot
    // 9
    val b9 =
      if (!left._2.isEmpty) {
        val b9_num =
          if (right._1._4 </ NumBot)
            right._1._4 === Helper.objToPrimitive(left._2, "Number")._4
          else
            BoolBot
        val b9_str =
          if (right._1._5 </ StrBot)
            right._1._5 === Helper.objToPrimitive(left._2, "String")._5
          else
            BoolBot
        b9_num + b9_str
      }
      else
        BoolBot
    // 10
    val b10 =
      if (   (   (left._1._1 </ UndefBot || left._1._2 </ NullBot) 
              && (right._1._4 </ NumBot || right._1._5 </ StrBot || !right._2.isEmpty))
          || (   (right._1._1 </ UndefBot || right._1._2 </ NullBot) 
              && (left._1._4 </ NumBot || left._1._5 </ StrBot || !left._2.isEmpty)))
        BoolFalse
      else
        BoolBot
    Value(b1 + b2 + b3 + b4 + b5 + b6 + b7 + b8 + b9 + b10)
  }
  
  /* != */
  def bopNeq(left: Value, right: Value): Value = {
    bopEq(left,right).pvalue._3.getPair match {
      case (AbsSingle, Some(b)) => if (b) Value(BoolFalse) else Value(BoolTrue)
      case (AbsTop, _) => Value(BoolTop)
      case (AbsBot, _) => Value(BoolBot)
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
  }
  
  /* === */
  def bopSEq(left: Value, right: Value): Value = {
    // 1
    val b_type =
      if ((left + right).typeCount > 1)
        BoolFalse
      else
        BoolBot
    val b_same =
      // 2
      (left._1._1 === right._1._1) +
      // 3
      (left._1._2 === right._1._2) +
      // 4
      (left._1._4 === right._1._4) +
      // 5
      (left._1._5 === right._1._5) +
      // 6
      (left._1._3 === right._1._3) +
      // 7
      (if (!left._2.isEmpty && !right._2.isEmpty) {
        val intersect = left._2.intersect(right._2) 
        if (intersect.isEmpty) BoolFalse
        else if (left._2.size == 1 && right._2.size == 1 && isRecentLoc(intersect.head)) BoolTrue
        else BoolTop}
      else BoolBot)
    Value(b_type + b_same)
  }
  
  /* !== */
  def bopSNeq(left: Value, right: Value): Value = {
    bopSEq(left, right).pvalue._3.getPair match {
      case (AbsSingle, Some(b)) => if (b) Value(BoolFalse) else Value(BoolTrue)
      case (AbsTop, _) => Value(BoolTop)
      case (AbsBot, _) => Value(BoolBot)
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
  }
  /* < */
  def bopLess(left: Value, right: Value): Value = {
   	_bopLess(Helper.toPrimitive(left), Helper.toPrimitive(right))
  }
  def _bopLess(px:PValue, py:PValue): Value = {
    (px.strval, py.strval) match {
      case (StrBot, _) | (_, StrBot) =>
        val nx = Helper.toNumber(px)
        val ny = Helper.toNumber(py)
        compLessNumNum(nx, ny) match {
          case PValue(UndefTop,_,_,_,_) =>  Value(BoolFalse)
          case b =>  Value(b)
        }
      case (s1, s2) =>  // String < String
        val lnext = PValue(px._1, px._2, px._3, px._4, StrBot)
        val rnext = PValue(py._1, py._2, py._3, py._4, StrBot)
      	Value(compLessStrStr(s1, s2)) + _bopLess(px, rnext) + _bopLess(lnext, py) 
    }
  }
  /* > */
  def bopGreater(left: Value, right: Value): Value = {
   	_bopGreater(Helper.toPrimitive(left), Helper.toPrimitive(right))
  }
  def _bopGreater(px:PValue, py:PValue): Value = {
    (py.strval, px.strval) match {
      case (StrBot, _) | (_, StrBot) =>
        val nx = Helper.toNumber(px)
        val ny = Helper.toNumber(py)
        compLessNumNum(ny, nx) match {
          case PValue(UndefTop,_,_,_,_) =>  Value(BoolFalse)
          case b =>  Value(b)
        }
      case (s2, s1) => { // String < String
        val lnext = PValue(px._1, px._2, px._3, px._4, StrBot)
        val rnext = PValue(py._1, py._2, py._3, py._4, StrBot)
        Value(compLessStrStr(s2, s1)) + _bopGreater(lnext, py) + _bopGreater(px, rnext)
      }
    }
  }

  /* <= */
  def bopLessEq(left: Value, right: Value): Value = {
   	_bopLessEq(Helper.toPrimitive(left), Helper.toPrimitive(right))
  }
  def _bopLessEq(px:PValue, py:PValue): Value = {
    (py.strval, px.strval) match {
      case (StrBot, _) | (_, StrBot) =>
        val nx = Helper.toNumber(px)
        val ny = Helper.toNumber(py)
        compLessNumNum(ny, nx) match {
          case PValue(UndefTop,_,_,_,_) =>  Value(BoolFalse)
          case PValue(_, _, BoolTrue, _, _) =>  Value(BoolFalse)
          case PValue(_, _, BoolFalse, _, _) =>  Value(BoolTrue)
          case n =>  Value(n)
        }
      case (s2, s1) =>	// String < String
        val lnext = PValue(px._1, px._2, px._3, px._4, StrBot)
        val rnext = PValue(py._1, py._2, py._3, py._4, StrBot)
        val absbool = compLessStrStr(s2, s1)
        val strRes = absbool.getPair match {
          case (AbsSingle, Some(b)) => if (b) Value(BoolFalse) else Value(BoolTrue)
          case _ =>  Value(absbool)
        }
        strRes + _bopLessEq(lnext, py) + _bopLessEq(px, rnext)
    }
  }

  /* >= */
  def bopGreaterEq(left: Value, right: Value): Value = {
   	_bopGreaterEq(Helper.toPrimitive(left), Helper.toPrimitive(right))
  }
  def _bopGreaterEq(px:PValue, py:PValue):Value = {
    (px.strval, py.strval) match {
      case (StrBot, _) | (_, StrBot)=>
        val nx = Helper.toNumber(px)
        val ny = Helper.toNumber(py)
        compLessNumNum(nx, ny) match {
          case PValue(UndefTop,_,_,_,_) =>  Value(BoolFalse)
          case PValue(_, _, BoolTrue, _, _) =>  Value(BoolFalse)
          case PValue(_, _, BoolFalse, _, _) =>  Value(BoolTrue)
          case n =>  Value(n)
        }
      case (s1, s2) =>	// String < String
        val lnext = PValue(px._1, px._2, px._3, px._4, StrBot)
        val rnext = PValue(py._1, py._2, py._3, py._4, StrBot)
        val absbool = compLessStrStr(s1, s2)
        val strRes = absbool.getPair match {
          case (AbsSingle, Some(b)) => if (b) Value(BoolFalse) else Value(BoolTrue)
          case _ =>  Value(absbool)
        }
        strRes + _bopGreaterEq(lnext, py) + _bopGreaterEq(px, rnext)
    }
  }

  private def compLessStrStr(px:AbsString, py:AbsString): AbsBool = {
    val str1 = px.gamma
    val str2 = py.gamma
    (str1, str2) match {
      case (Some(s1), Some(s2)) =>
        s1.foldLeft[AbsBool](BoolBot)((r1, x) => r1 + s2.foldLeft[AbsBool](BoolBot)((r2, y) => r2 + AbsBool.alpha(x < y)))
      case _ =>
        if (px <= StrBot || py <= StrBot)
          BoolBot
        else
          BoolTop
    }
    /*
    (px, py) match {
      case (NumStrSingle(s1), NumStrSingle(s2)) =>
        if (s1.compareTo(s2)<0) BoolTrue
        else BoolFalse
      case (NumStrSingle(s1), OtherStrSingle(s2)) =>
        if (s1.compareTo(s2)<0) BoolTrue
        else BoolFalse
	  case (OtherStrSingle(s1), NumStrSingle(s2)) =>
	    if (s1.compareTo(s2)<0) BoolTrue
	    else BoolFalse
	  case (OtherStrSingle(s1), OtherStrSingle(s2)) =>
	    if (s1.compareTo(s2)<0) BoolTrue
	    else BoolFalse
	  case (OtherStrSingle(n1), NumStr) =>
	    if (n1.compareTo("0")<0) BoolTrue
	    else BoolTop
	  case (NumStr, NumStrSingle(n2)) =>
	    if (n2.compareTo("0")==0) BoolFalse
	    else BoolTop
	  case (NumStr, OtherStrSingle(n2)) =>
	    if (n2.compareTo("0")<0) BoolFalse
	    else BoolTop
	  case _ =>  BoolTop
	} */
  }
  
  // 11.8.5 The Abstract Relational Comparison Algorithm
  def compLessNumNum(nx:AbsNumber, ny:AbsNumber):PValue = {
    // 3.c.
    (forMatch(nx), forMatch(ny)) match {
      case (NumBotPattern, _) =>  PValue(BoolBot)
      case (_, NumBotPattern) =>  PValue(BoolBot)
      // 11.8.5.3.c, 11.8.5.3.d
      case (NaNPattern, _) | (_, NaNPattern) =>  PValue(UndefTop)
      // 11.8.5.3.e
      case (PosInfPattern, PosInfPattern) | (NegInfPattern, NegInfPattern) =>  PValue(BoolFalse)
      // 11.8.5.3.h, 11.8.5.3.i, 11.8.5.3.j, 11.8.5.3.k
      case (InfinityPattern, NegInfPattern) | (PosInfPattern, InfinityPattern) =>  PValue(BoolFalse)
      case (InfinityPattern, _) | (_, InfinityPattern) =>  PValue(BoolTop)
      case (PosInfPattern, _) =>  PValue(BoolFalse)
      case (_, PosInfPattern) =>  PValue(BoolTrue)
      case (NegInfPattern, _) =>  PValue(BoolTrue)
      case (_, NegInfPattern) =>  PValue(BoolFalse)
      // 11.8.5.3.l
      case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>
        if (n1<n2) PValue(BoolTrue)
        else PValue(BoolFalse)
      case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        if (n1<n2) PValue(BoolTrue)
        else PValue(BoolFalse)
	  case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
        if (n1<n2) PValue(BoolTrue)
        else PValue(BoolFalse)
	  case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>
	    if (n1<n2) PValue(BoolTrue)
	    else PValue(BoolFalse)
	  case (NUIntSinglePattern(n1), UIntPattern) =>
	    if (n1<0) PValue(BoolTrue)
	    else PValue(BoolTop)
	  case (UIntPattern, UIntSinglePattern(n2)) =>
	    if (n2==0) PValue(BoolFalse)
	    else PValue(BoolTop)
	  case (UIntPattern, NUIntSinglePattern(n2)) =>
	    if (n2<0) PValue(BoolFalse)
	    else PValue(BoolTop)
	  case _ =>  PValue(BoolTop)
	}
  }

  // 5.2 Algorithm Conventions. The notation "x modulo y" computes ...
  def modulo(x:Double, y:Long):Long = {
    val result = math.abs(x.toLong) % math.abs(y)
    if(math.signum(x) < 0) return math.signum(y) * (math.abs(y) - result)
    math.signum(y) * result
  }

  def ToInteger(value: Value): AbsNumber = {
    val number = Helper.toNumber(Helper.toPrimitive(value))
    forMatch(number) match {
      case NaNPattern => AbsNumber.alpha(0)
      case InfinityPattern | PosInfPattern | NegInfPattern | UIntSinglePattern(_) => number
      case NUIntSinglePattern(n) => AbsNumber.alpha(math.signum(n)*math.floor(math.abs(n)))
      case UIntPattern => UInt
      case NUIntPattern => NumTop
      case NumBotPattern => NumBot
      case NumTopPattern => NumTop
    }
  }

  def ToInt32(value:Value):AbsNumber = {
    val pv = value.pvalue
    val number = Helper.toNumber(pv) + Helper.toNumber(Helper.objToPrimitive(value.locset, "Number"))
    forMatch(number) match {
      case NaNPattern | PosInfPattern | NegInfPattern | InfinityPattern =>  AbsNumber.alpha(0)
      case UIntSinglePattern(n) =>  AbsNumber.alpha(n)        
      case NUIntSinglePattern(n)=>
       val posInt = math.signum(n)*math.floor(math.abs(n))
        val int32bit = modulo(posInt, 0x100000000L);
        if (int32bit >= 0x80000000L) {
          val int32bitS = int32bit-0x100000000L
          if (int32bitS>=0) AbsNumber.alpha(int32bitS.toInt)
          else AbsNumber.alpha(int32bitS.toInt)
        }
        else {
          if (int32bit>=0) AbsNumber.alpha(int32bit.toInt)
          else AbsNumber.alpha(int32bit.toInt)
        }
      case UIntPattern =>  UInt
      case NUIntPattern =>  NumTop
      case _ =>  NumTop
    }
  }

  def ToUInt32(value:Value):AbsNumber = {
    val pv = value.pvalue
    val number = Helper.toNumber(pv) + Helper.toNumber(Helper.objToPrimitive(value.locset, "Number"))
    forMatch(number) match {
      case NaNPattern | UIntSinglePattern(0) | PosInfPattern | NegInfPattern | InfinityPattern =>  AbsNumber.alpha(0)
      case UIntSinglePattern(n) =>
        val posInt = math.signum(n)*math.floor(math.abs(n))
        val int32bit = modulo(posInt, 0x100000000L);
        AbsNumber.alpha(int32bit.toInt)
      case NUIntSinglePattern(n) =>
        val posInt = math.signum(n)*math.floor(math.abs(n))
        val int32bit = modulo(posInt, 0x100000000L);
        AbsNumber.alpha(int32bit.toInt)
      case NumBotPattern => NumBot
      case _ =>  UInt
    }
  }

  def ToUInt16(value:Value):AbsNumber = {
    val pv = value.pvalue
    val number = Helper.toNumber(pv) + Helper.toNumber(Helper.objToPrimitive(value.locset, "Number"))
    forMatch(number) match {
      case NaNPattern | UIntSinglePattern(0) | PosInfPattern | NegInfPattern | InfinityPattern =>  AbsNumber.alpha(0)
      case UIntSinglePattern(n) =>
        val posInt = math.signum(n)*math.floor(math.abs(n))
        val int16bit = modulo(posInt, 0x10000L);
        AbsNumber.alpha(int16bit.toInt)
      case NUIntSinglePattern(n) =>
        val posInt = math.signum(n)*math.floor(math.abs(n))
        val int16bit = modulo(posInt, 0x10000L);
        AbsNumber.alpha(int16bit.toInt)
      case _ =>  UInt
    }
  }

  def parseInt(string: AbsString, radix: AbsNumber): AbsNumber = {
    // Imprecise implementation!!!
    val R = ToInt32(Value(radix))
    R.getAbsCase match {
      case AbsTop => NumTop
      case AbsBot => NumBot
      case _ => R.gamma match {
        case Some(vs) =>
          vs.foldLeft[AbsNumber](NumBot)((r, v) => {
            r + (v match {
              case Double.PositiveInfinity => NaN
              case Double.NegativeInfinity => NaN
              case Double.NaN => NaN
              case _ if v != 0 && (v < 2 || v > 36) => NaN
              case _ if v == 0 || v == 10 =>
                string.gamma match {
                  case Some(vs) =>
                    vs.foldLeft[AbsNumber](NumBot)((r, v) => {
                      try {
                        r + AbsNumber.alpha(v.toDouble.toInt)
                      }
                      catch {
                        case ne: NumberFormatException =>
                          if(v.trim().equals("")) r + AbsNumber.alpha(0)
                          else r + NaN
                      }
                    })
                  case None =>
                    string.getAbsCase match {
                      case AbsBot => NumBot
                      case _ => NumTop
                    }
                }
              case _ => NumTop
            })
          })
        case None => NumTop
      }
    }
  }

  def parseFloat(string: AbsString): AbsNumber = {
    // Imprecise implementation!!!
    string.gamma match {
      case Some(vs) =>
        vs.foldLeft[AbsNumber](NumBot)((r, v) => {
          try {
            r + AbsNumber.alpha(v.toDouble)
          }
          catch {
            case ne: NumberFormatException =>
              if(v.trim().equals("")) r + AbsNumber.alpha(0)
              else r + NaN
          }
        })
      case None =>
        string.getAbsCase match {
          case AbsBot => NumBot
          case _ => NumTop
        }
    }
  }
}
