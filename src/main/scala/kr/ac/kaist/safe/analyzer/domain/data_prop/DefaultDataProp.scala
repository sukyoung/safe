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

import kr.ac.kaist.safe.errors.error.AbsDataPropParseError
import spray.json._

// default data property abstract domain
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
      if (va.isTop) v ⊔ AbsUndef.Top
      else v
    val writable =
      if (wa.isTop) w ⊔ AbsBool.False
      else w
    val enumerable =
      if (ea.isTop) e ⊔ AbsBool.False
      else e
    val configurable =
      if (ca.isTop) c ⊔ AbsBool.False
      else c
    Elem(value, writable, enumerable, configurable)
  }

  def fromJson(v: JsValue): Elem = v match {
    case JsObject(m) => (
      m.get("value").map(AbsValue.fromJson _),
      m.get("writable").map(AbsBool.fromJson _),
      m.get("enumerable").map(AbsBool.fromJson _),
      m.get("configurable").map(AbsBool.fromJson _)
    ) match {
        case (Some(v), Some(w), Some(e), Some(c)) => Elem(v, w, e, c)
        case _ => throw AbsDataPropParseError(v)
      }
    case _ => throw AbsDataPropParseError(v)
  }

  case class Elem(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
  ) extends ElemTrait {
    def gamma: ConSet[DataProp] = ConInf // TODO more precise

    def getSingle: ConSingle[DataProp] = ConMany() // TODO more precise

    def ⊑(that: Elem): Boolean = {
      val (left, right) = (this, that)
      left.value ⊑ right.value &&
        left.writable ⊑ right.writable &&
        left.enumerable ⊑ right.enumerable &&
        left.configurable ⊑ right.configurable
    }

    def ⊔(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.value ⊔ right.value,
        left.writable ⊔ right.writable,
        left.enumerable ⊔ right.enumerable,
        left.configurable ⊔ right.configurable
      )
    }

    def ⊓(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.value ⊓ right.value,
        left.writable ⊓ right.writable,
        left.enumerable ⊓ right.enumerable,
        left.configurable ⊓ right.configurable
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

    def copy(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
    ): Elem = Elem(value, writable, enumerable, configurable)

    def toJson: JsValue = JsObject(
      ("value", value.toJson),
      ("writable", writable.toJson),
      ("enumerable", enumerable.toJson),
      ("configurable", configurable.toJson)
    )
  }
}
