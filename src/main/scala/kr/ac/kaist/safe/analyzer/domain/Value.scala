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

import kr.ac.kaist.safe.analyzer.models.PredefLoc

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.util.Loc

case class ValueUtil(utils: Utils) {
  val pvalueU = utils.pvalue
  val Bot: Value = Value(pvalueU.Bot, LocSetEmpty)
  // TODO Top

  // constructor
  def apply(pvalue: PValue): Value = Value(pvalue, LocSetEmpty)
  def apply(loc: Loc): Value = Value(pvalueU.Bot, HashSet(loc))
  def apply(locSet: Set[Loc]): Value = Value(pvalueU.Bot, locSet)
  def apply(undefval: AbsUndef): Value = apply(pvalueU(undefval))
  def apply(nullval: AbsNull): Value = apply(pvalueU(nullval))
  def apply(boolval: AbsBool): Value = apply(pvalueU(boolval))
  def apply(numval: AbsNumber): Value = apply(pvalueU(numval))
  def apply(strval: AbsString): Value = apply(pvalueU(strval))

  // abstraction
  def alpha(): Value = apply(pvalueU.alpha())
  def alpha(x: Null): Value = apply(pvalueU.alpha(x))
  def alpha(str: String): Value = apply(pvalueU.alpha(str))
  def alpha(set: Set[String]): Value = apply(pvalueU.alpha(set))
  def alpha(d: Double): Value = apply(pvalueU.alpha(d))
  def alpha(l: Long): Value = apply(pvalueU.alpha(l))
  // trick for 'have same type after erasure' (Set[Double] & Set[String])
  def alpha(set: => Set[Double]): Value = apply(pvalueU.alpha(set))
  def alpha(b: Boolean): Value = apply(pvalueU.alpha(b))
}

case class Value(pvalue: PValue, locset: Set[Loc]) {
  override def toString: String = {
    val pvalStr =
      if (pvalue.isBottom) ""
      else pvalue.toString

    val locSetStr =
      if (locset.isEmpty) ""
      else locset.mkString(", ")

    (pvalue.isBottom, locset.isEmpty) match {
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
      case (_, _) => Value(
        this.pvalue + that.pvalue,
        this.locset ++ that.locset
      )
    }

  /* meet */
  def <>(that: Value): Value = {
    if (this eq that) this
    else {
      Value(
        this.pvalue <> that.pvalue,
        this.locset.intersect(that.locset)
      )
    }
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset(locR)) Value(this.pvalue, (this.locset - locR) + locO)
    else this
  }

  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset(locR)) Value(this.pvalue, this.locset + locO)
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

  // TODO working but more simple way is exist with modifying getBase
  def getThis(h: Heap)(utils: Utils): Set[Loc] = {
    val locSet1 = (pvalue.nullval.gamma, pvalue.undefval.gamma) match {
      case (ConSimpleBot, ConSimpleBot) => LocSetEmpty
      case _ => HashSet(PredefLoc.GLOBAL)
    }

    val foundDeclEnvRecord = locset.exists(loc => utils.absBool.False <= h.isObject(loc)(utils))

    val locSet2 =
      if (foundDeclEnvRecord) HashSet(PredefLoc.GLOBAL)
      else LocSetEmpty
    val locSet3 = locset.foldLeft(LocSetEmpty)((tmpLocSet, loc) => {
      if (utils.absBool.True <= h.isObject(loc)(utils)) tmpLocSet + loc
      else tmpLocSet
    })

    locSet1 ++ locSet2 ++ locSet3
  }
}
