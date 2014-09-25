/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import kr.ac.kaist.jsaf.analysis.cfg.FunctionId

object PropValue {
  /* convenience constructors */
  def apply(v: ObjectValue): PropValue = PropValue(v, FunSetBot)
  def apply(v: Value): PropValue = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolBot), FunSetBot)
  def apply(v: AbsString): PropValue = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolBot), FunSetBot)
  def apply(v: AbsNumber): PropValue = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolBot), FunSetBot)
  def apply(v: AbsBool): PropValue = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolBot), FunSetBot)
}

case class PropValue(objval: ObjectValue,
                     funid: FunSet) {
  /* tuple-like accessor */
  val _1 = objval
  val _2 = objval.value
  val _3 = funid

  /* partial order */
  def <= (that: PropValue): Boolean = {
    if (this eq that) true
    else {
      this.objval <= that.objval &&
      this.funid.subsetOf(that.funid)
    }
  }

  /* not a partial order */
  def </ (that: PropValue): Boolean = {
    if (this eq that) false
    else {
      this.objval </ that.objval ||
      !this.funid.subsetOf(that.funid)
    }
  }

  /* join */
  def + (that: PropValue): PropValue = {
    if (this eq that) this
    else { 
      PropValue(
          this.objval + that.objval,
          this.funid ++ that.funid)
    }
  }

  /* meet */
  def <> (that: PropValue): PropValue = {
    PropValue(
        this.objval <> that.objval,
        this.funid.intersect(that.funid))
  }
}
