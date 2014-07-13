/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

sealed abstract class Absent {
  /* partial order */
  def <= (that : Absent): Boolean = {
    (this == AbsentBot) || (that == AbsentTop)
  }
  
  /* not a partial order */
  def </ (that : Absent): Boolean = { 
    (this == AbsentTop) && (that == AbsentBot)
  }
  
  /* join */
  def + (that: Absent): Absent = {
    if (this == AbsentTop || that == AbsentTop) AbsentTop
    else AbsentBot
  }
  
  /* meet */
  def <> (that: Absent ) = {
    if (this == AbsentTop && that == AbsentTop) AbsentTop
    else AbsentBot
  }

  override def toString: String = {
    this match {
      case AbsentTop => "absent"
      case AbsentBot => "Bot"
    }
  }
  
  def isBot = this match {
    case AbsentBot => true
    case _ => false
  }
}

case object AbsentTop extends Absent
case object AbsentBot extends Absent
