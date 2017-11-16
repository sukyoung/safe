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
import kr.ac.kaist.safe.nodes.cfg.FunctionId

////////////////////////////////////////////////////////////////////////////////
// concrete internal value type
////////////////////////////////////////////////////////////////////////////////
abstract class IValue

////////////////////////////////////////////////////////////////////////////////
// value abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsIValue extends AbsDomain[IValue, AbsIValue] {
  val value: AbsValue
  val fidset: AbsFId
}

trait AbsIValueUtil extends AbsDomainUtil[IValue, AbsIValue] {
  def apply(value: AbsValue): AbsIValue
  def apply(fidset: AbsFId): AbsIValue
  def apply(value: AbsValue, fidset: AbsFId): AbsIValue
}

////////////////////////////////////////////////////////////////////////////////
// default internal value abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultIValue extends AbsIValueUtil {
  lazy val Bot: Dom = Dom(AbsValue.Bot, AbsFId.Bot)
  lazy val Top: Dom = Dom(AbsValue.Top, AbsFId.Top)

  def alpha(value: IValue): AbsIValue = value match {
    case (value: Value) => AbsValue(value)
    case (fid: FId) => AbsFId(fid)
  }

  def apply(value: AbsValue): AbsIValue = Bot.copy(value = value)
  def apply(fidset: AbsFId): AbsIValue = Bot.copy(fidset = fidset)
  def apply(value: AbsValue, fidset: AbsFId): AbsIValue = Dom(value, fidset)

  case class Dom(value: AbsValue, fidset: AbsFId) extends AbsIValue {
    def gamma: ConSet[IValue] = ConInf() // TODO more precisely

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[IValue] = ConMany()

    override def toString: String = {
      if (isBottom) "⊥AbsIValue"
      else {
        var list: List[String] = Nil
        value.foldUnit(list :+= _.toString)
        fidset.foldUnit(list :+= _.toString)
        list.mkString(", ")
      }
    }

    def <=(that: AbsIValue): Boolean = {
      val (left, right) = (this, check(that))
      left.value <= right.value &&
        left.fidset <= right.fidset
    }

    def +(that: AbsIValue): AbsIValue = {
      val (left, right) = (this, check(that))
      Dom(
        left.value + right.value,
        left.fidset + right.fidset
      )
    }

    def <>(that: AbsIValue): AbsIValue = {
      val (left, right) = (this, check(that))
      AbsIValue(
        left.value <> right.value,
        left.fidset <> right.fidset
      )
    }
  }
}
