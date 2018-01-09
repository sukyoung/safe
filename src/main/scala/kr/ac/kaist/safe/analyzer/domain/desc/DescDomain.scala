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

// descriptor abstract domain
trait DescDomain extends AbsDomain[Desc] {
  def apply(
    value: (AbsValue, AbsAbsent),
    writable: (AbsBool, AbsAbsent) = (AbsBool.Bot, AbsAbsent.Top),
    enumerable: (AbsBool, AbsAbsent) = (AbsBool.Bot, AbsAbsent.Top),
    configurable: (AbsBool, AbsAbsent) = (AbsBool.Bot, AbsAbsent.Top)
  ): Elem

  // 8.10.5 ToPropertyDescriptor ( Obj )
  def ToPropertyDescriptor(obj: AbsObj, h: AbsHeap): Elem

  // abstract descriptor element
  type Elem <: ElemTrait

  // abstract descriptor element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val value: (AbsValue, AbsAbsent)
    val writable: (AbsBool, AbsAbsent)
    val enumerable: (AbsBool, AbsAbsent)
    val configurable: (AbsBool, AbsAbsent)

    def copy(
      value: (AbsValue, AbsAbsent) = this.value,
      writable: (AbsBool, AbsAbsent) = this.writable,
      enumerable: (AbsBool, AbsAbsent) = this.enumerable,
      configurable: (AbsBool, AbsAbsent) = this.configurable
    ): Elem

    // 8.10.1 IsAccessorDescriptor ( Desc )
    // XXX: we do not support accessor descriptor yet
    // def IsAccessorDescriptor: AbsBool

    // 8.10.2 IsDataDescriptor ( Desc )
    def IsDataDescriptor: AbsBool

    // 8.10.3 IsGenericDescriptor ( Desc )
    def IsGenericDescriptor: AbsBool
  }
}
