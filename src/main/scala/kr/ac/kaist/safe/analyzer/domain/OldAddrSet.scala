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
import kr.ac.kaist.safe.util.Address

case class OldAddrSet(mayOld: Set[Address], mustOld: Set[Address]) {
  /* partial order */
  def <=(that: OldAddrSet): Boolean = {
    if (this eq that) true
    else {
      this.mayOld.subsetOf(that.mayOld) &&
        (if (this.mustOld == null) true
        else if (that.mustOld == null) false
        else that.mustOld.subsetOf(this.mustOld))
    }
  }

  /* bottom checking */
  def isBottom: Boolean = {
    this.mustOld == null && this.mayOld.isEmpty
  }

  /* join */
  def +(that: OldAddrSet): OldAddrSet = {
    if (this eq that) this
    else if (this.isBottom) that
    else if (that.isBottom) this
    else {
      val newMustOld =
        if (this.mustOld == null) that.mustOld
        else if (that.mustOld == null) this.mustOld
        else this.mustOld.intersect(that.mustOld)

      OldAddrSet(this.mayOld ++ that.mayOld, newMustOld)
    }
  }

  /* meet */
  def <>(that: OldAddrSet): OldAddrSet = {
    if (this eq that) this
    else {
      val newMustOld =
        if (this.mustOld == null) null
        else if (that.mustOld == null) null
        else this.mustOld ++ that.mustOld

      OldAddrSet(this.mayOld.intersect(that.mayOld), newMustOld)
    }
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): OldAddrSet = {
    OldAddrSet(mayOld + locR.address, mustOld + locR.address)
  }

  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): OldAddrSet = {
    OldAddrSet(mayOld + locR.address, mustOld)
  }

  override def toString: String = {
    if (this.isBottom) "⊥OldAddrSet"
    else
      "mayOld: (" + mayOld.mkString(", ") + ")" + LINE_SEP +
        "mustOld: (" + mustOld.mkString(", ") + ")"
  }

  def fixOldify(env: DecEnvRecord, mayOld: Set[Address], mustOld: Set[Address]): (OldAddrSet, DecEnvRecord) = {
    if (this.isBottom) (OldAddrSet.Bot, DecEnvRecord.Bot)
    else {
      mayOld.foldLeft((this, env))((res, a) => {
        val (resCtx, resEnv) = res
        val locR = Loc(a, Recent)
        val locO = Loc(a, Old)
        if (mustOld contains a) {
          val newCtx = resCtx.subsLoc(locR, locO)
          val newEnv = resEnv.subsLoc(locR, locO)
          (newCtx, newEnv)
        } else {
          val newCtx = resCtx.weakSubsLoc(locR, locO)
          val newEnv = resEnv.weakSubsLoc(locR, locO)
          (newCtx, newEnv)
        }
      })
    }
  }
}

object OldAddrSet {
  val Bot: OldAddrSet = OldAddrSet(HashSet[Address](), null)
  val Empty: OldAddrSet = OldAddrSet(HashSet[Address](), HashSet[Address]())
}
