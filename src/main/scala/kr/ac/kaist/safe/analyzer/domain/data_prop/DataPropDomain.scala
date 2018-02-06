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

// data property abstract domain
trait DataPropDomain extends AbsDomain[DataProp] {
  def apply(
    value: AbsValue = AbsUndef.Top,
    writable: AbsBool = AbsBool.False,
    enumerable: AbsBool = AbsBool.False,
    configurable: AbsBool = AbsBool.False
  ): Elem

  def apply(desc: AbsDesc): Elem

  // abstract data property element
  type Elem <: ElemTrait

  // abstract data property element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val value: AbsValue
    val writable: AbsBool
    val enumerable: AbsBool
    val configurable: AbsBool

    def copy(
      value: AbsValue = this.value,
      writable: AbsBool = this.writable,
      enumerable: AbsBool = this.enumerable,
      configurable: AbsBool = this.configurable
    ): Elem
  }
}
