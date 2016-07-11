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

import scala.collection.immutable.HashSet

object PropValue {
  def Bot: Utils => PropValue = utils =>
    PropValue(ObjectValue.Bot(utils), HashSet[FunctionId]())

  def apply(objval: ObjectValue): PropValue = PropValue(objval, HashSet[FunctionId]())
  def apply(pvalue: PValue, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): PropValue =
    PropValue(ObjectValue(Value(pvalue), writable, enumerable, configurable))

  def apply(fidSet: Set[FunctionId]): Utils => PropValue = utils =>
    PropValue(ObjectValue.Bot(utils), fidSet)

  def apply(undefval: AbsUndef): Utils => PropValue = utils =>
    PropValue(ObjectValue(PValue(undefval)(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot), HashSet[FunctionId]())

  def apply(nullval: AbsNull): Utils => PropValue = utils =>
    PropValue(ObjectValue(PValue(nullval)(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot), HashSet[FunctionId]())

  def apply(boolval: AbsBool): Utils => PropValue = utils =>
    PropValue(ObjectValue(PValue(boolval)(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot), HashSet[FunctionId]())

  def apply(numval: AbsNumber): Utils => PropValue = utils =>
    PropValue(ObjectValue(PValue(numval)(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot), HashSet[FunctionId]())

  def apply(strval: AbsString): Utils => PropValue = utils =>
    PropValue(ObjectValue(PValue(strval)(utils), utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot), HashSet[FunctionId]())
}

case class PropValue(
    objval: ObjectValue,
    funid: Set[FunctionId]
) {
  override def toString: String = {
    val objValStr =
      if (objval.isBottom) ""
      else objval.toString

    val funidSetStr =
      if (funid.isEmpty) ""
      else s"[FunIds] " + funid.map(id => id.toString).mkString(", ")

    (objval.isBottom, funid.isEmpty) match {
      case (true, true) => "⊥PropValue"
      case (true, false) => funidSetStr
      case (false, true) => objValStr
      case (false, false) => objValStr + LINE_SEP + funidSetStr
    }
  }

  /* partial order */
  def <=(that: PropValue): Boolean = {
    if (this eq that) true
    else {
      this.objval <= that.objval &&
        this.funid.subsetOf(that.funid)
    }
  }

  /* not a partial order */
  def </(that: PropValue): Boolean = {
    if (this eq that) false
    else {
      this.objval </ that.objval ||
        !this.funid.subsetOf(that.funid)
    }
  }

  /* join */
  def +(that: PropValue): PropValue = {
    if (this eq that) this
    else {
      PropValue(
        this.objval + that.objval,
        this.funid ++ that.funid
      )
    }
  }

  /* meet */
  def <>(that: PropValue): PropValue = {
    PropValue(
      this.objval <> that.objval,
      this.funid.intersect(that.funid)
    )
  }

  def isBottom: Boolean =
    this.objval.isBottom && this.funid.isEmpty
}
