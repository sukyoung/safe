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

// undefined abstract domain
trait UndefDomain extends AbsDomain[Undef] {
  // abstract undefined element
  type Elem <: ElemTrait

  // abstract undefined element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def StrictEquals(that: Elem): AbsBool
  }
}
