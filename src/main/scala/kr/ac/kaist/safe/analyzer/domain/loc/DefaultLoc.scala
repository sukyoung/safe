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

import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet
import scala.util.{ Try, Success, Failure }
import spray.json._

// default location abstract domain
case object DefaultLoc extends LocDomain {
  case object Top extends Elem
  case class LocSet(set: Set[Loc]) extends Elem
  object LocSet {
    def apply(seq: Loc*): LocSet = LocSet(seq.toSet)
  }
  lazy val Bot: Elem = LocSet()

  // TODO all location set
  // lazy val locSet: Set[Loc] = cfg.getAllASiteSet.foldLeft(HashSet[Loc]()) {
  //   case (set, asite) => set + Loc(asite, Recent) + Loc(asite, Old)
  // }

  def alpha(loc: Loc): Elem = LocSet(loc)
  override def alpha(locset: Set[Loc]): Elem = LocSet(locset)

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case _ => LocSet(json2set(v, Loc.fromJson))
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Loc] = this match {
      case Top => throw LocTopGammaError // TODO ConFin(locSet)
      case LocSet(set) => ConFin(set)
    }

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

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (Top, _) => false
      case (LocSet(lset), LocSet(rset)) => lset subsetOf rset
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (LocSet(lset), LocSet(rset)) => LocSet(lset ++ rset)
    }

    def ⊓(that: Elem): Elem = (this, that) match {
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

    def filter(f: Loc => Boolean): Elem = this match {
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

    def +(loc: Loc): Elem = this match {
      case Top => Top
      case LocSet(set) => LocSet(set + loc)
    }

    def -(loc: Loc): Elem = this match {
      case Top => Top // TODO LocSet(locSet - loc)
      case LocSet(set) => LocSet(set - loc)
    }

    def subsLoc(locR: Recency, locO: Recency): Elem = this match {
      case Top => Top // TODO LocSet(locSet - locR + locO)
      case LocSet(set) =>
        if (set contains locR) LocSet(set - locR + locO)
        else this
    }

    def weakSubsLoc(locR: Recency, locO: Recency): Elem = this match {
      case Top => Top
      case LocSet(set) =>
        if (set contains locR) LocSet(set + locO)
        else this
    }

    def toJson: JsValue = this match {
      case Top => JsString("⊤")
      case LocSet(set) => JsArray(set.toSeq.map(_.toJson): _*)
    }
  }
}

sealed abstract class AAddrType
case object NormalAAddr extends AAddrType
case object RecencyAAddr extends AAddrType
