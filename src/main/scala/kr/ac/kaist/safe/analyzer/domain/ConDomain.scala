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

////////////////////////////////////////////////////////////////////////////////
// concrete single domain
////////////////////////////////////////////////////////////////////////////////
sealed abstract class ConSingle[T] {

  override def toString: String = this match {
    case ConZero() => "[]"
    case ConOne(t) => "[$t]"
    case ConMany() => "[< more than 2 values >]"
  }

  def <=(that: ConSingle[T]): Boolean = (this, that) match {
    case (ConZero(), _) | (_, ConMany()) => true
    case (ConOne(t), ConOne(u)) if t == u => true
    case _ => false
  }

  def +(that: ConSingle[T]): ConSingle[T] = (this, that) match {
    case (ConZero(), _) => that
    case (_, ConZero()) => this
    case (ConOne(t), ConOne(u)) if t == u => this
    case _ => ConMany[T]()
  }

  def ⊓(that: ConSingle[T]): ConSingle[T] = (this, that) match {
    case (ConMany(), _) => that
    case (_, ConMany()) => this
    case (ConOne(t), ConOne(u)) if t == u => this
    case _ => ConZero[T]()
  }
}
case class ConZero[T]() extends ConSingle[T]
case class ConOne[T](value: T) extends ConSingle[T]
case class ConMany[T]() extends ConSingle[T]

////////////////////////////////////////////////////////////////////////////////
// concrete finite set domain
////////////////////////////////////////////////////////////////////////////////
sealed abstract class ConSet[+T] {

  override def toString: String = this match {
    case ConFin(set) => "[" + set.mkString(", ") + "]"
    case ConInf => "[< infinite values >]"
  }

  def isBottom: Boolean = this == ConFin()
}
case object ConInf extends ConSet[Nothing]
case class ConFin[T](values: Set[T]) extends ConSet[T]
object ConFin {
  def apply[T](seq: T*): ConFin[T] = ConFin(seq.toSet)
}
