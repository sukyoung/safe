/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import kr.ac.kaist.jsaf.analysis.cfg.FunctionId

object PropValue {
  /* convenience constructors */
  def apply(v: ObjectValue): PropValue = PropValue(v, ValueBot, FunSetBot)
  def apply(v: Value): PropValue = PropValue(ObjectValueBot, v, FunSetBot)
  def apply(v: AbsString): PropValue = PropValue(ObjectValueBot, Value(PValue(v), LocSetBot), FunSetBot)
  def apply(v: AbsNumber): PropValue = PropValue(ObjectValueBot, Value(PValue(v), LocSetBot), FunSetBot)
  def apply(v: AbsBool): PropValue = PropValue(ObjectValueBot, Value(PValue(v), LocSetBot), FunSetBot)
}

case class PropValue(objval: ObjectValue,
                     value: Value,
                     funid: FunSet) {
  /* tuple-like accessor */
  val _1 = objval
  val _2 = value
  val _3 = funid

  /* partial order */
  def <= (that: PropValue): Boolean = {
    if (this eq that) true
    else {
      this.objval <= that.objval &&
      this.value  <= that.value &&
      this.funid.subsetOf(that.funid)
    }
  }

  /* not a partial order */
  def </ (that: PropValue): Boolean = {
    if (this eq that) false
    else {
      this.objval </ that.objval ||
      this.value  </ that.value ||
      !this.funid.subsetOf(that.funid)
    }
  }

  /* join */
  def + (that: PropValue): PropValue = {
    if (this eq that) this
    else { 
      PropValue(
          this.objval + that.objval,
          this.value + that.value,
          this.funid ++ that.funid)
    }
  }

  /* meet */
  def <> (that: PropValue): PropValue = {
    PropValue(
        this.objval <> that.objval,
        this.value <> that.value,
        this.funid.intersect(that.funid))
  }
}
