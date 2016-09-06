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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.util.Loc

object BindingUtil {
  val Bot: Binding = Binding(ValueUtil.Bot, AbsBool.Bot, AbsBool.Bot)
  // TODO Top

  // constructor
  def apply(
    value: Value = ValueUtil.Bot,
    initialized: AbsBool = AbsBool.True,
    mutable: AbsBool = AbsBool.True
  ): Binding = Binding(value, initialized, mutable)
}

case class Binding(
    value: Value,
    initialized: AbsBool,
    mutable: AbsBool
) {
  /* partial order */
  def <=(that: Binding): Boolean = {
    this.value <= that.value &&
      this.initialized <= that.initialized &&
      this.mutable <= that.mutable
  }

  /* not a partial order */
  def </(that: Binding): Boolean = !(this <= that)

  /* join */
  def +(that: Binding): Binding = Binding(
    this.value + that.value,
    this.initialized + that.initialized,
    this.mutable + that.mutable
  )

  /* meet */
  def <>(that: Binding): Binding = Binding(
    this.value <> that.value,
    this.initialized <> that.initialized,
    this.mutable <> that.mutable
  )

  // bottom check
  def isBottom: Boolean = {
    value.isBottom &&
      initialized.isBottom &&
      mutable.isBottom
  }

  override def toString: String = s"[${mutable.toString.take(1)}] $value"
}
