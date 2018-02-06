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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.errors.error.OldASiteSetParseError
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet
import spray.json._

case class OldASiteSet(mayOld: Set[Loc], mustOld: Set[Loc]) {
  /* partial order */
  def <=(that: OldASiteSet): Boolean = {
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
  def +(that: OldASiteSet): OldASiteSet = {
    if (this eq that) this
    else if (this.isBottom) that
    else if (that.isBottom) this
    else {
      val newMustOld =
        if (this.mustOld == null) that.mustOld
        else if (that.mustOld == null) this.mustOld
        else this.mustOld.intersect(that.mustOld)

      OldASiteSet(this.mayOld ++ that.mayOld, newMustOld)
    }
  }

  /* meet */
  def ⊓(that: OldASiteSet): OldASiteSet = {
    if (this eq that) this
    else {
      val newMustOld =
        if (this.mustOld == null) null
        else if (that.mustOld == null) null
        else this.mustOld ++ that.mustOld

      OldASiteSet(this.mayOld.intersect(that.mayOld), newMustOld)
    }
  }

  /* substitute locR by locO */
  def subsLoc(locR: Recency, locO: Recency): OldASiteSet = {
    OldASiteSet(mayOld + locR.loc, mustOld + locR.loc)
  }

  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Recency, locO: Recency): OldASiteSet = {
    OldASiteSet(mayOld + locR.loc, mustOld)
  }

  override def toString: String = {
    if (this.isBottom) "⊥OldASiteSet"
    else
      "mayOld: (" + mayOld.mkString(", ") + "), " +
        "mustOld: (" + mustOld.mkString(", ") + ")"
  }

  def fixOldify(env: AbsLexEnv, mayOld: Set[Loc], mustOld: Set[Loc]): (OldASiteSet, AbsLexEnv) = {
    if (this.isBottom) (OldASiteSet.Bot, AbsLexEnv.Bot)
    else {
      mayOld.foldLeft((this, env))((res, a) => {
        val (resCtx, resEnv) = res
        val locR = Recency(a, Recent)
        val locO = Recency(a, Old)
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

  def toJson: JsValue = JsObject(
    ("mayOld", JsArray(mayOld.toSeq.map(_.toJson): _*)),
    ("mustOld", JsArray(mustOld.toSeq.map(_.toJson): _*))
  )
}

object OldASiteSet {
  val Bot: OldASiteSet = OldASiteSet(HashSet[Loc](), null)
  val Empty: OldASiteSet = OldASiteSet(HashSet[Loc](), HashSet[Loc]())
  def fromJson(v: JsValue): OldASiteSet = v match {
    case JsObject(m) => (
      m.get("mayOld").map(json2set(_, Loc.fromJson)),
      m.get("mustOld").map(json2set(_, Loc.fromJson))
    ) match {
        case (Some(may), Some(must)) => OldASiteSet(may, must)
        case _ => throw OldASiteSetParseError(v)
      }
    case _ => throw OldASiteSetParseError(v)
  }
}
