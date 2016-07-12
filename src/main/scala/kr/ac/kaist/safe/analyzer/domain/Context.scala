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

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util.{ Address, Loc, Old, Recent }

case class Context(private val env: Set[Loc], private val thisBinding: Set[Loc], mayOld: Set[Address], mustOld: Set[Address]) {
  /* partial order */
  def <=(that: Context): Boolean = {
    if (this eq that) true
    else {
      this.mayOld.subsetOf(that.mayOld) &&
        (if (this.mustOld == null) true
        else if (that.mustOld == null) false
        else that.mustOld.subsetOf(this.mustOld))
    }
  }

  /* not a partial order */
  def </(that: Context): Boolean = !(this <= that)

  /* bottom checking */
  def isBottom: Boolean = {
    this.mustOld == null && this.mayOld.isEmpty
  }

  /* join */
  def +(that: Context): Context = {
    if (this eq that) this
    else if (this.isBottom) that
    else if (that.isBottom) this
    else {
      val newMustOld =
        if (this.mustOld == null) that.mustOld
        else if (that.mustOld == null) this.mustOld
        else this.mustOld.intersect(that.mustOld)

      Context(HashSet[Loc](), HashSet[Loc](), this.mayOld ++ that.mayOld, newMustOld)
    }
  }

  /* meet */
  def <>(that: Context): Context = {
    if (this eq that) this
    else {
      val newMustOld =
        if (this.mustOld == null) null
        else if (that.mustOld == null) null
        else this.mustOld ++ that.mustOld

      Context(HashSet[Loc](), HashSet[Loc](), this.mayOld.intersect(that.mayOld), newMustOld)
    }
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Context = {
    Context(HashSet[Loc](), HashSet[Loc](), mayOld + locR.address, mustOld + locR.address)
  }

  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): Context = {
    Context(HashSet[Loc](), HashSet[Loc](), mayOld + locR.address, mustOld)
  }

  override def toString: String = {
    if (this.isBottom) "⊥Context"
    else
      "mayOld: (" + mayOld.mkString(", ") + ")" + LINE_SEP +
        "mustOld: (" + mustOld.mkString(", ") + ")"
  }

  def fixOldify(obj: Obj, mayOld: Set[Address], mustOld: Set[Address])(utils: Utils): (Context, Obj) = {
    if (this.isBottom) (Context.Bot, Obj.Bot(utils))
    else {
      mayOld.foldLeft((this, obj))((res, a) => {
        val (resCtx, resObj) = res
        val locR = Loc(a, Recent)
        val locO = Loc(a, Old)
        if (mustOld contains a) {
          val newCtx = resCtx.subsLoc(locR, locO)
          val newObj = resObj.subsLoc(locR, locO)
          (newCtx, newObj)
        } else {
          val newCtx = resCtx.weakSubsLoc(locR, locO)
          val newObj = resObj.weakSubsLoc(locR, locO)
          (newCtx, newObj)
        }
      })
    }
  }
}

object Context {
  val Bot: Context = Context(HashSet[Loc](), HashSet[Loc](), HashSet[Address](), null)
  val Empty: Context = Context(HashSet[Loc](), HashSet[Loc](), HashSet[Address](), HashSet[Address]())
}
