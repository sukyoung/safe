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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.FunctionId

sealed abstract class InternalName
case object IPrototype extends InternalName
case object IClass extends InternalName
case object IExtensible extends InternalName
case object IPrimitiveValue extends InternalName
case object ICall extends InternalName
case object IConstruct extends InternalName
case object IScope extends InternalName

case class InternalValue(value: Value, fidset: Set[FunctionId]) {
  override def toString: String = {
    val valStr =
      if (value.isBottom) ""
      else value.toString

    val funidSetStr =
      if (fidset.isEmpty) ""
      else s"[FunIds] " + fidset.map(id => id.toString).mkString(", ")

    (value.isBottom, fidset.isEmpty) match {
      case (true, true) => "⊥InternalValue"
      case (true, false) => funidSetStr
      case (false, true) => valStr
      case (false, false) => valStr + LINE_SEP + funidSetStr
    }
  }

  /* partial order */
  def <=(that: InternalValue): Boolean = {
    (this.value <= that.value) &&
      (this.fidset subsetOf that.fidset)
  }

  /* not a partial order */
  def </(that: InternalValue): Boolean = !(this <= that)

  /* join */
  def +(that: InternalValue): InternalValue = {
    InternalValue(
      this.value + that.value,
      this.fidset ++ that.fidset
    )
  }

  /* meet */
  def <>(that: InternalValue): InternalValue = {
    InternalValue(
      this.value <> that.value,
      this.fidset intersect that.fidset
    )
  }

  def isBottom: Boolean = {
    value.isBottom &&
      this.fidset.isEmpty
  }
}