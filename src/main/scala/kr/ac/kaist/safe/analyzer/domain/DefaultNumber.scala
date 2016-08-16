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

import scala.collection.immutable.HashSet

object DefaultNumUtil extends AbsNumberUtil {
  val Top: AbsNumber = DefaultNumTop
  val Bot: AbsNumber = DefaultNumBot
  val Infinity: AbsNumber = DefaultNumInf
  val PosInf: AbsNumber = DefaultNumPosInf
  val NegInf: AbsNumber = DefaultNumNegInf
  val NaN: AbsNumber = DefaultNumNaN
  val UInt: AbsNumber = DefaultNumUInt
  val NUInt: AbsNumber = DefaultNumNUInt
  val NaturalNumbers = UInt

  def alpha(num: Long): AbsNumber = num >= 0 match {
    case true => DefaultNumUIntConst(num)
    case false => DefaultNumNUIntConst(num.toDouble)
  }

  def alpha(num: Double): AbsNumber = num match {
    case _ if num.isNaN => DefaultNumNaN
    case Double.NegativeInfinity => DefaultNumNegInf
    case Double.PositiveInfinity => DefaultNumPosInf
    case _ =>
      val uint = num.toLong
      if ((num == uint) && uint >= 0) DefaultNumUIntConst(uint)
      else DefaultNumNUIntConst(num)
  }

  sealed abstract class DefaultNumber extends AbsNumber {
    /* AbsDomain Interface */
    def gamma: ConSet[Double]
    def gammaSingle: ConSingle[Double]
    def gammaSimple: ConSimple = ConSimpleTop
    override def toString: String
    def toAbsString(absString: AbsStringUtil): AbsString
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber

    /* AbsNumber Interface */
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

    def <(that: AbsNumber)(absBool: AbsBoolUtil): AbsBool = {
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

    def ===(that: AbsNumber)(absBool: AbsBoolUtil): AbsBool = {
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

    def <>(that: AbsNumber): AbsNumber = {
      (this <= that, that <= this) match {
        case (true, _) => this
        case (_, true) => that
        case _ => DefaultNumBot
      }
    }

    /* Operators */
    private def modulo(x: Double, y: Long): Long = {
      val result = math.abs(x.toLong) % math.abs(y)
      if (math.signum(x) < 0) math.signum(y) * (math.abs(y) - result)
      else math.signum(y) * result
    }

    def toInt32: AbsNumber = {
      this match {
        case DefaultNumBot => DefaultNumBot
        case DefaultNumNaN
          | DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf
          | DefaultNumUIntConst(0) => alpha(0)
        case DefaultNumUIntConst(n) => alpha(n)
        case DefaultNumNUIntConst(n) =>
          val posInt = math.signum(n) * math.floor(math.abs(n))
          val int32bit = modulo(posInt, 0x100000000L)
          if (int32bit >= 0x80000000L) {
            val int32bitS = int32bit - 0x100000000L
            if (int32bitS >= 0) alpha(int32bitS.toInt)
            else alpha(int32bitS.toInt)
          } else if (int32bit >= 0) alpha(int32bit.toInt)
          else alpha(int32bit.toInt)
        case DefaultNumUInt => DefaultNumUInt
        case DefaultNumNUInt
          | DefaultNumTop => DefaultNumTop
      }
    }

    def toUInt32: AbsNumber = {
      def help(n: Double): AbsNumber = {
        val posInt = math.signum(n) * math.floor(math.abs(n))
        val int32bit = modulo(posInt, 0x100000000L);
        alpha(int32bit.toInt)
      }
      this match {
        case DefaultNumBot => DefaultNumBot
        case DefaultNumNaN
          | DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf
          | DefaultNumUIntConst(0) => alpha(0)
        case DefaultNumUIntConst(n) => help(n)
        case DefaultNumNUIntConst(n) => help(n)
        case DefaultNumUInt
          | DefaultNumNUInt
          | DefaultNumTop => DefaultNumUInt
      }
    }

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

    def abs: AbsNumber = {
      this match {
        case DefaultNumInf | DefaultNumNegInf => DefaultNumPosInf
        case DefaultNumNUInt => DefaultNumTop
        case DefaultNumNUIntConst(n) => alpha(math.abs(n))
        case _ => this
      }
    }

    def acos: AbsNumber = {
      this match {
        case DefaultNumBot => this
        case DefaultNumNaN
          | DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf => DefaultNumNaN
        case DefaultNumUIntConst(n) if n > 1 || n < -1 => DefaultNumNaN
        case DefaultNumUIntConst(n) => alpha(Math.acos(n))
        case DefaultNumNUIntConst(n) if n > 1 || n < -1 => DefaultNumNaN
        case DefaultNumNUIntConst(n) => alpha(Math.acos(n))
        case _ => DefaultNumTop
      }
    }

    def asin: AbsNumber = {
      this match {
        case DefaultNumBot => this
        case DefaultNumNaN
          | DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf => DefaultNumNaN
        case DefaultNumUIntConst(n) if n > 1 || n < -1 => DefaultNumNaN
        case DefaultNumUIntConst(n) => alpha(Math.asin(n))
        case DefaultNumNUIntConst(n) if n > 1 || n < -1 => DefaultNumNaN
        case DefaultNumNUIntConst(n) => alpha(Math.asin(n))
        case _ => DefaultNumTop
      }
    }

    def atan: AbsNumber = {
      this match {
        case DefaultNumBot | DefaultNumNaN => this
        case DefaultNumInf => DefaultNumNUInt
        case DefaultNumPosInf => alpha(scala.math.Pi / 2)
        case DefaultNumNegInf => alpha(-scala.math.Pi / 2)
        case DefaultNumUIntConst(n) => alpha(Math.atan(n))
        case DefaultNumNUIntConst(n) => alpha(Math.atan(n))
        case _ => DefaultNumTop
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
  case object DefaultNumTop extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetTop()
    val gammaSingle: ConSingle[Double] = ConSingleTop()
    override val toString: String = "Number"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.NumStr
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Top
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.Top
  }

  case object DefaultNumBot extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetBot()
    val gammaSingle: ConSingle[Double] = ConSingleBot()
    override val gammaSimple: ConSimple = ConSimpleBot
    override val toString: String = "Bot"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.Bot
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Bot
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.Bot
  }

  case object DefaultNumInf extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetCon(HashSet(Double.PositiveInfinity, Double.NegativeInfinity))
    val gammaSingle: ConSingle[Double] = ConSingleTop()
    override val toString: String = "Inf"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.NumStr
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.True
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.Infinity
  }

  case object DefaultNumPosInf extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetCon(HashSet(Double.PositiveInfinity))
    val gammaSingle: ConSingle[Double] = ConSingleCon(Double.PositiveInfinity)
    override val toString: String = "+Inf"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha("Infinity")
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.True
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.PosInf
  }

  case object DefaultNumNegInf extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetCon(HashSet(Double.NegativeInfinity))
    val gammaSingle: ConSingle[Double] = ConSingleCon(Double.NegativeInfinity)
    override val toString: String = "-Inf"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha("-Infinity")
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.True
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.NegInf
  }

  case object DefaultNumNaN extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetCon(HashSet(Double.NaN))
    val gammaSingle: ConSingle[Double] = ConSingleCon(Double.NaN)
    override val toString: String = "NaN"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha("NaN")
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.False
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.NaN
  }

  case object DefaultNumUInt extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetTop()
    val gammaSingle: ConSingle[Double] = ConSingleTop()
    override val toString: String = "UInt"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.NumStr
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Top
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.UInt
  }

  case object DefaultNumNUInt extends DefaultNumber {
    val gamma: ConSet[Double] = ConSetTop()
    val gammaSingle: ConSingle[Double] = ConSingleTop()
    override val toString: String = "NUInt"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.NumStr
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Top
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.NUInt
  }

  case class DefaultNumUIntConst(value: Long) extends DefaultNumber {
    def gamma: ConSet[Double] = ConSetCon(HashSet(value))
    def gammaSingle: ConSingle[Double] = ConSingleCon(value)
    override def toString: String = value.toLong.toString
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha(value.toString)
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = value match {
      case 0 => absBool.False
      case _ => absBool.True
    }
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.alpha(value)
  }

  case class DefaultNumNUIntConst(value: Double) extends DefaultNumber {
    def gamma: ConSet[Double] = ConSetCon(HashSet(value))
    def gammaSingle: ConSingle[Double] = ConSingleCon(value)
    override def toString: String = value.toString
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha(value.toString)
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.True
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.alpha(value)
  }
}
