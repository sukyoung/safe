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

// option abstract domain
case class OptionDomain[V, VD <: AbsDomain[V]](
    AbsV: VD
) extends AbsDomain[Option[V]] {
  type AbsV = AbsV.Elem
  type AbsNone = AbsNone.Elem

  // abstraction function
  def alpha(opt: Option[V]): Elem = opt match {
    case Some(v) => Elem(AbsV(v), AbsNone.Bot)
    case None => Elem(AbsV.Bot, AbsNone.Top)
  }

  lazy val Bot: Elem = Elem(AbsV.Bot, AbsNone.Bot)
  lazy val Top: Elem = Elem(AbsV.Top, AbsNone.Top)

  // pair abstract element
  case class Elem(value: AbsV, none: AbsNone) extends ElemTrait {
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = this.value ⊑ that.value && this.none ⊑ that.none
    def ⊔(that: Elem): Elem = Elem(this.value ⊔ that.value, this.none ⊔ that.none)
    def ⊓(that: Elem): Elem = Elem(this.value ⊓ that.value, this.none ⊓ that.none)

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: ConSet[Option[V]] = (value.gamma, none.gamma) match {
      case (ConFin(v), ConFin(n)) => ConFin(v.map(Some(_)) ++ n)
      case _ => ConInf
    }

    def getSingle: ConSingle[Option[V]] = (value.getSingle, none.getSingle) match {
      case (ConOne(v), ConZero) => ConOne(Some(v))
      case (ConZero, ConOne(n)) => ConOne(n)
      case (ConZero, ConZero) => ConZero
      case _ => ConMany
    }

    override def toString: String = this match {
      case Bot => "⊥"
      case Top => "⊤"
      case _ => s"($value, $none)"
    }
  }
}

// abstract none domain
object AbsNone extends SimpleDomain[None.type]
