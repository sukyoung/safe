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

package kr.ac.kaist.safe.analyzer.domain

object DefaultNumUtil extends AbsNumberUtil {
  val Top: AbsNumber = DefaultNumTop
  val Bot: AbsNumber = DefaultNumBot
  val Infinity: AbsNumber = DefaultNumInf
  val PosInf: AbsNumber = DefaultNumPosInf
  val NegInf: AbsNumber = DefaultNumNegInf
  val NaN: AbsNumber = DefaultNumNaN
  val UInt: AbsNumber = DefaultNumUInt
  val NUInt: AbsNumber = DefaultNumNUInt

  val naturalNumbers = UInt

  def alpha(num: Double): AbsNumber =
    num match {
      case Double.NaN => DefaultNumNaN
      case Double.NegativeInfinity => DefaultNumNegInf
      case Double.PositiveInfinity => DefaultNumPosInf
      case _ =>
        val intnum = num.toInt
        val diff: Double = num - intnum.toDouble
        if ((diff == 0) && num >= 0) DefaultNumUIntConst(num)
        else DefaultNumNUIntConst(num)
    }

  sealed abstract class DefaultNumber extends AbsNumber {
    def getAbsCase: AbsCase =
      this match {
        case DefaultNumTop => AbsTop
        case DefaultNumBot => AbsBot
        case DefaultNumInf => AbsMulti
        case DefaultNumPosInf => AbsSingle
        case DefaultNumNegInf => AbsSingle
        case DefaultNumNaN => AbsSingle
        case DefaultNumUInt => AbsMulti
        case DefaultNumNUInt => AbsMulti
        case DefaultNumUIntConst(v) => AbsSingle
        case DefaultNumNUIntConst(v) => AbsSingle
      }

    def getSingle: Option[Double] =
      this match {
        case DefaultNumPosInf => Some(Double.PositiveInfinity)
        case DefaultNumNegInf => Some(Double.NegativeInfinity)
        case DefaultNumNaN => Some(Double.NaN)
        case DefaultNumUIntConst(v) => Some(v)
        case DefaultNumNUIntConst(v) => Some(v)
        case _ => None
      }

    def gammaOpt: Option[Set[Double]] =
      this match {
        case DefaultNumInf => Some(Set(Double.PositiveInfinity, Double.NegativeInfinity))
        case DefaultNumPosInf => Some(Set(Double.PositiveInfinity))
        case DefaultNumNegInf => Some(Set(Double.NegativeInfinity))
        case DefaultNumNaN => Some(Set(Double.NaN))
        case DefaultNumUIntConst(value) => Some(Set(value))
        case DefaultNumNUIntConst(value) => Some(Set(value))
        case _ => None
      }

    /* partial order */
    def <=(that: AbsNumber): Boolean =
      (this, that) match {
        case (DefaultNumBot, _) => true
        case (_, DefaultNumTop) => true
        case (DefaultNumNaN, DefaultNumNaN) => true
        case (DefaultNumPosInf, DefaultNumPosInf) => true
        case (DefaultNumPosInf, DefaultNumInf) => true
        case (DefaultNumNegInf, DefaultNumNegInf) => true
        case (DefaultNumNegInf, DefaultNumInf) => true
        case (DefaultNumInf, DefaultNumInf) => true
        case (DefaultNumUIntConst(a), DefaultNumUIntConst(b)) => a == b
        case (DefaultNumUIntConst(_), DefaultNumUInt) => true
        case (DefaultNumNUIntConst(a), DefaultNumNUIntConst(b)) => a == b
        case (DefaultNumNUIntConst(_), DefaultNumNUInt) => true
        case (DefaultNumUInt, DefaultNumUInt) => true
        case (DefaultNumNUInt, DefaultNumNUInt) => true
        case _ => false
      }

    def <(that: AbsNumber, absBool: AbsBoolUtil): AbsBool = {
      (this, that) match {
        case (DefaultNumBot, _) | (_, DefaultNumBot) => absBool.Bot
        case (DefaultNumNaN, _) | (_, DefaultNumNaN) => absBool.False
        case (DefaultNumUIntConst(n1), DefaultNumUIntConst(n2)) => absBool.alpha(n1 < n2)
        case (DefaultNumUIntConst(n1), DefaultNumNUIntConst(n2)) => absBool.alpha(n1 < n2)
        case (DefaultNumNUIntConst(n1), DefaultNumUIntConst(n2)) => absBool.alpha(n1 < n2)
        case (DefaultNumNUIntConst(n1), DefaultNumNUIntConst(n2)) => absBool.alpha(n1 < n2)
        case (DefaultNumUInt, DefaultNumUIntConst(n2)) if n2 <= 0 => absBool.False
        case (DefaultNumUInt, DefaultNumNUIntConst(n2)) if n2 <= 0 => absBool.False
        case _ => absBool.Top
      }
    }

    /* abstract operator 'equal to' */
    def ===(that: AbsNumber, absBool: AbsBoolUtil): AbsBool = {
      (this, that) match {
        case (DefaultNumBot, _) | (_, DefaultNumBot) => absBool.Bot
        case (DefaultNumNaN, _) | (_, DefaultNumNaN) => absBool.False
        case (DefaultNumUIntConst(n1), DefaultNumUIntConst(n2)) => absBool.alpha(n1 == n2)
        case (DefaultNumUIntConst(n1), DefaultNumNUIntConst(n2)) => absBool.alpha(n1 == n2)
        case (DefaultNumNUIntConst(n1), DefaultNumUIntConst(n2)) => absBool.alpha(n1 == n2)
        case (DefaultNumNUIntConst(n1), DefaultNumNUIntConst(n2)) => absBool.alpha(n1 == n2)
        case _ =>
          (this <= that, that <= this) match {
            case (false, false) => absBool.False
            case _ => absBool.Top
          }
      }
    }

    /* join */
    def +(that: AbsNumber): AbsNumber =
      (this, that) match {
        case (DefaultNumTop, _) => this
        case (_, DefaultNumTop) => that
        case (DefaultNumBot, _) => that
        case (_, DefaultNumBot) => this
        case (DefaultNumPosInf, DefaultNumNegInf) => DefaultNumInf
        case (DefaultNumNegInf, DefaultNumPosInf) => DefaultNumInf
        case (DefaultNumUIntConst(a), DefaultNumUIntConst(b)) =>
          if (a == b) this
          else DefaultNumUInt
        case (DefaultNumNUIntConst(a), DefaultNumNUIntConst(b)) =>
          if (a == b) this
          else DefaultNumNUInt
        case _ =>
          (this <= that, that <= this) match {
            case (true, _) => that
            case (_, true) => this
            case _ => DefaultNumTop
          }
      }

    /* meet */
    def <>(that: AbsNumber): AbsNumber = {
      (this <= that, that <= this) match {
        case (true, _) => this
        case (_, true) => that
        case _ => DefaultNumBot
      }
    }

    override def toString: String =
      this match {
        case DefaultNumTop => "Number"
        case DefaultNumBot => "Bot"
        case DefaultNumInf => "Inf"
        case DefaultNumPosInf => "+Inf"
        case DefaultNumNegInf => "-Inf"
        case DefaultNumNaN => "NaN"
        case DefaultNumUInt => "UInt"
        case DefaultNumNUInt => "NUInt"
        case DefaultNumUIntConst(n) => n.toLong.toString
        case DefaultNumNUIntConst(n) => n.toString
      }

    override def isTop: Boolean = { this == DefaultNumTop }

    override def isBottom: Boolean = { this == DefaultNumBot }

    override def isConcrete: Boolean = {
      this match {
        case DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf
          | DefaultNumNaN
          | DefaultNumUIntConst(_)
          | DefaultNumNUIntConst(_) => true
        case _ => false
      }
    }

    def toBoolean(absBool: AbsBoolUtil): AbsBool =
      this match {
        case DefaultNumPosInf | DefaultNumNegInf => absBool.True //TODO: Check DefaultNumInf => absBool.True?
        case DefaultNumNaN => absBool.False
        case DefaultNumUIntConst(n) if n == 0 => absBool.False
        case DefaultNumUIntConst(n) => absBool.True
        case DefaultNumNUIntConst(n) if n == 0 => absBool.False
        case DefaultNumNUIntConst(n) => absBool.True
        case _ => absBool.Bot //TODO: Check DefaultNumUInt => absBool.Top?
      }

    override def toAbsString(absString: AbsStringUtil): AbsString = {
      this match {
        case DefaultNumPosInf => absString.alpha("Infinity")
        case DefaultNumNegInf => absString.alpha("-Infinity")
        case DefaultNumNaN => absString.alpha("NaN")
        case DefaultNumUIntConst(n) => absString.alpha(n.toInt.toString)
        case DefaultNumNUIntConst(n) if n == n.toInt => absString.alpha(n.toInt.toString)
        case DefaultNumNUIntConst(n) => absString.alpha(n.toString)
        case DefaultNumBot => absString.Bot
        case _ => absString.NumStr
      }
    }

    def isNum(v: Double): Boolean =
      this match {
        case DefaultNumUIntConst(n) if n == v => true
        case _ => false
      }

    def isNum: Boolean =
      this match {
        case DefaultNumUIntConst(_)
          | DefaultNumNUIntConst(_) => true
        case _ => false
      }

    def isFinite: Boolean =
      this match {
        case DefaultNumUInt
          | DefaultNumNUInt
          | DefaultNumUIntConst(_)
          | DefaultNumNUIntConst(_) => true
        case _ => false
      }

    def isFiniteMulti: Boolean =
      this match {
        case DefaultNumUInt
          | DefaultNumNUInt => true
        case _ => false
      }

    def isInfinity: Boolean = this == DefaultNumInf
    def isPosInf: Boolean = this == DefaultNumPosInf
    def isNegInf: Boolean = this == DefaultNumNegInf

    def isInf: Boolean =
      this match {
        case DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf => true
        case _ => false
      }

    def isInfOrNaN: Boolean =
      this match {
        case DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf => true
        case DefaultNumNaN => true
        case _ => false
      }

    def getUIntSingle: Option[Double] =
      this match {
        case DefaultNumUIntConst(v) => Some(v)
        case _ => None
      }

    def isUIntSingle: Boolean =
      this match {
        case DefaultNumUIntConst(_) => true
        case _ => false
      }

    def isNUIntSingle: Boolean =
      this match {
        case DefaultNumNUIntConst(_) => true
        case _ => false
      }

    def isUIntAll: Boolean = this == DefaultNumTop | this == DefaultNumUInt

    def isUInt: Boolean = this == DefaultNumUInt

    def isUIntOrBot: Boolean =
      this match {
        case DefaultNumUInt
          | DefaultNumUIntConst(_) => true
        case DefaultNumBot => true
        case _ => false
      }

    def isNUInt: Boolean = this == DefaultNumNUInt

    def isNaN: Boolean = this == DefaultNumNaN

    /* Operators */
    def negate: AbsNumber = {
      this match {
        case DefaultNumNaN => DefaultNumNaN
        case DefaultNumUIntConst(0) => this
        case DefaultNumUIntConst(n) => alpha(-n)
        case DefaultNumNUIntConst(n) => alpha(-n)
        case DefaultNumUInt => DefaultNumNUInt
        case DefaultNumNUInt => DefaultNumTop
        case DefaultNumPosInf => DefaultNumNegInf
        case DefaultNumNegInf => DefaultNumPosInf
        case _ => this
      }
    }

    def bitNegate: AbsNumber = {
      this match {
        case DefaultNumUIntConst(n) => alpha(~n.toInt)
        case DefaultNumNUIntConst(n) => alpha(~n.toInt)
        case DefaultNumUInt => DefaultNumUInt
        case DefaultNumNUInt => DefaultNumUInt
        case _ => this
      }
    }

    def bitOr(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumUIntConst(0), _) => that
        case (_, DefaultNumUIntConst(0)) => this
        case (DefaultNumUIntConst(l), DefaultNumUIntConst(r)) => alpha(l.toInt | r.toInt)
        case (DefaultNumUIntConst(l), DefaultNumNUIntConst(r)) => alpha(l.toInt | r.toInt)
        case (DefaultNumUIntConst(_), DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumUIntConst(_), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumNUIntConst(l), DefaultNumUIntConst(r)) => alpha(l.toInt | r.toInt)
        case (DefaultNumNUIntConst(l), DefaultNumNUIntConst(r)) => alpha(l.toInt | r.toInt)
        case (DefaultNumNUIntConst(l), DefaultNumUInt) if l > 0 => DefaultNumUInt
        case (DefaultNumNUIntConst(l), DefaultNumUInt) if l <= 0 => DefaultNumNUInt
        case (DefaultNumNUIntConst(l), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumUInt, DefaultNumUIntConst(_)) => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(r)) if r > 0 => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(r)) if r <= 0 => DefaultNumNUInt
        case (DefaultNumUInt, DefaultNumUInt) => DefaultNumUInt
        case _ => DefaultNumTop
      }
    }

    def bitAnd(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumUIntConst(0), _) => alpha(0)
        case (_, DefaultNumUIntConst(0)) => alpha(0)
        case (DefaultNumUIntConst(l), DefaultNumUIntConst(r)) => alpha(l.toInt & r.toInt)
        case (DefaultNumUIntConst(l), DefaultNumNUIntConst(r)) => alpha(l.toInt & r.toInt)
        case (DefaultNumUIntConst(_), _) => DefaultNumUInt
        case (DefaultNumNUIntConst(l), DefaultNumUIntConst(r)) => alpha(l.toInt & r.toInt)
        case (DefaultNumNUIntConst(l), DefaultNumNUIntConst(r)) => alpha(l.toInt & r.toInt)
        case (DefaultNumNUIntConst(_), DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumNUIntConst(l), DefaultNumNUInt) if l > 0 => DefaultNumUInt
        case (DefaultNumNUIntConst(l), DefaultNumNUInt) if l <= 0 => DefaultNumTop
        case (DefaultNumUInt, _) => DefaultNumUInt
        case (DefaultNumNUInt, DefaultNumUIntConst(_)) => DefaultNumUInt
        case (DefaultNumNUInt, DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumNUInt, DefaultNumNUIntConst(r)) if r > 0 => DefaultNumUInt
        case (DefaultNumNUInt, DefaultNumNUIntConst(r)) if r <= 0 => DefaultNumTop
        case (DefaultNumNUInt, DefaultNumNUInt) => DefaultNumTop
        case _ => DefaultNumTop
      }
    }

    def bitXor(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumUIntConst(0), _) => that
        case (_, DefaultNumUIntConst(0)) => this
        case (DefaultNumUIntConst(l), DefaultNumUIntConst(r)) => alpha(l.toInt ^ r.toInt)
        case (DefaultNumUIntConst(_), DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumUIntConst(l), DefaultNumNUIntConst(r)) => alpha(l.toInt ^ r.toInt)
        case (DefaultNumUIntConst(_), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumNUIntConst(l), DefaultNumUIntConst(r)) => alpha(l.toInt ^ r.toInt)
        case (DefaultNumNUIntConst(l), DefaultNumNUIntConst(r)) => alpha(l.toInt ^ r.toInt)
        case (DefaultNumNUIntConst(l), DefaultNumUInt) if l > 0 => DefaultNumUInt
        case (DefaultNumNUIntConst(l), DefaultNumUInt) if l <= 0 => DefaultNumNUInt
        case (DefaultNumNUIntConst(_), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumUInt, DefaultNumUIntConst(_)) => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(r)) if r > 0 => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(r)) if r <= 0 => DefaultNumNUInt
        case (DefaultNumUInt, DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumNUInt, _) => DefaultNumTop
        case _ => DefaultNumTop
      }
    }

    def shiftHelp(shift: AbsNumber, shiftOp: (Int, Int) => Int): AbsNumber = {
      val MULTI_SHIFT = 0x20
      val shiftCount = shift match {
        case DefaultNumUIntConst(n) => n.toInt & 0x1F
        case DefaultNumNUIntConst(n) => n.toInt & 0x1F
        case _ => MULTI_SHIFT
      }
      this match {
        case _ if shiftCount == MULTI_SHIFT => DefaultNumTop
        case DefaultNumUIntConst(n) if shiftCount != MULTI_SHIFT =>
          val res = shiftOp(n.toInt, shiftCount)
          if (res >= 0) alpha(res)
          else alpha(res)
        case DefaultNumNUIntConst(n) if shiftCount != MULTI_SHIFT =>
          val res = shiftOp(n.toInt, shiftCount)
          if (res >= 0) alpha(res)
          else alpha(res)
        case _ => DefaultNumTop
      }
    }

    def bitLShift(shift: AbsNumber): AbsNumber = {
      shiftHelp(shift, (i, j) => i << j)
    }

    def bitRShift(shift: AbsNumber): AbsNumber = {
      shiftHelp(shift, (i, j) => i >> j)
    }

    def bitURShift(shift: AbsNumber): AbsNumber = {
      shiftHelp(shift, (i, j) => i >>> j)
    }

    def add(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumBot, _) | (_, DefaultNumBot) => DefaultNumBot
        case (DefaultNumNaN, _) | (_, DefaultNumNaN) => DefaultNumNaN
        case (DefaultNumInf, DefaultNumInf) => DefaultNumTop
        case (DefaultNumInf, _) | (_, DefaultNumInf) => DefaultNumInf
        case (DefaultNumPosInf, DefaultNumNegInf) | (DefaultNumNegInf, DefaultNumPosInf) => DefaultNumNaN
        case (DefaultNumPosInf, _) | (_, DefaultNumPosInf) => DefaultNumPosInf
        case (DefaultNumNegInf, _) | (_, DefaultNumNegInf) => DefaultNumNegInf
        case (DefaultNumUIntConst(0), _) => that
        case (_, DefaultNumUIntConst(0)) => this
        case (DefaultNumUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 + n2)
        case (DefaultNumUIntConst(_), DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 + n2)
        case (DefaultNumUInt, DefaultNumUIntConst(_)) => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(_)) => DefaultNumTop
        case (DefaultNumUInt, DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumNUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 + n2)
        case (DefaultNumNUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 + n2)
        case (DefaultNumNUIntConst(n), DefaultNumUInt) if (n > 0) & (n - n.toInt == 0.0) => DefaultNumUInt
        case (DefaultNumNUIntConst(n), DefaultNumUInt) => DefaultNumTop
        case (_, _) => DefaultNumTop
      }
    }

    def sub(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumBot, _) | (_, DefaultNumBot) => DefaultNumBot
        case (DefaultNumNaN, _) | (_, DefaultNumNaN) => DefaultNumNaN
        case (DefaultNumInf, _) | (_, DefaultNumInf) => DefaultNumInf
        case (DefaultNumPosInf, DefaultNumPosInf) | (DefaultNumNegInf, DefaultNumNegInf) => DefaultNumNaN
        case (DefaultNumPosInf, _) | (_, DefaultNumNegInf) => DefaultNumPosInf
        case (DefaultNumNegInf, _) | (_, DefaultNumPosInf) => DefaultNumNegInf
        case (DefaultNumUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 - n2)
        case (DefaultNumUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 - n2)
        case (DefaultNumNUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 - n2)
        case (DefaultNumNUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 - n2)
        case (DefaultNumNUIntConst(_), DefaultNumUInt) => DefaultNumNUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(n)) if (n < 0) & (n - n.toInt == 0.0) => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(n)) => DefaultNumNUInt
        case (DefaultNumUInt, DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumNUInt, DefaultNumUIntConst(_)) => DefaultNumNUInt
        case (DefaultNumNUInt, DefaultNumUInt) => DefaultNumNUInt
        case _ => DefaultNumTop
      }
    }

    def mul(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumBot, _) | (_, DefaultNumBot) => DefaultNumBot
        /* 11.5.1 first */
        case (DefaultNumNaN, _) | (_, DefaultNumNaN) => DefaultNumNaN
        /* 11.5.1 third */
        case (DefaultNumPosInf | DefaultNumNegInf | DefaultNumInf, DefaultNumUIntConst(0)) => DefaultNumNaN
        case (DefaultNumUIntConst(0), DefaultNumPosInf | DefaultNumNegInf | DefaultNumInf) => DefaultNumNaN
        /* 11.5.1 fourth */
        case (DefaultNumPosInf, DefaultNumPosInf) | (DefaultNumNegInf, DefaultNumNegInf) => DefaultNumPosInf
        case (DefaultNumPosInf, DefaultNumNegInf) | (DefaultNumNegInf, DefaultNumPosInf) => DefaultNumNegInf
        case (DefaultNumInf, DefaultNumPosInf | DefaultNumNegInf) => DefaultNumInf
        case (DefaultNumInf, DefaultNumInf) => DefaultNumInf
        case (DefaultNumPosInf | DefaultNumNegInf, DefaultNumInf) => DefaultNumInf
        /* 11.5.1 fifth */
        case (DefaultNumPosInf, DefaultNumUIntConst(_)) => DefaultNumPosInf
        case (DefaultNumPosInf, DefaultNumUInt) | (DefaultNumUInt, DefaultNumPosInf) => DefaultNumTop
        case (DefaultNumPosInf, DefaultNumNUIntConst(n)) if n > 0 => DefaultNumPosInf
        case (DefaultNumPosInf, DefaultNumNUIntConst(n)) => DefaultNumNegInf
        case (DefaultNumPosInf, DefaultNumNUInt) | (DefaultNumNUInt, DefaultNumPosInf) => DefaultNumInf
        case (DefaultNumUIntConst(_), DefaultNumPosInf) => DefaultNumPosInf
        case (DefaultNumNUIntConst(n), DefaultNumPosInf) if n > 0 => DefaultNumPosInf
        case (DefaultNumNUIntConst(n), DefaultNumPosInf) => DefaultNumNegInf
        case (DefaultNumNegInf, DefaultNumUIntConst(_)) => DefaultNumNegInf
        case (DefaultNumNegInf, DefaultNumUInt) | (DefaultNumUInt, DefaultNumNegInf) => DefaultNumTop
        case (DefaultNumNegInf, DefaultNumNUIntConst(n)) if n > 0 => DefaultNumNegInf
        case (DefaultNumNegInf, DefaultNumNUIntConst(n)) => DefaultNumPosInf
        case (DefaultNumNegInf, DefaultNumNUInt) | (DefaultNumNUInt, DefaultNumNegInf) => DefaultNumInf
        case (DefaultNumUIntConst(_), DefaultNumNegInf) => DefaultNumNegInf
        case (DefaultNumNUIntConst(n), DefaultNumNegInf) if n > 0 => DefaultNumNegInf
        case (DefaultNumNUIntConst(_), DefaultNumNegInf) => DefaultNumPosInf
        case (DefaultNumInf, DefaultNumUInt) | (DefaultNumUInt, DefaultNumInf) => DefaultNumTop
        case (DefaultNumInf, DefaultNumUIntConst(_)) => DefaultNumInf
        case (DefaultNumInf, DefaultNumNUIntConst(_)) => DefaultNumInf
        case (DefaultNumInf, DefaultNumNUInt) | (DefaultNumNUInt, DefaultNumInf) => DefaultNumInf
        case (DefaultNumUIntConst(_), DefaultNumInf) => DefaultNumInf
        case (DefaultNumNUIntConst(_), DefaultNumInf) => DefaultNumInf
        /* 11.5.1 sixth */
        case (DefaultNumUIntConst(0), _) => alpha(0)
        case (_, DefaultNumUIntConst(0)) => alpha(0)
        case (DefaultNumUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 * n2)
        case (DefaultNumUIntConst(n1), DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 * n2)
        case (DefaultNumUIntConst(n1), DefaultNumNUInt) => DefaultNumNUInt
        case (DefaultNumUInt, DefaultNumUIntConst(n2)) => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumUInt) => DefaultNumUInt
        case (DefaultNumUInt, DefaultNumNUIntConst(n2)) => DefaultNumTop
        case (DefaultNumUInt, DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumNUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 * n2)
        case (DefaultNumNUIntConst(n1), DefaultNumUInt) => DefaultNumTop
        case (DefaultNumNUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 * n2)
        case (DefaultNumNUIntConst(n1), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumNUInt, _) => DefaultNumTop
        case _ => DefaultNumTop
      }
    }

    def div(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumBot, _) | (_, DefaultNumBot) => DefaultNumBot
        /* 11.5.2 first */
        case (DefaultNumNaN, _) | (_, DefaultNumNaN) => DefaultNumNaN
        /* 11.5.2 third */
        case (DefaultNumPosInf | DefaultNumNegInf | DefaultNumInf, DefaultNumPosInf | DefaultNumNegInf | DefaultNumInf) => DefaultNumNaN
        /* 11.5.2 fourth */
        case (DefaultNumPosInf, DefaultNumUIntConst(0)) => DefaultNumPosInf
        case (DefaultNumNegInf, DefaultNumUIntConst(0)) => DefaultNumNegInf
        case (DefaultNumInf, DefaultNumUIntConst(0)) => DefaultNumInf
        /* 11.5.2 fifth */
        case (DefaultNumPosInf, DefaultNumUIntConst(_)) => DefaultNumPosInf
        case (DefaultNumPosInf, DefaultNumUInt) => DefaultNumPosInf
        case (DefaultNumPosInf, DefaultNumNUIntConst(n)) if n > 0 => DefaultNumPosInf
        case (DefaultNumPosInf, DefaultNumNUIntConst(_)) => DefaultNumNegInf
        case (DefaultNumPosInf, DefaultNumNUInt) => DefaultNumInf
        case (DefaultNumNegInf, DefaultNumUIntConst(_)) => DefaultNumNegInf
        case (DefaultNumNegInf, DefaultNumUInt) => DefaultNumNegInf
        case (DefaultNumNegInf, DefaultNumNUIntConst(n)) if n > 0 => DefaultNumNegInf
        case (DefaultNumNegInf, DefaultNumNUIntConst(_)) => DefaultNumPosInf
        case (DefaultNumNegInf, DefaultNumNUInt) => DefaultNumInf
        case (DefaultNumInf, _) => DefaultNumInf
        /* 11.5.2 sixth */
        case (_, DefaultNumPosInf) => alpha(0)
        case (_, DefaultNumNegInf) => alpha(0)
        case (_, DefaultNumInf) => alpha(0)
        /* 11.5.2  seventh */
        case (DefaultNumUIntConst(0), DefaultNumUIntConst(0)) => DefaultNumNaN
        case (DefaultNumUIntConst(0), _) => alpha(0)
        /* 11.5.2  eighth */
        case (DefaultNumUIntConst(n), DefaultNumUIntConst(0)) => DefaultNumPosInf
        case (DefaultNumUInt, DefaultNumUIntConst(0)) => DefaultNumTop
        case (DefaultNumNUIntConst(n), DefaultNumUIntConst(0)) if n > 0 => DefaultNumPosInf
        case (DefaultNumNUIntConst(n), DefaultNumUIntConst(0)) => DefaultNumNegInf
        case (DefaultNumNUInt, DefaultNumUIntConst(0)) => DefaultNumInf
        /* 11.5.2  ninth */
        case (DefaultNumUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 / n2)
        case (DefaultNumUIntConst(n1), DefaultNumUInt) => DefaultNumTop
        case (DefaultNumUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 / n2)
        case (DefaultNumUIntConst(n1), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumNUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 / n2)
        case (DefaultNumNUIntConst(n1), DefaultNumUInt) => DefaultNumTop
        case (DefaultNumNUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 / n2)
        case (DefaultNumNUIntConst(n1), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumUInt, _) => DefaultNumTop
        case (DefaultNumNUInt, DefaultNumUIntConst(n2)) => DefaultNumNUInt
        case (DefaultNumNUInt, _) => DefaultNumTop
        case _ => DefaultNumTop
      }
    }

    def mod(that: AbsNumber): AbsNumber = {
      (this, that) match {
        case (DefaultNumBot, _) | (_, DefaultNumBot) => DefaultNumBot
        /* 11.5.3 first */
        case (DefaultNumNaN, _) | (_, DefaultNumNaN) => DefaultNumNaN
        /* 11.5.3 third */
        case (DefaultNumPosInf | DefaultNumNegInf | DefaultNumInf, _) => DefaultNumNaN
        case (_, DefaultNumUIntConst(0)) => DefaultNumNaN
        /* 11.5.3 fifth */
        case (DefaultNumUIntConst(0), _) => alpha(0)
        /* 11.5.3 fourth */
        case (_, DefaultNumPosInf | DefaultNumNegInf | DefaultNumInf) => that
        /* 11.5.3 sixth */
        case (DefaultNumUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 % n2)
        case (DefaultNumUIntConst(n1), DefaultNumUInt) => DefaultNumTop
        case (DefaultNumUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 % n2)
        case (DefaultNumUIntConst(n1), DefaultNumNUInt) => DefaultNumTop
        case (DefaultNumUInt, DefaultNumUIntConst(n2)) => DefaultNumUInt
        case (DefaultNumUInt, _) => DefaultNumTop
        case (DefaultNumNUIntConst(n1), DefaultNumUIntConst(n2)) => alpha(n1 % n2)
        case (DefaultNumNUIntConst(n1), DefaultNumNUIntConst(n2)) => alpha(n1 % n2)
        case (DefaultNumNUIntConst(n1), _) => DefaultNumTop
        case (DefaultNumNUInt, DefaultNumUIntConst(n2)) => DefaultNumNUInt
        case (DefaultNumNUInt, _) => DefaultNumTop
        case _ => DefaultNumTop
      }
    }
  }
  case object DefaultNumTop extends DefaultNumber
  case object DefaultNumBot extends DefaultNumber
  case object DefaultNumInf extends DefaultNumber
  case object DefaultNumPosInf extends DefaultNumber
  case object DefaultNumNegInf extends DefaultNumber
  case object DefaultNumNaN extends DefaultNumber
  case object DefaultNumUInt extends DefaultNumber
  case object DefaultNumNUInt extends DefaultNumber
  case class DefaultNumUIntConst(value: Double) extends DefaultNumber
  case class DefaultNumNUIntConst(value: Double) extends DefaultNumber
}
