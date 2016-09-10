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

// domain
trait Domain[Self <: Domain[_]] {
  // bottom check
  def isBottom: Boolean

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

// utility class for given domain
trait DomainUtil[DOM <: Domain[DOM]] {
  // implementation
  type Dom <: DOM

  // lattice top
  val Top: DOM

  // lattice bottom
  val Bot: DOM

  // check for input
  def check(that: DOM): Dom = that.asInstanceOf[Dom]
}
