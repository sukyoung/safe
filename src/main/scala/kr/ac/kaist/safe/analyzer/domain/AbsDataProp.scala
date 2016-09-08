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

////////////////////////////////////////////////////////////////////////////////
// concrete data property type
////////////////////////////////////////////////////////////////////////////////
case class DataProp(
  value: Value,
  writable: Bool,
  enumerable: Bool,
  configurable: Bool
)

////////////////////////////////////////////////////////////////////////////////
// data property abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsDataProp extends AbsDomain[DataProp, AbsDataProp] {
  val value: AbsValue
  val writable: AbsBool
  val enumerable: AbsBool
  val configurable: AbsBool

  def copyWith(
    value: AbsValue = this.value,
    writable: AbsBool = this.writable,
    enumerable: AbsBool = this.enumerable,
    configurable: AbsBool = this.configurable
  ): AbsDataProp
}

trait AbsDataPropUtil extends AbsDomainUtil[DataProp, AbsDataProp] {
  def apply(
    value: AbsValue,
    writable: AbsBool = AbsBool.Top,
    enumerable: AbsBool = AbsBool.Top,
    configurable: AbsBool = AbsBool.Top
  ): AbsDataProp
}

////////////////////////////////////////////////////////////////////////////////
// default data property abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultDataProp extends AbsDataPropUtil {
  lazy val Bot: AbsDom = AbsDom(AbsValue.Bot, AbsBool.Bot, AbsBool.Bot, AbsBool.Bot)
  lazy val Top: AbsDom = AbsDom(AbsValue.Top, AbsBool.Top, AbsBool.Top, AbsBool.Top)

  def alpha(prop: DataProp): AbsDataProp = AbsDom(
    AbsValue(prop.value),
    AbsBool(prop.writable),
    AbsBool(prop.enumerable),
    AbsBool(prop.configurable)
  )

  def apply(
    value: AbsValue = AbsValue.Bot,
    writable: AbsBool = AbsBool.Bot,
    enumerable: AbsBool = AbsBool.Bot,
    configurable: AbsBool = AbsBool.Bot
  ): AbsDataProp = AbsDom(value, writable, enumerable, configurable)

  case class AbsDom(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
  ) extends AbsDataProp {
    def gamma: ConSet[DataProp] = ConSetTop() // TODO more precise

    def <=(that: AbsDataProp): Boolean = {
      val (left, right) = (this, check(that))
      left.value <= right.value &&
        left.writable <= right.writable &&
        left.enumerable <= right.enumerable &&
        left.configurable <= right.configurable
    }

    def +(that: AbsDataProp): AbsDataProp = {
      val (left, right) = (this, check(that))
      AbsDom(
        left.value + right.value,
        left.writable + right.writable,
        left.enumerable + right.enumerable,
        left.configurable + right.configurable
      )
    }

    def <>(that: AbsDataProp): AbsDataProp = {
      val (left, right) = (this, check(that))
      AbsDom(
        left.value <> right.value,
        left.writable <> right.writable,
        left.enumerable <> right.enumerable,
        left.configurable <> right.configurable
      )
    }

    override def toString: String = {
      if (isBottom) "⊥DataProp"
      else {
        val wch = writable.toString.take(1)
        val ech = enumerable.toString.take(1)
        val cch = configurable.toString.take(1)
        s"[$wch$ech$cch] $value"
      }
    }

    def isBottom: Boolean = {
      value.isBottom &&
        writable.isBottom &&
        enumerable.isBottom &&
        configurable.isBottom
    }

    def copyWith(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
    ): AbsDataProp = AbsDom(value, writable, enumerable, configurable)
  }
}
