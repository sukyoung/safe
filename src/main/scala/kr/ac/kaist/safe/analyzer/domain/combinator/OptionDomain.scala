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

import kr.ac.kaist.safe.util.UIdObjMap
import spray.json._

// option abstract domain
case class OptionDomain[V, VD <: AbsDomain[V]](
    AbsV: VD
) extends AbsDomain[Option[V]] {
  type AbsV = AbsV.Elem
  type AbsAbsent = AbsAbsent.Elem

  // abstraction function
  def alpha(opt: Option[V]): Elem = opt match {
    case Some(v) => Elem(AbsV(v), AbsAbsent.Bot)
    case None => Elem(AbsV.Bot, AbsAbsent.Top)
  }

  lazy val Bot: Elem = Elem(AbsV.Bot, AbsAbsent.Bot)
  lazy val Top: Elem = Elem(AbsV.Top, AbsAbsent.Top)

  def apply(value: AbsV, absent: AbsAbsent): Elem = Elem(value, absent)
  def unapply(elem: Elem): Option[(AbsV, AbsAbsent)] = Some((elem.value, elem.absent))

  // pair abstract element
  case class Elem(value: AbsV, absent: AbsAbsent) extends ElemTrait {
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = this.value ⊑ that.value && this.absent ⊑ that.absent
    def ⊔(that: Elem): Elem = Elem(this.value ⊔ that.value, this.absent ⊔ that.absent)
    def ⊓(that: Elem): Elem = Elem(this.value ⊓ that.value, this.absent ⊓ that.absent)

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: ConSet[Option[V]] = (value.gamma, absent.gamma) match {
      case (ConFin(v), ConFin(n)) => ConFin(v.map(Some(_)) ++ n)
      case _ => ConInf
    }

    def getSingle: ConSingle[Option[V]] = (value.getSingle, absent.getSingle) match {
      case (ConOne(v), ConZero) => ConOne(Some(v))
      case (ConZero, ConOne(n)) => ConOne(n)
      case (ConZero, ConZero) => ConZero
      case _ => ConMany
    }

    override def toString: String = this match {
      case Bot => "⊥"
      case Top => "⊤"
      case _ => s"($value, $absent)"
    }

    // existence check
    def exists(f: AbsV => Boolean): AbsBool = {
      val trueB =
        if (!f(value)) AT
        else AbsBool.Bot
      val falseB =
        if (!absent.isBottom) AF
        else AbsBool.Bot
      trueB ⊔ falseB
    }

    def isAbsent: Boolean = value.isBottom && absent.isTop

    def toJSON(implicit uomap: UIdObjMap): JsValue =
      if (absent.isTop) fail else value.toJSON
  }
}
