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

import kr.ac.kaist.safe.errors.error.{ NoRecencyTag, NoLoc }
import kr.ac.kaist.safe.util._
import scala.util.{ Try, Success, Failure }
import scala.collection.immutable.HashSet

////////////////////////////////////////////////////////////////////////////////
// concrete location type
////////////////////////////////////////////////////////////////////////////////
case class Loc(address: Address, recency: RecencyTag = Recent) extends Value {
  override def toString: String = s"${recency}${address}"
}

object Loc {
  def parse(str: String): Try[Loc] = {
    val pgmPattern = "(#|##)([0-9]+)".r
    val sysPattern = "(#|##)([0-9a-zA-Z.<>]+)".r
    str match {
      case pgmPattern(prefix, idStr) =>
        RecencyTag.parse(prefix).map(Loc(ProgramAddr(idStr.toInt), _))
      case sysPattern(prefix, name) =>
        RecencyTag.parse(prefix).map(Loc(SystemAddr(name), _))
      case str => Failure(NoLoc(str))
    }
  }
  implicit def ordering[B <: Loc]: Ordering[B] = Ordering.by({
    case Loc(address, _) => address match {
      case ProgramAddr(id) => (id, "")
      case SystemAddr(name) => (0, name)
    }
  })
}

// system location
object SystemLoc {
  def apply(name: String, recency: RecencyTag = Recent): Loc =
    Loc(SystemAddr(name), recency)
}

// recency tag
sealed abstract class RecencyTag(prefix: String) {
  override def toString: String = prefix
}
object RecencyTag {
  def parse(prefix: String): Try[RecencyTag] = prefix match {
    case "#" => Success(Recent)
    case "##" => Success(Old)
    case str => Failure(NoRecencyTag(str))
  }
}

case object Recent extends RecencyTag("#")
case object Old extends RecencyTag("##")

////////////////////////////////////////////////////////////////////////////////
// location abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsLoc extends AbsDomain[Loc, AbsLoc] {
  def contains(loc: Loc): Boolean
  def exists(f: Loc => Boolean): Boolean
  def filter(f: Loc => Boolean): AbsLoc
  def foreach(f: Loc => Unit): Unit
  def foldLeft[T](initial: T)(f: (T, Loc) => T): T
  def map[T](f: Loc => T): Set[T]
  def isConcrete: Boolean
  def +(loc: Loc): AbsLoc
  def -(loc: Loc): AbsLoc
  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): AbsLoc
  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): AbsLoc
}

trait AbsLocUtil extends AbsDomainUtil[Loc, AbsLoc]

////////////////////////////////////////////////////////////////////////////////
// default location abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultLoc extends AbsLocUtil {
  case object Top extends Dom
  case class LocSet(set: Set[Loc]) extends Dom
  object LocSet {
    def apply(seq: Loc*): LocSet = LocSet(seq.toSet)
  }
  lazy val Bot: Dom = LocSet()

  def alpha(loc: Loc): AbsLoc = LocSet(loc)
  override def alpha(locset: Set[Loc]): AbsLoc = LocSet(locset)

  sealed abstract class Dom extends AbsLoc {
    def gamma: ConSet[Loc] = this match {
      case Top => ConInf()
      case LocSet(set) => ConFin(set)
    }

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[Loc] = this match {
      case LocSet(set) if set.size == 0 => ConZero()
      case LocSet(set) if set.size == 1 => ConOne(set.head)
      case _ => ConMany()
    }

    override def toString: String = this match {
      case Top => "Top(location)"
      case LocSet(set) if set.size == 0 => "⊥(location)"
      case LocSet(set) => set.mkString(", ")
    }

    def <=(that: AbsLoc): Boolean = (this, check(that)) match {
      case (_, Top) => true
      case (Top, _) => false
      case (LocSet(lset), LocSet(rset)) => lset subsetOf rset
    }

    def +(that: AbsLoc): AbsLoc = (this, check(that)) match {
      case (Top, _) | (_, Top) => Top
      case (LocSet(lset), LocSet(rset)) => LocSet(lset ++ rset)
    }

    def <>(that: AbsLoc): AbsLoc = (this, check(that)) match {
      case (Top, _) => that
      case (_, Top) => this
      case (LocSet(lset), LocSet(rset)) => LocSet(lset intersect rset)
    }

    def contains(loc: Loc): Boolean = this match {
      case Top => true
      case LocSet(set) => set.contains(loc)
    }

    def exists(f: Loc => Boolean): Boolean = this match {
      case Top => true
      case LocSet(set) => set.exists(f)
    }

    def filter(f: Loc => Boolean): AbsLoc = this match {
      case Top => Top
      case LocSet(set) => LocSet(set.filter(f))
    }

    def foreach(f: Loc => Unit): Unit = this match {
      case Top => // TODO unsound
      case LocSet(set) => set.foreach(f)
    }

    def foldLeft[T](initial: T)(f: (T, Loc) => T): T = this match {
      case Top => initial // TODO unsound
      case LocSet(set) => set.foldLeft(initial)(f)
    }

    def map[T](f: Loc => T): Set[T] = this match {
      case Top => HashSet()
      case LocSet(set) => set.map(f)
    }

    def isConcrete: Boolean = this match {
      case Top => false
      case LocSet(set) => set.size == 1
    }

    def +(loc: Loc): AbsLoc = this match {
      case Top => Top
      case LocSet(set) => LocSet(set + loc)
    }

    def -(loc: Loc): AbsLoc = this match {
      case Top => Top
      case LocSet(set) => LocSet(set - loc)
    }

    def subsLoc(locR: Loc, locO: Loc): AbsLoc = this match {
      case Top => Top
      case LocSet(set) =>
        if (set contains locR) LocSet(set - locR + locO)
        else this
    }

    def weakSubsLoc(locR: Loc, locO: Loc): AbsLoc = this match {
      case Top => Top
      case LocSet(set) => LocSet(set + locO)
    }
  }
}
