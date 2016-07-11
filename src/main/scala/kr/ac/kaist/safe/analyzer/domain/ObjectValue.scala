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

import kr.ac.kaist.safe.util.Loc

object ObjectValue {
  def Bot: Utils => ObjectValue = utils =>
    ObjectValue(Value.Bot(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)

  def apply(value: Value): Utils => ObjectValue = utils =>
    ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)

  def apply(pvalue: PValue, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue =
    ObjectValue(Value(pvalue), writable, enumerable, configurable)

  def apply(loc: Loc): Utils => ObjectValue = utils =>
    ObjectValue(Value(loc)(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)

  def apply(locSet: Set[Loc]): Utils => ObjectValue = utils =>
    ObjectValue(Value(locSet)(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
}

case class ObjectValue(
    value: Value,
    writable: AbsBool,
    enumerable: AbsBool,
    configurable: AbsBool
) {
  override def toString: String = {
    if (isBottom) "⊥ObjectValue"
    else {
      val prefix =
        (writable.gammaSimple, enumerable.gammaSimple, configurable.gammaSimple) match {
          case (ConSimpleBot, ConSimpleBot, ConSimpleBot) => "[Val] "
          case _ => s"[${writable.toString.take(1)}${enumerable.toString.take(1)}${configurable.toString.take(1)}] "
        }
      prefix + value.toString
    }
  }

  /* partial order */
  def <=(that: ObjectValue): Boolean = {
    this.value <= that.value &&
      this.writable <= that.writable &&
      this.enumerable <= that.enumerable &&
      this.configurable <= that.configurable
  }

  /* not a partial order */
  def </(that: ObjectValue): Boolean = {
    this.value </ that.value ||
      this.writable </ that.writable ||
      this.enumerable </ that.enumerable ||
      this.configurable </ that.configurable
  }

  /* join */
  def +(that: ObjectValue): ObjectValue = {
    ObjectValue(
      this.value + that.value,
      this.writable + that.writable,
      this.enumerable + that.enumerable,
      this.configurable + that.configurable
    )
  }

  /* meet */
  def <>(that: ObjectValue): ObjectValue = {
    ObjectValue(
      this.value <> that.value,
      this.writable <> that.writable,
      this.enumerable <> that.enumerable,
      this.configurable <> that.configurable
    )
  }

  def isBottom: Boolean = {
    value.isBottom &&
      (writable.gammaSimple, enumerable.gammaSimple, configurable.gammaSimple) ==
      (ConSimpleBot, ConSimpleBot, ConSimpleBot)
  }

  def copyWith(newValue: Value): ObjectValue = ObjectValue(newValue, writable, enumerable, configurable)
}
