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

import kr.ac.kaist.safe.errors.error.FIdTopGammaError
import kr.ac.kaist.safe.nodes.cfg.FunctionId

////////////////////////////////////////////////////////////////////////////////
// concrete function id type
////////////////////////////////////////////////////////////////////////////////
case class FId(id: Int) extends IValue {
  override def toString: String = s"fun($id)"
}

////////////////////////////////////////////////////////////////////////////////
// function id abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsFId extends AbsDomain[FId, AbsFId] {
  def contains(fid: FunctionId): Boolean
  def exists(f: FunctionId => Boolean): Boolean
  def filter(f: FunctionId => Boolean): AbsFId
  def foreach(f: FunctionId => Unit): Unit
  def foldLeft[T](initial: T)(f: (T, FunctionId) => T): T
  def map[T](f: FunctionId => T): Set[T]
  def +(fid: FunctionId): AbsFId
  def -(fid: FunctionId): AbsFId
}

trait AbsFIdUtil extends AbsDomainUtil[FId, AbsFId] {
  def apply(fid: FunctionId): AbsFId
  def apply(fid: Set[FunctionId]): AbsFId
}

////////////////////////////////////////////////////////////////////////////////
// default function id abstract domain
////////////////////////////////////////////////////////////////////////////////
case object DefaultFId extends AbsFIdUtil {
  case object Top extends Dom
  case class FIdSet(set: Set[FunctionId]) extends Dom
  object FIdSet {
    def apply(seq: FunctionId*): FIdSet = FIdSet(seq.toSet)
  }
  lazy val Bot: Dom = FIdSet()

  def alpha(fid: FId): AbsFId = FIdSet(fid.id)
  override def alpha(fidset: Set[FId]): AbsFId = FIdSet(fidset.map(_.id))

  def apply(fid: FunctionId): AbsFId = FIdSet(fid)
  def apply(fidset: Set[FunctionId]): AbsFId = FIdSet(fidset)

  sealed abstract class Dom extends AbsFId {
    def gamma: ConSet[FId] = this match {
      case Top => throw FIdTopGammaError // TODO FIdSet(fidset.filter(f))
      case FIdSet(set) => ConFin(set.map(FId(_)))
    }

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[FId] = this match {
      case FIdSet(set) if set.size == 0 => ConZero()
      case FIdSet(set) if set.size == 1 => ConOne(FId(set.head))
      case _ => ConMany()
    }

    override def toString: String = this match {
      case Top => "Top(fid)"
      case FIdSet(set) if set.size == 0 => "⊥(fid)"
      case FIdSet(set) => set.mkString(", ")
    }

    def <=(that: AbsFId): Boolean = (this, check(that)) match {
      case (_, Top) => true
      case (Top, _) => false
      case (FIdSet(lset), FIdSet(rset)) => lset subsetOf rset
    }

    def +(that: AbsFId): AbsFId = (this, check(that)) match {
      case (Top, _) | (_, Top) => Top
      case (FIdSet(lset), FIdSet(rset)) => FIdSet(lset ++ rset)
    }

    def <>(that: AbsFId): AbsFId = (this, check(that)) match {
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

    def filter(f: FunctionId => Boolean): AbsFId = this match {
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

    def +(fid: FunctionId): AbsFId = this match {
      case Top => Top
      case FIdSet(set) => FIdSet(set + fid)
    }

    def -(fid: FunctionId): AbsFId = this match {
      case Top => Top
      case FIdSet(set) => FIdSet(set - fid)
    }
  }
}
