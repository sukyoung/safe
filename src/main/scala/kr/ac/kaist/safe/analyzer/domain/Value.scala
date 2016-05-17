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

trait Value {
  val pvalue: PValue
  val locset: Set[Loc]

  /* partial order */
  def <=(that: Value): Boolean
  /* not a partial order */
  def </(that: Value): Boolean
  /* join */
  def +(that: Value): Value
  /* meet */
  def <>(that: Value): Value
  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Value
  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): Value

  def typeCount: Int
  def typeKinds: String

  def isBottom: Boolean
}

case class DefaultValue(pvalue: PValue, locset: Set[Loc]) extends Value {
  /* partial order */
  def <=(that: Value): Boolean = {
    if (this eq that) true
    else {
      this.pvalue <= that.pvalue &&
        this.locset.subsetOf(that.locset)
    }
  }

  /* not a partial order */
  def </(that: Value): Boolean = {
    if (this eq that) false
    else {
      !(this.pvalue <= that.pvalue) ||
        !(this.locset.subsetOf(that.locset))
    }
  }

  /* join */
  def +(that: Value): Value =
    (this, that) match {
      case (a, b) if a eq b => this
      case (a, _) if a.isBottom => that
      case (_, b) if b.isBottom => this
      case (_, _) => DefaultValue(
        this.pvalue + that.pvalue,
        this.locset ++ that.locset
      )
    }

  /* meet */
  def <>(that: Value): Value = {
    if (this eq that) this
    else {
      DefaultValue(
        this.pvalue <> that.pvalue,
        this.locset.intersect(that.locset)
      )
    }
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset(locR)) DefaultValue(this.pvalue, (this.locset - locR) + locO)
    else this
  }

  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset(locR)) DefaultValue(this.pvalue, this.locset + locO)
    else this
  }

  def typeCount: Int = {
    if (this.locset.isEmpty)
      pvalue.typeCount
    else
      pvalue.typeCount + 1
  }

  def typeKinds: String = {
    val sb = new StringBuilder()
    sb.append(pvalue.typeKinds)
    if (!this.locset.isEmpty) sb.append((if (sb.length > 0) ", " else "") + "Object")
    sb.toString
  }

  def isBottom: Boolean =
    this.pvalue.isBottom && this.locset.isEmpty
}
