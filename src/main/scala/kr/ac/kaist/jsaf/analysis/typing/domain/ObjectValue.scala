/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

object ObjectValue {
  /* convenience constructors */
  def apply(v: AbsNumber, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue
  = ObjectValue(Value(v),writable, enumerable, configurable)
  def apply(v: AbsUndef, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue
  = ObjectValue(Value(v),writable, enumerable, configurable)
  def apply(v: AbsString, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue
  = ObjectValue(Value(v),writable, enumerable, configurable)
  def apply(v: AbsBool, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue
  = ObjectValue(Value(v),writable, enumerable, configurable)
  def apply(v: PValue, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue
  = ObjectValue(Value(v),writable, enumerable, configurable)
  def apply(v: AbsNull, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue
  = ObjectValue(Value(v),writable, enumerable, configurable)
  def apply(v: Loc, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue
  = ObjectValue(Value(v),writable, enumerable, configurable)
}

case class ObjectValue(value: Value,
                       writable: AbsBool,
                       enumerable: AbsBool,
                       configurable: AbsBool) {
  /* tuple-like accessor */
  val _1 = value
  val _2 = writable
  val _3 = enumerable
  val _4 = configurable

  /* partial order */
  def <= (that: ObjectValue): Boolean = {
    this.value <= that.value &&
    this.writable <= that.writable &&
    this.enumerable <= that.enumerable &&
    this.configurable <= that.configurable
  }

  /* not a partial order */
  def </ (that: ObjectValue): Boolean = {
    this.value </ that.value ||
    this.writable </ that.writable ||
    this.enumerable </ that.enumerable ||
    this.configurable </ that.configurable
  }

  /* join */
  def + (that: ObjectValue): ObjectValue = {
    ObjectValue(
      this.value + that.value,
      this.writable + that.writable,
      this.enumerable + that.enumerable,
      this.configurable + that.configurable)
  }

  /* meet */
  def <> (that: ObjectValue): ObjectValue = {
    ObjectValue(
      this.value <> that.value,
      this.writable <> that.writable,
      this.enumerable <> that.enumerable,
      this.configurable <> that.configurable)
  }
}
