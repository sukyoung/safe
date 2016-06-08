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
  def alpha(bool: Boolean): AbsBool = if (bool) DefaultBoolTrue else DefaultBoolFalse

  sealed abstract class DefaultBool extends AbsBool {
    /* AbsDomain Interface */
    def gamma: ConSingle[Boolean]
    def gammaSimple: ConSimple = ConSimpleTop
    override def toString: String
    def toAbsString(absString: AbsStringUtil): AbsString
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber

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

    def negate: AbsBool = {
      this match {
        case DefaultBoolTrue => DefaultBoolFalse
        case DefaultBoolFalse => DefaultBoolTrue
        case _ => this
      }
    }
  }

  case object DefaultBoolTop extends DefaultBool {
    val gamma: ConSingle[Boolean] = ConSingleTop()
    override val toString: String = "Bool"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.OtherStr
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Top
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.UInt
  }

  case object DefaultBoolBot extends DefaultBool {
    val gamma: ConSingle[Boolean] = ConSingleBot()
    override val toString: String = "Bot"
    override val gammaSimple: ConSimple = ConSimpleBot
    def toAbsString(absString: AbsStringUtil): AbsString = absString.Bot
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Bot
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.Bot
  }

  case object DefaultBoolTrue extends DefaultBool {
    val gamma: ConSingle[Boolean] = ConSingleCon(true)
    override val toString: String = "true"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha("true")
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.True
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.alpha(1)
  }

  case object DefaultBoolFalse extends DefaultBool {
    val gamma: ConSingle[Boolean] = ConSingleCon(false)
    override val toString: String = "false"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha("false")
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.False
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.alpha(+0)
  }
}
