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
import kr.ac.kaist.safe.analyzer.TypeConversionHelper

/* 8.10 The Property Descriptor and Property Identifier Specification Types */

////////////////////////////////////////////////////////////////////////////////
// concrete descriptor type
////////////////////////////////////////////////////////////////////////////////
case class Desc(
  value: Option[Value],
  writable: Option[Bool],
  enumerable: Option[Bool],
  configurable: Option[Bool]
)

////////////////////////////////////////////////////////////////////////////////
// descriptor abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsDesc extends AbsDomain[Desc, AbsDesc] {
  val value: (AbsValue, AbsAbsent)
  val writable: (AbsBool, AbsAbsent)
  val enumerable: (AbsBool, AbsAbsent)
  val configurable: (AbsBool, AbsAbsent)

  def copyWith(
    value: (AbsValue, AbsAbsent) = this.value,
    writable: (AbsBool, AbsAbsent) = this.writable,
    enumerable: (AbsBool, AbsAbsent) = this.enumerable,
    configurable: (AbsBool, AbsAbsent) = this.configurable
  ): AbsDesc

  // 8.10.1 IsAccessorDescriptor ( Desc )
  // XXX: we do not support accessor descriptor yet
  // def IsAccessorDescriptor: AbsBool

  // 8.10.2 IsDataDescriptor ( Desc )
  def IsDataDescriptor: AbsBool

  // 8.10.3 IsGenericDescriptor ( Desc )
  def IsGenericDescriptor: AbsBool
}

trait AbsDescUtil extends AbsDomainUtil[Desc, AbsDesc] {
  def apply(
    value: (AbsValue, AbsAbsent),
    writable: (AbsBool, AbsAbsent) = (AbsBool.Bot, AbsAbsent.Top),
    enumerable: (AbsBool, AbsAbsent) = (AbsBool.Bot, AbsAbsent.Top),
    configurable: (AbsBool, AbsAbsent) = (AbsBool.Bot, AbsAbsent.Top)
  ): AbsDesc

  // 8.10.5 ToPropertyDescriptor ( Obj )
  def ToPropertyDescriptor(obj: AbsObject, h: Heap): AbsDesc
}

////////////////////////////////////////////////////////////////////////////////
// default descriptor abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultDesc extends AbsDescUtil {
  lazy val Bot: Dom = Dom(
    (AbsValue.Bot, AbsAbsent.Bot),
    (AbsBool.Bot, AbsAbsent.Bot),
    (AbsBool.Bot, AbsAbsent.Bot),
    (AbsBool.Bot, AbsAbsent.Bot)
  )
  lazy val Top: Dom = Dom(
    (AbsValue.Top, AbsAbsent.Top),
    (AbsBool.Top, AbsAbsent.Top),
    (AbsBool.Top, AbsAbsent.Top),
    (AbsBool.Top, AbsAbsent.Top)
  )

  private def conversion[C, A <: AbsDomain[C, A], U <: AbsDomainUtil[C, A]](
    opt: Option[C],
    util: U
  ): (A, AbsAbsent) = opt match {
    case Some(v) => (util(v), AbsAbsent.Bot)
    case None => (util.Bot, AbsAbsent.Top)
  }
  def alpha(desc: Desc): AbsDesc = Dom(
    conversion[Value, AbsValue, AbsValueUtil](desc.value, AbsValue),
    conversion[Bool, AbsBool, AbsBoolUtil](desc.writable, AbsBool),
    conversion[Bool, AbsBool, AbsBoolUtil](desc.enumerable, AbsBool),
    conversion[Bool, AbsBool, AbsBoolUtil](desc.configurable, AbsBool)
  )

  def apply(
    value: (AbsValue, AbsAbsent),
    writable: (AbsBool, AbsAbsent),
    enumerable: (AbsBool, AbsAbsent),
    configurable: (AbsBool, AbsAbsent)
  ): AbsDesc = Dom(value, writable, enumerable, configurable)

  case class Dom(
      value: (AbsValue, AbsAbsent),
      writable: (AbsBool, AbsAbsent),
      enumerable: (AbsBool, AbsAbsent),
      configurable: (AbsBool, AbsAbsent)
  ) extends AbsDesc {
    def gamma: ConSet[Desc] = ConInf() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[Desc] = ConMany() // TODO more precise

    def <=(that: AbsDesc): Boolean = {
      val (left, right) = (this, check(that))
      val (lv, lva) = left.value
      val (lw, lwa) = left.writable
      val (le, lea) = left.enumerable
      val (lc, lca) = left.configurable
      val (rv, rva) = right.value
      val (rw, rwa) = right.writable
      val (re, rea) = right.enumerable
      val (rc, rca) = right.configurable
      lv <= rv && lva <= rva &&
        lw <= rw && lwa <= rwa &&
        le <= re && lea <= rea &&
        lc <= rc && lca <= rca
    }

    def +(that: AbsDesc): AbsDesc = {
      val (left, right) = (this, check(that))
      val (lv, lva) = left.value
      val (lw, lwa) = left.writable
      val (le, lea) = left.enumerable
      val (lc, lca) = left.configurable
      val (rv, rva) = right.value
      val (rw, rwa) = right.writable
      val (re, rea) = right.enumerable
      val (rc, rca) = right.configurable
      Dom(
        (lv + rv, lva + rva),
        (lw + rw, lwa + rwa),
        (le + re, lea + rea),
        (lc + rc, lca + rca)
      )
    }

    def <>(that: AbsDesc): AbsDesc = {
      val (left, right) = (this, check(that))
      val (lv, lva) = left.value
      val (lw, lwa) = left.writable
      val (le, lea) = left.enumerable
      val (lc, lca) = left.configurable
      val (rv, rva) = right.value
      val (rw, rwa) = right.writable
      val (re, rea) = right.enumerable
      val (rc, rca) = right.configurable
      Dom(
        (lv <> rv, lva <> rva),
        (lw <> rw, lwa <> rwa),
        (le <> re, lea <> rea),
        (lc <> rc, lca <> rca)
      )
    }

    override def toString: String = {
      if (isBottom) "⊥Desc"
      else {
        val wch = writable.toString
        val ech = enumerable.toString
        val cch = configurable.toString
        s"[$wch$ech$cch] $value"
      }
    }

    def copyWith(
      value: (AbsValue, AbsAbsent) = this.value,
      writable: (AbsBool, AbsAbsent) = this.writable,
      enumerable: (AbsBool, AbsAbsent) = this.enumerable,
      configurable: (AbsBool, AbsAbsent) = this.configurable
    ): AbsDesc = Dom(value, writable, enumerable, configurable)

    def IsDataDescriptor: AbsBool = {
      val (v, va) = value
      val (w, wa) = writable
      val trueV =
        if (v.isBottom && w.isBottom) AbsBool.Bot
        else AbsBool.True
      val falseV =
        if (va.isBottom || wa.isBottom) AbsBool.Bot
        else AbsBool.False
      trueV + falseV
    }

    def IsGenericDescriptor: AbsBool =
      IsDataDescriptor.negate
  }

  def ToPropertyDescriptor(obj: AbsObject, h: Heap): AbsDesc = {
    def get(str: String): (AbsValue, AbsAbsent) = {
      val has = obj.HasProperty(AbsString(str), h)
      val v =
        if (AbsBool.True <= has) obj.Get(str, h)
        else AbsValue.Bot

      val va =
        if (AbsBool.False <= has) AbsAbsent.Top
        else AbsAbsent.Bot
      (v, va)
    }
    def getB(str: String): (AbsBool, AbsAbsent) = {
      val (v, va) = get(str)
      (TypeConversionHelper.ToBoolean(v), va)
    }

    val v = get("value")
    val w = getB("writable")
    val e = getB("enumerable")
    val c = getB("configurable")

    AbsDesc(v, w, e, c)
  }
}
