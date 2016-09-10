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

////////////////////////////////////////////////////////////////////////////////
// concrete boolean type
////////////////////////////////////////////////////////////////////////////////
case class Bool(b: Boolean) extends PValue

////////////////////////////////////////////////////////////////////////////////
// boolean abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsBool extends AbsDomain[Bool, AbsBool] {
  def ===(that: AbsBool): AbsBool
  def negate: AbsBool

  def toAbsNumber: AbsNumber
  def toAbsString: AbsString

  def map[T <: Domain[T]](
    thenV: => T,
    elseV: => T
  )(implicit util: DomainUtil[T]): T
}

trait AbsBoolUtil extends AbsDomainUtil[Bool, AbsBool] {
  // abstraction from true
  val True: AbsBool

  // abstraction from false
  val False: AbsBool
}
