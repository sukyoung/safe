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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.util._

////////////////////////////////////////////////////////////////////////////////
// concrete value type
////////////////////////////////////////////////////////////////////////////////
abstract class Value extends IValue

// helper values for modeling
abstract class TypeValue(name: String) extends Value {
  override def toString: String = name
}
case object StringT extends TypeValue("string")
case object NumberT extends TypeValue("number")
case object BoolT extends TypeValue("bool")

////////////////////////////////////////////////////////////////////////////////
// value abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsValue extends AbsDomain[Value, AbsValue] {
  val pvalue: AbsPValue
  val locset: AbsLoc

  /* substitute locR by locO */
  def subsLoc(locR: Recency, locO: Recency): AbsValue
  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Recency, locO: Recency): AbsValue
  // TODO working but a more simple way exists with modifying getBase
  def getThis(h: AbsHeap): AbsLoc

  def typeCount: Int
  def typeKinds: Set[String]
}

trait AbsValueUtil extends AbsDomainUtil[Value, AbsValue] {
  def apply(pvalue: AbsPValue): AbsValue
  def apply(locset: AbsLoc): AbsValue
  def apply(pvalue: AbsPValue, locset: AbsLoc): AbsValue
}

////////////////////////////////////////////////////////////////////////////////
// default value abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultValue extends AbsValueUtil {
  lazy val Bot: Dom = Dom(AbsPValue.Bot, AbsLoc.Bot)
  lazy val Top: Dom = Dom(AbsPValue.Top, AbsLoc.Top)

  def alpha(value: Value): AbsValue = value match {
    case (pvalue: PValue) => apply(AbsPValue(pvalue))
    case (loc: Loc) => apply(AbsLoc(loc))
    case StringT => apply(AbsString.Top)
    case NumberT => apply(AbsNumber.Top)
    case BoolT => apply(AbsBool.Top)
  }

  def apply(pvalue: AbsPValue): AbsValue = Bot.copy(pvalue = pvalue)
  def apply(locset: AbsLoc): AbsValue = Bot.copy(locset = locset)
  def apply(pvalue: AbsPValue, locset: AbsLoc): AbsValue = Dom(pvalue, locset)

  case class Dom(pvalue: AbsPValue, locset: AbsLoc) extends AbsValue {
    def gamma: ConSet[Value] = ConInf() // TODO more precisely

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[Value] = ConMany() // TODO more precisely

    def <=(that: AbsValue): Boolean = {
      val (left, right) = (this, check(that))
      left.pvalue <= right.pvalue &&
        left.locset <= right.locset
    }

    def +(that: AbsValue): AbsValue = {
      val (left, right) = (this, check(that))
      Dom(
        left.pvalue + right.pvalue,
        left.locset + right.locset
      )
    }

    def <>(that: AbsValue): AbsValue = {
      val (left, right) = (this, check(that))
      Dom(
        left.pvalue <> right.pvalue,
        left.locset <> right.locset
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

    def subsLoc(locR: Recency, locO: Recency): AbsValue =
      Dom(this.pvalue, this.locset.subsLoc(locR, locO))

    def weakSubsLoc(locR: Recency, locO: Recency): AbsValue =
      Dom(this.pvalue, this.locset.weakSubsLoc(locR, locO))

    def typeCount: Int = {
      if (this.locset.isBottom)
        pvalue.typeCount
      else
        pvalue.typeCount + 1
    }

    def typeKinds: Set[String] = {

      val typeKindsSet: Set[(AbsDomain[_, _], String)] = Set(
        (pvalue.boolval, "Boolean"),
        (pvalue.nullval, "Null"),
        (pvalue.numval, "Number"),
        (pvalue.strval, "String"),
        (pvalue.undefval, "Undefined"),
        (locset, "Object")
      )
      typeKindsSet.filter({
        case (value, _) =>
          !value.isBottom
      }).map({
        case (_, default) =>
          default
      })
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

      locSet1 + locSet2 + locSet3
    }
  }
}
