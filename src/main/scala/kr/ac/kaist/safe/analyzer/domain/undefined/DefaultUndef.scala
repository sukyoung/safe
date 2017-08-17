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

// default undefined abstract domain
object DefaultUndef extends AbsUndefUtil {
  case object Top extends Dom
  case object Bot extends Dom

  def alpha(undef: Undef): AbsUndef = Top

  sealed abstract class Dom extends AbsUndef {
    def gamma: ConSet[Undef] = this match {
      case Bot => ConFin()
      case Top => ConFin(Undef)
    }

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[Undef] = this match {
      case Bot => ConZero()
      case Top => ConOne(Undef)
    }

    override def toString: String = this match {
      case Bot => "⊥(undefined)"
      case Top => "Top(undefined)"
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
