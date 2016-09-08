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

import kr.ac.kaist.safe.analyzer.domain.Utils._

object DataPropertyUtil {
  val AB = AbsBool.Bot
  val Bot: DataProperty = DataProperty(AbsValue.Bot, AB, AB, AB)
  // TODO Top

  // TODO more consturctor
  // constructor
  def apply(value: AbsValue): DPGen = DPGen(DataProperty(value, _, _, _), AB)
  def apply(pvalue: AbsPValue): DPGen = apply(AbsValue(pvalue))
  def apply(loc: Loc): DPGen = apply(AbsValue(loc))
  def apply(locSet: AbsLoc): DPGen = apply(AbsValue(locSet))
  def apply(undefval: AbsUndef): DPGen = apply(AbsValue(undefval))
  def apply(nullval: AbsNull): DPGen = apply(AbsValue(nullval))
  def apply(boolval: AbsBool): DPGen = apply(AbsValue(boolval))
  def apply(numval: AbsNumber): DPGen = apply(AbsValue(numval))
  def apply(strval: AbsString): DPGen = apply(AbsValue(strval))

  // TODO alpha
}

case class DPGen(
    gen: (AbsBool, AbsBool, AbsBool) => DataProperty,
    default: AbsBool
) {
  def apply(
    writable: AbsBool,
    enumerable: AbsBool,
    configurable: AbsBool
  ): DataProperty = gen(writable, enumerable, configurable)
}
object DPGen {
  // implicit conversion for no attributes setting
  implicit def conversion(dpGen: DPGen): DataProperty =
    dpGen(dpGen.default, dpGen.default, dpGen.default)
}

case class DataProperty(
    value: AbsValue,
    writable: AbsBool,
    enumerable: AbsBool,
    configurable: AbsBool
) {
  override def toString: String = {
    if (isBottom) "⊥DataProperty"
    else {
      val prefix =
        (writable.gammaSimple, enumerable.gammaSimple, configurable.gammaSimple) match {
          case (ConSimpleBot(), ConSimpleBot(), ConSimpleBot()) => "[Val] "
          case _ => s"[${writable.toString.take(1)}${enumerable.toString.take(1)}${configurable.toString.take(1)}] "
        }
      prefix + value.toString
    }
  }

  /* partial order */
  def <=(that: DataProperty): Boolean = {
    this.value <= that.value &&
      this.writable <= that.writable &&
      this.enumerable <= that.enumerable &&
      this.configurable <= that.configurable
  }

  /* not a partial order */
  def </(that: DataProperty): Boolean = {
    this.value </ that.value ||
      this.writable </ that.writable ||
      this.enumerable </ that.enumerable ||
      this.configurable </ that.configurable
  }

  /* join */
  def +(that: DataProperty): DataProperty = {
    DataProperty(
      this.value + that.value,
      this.writable + that.writable,
      this.enumerable + that.enumerable,
      this.configurable + that.configurable
    )
  }

  /* meet */
  def <>(that: DataProperty): DataProperty = {
    DataProperty(
      this.value <> that.value,
      this.writable <> that.writable,
      this.enumerable <> that.enumerable,
      this.configurable <> that.configurable
    )
  }

  def isBottom: Boolean = {
    value.isBottom &&
      (writable.gammaSimple, enumerable.gammaSimple, configurable.gammaSimple) ==
      (ConSimpleBot(), ConSimpleBot(), ConSimpleBot())
  }

  def copyWith(newValue: AbsValue): DataProperty = DataProperty(newValue, writable, enumerable, configurable)
}
