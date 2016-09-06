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

// default boolean abstract domain
object DefaultBool extends AbsBoolUtil {
  case object Bot extends AbsDom
  case object True extends AbsDom
  case object False extends AbsDom
  case object Top extends AbsDom

  def alpha(bool: Boolean): AbsBool = if (bool) True else False

  sealed abstract class AbsDom extends AbsBool {
    def gamma: ConSingle[Boolean] = this match {
      case Bot => ConSingleBot()
      case True => ConSingleCon(true)
      case False => ConSingleCon(false)
      case Top => ConSingleTop()
    }

    def gammaSimple: ConSimple[Boolean] = isBottom match {
      case true => ConSimpleBot()
      case false => ConSimpleTop()
    }

    def isBottom: Boolean = this == Bot

    override def toString: String = this match {
      case Bot => "⊥"
      case True => "true"
      case False => "false"
      case Top => "Top"
    }

    def toAbsString: AbsString = this match {
      case Bot => AbsString.Bot
      case True => AbsString.alpha("true")
      case False => AbsString.alpha("false")
      case Top => AbsString.alpha("true", "false")
    }

    def toAbsBoolean: AbsBool = this

    def toAbsNumber: AbsNumber = this match {
      case Bot => AbsNumber.Bot
      case True => AbsNumber.alpha(1)
      case False => AbsNumber.alpha(+0)
      case Top => AbsNumber.alpha(1, +0)
    }

    def <=(that: AbsBool): Boolean = (this, check(that)) match {
      case (a, b) if a == b => true
      case (Bot, _) => true
      case (_, Top) => true
      case _ => false
    }

    def +(that: AbsBool): AbsBool = (this, check(that)) match {
      case (a, b) if a == b => this
      case (Bot, _) => that
      case (_, Bot) => this
      case _ => Top
    }

    def <>(that: AbsBool): AbsBool = (this, check(that)) match {
      case (a, b) if a == b => this
      case (Top, _) => that
      case (_, Top) => this
      case _ => Bot
    }

    def ===(that: AbsBool): AbsBool = {
      (this, check(that)) match {
        case (Bot, _) => Bot
        case (_, Bot) => Bot
        case (Top, _) => Top
        case (_, Top) => Top
        case (True, True) => True
        case (False, False) => True
        case _ => False
      }
    }

    def negate: AbsBool = this match {
      case True => False
      case False => True
      case _ => this
    }
  }
}
