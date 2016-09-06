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

  sealed abstract class AbsDom extends AbsUndef {
    def gamma: ConSimple[Undef] = this match {
      case Bot => ConSimpleBot()
      case Top => ConSimpleTop()
    }

    def isBottom: Boolean = this == Bot

    override def toString: String = this match {
      case Bot => "Bot"
      case Top => "undefined"
    }

    def toAbsString: AbsString = this match {
      case Bot => AbsString.Bot
      case Top => AbsString.alpha("undefined")
    }

    def toAbsBoolean: AbsBool = this match {
      case Bot => AbsBool.Bot
      case Top => AbsBool.False
    }

    def toAbsNumber: AbsNumber = this match {
      case Bot => AbsNumber.Bot
      case Top => AbsNumber.NaN
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
