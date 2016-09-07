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
import kr.ac.kaist.safe.util.Loc

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
  val Bot: InternalValue = InternalValue(ValueUtil.Bot, FidSetEmpty)

  // constructor
  def apply(undefval: AbsUndef): InternalValue = InternalValue(ValueUtil(undefval), FidSetEmpty)
  def apply(nullval: AbsNull): InternalValue = InternalValue(ValueUtil(nullval), FidSetEmpty)
  def apply(boolval: AbsBool): InternalValue = InternalValue(ValueUtil(boolval), FidSetEmpty)
  def apply(numval: AbsNumber): InternalValue = InternalValue(ValueUtil(numval), FidSetEmpty)
  def apply(strval: AbsString): InternalValue = InternalValue(ValueUtil(strval), FidSetEmpty)
  def apply(loc: Loc): InternalValue = InternalValue(ValueUtil(loc), FidSetEmpty)
  def apply(locSet: AbsLoc): InternalValue = InternalValue(ValueUtil(locSet), FidSetEmpty)
  def apply(fid: FunctionId): InternalValue = InternalValue(ValueUtil.Bot, FidSetEmpty + fid)
  def apply(fidSet: => Set[FunctionId]): InternalValue = InternalValue(ValueUtil.Bot, fidSet)
  def apply(value: Value): InternalValue = InternalValue(value, FidSetEmpty)
}

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
