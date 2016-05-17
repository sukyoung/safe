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

import kr.ac.kaist.safe.cfg_builder.FunctionId

trait PropValue {
  val objval: ObjectValue
  val funid: Set[FunctionId]

  /* partial order */
  def <=(that: PropValue): Boolean
  /* not a partial order */
  def </(that: PropValue): Boolean
  /* join */
  def +(that: PropValue): PropValue
  /* meet */
  def <>(that: PropValue): PropValue

  def isBottom: Boolean
}

case class DefaultPropValue(
    objval: ObjectValue,
    funid: Set[FunctionId]
) extends PropValue {
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
      DefaultPropValue(
        this.objval + that.objval,
        this.funid ++ that.funid
      )
    }
  }

  /* meet */
  def <>(that: PropValue): PropValue = {
    DefaultPropValue(
      this.objval <> that.objval,
      this.funid.intersect(that.funid)
    )
  }

  def isBottom: Boolean =
    this.objval.isBottom && this.funid.isEmpty
}