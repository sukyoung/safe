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

object DefaultNullUtil extends AbsNullUtil {
  val Top: AbsNull = DefaultNullTop
  val Bot: AbsNull = DefaultNullBot
  def alpha: AbsNull = Top

  sealed abstract class DefaultNull extends AbsNull {
    /* AbsDomain Interface */
    def gamma: ConSimple
    override def toString: String
    def toAbsString: AbsString
    def toAbsBoolean: AbsBool
    def toAbsNumber: AbsNumber

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

    def ===(that: AbsNull): AbsBool =
      (this, that) match {
        case (DefaultNullBot, _) | (_, DefaultNullBot) => AbsBool.Bot
        case _ => AbsBool.True
      }
  }

  case object DefaultNullTop extends DefaultNull {
    val gamma: ConSimple = ConSimpleTop
    override val toString: String = "null"
    def toAbsString: AbsString = AbsString.alpha("null")
    def toAbsBoolean: AbsBool = AbsBool.False
    def toAbsNumber: AbsNumber = AbsNumber.alpha(+0)
  }

  case object DefaultNullBot extends DefaultNull {
    val gamma: ConSimple = ConSimpleBot
    override val toString: String = "Bot"
    def toAbsString: AbsString = AbsString.Bot
    def toAbsBoolean: AbsBool = AbsBool.Bot
    def toAbsNumber: AbsNumber = AbsNumber.Bot
  }
}
