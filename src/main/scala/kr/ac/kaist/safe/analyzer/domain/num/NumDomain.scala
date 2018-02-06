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

// number abstract domain
trait NumDomain extends AbsDomain[Num] { domain: NumDomain =>
  def Inf: Elem = alpha(Num.PosInf, Num.NegInf)
  def PosInf: Elem = alpha(Num.PosInf)
  def NegInf: Elem = alpha(Num.NegInf)
  def NaN: Elem = alpha(Num.NaN)
  def UInt: Elem
  def NUInt: Elem

  // abstract number element
  type Elem <: ElemTrait

  // abstract number element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // 9.1 ToPrimitive
    def ToPrimitive: AbsPValue = alpha(_.ToPrimitive)(AbsPValue)(this)

    // 9.2 ToBoolean
    def ToBoolean: AbsBool = alpha(_.ToBoolean)(AbsBool)(this)

    // 9.3 ToNumber
    def ToNumber: Elem = alpha(_.ToNumber)(domain)(this)

    // 9.4 ToInteger
    def ToInteger: Elem = alpha(_.ToInteger)(domain)(this)

    // 9.5 ToInt32: (Signed 32 Bit Integer)
    def ToInt32: Elem = alpha(_.ToInt32)(domain)(this)

    // 9.6 ToUint32: (Unsigned 32 Bit Integer)
    def ToUint32: Elem = alpha(_.ToUint32)(domain)(this)

    // 9.7 ToUint16: (Unsigned 16 Bit Integer)
    def ToUint16: Elem = alpha(_.ToUint16)(domain)(this)

    // 9.8 ToString
    def ToString: AbsStr = alpha(_.ToString)(AbsStr)(this)

    // 9.12 The SameValue Algorithm
    def SameValue(that: Elem): AbsBool = alpha(_ SameValue _)(AbsBool)(this, that)

    // 11.4.6 Unary + Operator
    def unary_+(): Elem = alpha(+_)(domain)(this)

    // 11.4.7 Unary - Operator
    def unary_-(): Elem = alpha(-_)(domain)(this)

    // 11.4.8 Bitwise NOT Operator ( ~ )
    def unary_~(): Elem = alpha(~_)(domain)(this)

    // 11.4.9 Logical NOT Operator ( ! )
    def unary_!(): AbsBool = alpha(!_)(AbsBool)(this)

    // 11.5 Multiplicative Operators
    // 11.5.1 Applying the * Operator
    def *(that: Elem): Elem = alpha(_ * _)(domain)(this, that)

    // 11.5.1 Applying the / Operator
    def /(that: Elem): Elem = alpha(_ / _)(domain)(this, that)

    // 11.5.1 Applying the % Operator
    def %(that: Elem): Elem = alpha(_ % _)(domain)(this, that)

    // 11.6 Additive Operators
    // 11.6.1 The Addition operator ( + )
    def +(that: Elem): Elem = alpha(_ + _)(domain)(this, that)

    // 11.6.2 The Subtraction Operator ( - )
    def -(that: Elem): Elem = alpha(_ - _)(domain)(this, that)

    // 11.7 Bitwise Shift Operators
    // 11.7.1 The Left Shift Operator ( << )
    def <<(that: Elem): Elem = alpha(_ << _)(domain)(this, that)

    // 11.7.2 The Signed Right Shift Operator ( >> )
    def >>(that: Elem): Elem = alpha(_ >> _)(domain)(this, that)

    // 11.7.3 The Unsigned Right Shift Operator ( >>> )
    def >>>(that: Elem): Elem = alpha(_ >>> _)(domain)(this, that)

    // 11.8 Relational Operators
    // 11.8.1 The Less-than Operator ( < )
    def <(that: Elem): AbsBool = alpha(_ < _)(AbsBool)(this, that)

    // 11.8.2 The Greater-than Operator ( > )
    def >(that: Elem): AbsBool = alpha(_ > _)(AbsBool)(this, that)

    // 11.8.3 The Less-than-or-equal Operator ( <= )
    def <=(that: Elem): AbsBool = alpha(_ <= _)(AbsBool)(this, that)

    // 11.8.4 The Greater-than-or-equal Operator ( >= )
    def >=(that: Elem): AbsBool = alpha(_ >= _)(AbsBool)(this, that)

    // 11.9.1 TheEqualsOperator(==)
    // 11.9.3 The Abstract Equality Comparison Algorithm
    def Equals(that: Elem): AbsBool = alpha(_ Equals _)(AbsBool)(this, that)

    // 11.9.4 The Strict Equals Operator ( StrictEquals )
    // 11.9.6 The Strict Equality Comparison Algorithm
    def StrictEquals(that: Elem): AbsBool = alpha(_ StrictEquals _)(AbsBool)(this, that)

    // 11.10 BinaryBitwiseOperators
    def &(that: Elem): Elem = alpha(_ & _)(domain)(this, that)
    def ^(that: Elem): Elem = alpha(_ ^ _)(domain)(this, that)
    def |(that: Elem): Elem = alpha(_ | _)(domain)(this, that)

    // 11.11 BinaryLogicalOperators
    def &&(that: Elem): Elem = alpha(_ && _)(domain)(this, that)
    def ||(that: Elem): Elem = alpha(_ || _)(domain)(this, that)

    // Builtin-in functions
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
  }
}
