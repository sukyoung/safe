/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

object AbsNumber {
  sealed abstract class AbsNumberCase
  case object NumTopCase extends AbsNumberCase
  case object NumBotCase extends AbsNumberCase
  case object InfinityCase extends AbsNumberCase
  case object PosInfCase extends AbsNumberCase
  case object NegInfCase extends AbsNumberCase
  case object NaNCase extends AbsNumberCase
  case object UIntCase extends AbsNumberCase
  case object NUIntCase extends AbsNumberCase
  case class UIntSingleCase(value : Double) extends AbsNumberCase {
    override def toString = value.toLong.toString
  }
  case class NUIntSingleCase(value : Double) extends AbsNumberCase {
    override def toString = value.toString
  }

  val NumTop: AbsNumber = new AbsNumber(NumTopCase)
  val NumBot: AbsNumber = new AbsNumber(NumBotCase)
  val Infinity: AbsNumber = new AbsNumber(InfinityCase)
  val PosInf: AbsNumber = new AbsNumber(PosInfCase)
  val NegInf: AbsNumber = new AbsNumber(NegInfCase)
  val NaN: AbsNumber = new AbsNumber(NaNCase)
  val UInt: AbsNumber = new AbsNumber(UIntCase)
  val NUInt: AbsNumber = new AbsNumber(NUIntCase)
  private def UIntSingle(value : Double): AbsNumber = new AbsNumber(UIntSingleCase(value))
  private def NUIntSingle(value : Double): AbsNumber = new AbsNumber(NUIntSingleCase(value))

  val naturalNumbers = UInt
  
  def alpha(num: Double): AbsNumber = {
    if (num.isNaN)
      NaN
    else if (num.isNegInfinity)
      NegInf
    else if (num.isPosInfinity)
      PosInf
    else {
      val intnum = num.toInt
        val diff:Double = num - intnum.toDouble
        if ((diff == 0) && num >= 0) UIntSingle(num)
        else NUIntSingle(num)
    }
  }

  def isNum(an: AbsNumber, v: Double): Boolean = an.kind match {
    case UIntSingleCase(n) if n == v => true
    case _ => false
  }

  def isNum(an: AbsNumber): Boolean = an.kind match {
    case UIntSingleCase(_) => true
    case NUIntSingleCase(_) => true
    case _ => false
  }

  def isFinite(an: AbsNumber): Boolean = an.kind match {
    case UIntCase => true
    case NUIntCase => true
    case UIntSingleCase(_) => true
    case NUIntSingleCase(_) => true
    case _ => false
  }

  def isFiniteMulti(an: AbsNumber): Boolean = an.kind match {
    case UIntCase => true
    case NUIntCase => true
    case _ => false
  }

  def isInfinity(an: AbsNumber): Boolean = an.kind match {
    case InfinityCase => true
    case _ => false
  }

  def isPosInf(an: AbsNumber): Boolean = an.kind match {
    case PosInfCase => true
    case _ => false
  }

  def isNegInf(an: AbsNumber): Boolean = an.kind match {
    case NegInfCase => true
    case _ => false
  }

  def isInf(an: AbsNumber): Boolean = an.kind match {
    case InfinityCase => true
    case PosInfCase => true
    case NegInfCase => true
    case _ => false
  }

  def isInfOrNaN(an: AbsNumber): Boolean = an.kind match {
    case InfinityCase => true
    case PosInfCase => true
    case NegInfCase => true
    case NaNCase => true
    case _ => false
  }

  def getUIntSingle(an: AbsNumber): Option[Double] = an.kind match {
    case UIntSingleCase(v) => Some(v)
    case _ => None
  }

  def isUIntSingle(an: AbsNumber): Boolean = an.kind match {
    case UIntSingleCase(_) => true
    case _ => false
  }

  def isNUIntSingle(an: AbsNumber): Boolean = an.kind match {
    case NUIntSingleCase(_) => true
    case _ => false
  }

  def isUIntAll(an: AbsNumber): Boolean = an.kind match {
    case NumTopCase | UIntCase => true
    case _ => false
  }

  def isUInt(an: AbsNumber): Boolean = an.kind match {
    case UIntCase => true
    case _ => false
  }

  def isUIntOrBot(an: AbsNumber): Boolean = an.kind match {
    case UIntCase => true
    case UIntSingleCase(_) => true
    case NumBotCase => true
    case _ => false
  }

  def isNaN(an: AbsNumber): Boolean = an.kind match {
    case NaNCase => true
    case _ => false
  }
}

class AbsNumber(_kind: AbsNumber.AbsNumberCase) extends AbsBase[Double] {
  val kind: AbsNumber.AbsNumberCase = _kind

  override def getAbsCase: AbsCase =
    this.kind match {
      case AbsNumber.NumTopCase => AbsTop
      case AbsNumber.NumBotCase => AbsBot
      case AbsNumber.InfinityCase => AbsMulti
      case AbsNumber.PosInfCase => AbsSingle
      case AbsNumber.NegInfCase => AbsSingle
      case AbsNumber.NaNCase => AbsSingle
      case AbsNumber.UIntCase => AbsMulti
      case AbsNumber.NUIntCase => AbsMulti
      case AbsNumber.UIntSingleCase(v) => AbsSingle
      case AbsNumber.NUIntSingleCase(v) => AbsSingle
    }

  override def getSingle: Option[Double] =
    this.kind match {
      case AbsNumber.PosInfCase => Some(Double.PositiveInfinity)
      case AbsNumber.NegInfCase => Some(Double.NegativeInfinity)
      case AbsNumber.NaNCase => Some(Double.NaN)
      case AbsNumber.UIntSingleCase(v) => Some(v)
      case AbsNumber.NUIntSingleCase(v) => Some(v)
      case _ => None
    }

  override def gamma: Option[Set[Double]] =
    this.kind match {
      case AbsNumber.InfinityCase => Some(Set(Double.PositiveInfinity, Double.NegativeInfinity))
      case AbsNumber.PosInfCase => Some(Set(Double.PositiveInfinity))
      case AbsNumber.NegInfCase => Some(Set(Double.NegativeInfinity))
      case AbsNumber.NaNCase => Some(Set(Double.NaN))
      case AbsNumber.UIntSingleCase(value) => Some(Set(value))
      case AbsNumber.NUIntSingleCase(value) => Some(Set(value))
      case _ => None
  }

  /* partial order */
  def <= (that : AbsNumber) = (this.kind, that.kind) match {
    case (AbsNumber.NumBotCase,_) => true
    case (_, AbsNumber.NumTopCase) => true
    case (AbsNumber.NaNCase, AbsNumber.NaNCase) => true
    case (AbsNumber.PosInfCase, AbsNumber.PosInfCase) => true
    case (AbsNumber.PosInfCase, AbsNumber.InfinityCase) => true
    case (AbsNumber.NegInfCase, AbsNumber.NegInfCase) => true
    case (AbsNumber.NegInfCase, AbsNumber.InfinityCase) => true
    case (AbsNumber.InfinityCase, AbsNumber.InfinityCase) => true
    case (AbsNumber.UIntSingleCase(a), AbsNumber.UIntSingleCase(b)) => a == b
    case (AbsNumber.UIntSingleCase(_), AbsNumber.UIntCase) => true
    case (AbsNumber.NUIntSingleCase(a), AbsNumber.NUIntSingleCase(b)) => a == b
    case (AbsNumber.NUIntSingleCase(_), AbsNumber.NUIntCase) => true
    case (AbsNumber.UIntCase, AbsNumber.UIntCase) => true
    case (AbsNumber.NUIntCase, AbsNumber.NUIntCase) => true
    case _ => false
  }
  
  /* not a partial order */
  def </ (that: AbsNumber) = !(this <= that)

  def < (that: AbsNumber) = {
    if (this <= NumBot || that <= NumBot) BoolBot
    else {
      (this.getSingle, that.getSingle) match {
        case (Some(v_1), Some(v_2)) => AbsBool.alpha(v_1 < v_2)
        case (None, Some(v_2)) => {
          this.kind match {
            case AbsNumber.UIntCase => {
              if (v_2 <= 0) BoolFalse
              else BoolTop
            }
            case _ => BoolTop
          }
        }
        case _ => BoolTop
      }
    }
  }

  /* abstract operator 'equal to' */
  def === (that: AbsNumber): AbsBool = {
    if (this <= NumBot || that <= NumBot)
      BoolBot
    else if (this <= NaN || that <= NaN)
      BoolFalse
    else {
      (this.getSingle, that.getSingle) match {
        case (Some(v_1), Some(v_2)) => AbsBool.alpha(v_1 == v_2)
        case _ =>
          (this <= that, that <= this) match {
            case (false, false) => BoolFalse
            case _ => BoolTop
          }
      }
    }
  }

  /* join */
  def + (that: AbsNumber) = (this.kind, that.kind) match {
    case (AbsNumber.NumTopCase, _) => this
    case (_, AbsNumber.NumTopCase) => that
    case (AbsNumber.NumBotCase, _) => that
    case (_, AbsNumber.NumBotCase) => this
    case (AbsNumber.PosInfCase, AbsNumber.NegInfCase) => Infinity
    case (AbsNumber.NegInfCase, AbsNumber.PosInfCase) => Infinity
    case (AbsNumber.UIntSingleCase(a), AbsNumber.UIntSingleCase(b)) =>
      if (a==b) this
      else UInt
    case (AbsNumber.NUIntSingleCase(a), AbsNumber.NUIntSingleCase(b)) =>
      if (a==b) this
      else NUInt
    case _ =>
      (this<=that, that<=this) match {
        case (true, _) => that
        case (_, true) => this
        case _ => NumTop
      }
  }

  /* meet */
  def <> (that: AbsNumber) = {
      (this<=that, that<=this) match {
        case (true, _) => this
        case (_, true) => that
        case _ => NumBot
      }
  }

  override def toString: String = {
    this.kind match {
      case AbsNumber.NumTopCase => "Number"
      case AbsNumber.NumBotCase => "Bot"
      case AbsNumber.InfinityCase => "Inf"
      case AbsNumber.PosInfCase => "+inf"
      case AbsNumber.NegInfCase => "-inf"
      case AbsNumber.NaNCase => "NaN"
      case AbsNumber.UIntCase => "UInt"
      case AbsNumber.NUIntCase => "NUInt"
      case AbsNumber.UIntSingleCase(n) => n.toLong.toString
      case AbsNumber.NUIntSingleCase(n) => n.toString
    }
  }

  override def isTop: Boolean = {this == NumTop}

  override def isBottom: Boolean = {this == NumBot}

  override def isConcrete: Boolean = {
    if(this == Infinity || this == PosInf || this == NegInf || this == NaN) return true
    this.kind match {
      case _: AbsNumber.UIntSingleCase => true
      case _: AbsNumber.NUIntSingleCase => true
      case _ => false
    }
  }

  def toBoolean: AbsBool =
    this.kind match {
      case AbsNumber.PosInfCase => BoolTrue
      case AbsNumber.NegInfCase => BoolTrue
      case AbsNumber.NaNCase => BoolFalse
      case AbsNumber.UIntSingleCase(n) => if (n == 0) BoolFalse else BoolTrue
      case AbsNumber.NUIntSingleCase(n) => if (n == 0) BoolFalse else BoolTrue
      case _ => BoolBot
    }

  override def toAbsString: AbsString = {
    this.kind match {
      case AbsNumber.PosInfCase => AbsString.alpha("Infinity")
      case AbsNumber.NegInfCase => AbsString.alpha("-Infinity")
      case AbsNumber.NaNCase => AbsString.alpha("NaN")
      case AbsNumber.UIntSingleCase(n) => AbsString.alpha(n.toInt.toString)
      case AbsNumber.NUIntSingleCase(n) =>
        if(n == n.toInt) AbsString.alpha(n.toInt.toString)
        else AbsString.alpha(n.toString)
      case _ => StrBot
    }
  }

  def isUInt: Boolean =
    this.kind match {
      case AbsNumber.UIntCase => true
      case _: AbsNumber.UIntSingleCase => true
      case _ => false
    }
}
