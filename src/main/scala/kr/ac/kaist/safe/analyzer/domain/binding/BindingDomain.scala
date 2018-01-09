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

import scala.collection.immutable.HashSet

// binding abstract domain
trait BindingDomain extends AbsDomain[Binding] {
  def apply(
    value: AbsValue,
    uninit: AbsAbsent = AbsAbsent.Bot,
    mutable: AbsBool = AbsBool.True
  ): Elem

  // abstract binding element
  type Elem <: ElemTrait

  // abstract binding element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val value: AbsValue
    val uninit: AbsAbsent
    val mutable: AbsBool

    def copy(
      value: AbsValue = this.value,
      uninit: AbsAbsent = this.uninit,
      mutable: AbsBool = this.mutable
    ): Elem
  }
}
