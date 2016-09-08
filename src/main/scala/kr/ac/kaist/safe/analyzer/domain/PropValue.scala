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
import scala.collection.immutable.HashSet

object PropValue {
  def Bot: PropValue = PropValue(DataPropertyUtil.Bot, HashSet[FunctionId]())

  def apply(objval: DataProperty): PropValue = PropValue(objval, HashSet[FunctionId]())
  def apply(pvalue: AbsPValue, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): PropValue =
    PropValue(DataProperty(AbsValue(pvalue), writable, enumerable, configurable))

  def apply(fidSet: Set[FunctionId]): PropValue =
    PropValue(DataPropertyUtil.Bot, fidSet)

  def apply(undefval: AbsUndef): PropValue =
    PropValue(DataPropertyUtil(undefval), HashSet[FunctionId]())

  def apply(nullval: AbsNull): PropValue =
    PropValue(DataPropertyUtil(nullval), HashSet[FunctionId]())

  def apply(boolval: AbsBool): PropValue =
    PropValue(DataPropertyUtil(boolval), HashSet[FunctionId]())

  def apply(numval: AbsNumber): PropValue =
    PropValue(DataPropertyUtil(numval), HashSet[FunctionId]())

  def apply(strval: AbsString): PropValue =
    PropValue(DataPropertyUtil(strval), HashSet[FunctionId]())
}

case class PropValue(
    objval: DataProperty,
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
