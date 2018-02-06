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

// flat abstract domain for
abstract class FlatDomain[V](total: Option[Set[V]]) { this: AbsDomain[V] =>
  // uni-value element
  trait UniConstructor {
    def apply(v: V): Elem
    def unapply(uni: Elem): Option[V]
  }
  val Uni: UniConstructor

  // abstraction function
  def alpha(v: V): Elem = Uni(v)

  // other constructors
  def this(set: Set[V]) = this(Some(set))
  def this(seq: V*) = this(seq.size match {
    case 0 => None
    case _ => Some(seq.toSet)
  })

  // abstract element
  type Elem <: ElemTrait with FlatTrait

  // flat abstract element
  trait FlatTrait { this: this.Elem =>
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) | (_, Top) => true
      case (Top, _) | (_, Bot) => false
      case (Uni(l), Uni(r)) => l == r
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (Uni(l), Uni(r)) if l == r => this
      case _ => Top
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, _) => that
      case (_, Top) => this
      case (Uni(l), Uni(r)) if l == r => this
      case _ => Bot
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: Option[Set[V]] = this match {
      case Top => total
      case Uni(v) => Some(HashSet(v))
      case Bot => Some(HashSet())
    }
  }
}
