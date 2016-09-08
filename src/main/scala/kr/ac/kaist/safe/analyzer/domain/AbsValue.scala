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

////////////////////////////////////////////////////////////////////////////////
// concrete value type
////////////////////////////////////////////////////////////////////////////////
abstract class Value
object Value {
  // TODO how to define only once following implicit conversions
  implicit def bool2bool(b: Boolean): Bool = Bool(b)
  implicit def bool2bool(set: Set[Boolean]): Set[Bool] = set.map(bool2bool)
  implicit def num2num(num: Double): Num = Num(num)
  implicit def num2num(set: Set[Double]): Set[Num] = set.map(num2num)
  implicit def str2str(str: String): Str = Str(str)
  implicit def str2str(set: Set[String]): Set[Str] = set.map(str2str)
}

////////////////////////////////////////////////////////////////////////////////
// value abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsValue extends AbsDomain[Value, AbsValue] {
  val pvalue: AbsPValue
  val locset: AbsLoc

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): AbsValue
  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): AbsValue
  // TODO working but more simple way is exist with modifying getBase
  def getThis(h: Heap): AbsLoc

  def typeCount: Int
  def typeKinds: String
}

trait AbsValueUtil extends AbsDomainUtil[Value, AbsValue] {
  def apply(pvalue: AbsPValue): AbsValue
  def apply(loc: Loc): AbsValue
  def apply(locset: AbsLoc): AbsValue
  def apply(undefval: AbsUndef): AbsValue
  def apply(nullval: AbsNull): AbsValue
  def apply(boolval: AbsBool): AbsValue
  def apply(numval: AbsNumber): AbsValue
  def apply(strval: AbsString): AbsValue
}

////////////////////////////////////////////////////////////////////////////////
// default value abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultValue extends AbsValueUtil {
  lazy val Bot: AbsDom = AbsDom(AbsPValue.Bot, AbsLoc.Bot)
  lazy val Top: AbsDom = AbsDom(AbsPValue.Top, AbsLoc.Top)

  def alpha(value: Value): AbsValue = value match {
    case (pvalue: PValue) => apply(AbsPValue.alpha(pvalue))
    case (loc: Loc) => apply(AbsLoc.alpha(loc))
  }

  def apply(pvalue: AbsPValue): AbsValue = Bot.copy(pvalue = pvalue)
  def apply(loc: Loc): AbsValue = Bot.copy(locset = AbsLoc.alpha(loc))
  def apply(locset: AbsLoc): AbsValue = Bot.copy(locset = locset)
  def apply(undefval: AbsUndef): AbsValue = apply(AbsPValue(undefval))
  def apply(nullval: AbsNull): AbsValue = apply(AbsPValue(nullval))
  def apply(boolval: AbsBool): AbsValue = apply(AbsPValue(boolval))
  def apply(numval: AbsNumber): AbsValue = apply(AbsPValue(numval))
  def apply(strval: AbsString): AbsValue = apply(AbsPValue(strval))

  case class AbsDom(pvalue: AbsPValue, locset: AbsLoc) extends AbsValue {
    def gamma: ConSet[Value] = ConSetTop() // TODO more precisely

    def isBottom: Boolean = this == Bot

    def <=(that: AbsValue): Boolean = {
      val (left, right) = (this, check(that))
      left.pvalue <= right.pvalue &&
        left.locset <= right.locset
    }

    def +(that: AbsValue): AbsValue = {
      val (left, right) = (this, check(that))
      AbsDom(
        left.pvalue + right.pvalue,
        left.locset + right.locset
      )
    }

    def <>(that: AbsValue): AbsValue = {
      val (left, right) = (this, check(that))
      AbsDom(
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
        case (true, true) => "⊥Value"
        case (true, false) => locSetStr
        case (false, true) => pvalStr
        case (false, false) => s"$pvalStr, $locSetStr"
      }
    }

    def subsLoc(locR: Loc, locO: Loc): AbsValue = {
      if (this.locset contains locR) AbsDom(this.pvalue, (this.locset - locR) + locO)
      else this
    }

    def weakSubsLoc(locR: Loc, locO: Loc): AbsValue = {
      if (this.locset contains locR) AbsDom(this.pvalue, this.locset + locO)
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
}
