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

import kr.ac.kaist.safe.analyzer.domain.Utils._

// default number abstract domain
object DefaultNumber extends AbsNumberUtil {
  case object Top extends Dom
  case object Bot extends Dom
  case object Inf extends Dom
  case object PosInf extends Dom
  case object NegInf extends Dom
  case object NaN extends Dom
  case object UInt extends Dom
  case object NUInt extends Dom
  case class UIntConst(value: Long) extends Dom
  case class NUIntConst(value: Double) extends Dom
  val NatNum: Dom = UInt

  def alpha(num: Num): AbsNumber = num.num match {
    case num if num.isNaN => NaN
    case Double.NegativeInfinity => NegInf
    case Double.PositiveInfinity => PosInf
    case num =>
      val uint = num.toLong
      if ((num == uint) && (uint > 0 || (num compare 0.0) == 0)) UIntConst(uint)
      else NUIntConst(num)
  }

  sealed abstract class Dom extends AbsNumber {
    def gamma: ConSet[Num] = this match {
      case Bot => ConFin()
      case Inf => ConFin(Double.PositiveInfinity, Double.NegativeInfinity)
      case PosInf => ConFin(Double.PositiveInfinity)
      case NegInf => ConFin(Double.NegativeInfinity)
      case NaN => ConFin(Double.NaN)
      case UIntConst(v) => ConFin(v)
      case NUIntConst(v) => ConFin(v)
      case Top | UInt | NUInt => ConInf()
    }

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[Num] = this match {
      case Bot => ConZero()
      case PosInf => ConOne(Double.PositiveInfinity)
      case NegInf => ConOne(Double.NegativeInfinity)
      case NaN => ConOne(Double.NaN)
      case UIntConst(v) => ConOne(v)
      case NUIntConst(v) => ConOne(v)
      case Top | UInt | NUInt | Inf => ConMany()
    }

    override def toString: String = this match {
      case Top => "Top(number)"
      case Bot => "⊥(number)"
      case Inf => "Inf"
      case PosInf => "+Inf"
      case NegInf => "-Inf"
      case NaN => "NaN"
      case UInt => "UInt"
      case NUInt => "NUInt"
      case UIntConst(v) => v.toString
      case NUIntConst(v) => v.toString
    }

    private def toString(d: Double): String = {
      if (Math.floor(d) == d && !d.isInfinity) d.toLong.toString
      else d.toString
    }
    def toAbsString: AbsString = this match {
      case Top => AbsString.Number
      case Bot => AbsString.Bot
      case Inf => AbsString("Infinity", "-Infinity")
      case PosInf => AbsString("Infinity")
      case NegInf => AbsString("-Infinity")
      case NaN => AbsString("NaN")
      case UInt => AbsString.Number
      case NUInt => AbsString.Number
      case UIntConst(v) => AbsString(v.toString)
      case NUIntConst(v) => AbsString(toString(v))
    }

    def toAbsBoolean: AbsBool = this match {
      case Bot => AbsBool.Bot
      case NaN => AbsBool.False
      case UIntConst(v) if v == 0 => AbsBool.False
      case Top | UInt => AbsBool.Top
      case _ => AbsBool.True
    }

    def <=(that: AbsNumber): Boolean = (this, check(that)) match {
      case (Bot, _) => true
      case (_, Top) => true
      case (NaN, NaN) => true
      case (PosInf, PosInf) => true
      case (PosInf, Inf) => true
      case (NegInf, NegInf) => true
      case (NegInf, Inf) => true
      case (Inf, Inf) => true
      case (UIntConst(a), UIntConst(b)) => a == b
      case (UIntConst(_), UInt) => true
      case (NUIntConst(a), NUIntConst(b)) => a == b
      case (NUIntConst(_), NUInt) => true
      case (UInt, UInt) => true
      case (NUInt, NUInt) => true
      case _ => false
    }

    def <(that: AbsNumber): AbsBool = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      case (NaN, _) | (_, NaN) => AbsBool.False
      case (UIntConst(n1), UIntConst(n2)) => AbsBool(n1 < n2)
      case (UIntConst(n1), NUIntConst(n2)) => AbsBool(n1 < n2)
      case (NUIntConst(n1), UIntConst(n2)) => AbsBool(n1 < n2)
      case (NUIntConst(n1), NUIntConst(n2)) => AbsBool(n1 < n2)
      case (UInt, UIntConst(n2)) if n2 <= 0 => AbsBool.False
      case (UInt, NUIntConst(n2)) if n2 <= 0 => AbsBool.False
      case _ => AbsBool.Top
    }

    def ===(that: AbsNumber): AbsBool = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      case (NaN, _) | (_, NaN) => AbsBool.False
      case (UIntConst(n1), UIntConst(n2)) => AbsBool(n1 == n2)
      case (UIntConst(n1), NUIntConst(n2)) => AbsBool(n1 == n2)
      case (NUIntConst(n1), UIntConst(n2)) => AbsBool(n1 == n2)
      case (NUIntConst(n1), NUIntConst(n2)) => AbsBool(n1 == n2)
      case _ =>
        (this <= that, that <= this) match {
          case (false, false) => AbsBool.False
          case _ => AbsBool.Top
        }
    }

    def +(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (Top, _) => this
      case (_, Top) => that
      case (Bot, _) => that
      case (_, Bot) => this
      case (PosInf, NegInf) => Inf
      case (NegInf, PosInf) => Inf
      case (UIntConst(a), UIntConst(b)) =>
        if (a == b) this
        else UInt
      case (NUIntConst(a), NUIntConst(b)) =>
        if (a == b) this
        else NUInt
      case _ =>
        (this <= that, that <= this) match {
          case (true, _) => that
          case (_, true) => this
          case _ => Top
        }
    }

    def <>(that: AbsNumber): AbsNumber = {
      (this <= that, that <= this) match {
        case (true, _) => this
        case (_, true) => that
        case _ => Bot
      }
    }

    /* Operators */
    private def modulo(x: Double, y: Long): Long = {
      val result = math.abs(x.toLong) % math.abs(y)
      if (math.signum(x) < 0) math.signum(y) * (math.abs(y) - result)
      else math.signum(y) * result
    }

    def isPositive: Boolean = {
      this match {
        case PosInf => true
        case UIntConst(x) if x > 0 => true
        case NUIntConst(x) if x > 0 => true
        case _ => false
      }
    }

    def isNegative: Boolean = {
      this match {
        case NegInf => true
        case NUIntConst(x) if x < 0 => true
        case _ => false
      }
    }

    def isZero: Boolean = {
      isPositiveZero || isNegativeZero
    }

    def isPositiveZero: Boolean = {
      this match {
        case UIntConst(x) if x == 0 => true
        case _ => false
      }
    }

    def isNegativeZero: Boolean = {
      this match {
        case NUIntConst(x) if x == 0 => true
        case _ => false
      }
    }

    def toInteger: AbsNumber = {
      this match {
        case NaN => alpha(0)
        case UIntConst(0)
          | Inf
          | PosInf
          | NegInf => this
        case UIntConst(n) =>
          alpha(math.floor(n))
        case NUIntConst(n) =>
          alpha(math.floor(n))
        case _ => UInt
      }
    }

    def toInt32: AbsNumber = {
      this match {
        case Bot => Bot
        case NaN
          | Inf
          | PosInf
          | NegInf
          | UIntConst(0) => alpha(0)
        case UIntConst(n) => alpha(n)
        case NUIntConst(n) =>
          val posInt = math.signum(n) * math.floor(math.abs(n))
          val int32bit = modulo(posInt, 0x100000000L)
          if (int32bit >= 0x80000000L) {
            val int32bitS = int32bit - 0x100000000L
            if (int32bitS >= 0) alpha(int32bitS.toInt)
            else alpha(int32bitS.toInt)
          } else if (int32bit >= 0) alpha(int32bit.toInt)
          else alpha(int32bit.toInt)
        case UInt => UInt
        case NUInt
          | Top => Top
      }
    }

    def toUInt32: AbsNumber = {
      def help(n: Double): AbsNumber = {
        val posInt = math.floor(math.abs(n))
        val int32bit = modulo(posInt, 0x100000000L);
        alpha(int32bit.toInt)
      }
      this match {
        case Bot => Bot
        case NaN
          | Inf
          | PosInf
          | NegInf
          | UIntConst(0) => alpha(0)
        case UIntConst(n) => help(n)
        case NUIntConst(n) => help(n)
        case UInt
          | NUInt
          | Top => UInt
      }
    }

    def toUInt16: AbsNumber = {
      def help(n: Double): AbsNumber = {
        val posInt = math.signum(n) * math.floor(math.abs(n))
        val int16bit = modulo(posInt, 0x10000L);
        alpha(int16bit.toInt)
      }
      this match {
        case Bot => Bot
        case NaN
          | Inf
          | PosInf
          | NegInf
          | UIntConst(0) => alpha(0)
        case UIntConst(n) => help(n)
        case NUIntConst(n) => help(n)
        case UInt
          | NUInt
          | Top => UInt
      }
    }

    def sameValue(that: AbsNumber): AbsBool = (this, check(that)) match {
      case (NaN, NaN) => AbsBool.True
      case (UIntConst(0), UIntConst(0)) => AbsBool.Top
      case _ => this === that
    }

    def negate: AbsNumber = {
      this match {
        case NaN => NaN
        case UIntConst(0) => this
        case UIntConst(n) => alpha(-n)
        case NUIntConst(n) => alpha(-n)
        case UInt => NUInt
        case NUInt => Top
        case PosInf => NegInf
        case NegInf => PosInf
        case _ => this
      }
    }

    def abs: AbsNumber = {
      this match {
        case Inf | NegInf => PosInf
        case NUInt => Top
        case NUIntConst(n) => alpha(math.abs(n))
        case _ => this
      }
    }

    def acos: AbsNumber = {
      this match {
        case Bot => this
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case UIntConst(n) if n > 1 => NaN
        case UIntConst(n) => alpha(Math.acos(n))
        case NUIntConst(n) if n > 1 || n < -1 => NaN
        case NUIntConst(n) => alpha(Math.acos(n))
        case _ => Top
      }
    }

    def asin: AbsNumber = {
      this match {
        case Bot => this
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case UIntConst(n) if n > 1 => NaN
        case UIntConst(n) => alpha(Math.asin(n))
        case NUIntConst(n) if n > 1 || n < -1 => NaN
        case NUIntConst(n) => alpha(Math.asin(n))
        case _ => Top
      }
    }

    def atan: AbsNumber = {
      this match {
        case Bot | NaN => this
        case Inf => NUInt
        case PosInf => alpha(scala.math.Pi / 2)
        case NegInf => alpha(-scala.math.Pi / 2)
        case UIntConst(n) => alpha(Math.atan(n))
        case NUIntConst(n) => alpha(Math.atan(n))
        case UInt
          | NUInt => NUInt
        case _ => Top
      }
    }

    //TODO sound but not precise if UInt, NUInt
    def atan2(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (NaN, _)
        | (_, NaN) => NaN
      case (_, _) if this.isPositive && that.isZero => alpha(scala.math.Pi / 2)
      case (_, _) if this.isPositiveZero => {
        if (that.isPositive || that.isPositiveZero) alpha(0)
        else alpha(scala.math.Pi)
      }
      case (_, _) if this.isNegativeZero => {
        if (that.isPositive || that.isPositiveZero) alpha(-0.0)
        else alpha(-scala.math.Pi)
      }
      case (_, _) if this.isNegative && that.isZero => alpha(-scala.math.Pi / 2)
      case (UIntConst(y), PosInf) if y > 0 => alpha(0)
      case (NUIntConst(y), PosInf) if y > 0 => alpha(0)
      case (UIntConst(y), NegInf) if y > 0 => alpha(scala.math.Pi)
      case (NUIntConst(y), NegInf) if y > 0 => alpha(scala.math.Pi)
      case (NUIntConst(y), PosInf) if y < 0 => alpha(-0.0)
      case (NUIntConst(y), NegInf) if y < 0 => alpha(-scala.math.Pi)
      case (PosInf, UInt | NUInt | UIntConst(_) | NUIntConst(_)) => alpha(scala.math.Pi / 2)
      case (NegInf, UInt | NUInt | UIntConst(_) | NUIntConst(_)) => alpha(scala.math.Pi / 2)
      case (PosInf, PosInf) => alpha(scala.math.Pi / 4)
      case (PosInf, NegInf) => alpha(scala.math.Pi * 3 / 4)
      case (NegInf, PosInf) => alpha(-scala.math.Pi / 4)
      case (NegInf, NegInf) => alpha(-scala.math.Pi * 3 / 4)
      case (UIntConst(y), UIntConst(x)) => alpha(scala.math.atan2(y, x))
      case (UIntConst(y), NUIntConst(x)) => alpha(scala.math.atan2(y, x))
      case (NUIntConst(y), UIntConst(x)) => alpha(scala.math.atan2(y, x))
      case (NUIntConst(y), NUIntConst(x)) => alpha(scala.math.atan2(y, x))
      case (_, _) => Top
    }

    def ceil: AbsNumber = {
      this match {
        case Bot
          | NaN
          | Inf
          | PosInf
          | NegInf
          | UInt => this
        case UIntConst(n) => alpha(scala.math.ceil(n))
        case NUIntConst(n) => alpha(scala.math.ceil(n))
        case _ => Top
      }
    }

    def cos: AbsNumber = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case UInt
          | NUInt => NUInt
        case UIntConst(n) => alpha(Math.cos(n))
        case NUIntConst(n) => alpha(Math.cos(n))
        case _ => Top
      }
    }

    def exp: AbsNumber = {
      this match {
        case NaN
          | PosInf => this
        case _ if isZero => alpha(1)
        case NegInf => alpha(0)
        case UInt
          | NUInt => NUInt
        case UIntConst(n) => alpha(Math.exp(n))
        case NUIntConst(n) => alpha(Math.exp(n))
        case _ => Top
      }
    }

    def floor: AbsNumber = {
      this match {
        case Bot
          | NaN
          | Inf
          | PosInf
          | NegInf
          | UInt => this
        case UIntConst(n) => alpha(scala.math.floor(n))
        case NUIntConst(n) => alpha(scala.math.floor(n))
        case _ => Top
      }
    }

    def log: AbsNumber = {
      this match {
        case NaN => this
        case _ if isNegative => NaN
        case _ if isZero => NegInf
        case PosInf => this
        case UIntConst(n) => alpha(scala.math.log(n))
        case NUIntConst(n) => alpha(scala.math.log(n))
        case _ => Top
      }
    }

    def pow(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (UInt, UInt) => UInt
      case (UInt, UIntConst(_)) => UInt
      case (UIntConst(_), UInt) => UInt
      case (UIntConst(1), Inf) => NaN
      case (UIntConst(_) | NUIntConst(_) | NaN | PosInf | NegInf,
        UIntConst(_) | NUIntConst(_) | NaN | PosInf | NegInf) => {
        val x: Double = this match {
          case UIntConst(n) => n
          case NUIntConst(n) => n
          case NaN => Double.NaN
          case PosInf => Double.PositiveInfinity
          case NegInf => Double.NegativeInfinity
          case _ => 1 //unreachable
        }
        val y: Double = that match {
          case UIntConst(n) => n
          case NUIntConst(n) => n
          case NaN => Double.NaN
          case PosInf => Double.PositiveInfinity
          case NegInf => Double.NegativeInfinity
          case _ => 1 //unreachable
        }
        alpha(scala.math.pow(x, y))
      }
      case (_, _) => Top
    }

    def round: AbsNumber = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf
          | UInt
          | UIntConst(_) => this
        case NUIntConst(n) => alpha(scala.math.round(n))
        case _ => Top
      }
    }

    def sin: AbsNumber = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case NUInt => this
        case UIntConst(n) => alpha(scala.math.sin(n))
        case NUIntConst(n) => alpha(scala.math.sin(n))
        case _ => Top
      }
    }

    def sqrt: AbsNumber = {
      this match {
        case NaN
          | NegInf => NaN
        case PosInf
          | UInt => this
        case UIntConst(n) => alpha(scala.math.sqrt(n))
        case NUIntConst(n) if n < 0 => NaN
        case NUIntConst(n) => alpha(scala.math.sqrt(n))
        case _ => Top
      }
    }

    def tan: AbsNumber = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case NUInt => this
        case UIntConst(n) => alpha(scala.math.tan(n))
        case NUIntConst(n) => alpha(scala.math.tan(n))
        case _ => Top
      }
    }

    def bitNegate: AbsNumber = {
      this match {
        case UIntConst(n) => alpha(~n.toInt)
        case NUIntConst(n) => alpha(~n.toInt)
        case UInt => UInt
        case NUInt => UInt
        case _ => this
      }
    }

    def bitOr(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (UIntConst(0), _) => that
      case (_, UIntConst(0)) => this
      case (UIntConst(l), UIntConst(r)) => alpha(l.toInt | r.toInt)
      case (UIntConst(l), NUIntConst(r)) => alpha(l.toInt | r.toInt)
      case (UIntConst(_), UInt) => UInt
      case (UIntConst(_), NUInt) => Top
      case (NUIntConst(l), UIntConst(r)) => alpha(l.toInt | r.toInt)
      case (NUIntConst(l), NUIntConst(r)) => alpha(l.toInt | r.toInt)
      case (NUIntConst(l), UInt) if l > 0 => UInt
      case (NUIntConst(l), UInt) if l <= 0 => NUInt
      case (NUIntConst(l), NUInt) => Top
      case (UInt, UIntConst(_)) => UInt
      case (UInt, NUIntConst(r)) if r > 0 => UInt
      case (UInt, NUIntConst(r)) if r <= 0 => NUInt
      case (UInt, UInt) => UInt
      case _ => Top
    }

    def bitAnd(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (UIntConst(0), _) => alpha(0)
      case (_, UIntConst(0)) => alpha(0)
      case (UIntConst(l), UIntConst(r)) => alpha(l.toInt & r.toInt)
      case (UIntConst(l), NUIntConst(r)) => alpha(l.toInt & r.toInt)
      case (UIntConst(_), _) => UInt
      case (NUIntConst(l), UIntConst(r)) => alpha(l.toInt & r.toInt)
      case (NUIntConst(l), NUIntConst(r)) => alpha(l.toInt & r.toInt)
      case (NUIntConst(_), UInt) => UInt
      case (NUIntConst(l), NUInt) if l > 0 => UInt
      case (NUIntConst(l), NUInt) if l <= 0 => Top
      case (UInt, _) => UInt
      case (NUInt, UIntConst(_)) => UInt
      case (NUInt, UInt) => UInt
      case (NUInt, NUIntConst(r)) if r > 0 => UInt
      case (NUInt, NUIntConst(r)) if r <= 0 => Top
      case (NUInt, NUInt) => Top
      case _ => Top
    }

    def bitXor(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (UIntConst(0), _) => that
      case (_, UIntConst(0)) => this
      case (UIntConst(l), UIntConst(r)) => alpha(l.toInt ^ r.toInt)
      case (UIntConst(_), UInt) => UInt
      case (UIntConst(l), NUIntConst(r)) => alpha(l.toInt ^ r.toInt)
      case (UIntConst(_), NUInt) => Top
      case (NUIntConst(l), UIntConst(r)) => alpha(l.toInt ^ r.toInt)
      case (NUIntConst(l), NUIntConst(r)) => alpha(l.toInt ^ r.toInt)
      case (NUIntConst(l), UInt) if l > 0 => UInt
      case (NUIntConst(l), UInt) if l <= 0 => NUInt
      case (NUIntConst(_), NUInt) => Top
      case (UInt, UIntConst(_)) => UInt
      case (UInt, UInt) => UInt
      case (UInt, NUIntConst(r)) if r > 0 => UInt
      case (UInt, NUIntConst(r)) if r <= 0 => NUInt
      case (UInt, NUInt) => Top
      case (NUInt, _) => Top
      case _ => Top
    }

    def shiftHelp(shift: AbsNumber, shiftOp: (Int, Int) => Int): AbsNumber = {
      val MULTI_SHIFT = 0x20
      val shiftCount = shift match {
        case UIntConst(n) => n.toInt & 0x1F
        case NUIntConst(n) => n.toInt & 0x1F
        case _ => MULTI_SHIFT
      }
      this match {
        case _ if shiftCount == MULTI_SHIFT => Top
        case UIntConst(n) if shiftCount != MULTI_SHIFT =>
          val res = shiftOp(n.toInt, shiftCount)
          if (res >= 0) alpha(res)
          else alpha(res)
        case NUIntConst(n) if shiftCount != MULTI_SHIFT =>
          val res = shiftOp(n.toInt, shiftCount)
          if (res >= 0) alpha(res)
          else alpha(res)
        case _ => Top
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

    def add(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      case (NaN, _) | (_, NaN) => NaN
      case (Inf, Inf) => Top
      case (Inf, _) | (_, Inf) => Inf
      case (PosInf, NegInf) | (NegInf, PosInf) => NaN
      case (PosInf, _) | (_, PosInf) => PosInf
      case (NegInf, _) | (_, NegInf) => NegInf
      case (UIntConst(0), _) => that
      case (_, UIntConst(0)) => this
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 + n2)
      case (UIntConst(_), UInt) => UInt
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 + n2)
      case (UInt, UIntConst(_)) => UInt
      case (UInt, NUIntConst(_)) => Top
      case (UInt, UInt) => UInt
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 + n2)
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 + n2)
      case (NUIntConst(n), UInt) if (n > 0) & (n - n.toInt == 0.0) => UInt
      case (NUIntConst(n), UInt) => Top
      case (_, _) => Top
    }

    def sub(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      case (NaN, _) | (_, NaN) => NaN
      case (Inf, _) | (_, Inf) => Inf
      case (PosInf, PosInf) | (NegInf, NegInf) => NaN
      case (PosInf, _) | (_, NegInf) => PosInf
      case (NegInf, _) | (_, PosInf) => NegInf
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 - n2)
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 - n2)
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 - n2)
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 - n2)
      case (NUIntConst(_), UInt) => NUInt
      case (UInt, NUIntConst(n)) if (n < 0) & (n - n.toInt == 0.0) => UInt
      case (UInt, NUIntConst(n)) => NUInt
      case (UInt, NUInt) => Top
      case (NUInt, UIntConst(_)) => NUInt
      case (NUInt, UInt) => NUInt
      case _ => Top
    }

    def mul(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      /* 11.5.1 first */
      case (NaN, _) | (_, NaN) => NaN
      /* 11.5.1 third */
      case (PosInf | NegInf | Inf, UIntConst(0)) => NaN
      case (UIntConst(0), PosInf | NegInf | Inf) => NaN
      /* 11.5.1 fourth */
      case (PosInf, PosInf) | (NegInf, NegInf) => PosInf
      case (PosInf, NegInf) | (NegInf, PosInf) => NegInf
      case (Inf, PosInf | NegInf) => Inf
      case (Inf, Inf) => Inf
      case (PosInf | NegInf, Inf) => Inf
      /* 11.5.1 fifth */
      case (PosInf, UIntConst(_)) => PosInf
      case (PosInf, UInt) | (UInt, PosInf) => Top
      case (PosInf, NUIntConst(n)) if n > 0 => PosInf
      case (PosInf, NUIntConst(n)) => NegInf
      case (PosInf, NUInt) | (NUInt, PosInf) => Inf
      case (UIntConst(_), PosInf) => PosInf
      case (NUIntConst(n), PosInf) if n > 0 => PosInf
      case (NUIntConst(n), PosInf) => NegInf
      case (NegInf, UIntConst(_)) => NegInf
      case (NegInf, UInt) | (UInt, NegInf) => Top
      case (NegInf, NUIntConst(n)) if n > 0 => NegInf
      case (NegInf, NUIntConst(n)) => PosInf
      case (NegInf, NUInt) | (NUInt, NegInf) => Inf
      case (UIntConst(_), NegInf) => NegInf
      case (NUIntConst(n), NegInf) if n > 0 => NegInf
      case (NUIntConst(_), NegInf) => PosInf
      case (Inf, UInt) | (UInt, Inf) => Top
      case (Inf, UIntConst(_)) => Inf
      case (Inf, NUIntConst(_)) => Inf
      case (Inf, NUInt) | (NUInt, Inf) => Inf
      case (UIntConst(_), Inf) => Inf
      case (NUIntConst(_), Inf) => Inf
      /* 11.5.1 sixth */
      case (UIntConst(0), _) => alpha(0)
      case (_, UIntConst(0)) => alpha(0)
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 * n2)
      case (UIntConst(n1), UInt) => UInt
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 * n2)
      case (UIntConst(n1), NUInt) => NUInt
      case (UInt, UIntConst(n2)) => UInt
      case (UInt, UInt) => UInt
      case (UInt, NUIntConst(n2)) => Top
      case (UInt, NUInt) => Top
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 * n2)
      case (NUIntConst(n1), UInt) => Top
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 * n2)
      case (NUIntConst(n1), NUInt) => Top
      case (NUInt, _) => Top
      case _ => Top
    }

    def div(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      /* 11.5.2 first */
      case (NaN, _) | (_, NaN) => NaN
      /* 11.5.2 third */
      case (PosInf | NegInf | Inf, PosInf | NegInf | Inf) => NaN
      /* 11.5.2 fourth */
      case (PosInf, UIntConst(0)) => PosInf
      case (NegInf, UIntConst(0)) => NegInf
      case (Inf, UIntConst(0)) => Inf
      /* 11.5.2 fifth */
      case (PosInf, UIntConst(_)) => PosInf
      case (PosInf, UInt) => PosInf
      case (PosInf, NUIntConst(n)) if n > 0 => PosInf
      case (PosInf, NUIntConst(_)) => NegInf
      case (PosInf, NUInt) => Inf
      case (NegInf, UIntConst(_)) => NegInf
      case (NegInf, UInt) => NegInf
      case (NegInf, NUIntConst(n)) if n > 0 => NegInf
      case (NegInf, NUIntConst(_)) => PosInf
      case (NegInf, NUInt) => Inf
      case (Inf, _) => Inf
      /* 11.5.2 sixth */
      case (_, PosInf) => alpha(0)
      case (_, NegInf) => alpha(0)
      case (_, Inf) => alpha(0)
      /* 11.5.2  seventh */
      case (UIntConst(0), UIntConst(0)) => NaN
      case (UIntConst(0), _) => alpha(0)
      /* 11.5.2  eighth */
      case (UIntConst(n), UIntConst(0)) => PosInf
      case (UInt, UIntConst(0)) => Top
      case (NUIntConst(n), UIntConst(0)) if n > 0 => PosInf
      case (NUIntConst(n), UIntConst(0)) => NegInf
      case (NUInt, UIntConst(0)) => Inf
      /* 11.5.2  ninth */
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 / n2)
      case (UIntConst(n1), UInt) => Top
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 / n2)
      case (UIntConst(n1), NUInt) => Top
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 / n2)
      case (NUIntConst(n1), UInt) => Top
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 / n2)
      case (NUIntConst(n1), NUInt) => Top
      case (UInt, _) => Top
      case (NUInt, UIntConst(n2)) => NUInt
      case (NUInt, _) => Top
      case _ => Top
    }

    def mod(that: AbsNumber): AbsNumber = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      /* 11.5.3 first */
      case (NaN, _) | (_, NaN) => NaN
      /* 11.5.3 third */
      case (PosInf | NegInf | Inf, _) => NaN
      case (_, UIntConst(0)) => NaN
      /* 11.5.3 fifth */
      case (UIntConst(0), _) => alpha(0)
      /* 11.5.3 fourth */
      case (_, PosInf | NegInf | Inf) => that
      /* 11.5.3 sixth */
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 % n2)
      case (UIntConst(n1), UInt) => Top
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 % n2)
      case (UIntConst(n1), NUInt) => Top
      case (UInt, UIntConst(n2)) => UInt
      case (UInt, _) => Top
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 % n2)
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 % n2)
      case (NUIntConst(n1), _) => Top
      case (NUInt, UIntConst(n2)) => NUInt
      case (NUInt, _) => Top
      case _ => Top
    }
  }
}
