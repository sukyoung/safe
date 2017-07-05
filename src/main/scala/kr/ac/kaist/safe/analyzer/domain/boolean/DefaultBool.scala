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

// default boolean abstract domain
object DefaultBool extends AbsBoolUtil {
  case object Bot extends Dom
  case object True extends Dom
  case object False extends Dom
  case object Top extends Dom

  def alpha(bool: Bool): AbsBool = if (bool) True else False

  sealed abstract class Dom extends AbsBool {
    def gamma: ConSet[Bool] = this match {
      case Bot => ConFin()
      case True => ConFin(true)
      case False => ConFin(false)
      case Top => ConFin(true, false)
    }

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def getSingle: ConSingle[Bool] = this match {
      case Bot => ConZero()
      case True => ConOne(true)
      case False => ConOne(false)
      case Top => ConMany()
    }

    override def toString: String = this match {
      case Bot => "⊥(boolean)"
      case True => "true"
      case False => "false"
      case Top => "Top(boolean)"
    }

    def toAbsString: AbsString = this match {
      case Bot => AbsString.Bot
      case True => AbsString("true")
      case False => AbsString("false")
      case Top => AbsString("true", "false")
    }

    def toAbsNumber: AbsNumber = this match {
      case Bot => AbsNumber.Bot
      case True => AbsNumber(1)
      case False => AbsNumber(+0)
      case Top => AbsNumber(1, +0)
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

    def &&(that: AbsBool): Dom = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      case (False, _) | (_, False) => False
      case (True, True) => True
      case _ => Top
    }

    def ||(that: AbsBool): Dom = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      case (True, _) | (_, True) => True
      case (False, False) => False
      case _ => Top
    }

    def xor(that: AbsBool): Dom = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      case (True, False) | (False, True) => True
      case (False, False) | (True, True) => False
      case _ => Top
    }

    def map[T <: Domain[T]](
      thenV: => T,
      elseV: => T
    )(implicit util: DomainUtil[T]): T = {
      val t =
        if (True <= this) thenV
        else util.Bot
      val e =
        if (False <= this) elseV
        else util.Bot
      t + e
    }

    def map[T <: Domain[T], U <: Domain[U]](
      thenV: => (T, U),
      elseV: => (T, U)
    )(implicit util: (DomainUtil[T], DomainUtil[U])): (T, U) = {
      val (lUtil, rUtil) = util
      val (lt, rt) =
        if (True <= this) thenV
        else (lUtil.Bot, rUtil.Bot)
      val (le, re) =
        if (False <= this) elseV
        else (lUtil.Bot, rUtil.Bot)
      (lt + le, rt + re)
    }
  }
}
