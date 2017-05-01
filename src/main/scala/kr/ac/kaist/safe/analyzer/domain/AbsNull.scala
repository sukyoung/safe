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
// concrete null type
////////////////////////////////////////////////////////////////////////////////
sealed abstract class Null extends PValue
case object Null extends Null {
  override def toString: String = "null"
}

////////////////////////////////////////////////////////////////////////////////
// null abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsNull extends AbsDomain[Null, AbsNull] {
  def ===(that: AbsNull): AbsBool
}

trait AbsNullUtil extends AbsDomainUtil[Null, AbsNull]
