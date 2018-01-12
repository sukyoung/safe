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

import kr.ac.kaist.safe.analyzer.domain.Num._

// concrete primitive value type
abstract class PValue extends Value {
  // 9.1 ToPrimitive
  def ToPrimitive: PValue = this

  // 9.2 ToBoolean
  def ToBoolean: Bool = this match {
    // Undefined: false
    case Undef => F
    // Null: false
    case Null => F
    // Boolean: The result equals the input argument (no conversion).
    case b: Bool => b
    // Number: The result is false if the argument is +0, -0, or NaN;
    //         otherwise the result is true.
    case PosZero | NegZero | NaN => F
    case _: Num => T
    // String: The result is false if the argument is the empty String
    //         (its length is zero); otherwise the result is true.
    case Str(str) => str != ""
  }

  // 9.3 ToNumber
  def ToNumber: Num = this match {
    // Undefined: NaN
    case Undef => NaN
    // Null: +0
    case Null => PosZero
    // Boolean: The result is 1 if the argument is true.
    //          The result is +0 if the argument is false.
    case T => Num(1)
    case F => PosZero
    // Number: The result equals the input argument (no conversion).
    case n: Num => n
    // String: See Str.ToNumber
    case s: Str => s.ToNumber
  }

  // 9.4 ToInteger
  def ToInteger: Num = {
    // 1. Let number be the result of calling ToNumber on the input argument.
    val number = ToNumber
    number match {
      // 2. If number is NaN, return +0.
      case NaN => PosZero
      // 3. If number is +0, -0, +Infinity, or -Infinity, return number.
      case PosZero | NegZero | PosInf | NegInf => number
      // 4. Return the result of computing sign(number) * floor(abs(number)).
      case _ => number.sign * number.abs.floor
    }
  }

  // 9.5 ToInt32: (Signed 32 Bit Integer)
  def ToInt32: Num = {
    // 1. Let number be the result of calling ToNumber on the input argument.
    val number = ToNumber
    number match {
      // 2. If number is NaN, +0, -0, +Infinity, or -Infinity, return +0.
      case NaN | PosZero | NegZero | PosInf | NegInf => PosZero
      case _ =>
        // 3. Let posInt be sign(number) * floor(abs(number)).
        val posInt = number.sign * number.abs.floor
        // 4. Let int32bit be posInt modulo 2^32; that is,
        //    a finite integer value k of Number type with positive sign and less
        //    than 2^32 in magnitude such that the mathematical difference of posInt
        //    and k is mathematically an integer multiple of 2^32.
        val int32bit = posInt modulo (1L << 32)
        // 5. If int32bit is greater than or equal to 2^31, return int32bit - 2^32,
        //    otherwise return int32bit.
        if (int32bit > (1L << 31)) Num(int32bit - (1L << 32))
        else Num(int32bit)
    }
  }

  // 9.6 ToUint32: (Unsigned 32 Bit Integer)
  def ToUint32: Num = {
    // 1. Let number be the result of calling ToNumber on the input argument.
    val number = ToNumber
    number match {
      // 2. If number is NaN, +0, -0, +Infinity, or -Infinity, return +0.
      case NaN | PosZero | NegZero | PosInf | NegInf => PosZero
      case _ =>
        // 3. Let posInt be sign(number) * floor(abs(number)).
        val posInt = number.sign * number.abs.floor
        // 4. Let int32bit be posInt modulo 2^32; that is,
        //    a finite integer value k of Number type with positive sign and less
        //    than 2^32 in magnitude such that the mathematical difference of posInt
        //    and k is mathematically an integer multiple of 2^32.
        val int32bit = posInt modulo (1L << 32)
        // 5. Return int32bit.
        Num(int32bit)
    }
  }

  // 9.7 ToUint16: (Unsigned 16 Bit Integer)
  def ToUint16: Num = {
    // 1. Let number be the result of calling ToNumber on the input argument.
    val number = ToNumber
    number match {
      // 2. If number is NaN, +0, -0, +Infinity, or -Infinity, return +0.
      case NaN | PosZero | NegZero | PosInf | NegInf => PosZero
      case _ =>
        // 3. Let posInt be sign(number) * floor(abs(number)).
        val posInt = number.sign * number.abs.floor
        // 4. Let int16bit be posInt modulo 2^16; that is,
        //    a finite integer value k of Number type with positive sign and less
        //    than 2^16 in magnitude such that the mathematical difference of posInt
        //    and k is mathematically an integer multiple of 2^16.
        val int16bit = posInt modulo (1L << 16)
        // 5. Return int16bit.
        Num(int16bit)
    }
  }

  // 9.8 ToString
  def ToString: Str = this match {
    // Undefined: "undefined"
    case Undef => Str("undefined")
    // Null: "null"
    case Null => Str("null")
    // Boolean: If the argument is true, then the result is "true".
    //          If the argument is false, then the result is "false".
    case T => Str("true")
    case F => Str("false")
    // Number: See Num.ToString
    case n: Num => n.ToString
    // String: Return the input argument (no conversion)
    case s: Str => s
  }

  // 9.12 The SameValue Algorithm
  def SameValue(that: PValue): Bool = Bool(this == that)

  // 11.4.6 Unary + Operator
  def unary_+(): Num = ToNumber

  // 11.4.7 Unary - Operator
  def unary_-(): Num = Num(-ToNumber.num)

  // 11.4.8 Bitwise NOT Operator ( ~ )
  def unary_~(): Num = Num(~ToNumber.ToInt32.num.toInt)

  // 11.4.9 Logical NOT Operator ( ! )
  def unary_!(): Bool = Bool(!ToBoolean.bool)

  // 11.5 Multiplicative Operators
  // 11.5.1 Applying the * Operator
  def *(that: PValue): Num = Num(this.ToNumber.num * that.ToNumber.num)

  // 11.5.1 Applying the / Operator
  def /(that: PValue): Num = Num(this.ToNumber.num / that.ToNumber.num)

  // 11.5.1 Applying the % Operator
  def %(that: PValue): Num = Num(this.ToNumber.num % that.ToNumber.num)

  // 11.6 Additive Operators
  // 11.6.1 The Addition operator ( + )
  def +(that: PValue): PValue = (this, that) match {
    case (_: Str, _) | (_, _: Str) => Str(this.ToString.str + that.ToString.str)
    case _ => Num(this.ToNumber.num + that.ToNumber.num)
  }

  // 11.6.2 The Subtraction Operator ( - )
  def -(that: PValue): Num = Num(this.ToNumber.num - that.ToNumber.num)

  // 11.7 Bitwise Shift Operators
  // 11.7.1 The Left Shift Operator ( << )
  def <<(that: PValue): Num = Num(this.ToInt32.num.toInt << (this.ToUint32.num.toLong & 0x1f))

  // 11.7.2 The Signed Right Shift Operator ( >> )
  def >>(that: PValue): Num = Num(this.ToInt32.num.toInt >> (this.ToUint32.num.toLong & 0x1f))

  // 11.7.3 The Unsigned Right Shift Operator ( >>> )
  def >>>(that: PValue): Num = Num(this.ToUint32.num.toLong >>> (this.ToUint32.num.toLong & 0x1f))

  // 11.8 Relational Operators
  // 11.8.1 The Less-than Operator ( < )
  def <(that: PValue): Bool = (this compare that).getOrElse(F)

  // 11.8.2 The Greater-than Operator ( > )
  def >(that: PValue): Bool = (that compare this).getOrElse(F)

  // 11.8.3 The Less-than-or-equal Operator ( <= )
  def <=(that: PValue): Bool = !((that compare this).getOrElse(T))

  // 11.8.4 The Greater-than-or-equal Operator ( >= )
  def >=(that: PValue): Bool = !((this compare that).getOrElse(T))

  // 11.8.5 The Abstract Relational Comparison Algorithm
  private def compare(that: PValue): Option[Bool] = (this, that) match {
    case (x: Str, y: Str) => Some(Bool(x.str < y.str))
    case (x, y) => (x.ToNumber, y.ToNumber) match {
      case (NaN, _) | (_, NaN) => None
      case (Num(nx), Num(ny)) => Some(Bool(nx < ny))
    }
  }

  // 11.9.1 TheEqualsOperator(==)
  // 11.9.3 The Abstract Equality Comparison Algorithm
  def Equals(that: PValue): Bool = (this, that) match {
    case (Undef, Undef) | (Null, Null) => T
    case (x: Num, y: Num) => (x, y) match {
      case (Num.NaN, _) | (_, Num.NaN) => F
      case _ if x SameValue y => T
      case (Num.PosZero, Num.NegZero) => T
      case (Num.NegZero, Num.PosZero) => T
      case _ => F
    }
    case (x: Str, y: Str) => x SameValue y
    case (x: Bool, y: Bool) => x SameValue y
    case (Null, Undef) => T
    case (Undef, Null) => T
    case (x: Num, y: Str) => x Equals y.ToNumber
    case (x: Str, y: Num) => x.ToNumber Equals y
    case (x: Bool, y) => x.ToNumber Equals y
    case (x, y: Bool) => x Equals y.ToNumber
    case _ => F
  }

  // 11.9.4 The Strict Equals Operator ( StrictEquals )
  // 11.9.6 The Strict Equality Comparison Algorithm
  def StrictEquals(that: PValue): Bool = (this, that) match {
    case (Undef, Undef) | (Null, Null) => T
    case (x: Num, y: Num) => (x, y) match {
      case (Num.NaN, _) | (_, Num.NaN) => F
      case _ if x SameValue y => T
      case (Num.PosZero, Num.NegZero) => T
      case (Num.NegZero, Num.PosZero) => T
      case _ => F
    }
    case (x: Str, y: Str) => x SameValue y
    case (x: Bool, y: Bool) => x SameValue y
    case _ => F
  }

  // 11.10 Binary Bitwise Operators
  private def binBitOp(op: (Int, Int) => Int): (PValue, PValue) => Num =
    (l, r) => Num(op(l.ToInt32.num.toInt, r.ToInt32.num.toInt))
  def &(that: PValue): Num = binBitOp(_ & _)(this, that)
  def ^(that: PValue): Num = binBitOp(_ ^ _)(this, that)
  def |(that: PValue): Num = binBitOp(_ | _)(this, that)

  // 11.11 BinaryLogicalOperators
  def &&(that: PValue): PValue = if (ToBoolean == F) this else that
  def ||(that: PValue): PValue = if (ToBoolean == T) this else that
}
