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

object DNullUtil extends AbsNullUtil {
  val Top: AbsNull = DNullTop
  val Bot: AbsNull = DNullBot

  def alpha: AbsNull = Top

  sealed abstract class DNull extends AbsNull {
    /* AbsUndef Interface */
    def <=(that: AbsNull): Boolean =
      (this, that) match {
        case (DNullBot, _) => true
        case (_, DNullTop) => true
        case _ => false
      }

    def +(that: AbsNull): AbsNull =
      (this, that) match {
        case (_, DNullTop) | (DNullTop, _) => DNullTop
        case _ => DNullBot
      }

    def <>(that: AbsNull): AbsNull =
      (this, that) match {
        case (DNullTop, DNullTop) => DNullTop
        case _ => DNullBot
      }

    def ===(that: AbsNull, absBool: AbsBoolUtil): AbsBool =
      (this, that) match {
        case (DNullBot, _) | (_, DNullBot) => absBool.Bot
        case _ => absBool.True
      }

    override def toString: String =
      this match {
        case DNullTop => "null"
        case DNullBot => "Bot"
      }

    /* AbsDomain Interface */
    def getAbsCase: AbsCase =
      this match {
        case DNullBot => AbsBot
        case DNullTop => AbsTop
      }
    def isTop: Boolean = this == DNullTop
    def isBottom: Boolean = this == DNullBot
    def isConcrete: Boolean = this == DNullTop
    def toAbsString(absString: AbsStringUtil): AbsString =
      if (isConcrete) absString.alpha("null")
      else absString.Bot
  }
  case object DNullTop extends DNull
  case object DNullBot extends DNull
}