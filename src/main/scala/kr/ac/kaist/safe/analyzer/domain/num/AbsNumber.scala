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

////////////////////////////////////////////////////////////////////////////////
// concrete number type
////////////////////////////////////////////////////////////////////////////////
case class Num(num: Double) extends PValue {
  override def toString: String = num.toString
}

////////////////////////////////////////////////////////////////////////////////
// number abstract domain
////////////////////////////////////////////////////////////////////////////////
trait NumDomain extends AbsDomain[Num] { domain: NumDomain =>
  val Inf: Elem
  val PosInf: Elem
  val NegInf: Elem
  val NaN: Elem
  val UInt: Elem
  val NUInt: Elem

  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def ===(that: Elem): AbsBool
    def <(that: Elem): AbsBool

    // Abstraction of step 2 - 4 in section 9.5, ECMAScript 5.1
    def toInteger: Elem
    // Abstraction of step 2 - 5 in section 9.6, ECMAScript 5.1
    def toInt32: Elem
    // Abstraction of step 2 - 5 in section 9.7, ECMAScript 5.1
    def toUInt32: Elem
    // Abstraction of step 2 - 5 in section 9.8, ECMAScript 5.1
    def toUInt16: Elem

    def toAbsBoolean: AbsBool
    def toAbsStr: AbsStr

    // Abstraction of step 4.a - 4.e in section 9.12, ECMAScript 5.1
    // This algorithm differs from the strict equal(===) in its treatment of signed zeros and NaN
    def sameValue(that: Elem): AbsBool

    def negate: Elem
    def abs: Elem
    def acos: Elem
    def asin: Elem
    def atan: Elem
    def atan2(that: Elem): Elem
    def ceil: Elem
    def cos: Elem
    def exp: Elem
    def floor: Elem
    def log: Elem
    def pow(that: Elem): Elem
    def round: Elem
    def sin: Elem
    def sqrt: Elem
    def tan: Elem
    def bitNegate: Elem
    def bitOr(that: Elem): Elem
    def bitAnd(that: Elem): Elem
    def bitXor(that: Elem): Elem
    def bitLShift(shift: Elem): Elem
    def bitRShift(shift: Elem): Elem
    def bitURShift(shift: Elem): Elem
    def add(that: Elem): Elem
    def sub(that: Elem): Elem
    def mul(that: Elem): Elem
    def div(that: Elem): Elem
    def mod(that: Elem): Elem
  }
}
