/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import scala.collection.immutable.HashSet

object IValue {
  /* convenience constructors */
  def apply(v: Loc): IValue = IValue(PValueBot, LocSet(v))
  def apply(v: LocSet): IValue = IValue(PValueBot, v)
  def apply(v: PValue): IValue = IValue(v, LocSetBot)
  def apply(v: AbsUndef): IValue = IValue(PValue(v), LocSetBot)
  def apply(v: AbsNumber): IValue = IValue(PValue(v), LocSetBot)
  def apply(v: AbsBool): IValue = IValue(PValue(v), LocSetBot)
  def apply(v: AbsNull): IValue = IValue(PValue(v), LocSetBot)
  def apply(v: AbsString): IValue = IValue(PValue(v), LocSetBot)
}

case class IValue(pvalue: PValue, locset: LocSet) {
  /* tuple-like accessor */
  val _1 = pvalue
  val _2 = locset

  /* partial order */
  def <= (that : IValue): Boolean = {
    if (this eq that) true 
    else {
      this.pvalue <= that.pvalue &&
      this.locset.subsetOf(that.locset)
    }
  }

  /* not a partial order */
  def </ (that: IValue): Boolean = {
    if (this eq that) false 
    else {
      !(this.pvalue <= that.pvalue) ||
      !(this.locset.subsetOf(that.locset))
    }
  }

  /* join */
  def + (that: IValue): IValue = {
    if (this eq that) this
    else if (this eq IValueBot) that
    else if (that eq IValueBot) this
    else {
      IValue(
        this.pvalue + that.pvalue,
        this.locset ++ that.locset)
    }
  }

  /* meet */
  def <> (that: IValue): IValue = {
    if (this eq that) this 
    else {
      IValue(
        this.pvalue <> that.pvalue,
        this.locset.intersect(that.locset))
    }
  }

  /* substitute l_r by l_o */
  def subsLoc(l_r: Loc, l_o: Loc): IValue = {
    if (locset(l_r)) IValue(pvalue, (locset - l_r) + l_o)
    else this
  }
  
  /* weakly substitute l_r by l_o, that is keep l_r together */
  def weakSubsLoc(l_r: Loc, l_o: Loc): IValue = {
    if (locset(l_r)) IValue(pvalue, locset + l_o)
    else this
  }

  def typeCount = {
    if (locset.isEmpty)
      pvalue.typeCount
    else
      pvalue.typeCount + 1
  }

  def typeKinds: String = {
    val sb = new StringBuilder()
    sb.append(pvalue.typeKinds)
    if(!locset.isEmpty) sb.append((if(sb.length > 0) ", " else "") + "Object")
    sb.toString
  }
}
