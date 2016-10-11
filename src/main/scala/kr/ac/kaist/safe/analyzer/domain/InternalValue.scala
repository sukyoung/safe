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
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.FunctionId

sealed abstract class InternalName
case object IPrototype extends InternalName {
  override def toString: String = s"[[Prototype]]"
}
case object IClass extends InternalName {
  override def toString: String = s"[[Class]]"
}
case object IExtensible extends InternalName {
  override def toString: String = s"[[Extensible]]"
}
case object IPrimitiveValue extends InternalName {
  override def toString: String = s"[[PrimitiveValue]]"
}
case object ICall extends InternalName {
  override def toString: String = s"[[Call]]"
}
case object IConstruct extends InternalName {
  override def toString: String = s"[[Construct]]"
}
case object IScope extends InternalName {
  override def toString: String = s"[[Scope]]"
}
case object IHasInstance extends InternalName {
  override def toString: String = s"@hasinstance" // TODO
}

object InternalValueUtil {
  val Bot: InternalValue = InternalValue(AbsValue.Bot, FidSetEmpty)
  val Top: InternalValue = InternalValue(AbsValue.Top, FidSetEmpty) // TODO unsound

  // constructor
  def apply(undefval: AbsUndef): InternalValue = InternalValue(AbsValue(undefval), FidSetEmpty)
  def apply(nullval: AbsNull): InternalValue = InternalValue(AbsValue(nullval), FidSetEmpty)
  def apply(boolval: AbsBool): InternalValue = InternalValue(AbsValue(boolval), FidSetEmpty)
  def apply(numval: AbsNumber): InternalValue = InternalValue(AbsValue(numval), FidSetEmpty)
  def apply(strval: AbsString): InternalValue = InternalValue(AbsValue(strval), FidSetEmpty)
  def apply(loc: Loc): InternalValue = InternalValue(AbsValue(loc), FidSetEmpty)
  def apply(locSet: AbsLoc): InternalValue = InternalValue(AbsValue(locSet), FidSetEmpty)
  def apply(fid: FunctionId): InternalValue = InternalValue(AbsValue.Bot, FidSetEmpty + fid)
  def apply(fidSet: => Set[FunctionId]): InternalValue = InternalValue(AbsValue.Bot, fidSet)
  def apply(value: AbsValue): InternalValue = InternalValue(value, FidSetEmpty)
}

case class InternalValue(value: AbsValue, fidset: Set[FunctionId]) {
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
