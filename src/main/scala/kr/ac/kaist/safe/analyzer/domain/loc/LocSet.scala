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

import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.util._
import scala.util.{ Try, Success, Failure }

// location set
object LocSet extends AbsDomain[Loc] {
  case object Top extends Elem
  case class LSet(set: Set[Loc]) extends Elem
  object LSet {
    def apply(seq: Loc*): LSet = LSet(seq.toSet)
  }
  lazy val Bot: Elem = LSet()

  // TODO all location set
  // lazy val locSet: Set[Loc] = cfg.getAllASiteSet.foldLeft(Set[Loc]()) {
  //   case (set, asite) => set + Loc(asite, Recent) + Loc(asite, Old)
  // }

  def alpha(loc: Loc): Elem = LSet(loc)
  override def alpha(locset: Set[Loc]): Elem = LSet(locset)

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Loc] = this match {
      case Top => throw LocTopGammaError // TODO ConFin(locSet)
      case LSet(set) => ConFin(set)
    }

    def getSingle: ConSingle[Loc] = this match {
      case LSet(set) if set.size == 0 => ConZero
      case LSet(set) if set.size == 1 => ConOne(set.head)
      case _ => ConMany
    }

    override def toString: String = this match {
      case Top => "Top(location)"
      case LSet(set) if set.size == 0 => "⊥(location)"
      case LSet(set) => set.mkString(", ")
    }

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (Top, _) => false
      case (LSet(lset), LSet(rset)) => lset subsetOf rset
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (LSet(lset), LSet(rset)) => LSet(lset ++ rset)
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, _) => that
      case (_, Top) => this
      case (LSet(lset), LSet(rset)) => LSet(lset intersect rset)
    }

    def contains(loc: Loc): Boolean = this match {
      case Top => true
      case LSet(set) => set.contains(loc)
    }

    def exists(f: Loc => Boolean): Boolean = this match {
      case Top => true
      case LSet(set) => set.exists(f)
    }

    def filter(f: Loc => Boolean): Elem = this match {
      case Top => throw LocTopGammaError // TODO LSet(locSet.filter(f))
      case LSet(set) => LSet(set.filter(f))
    }

    def foreach(f: Loc => Unit): Unit = this match {
      case Top => throw LocTopGammaError // TODO locSet.foreach(f)
      case LSet(set) => set.foreach(f)
    }

    def foldLeft[T](initial: T)(f: (T, Loc) => T): T = this match {
      case Top => throw LocTopGammaError // TODO locSet.foldLeft(initial)(f)
      case LSet(set) => set.foldLeft(initial)(f)
    }

    def map[T](f: Loc => T): Set[T] = this match {
      case Top => throw LocTopGammaError // TODO locSet.map(f)
      case LSet(set) => set.map(f)
    }

    def +(loc: Loc): Elem = this match {
      case Top => Top
      case LSet(set) => LSet(set + loc)
    }

    def -(loc: Loc): Elem = this match {
      case Top => Top // TODO LSet(locSet - loc)
      case LSet(set) => LSet(set - loc)
    }

    def subsLoc(from: Loc, to: Loc): Elem = this match {
      case Top => Top // TODO LSet(locSet - from + to)
      case LSet(set) =>
        if (set contains from) LSet(set - from + to)
        else this
    }

    def weakSubsLoc(from: Loc, to: Loc): Elem = this match {
      case Top => Top
      case LSet(set) =>
        if (set contains from) LSet(set + to)
        else this
    }

    def remove(locs: Set[Loc]): Elem = this match {
      case Top => Top
      case LSet(set) => LSet(set -- locs)
    }
  }
}
