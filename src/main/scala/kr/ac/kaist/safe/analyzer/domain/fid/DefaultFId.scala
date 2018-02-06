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

import kr.ac.kaist.safe.errors.error.{ FIdTopGammaError, AbsFIdParseError }
import kr.ac.kaist.safe.nodes.cfg.FunctionId

import spray.json._

// default function id abstract domain
case object DefaultFId extends FIdDomain {
  case object Top extends Elem
  case class FIdSet(set: Set[FunctionId]) extends Elem
  object FIdSet {
    def apply(seq: FunctionId*): FIdSet = FIdSet(seq.toSet)
  }
  lazy val Bot: Elem = FIdSet()

  def alpha(fid: FId): Elem = FIdSet(fid.id)
  override def alpha(fidset: Set[FId]): Elem = FIdSet(fidset.map(_.id))

  def apply(fid: FunctionId): Elem = FIdSet(fid)
  def apply(fidset: Set[FunctionId]): Elem = FIdSet(fidset)

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case _ => FIdSet(json2set(v, json2int(_)))
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[FId] = this match {
      case Top => throw FIdTopGammaError // TODO FIdSet(fidset.filter(f))
      case FIdSet(set) => ConFin(set.map(FId(_)))
    }

    def getSingle: ConSingle[FId] = this match {
      case FIdSet(set) if set.size == 0 => ConZero()
      case FIdSet(set) if set.size == 1 => ConOne(FId(set.head))
      case _ => ConMany()
    }

    override def toString: String = this match {
      case Top => "Top(fid)"
      case FIdSet(set) if set.size == 0 => "⊥(fid)"
      case FIdSet(set) => set.mkString("fid(", ", ", ")")
    }

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) => true
      case (Top, _) => false
      case (FIdSet(lset), FIdSet(rset)) => lset subsetOf rset
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (FIdSet(lset), FIdSet(rset)) => FIdSet(lset ++ rset)
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, _) => that
      case (_, Top) => this
      case (FIdSet(lset), FIdSet(rset)) => FIdSet(lset intersect rset)
    }

    def contains(fid: FunctionId): Boolean = this match {
      case Top => true
      case FIdSet(set) => set.contains(fid)
    }

    def exists(f: FunctionId => Boolean): Boolean = this match {
      case Top => true
      case FIdSet(set) => set.exists(f)
    }

    def filter(f: FunctionId => Boolean): Elem = this match {
      case Top => throw FIdTopGammaError // TODO FIdSet(fidset.filter(f))
      case FIdSet(set) => FIdSet(set.filter(f))
    }

    def foreach(f: FunctionId => Unit): Unit = this match {
      case Top => throw FIdTopGammaError // TODO fidset.foreach(f)
      case FIdSet(set) => set.foreach(f)
    }

    def foldLeft[T](initial: T)(f: (T, FunctionId) => T): T = this match {
      case Top => throw FIdTopGammaError // TODO fidset.foldLeft(initial)(f)
      case FIdSet(set) => set.foldLeft(initial)(f)
    }

    def map[T](f: FunctionId => T): Set[T] = this match {
      case Top => throw FIdTopGammaError // TODO fidset.map(f)
      case FIdSet(set) => set.map(f)
    }

    def +(fid: FunctionId): Elem = this match {
      case Top => Top
      case FIdSet(set) => FIdSet(set + fid)
    }

    def -(fid: FunctionId): Elem = this match {
      case Top => Top
      case FIdSet(set) => FIdSet(set - fid)
    }

    def toJson: JsValue = this match {
      case Top => JsString("⊤")
      case FIdSet(set) => JsArray(set.toSeq.map(JsNumber(_)): _*)
    }
  }
}
