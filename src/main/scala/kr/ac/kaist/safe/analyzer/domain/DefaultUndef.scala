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

/* Default Undefined Domain */

object DefaultUndefUtil extends AbsUndefUtil {
  val Top: AbsUndef = DefaultUndefTop
  val Bot: AbsUndef = DefaultUndefBot
  def alpha: AbsUndef = Top

  sealed abstract class DefaultUndef extends AbsUndef {
    /* AbsDomain Interface */
    def gamma: ConSimple
    def toAbsString(absString: AbsStringUtil): AbsString
    override def toString: String

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
  }

  case object DefaultUndefTop extends DefaultUndef {
    val gamma: ConSimple = ConSimpleTop
    override val toString: String = "undefined"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha("undefined")
  }

  case object DefaultUndefBot extends DefaultUndef {
    val gamma: ConSimple = ConSimpleBot
    override val toString: String = "Bot"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.Bot
  }
}
