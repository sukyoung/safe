/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.domain.Utils._
import scala.collection.immutable.HashSet

object PValueUtil {
  val Bot: PValue =
    PValue(AbsUndef.Bot, AbsNull.Bot, AbsBool.Bot, AbsNumber.Bot, AbsString.Bot)
  val Top: PValue =
    PValue(AbsUndef.Top, AbsNull.Top, AbsBool.Top, AbsNumber.Top, AbsString.Top)

  // constructor
  def apply(undefval: AbsUndef): PValue = Bot.copy(undefval = undefval)
  def apply(nullval: AbsNull): PValue = Bot.copy(nullval = nullval)
  def apply(boolval: AbsBool): PValue = Bot.copy(boolval = boolval)
  def apply(numval: AbsNumber): PValue = Bot.copy(numval = numval)
  def apply(strval: AbsString): PValue = Bot.copy(strval = strval)

  // abstraction
  def alpha(): PValue = apply(AbsUndef.alpha(Undefined))
  def alpha(x: Null): PValue = apply(AbsNull.alpha(null))
  def alpha(str: String): PValue = apply(AbsString.alpha(str))
  def alpha(set: Set[String]): PValue = apply(AbsString.alpha(set))
  def alpha(d: Double): PValue = apply(AbsNumber.alpha(d))
  def alpha(l: Long): PValue = apply(AbsNumber.alpha(l))
  // trick for 'have same type after erasure' (Set[Double] & Set[String])
  def alpha(set: => Set[Double]): PValue = apply(AbsNumber.alpha(set))
  def alpha(b: Boolean): PValue = apply(AbsBool.alpha(b))
}

case class PValue(
    undefval: AbsUndef,
    nullval: AbsNull,
    boolval: AbsBool,
    numval: AbsNumber,
    strval: AbsString
) {

  /* partial order */
  def <=(that: PValue): Boolean = {
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
  def </(that: PValue): Boolean = {
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
  def +(that: PValue): PValue = {
    if (this eq that) this
    else {
      PValue(
        this.undefval + that.undefval,
        this.nullval + that.nullval,
        this.boolval + that.boolval,
        this.numval + that.numval,
        this.strval + that.strval
      )
    }
  }

  /* meet */
  def <>(that: PValue): PValue = {
    PValue(
      this.undefval <> that.undefval,
      this.nullval <> that.nullval,
      this.boolval <> that.boolval,
      this.numval <> that.numval,
      this.strval <> that.strval
    )
  }

  override def toString(): String = {
    var lst: List[String] = Nil

    this.undefval.fold(()) { lst ::= _.toString }
    this.nullval.fold(()) { lst ::= _.toString }
    this.boolval.fold(()) { lst ::= _.toString }
    this.numval.fold(()) { lst ::= _.toString }
    this.strval.fold(()) { lst ::= _.toString }

    lst match {
      case Nil => "⊥PValue"
      case _ => lst.mkString(", ")
    }
  }

  def typeCount: Int = {
    var count = 0;
    this.undefval.fold(()) { _ => count += 1 }
    this.nullval.fold(()) { _ => count += 1 }
    this.boolval.fold(()) { _ => count += 1 }
    this.numval.fold(()) { _ => count += 1 }
    this.strval.fold(()) { _ => count += 1 }
    count
  }

  def typeKinds: String = {
    var lst: List[String] = Nil
    this.undefval.fold(()) { _ => lst ::= "Undefined" }
    this.nullval.fold(()) { _ => lst ::= "Null" }
    this.boolval.fold(()) { _ => lst ::= "Boolean" }
    this.numval.fold(()) { _ => lst ::= "Number" }
    this.strval.fold(()) { _ => lst ::= "String" }
    lst.mkString(", ")
  }

  def toStringSet(absString: AbsStringUtil): Set[AbsString] = {
    var set = HashSet[AbsString]()

    this.undefval.foldUnit(set += absString.alpha("undefined"))
    this.nullval.foldUnit(set += absString.alpha("null"))

    this.boolval.gamma match {
      case ConSingleBot() => ()
      case ConSingleCon(true) => set += absString.alpha("true")
      case ConSingleCon(false) => set += absString.alpha("false")
      case ConSingleTop() =>
        set += absString.alpha("true")
        set += absString.alpha("false")
    }

    set += this.numval.toAbsString

    this.strval.foldUnit(set += this.strval)

    // remove redundancies
    set.filter(s => !set.exists(o => s != o && s <= o))
  }

  def foreach(f: (Primitive => Unit)): Unit = {
    f(undefval); f(nullval); f(boolval); f(numval); f(strval)
  }

  def foreach[T](f: (Primitive => T)): (T, T, T, T, T) = {
    (f(undefval), f(nullval), f(boolval), f(numval), f(strval))
  }

  def isBottom: Boolean =
    (undefval.gamma, nullval.gamma, boolval.gammaSimple, numval.gammaSimple, strval.gammaSimple) ==
      (ConSimpleBot(), ConSimpleBot(), ConSimpleBot(), ConSimpleBot(), ConSimpleBot())

  def copyWith(newStringVal: AbsString): PValue = PValue(undefval, nullval, boolval, numval, newStringVal)
}
