/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

abstract class AbsDomain {
  def getAbsCase: AbsCase

  /*
  def <= (that: ThisType): Boolean // partial order
  def </ (that: ThisType): Boolean // not a partial order
  def + (that: ThisType): ThisType // join
  def <> (that: ThisType): ThisType // meet
  def === (that: ThisType): AbsBool // abstract operator 'equal to'
  */

  def isTop: Boolean
  def isBottom: Boolean
  def isConcrete: Boolean
  def toAbsString: AbsString
  def getConcreteValueAsString(defaultString: String = ""): String = {
    if(isConcrete) toString else defaultString
  }
}

sealed abstract class AbsCase
case object AbsTop extends AbsCase
case object AbsBot extends AbsCase
case object AbsSingle extends AbsCase
case object AbsMulti extends AbsCase
