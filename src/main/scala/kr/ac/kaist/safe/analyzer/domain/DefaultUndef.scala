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

/* Default Undefined Domain */

object DefaultUndefUtil extends AbsUndefUtil {
  val Top: AbsUndef = DefaultUndefTop
  val Bot: AbsUndef = DefaultUndefBot
  def alpha: AbsUndef = Top

  sealed abstract class DefaultUndef extends AbsUndef {
    /* AbsUndef Interface */
    def <=(that: AbsUndef): Boolean =
      (this, that) match {
        case (DefaultUndefBot, _) => true
        case (_, DefaultUndefTop) => true
        case _ => false
      }

    def +(that: AbsUndef): AbsUndef =
      (this, that) match {
        case (DefaultUndefBot, _) => that
        case (_, DefaultUndefBot) => this
        case (_, DefaultUndefTop) | (DefaultUndefTop, _) => DefaultUndefTop
      }

    def <>(that: AbsUndef): AbsUndef =
      (this, that) match {
        case (DefaultUndefTop, DefaultUndefTop) => DefaultUndefTop
        case _ => DefaultUndefBot
      }

    def ===(that: AbsUndef, absBool: AbsBoolUtil): AbsBool =
      (this, that) match {
        case (DefaultUndefBot, _) | (_, DefaultUndefBot) => absBool.Bot
        case _ => absBool.True
      }

    override def toString: String =
      this match {
        case DefaultUndefTop => "undefined"
        case DefaultUndefBot => "Bot"
      }

    /* AbsDomain Interface */
    def getAbsCase: AbsCase =
      this match {
        case DefaultUndefBot => AbsBot
        case DefaultUndefTop => AbsTop
      }
    def isTop: Boolean = this == DefaultUndefTop
    def isBottom: Boolean = this == DefaultUndefBot
    def isConcrete: Boolean = this == DefaultUndefTop
    def toAbsString(absString: AbsStringUtil): AbsString =
      if (isConcrete) absString.alpha("undefined")
      else absString.Bot
  }
  case object DefaultUndefTop extends DefaultUndef
  case object DefaultUndefBot extends DefaultUndef
}