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

////////////////////////////////////////////////////////////////////////////////
// concrete number type
////////////////////////////////////////////////////////////////////////////////
case class Num(num: Double) extends PValue

////////////////////////////////////////////////////////////////////////////////
// number abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsNumber extends AbsDomain[Num, AbsNumber] {
  def ===(that: AbsNumber): AbsBool
  def <(that: AbsNumber): AbsBool

  def isPositive: Boolean
  def isNegative: Boolean
  def isZero: Boolean
  def isPositiveZero: Boolean
  def isNegativeZero: Boolean

  // Abstraction of step 2 - 4 in section 9.5, ECMAScript 5.1
  def toInteger: AbsNumber
  // Abstraction of step 2 - 5 in section 9.6, ECMAScript 5.1
  def toInt32: AbsNumber
  // Abstraction of step 2 - 5 in section 9.7, ECMAScript 5.1
  def toUInt32: AbsNumber
  // Abstraction of step 2 - 5 in section 9.8, ECMAScript 5.1
  def toUInt16: AbsNumber

  def toAbsBoolean: AbsBool
  def toAbsString: AbsString

  // Abstraction of step 4.a - 4.e in section 9.12, ECMAScript 5.1
  // This algorithm differs from the strict equal(===) in its treatment of signed zeros and NaN
  def sameValue(that: AbsNumber): AbsBool

  def negate: AbsNumber
  def abs: AbsNumber
  def acos: AbsNumber
  def asin: AbsNumber
  def atan: AbsNumber
  def atan2(that: AbsNumber): AbsNumber
  def ceil: AbsNumber
  def cos: AbsNumber
  def exp: AbsNumber
  def floor: AbsNumber
  def log: AbsNumber
  def pow(that: AbsNumber): AbsNumber
  def round: AbsNumber
  def sin: AbsNumber
  def sqrt: AbsNumber
  def tan: AbsNumber
  def bitNegate: AbsNumber
  def bitOr(that: AbsNumber): AbsNumber
  def bitAnd(that: AbsNumber): AbsNumber
  def bitXor(that: AbsNumber): AbsNumber
  def bitLShift(shift: AbsNumber): AbsNumber
  def bitRShift(shift: AbsNumber): AbsNumber
  def bitURShift(shift: AbsNumber): AbsNumber
  def add(that: AbsNumber): AbsNumber
  def sub(that: AbsNumber): AbsNumber
  def mul(that: AbsNumber): AbsNumber
  def div(that: AbsNumber): AbsNumber
  def mod(that: AbsNumber): AbsNumber
}

trait AbsNumberUtil extends AbsDomainUtil[Num, AbsNumber] {
  val Inf: AbsNumber
  val PosInf: AbsNumber
  val NegInf: AbsNumber
  val NaN: AbsNumber
  val UInt: AbsNumber
  val NUInt: AbsNumber
  val NatNum: AbsNumber
}
