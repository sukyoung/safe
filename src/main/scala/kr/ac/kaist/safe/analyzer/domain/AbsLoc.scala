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

import kr.ac.kaist.safe.util.Loc

// location abstract domain
trait AbsLoc extends AbsDomain[Loc, AbsLoc] {
  def gamma: ConSet[Loc]
  def gammaSingle: ConSingle[Loc]
  def contains(loc: Loc): Boolean
  def exists(f: Loc => Boolean): Boolean
  def filter(f: Loc => Boolean): AbsLoc
  def foreach(f: Loc => Unit): Unit
  def foldLeft[T](initial: T)(f: (T, Loc) => T): T
  def map[T](f: Loc => T): Set[T]
  def isConcrete: Boolean
  def +(loc: Loc): AbsLoc
  def -(loc: Loc): AbsLoc
}

trait AbsLocUtil extends AbsDomainUtil[Loc, AbsLoc]

// default location abstract domain
case class DefaultLoc(
    private val totalLocSet: Set[Loc]
) extends AbsLocUtil {
  val Top: AbsDom = AbsDom(totalLocSet)
  val Bot: AbsDom = AbsDom()

  def alpha(loc: Loc): AbsLoc = AbsDom(loc)
  override def alpha(locset: Set[Loc]): AbsLoc = AbsDom(locset)

  val MAX_SIZE: Int = totalLocSet.size
  case class AbsDom(set: Set[Loc]) extends AbsLoc {
    def gamma: ConSet[Loc] = set.size match {
      case 0 => ConSetBot()
      case MAX_SIZE => ConSetTop()
      case _ => ConSetCon(set)
    }

    def gammaSingle: ConSingle[Loc] = set.size match {
      case 0 => ConSingleBot()
      case 1 => ConSingleCon(set.head)
      case _ => ConSingleTop()
    }

    def isBottom: Boolean = set.isEmpty

    override def toString: String = set.size match {
      case MAX_SIZE => "Top"
      case _ => set.mkString(", ")
    }

    def <=(that: AbsLoc): Boolean = set subsetOf check(that).set

    def +(that: AbsLoc): AbsLoc = AbsDom(set ++ check(that).set)

    def <>(that: AbsLoc): AbsLoc = AbsDom(set intersect check(that).set)

    def contains(loc: Loc): Boolean = set.contains(loc)

    def exists(f: Loc => Boolean): Boolean = set.exists(f)

    def filter(f: Loc => Boolean): AbsLoc = AbsDom(set.filter(f))

    def foreach(f: Loc => Unit): Unit = set.foreach(f)

    def foldLeft[T](initial: T)(f: (T, Loc) => T): T = set.foldLeft(initial)(f)

    def map[T](f: Loc => T): Set[T] = set.map(f)

    def isConcrete: Boolean = set.size == 1

    def +(loc: Loc): AbsLoc = AbsDom(set + loc)

    def -(loc: Loc): AbsLoc = AbsDom(set - loc)
  }
  object AbsDom {
    def apply(seq: Loc*): AbsDom = AbsDom(seq.toSet)
  }
}
