/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import spray.json._
import kr.ac.kaist.safe.util.UIdObjMap
import kr.ac.kaist.safe.nodes.cfg.CFG

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

  case class Elem(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
  ) extends ElemTrait {
    def gamma: ConSet[DataProp] = ConInf // TODO more precise

    def getSingle: ConSingle[DataProp] = ConMany // TODO more precise

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

    def toJSON(implicit uomap: UIdObjMap): JsValue = (
      writable.getSingle,
      enumerable.getSingle,
      configurable.getSingle
    ) match {
        case (ConOne(w), ConOne(e), ConOne(c)) => JsObject(
          "value" -> value.toJSON,
          "writable" -> JsBoolean(w),
          "enumerable" -> JsBoolean(e),
          "configurable" -> JsBoolean(c)
        )
        case _ => fail
      }

    def copy(
      value: AbsValue,
      writable: AbsBool,
      enumerable: AbsBool,
      configurable: AbsBool
    ): Elem = Elem(value, writable, enumerable, configurable)
  }

  def fromJSON(json: JsValue, cfg: CFG)(implicit uomap: UIdObjMap): Elem = {
    val fields = json.asJsObject().fields
    Elem(AbsValue.fromJSON(fields("value"), cfg), AbsBool.fromJSON(fields("writable")), AbsBool.fromJSON(fields("enumerable")), AbsBool.fromJSON(fields("configurable")))
  }
}
