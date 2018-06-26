/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet

case class AllocLocSet(mayAlloc: LocSet, mustAlloc: LocSet) {
  /* partial order */
  def ⊑(that: AllocLocSet): Boolean = {
    this.mayAlloc ⊑ that.mayAlloc &&
      that.mayAlloc ⊑ this.mayAlloc
  }

  /* bottom checking */
  def isBottom: Boolean = mayAlloc.isBottom && mustAlloc.isTop

  /* join */
  def ⊔(that: AllocLocSet): AllocLocSet = {
    val mayAlloc = this.mayAlloc ⊔ that.mayAlloc
    val mustAlloc = this.mustAlloc ⊓ that.mustAlloc
    AllocLocSet(mayAlloc, mustAlloc)
  }

  /* meet */
  def ⊓(that: AllocLocSet): AllocLocSet = {
    val mayAlloc = this.mayAlloc ⊓ that.mayAlloc
    val mustAlloc = this.mustAlloc ⊔ that.mustAlloc
    AllocLocSet(mayAlloc, mustAlloc)
  }

  /* substitute location */
  def subsLoc(from: Loc, to: Loc): AllocLocSet = {
    AllocLocSet(mayAlloc.subsLoc(from, to), mustAlloc.subsLoc(from, to))
  }

  /* weakly substitute location */
  def weakSubsLoc(from: Loc, to: Loc): AllocLocSet = {
    AllocLocSet(mayAlloc.weakSubsLoc(from, to), mustAlloc.weakSubsLoc(from, to))
  }

  /* allocate location */
  def alloc(loc: Loc): AllocLocSet = {
    AllocLocSet(mayAlloc + loc, mustAlloc + loc)
  }

  /* weakly allocate location */
  def weakAlloc(loc: Loc): AllocLocSet = {
    AllocLocSet(mayAlloc + loc, mustAlloc + loc)
  }

  override def toString: String = s"mayAlloc: ($mayAlloc), mustAlloc: ($mustAlloc)"

  def fix(env: AbsLexEnv, that: AllocLocSet): (AllocLocSet, AbsLexEnv) = {
    if (this.isBottom) (AllocLocSet.Bot, AbsLexEnv.Bot)
    else that.mayAlloc.foldLeft(this, env) {
      case ((resAllocs, resEnv), loc) => {
        val newAllocs =
          if (that.mustAlloc contains loc) resAllocs.alloc(loc)
          else resAllocs.weakAlloc(loc)
        val newEnv = loc match {
          case locR @ Recency(l, Recent) => {
            val locO = Recency(l, Old)
            if (that.mustAlloc contains locR) resEnv.subsLoc(locR, locO)
            else resEnv.weakSubsLoc(locR, locO)
          }
          case _ => resEnv
        }
        (newAllocs, newEnv)
      }
    }
  }
}

object AllocLocSet {
  val Bot: AllocLocSet = AllocLocSet(LocSet.Bot, LocSet.Top)
  val Top: AllocLocSet = AllocLocSet(LocSet.Top, LocSet.Bot)
  val Empty: AllocLocSet = AllocLocSet(LocSet.Bot, LocSet.Bot)
}
