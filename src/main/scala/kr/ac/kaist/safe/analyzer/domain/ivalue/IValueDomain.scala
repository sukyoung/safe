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

// internal value abstract domain
trait IValueDomain extends AbsDomain[IValue] {
  def apply(value: AbsValue): Elem
  def apply(fidset: AbsFId): Elem
  def apply(value: AbsValue, fidset: AbsFId): Elem

  // abstract internal value element
  type Elem <: ElemTrait

  // abstract internal value element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val value: AbsValue
    val fidset: AbsFId

    def copy(
      value: AbsValue = this.value,
      fidset: AbsFId = this.fidset
    ): Elem
  }
}
