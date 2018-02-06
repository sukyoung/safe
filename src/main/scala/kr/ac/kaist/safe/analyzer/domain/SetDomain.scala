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

// set abstract domain
abstract class SetDomain[V](total: Option[Set[V]]) { this: AbsDomain[V] =>
  // finite-value element
  trait FinConstructor {
    def apply(set: Set[V]): Elem
    def apply(seq: V*): Elem = apply(seq.toSet)
    def unapply(fin: Elem): Option[Set[V]]
  }
  val Fin: FinConstructor
  lazy val Bot: Elem = Fin()

  // abstraction function
  def alpha(v: V): Elem = Fin(v)

  // other constructors
  def this(set: Set[V]) = this(Some(set))
  def this(seq: V*) = this(seq.size match {
    case 0 => None
    case _ => Some(seq.toSet)
  })

  // abstract element
  type Elem <: ElemTrait with SetTrait

  // set abstract element
  trait SetTrait { this: this.Elem =>
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (Top, _) => false
      case (Fin(l), Fin(r)) => l subsetOf r
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (Fin(l), Fin(r)) => Fin(l ++ r)
    }

    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, _) => that
      case (_, Top) => this
      case (Fin(l), Fin(r)) => Fin(l intersect r)
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: Option[Set[V]] = this match {
      case Top => total
      case Fin(set) => Some(set)
    }
  }
}
