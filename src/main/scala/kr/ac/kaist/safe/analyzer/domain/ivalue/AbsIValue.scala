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
trait IValueDomain extends AbsDomain[IValue] { domain: IValueDomain =>
  def apply(value: AbsValue): Elem
  def apply(fidset: AbsFId): Elem
  def apply(value: AbsValue, fidset: AbsFId): Elem

  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val value: AbsValue
    val fidset: AbsFId
  }
}

////////////////////////////////////////////////////////////////////////////////
// default internal value abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultIValue extends IValueDomain {
  lazy val Bot: Elem = Elem(AbsValue.Bot, AbsFId.Bot)
  lazy val Top: Elem = Elem(AbsValue.Top, AbsFId.Top)

  def alpha(value: IValue): Elem = value match {
    case (value: Value) => AbsValue(value)
    case (fid: FId) => AbsFId(fid)
  }

  def apply(value: AbsValue): Elem = Bot.copy(value = value)
  def apply(fidset: AbsFId): Elem = Bot.copy(fidset = fidset)
  def apply(value: AbsValue, fidset: AbsFId): Elem = Elem(value, fidset)

  case class Elem(value: AbsValue, fidset: AbsFId) extends ElemTrait {
    def gamma: ConSet[IValue] = ConInf() // TODO more precisely

    def getSingle: ConSingle[IValue] = ConMany()

    override def toString: String = {
      if (isBottom) "⊥Elem"
      else {
        var list: List[String] = Nil
        value.foldUnit(list :+= _.toString)
        fidset.foldUnit(list :+= _.toString)
        list.mkString(", ")
      }
    }

    def <=(that: Elem): Boolean = {
      val (left, right) = (this, that)
      left.value <= right.value &&
        left.fidset <= right.fidset
    }

    def +(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.value + right.value,
        left.fidset + right.fidset
      )
    }

    def <>(that: Elem): Elem = {
      val (left, right) = (this, that)
      Elem(
        left.value <> right.value,
        left.fidset <> right.fidset
      )
    }
  }
}
