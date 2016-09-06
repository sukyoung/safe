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

// concrete simple domain
sealed abstract class ConSimple[T] extends ConDomain[T]
case class ConSimpleTop[T]() extends ConSimple[T]
case class ConSimpleBot[T]() extends ConSimple[T]

// concrete single domain
sealed abstract class ConSingle[T] extends ConDomain[T]
case class ConSingleTop[T]() extends ConSingle[T]
case class ConSingleBot[T]() extends ConSingle[T]
case class ConSingleCon[T](value: T) extends ConSingle[T]

// concrete set domain
sealed abstract class ConSet[T] extends ConDomain[T]
case class ConSetTop[T]() extends ConSet[T]
case class ConSetBot[T]() extends ConSet[T]
case class ConSetCon[T](values: Set[T]) extends ConSet[T]
object ConSetCon {
  def apply[T](seq: T*): ConSetCon[T] = ConSetCon(seq.toSet)
}
