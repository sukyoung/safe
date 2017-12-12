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

import kr.ac.kaist.safe.analyzer.domain.Utils.AAddrType
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.errors.error.{ NoLoc, LocTopGammaError, UserAllocSiteError }
import kr.ac.kaist.safe.util._
import scala.util.{ Try, Success, Failure }
import scala.collection.immutable.HashSet

////////////////////////////////////////////////////////////////////////////////
// concrete location type
////////////////////////////////////////////////////////////////////////////////
abstract class Loc extends Value {
  def isUser: Boolean = this match {
    case Recency(loc, _) => loc.isUser
    case UserAllocSite(_) => true
    case PredAllocSite(_) => false
  }

  override def toString: String = this match {
    case Recency(loc, _) => loc.toString
    case u @ UserAllocSite(_) => throw UserAllocSiteError(u)
    case p @ PredAllocSite(_) => p.toString
  }
}

object Loc {
  // predefined special concrete location
  lazy val predConSet: Set[Loc] = HashSet(
    PredAllocSite.GLOBAL_ENV,
    PredAllocSite.PURE_LOCAL
  )

  def parse(str: String): Try[Loc] = {
    val recency = "(R|O)(.+)".r
    val userASite = "#([0-9]+)".r
    val predASite = "#([0-9a-zA-Z-.<>]+)".r
    str match {
      // allocation site
      case userASite(id) => Try(UserAllocSite(id.toInt))
      case predASite(name) => Success(PredAllocSite(name))
      // recency abstraction
      case recency("R", str) => parse(str).map(Recency(_, Recent))
      case recency("O", str) => parse(str).map(Recency(_, Old))
      // otherwise
      case str => Failure(NoLoc(str))
    }
  }

  def apply(str: String): Loc = apply(PredAllocSite(str))
  def apply(asite: AllocSite): Loc = AAddrType match {
    case NormalAAddr => asite
    case RecencyAAddr => Recency(asite, Recent)
  }

  implicit def ordering[B <: Loc]: Ordering[B] = Ordering.by({
    case addrPart => addrPart.toString
  })
}

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
  def +(loc: Loc): AbsLoc
  def -(loc: Loc): AbsLoc
  /* substitute locR by locO */
  def subsLoc(locR: Recency, locO: Recency): AbsLoc
  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Recency, locO: Recency): AbsLoc
}

trait AbsLocUtil extends AbsDomainUtil[Loc, AbsLoc]

////////////////////////////////////////////////////////////////////////////////
// default location abstract domain
////////////////////////////////////////////////////////////////////////////////
case object DefaultLoc extends AbsLocUtil {
  case object Top extends Dom
  case class LocSet(set: Set[Loc]) extends Dom
  object LocSet {
    def apply(seq: Loc*): LocSet = LocSet(seq.toSet)
  }
  lazy val Bot: Dom = LocSet()

  // TODO all location set
  // lazy val locSet: Set[Loc] = cfg.getAllASiteSet.foldLeft(HashSet[Loc]()) {
  //   case (set, asite) => set + Loc(asite, Recent) + Loc(asite, Old)
  // }

  def alpha(loc: Loc): AbsLoc = LocSet(loc)
  override def alpha(locset: Set[Loc]): AbsLoc = LocSet(locset)

  sealed abstract class Dom extends AbsLoc {
    def gamma: ConSet[Loc] = this match {
      case Top => throw LocTopGammaError // TODO ConFin(locSet)
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
      case Top => throw LocTopGammaError // TODO LocSet(locSet.filter(f))
      case LocSet(set) => LocSet(set.filter(f))
    }

    def foreach(f: Loc => Unit): Unit = this match {
      case Top => throw LocTopGammaError // TODO locSet.foreach(f)
      case LocSet(set) => set.foreach(f)
    }

    def foldLeft[T](initial: T)(f: (T, Loc) => T): T = this match {
      case Top => throw LocTopGammaError // TODO locSet.foldLeft(initial)(f)
      case LocSet(set) => set.foldLeft(initial)(f)
    }

    def map[T](f: Loc => T): Set[T] = this match {
      case Top => throw LocTopGammaError // TODO locSet.map(f)
      case LocSet(set) => set.map(f)
    }

    def +(loc: Loc): AbsLoc = this match {
      case Top => Top
      case LocSet(set) => LocSet(set + loc)
    }

    def -(loc: Loc): AbsLoc = this match {
      case Top => Top // TODO LocSet(locSet - loc)
      case LocSet(set) => LocSet(set - loc)
    }

    def subsLoc(locR: Recency, locO: Recency): AbsLoc = this match {
      case Top => Top // TODO LocSet(locSet - locR + locO)
      case LocSet(set) =>
        if (set contains locR) LocSet(set - locR + locO)
        else this
    }

    def weakSubsLoc(locR: Recency, locO: Recency): AbsLoc = this match {
      case Top => Top
      case LocSet(set) =>
        if (set contains locR) LocSet(set + locO)
        else this
    }
  }
}

sealed abstract class AAddrType
case object NormalAAddr extends AAddrType
case object RecencyAAddr extends AAddrType
