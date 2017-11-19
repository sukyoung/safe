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

// absent abstract domain
trait AbsentDomain extends AbsDomain[Absent] {
  // abstract absent element
  type Elem <: ElemTrait

  // abstract absent element traits
  trait ElemTrait extends super.ElemTrait { this: Elem => }
}
