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
        val int32bit = posInt % (1L << 32)
        // 5. If int32bit is greater than or equal to 2^31, return int32bit - 2^32,
        //    otherwise return int32bit.
        if (int32bit > (1L << 31)) Num(int32bit - (1L << 32))
        else Num(int32bit)
    }
  }

  // 9.6 ToUint32: (Unsigned 32 Bit Integer)
  def ToUInt32: Num = {
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
        val int32bit = posInt % (1L << 32)
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
        val int16bit = posInt % (1L << 16)
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
}
