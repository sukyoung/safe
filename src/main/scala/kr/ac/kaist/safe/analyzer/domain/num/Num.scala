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

// concrete number type
case class Num(num: Double) extends PValue {
  import Num._

  // constructor for Long
  def this(long: Long) = this(long.toDouble)
  def this(int: Int) = this(int.toDouble)

  // 9.8 ToString
  // 9.8.1 ToString Applied to the Number Type
  override def ToString: Str = this match {
    // 1. If m is NaN, return the String "NaN".
    case NaN => Str("NaN")
    // 2. If m is +0 or -0, return the String "0".
    case PosZero | NegZero => Str("0")
    // 3. If m is less thanezero, return the String concatenation of the String "-"
    //    and ToString(-m).
    case Num(n) if n < 0 => Str("-" + Num(-n).ToString.str)
    // 4. If m is infinity, return the String "Infinity".
    case PosInf | NegInf => Str("Infinity")
    case Num(n) => Str(toStringHelper(n))
  }
  private def toStringHelper(m: Double): String = {
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

  // 11.6.1 The Addition operator ( + )
  def +(that: Num): Num = Num(this.num + that.num)

  // 11.11 BinaryLogicalOperators
  def &&(that: Num): Num = if (ToBoolean == F) this else that
  def ||(that: Num): Num = if (ToBoolean == T) this else that

  // internal helper
  def sign: Num = Num(math.signum(num))
  def floor: Num = Num(math.floor(num))
  def abs: Num = Num(math.abs(num))

  // modulo
  def modulo(bound: Long): Long = {
    val value = num.toLong % bound
    if (value < 0) value + bound
    else value
  }

  // toString
  override def toString: String = num.toString

  // equals
  override def equals(that: Any): Boolean = that match {
    case Num(k) => (Num.isNegZero(k), Num.isNegZero(num)) match {
      case (true, true) => true
      case (false, false) => k == num
      case _ => false
    }
    case _ => false
  }
}
object Num {
  val PosZero: Num = Num(+0.0)
  val NegZero: Num = Num(-0.0)
  val NaN: Num = Num(Double.NaN)
  val PosInf: Num = Num(Double.PositiveInfinity)
  val NegInf: Num = Num(Double.NegativeInfinity)
  private def isNegZero(k: Double): Boolean = 1 / k == Double.NegativeInfinity
}
