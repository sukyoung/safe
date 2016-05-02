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

object DNumUtil extends AbsNumberUtil {
  val Top: AbsNumber = DNumTop
  val Bot: AbsNumber = DNumBot
  val Infinity: AbsNumber = DNumInf
  val PosInf: AbsNumber = DNumPosInf
  val NegInf: AbsNumber = DNumNegInf
  val NaN: AbsNumber = DNumNaN
  val UInt: AbsNumber = DNumUInt
  val NUInt: AbsNumber = DNumNUInt

  val naturalNumbers = UInt

  def alpha(num: Double): AbsNumber =
    num match {
      case Double.NaN => DNumNaN
      case Double.NegativeInfinity => DNumNegInf
      case Double.PositiveInfinity => DNumPosInf
      case _ =>
        val intnum = num.toInt
        val diff: Double = num - intnum.toDouble
        if ((diff == 0) && num >= 0) DNumUIntConst(num)
        else DNumNUIntConst(num)
    }

  sealed abstract class DNumber extends AbsNumber {
    def getAbsCase: AbsCase =
      this match {
        case DNumTop => AbsTop
        case DNumBot => AbsBot
        case DNumInf => AbsMulti
        case DNumPosInf => AbsSingle
        case DNumNegInf => AbsSingle
        case DNumNaN => AbsSingle
        case DNumUInt => AbsMulti
        case DNumNUInt => AbsMulti
        case DNumUIntConst(v) => AbsSingle
        case DNumNUIntConst(v) => AbsSingle
      }

    def getSingle: Option[Double] =
      this match {
        case DNumPosInf => Some(Double.PositiveInfinity)
        case DNumNegInf => Some(Double.NegativeInfinity)
        case DNumNaN => Some(Double.NaN)
        case DNumUIntConst(v) => Some(v)
        case DNumNUIntConst(v) => Some(v)
        case _ => None
      }

    def gammaOpt: Option[Set[Double]] =
      this match {
        case DNumInf => Some(Set(Double.PositiveInfinity, Double.NegativeInfinity))
        case DNumPosInf => Some(Set(Double.PositiveInfinity))
        case DNumNegInf => Some(Set(Double.NegativeInfinity))
        case DNumNaN => Some(Set(Double.NaN))
        case DNumUIntConst(value) => Some(Set(value))
        case DNumNUIntConst(value) => Some(Set(value))
        case _ => None
      }

    /* partial order */
    def <=(that: AbsNumber): Boolean =
      (this, that) match {
        case (DNumBot, _) => true
        case (_, DNumTop) => true
        case (DNumNaN, DNumNaN) => true
        case (DNumPosInf, DNumPosInf) => true
        case (DNumPosInf, DNumInf) => true
        case (DNumNegInf, DNumNegInf) => true
        case (DNumNegInf, DNumInf) => true
        case (DNumInf, DNumInf) => true
        case (DNumUIntConst(a), DNumUIntConst(b)) => a == b
        case (DNumUIntConst(_), DNumUInt) => true
        case (DNumNUIntConst(a), DNumNUIntConst(b)) => a == b
        case (DNumNUIntConst(_), DNumNUInt) => true
        case (DNumUInt, DNumUInt) => true
        case (DNumNUInt, DNumNUInt) => true
        case _ => false
      }

    def <(that: AbsNumber, absBool: AbsBoolUtil): AbsBool = {
      if (this <= DNumBot || that <= DNumBot) absBool.Bot
      else {
        (this.getSingle, that.getSingle) match {
          case (Some(v_1), Some(v_2)) => absBool.alpha(v_1 < v_2)
          case (None, Some(v_2)) => {
            this match {
              case DNumUInt => {
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
    def ===(that: AbsNumber, absBool: AbsBoolUtil): AbsBool = {
      if (this <= DNumBot || that <= DNumBot)
        absBool.Bot
      else if (this <= DNumNaN || that <= DNumNaN)
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
        case (DNumTop, _) => this
        case (_, DNumTop) => that
        case (DNumBot, _) => that
        case (_, DNumBot) => this
        case (DNumPosInf, DNumNegInf) => DNumInf
        case (DNumNegInf, DNumPosInf) => DNumInf
        case (DNumUIntConst(a), DNumUIntConst(b)) =>
          if (a == b) this
          else DNumUInt
        case (DNumNUIntConst(a), DNumNUIntConst(b)) =>
          if (a == b) this
          else DNumNUInt
        case _ =>
          (this <= that, that <= this) match {
            case (true, _) => that
            case (_, true) => this
            case _ => DNumTop
          }
      }

    /* meet */
    def <>(that: AbsNumber): AbsNumber = {
      (this <= that, that <= this) match {
        case (true, _) => this
        case (_, true) => that
        case _ => DNumBot
      }
    }

    override def toString: String =
      this match {
        case DNumTop => "Number"
        case DNumBot => "Bot"
        case DNumInf => "Inf"
        case DNumPosInf => "+Inf"
        case DNumNegInf => "-Inf"
        case DNumNaN => "NaN"
        case DNumUInt => "UInt"
        case DNumNUInt => "NUInt"
        case DNumUIntConst(n) => n.toLong.toString
        case DNumNUIntConst(n) => n.toString
      }

    override def isTop: Boolean = { this == DNumTop }

    override def isBottom: Boolean = { this == DNumBot }

    override def isConcrete: Boolean = {
      this match {
        case DNumInf
          | DNumPosInf
          | DNumNegInf
          | DNumNaN
          | DNumUIntConst(_)
          | DNumNUIntConst(_) => true
        case _ => false
      }
    }

    def toBoolean(absBool: AbsBoolUtil): AbsBool =
      this match {
        case DNumPosInf | DNumNegInf => absBool.True //TODO: Check DNumInf => absBool.True?
        case DNumNaN => absBool.False
        case DNumUIntConst(n) if n == 0 => absBool.False
        case DNumUIntConst(n) => absBool.True
        case DNumNUIntConst(n) if n == 0 => absBool.False
        case DNumNUIntConst(n) => absBool.True
        case _ => absBool.Bot //TODO: Check DNumUInt => absBool.Top?
      }

    override def toAbsString(absString: AbsStringUtil): AbsString = {
      this match {
        case DNumPosInf => absString.alpha("Infinity")
        case DNumNegInf => absString.alpha("-Infinity")
        case DNumNaN => absString.alpha("NaN")
        case DNumUIntConst(n) => absString.alpha(n.toInt.toString)
        case DNumNUIntConst(n) if n == n.toInt => absString.alpha(n.toInt.toString)
        case DNumNUIntConst(n) => absString.alpha(n.toString)
        case _ => absString.Bot
      }
    }

    def isNum(v: Double): Boolean =
      this match {
        case DNumUIntConst(n) if n == v => true
        case _ => false
      }

    def isNum: Boolean =
      this match {
        case DNumUIntConst(_)
          | DNumNUIntConst(_) => true
        case _ => false
      }

    def isFinite: Boolean =
      this match {
        case DNumUInt
          | DNumNUInt
          | DNumUIntConst(_)
          | DNumNUIntConst(_) => true
        case _ => false
      }

    def isFiniteMulti: Boolean =
      this match {
        case DNumUInt
          | DNumNUInt => true
        case _ => false
      }

    def isInfinity: Boolean = this == DNumInf
    def isPosInf: Boolean = this == DNumPosInf
    def isNegInf: Boolean = this == DNumNegInf

    def isInf: Boolean =
      this match {
        case DNumInf
          | DNumPosInf
          | DNumNegInf => true
        case _ => false
      }

    def isInfOrNaN: Boolean =
      this match {
        case DNumInf
          | DNumPosInf
          | DNumNegInf => true
        case DNumNaN => true
        case _ => false
      }

    def getUIntSingle: Option[Double] =
      this match {
        case DNumUIntConst(v) => Some(v)
        case _ => None
      }

    def isUIntSingle: Boolean =
      this match {
        case DNumUIntConst(_) => true
        case _ => false
      }

    def isNUIntSingle: Boolean =
      this match {
        case DNumNUIntConst(_) => true
        case _ => false
      }

    def isUIntAll: Boolean = this == DNumTop | this == DNumUInt

    def isUInt: Boolean = this == DNumUInt

    def isUIntOrBot: Boolean =
      this match {
        case DNumUInt
          | DNumUIntConst(_) => true
        case DNumBot => true
        case _ => false
      }

    def isNaN: Boolean = this == DNumNaN
  }
  case object DNumTop extends DNumber
  case object DNumBot extends DNumber
  case object DNumInf extends DNumber
  case object DNumPosInf extends DNumber
  case object DNumNegInf extends DNumber
  case object DNumNaN extends DNumber
  case object DNumUInt extends DNumber
  case object DNumNUInt extends DNumber
  case class DNumUIntConst(value: Double) extends DNumber
  case class DNumNUIntConst(value: Double) extends DNumber
}
