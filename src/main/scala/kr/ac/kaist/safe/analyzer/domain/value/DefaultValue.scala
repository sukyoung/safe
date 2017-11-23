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

import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.errors.error.AbsValueParseError
import kr.ac.kaist.safe.util._
import spray.json._

// default value abstract domain
object DefaultValue extends ValueDomain {
  lazy val Bot: Elem = Elem(AbsPValue.Bot, AbsLoc.Bot)
  lazy val Top: Elem = Elem(AbsPValue.Top, AbsLoc.Top)

  def alpha(value: Value): Elem = value match {
    case (pvalue: PValue) => apply(AbsPValue(pvalue))
    case (loc: Loc) => apply(AbsLoc(loc))
    case StringT => apply(AbsStr.Top)
    case NumberT => apply(AbsNum.Top)
    case BoolT => apply(AbsBool.Top)
  }

  def apply(pvalue: AbsPValue): Elem = Bot.copy(pvalue = pvalue)
  def apply(locset: AbsLoc): Elem = Bot.copy(locset = locset)
  def apply(pvalue: AbsPValue, locset: AbsLoc): Elem = Elem(pvalue, locset)

  def fromJson(v: JsValue): Elem = v match {
    case JsObject(m) => (
      m.get("pvalue").map(AbsPValue.fromJson _),
      m.get("locset").map(AbsLoc.fromJson _)
    ) match {
        case (Some(p), Some(l)) => Elem(p, l)
        case _ => throw AbsValueParseError(v)
      }
    case _ => throw AbsValueParseError(v)
  }

  case class Elem(pvalue: AbsPValue, locset: AbsLoc) extends ElemTrait {
    def gamma: ConSet[Value] = ConInf // TODO more precisely

    def getSingle: ConSingle[Value] = ConMany() // TODO more precisely

    def ⊑(that: Elem): Boolean = {
      val (left, right) = (this, that)
      left.pvalue ⊑ right.pvalue &&
        left.locset ⊑ right.locset
    }

    def ⊔(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.pvalue ⊔ right.pvalue,
        left.locset ⊔ right.locset
      )
    }

    def ⊓(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.pvalue ⊓ right.pvalue,
        left.locset ⊓ right.locset
      )
    }

    override def toString: String = {
      val pvalStr =
        if (pvalue.isBottom) ""
        else pvalue.toString

      val locSetStr =
        if (locset.isBottom) ""
        else locset.toString

      (pvalue.isBottom, locset.isBottom) match {
        case (true, true) => "⊥(value)"
        case (true, false) => locSetStr
        case (false, true) => pvalStr
        case (false, false) => s"$pvalStr, $locSetStr"
      }
    }

    def subsLoc(locR: Recency, locO: Recency): Elem =
      Elem(this.pvalue, this.locset.subsLoc(locR, locO))

    def weakSubsLoc(locR: Recency, locO: Recency): Elem =
      Elem(this.pvalue, this.locset.weakSubsLoc(locR, locO))

    def typeCount: Int = {
      if (this.locset.isBottom)
        pvalue.typeCount
      else
        pvalue.typeCount + 1
    }

    def getThis(h: AbsHeap): AbsLoc = {
      val locSet1 = (pvalue.nullval.isBottom, pvalue.undefval.isBottom) match {
        case (true, true) => AbsLoc.Bot
        case _ => AbsLoc(BuiltinGlobal.loc)
      }

      val foundDeclEnvRecord = locset.exists(loc => !h.isObject(loc))

      val locSet2 =
        if (foundDeclEnvRecord) AbsLoc(BuiltinGlobal.loc)
        else AbsLoc.Bot
      val locSet3 = locset.foldLeft(AbsLoc.Bot)((tmpLocSet, loc) => {
        if (h.isObject(loc)) tmpLocSet + loc
        else tmpLocSet
      })

      locSet1 ⊔ locSet2 ⊔ locSet3
    }

    def toJson: JsValue = JsObject(
      ("pvalue", pvalue.toJson),
      ("locset", locset.toJson)
    )
  }
}
