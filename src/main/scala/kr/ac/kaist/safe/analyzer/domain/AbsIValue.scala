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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.FunctionId

sealed abstract class IName
case object IPrototype extends IName {
  override def toString: String = s"[[Prototype]]"
}
case object IClass extends IName {
  override def toString: String = s"[[Class]]"
}
case object IExtensible extends IName {
  override def toString: String = s"[[Extensible]]"
}
case object IPrimitiveValue extends IName {
  override def toString: String = s"[[PrimitiveValue]]"
}
case object ICall extends IName {
  override def toString: String = s"[[Call]]"
}
case object IConstruct extends IName {
  override def toString: String = s"[[Construct]]"
}
case object IScope extends IName {
  override def toString: String = s"[[Scope]]"
}
case object IHasInstance extends IName {
  override def toString: String = s"[[HasInstance]]" // TODO
}
case object ITargetFunction extends IName {
  override def toString: String = s"[[TargetFunction]]"
}
case object IBoundThis extends IName {
  override def toString: String = s"[[BoundThis]]"
}
case object IBoundArgs extends IName {
  override def toString: String = s"[[BoundArgs]]"
}

abstract class IValue {
  def +(other: IValue): IValue = {
    other
  }
}

object AbsIValueUtil {
  val Bot: AbsIValue = AbsIValue(AbsValue.Bot, AbsFId.Bot)
  val Top: AbsIValue = AbsIValue(AbsValue.Top, AbsFId.Top)

  // constructor
  def apply(undefval: AbsUndef): AbsIValue = AbsIValue(AbsValue(undefval), AbsFId.Bot)
  def apply(nullval: AbsNull): AbsIValue = AbsIValue(AbsValue(nullval), AbsFId.Bot)
  def apply(boolval: AbsBool): AbsIValue = AbsIValue(AbsValue(boolval), AbsFId.Bot)
  def apply(numval: AbsNumber): AbsIValue = AbsIValue(AbsValue(numval), AbsFId.Bot)
  def apply(strval: AbsString): AbsIValue = AbsIValue(AbsValue(strval), AbsFId.Bot)
  def apply(loc: Loc): AbsIValue = AbsIValue(AbsValue(loc), AbsFId.Bot)
  def apply(locSet: AbsLoc): AbsIValue = AbsIValue(AbsValue(locSet), AbsFId.Bot)
  def apply(fid: FunctionId): AbsIValue = AbsIValue(AbsValue.Bot, AbsFId(fid))
  def apply(fidSet: => Set[FunctionId]): AbsIValue = AbsIValue(AbsValue.Bot, AbsFId(fidSet))
  def apply(fidSet: AbsFId): AbsIValue = AbsIValue(AbsValue.Bot, fidSet)
  def apply(value: AbsValue): AbsIValue = AbsIValue(value, AbsFId.Bot)
}

case class AbsIValue(value: AbsValue, fidset: AbsFId) {
  override def toString: String = {
    val valStr =
      if (value.isBottom) ""
      else value.toString

    val funidSetStr =
      if (fidset.isBottom) ""
      else s"[FunIds] " + fidset.map(id => id.toString).mkString(", ")

    (value.isBottom, fidset.isBottom) match {
      case (true, true) => "⊥AbsIValue"
      case (true, false) => funidSetStr
      case (false, true) => valStr
      case (false, false) => valStr + LINE_SEP + funidSetStr
    }
  }

  /* partial order */
  def <=(that: AbsIValue): Boolean = {
    (this.value <= that.value) &&
      (this.fidset <= that.fidset)
  }

  /* not a partial order */
  def </(that: AbsIValue): Boolean = !(this <= that)

  /* join */
  def +(that: AbsIValue): AbsIValue = {
    AbsIValue(
      this.value + that.value,
      this.fidset + that.fidset
    )
  }

  /* meet */
  def <>(that: AbsIValue): AbsIValue = {
    AbsIValue(
      this.value <> that.value,
      this.fidset <> that.fidset
    )
  }

  def isBottom: Boolean = {
    value.isBottom &&
      this.fidset.isBottom
  }
}
