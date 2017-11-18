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
// concrete undefined type
////////////////////////////////////////////////////////////////////////////////
sealed abstract class Undef extends PValue
case object Undef extends Undef {
  override def toString: String = "undefined"
}

////////////////////////////////////////////////////////////////////////////////
// undefined abstract domain
////////////////////////////////////////////////////////////////////////////////
trait UndefDomain extends AbsDomain[Undef] { domain: UndefDomain =>
  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def ===(that: Elem): AbsBool
  }
}
