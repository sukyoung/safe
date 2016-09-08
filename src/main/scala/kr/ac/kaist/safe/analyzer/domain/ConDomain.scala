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

sealed abstract class ConDomain[T]

// concrete single domain
sealed abstract class ConSingle[T] extends ConDomain[T]
case class ConZero[T]() extends ConSingle[T]
case class ConOne[T](value: T) extends ConSingle[T]
case class ConMany[T]() extends ConSingle[T]

// concrete finite set domain
sealed abstract class ConSet[T] extends ConDomain[T]
case class ConInf[T]() extends ConSet[T]
case class ConFin[T](values: Set[T]) extends ConSet[T]
object ConFin {
  def apply[T](seq: T*): ConFin[T] = ConFin(seq.toSet)
}
