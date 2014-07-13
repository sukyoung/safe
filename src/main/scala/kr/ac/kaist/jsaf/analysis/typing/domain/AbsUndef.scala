/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

object AbsUndef {
  sealed abstract class AbsUndefCase
  case object UndefTopCase extends AbsUndefCase
  case object UndefBotCase extends AbsUndefCase

  val UndefTop: AbsUndef = new AbsUndef(UndefTopCase)
  val UndefBot: AbsUndef = new AbsUndef(UndefBotCase)

  def alpha: AbsUndef = UndefTop
}

class AbsUndef(_kind: AbsUndef.AbsUndefCase) extends AbsDomain {
  val kind: AbsUndef.AbsUndefCase = _kind

  override def getAbsCase: AbsCase =
    this.kind match {
      case AbsUndef.UndefBotCase => AbsBot
      case AbsUndef.UndefTopCase => AbsTop
    }

  /* partial order */
  def <= (that: AbsUndef) = {
    (this == UndefBot) || (that == UndefTop)
  }

  /* not a partial order */
  def </ (that: AbsUndef) = {
    (this == UndefTop) && (that == UndefBot)
  }

  /* join */
  def + (that: AbsUndef) = {
    if (this == UndefTop || that == UndefTop) UndefTop
    else UndefBot
  }

  /* meet */
  def <> (that: AbsUndef) = {
    if (this == UndefTop && that == UndefTop) UndefTop
    else UndefBot
  }
  
  /* abstract operator 'equal to' */
  def === (that: AbsUndef): AbsBool = {
    (this.kind, that.kind) match {
      case (AbsUndef.UndefBotCase, _) => BoolBot
      case (_, AbsUndef.UndefBotCase) => BoolBot
      case _ => BoolTrue
    }
  }
  
  override def toString: String = {
    this.kind match {
      case AbsUndef.UndefTopCase => "undefined"
      case AbsUndef.UndefBotCase => "Bot"
    }
  }

  override def isTop: Boolean = {this == UndefTop}

  override def isBottom: Boolean = {this == UndefBot}

  override def isConcrete: Boolean = {
    this == UndefTop
  }

  override def toAbsString: AbsString = {
    if(isConcrete) AbsString.alpha("undefined") else StrBot
  }
}
