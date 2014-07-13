/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

object AbsBool {
  sealed abstract class AbsBoolCase
  case object BoolTopCase extends AbsBoolCase
  case object BoolBotCase extends AbsBoolCase
  case object BoolTrueCase extends AbsBoolCase
  case object BoolFalseCase extends AbsBoolCase

  def alpha(bool: Boolean): AbsBool = {
    if (bool) BoolTrue else BoolFalse
  }

  val BoolTop: AbsBool = new AbsBool(BoolTopCase)
  val BoolBot: AbsBool = new AbsBool(BoolBotCase)
  val BoolTrue: AbsBool = new AbsBool(BoolTrueCase)
  val BoolFalse: AbsBool = new AbsBool(BoolFalseCase)

  val bot: AbsBool = BoolBot
  val top: AbsBool = BoolTop
}

class AbsBool(_kind: AbsBool.AbsBoolCase) extends AbsBase[Boolean] {
  val kind: AbsBool.AbsBoolCase = _kind

  override def getAbsCase: AbsCase =
    this.kind match {
      case AbsBool.BoolBotCase => AbsBot
      case AbsBool.BoolTopCase => AbsTop
      case AbsBool.BoolTrueCase => AbsSingle
      case AbsBool.BoolFalseCase => AbsSingle
    }

  override def getSingle: Option[Boolean] =
    this.kind match {
      case AbsBool.BoolTrueCase => Some(true)
      case AbsBool.BoolFalseCase => Some(false)
      case _ => None
    }

  override def gamma: Option[Set[Boolean]] = this.kind match {
    case AbsBool.BoolTrueCase => Some(Set(true))
    case AbsBool.BoolFalseCase => Some(Set(false))
    case _ => None
  }

  /* partial order */
  def <= (that: AbsBool) = {
    if (this == that) true
    else if (this == BoolBot) true
    else if (that == BoolTop) true
    else false
  }
  
  /* not a partial order */
  def </ (that: AbsBool) = {
    if (this == that) false
    else if (this == BoolBot) false
    else if (that == BoolTop) false
    else true
  }
  
  /* join */
  def + (that: AbsBool) = {
    if (this == that) this
    else if (this == BoolBot) that
    else if (that == BoolBot) this
    else BoolTop
  }

  /* meet */
  def <> (that: AbsBool) = {
    if (this == that) this
    else if (this == BoolTop) that
    else if (that == BoolTop) this
    else BoolBot
  }
  
  /* abstract operator 'equal to' */
  def === (that: AbsBool): AbsBool = {
    (this.kind, that.kind) match {
      case (AbsBool.BoolBotCase, _) => BoolBot
      case (_, AbsBool.BoolBotCase) => BoolBot
      case (AbsBool.BoolTopCase, _) => BoolTop
      case (_, AbsBool.BoolTopCase) => BoolTop
      case (AbsBool.BoolTrueCase, AbsBool.BoolTrueCase) => BoolTrue
      case (AbsBool.BoolFalseCase, AbsBool.BoolFalseCase) => BoolTrue
      case _ => BoolFalse
    }
  }

  def unary_!(): AbsBool = {
    this.kind match {
      case AbsBool.BoolTopCase => BoolTop
      case AbsBool.BoolBotCase => BoolBot
      case AbsBool.BoolTrueCase => BoolFalse
      case AbsBool.BoolFalseCase => BoolTrue
    }
  }

  override def toString: String = {
    this.kind match {
      case AbsBool.BoolTopCase => "Bool"
      case AbsBool.BoolBotCase => "Bot"
      case AbsBool.BoolTrueCase => "true"
      case AbsBool.BoolFalseCase => "false"
    }
  }

  override def isTop: Boolean = {this == BoolTop}

  override def isBottom: Boolean = {this == BoolBot}

  override def isConcrete: Boolean = {
    this == BoolTrue || this == BoolFalse
  }

  override def toAbsString: AbsString = {
    this.kind match {
      case AbsBool.BoolTrueCase => AbsString.alpha("true")
      case AbsBool.BoolFalseCase => AbsString.alpha("false")
      case _ => StrBot
    }
  }
}
