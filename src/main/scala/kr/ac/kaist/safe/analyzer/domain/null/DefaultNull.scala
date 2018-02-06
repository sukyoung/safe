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

import kr.ac.kaist.safe.errors.error.AbsNullParseError
import spray.json._

// default null abstract domain
object DefaultNull extends NullDomain {
  case object Top extends Elem
  case object Bot extends Elem

  def alpha(x: Null): Elem = Top

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case JsString("⊥") => Bot
    case _ => throw AbsNullParseError(v)
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Null] = this match {
      case Bot => ConFin()
      case Top => ConFin(Null)
    }

    def getSingle: ConSingle[Null] = this match {
      case Bot => ConZero()
      case Top => ConOne(Null)
    }

    override def toString: String = this match {
      case Bot => "⊥(null)"
      case Top => "Top(null)"
    }

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Top, Bot) => false
      case _ => true
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    def StrictEquals(that: Elem): AbsBool = (this, that) match {
      case (Top, Top) => AbsBool.True
      case _ => AbsBool.Bot
    }

    def toJson: JsValue = this match {
      case Top => JsString("⊤")
      case Bot => JsString("⊥")
    }
  }
}
