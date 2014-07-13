/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

object AbsNull {
  sealed abstract class AbsNullCase
  case object NullTopCase extends AbsNullCase
  case object NullBotCase extends AbsNullCase

  val NullTop: AbsNull = new AbsNull(NullTopCase)
  val NullBot: AbsNull = new AbsNull(NullBotCase)

  def alpha: AbsNull = NullTop
}

class AbsNull(_kind: AbsNull.AbsNullCase) extends AbsDomain {
  val kind: AbsNull.AbsNullCase = _kind

  override def getAbsCase: AbsCase =
    this.kind match {
      case AbsNull.NullBotCase => AbsBot
      case AbsNull.NullTopCase => AbsTop
    }

  /* partial order */
  def <= (that: AbsNull) = {
    (this == NullBot) || (that == NullTop)    
  }

  /* not a partial order */
  def </ (that: AbsNull) = {
    (this == NullTop) && (that == NullBot)
  }

  /* join */
  def + (that: AbsNull) = {
    if (this == NullTop || that == NullTop) NullTop
    else NullBot
  }

  /* meet */
  def <> (that: AbsNull) = {
    if (this == NullTop && that == NullTop) NullTop
    else NullBot
  }

  /* abstract operator 'equal to' */
  def === (that: AbsNull): AbsBool = {
    (this.kind, that.kind) match {
      case (AbsNull.NullBotCase, _) => BoolBot
      case (_, AbsNull.NullBotCase) => BoolBot
      case _ => BoolTrue
    }
  }
  
  override def toString: String = {
    this.kind match {
      case AbsNull.NullTopCase => "null"
      case AbsNull.NullBotCase => "Bot"
    }
  }

  override def isTop: Boolean = {this == NullTop}

  override def isBottom: Boolean = {this == NullBot}

  override def isConcrete: Boolean = {
    this == NullTop
  }

  override def toAbsString: AbsString = {
    if(this == NullTop) AbsString.alpha("null") else StrBot
  }
}
