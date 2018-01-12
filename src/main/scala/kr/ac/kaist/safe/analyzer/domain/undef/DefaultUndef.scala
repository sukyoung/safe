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

import kr.ac.kaist.safe.errors.error.AbsUndefParseError
import spray.json._

// default undefined abstract domain
object DefaultUndef extends UndefDomain {
  case object Top extends Elem
  case object Bot extends Elem

  def alpha(undef: Undef): Elem = Top

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case JsString("⊥") => Bot
    case _ => throw AbsUndefParseError(v)
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Undef] = this match {
      case Bot => ConFin()
      case Top => ConFin(Undef)
    }

    def getSingle: ConSingle[Undef] = this match {
      case Bot => ConZero()
      case Top => ConOne(Undef)
    }

    override def toString: String = this match {
      case Bot => "⊥(undefined)"
      case Top => "Top(undefined)"
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
