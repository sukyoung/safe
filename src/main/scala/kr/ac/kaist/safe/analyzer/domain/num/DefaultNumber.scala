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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.errors.error.AbsNumParseError
import spray.json._

// default number abstract domain
object DefaultNumber extends NumDomain {
  case object Top extends Elem
  case object Bot extends Elem
  override case object Inf extends Elem
  override case object PosInf extends Elem
  override case object NegInf extends Elem
  override case object NaN extends Elem
  case object UInt extends Elem
  case object NUInt extends Elem
  case class UIntConst(value: Long) extends Elem
  case class NUIntConst(value: Double) extends Elem

  def alpha(num: Num): Elem = num.num match {
    case num if num.isNaN => NaN
    case Double.NegativeInfinity => NegInf
    case Double.PositiveInfinity => PosInf
    case num =>
      val uint = num.toLong
      if ((num == uint) && (uint > 0 || (num compare 0.0) == 0)) UIntConst(uint)
      else NUIntConst(num)
  }

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case JsString("⊥") => Bot
    case JsString("+inf|-inf") => Inf
    case JsString("+inf") => PosInf
    case JsString("-inf") => NegInf
    case JsString("NaN") => NaN
    case JsString("uint") => UInt
    case JsString("nuint") => NUInt
    case JsString("-0") => NUIntConst(-0.0)
    case JsArray(Vector(JsString("uint"), JsNumber(v))) => UIntConst(v.toLong)
    case JsArray(Vector(JsString("nuint"), JsNumber(v))) => NUIntConst(v.toDouble)
    case _ => throw AbsNumParseError(v)
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Num] = this match {
      case Bot => ConFin()
      case Inf => ConFin(Double.PositiveInfinity, Double.NegativeInfinity)
      case PosInf => ConFin(Double.PositiveInfinity)
      case NegInf => ConFin(Double.NegativeInfinity)
      case NaN => ConFin(Double.NaN)
      case UIntConst(v) => ConFin(v)
      case NUIntConst(v) => ConFin(v)
      case Top | UInt | NUInt => ConInf
    }

    def getSingle: ConSingle[Num] = this match {
      case Bot => ConZero()
      case PosInf => ConOne(Double.PositiveInfinity)
      case NegInf => ConOne(Double.NegativeInfinity)
      case NaN => ConOne(Double.NaN)
      case UIntConst(v) => ConOne(v)
      case NUIntConst(v) => ConOne(v)
      case Top | UInt | NUInt | Inf => ConMany()
    }

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Top) => true
      case (left, right) if left == right => true
      case (PosInf, Inf) => true
      case (NegInf, Inf) => true
      case (UIntConst(_), UInt) => true
      case (NUIntConst(_), NUInt) => true
      case _ => false
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (left, right) if left ⊑ right => right
      case (left, right) if right ⊑ left => left
      case (PosInf, NegInf) => Inf
      case (NegInf, PosInf) => Inf
      case (UIntConst(a), UIntConst(b)) if a == b => this
      case (UIntConst(_), UIntConst(_)) => UInt
      case (NUIntConst(a), NUIntConst(b)) if a == b => this
      case (NUIntConst(_), NUIntConst(_)) => NUInt
      case _ => Top
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (left, right) if left ⊑ right => left
      case (left, right) if right ⊑ left => right
      case _ => Bot
    }

    // 9.2 ToBoolean
    override def ToBoolean: AbsBool = this match {
      case Bot => AbsBool.Bot
      // The result is false if the argument is +0, -0, or NaN;
      case UIntConst(0) | NUIntConst(0) | NaN => AF
      // otherwise the result is true.
      case UIntConst(_) | NUIntConst(_) | PosInf | NegInf | Inf => AT
      // other cases
      case _ => AbsBool.Top
    }

    // 9.4 ToInteger
    override def ToInteger: Elem = this match {
      case Bot => Bot
      // 2. If number is NaN, return +0.
      case NaN => UIntConst(0)
      // 3. If number is +0, -0, +Infinity, or Infinity, return number.
      case UIntConst(0) | NUIntConst(0) | PosInf | NegInf | Inf => this
      // 4. Return the result of computing sign(number) * floor(abs(number)).
      case UIntConst(_) | UInt => this
      case NUIntConst(n) => alpha(Num(n).ToInteger)
      // other cases
      case _ => Top
    }

    override def <(that: Elem): AbsBool = (this, that) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      case (NaN, _) | (_, NaN) => AF
      case (PosInf, _) => AF
      case (_, PosInf) => AT
      case (_, NegInf) => AF
      case (NegInf, _) => AT
      case (UIntConst(n1), UIntConst(n2)) => AbsBool(n1 < n2)
      case (UIntConst(n1), NUIntConst(n2)) => AbsBool(n1 < n2)
      case (NUIntConst(n1), UIntConst(n2)) => AbsBool(n1 < n2)
      case (NUIntConst(n1), NUIntConst(n2)) => AbsBool(n1 < n2)
      case (UInt, UIntConst(0)) => AF
      case (UInt, NUIntConst(n2)) if n2 <= 0 => AF
      case _ => AbsBool.Top
    }

    override def StrictEquals(that: Elem): AbsBool = (this, that) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      case (NaN, _) | (_, NaN) => AF
      case (UIntConst(n1), UIntConst(n2)) => AbsBool(n1 == n2)
      case (UIntConst(n1), NUIntConst(n2)) => AbsBool(n1 == n2)
      case (NUIntConst(n1), UIntConst(n2)) => AbsBool(n1 == n2)
      case (NUIntConst(n1), NUIntConst(n2)) => AbsBool(n1 == n2)
      case (NegInf, NegInf) | (PosInf, PosInf) => AT
      case (left, right) if !(left ⊑ right) && !(right ⊑ left) => AF
      case _ => AbsBool.Top
    }

    /* Operators */
    private def toString(m: Double): String = {
      // 5. Otherwise, let n, k, and s be integers such that k >= 1,
      //    10^(k-1) <= s < 10^k, the Number value for s * 10^(n-k) is m,
      //    and k is as small as possible.
      //    Note that k is the number of digits in the decimal representation of s,
      //    that s is not divisible by 10, and that the least significant digit of s
      //    is not necessarily uniquely determined by these criteria.
      var s = BigDecimal(m)
      var n = 0
      while (s % 10 == BigDecimal(0) || s % 1 != BigDecimal(0)) {
        if (s % 10 == BigDecimal(0)) {
          s /= 10
          n += 1
        } else {
          s *= 10
          n -= 1
        }
      }
      var sLong = s.toLong
      var k = 0
      while (s >= BigDecimal(1)) {
        s /= 10
        k += 1
      }
      n += k
      def getStr(number: Long): String = {
        var str = ""
        var sLong = number
        while (sLong > 0) {
          str += (sLong % 10).toString
          sLong /= 10
        }
        str.reverse
      }
      def getSign(n: Int): Char = {
        if (n - 1 > 0) '+'
        else '-'
      }
      if (k <= n && n <= 21) {
        // 6. If k <= n <= 21, return the String consisting of the k digits of the decimal representation of s
        //    (in order, with no leading zeroes), followed by n-k occurrences of the character '0'.
        getStr(sLong) + ("0" * (n - k))
      } else if (0 < n && n <= 21) {
        // 7. If 0 < n <= 21, return the String consisting of the most significant n digits of
        //    the decimal representation of s, followed by a decimal point '.',
        //    followed by the remaining k-n digits of the decimal representation of s.
        val str = getStr(sLong)
        str.substring(0, n) + '.' + str.substring(n)
      } else if (-6 < n && n <= 0) {
        // 8. If -6 < n <= 0, return the String consisting of the character '0', followed by a decimal point '.',
        //    followed by -n occurrences of the character '0', followed by the k digits of the decimal representation of s.
        "0." + ("0" * (-n)) + getStr(sLong)
      } else if (k == 1) {
        // 9. Otherwise, if k = 1, return the String consisting of the single digit of s,
        //    followed by lowercase character 'e', followed by a plus sign '+' or minus sign '-'
        //    according to whether n-1 is positive or negative, followed by the decimal representation of
        //    the integer abs(n-1) (with no leading zeroes).
        getStr(sLong) + "e" + getSign(n) + math.abs(n - 1).toString
      } else {
        // 10. Return the String consisting of the most significant digit of the decimal representation of s,
        //     followed by a decimal point '.', followed by the remaining k-1 digits of the decimal representation of s,
        //     followed by the lowercase character 'e', followed by a plus sign '+' or minus sign '-' according to
        //     whether n-1 is positive or negative, followed by the decimal representation of the integer abs(n-1) (with no leading zeroes).
        val str = getStr(sLong)
        str.substring(0, 1) + '.' + str.substring(1) + 'e' + getSign(n) + math.abs(n - 1).toString
      }
    }

    // 9.8.1 ToString Applied to the Number Type
    override def ToString: AbsStr = this match {
      case Bot => AbsStr.Bot
      // 1. If m is NaN, return the String "NaN".
      case NaN => AbsStr("NaN")
      // 2. If m is +0 or -0, return the String "0".
      case UIntConst(0) | NUIntConst(0) => AbsStr("0")
      // 3. If m is less than zero, return the String concatenation of the String "-" and ToString( m).
      case NUIntConst(n) if n < 0 => AbsStr("-") concat alpha(-n).ToString
      case NegInf => AbsStr("-Infinity")
      // 4. If m is infinity, return the String "Infinity".
      case PosInf => AbsStr("Infinity")
      // 5. Otherwise,
      case UIntConst(n) => AbsStr(toString(n.toDouble))
      case NUIntConst(n) => AbsStr(toString(n))
      case _ => AbsStr.Number
    }

    private def modulo(posInt: Long, bound: Long): Long = {
      val value = posInt % bound
      if (value < 0) value + bound
      else value
    }

    // 9.5 ToInt32: (Signed 32 Bit Integer)
    override def ToInt32: Elem = {
      def helper(number: Double): Long = {
        val bound = 0x100000000L // 2^32
        // 3. Let posInt be sign(number) * floor(abs(number)).
        val posInt: Long = (math.signum(number) * math.floor(math.abs(number))).toLong
        // 4. Let int32bit be posInt modulo 2^32;
        val int32bit = modulo(posInt, bound)
        // 5. If int32bit is greater than or equal to 2^31, return int32bit - 2^32, otherwise return int32bit.
        if (int32bit > bound / 2) int32bit - bound
        else int32bit
      }
      this match {
        case Bot => Bot
        // 2. If number is NaN, +0, -0, +Infinity, or Infinity, return +0.
        case NaN | UIntConst(0) | NUIntConst(0) | PosInf | NegInf | Inf => UIntConst(0)
        // by helper
        case UIntConst(n) => alpha(helper(n.toDouble))
        case NUIntConst(n) => alpha(helper(n))
        // other cases
        case _ => Top
      }
    }

    // 9.6 ToUint32: (Unsigned 32 Bit Integer)
    override def ToUint32: Elem = {
      def helper(number: Double): Long = {
        val bound = 0x100000000L // 2^32
        // 3. Let posInt be sign(number) * floor(abs(number)).
        val posInt: Long = (math.signum(number) * math.floor(math.abs(number))).toLong
        // 4. Let int32bit be posInt modulo 2^32;
        val int32bit = modulo(posInt, bound)
        // 5. Return int32bit.
        int32bit
      }
      this match {
        case Bot => Bot
        // 2. If number is NaN, +0, -0, +Infinity, or Infinity, return +0.
        case NaN | UIntConst(0) | NUIntConst(0) | PosInf | NegInf | Inf => UIntConst(0)
        // by helper
        case UIntConst(n) => UIntConst(helper(n.toDouble))
        case NUIntConst(n) => UIntConst(helper(n))
        // other cases
        case _ => UInt
      }
    }

    // 9.7 ToUint16: (Unsigned 16 Bit Integer)
    override def ToUint16: Elem = {
      def helper(number: Double): Long = {
        val bound = 0x10000L // 2^16
        // 3. Let posInt be sign(number) * floor(abs(number)).
        val posInt: Long = (math.signum(number) * math.floor(math.abs(number))).toLong
        // 4. Let int16bit be posInt modulo 2^16;
        val int16bit = modulo(posInt, bound)
        // 5. Return int16bit.
        int16bit
      }
      this match {
        case Bot => Bot
        // 2. If number is NaN, +0, -0, +Infinity, or Infinity, return +0.
        case NaN | UIntConst(0) | NUIntConst(0) | PosInf | NegInf | Inf => UIntConst(0)
        // by helper
        case UIntConst(n) => UIntConst(helper(n.toDouble))
        case NUIntConst(n) => UIntConst(helper(n))
        // other cases
        case _ => UInt
      }
    }

    // 9.12 The SameValue Algorithm
    override def SameValue(that: Elem): AbsBool = (this, that) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      // a. If x is NaN and y is NaN, return true.
      case (NaN, NaN) => AT
      case (NaN, Top) | (Top, NaN) => AbsBool.Top
      // b. If x is +0 and y is -0, return false.
      case (UIntConst(0), NUIntConst(0)) => AF
      case (UIntConst(0), NUInt) | (UIntConst(0), Top) => AbsBool.Top
      case (UInt, NUIntConst(0)) | (Top, NUIntConst(0)) => AbsBool.Top
      // c. If x is -0 and y is +0, return false.
      case (NUIntConst(0), UIntConst(0)) => AF
      case (NUIntConst(0), UInt) | (NUIntConst(0), Top) => AbsBool.Top
      case (NUInt, UIntConst(0)) | (Top, UIntConst(0)) => AbsBool.Top
      // d. If x is the same Number value as y, return true.
      // e. Return false.
      case (left, right) => left StrictEquals right
    }

    // 11.4.7 Unary-Operator
    override def unary_-(): Elem = this match {
      case Bot => Bot
      // 3. If oldValue is NaN, return NaN.
      case NaN => NaN
      // 4. Return the result of negating oldValue
      case PosInf => NegInf
      case NegInf => PosInf
      case UInt => NUInt
      case UIntConst(n) => alpha(-(n.toDouble))
      case NUIntConst(n) => alpha(-n)
      // other cases
      case _ => this
    }

    // 15.8.2.1 abs (x)
    def abs: Elem = this match {
      case Bot => Bot
      // If x is NaN, the result is NaN.
      case NaN => NaN
      // If x is -0, the result is +0.
      case NUIntConst(0) => UIntConst(0)
      // If x is -Infinity, the result is +Infinity.
      case NegInf | Inf => PosInf
      // other cases
      case NUIntConst(n) => alpha(math.abs(n))
      case NUInt => Top
      case _ => this
    }

    // TODO 15.8.2.2 acos (x)
    def acos: Elem = this match {
      case Top => Top
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

    // TODO 15.8.2.3 asin (x)
    def asin: Elem = {
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

    // TODO 15.8.2.4 atan (x)
    def atan: Elem = {
      this match {
        case Bot | NaN => this
        case Inf => NUInt
        case PosInf => alpha(scala.math.Pi / 2)
        case NegInf => alpha(-scala.math.Pi / 2)
        case UIntConst(n) => alpha(Math.atan(n))
        case NUIntConst(n) => alpha(Math.atan(n))
        case NUInt => Top
        case _ => Top
      }
    }

    //TODO 15.8.2.5 atan2 (y, x)
    def atan2(that: Elem): Elem = (this, that) match {
      case (NaN, _)
        | (_, NaN) => NaN
      case (Bot, _) | (_, Bot) => Bot
      case (Top, _) | (_, Top) => Top
      // if (this > 0) & (that == + 0 | that == - 0) then PI/2
      case (UIntConst(x), UIntConst(0)) if x > 0 => alpha(scala.math.Pi / 2)
      case (UIntConst(x), NUIntConst(0)) if x > 0 => alpha(scala.math.Pi / 2)
      case (NUIntConst(x), UIntConst(0)) if x > 0 => alpha(scala.math.Pi / 2)
      case (NUIntConst(x), NUIntConst(0)) if x > 0 => alpha(scala.math.Pi / 2)
      case (UInt, UIntConst(0)) => Top
      case (UInt, NUIntConst(0)) => NUInt
      case (PosInf, UIntConst(0)) | (PosInf, NUIntConst(0)) => alpha(scala.math.Pi / 2)
      // if (this == + 0) & (that >= + 0) then 0
      case (UIntConst(0), UIntConst(_)) => alpha(0.0)
      case (UIntConst(0), NUIntConst(x)) if x > 0 => alpha(0.0)
      case (UIntConst(0), UInt) => alpha(0.0)
      case (UIntConst(0), PosInf) => alpha(0.0)
      // if (this == + 0) & (that <= - 0) then PI
      case (UIntConst(0), NUIntConst(0)) => alpha(scala.math.Pi)
      case (UIntConst(0), NUIntConst(x)) if x < 0 => alpha(scala.math.Pi)
      case (UIntConst(0), NegInf) => alpha(scala.math.Pi)
      // if (this == + 0) & (that ? 0) then (0 or PI)
      case (UIntConst(0), NUInt) | (UIntConst(0), Inf) => alpha(0.0) ⊔ alpha(scala.math.Pi)
      // if (this == - 0) & (that >= +0) then - 0
      case (NUIntConst(0), UIntConst(_)) => alpha(-0.0)
      case (NUIntConst(0), NUIntConst(x)) if x > 0 => alpha(-0.0)
      case (NUIntConst(0), UInt) => alpha(-0.0)
      case (NUIntConst(0), PosInf) => alpha(-0.0)
      // if (this == - 0) & (that <= - 0) then - PI
      case (NUIntConst(0), NUIntConst(0)) => alpha(-scala.math.Pi)
      case (NUIntConst(0), NUIntConst(x)) if x < 0 => alpha(-scala.math.Pi)
      case (NUIntConst(0), NegInf) => alpha(-scala.math.Pi)
      // if (this == - 0) & (that ? 0) then (- 0 or - PI)
      case (NUIntConst(0), NUInt) | (UIntConst(0), Inf) => alpha(-0.0) ⊔ alpha(-scala.math.Pi)
      // if (this < 0) & (that == + 0 | that == - 0) then - PI/2
      case (NUIntConst(x), UIntConst(0)) if x < 0 => alpha(-scala.math.Pi / 2)
      case (NUIntConst(x), NUIntConst(0)) if x < 0 => alpha(-scala.math.Pi / 2)
      case (NegInf, UIntConst(0)) => alpha(-scala.math.Pi / 2)
      case (NegInf, NUIntConst(0)) => alpha(-scala.math.Pi / 2)

      case (UIntConst(y), PosInf) if y > 0 => alpha(0)
      case (NUIntConst(y), PosInf) if y > 0 => alpha(0)
      case (UIntConst(y), NegInf) if y > 0 => alpha(scala.math.Pi)
      case (NUIntConst(y), NegInf) if y > 0 => alpha(scala.math.Pi)
      case (NUIntConst(y), PosInf) if y < 0 => alpha(-0.0)
      case (NUIntConst(y), NegInf) if y < 0 => alpha(-scala.math.Pi)
      case (PosInf, UInt | NUInt | UIntConst(_) | NUIntConst(_)) => alpha(scala.math.Pi / 2)
      case (NegInf, UInt | NUInt | UIntConst(_) | NUIntConst(_)) => alpha(-scala.math.Pi / 2)
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

    // TODO 15.8.2.6 ceil (x)
    def ceil: Elem = {
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

    // TODO 15.8.2.7 cos (x)
    def cos: Elem = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case UIntConst(n) => alpha(Math.cos(n))
        case NUIntConst(n) => alpha(Math.cos(n))
        case _ => Top
      }
    }

    // TODO 15.8.2.8 exp (x)
    def exp: Elem = {
      this match {
        case NaN
          | PosInf => this
        case UIntConst(0) | NUIntConst(0) => alpha(1)
        case NegInf => alpha(0)
        case UIntConst(n) => alpha(Math.exp(n))
        case NUIntConst(n) => alpha(Math.exp(n))
        case _ => Top
      }
    }

    // TODO 15.8.2.9 floor (x)
    def floor: Elem = {
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

    // TODO 15.8.2.10 log (x)
    def log: Elem = {
      this match {
        case NaN => this
        case NegInf => NaN
        case NUIntConst(x) if x < 0 => NaN
        case UIntConst(0) | NUIntConst(0) => NegInf
        case PosInf => this
        case UIntConst(n) => alpha(scala.math.log(n))
        case NUIntConst(n) => alpha(scala.math.log(n))
        case _ => Top
      }
    }

    // TODO 15.8.2.13 pow (x, y)
    def pow(that: Elem): Elem = (this, that) match {
      case (UInt, UInt) => Top
      case (UInt, UIntConst(_)) => Top
      case (UIntConst(_), UInt) => Top
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

    // TODO 15.8.2.15 round (x)
    def round: Elem = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf
          | UInt
          | UIntConst(_) => this
        case NUIntConst(0) => NUIntConst(0)
        case NUIntConst(n) => alpha(scala.math.round(n))
        case _ => Top
      }
    }

    // TODO 15.8.2.16 sin (x)
    def sin: Elem = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case NUInt => Top
        case UIntConst(n) => alpha(scala.math.sin(n))
        case NUIntConst(n) => alpha(scala.math.sin(n))
        case _ => Top
      }
    }

    // TODO 15.8.2.17 sqrt (x)
    def sqrt: Elem = {
      this match {
        case NaN
          | NegInf => NaN
        case PosInf => PosInf
        case UIntConst(n) => alpha(scala.math.sqrt(n))
        case NUIntConst(n) if n < 0 => NaN
        case NUIntConst(n) => alpha(scala.math.sqrt(n))
        case _ => Top
      }
    }

    // TODO 15.8.2.18 tan (x)
    def tan: Elem = {
      this match {
        case NaN
          | Inf
          | PosInf
          | NegInf => NaN
        case NUInt => Top
        case UIntConst(n) => alpha(scala.math.tan(n))
        case NUIntConst(n) => alpha(scala.math.tan(n))
        case _ => Top
      }
    }

    // 11.4.8 Bitwise NOT Operator ( ~ )
    // 1. Let expr be the result of evaluating UnaryExpression.
    // 2. Let oldValue be ToInt32(GetValue(expr)).
    override def unary_~(): Elem = ToInt32 match {
      case Bot => Bot
      // 3. Return the result of applying bitwise complement to oldValue.
      case UIntConst(n) => alpha(~(n.toInt))
      case NUIntConst(n) => alpha(~(n.toInt))
      case UInt => NUInt
      case _ => Top
    }

    private def binaryBitwiseOp(left: Elem, right: Elem)(op: (Int, Int) => Int): Elem = (left.ToInt32, right.ToInt32) match {
      case (UIntConst(l), UIntConst(r)) => alpha(op(l.toInt, r.toInt))
      case (UIntConst(l), NUIntConst(r)) => alpha(op(l.toInt, r.toInt))
      case (NUIntConst(l), UIntConst(r)) => alpha(op(l.toInt, r.toInt))
      case (NUIntConst(l), NUIntConst(r)) => alpha(op(l.toInt, r.toInt))
      case _ => Top
    }

    // 11.10 BinaryBitwiseOperators
    override def |(that: Elem): Elem = binaryBitwiseOp(this, that)(_ | _)
    override def &(that: Elem): Elem = binaryBitwiseOp(this, that)(_ & _)
    override def ^(that: Elem): Elem = binaryBitwiseOp(this, that)(_ ^ _)

    private def binaryShiftOp(
      left: Elem,
      right: Elem,
      signed: Boolean = true
    )(op: (Int, Int) => Long): Elem = (left.ToUint32, right.ToUint32) match {
      case (UIntConst(l), UIntConst(r)) =>
        val bound = 0x100000000L
        val l32 = l.toInt
        val r32 = (r & 0x1F).toInt
        val result = op(l32, r32)
        if (!signed && result < 0) alpha(bound + result.toLong)
        else alpha(result)
      case _ => Top
    }

    // 11.7.1 The Left Shift Operator ( << )
    override def <<(shift: Elem): Elem = binaryShiftOp(this, shift)(_ << _)
    // 11.7.2 The Signed Right Shift Operator ( >> )
    override def >>(shift: Elem): Elem = binaryShiftOp(this, shift)(_ >> _)
    // 11.7.3 The Unsigned Right Shift Operator ( >>> )
    override def >>>(shift: Elem): Elem = binaryShiftOp(this, shift, false)(_ >>> _)

    // 11.6.3 Applying the Additive Operators to Numbers
    override def +(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      // If either operand is NaN, the result is NaN.
      case (NaN, _) | (_, NaN) => NaN
      // The sum of two infinities of opposite sign is NaN.
      case (NegInf, PosInf) | (PosInf, NegInf) => NaN
      // The sum of two infinities of the same sign is the infinity of that sign.
      case (NegInf, NegInf) | (PosInf, PosInf) => this
      // The sum of an infinity and a finite value is equal to the infinite operand.
      case (NegInf, _) | (_, NegInf) => NegInf
      case (PosInf, _) | (_, PosInf) => PosInf
      // The sum of two negative zeroes is -0.
      case (NUIntConst(0), NUIntConst(0)) => NUIntConst(0)
      // The sum of two positive zeroes, or of two zeroes of opposite sign, is +0.
      case (NUIntConst(0) | UIntConst(0), NUIntConst(0) | UIntConst(0)) => UIntConst(0)
      // The sum of a zero and a nonzero finite value is equal to the nonzero operand.
      case (NUIntConst(0) | UIntConst(0), right) => right
      case (left, NUIntConst(0) | UIntConst(0)) => left
      // The sum of two nonzero finite values of the same magnitude and opposite sign is +0.
      // In the remaining cases, add two numbers.
      case (UIntConst(l), UIntConst(r)) => alpha(l + r.toDouble)
      case (UIntConst(l), NUIntConst(r)) => alpha(l + r)
      case (NUIntConst(l), UIntConst(r)) => alpha(l + r)
      case (NUIntConst(l), NUIntConst(r)) => alpha(l + r)
      case _ => Top
    }

    override def -(that: Elem): Elem = this + (-that)

    override def *(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      /* 11.5.1 first */
      case (NaN, _) | (_, NaN) => NaN
      /* 11.5.1 third */
      case (PosInf | NegInf, UIntConst(0) | NUIntConst(0)) => NaN
      case (UIntConst(0) | NUIntConst(0), PosInf | NegInf) => NaN
      /* 11.5.1 fourth */
      case (PosInf, PosInf) => PosInf
      case (PosInf, NegInf) => NegInf
      case (NegInf, NegInf) => PosInf
      case (NegInf, PosInf) => NegInf
      /* 11.5.1 fifth */
      case (PosInf, UIntConst(_)) => PosInf
      case (PosInf, NUIntConst(n)) if n > 0 => PosInf
      case (PosInf, NUIntConst(_)) => NegInf
      case (NegInf, UIntConst(_)) => NegInf
      case (NegInf, NUIntConst(n)) if n > 0 => NegInf
      case (NegInf, NUIntConst(_)) => PosInf
      case (UIntConst(_), PosInf) => PosInf
      case (NUIntConst(n), PosInf) if n > 0 => PosInf
      case (NUIntConst(_), PosInf) => NegInf
      case (UIntConst(_), NegInf) => NegInf
      case (NUIntConst(n), NegInf) if n > 0 => NegInf
      case (NUIntConst(_), NegInf) => PosInf
      /* 11.5.1 sixth */
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 * n2.toDouble)
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 * n2)
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 * n2)
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 * n2)
      case _ => Top
    }

    override def /(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      /* 11.5.2 first */
      case (NaN, _) | (_, NaN) => NaN
      /* 11.5.2 third */
      case (PosInf | NegInf, PosInf | NegInf) => NaN
      /* 11.5.2 fourth */
      case (PosInf, UIntConst(0)) => PosInf
      case (PosInf, NUIntConst(0)) => NegInf
      case (NegInf, UIntConst(0)) => NegInf
      case (NegInf, NUIntConst(0)) => PosInf
      /* 11.5.2 fifth */
      case (PosInf, UIntConst(_)) => PosInf
      case (PosInf, NUIntConst(n)) if n > 0 => PosInf
      case (PosInf, NUIntConst(_)) => NegInf
      case (NegInf, UIntConst(_)) => NegInf
      case (NegInf, NUIntConst(n)) if n > 0 => NegInf
      case (NegInf, NUIntConst(_)) => PosInf
      /* 11.5.2 sixth */
      case (UIntConst(_), PosInf) => alpha(0)
      case (NUIntConst(n), PosInf) if n > 0 => alpha(0)
      case (NUIntConst(_), PosInf) => alpha(-0.0)
      case (UIntConst(_), NegInf) => alpha(-0.0)
      case (NUIntConst(n), NegInf) if n > 0 => alpha(-0.0)
      case (NUIntConst(_), NegInf) => alpha(0)
      /* 11.5.2  seventh */
      case (UIntConst(0) | NUIntConst(0), UIntConst(0) | NUIntConst(0.0)) => NaN
      case (UIntConst(0), UIntConst(_)) => alpha(0)
      case (UIntConst(0), NUIntConst(n)) if n > 0 => alpha(0)
      case (UIntConst(0), NUIntConst(_)) => alpha(-0.0)
      case (NUIntConst(0), UIntConst(_)) => alpha(-0.0)
      case (NUIntConst(0), NUIntConst(n)) if n > 0 => alpha(-0.0)
      case (NUIntConst(0), NUIntConst(_)) => alpha(0)
      /* 11.5.2  eighth */
      case (UIntConst(_), UIntConst(0)) => PosInf
      case (UIntConst(_), NUIntConst(0)) => NegInf
      case (NUIntConst(n), UIntConst(0)) if n > 0 => PosInf
      case (NUIntConst(n), NUIntConst(0)) if n > 0 => NegInf
      case (NUIntConst(_), UIntConst(0)) => NegInf
      case (NUIntConst(_), NUIntConst(0)) => PosInf
      /* 11.5.2  ninth */
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 / n2.toDouble)
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 / n2)
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 / n2)
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 / n2)
      case _ => Top
    }

    override def %(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      /* 11.5.3 first */
      case (NaN, _) | (_, NaN) => NaN
      /* 11.5.3 third */
      case (PosInf | NegInf, _) => NaN
      case (_, UIntConst(0) | NUIntConst(0)) => NaN
      /* 11.5.3 fourth */
      case (UIntConst(_) | NUIntConst(_), PosInf | NegInf | Inf) => this
      /* 11.5.3 fifth */
      case (UIntConst(0) | NUIntConst(0), UIntConst(_) | NUIntConst(_)) => this
      /* 11.5.3 sixth */
      case (UIntConst(n1), UIntConst(n2)) => alpha(n1 % n2)
      case (UIntConst(n1), NUIntConst(n2)) => alpha(n1 % n2)
      case (NUIntConst(n1), UIntConst(n2)) => alpha(n1 % n2)
      case (NUIntConst(n1), NUIntConst(n2)) => alpha(n1 % n2)
      case _ => Top
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
      case NUIntConst(0) => "-0"
      case NUIntConst(v) =>
        if (Math.floor(v) == v && !v.isInfinity) v.toLong.toString
        else v.toString
    }

    def toJson: JsValue = this match {
      case Top => JsString("⊤")
      case Bot => JsString("⊥")
      case Inf => JsString("+inf|-inf")
      case PosInf => JsString("+inf")
      case NegInf => JsString("-inf")
      case NaN => JsString("NaN")
      case UInt => JsString("uint")
      case NUInt => JsString("nuint")
      case UIntConst(n) => JsArray(JsString("uint"), JsNumber(n))
      case NUIntConst(n) if isNegZero(n) => JsString("-0")
      case NUIntConst(n) => JsArray(JsString("nuint"), JsNumber(n))
    }
  }

  private def isNegZero(v: Double): Boolean = 1 / v == Double.NegativeInfinity
}
