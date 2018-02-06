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

// abstract domain for value V
trait AbsDomain[V] extends Domain {
  // concrete value type
  type ConV = V

  // abstraction functions for values
  def alpha(v: V): Elem
  def alpha(set: Set[V]): Elem = set.foldLeft(Bot) {
    case (elem, v) => elem ⊔ alpha(v)
  }
  def alpha(seq: V*): Elem = alpha(seq.toSet)
  def apply(set: Set[V]): Elem = alpha(set)
  def apply(seq: V*): Elem = alpha(seq.toSet)

  // abstraction functions for operators
  def alpha[U, D <: AbsDomain[U]](
    f: V => U
  )(domain: D): Elem => domain.Elem = elem => elem.gamma match {
    case ConInf => domain.Top
    case ConFin(vset) => domain.alpha(vset.map(f(_)))
  }
  def alpha[U, D <: AbsDomain[U]](
    f: (V, V) => U
  )(domain: D): (Elem, Elem) => domain.Elem = (l, r) => (l.gamma, r.gamma) match {
    case (ConFin(lset), ConFin(rset)) => domain.alpha(lset.foldLeft(HashSet[U]()) {
      case (set, l) => set ++ rset.map(f(l, _))
    })
    case _ => domain.Top
  }

  // abstract element
  type Elem <: ElemTrait

  // abstract element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def gamma: ConSet[V]
    def getSingle: ConSingle[V]
  }
}
