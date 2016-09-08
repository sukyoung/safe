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

// default undefined abstract domain
object DefaultUndef extends AbsUndefUtil {
  case object Top extends AbsDom
  case object Bot extends AbsDom

  def alpha(undef: Undef): AbsUndef = Top

  sealed abstract class AbsDom extends AbsUndef {
    def gamma: ConSet[Undef] = this match {
      case Bot => ConFin()
      case Top => ConFin(Undef)
    }

    def isBottom: Boolean = this == Bot

    def getSingle: ConSingle[Undef] = this match {
      case Bot => ConZero()
      case Top => ConOne(Undef)
    }

    override def toString: String = this match {
      case Bot => "Bot"
      case Top => "undefined"
    }

    def <=(that: AbsUndef): Boolean = (this, check(that)) match {
      case (Top, Bot) => false
      case _ => true
    }

    def +(that: AbsUndef): AbsUndef = (this, check(that)) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    def <>(that: AbsUndef): AbsUndef = (this, check(that)) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    def ===(that: AbsUndef): AbsBool = (this, check(that)) match {
      case (Top, Top) => AbsBool.True
      case _ => AbsBool.Bot
    }
  }
}
