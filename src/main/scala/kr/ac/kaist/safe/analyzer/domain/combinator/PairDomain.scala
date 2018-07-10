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

// pair abstract domain
case class PairDomain[L, R, LD <: AbsDomain[L], RD <: AbsDomain[R]](
    ldom: LD,
    rdom: RD
) extends AbsDomain[(L, R)] {
  // abstraction function
  def alpha(pair: (L, R)): Elem = {
    val (l, r) = pair
    Elem(ldom(l), rdom(r))
  }

  lazy val Bot: Elem = Elem(ldom.Bot, rdom.Bot)
  lazy val Top: Elem = Elem(ldom.Top, rdom.Top)

  // pair abstract element
  case class Elem(left: ldom.Elem, right: rdom.Elem) extends ElemTrait {
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = {
      this.left ⊑ that.left &&
        this.right ⊑ that.right
    }

    // join operator
    def ⊔(that: Elem): Elem = Elem(
      this.left ⊔ that.left,
      this.right ⊔ that.right
    )

    // meet operator
    def ⊓(that: Elem): Elem = Elem(
      this.left ⊓ that.left,
      this.right ⊓ that.right
    )

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: ConSet[(L, R)] = (left.gamma, right.gamma) match {
      case (ConFin(lset), ConFin(rset)) => ConFin((Set[(L, R)]() /: lset)((set, l) => {
        (set /: rset)((set, r) => set + ((l, r)))
      }))
      case _ => ConInf
    }

    def getSingle: ConSingle[(L, R)] = (left.getSingle, right.getSingle) match {
      case (ConOne(l), ConOne(r)) => ConOne((l, r))
      case (ConMany, _) | (_, ConMany) => ConMany
      case _ => ConZero
    }

    override def toString: String = this match {
      case Top => "⊤"
      case Bot => "⊥"
      case _ => s"($left, $right)"
    }
  }
}
