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

object DefaultBoolUtil extends AbsBoolUtil {
  val Top: AbsBool = DefaultBoolTop
  val Bot: AbsBool = DefaultBoolBot
  val True: AbsBool = DefaultBoolTrue
  val False: AbsBool = DefaultBoolFalse

  val bot: AbsBool = DefaultBoolBot

  def alpha(bool: Boolean): AbsBool = if (bool) DefaultBoolTrue else DefaultBoolFalse

  sealed abstract class DefaultBool extends AbsBool {
    /* AbsUndef Interface */
    def <=(that: AbsBool): Boolean =
      (this, that) match {
        case (a, b) if a == b => true
        case (DefaultBoolBot, _) => true
        case (_, DefaultBoolTop) => true
        case _ => false
      }

    def </(that: AbsBool): Boolean =
      (this, that) match {
        case (a, b) if a == b => false
        case (DefaultBoolBot, _) => false
        case (_, DefaultBoolTop) => false
        case _ => true
      }

    def +(that: AbsBool): AbsBool =
      (this, that) match {
        case (a, b) if a == b => this
        case (DefaultBoolBot, _) => that
        case (_, DefaultBoolBot) => this
        case _ => DefaultBoolTop
      }

    def <>(that: AbsBool): AbsBool =
      (this, that) match {
        case (a, b) if a == b => this
        case (DefaultBoolTop, _) => that
        case (_, DefaultBoolTop) => this
        case _ => DefaultBoolBot
      }

    def ===(that: AbsBool, absBool: AbsBoolUtil): AbsBool = {
      (this, that) match {
        case (DefaultBoolBot, _) => DefaultBoolBot
        case (_, DefaultBoolBot) => DefaultBoolBot
        case (DefaultBoolTop, _) => DefaultBoolTop
        case (_, DefaultBoolTop) => DefaultBoolTop
        case (DefaultBoolTrue, DefaultBoolTrue) => DefaultBoolTrue
        case (DefaultBoolFalse, DefaultBoolFalse) => DefaultBoolTrue
        case _ => DefaultBoolFalse
      }
    }

    override def toString: String =
      this match {
        case DefaultBoolTop => "Bool"
        case DefaultBoolBot => "Bot"
        case DefaultBoolTrue => "true"
        case DefaultBoolFalse => "false"
      }

    def unary(): AbsBool = {
      this match {
        case DefaultBoolTop => DefaultBoolTop
        case DefaultBoolBot => DefaultBoolBot
        case DefaultBoolTrue => DefaultBoolFalse
        case DefaultBoolFalse => DefaultBoolTrue
      }
    }

    /* AbsDomain Interface */
    def getAbsCase: AbsCase =
      this match {
        case DefaultBoolBot => AbsBot
        case DefaultBoolTop => AbsTop
        case DefaultBoolTrue | DefaultBoolFalse => AbsSingle
      }

    def getSingle: Option[Boolean] =
      this match {
        case DefaultBoolTrue => Some(true)
        case DefaultBoolFalse => Some(false)
        case _ => None
      }

    def gammaOpt: Option[Set[Boolean]] =
      this match {
        case DefaultBoolTrue => Some(Set(true))
        case DefaultBoolFalse => Some(Set(false))
        case _ => None
      }

    def isTop: Boolean = this == DefaultBoolTop
    def isBottom: Boolean = this == DefaultBoolBot
    def isConcrete: Boolean = this == DefaultBoolTrue || this == DefaultBoolFalse
    def toAbsString(absString: AbsStringUtil): AbsString =
      this match {
        case DefaultBoolTop => absString.OtherStr
        case DefaultBoolTrue => absString.alpha("true")
        case DefaultBoolFalse => absString.alpha("false")
        case DefaultBoolBot => absString.Bot
      }
  }
  case object DefaultBoolTop extends DefaultBool
  case object DefaultBoolBot extends DefaultBool
  case object DefaultBoolTrue extends DefaultBool
  case object DefaultBoolFalse extends DefaultBool
}
