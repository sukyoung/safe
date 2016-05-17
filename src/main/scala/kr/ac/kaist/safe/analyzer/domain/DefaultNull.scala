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

object DefaultNullUtil extends AbsNullUtil {
  val Top: AbsNull = DefaultNullTop
  val Bot: AbsNull = DefaultNullBot

  def alpha: AbsNull = Top

  sealed abstract class DefaultNull extends AbsNull {
    /* AbsUndef Interface */
    def <=(that: AbsNull): Boolean =
      (this, that) match {
        case (DefaultNullBot, _) => true
        case (_, DefaultNullTop) => true
        case _ => false
      }

    def +(that: AbsNull): AbsNull =
      (this, that) match {
        case (_, DefaultNullTop) | (DefaultNullTop, _) => DefaultNullTop
        case _ => DefaultNullBot
      }

    def <>(that: AbsNull): AbsNull =
      (this, that) match {
        case (DefaultNullTop, DefaultNullTop) => DefaultNullTop
        case _ => DefaultNullBot
      }

    def ===(that: AbsNull, absBool: AbsBoolUtil): AbsBool =
      (this, that) match {
        case (DefaultNullBot, _) | (_, DefaultNullBot) => absBool.Bot
        case _ => absBool.True
      }

    override def toString: String =
      this match {
        case DefaultNullTop => "null"
        case DefaultNullBot => "Bot"
      }

    /* AbsDomain Interface */
    def getAbsCase: AbsCase =
      this match {
        case DefaultNullBot => AbsBot
        case DefaultNullTop => AbsTop
      }
    def isTop: Boolean = this == DefaultNullTop
    def isBottom: Boolean = this == DefaultNullBot
    def isConcrete: Boolean = this == DefaultNullTop
    def toAbsString(absString: AbsStringUtil): AbsString =
      if (isConcrete) absString.alpha("null")
      else absString.Bot
  }
  case object DefaultNullTop extends DefaultNull
  case object DefaultNullBot extends DefaultNull
}