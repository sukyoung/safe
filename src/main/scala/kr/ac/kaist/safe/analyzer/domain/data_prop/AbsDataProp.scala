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

////////////////////////////////////////////////////////////////////////////////
// concrete data property type
////////////////////////////////////////////////////////////////////////////////
case class DataProp(
    value: Value,
    writable: Bool,
    enumerable: Bool,
    configurable: Bool
) {
  def +(other: DataProp): DataProp = {
    // can be several option
    other
  }
  override def toString: String = {
    var w = "F"
    var e = "F"
    var c = "F"
    if (writable) w = "T"
    if (enumerable) e = "T"
    if (configurable) c = "T"
    s"<$value, $w, $e, $c>"
  }
}

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
    value: AbsValue = AbsUndef.Top,
    writable: AbsBool = AbsBool.False,
    enumerable: AbsBool = AbsBool.False,
    configurable: AbsBool = AbsBool.False
  ): AbsDataProp

  def apply(desc: AbsDesc): AbsDataProp
}

////////////////////////////////////////////////////////////////////////////////
// default data property abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultDataProp extends AbsDataPropUtil {
  lazy val Bot: Dom = Dom(AbsValue.Bot, AbsBool.Bot, AbsBool.Bot, AbsBool.Bot)
  lazy val Top: Dom = Dom(AbsValue.Top, AbsBool.Top, AbsBool.Top, AbsBool.Top)

  def alpha(prop: DataProp): AbsDataProp = Dom(
    AbsValue(prop.value),
    AbsBool(prop.writable),
    AbsBool(prop.enumerable),
    AbsBool(prop.configurable)
  )

  def apply(
    value: AbsValue,
    writable: AbsBool,
    enumerable: AbsBool,
    configurable: AbsBool
  ): AbsDataProp = Dom(value, writable, enumerable, configurable)

  def apply(desc: AbsDesc): AbsDataProp = {
    val (v, va) = desc.value
    val (w, wa) = desc.writable
    val (e, ea) = desc.enumerable
    val (c, ca) = desc.configurable

    val value =
      if (va.isTop) v + AbsUndef.Top
      else v
    val writable =
      if (wa.isTop) w + AbsBool.False
      else w
    val enumerable =
      if (ea.isTop) e + AbsBool.False
      else e
    val configurable =
      if (ca.isTop) c + AbsBool.False
      else c
    Dom(value, writable, enumerable, configurable)
  }

  case class Dom(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
  ) extends AbsDataProp {
    def gamma: ConSet[DataProp] = ConInf() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[DataProp] = ConMany() // TODO more precise

    def <=(that: AbsDataProp): Boolean = {
      val (left, right) = (this, check(that))
      left.value <= right.value &&
        left.writable <= right.writable &&
        left.enumerable <= right.enumerable &&
        left.configurable <= right.configurable
    }

    def +(that: AbsDataProp): AbsDataProp = {
      val (left, right) = (this, check(that))
      Dom(
        left.value + right.value,
        left.writable + right.writable,
        left.enumerable + right.enumerable,
        left.configurable + right.configurable
      )
    }

    def <>(that: AbsDataProp): AbsDataProp = {
      val (left, right) = (this, check(that))
      Dom(
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

    def copyWith(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
    ): AbsDataProp = Dom(value, writable, enumerable, configurable)
  }
}
