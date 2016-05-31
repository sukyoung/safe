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

object DefaultNumUtil extends AbsNumberUtil {
  val Top: AbsNumber = DefaultNumTop
  val Bot: AbsNumber = DefaultNumBot
  val Infinity: AbsNumber = DefaultNumInf
  val PosInf: AbsNumber = DefaultNumPosInf
  val NegInf: AbsNumber = DefaultNumNegInf
  val NaN: AbsNumber = DefaultNumNaN
  val UInt: AbsNumber = DefaultNumUInt
  val NUInt: AbsNumber = DefaultNumNUInt

  val naturalNumbers = UInt

  def alpha(num: Double): AbsNumber =
    num match {
      case Double.NaN => DefaultNumNaN
      case Double.NegativeInfinity => DefaultNumNegInf
      case Double.PositiveInfinity => DefaultNumPosInf
      case _ =>
        val intnum = num.toInt
        val diff: Double = num - intnum.toDouble
        if ((diff == 0) && num >= 0) DefaultNumUIntConst(num)
        else DefaultNumNUIntConst(num)
    }

  sealed abstract class DefaultNumber extends AbsNumber {
    def getAbsCase: AbsCase =
      this match {
        case DefaultNumTop => AbsTop
        case DefaultNumBot => AbsBot
        case DefaultNumInf => AbsMulti
        case DefaultNumPosInf => AbsSingle
        case DefaultNumNegInf => AbsSingle
        case DefaultNumNaN => AbsSingle
        case DefaultNumUInt => AbsMulti
        case DefaultNumNUInt => AbsMulti
        case DefaultNumUIntConst(v) => AbsSingle
        case DefaultNumNUIntConst(v) => AbsSingle
      }

    def getSingle: Option[Double] =
      this match {
        case DefaultNumPosInf => Some(Double.PositiveInfinity)
        case DefaultNumNegInf => Some(Double.NegativeInfinity)
        case DefaultNumNaN => Some(Double.NaN)
        case DefaultNumUIntConst(v) => Some(v)
        case DefaultNumNUIntConst(v) => Some(v)
        case _ => None
      }

    def gammaOpt: Option[Set[Double]] =
      this match {
        case DefaultNumInf => Some(Set(Double.PositiveInfinity, Double.NegativeInfinity))
        case DefaultNumPosInf => Some(Set(Double.PositiveInfinity))
        case DefaultNumNegInf => Some(Set(Double.NegativeInfinity))
        case DefaultNumNaN => Some(Set(Double.NaN))
        case DefaultNumUIntConst(value) => Some(Set(value))
        case DefaultNumNUIntConst(value) => Some(Set(value))
        case _ => None
      }

    /* partial order */
    def <=(that: AbsNumber): Boolean =
      (this, that) match {
        case (DefaultNumBot, _) => true
        case (_, DefaultNumTop) => true
        case (DefaultNumNaN, DefaultNumNaN) => true
        case (DefaultNumPosInf, DefaultNumPosInf) => true
        case (DefaultNumPosInf, DefaultNumInf) => true
        case (DefaultNumNegInf, DefaultNumNegInf) => true
        case (DefaultNumNegInf, DefaultNumInf) => true
        case (DefaultNumInf, DefaultNumInf) => true
        case (DefaultNumUIntConst(a), DefaultNumUIntConst(b)) => a == b
        case (DefaultNumUIntConst(_), DefaultNumUInt) => true
        case (DefaultNumNUIntConst(a), DefaultNumNUIntConst(b)) => a == b
        case (DefaultNumNUIntConst(_), DefaultNumNUInt) => true
        case (DefaultNumUInt, DefaultNumUInt) => true
        case (DefaultNumNUInt, DefaultNumNUInt) => true
        case _ => false
      }

    def isSmallerThan(that: AbsNumber, absBool: AbsBoolUtil): AbsBool = {
      if (this <= DefaultNumBot || that <= DefaultNumBot) absBool.Bot
      else {
        (this.getSingle, that.getSingle) match {
          case (Some(v_1), Some(v_2)) => absBool.alpha(v_1 < v_2)
          case (None, Some(v_2)) => {
            this match {
              case DefaultNumUInt => {
                if (v_2 <= 0) absBool.False
                else absBool.Top
              }
              case _ => absBool.Top
            }
          }
          case _ => absBool.Top
        }
      }
    }

    /* abstract operator 'equal to' */
    def isEqualTo(that: AbsNumber, absBool: AbsBoolUtil): AbsBool = {
      if (this <= DefaultNumBot || that <= DefaultNumBot)
        absBool.Bot
      else if (this <= DefaultNumNaN || that <= DefaultNumNaN)
        absBool.False
      else {
        (this.getSingle, that.getSingle) match {
          case (Some(v_1), Some(v_2)) => absBool.alpha(v_1 == v_2)
          case _ =>
            (this <= that, that <= this) match {
              case (false, false) => absBool.False
              case _ => absBool.Top
            }
        }
      }
    }

    /* join */
    def +(that: AbsNumber): AbsNumber =
      (this, that) match {
        case (DefaultNumTop, _) => this
        case (_, DefaultNumTop) => that
        case (DefaultNumBot, _) => that
        case (_, DefaultNumBot) => this
        case (DefaultNumPosInf, DefaultNumNegInf) => DefaultNumInf
        case (DefaultNumNegInf, DefaultNumPosInf) => DefaultNumInf
        case (DefaultNumUIntConst(a), DefaultNumUIntConst(b)) =>
          if (a == b) this
          else DefaultNumUInt
        case (DefaultNumNUIntConst(a), DefaultNumNUIntConst(b)) =>
          if (a == b) this
          else DefaultNumNUInt
        case _ =>
          (this <= that, that <= this) match {
            case (true, _) => that
            case (_, true) => this
            case _ => DefaultNumTop
          }
      }

    /* meet */
    def <>(that: AbsNumber): AbsNumber = {
      (this <= that, that <= this) match {
        case (true, _) => this
        case (_, true) => that
        case _ => DefaultNumBot
      }
    }

    override def toString: String =
      this match {
        case DefaultNumTop => "Number"
        case DefaultNumBot => "Bot"
        case DefaultNumInf => "Inf"
        case DefaultNumPosInf => "+Inf"
        case DefaultNumNegInf => "-Inf"
        case DefaultNumNaN => "NaN"
        case DefaultNumUInt => "UInt"
        case DefaultNumNUInt => "NUInt"
        case DefaultNumUIntConst(n) => n.toLong.toString
        case DefaultNumNUIntConst(n) => n.toString
      }

    override def isTop: Boolean = { this == DefaultNumTop }

    override def isBottom: Boolean = { this == DefaultNumBot }

    override def isConcrete: Boolean = {
      this match {
        case DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf
          | DefaultNumNaN
          | DefaultNumUIntConst(_)
          | DefaultNumNUIntConst(_) => true
        case _ => false
      }
    }

    def toBoolean(absBool: AbsBoolUtil): AbsBool =
      this match {
        case DefaultNumPosInf | DefaultNumNegInf => absBool.True //TODO: Check DefaultNumInf => absBool.True?
        case DefaultNumNaN => absBool.False
        case DefaultNumUIntConst(n) if n == 0 => absBool.False
        case DefaultNumUIntConst(n) => absBool.True
        case DefaultNumNUIntConst(n) if n == 0 => absBool.False
        case DefaultNumNUIntConst(n) => absBool.True
        case _ => absBool.Bot //TODO: Check DefaultNumUInt => absBool.Top?
      }

    override def toAbsString(absString: AbsStringUtil): AbsString = {
      this match {
        case DefaultNumPosInf => absString.alpha("Infinity")
        case DefaultNumNegInf => absString.alpha("-Infinity")
        case DefaultNumNaN => absString.alpha("NaN")
        case DefaultNumUIntConst(n) => absString.alpha(n.toInt.toString)
        case DefaultNumNUIntConst(n) if n == n.toInt => absString.alpha(n.toInt.toString)
        case DefaultNumNUIntConst(n) => absString.alpha(n.toString)
        case DefaultNumBot => absString.Bot
        case _ => absString.NumStr
      }
    }

    def isNum(v: Double): Boolean =
      this match {
        case DefaultNumUIntConst(n) if n == v => true
        case _ => false
      }

    def isNum: Boolean =
      this match {
        case DefaultNumUIntConst(_)
          | DefaultNumNUIntConst(_) => true
        case _ => false
      }

    def isFinite: Boolean =
      this match {
        case DefaultNumUInt
          | DefaultNumNUInt
          | DefaultNumUIntConst(_)
          | DefaultNumNUIntConst(_) => true
        case _ => false
      }

    def isFiniteMulti: Boolean =
      this match {
        case DefaultNumUInt
          | DefaultNumNUInt => true
        case _ => false
      }

    def isInfinity: Boolean = this == DefaultNumInf
    def isPosInf: Boolean = this == DefaultNumPosInf
    def isNegInf: Boolean = this == DefaultNumNegInf

    def isInf: Boolean =
      this match {
        case DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf => true
        case _ => false
      }

    def isInfOrNaN: Boolean =
      this match {
        case DefaultNumInf
          | DefaultNumPosInf
          | DefaultNumNegInf => true
        case DefaultNumNaN => true
        case _ => false
      }

    def getUIntSingle: Option[Double] =
      this match {
        case DefaultNumUIntConst(v) => Some(v)
        case _ => None
      }

    def isUIntSingle: Boolean =
      this match {
        case DefaultNumUIntConst(_) => true
        case _ => false
      }

    def isNUIntSingle: Boolean =
      this match {
        case DefaultNumNUIntConst(_) => true
        case _ => false
      }

    def isUIntAll: Boolean = this == DefaultNumTop | this == DefaultNumUInt

    def isUInt: Boolean = this == DefaultNumUInt

    def isUIntOrBot: Boolean =
      this match {
        case DefaultNumUInt
          | DefaultNumUIntConst(_) => true
        case DefaultNumBot => true
        case _ => false
      }

    def isNaN: Boolean = this == DefaultNumNaN
  }
  case object DefaultNumTop extends DefaultNumber
  case object DefaultNumBot extends DefaultNumber
  case object DefaultNumInf extends DefaultNumber
  case object DefaultNumPosInf extends DefaultNumber
  case object DefaultNumNegInf extends DefaultNumber
  case object DefaultNumNaN extends DefaultNumber
  case object DefaultNumUInt extends DefaultNumber
  case object DefaultNumNUInt extends DefaultNumber
  case class DefaultNumUIntConst(value: Double) extends DefaultNumber
  case class DefaultNumNUIntConst(value: Double) extends DefaultNumber
}
