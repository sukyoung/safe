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
import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.util.Loc

object ValueUtil {
  val Bot: Value = Value(AbsPValue.Bot, AbsLoc.Bot)
  // TODO Top

  // constructor
  def apply(pvalue: AbsPValue): Value = Value(pvalue, AbsLoc.Bot)
  def apply(loc: Loc): Value = Value(AbsPValue.Bot, AbsLoc.alpha(loc))
  def apply(locSet: AbsLoc): Value = Value(AbsPValue.Bot, locSet)
  def apply(undefval: AbsUndef): Value = apply(AbsPValue(undefval))
  def apply(nullval: AbsNull): Value = apply(AbsPValue(nullval))
  def apply(boolval: AbsBool): Value = apply(AbsPValue(boolval))
  def apply(numval: AbsNumber): Value = apply(AbsPValue(numval))
  def apply(strval: AbsString): Value = apply(AbsPValue(strval))

  // abstraction
  def alpha(undef: Undef): Value = apply(AbsPValue.alpha(Undef))
  def alpha(x: Null): Value = apply(AbsPValue.alpha(x))
  def alpha(str: String): Value = apply(AbsPValue.alpha(str))
  def alpha(d: Double): Value = apply(AbsPValue.alpha(d))
  def alpha(l: Long): Value = apply(AbsPValue.alpha(l))
  def alpha(b: Boolean): Value = apply(AbsPValue.alpha(b))
}

case class Value(pvalue: AbsPValue, locset: AbsLoc) {
  override def toString: String = {
    val pvalStr =
      if (pvalue.isBottom) ""
      else pvalue.toString

    val locSetStr =
      if (locset.isBottom) ""
      else locset.toString

    (pvalue.isBottom, locset.isBottom) match {
      case (true, true) => "⊥Value"
      case (true, false) => locSetStr
      case (false, true) => pvalStr
      case (false, false) => s"$pvalStr, $locSetStr"
    }
  }

  /* partial order */
  def <=(that: Value): Boolean = {
    if (this eq that) true
    else {
      this.pvalue <= that.pvalue &&
        this.locset <= that.locset
    }
  }

  /* not a partial order */
  def </(that: Value): Boolean = {
    if (this eq that) false
    else {
      !(this.pvalue <= that.pvalue) ||
        !(this.locset <= that.locset)
    }
  }

  /* join */
  def +(that: Value): Value =
    (this, that) match {
      case (a, b) if a eq b => this
      case (a, _) if a.isBottom => that
      case (_, b) if b.isBottom => this
      case (_, _) => Value(
        this.pvalue + that.pvalue,
        this.locset + that.locset
      )
    }

  /* meet */
  def <>(that: Value): Value = {
    if (this eq that) this
    else {
      Value(
        this.pvalue <> that.pvalue,
        this.locset <> that.locset
      )
    }
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset contains locR) Value(this.pvalue, (this.locset - locR) + locO)
    else this
  }

  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset contains locR) Value(this.pvalue, this.locset + locO)
    else this
  }

  def typeCount: Int = {
    if (this.locset.isBottom)
      pvalue.typeCount
    else
      pvalue.typeCount + 1
  }

  def typeKinds: String = {
    val sb = new StringBuilder()
    sb.append(pvalue.typeKinds)
    if (!this.locset.isBottom) sb.append((if (sb.length > 0) ", " else "") + "Object")
    sb.toString
  }

  def isBottom: Boolean =
    this.pvalue.isBottom && this.locset.isBottom

  // TODO working but more simple way is exist with modifying getBase
  def getThis(h: Heap): AbsLoc = {
    val locSet1 = (pvalue.nullval.gamma, pvalue.undefval.gamma) match {
      case (ConSimpleBot(), ConSimpleBot()) => AbsLoc.Bot
      case _ => AbsLoc.alpha(BuiltinGlobal.loc)
    }

    val foundDeclEnvRecord = locset.exists(loc => AbsBool.False <= h.isObject(loc))

    val locSet2 =
      if (foundDeclEnvRecord) AbsLoc.alpha(BuiltinGlobal.loc)
      else AbsLoc.Bot
    val locSet3 = locset.foldLeft(AbsLoc.Bot)((tmpLocSet, loc) => {
      if (AbsBool.True <= h.isObject(loc)) tmpLocSet + loc
      else tmpLocSet
    })

    locSet1 + locSet2 + locSet3
  }
}
