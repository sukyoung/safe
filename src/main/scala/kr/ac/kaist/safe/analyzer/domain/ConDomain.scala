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

////////////////////////////////////////////////////////////////////////////////
// concrete single domain
////////////////////////////////////////////////////////////////////////////////
sealed abstract class ConSingle[+T] {
  override def toString: String = this match {
    case ConZero => "[]"
    case ConOne(t) => s"[$t]"
    case ConMany => "[< more than 2 values >]"
  }
}
case object ConZero extends ConSingle[Nothing]
case class ConOne[T](value: T) extends ConSingle[T]
case object ConMany extends ConSingle[Nothing]

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
