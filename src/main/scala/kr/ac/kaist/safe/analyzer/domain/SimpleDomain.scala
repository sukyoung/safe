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

import scala.collection.immutable.HashSet

// simple abstract domain
abstract class SimpleDomain[V](total: Option[Set[V]]) { this: AbsDomain[V] =>
  // abstraction function
  def alpha(v: V): Elem = Top

  // other constructors
  def this(set: Set[V]) = this(Some(set))
  def this(seq: V*) = this(seq.size match {
    case 0 => None
    case _ => Some(seq.toSet)
  })

  // abstract element
  type Elem <: ElemTrait with SimpleTrait

  // simple abstract element
  trait SimpleTrait { this: this.Elem =>
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
    def gamma: Option[Set[V]] = this match {
      case Top => total
      case Bot => Some(HashSet())
    }
  }
}
