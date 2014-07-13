/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

object PValue {
  /* convenience constructors */
  def apply(v: AbsUndef): PValue = PValue(v, NullBot, BoolBot, NumBot, StrBot)
  def apply(v: AbsNull): PValue = PValue(UndefBot, v, BoolBot,  NumBot, StrBot)
  def apply(v: AbsBool): PValue = PValue(UndefBot, NullBot, v, NumBot, StrBot)
  def apply(v: AbsNumber): PValue = PValue(UndefBot, NullBot, BoolBot, v, StrBot)
  def apply(v: AbsString): PValue = PValue(UndefBot, NullBot, BoolBot, NumBot, v)
}

case class PValue(undefval: AbsUndef,
                  nullval: AbsNull,
                  boolval: AbsBool,
                  numval: AbsNumber,
                  strval: AbsString) {
  /* tuple-like accessor */
  val _1 = undefval
  val _2 = nullval
  val _3 = boolval
  val _4 = numval
  val _5 = strval

  /* partial order */
  def <= (that : PValue): Boolean = {
    if (this eq that) true
    else {
      (this.undefval <= that.undefval) &&
      (this.nullval <= that.nullval) &&
      (this.boolval <= that.boolval) &&
      (this.numval <= that.numval) &&
      (this.strval <= that.strval)
    }
  }

  /* not a partial order */
  def </ (that : PValue): Boolean = {
    if (this eq that) false 
    else {
      !(this.undefval <= that.undefval) ||
      !(this.nullval <= that.nullval) ||
      !(this.boolval <= that.boolval) ||
      !(this.numval <= that.numval) ||
      !(this.strval <= that.strval)
    }
  }

  /* join */
  def + (that: PValue): PValue = {
    if (this eq that) this
    else {
      PValue(
          this.undefval + that.undefval,
          this.nullval + that.nullval,
          this.boolval + that.boolval,
          this.numval + that.numval,
          this.strval + that.strval)
    }
  }

  /* meet */
  def <> (that: PValue): PValue = {
    PValue(
        this.undefval <> that.undefval,
        this.nullval <> that.nullval,
        this.boolval <> that.boolval,
        this.numval <> that.numval,
        this.strval <> that.strval)
  }

  override def toString(): String = {
    if (this == PValueTop) {
      "PValue"
    }
    else {
      var first = true
      val sb = new StringBuilder()

      if (undefval != UndefBot) {
        sb.append(undefval.toString)
        first = false
      }
      if (nullval != NullBot) {
        if (!first) sb.append(", ");
        sb.append(nullval.toString);
        first = false;
      }
      if (boolval != BoolBot) {
        if (!first) sb.append(", ");
        sb.append(boolval.toString);
        first = false;
      }
      if (numval != NumBot) {
        if (!first) sb.append(", ");
        sb.append(numval.toString);
        first = false;
      }
      if (strval != StrBot) {
        if (!first) sb.append(", ");
        sb.append(strval.toString);
        first = false;
      }

      if (first) "Bot" else sb.toString()
    }
  }

  def typeCount = {
    var count = 0;
    if (undefval </ UndefBot)
      count = count + 1;
    if (nullval </ NullBot)
      count = count + 1;
    if (boolval </ BoolBot)
      count = count + 1;
    if (numval </ NumBot)
      count = count + 1;
    if (strval </ StrBot)
      count = count + 1;
    count
  }

  def typeKinds: String = {
    val sb = new StringBuilder()
    if(!undefval.isBottom) sb.append("Undefined")
    if(!nullval.isBottom) sb.append((if(sb.length > 0) ", " else "") + "Null")
    if(!boolval.isBottom) sb.append((if(sb.length > 0) ", " else "") + "Boolean")
    if(!numval.isBottom) sb.append((if(sb.length > 0) ", " else "") + "Number")
    if(!strval.isBottom) sb.append((if(sb.length > 0) ", " else "") + "String")
    sb.toString
  }

  def foreach(f: (AbsDomain => Unit)): Unit = {
    f(undefval); f(nullval); f(boolval); f(numval); f(strval)
  }
}
