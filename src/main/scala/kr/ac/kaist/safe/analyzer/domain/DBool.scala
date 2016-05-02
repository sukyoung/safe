/**
 * *****************************************************************************
 * Copyright (c) 2012-2013, S-Core, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

object DBoolUtil extends AbsBoolUtil {
  val Top: AbsBool = DBoolTop
  val Bot: AbsBool = DBoolBot
  val True: AbsBool = DBoolTrue
  val False: AbsBool = DBoolFalse

  val bot: AbsBool = DBoolBot

  def alpha(bool: Boolean): AbsBool = if (bool) DBoolTrue else DBoolFalse

  sealed abstract class DBool extends AbsBool {
    /* AbsUndef Interface */
    def <=(that: AbsBool): Boolean =
      (this, that) match {
        case (a, b) if a == b => true
        case (DBoolBot, _) => true
        case (_, DBoolTop) => true
        case _ => false
      }

    def </(that: AbsBool): Boolean =
      (this, that) match {
        case (a, b) if a == b => false
        case (DBoolBot, _) => false
        case (_, DBoolTop) => false
        case _ => true
      }

    def +(that: AbsBool): AbsBool =
      (this, that) match {
        case (a, b) if a == b => this
        case (DBoolBot, _) => that
        case (_, DBoolBot) => this
        case _ => DBoolTop
      }

    def <>(that: AbsBool): AbsBool =
      (this, that) match {
        case (a, b) if a == b => this
        case (DBoolTop, _) => that
        case (_, DBoolTop) => this
        case _ => DBoolBot
      }

    def ===(that: AbsBool, absBool: AbsBoolUtil): AbsBool = {
      (this, that) match {
        case (DBoolBot, _) => DBoolBot
        case (_, DBoolBot) => DBoolBot
        case (DBoolTop, _) => DBoolTop
        case (_, DBoolTop) => DBoolTop
        case (DBoolTrue, DBoolTrue) => DBoolTrue
        case (DBoolFalse, DBoolFalse) => DBoolTrue
        case _ => DBoolFalse
      }
    }

    override def toString: String =
      this match {
        case DBoolTop => "Bool"
        case DBoolBot => "Bot"
        case DBoolTrue => "true"
        case DBoolFalse => "false"
      }

    def unary_!(): AbsBool = {
      this match {
        case DBoolTop => DBoolTop
        case DBoolBot => DBoolBot
        case DBoolTrue => DBoolFalse
        case DBoolFalse => DBoolTrue
      }
    }

    /* AbsDomain Interface */
    def getAbsCase: AbsCase =
      this match {
        case DBoolBot => AbsBot
        case DBoolTop => AbsTop
        case DBoolTrue | DBoolFalse => AbsSingle
      }

    def getSingle: Option[Boolean] =
      this match {
        case DBoolTrue => Some(true)
        case DBoolFalse => Some(false)
        case _ => None
      }

    def gammaOpt: Option[Set[Boolean]] =
      this match {
        case DBoolTrue => Some(Set(true))
        case DBoolFalse => Some(Set(false))
        case _ => None
      }

    def isTop: Boolean = this == DBoolTop
    def isBottom: Boolean = this == DBoolBot
    def isConcrete: Boolean = this == DBoolTrue || this == DBoolFalse
    def toAbsString(absString: AbsStringUtil): AbsString =
      this match {
        case DBoolTrue => absString.alpha("true")
        case DBoolFalse => absString.alpha("false")
        case _ => absString.Bot
      }
  }
  case object DBoolTop extends DBool
  case object DBoolBot extends DBool
  case object DBoolTrue extends DBool
  case object DBoolFalse extends DBool
}