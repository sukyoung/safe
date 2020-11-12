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

import kr.ac.kaist.safe.analyzer.model.GLOBAL_LOC
import kr.ac.kaist.safe.util._

import spray.json._
import kr.ac.kaist.safe.nodes.cfg.CFG

// default value abstract domain
object DefaultValue extends ValueDomain {
  lazy val Bot: Elem = Elem(AbsPValue.Bot, LocSet.Bot)
  lazy val Top: Elem = Elem(AbsPValue.Top, LocSet.Top)

  def alpha(value: Value): Elem = value match {
    case (pvalue: PValue) => apply(AbsPValue(pvalue))
    case (loc: Loc) => apply(LocSet(loc))
    case StringT => apply(AbsStr.Top)
    case NumberT => apply(AbsNum.Top)
    case BoolT => apply(AbsBool.Top)
  }

  def apply(pvalue: AbsPValue): Elem = Bot.copy(pvalue = pvalue)
  def apply(locset: LocSet): Elem = Bot.copy(locset = locset)
  def apply(pvalue: AbsPValue, locset: LocSet): Elem = Elem(pvalue, locset)

  case class Elem(pvalue: AbsPValue, locset: LocSet) extends ElemTrait {
    def gamma: ConSet[Value] = (pvalue.gamma, locset.gamma) match {
      case (ConFin(pset), ConFin(lset)) =>
        val psetv: Set[Value] = pset.map((v) => {
          val conv: Value = v
          v
        })
        val lsetv: Set[Value] = lset.map((v) => {
          val conv: Value = v
          v
        })
        ConFin(psetv | lsetv)
      case _ => ConInf
    }

    def getSingle: ConSingle[Value] = (pvalue.getSingle, locset.getSingle) match {
      case (ConZero, ConZero) => ConZero
      case (ConOne(v), ConZero) => ConOne(v)
      case (ConZero, ConOne(v)) => ConOne(v)
      case _ => ConMany
    }

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

    def toJSON(implicit uomap: UIdObjMap): JsValue = resolve {
      getSingle match {
        case ConOne(v) => v.toJSON
        case _ => fail
      }
    }

    def subsLoc(from: Loc, to: Loc): Elem =
      Elem(this.pvalue, this.locset.subsLoc(from, to))

    def weakSubsLoc(from: Loc, to: Loc): Elem =
      Elem(this.pvalue, this.locset.weakSubsLoc(from, to))

    def remove(locs: Set[Loc]): Elem =
      Elem(this.pvalue, this.locset.remove(locs))

    def typeCount: Int = {
      if (this.locset.isBottom)
        pvalue.typeCount
      else
        pvalue.typeCount + 1
    }

    def getThis(h: AbsHeap): LocSet = {
      val locSet1 = (pvalue.nullval.isBottom, pvalue.undefval.isBottom) match {
        case (true, true) => LocSet.Bot
        case _ => LocSet(GLOBAL_LOC)
      }

      val foundDeclEnvRecord = locset.exists(loc => !h.isObject(loc))

      val locSet2 =
        if (foundDeclEnvRecord) LocSet(GLOBAL_LOC)
        else LocSet.Bot
      val locSet3 = locset.foldLeft(LocSet.Bot)((tmpLocSet, loc) => {
        if (h.isObject(loc)) tmpLocSet + loc
        else tmpLocSet
      })

      locSet1 ⊔ locSet2 ⊔ locSet3
    }
  }

  def fromJSON(json: JsValue, cfg: CFG)(implicit uomap: UIdObjMap): Elem = uomap.symbolCheck(json, {
    val fields = json.asJsObject().fields
    Elem(AbsPValue.fromJSON(fields("pvalue")), LocSet.fromJSON(fields("locset"), cfg))
  })
}
