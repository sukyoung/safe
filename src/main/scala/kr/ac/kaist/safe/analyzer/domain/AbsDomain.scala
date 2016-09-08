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

// abstract domain
trait AbsDomain[C, Self <: AbsDomain[C, _]] {
  // concretization
  def gamma: ConSet[C]

  // bottom check
  def isBottom: Boolean

  // get concrete value if |gamma(this)| = 1
  def getSingle: ConSingle[C]

  // folding for bottom
  def foldUnit(f: => Unit): Unit = fold(())(_ => f)
  def foldUnit(f: Self => Unit): Unit = fold(())(f)
  def fold[T](default: T)(f: Self => T): T = isBottom match {
    case true => default
    case false => f(this.asInstanceOf[Self])
  }

  // conversion to string
  override def toString: String

  // partial order
  def <=(that: Self): Boolean

  // not a partial order
  def </(that: Self): Boolean = !(this <= that)

  // join
  def +(that: Self): Self

  // meet
  def <>(that: Self): Self
}

// utility class for given abstract domain
trait AbsDomainUtil[C, ABS <: AbsDomain[C, ABS]] {
  // implementation
  type AbsDom <: ABS

  // lattice top
  val Top: ABS

  // lattice bottom
  /** gamma(Bot) is always empty set in our analyzer **/
  val Bot: ABS

  // abstraction
  def alpha(value: C): ABS
  def alpha(set: Set[C]): ABS = set.foldLeft(Bot) {
    case (abs, value) => abs + alpha(value)
  }
  def alpha(seq: C*): ABS = alpha(seq.toSet)
  def apply(set: Set[C]): ABS = alpha(set)
  def apply(seq: C*): ABS = alpha(seq.toSet)

  // check for input
  def check(that: ABS): AbsDom = that.asInstanceOf[AbsDom]
}
