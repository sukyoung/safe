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

// pair abstract domain
case class PairDomain[L, R, LD <: AbsDomain[L], RD <: AbsDomain[R]](
    AbsL: LD,
    AbsR: RD
) extends AbsDomain[(L, R)] {
  // abstraction function
  def alpha(pair: (L, R)): Elem = {
    val (l, r) = pair
    Pair(AbsL(l), AbsR(r))
  }

  case object Bot extends Elem
  case class Pair(left: AbsL.Elem, right: AbsR.Elem) extends Elem
  lazy val Top: Elem = Pair(AbsL.Top, AbsR.Top)

  // pair abstract element
  sealed trait Elem extends ElemTrait {
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => true
      case (Pair(thisL, thisR), Pair(thatL, thatR)) =>
        thisL ⊑ thatL && thisR ⊑ thatR
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (Pair(thisL, thisR), Pair(thatL, thatR)) =>
        Pair(thisL ⊔ thatL, thisR ⊔ thatR)
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Pair(thisL, thisR), Pair(thatL, thatR)) =>
        (thisL ⊓ thatL, thisR ⊓ thatR) match {
          case (AbsL.Bot, _) | (_, AbsR.Bot) => Bot
          case (l, r) => Pair(l, r)
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: ConSet[(L, R)] = this match {
      case Bot => ConFin()
      case Pair(left, right) => (left.gamma, right.gamma) match {
        case (ConFin(lset), ConFin(rset)) => ConFin((Set[(L, R)]() /: lset)((set, l) => {
          (set /: rset)((set, r) => set + ((l, r)))
        }))
        case _ => ConInf
      }
    }

    def getSingle: ConSingle[(L, R)] = this match {
      case Bot => ConZero
      case Pair(left, right) => (left.getSingle, right.getSingle) match {
        case (ConOne(l), ConOne(r)) => ConOne((l, r))
        case (ConMany, _) | (_, ConMany) => ConMany
        case _ => ConZero
      }
    }

    override def toString: String = this match {
      case Bot => "⊥"
      case Top => "⊤"
      case Pair(left, right) => s"($left, $right)"
    }

    def toJSON(implicit uomap: UIdObjMap): JsValue = fail
  }
}
