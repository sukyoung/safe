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

  // convert function
  def toAbsString(absString: AbsStringUtil): AbsString
  def toAbsBoolean(absBool: AbsBoolUtil): AbsBool
  def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber
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
  def ===(that: AbsUndef)(absBool: AbsBoolUtil): AbsBool
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
  def ===(that: AbsNull)(absBool: AbsBoolUtil): AbsBool
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
  def ===(that: AbsBool)(absBool: AbsBoolUtil): AbsBool
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
  def ===(that: AbsNumber)(absBool: AbsBoolUtil): AbsBool
  def <(that: AbsNumber)(absBool: AbsBoolUtil): AbsBool

  def isNum(v: Double): Boolean
  def isNum: Boolean
  def isInfinity: Boolean
  def isPosInf: Boolean
  def isNegInf: Boolean
  def getUIntSingle: Option[Double]
  def isUIntSingle: Boolean
  def isNUIntSingle: Boolean
  def isUIntAll: Boolean
  def isUInt: Boolean
  def isUIntOrBot: Boolean
  def isNUInt: Boolean
  def isNaN: Boolean

  def negate: AbsNumber
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
  def ===(that: AbsString)(absBool: AbsBoolUtil): AbsBool
  def <(that: AbsString)(absBool: AbsBoolUtil): AbsBool

  def trim: AbsString
  def concat(that: AbsString): AbsString
  def charAt(pos: AbsNumber): AbsString
  def charCodeAt(pos: AbsNumber)(absNumber: AbsNumberUtil): AbsNumber
  def contains(that: AbsString)(absBool: AbsBoolUtil): AbsBool
  def length(absNumber: AbsNumberUtil): AbsNumber
  def toLowerCase: AbsString
  def toUpperCase: AbsString

  def isAllNums: Boolean
  def isAllOthers: Boolean
}
