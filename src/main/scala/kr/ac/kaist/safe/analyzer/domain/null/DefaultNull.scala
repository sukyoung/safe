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

// default null abstract domain
object DefaultNull extends AbsNullUtil {
  case object Top extends AbsDom
  case object Bot extends AbsDom

  def alpha(x: Null): AbsNull = Top

  sealed abstract class AbsDom extends AbsNull {
    def gamma: ConSet[Null] = this match {
      case Bot => ConFin()
      case Top => ConFin(Null)
    }

    def isBottom: Boolean = this == Bot

    def getSingle: ConSingle[Null] = this match {
      case Bot => ConZero()
      case Top => ConOne(Null)
    }

    override def toString: String = this match {
      case Bot => "⊥(null)"
      case Top => "Top(null)"
    }

    def <=(that: AbsNull): Boolean = (this, check(that)) match {
      case (Top, Bot) => false
      case _ => true
    }

    def +(that: AbsNull): AbsNull = (this, check(that)) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    def <>(that: AbsNull): AbsNull = (this, check(that)) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    def ===(that: AbsNull): AbsBool = (this, check(that)) match {
      case (Top, Top) => AbsBool.True
      case _ => AbsBool.Bot
    }
  }
}
