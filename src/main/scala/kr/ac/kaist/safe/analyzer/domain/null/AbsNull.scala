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
// concrete null type
////////////////////////////////////////////////////////////////////////////////
sealed abstract class Null extends PValue
case object Null extends Null {
  override def toString: String = "null"
}

////////////////////////////////////////////////////////////////////////////////
// null abstract domain
////////////////////////////////////////////////////////////////////////////////
trait NullDomain extends AbsDomain[Null] { domain: NullDomain =>
  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def ===(that: Elem): AbsBool
  }
}
