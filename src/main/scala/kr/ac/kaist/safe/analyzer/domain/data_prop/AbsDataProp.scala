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
trait DataPropDomain extends AbsDomain[DataProp] { domain: DataPropDomain =>
  def apply(
    value: AbsValue = AbsUndef.Top,
    writable: AbsBool = AbsBool.False,
    enumerable: AbsBool = AbsBool.False,
    configurable: AbsBool = AbsBool.False
  ): Elem

  def apply(desc: AbsDesc): Elem

  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val value: AbsValue
    val writable: AbsBool
    val enumerable: AbsBool
    val configurable: AbsBool

    def copyWith(
      value: AbsValue = this.value,
      writable: AbsBool = this.writable,
      enumerable: AbsBool = this.enumerable,
      configurable: AbsBool = this.configurable
    ): Elem
  }
}

////////////////////////////////////////////////////////////////////////////////
// default data property abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultDataProp extends DataPropDomain {
  lazy val Bot: Elem = Elem(AbsValue.Bot, AbsBool.Bot, AbsBool.Bot, AbsBool.Bot)
  lazy val Top: Elem = Elem(AbsValue.Top, AbsBool.Top, AbsBool.Top, AbsBool.Top)

  def alpha(prop: DataProp): Elem = Elem(
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
  ): Elem = Elem(value, writable, enumerable, configurable)

  def apply(desc: AbsDesc): Elem = {
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
    Elem(value, writable, enumerable, configurable)
  }

  case class Elem(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
  ) extends ElemTrait {
    def gamma: ConSet[DataProp] = ConInf() // TODO more precise

    def getSingle: ConSingle[DataProp] = ConMany() // TODO more precise

    def <=(that: Elem): Boolean = {
      val (left, right) = (this, that)
      left.value <= right.value &&
        left.writable <= right.writable &&
        left.enumerable <= right.enumerable &&
        left.configurable <= right.configurable
    }

    def +(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.value + right.value,
        left.writable + right.writable,
        left.enumerable + right.enumerable,
        left.configurable + right.configurable
      )
    }

    def <>(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
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
    ): Elem = Elem(value, writable, enumerable, configurable)
  }
}
