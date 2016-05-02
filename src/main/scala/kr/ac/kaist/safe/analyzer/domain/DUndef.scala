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

object DUndefUtil extends AbsUndefUtil {
  val Top: AbsUndef = DUndefTop
  val Bot: AbsUndef = DUndefBot
  def alpha: AbsUndef = Top

  sealed abstract class DUndef extends AbsUndef {
    /* AbsUndef Interface */
    def <=(that: AbsUndef): Boolean =
      (this, that) match {
        case (DUndefBot, _) => true
        case (_, DUndefTop) => true
        case _ => false
      }

    def +(that: AbsUndef): AbsUndef =
      (this, that) match {
        case (DUndefBot, _) => that
        case (_, DUndefBot) => this
        case (_, DUndefTop) | (DUndefTop, _) => DUndefTop
      }

    def <>(that: AbsUndef): AbsUndef =
      (this, that) match {
        case (DUndefTop, DUndefTop) => DUndefTop
        case _ => DUndefBot
      }

    def ===(that: AbsUndef, absBool: AbsBoolUtil): AbsBool =
      (this, that) match {
        case (DUndefBot, _) | (_, DUndefBot) => absBool.Bot
        case _ => absBool.True
      }

    override def toString: String =
      this match {
        case DUndefTop => "undefined"
        case DUndefBot => "Bot"
      }

    /* AbsDomain Interface */
    def getAbsCase: AbsCase =
      this match {
        case DUndefBot => AbsBot
        case DUndefTop => AbsTop
      }
    def isTop: Boolean = this == DUndefTop
    def isBottom: Boolean = this == DUndefBot
    def isConcrete: Boolean = this == DUndefTop
    def toAbsString(absString: AbsStringUtil): AbsString =
      if (isConcrete) absString.alpha("undefined")
      else absString.Bot
  }
  case object DUndefTop extends DUndef
  case object DUndefBot extends DUndef
}