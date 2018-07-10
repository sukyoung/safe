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

// single abstract domain
case class SingleDomain[V](value: V) extends AbsDomain[V] {
  object Top extends Elem
  object Bot extends Elem

  // abstraction function
  def alpha(v: V): Elem = Top

  // single abstract element
  trait Elem extends ElemTrait {
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Top, Bot) => false
      case _ => true
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: ConSet[V] = this match {
      case Top => ConFin(value)
      case Bot => ConFin()
    }

    def getSingle: ConSingle[V] = this match {
      case Top => ConOne(value)
      case Bot => ConZero
    }
  }
}
