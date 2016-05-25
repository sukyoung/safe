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

trait PValue {
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

  override def toString(): String
  def typeCount: Int
  def typeKinds: String
  def foreach(f: (AbsDomain => Unit)): Unit

  def isTop: Boolean
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

  override def toString(): String = {
    if (this.isTop) {
      "PValue"
    } else {
      var first = true
      val sb = new StringBuilder()

      if (!this.undefval.isBottom) {
        sb.append(this.undefval.toString)
        first = false
      }
      if (!this.nullval.isBottom) {
        if (!first) sb.append(", ");
        sb.append(this.nullval.toString);
        first = false;
      }
      if (!this.boolval.isBottom) {
        if (!first) sb.append(", ");
        sb.append(this.boolval.toString);
        first = false;
      }
      if (!this.numval.isBottom) {
        if (!first) sb.append(", ");
        sb.append(this.numval.toString);
        first = false;
      }
      if (!this.strval.isBottom) {
        if (!first) sb.append(", ");
        sb.append(this.strval.toString);
        first = false;
      }

      if (first) "Bot" else sb.toString()
    }
  }

  def typeCount: Int = {
    var count = 0;
    if (!this.undefval.isBottom)
      count = count + 1;
    if (!this.nullval.isBottom)
      count = count + 1;
    if (!this.boolval.isBottom)
      count = count + 1;
    if (!this.numval.isBottom)
      count = count + 1;
    if (!this.strval.isBottom)
      count = count + 1;
    count
  }

  def typeKinds: String = {
    val sb = new StringBuilder()
    if (!undefval.isBottom) sb.append("Undefined")
    if (!nullval.isBottom) sb.append((if (sb.length > 0) ", " else "") + "Null")
    if (!boolval.isBottom) sb.append((if (sb.length > 0) ", " else "") + "Boolean")
    if (!numval.isBottom) sb.append((if (sb.length > 0) ", " else "") + "Number")
    if (!strval.isBottom) sb.append((if (sb.length > 0) ", " else "") + "String")
    sb.toString
  }

  def foreach(f: (AbsDomain => Unit)): Unit = {
    f(undefval); f(nullval); f(boolval); f(numval); f(strval)
  }

  def isTop: Boolean =
    undefval.isTop && nullval.isTop && boolval.isTop && numval.isTop && strval.isTop
  def isBottom: Boolean =
    undefval.isBottom && nullval.isBottom && boolval.isBottom && numval.isBottom && strval.isBottom

  def copyWith(newUndefVal: AbsUndef): PValue = DefaultPValue(newUndefVal, nullval, boolval, numval, strval)
  def copyWith(newNullVal: AbsNull): PValue = DefaultPValue(undefval, newNullVal, boolval, numval, strval)
  def copyWith(newBoolVal: AbsBool): PValue = DefaultPValue(undefval, nullval, newBoolVal, numval, strval)
  def copyWith(newNumberVal: AbsNumber): PValue = DefaultPValue(undefval, nullval, boolval, newNumberVal, strval)
  def copyWith(newStringVal: AbsString): PValue = DefaultPValue(undefval, nullval, boolval, numval, newStringVal)
}
