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

// boolean abstract domain
trait AbsBool extends AbsDomain[Boolean, AbsBool] {
  def gamma: ConSingle[Boolean]
  def gammaSimple: ConSimple[Boolean]
  def ===(that: AbsBool): AbsBool
  def negate: AbsBool

  def toAbsNumber: AbsNumber
  def toAbsString: AbsString
}

trait AbsBoolUtil extends AbsDomainUtil[Boolean, AbsBool] {
  // abstraction from true
  val True: AbsBool

  // abstraction from false
  val False: AbsBool
}
