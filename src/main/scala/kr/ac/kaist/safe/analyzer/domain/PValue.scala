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

sealed abstract class PValue {
  val undefval: AbsUndef
  val nullval: AbsNull
  val boolval: AbsBool
  val numval: AbsNumber
  val strval: AbsString

  /* partial order */
  def <=(that: PValue): Boolean
  /* not a partial order */
  def </(that: PValue): Boolean
  /* join */
  def +(that: PValue): PValue
  /* meet */
  def <>(that: PValue): PValue

  def gammaSimple: ConSimple

  override def toString(): String
  def typeCount: Int
  def typeKinds: String
  def foreach(f: (AbsDomain => Unit)): Unit

  def isBottom: Boolean

  def copyWith(newUndefVal: AbsUndef): PValue
  def copyWith(newNullVal: AbsNull): PValue
  def copyWith(newBoolVal: AbsBool): PValue
  def copyWith(newNumberVal: AbsNumber): PValue
  def copyWith(newStringVal: AbsString): PValue
}

case class DefaultPValue(
    undefval: AbsUndef,
    nullval: AbsNull,
    boolval: AbsBool,
    numval: AbsNumber,
    strval: AbsString
) extends PValue {

  /* partial order */
  def <=(that: PValue): Boolean = {
    if (this eq that) true
    else {
      (this.undefval <= that.undefval) &&
        (this.nullval <= that.nullval) &&
        (this.boolval <= that.boolval) &&
        (this.numval <= that.numval) &&
        (this.strval <= that.strval)
    }
  }

  /* not a partial order */
  def </(that: PValue): Boolean = {
    if (this eq that) false
    else {
      !(this.undefval <= that.undefval) ||
        !(this.nullval <= that.nullval) ||
        !(this.boolval <= that.boolval) ||
        !(this.numval <= that.numval) ||
        !(this.strval <= that.strval)
    }
  }

  /* join */
  def +(that: PValue): DefaultPValue = {
    if (this eq that) this
    else {
      DefaultPValue(
        this.undefval + that.undefval,
        this.nullval + that.nullval,
        this.boolval + that.boolval,
        this.numval + that.numval,
        this.strval + that.strval
      )
    }
  }

  /* meet */
  def <>(that: PValue): DefaultPValue = {
    DefaultPValue(
      this.undefval <> that.undefval,
      this.nullval <> that.nullval,
      this.boolval <> that.boolval,
      this.numval <> that.numval,
      this.strval <> that.strval
    )
  }

  def gammaSimple: ConSimple = typeCount match {
    case 0 => ConSimpleBot
    case _ => ConSimpleTop
  }

  override def toString(): String = {
    var lst: List[String] = Nil

    this.undefval.fold(()) { lst ::= _.toString }
    this.nullval.fold(()) { lst ::= _.toString }
    this.boolval.fold(()) { lst ::= _.toString }
    this.numval.fold(()) { lst ::= _.toString }
    this.strval.fold(()) { lst ::= _.toString }

    lst match {
      case Nil => "Bot"
      case _ => lst.mkString(", ")
    }
  }

  def typeCount: Int = {
    var count = 0;
    this.undefval.fold(()) { _ => count += 1 }
    this.nullval.fold(()) { _ => count += 1 }
    this.boolval.fold(()) { _ => count += 1 }
    this.numval.fold(()) { _ => count += 1 }
    this.strval.fold(()) { _ => count += 1 }
    count
  }

  def typeKinds: String = {
    var lst: List[String] = Nil
    this.undefval.fold(()) { _ => lst ::= "Undefined" }
    this.nullval.fold(()) { _ => lst ::= "Null" }
    this.boolval.fold(()) { _ => lst ::= "Boolean" }
    this.numval.fold(()) { _ => lst ::= "Number" }
    this.strval.fold(()) { _ => lst ::= "String" }
    lst.mkString(", ")
  }

  def foreach(f: (AbsDomain => Unit)): Unit = {
    f(undefval); f(nullval); f(boolval); f(numval); f(strval)
  }

  def isBottom: Boolean =
    (undefval.gamma, nullval.gamma, boolval.gammaSimple, numval.gammaSimple, strval.gammaSimple) ==
      (ConSimpleBot, ConSimpleBot, ConSimpleBot, ConSimpleBot, ConSimpleBot)

  def copyWith(newUndefVal: AbsUndef): PValue = DefaultPValue(newUndefVal, nullval, boolval, numval, strval)
  def copyWith(newNullVal: AbsNull): PValue = DefaultPValue(undefval, newNullVal, boolval, numval, strval)
  def copyWith(newBoolVal: AbsBool): PValue = DefaultPValue(undefval, nullval, newBoolVal, numval, strval)
  def copyWith(newNumberVal: AbsNumber): PValue = DefaultPValue(undefval, nullval, boolval, newNumberVal, strval)
  def copyWith(newStringVal: AbsString): PValue = DefaultPValue(undefval, nullval, boolval, numval, newStringVal)
}
