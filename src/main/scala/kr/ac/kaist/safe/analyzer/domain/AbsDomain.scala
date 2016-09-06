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

trait AbsDomain {
  // default gamma function
  def gamma: ConDomain
  override def toString: String

  // Abstraction of Table 11 in section 9.2, ECMAScript 5.1
  def toAbsBoolean: AbsBool
  // Abstraction of Table 12 in section 9.3, ECMAScript 5.1
  def toAbsNumber: AbsNumber
  // Abstraction of Table 13 in section 9.8, ECMAScript 5.1
  def toAbsString: AbsString
}

trait AbsUndef extends AbsDomain {
  def gamma: ConSimple
  def foldUnit(f: => Unit): Unit = fold(())(_ => f)
  def foldUnit(f: AbsUndef => Unit): Unit = fold(())(f)
  def fold[T](default: T)(f: AbsUndef => T): T = gamma match {
    case ConSimpleBot => default
    case _ => f(this)
  }
  def <=(that: AbsUndef): Boolean
  def </(that: AbsUndef): Boolean = !(this <= that)
  def +(that: AbsUndef): AbsUndef
  def <>(that: AbsUndef): AbsUndef
  def ===(that: AbsUndef): AbsBool
}

trait AbsNull extends AbsDomain {
  def gamma: ConSimple
  def foldUnit(f: => Unit): Unit = fold(())(_ => f)
  def foldUnit(f: AbsNull => Unit): Unit = fold(())(f)
  def fold[T](default: T)(f: AbsNull => T): T = gamma match {
    case ConSimpleBot => default
    case _ => f(this)
  }
  def <=(that: AbsNull): Boolean
  def </(that: AbsNull): Boolean = !(this <= that)
  def +(that: AbsNull): AbsNull
  def <>(that: AbsNull): AbsNull
  def ===(that: AbsNull): AbsBool
}

trait AbsBool extends AbsDomain {
  def gamma: ConSingle[Boolean]
  def gammaSimple: ConSimple
  def foldUnit(f: => Unit): Unit = fold(())(_ => f)
  def foldUnit(f: AbsBool => Unit): Unit = fold(())(f)
  def fold[T](default: T)(f: AbsBool => T): T = gammaSimple match {
    case ConSimpleBot => default
    case _ => f(this)
  }
  def <=(that: AbsBool): Boolean
  def </(that: AbsBool): Boolean
  def +(that: AbsBool): AbsBool
  def <>(that: AbsBool): AbsBool
  def ===(that: AbsBool): AbsBool
  def negate: AbsBool
}

trait AbsNumber extends AbsDomain {
  def gamma: ConSet[Double]
  def gammaSingle: ConSingle[Double]
  def gammaSimple: ConSimple
  def foldUnit(f: => Unit): Unit = fold(())(_ => f)
  def foldUnit(f: AbsNumber => Unit): Unit = fold(())(f)
  def fold[T](default: T)(f: AbsNumber => T): T = gammaSimple match {
    case ConSimpleBot => default
    case _ => f(this)
  }
  def <=(that: AbsNumber): Boolean
  def </(that: AbsNumber): Boolean = !(this <= that)
  def +(that: AbsNumber): AbsNumber
  def <>(that: AbsNumber): AbsNumber
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

trait AbsString extends AbsDomain {
  def gamma: ConSet[String]
  def gammaSingle: ConSingle[String]
  def gammaSimple: ConSimple
  def gammaIsAllNums: ConSingle[Boolean]
  def foldUnit(f: => Unit): Unit = fold(())(_ => f)
  def foldUnit(f: AbsString => Unit): Unit = fold(())(f)
  def fold[T](default: T)(f: AbsString => T): T = gammaSimple match {
    case ConSimpleBot => default
    case _ => f(this)
  }
  def <=(that: AbsString): Boolean
  def </(that: AbsString): Boolean = !(this <= that)
  def +(that: AbsString): AbsString
  def <>(that: AbsString): AbsString
  def ===(that: AbsString): AbsBool
  def <(that: AbsString): AbsBool

  def trim: AbsString
  def concat(that: AbsString): AbsString
  def charAt(pos: AbsNumber): AbsString
  def charCodeAt(pos: AbsNumber): AbsNumber
  def contains(that: AbsString): AbsBool
  def length: AbsNumber
  def toLowerCase: AbsString
  def toUpperCase: AbsString

  def isAllNums: Boolean
  def isAllOthers: Boolean
  def isArrayIndex: AbsBool
}
