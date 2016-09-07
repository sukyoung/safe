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

// concrete primitive value type
sealed abstract class PValue
case object PVUndef extends PValue
case object PVNull extends PValue
case class PVBool(b: Boolean) extends PValue
case class PVNumber(n: Double) extends PValue
case class PVString(str: String) extends PValue
case object PValue {
  implicit def undef2PValue(undef: Undef): PValue = PVUndef
  implicit def null2PValue(x: Null): PValue = PVNull
  implicit def bool2PValue(b: Boolean): PValue = PVBool(b)
  implicit def num2PValue(n: Double): PValue = PVNumber(n)
  implicit def numset2PValue(set: Set[Double]): Set[PValue] = set.map(num2PValue)
  implicit def str2PValue(str: String): PValue = PVString(str)
  implicit def strset2PValue(set: Set[String]): Set[PValue] = set.map(str2PValue)
}

// primitive value abstract domain
trait AbsPValue extends AbsDomain[PValue, AbsPValue] {
  val undefval: AbsUndef
  val nullval: AbsNull
  val boolval: AbsBool
  val numval: AbsNumber
  val strval: AbsString

  def typeCount: Int
  def typeKinds: String
  def toStringSet: Set[AbsString]
  def foreach(f: (Primitive => Unit)): Unit
  def foldLeft[T](default: T)(f: ((T, Primitive) => T)): T
  def copyWith(prim: Primitive): AbsPValue
}

trait AbsPValueUtil extends AbsDomainUtil[PValue, AbsPValue] {
  def apply(prim: Primitive): AbsPValue
}

// default primitive value abstract domain
case class DefaultPValue(
    AbsUndef: AbsUndefUtil,
    AbsNull: AbsNullUtil,
    AbsBool: AbsBoolUtil,
    AbsNumber: AbsNumberUtil,
    AbsString: AbsStringUtil
) extends AbsPValueUtil {
  val Bot: AbsDom =
    AbsDom(AbsUndef.Bot, AbsNull.Bot, AbsBool.Bot, AbsNumber.Bot, AbsString.Bot)
  val Top: AbsDom =
    AbsDom(AbsUndef.Top, AbsNull.Top, AbsBool.Top, AbsNumber.Top, AbsString.Top)

  def apply(prim: Primitive): AbsPValue = Bot.copyWith(prim)

  def alpha(pvalue: PValue): AbsPValue = pvalue match {
    case PVUndef => Bot.copy(undefval = AbsUndef.Top)
    case PVNull => Bot.copy(nullval = AbsNull.Top)
    case PVBool(b) => Bot.copy(boolval = AbsBool.alpha(b))
    case PVNumber(n) => Bot.copy(numval = AbsNumber.alpha(n))
    case PVString(str) => Bot.copy(strval = AbsString.alpha(str))
  }

  case class AbsDom(
      undefval: AbsUndef,
      nullval: AbsNull,
      boolval: AbsBool,
      numval: AbsNumber,
      strval: AbsString
  ) extends AbsPValue {
    def gamma: ConSet[PValue] = ConSetTop() // TODO more precisely

    def isBottom: Boolean = this == Bot

    /* partial order */
    def <=(that: AbsPValue): Boolean = {
      val (left, right) = (this, check(that))
      if (left eq right) true
      else {
        (left.undefval <= right.undefval) &&
          (left.nullval <= right.nullval) &&
          (left.boolval <= right.boolval) &&
          (left.numval <= right.numval) &&
          (left.strval <= right.strval)
      }
    }

    /* join */
    def +(that: AbsPValue): AbsPValue = {
      val (left, right) = (this, check(that))
      if (left eq right) left
      else {
        AbsDom(
          left.undefval + right.undefval,
          left.nullval + right.nullval,
          left.boolval + right.boolval,
          left.numval + right.numval,
          left.strval + right.strval
        )
      }
    }

    /* meet */
    def <>(that: AbsPValue): AbsPValue = {
      val (left, right) = (this, check(that))
      AbsDom(
        left.undefval <> right.undefval,
        left.nullval <> right.nullval,
        left.boolval <> right.boolval,
        left.numval <> right.numval,
        left.strval <> right.strval
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

    def toStringSet: Set[AbsString] = {
      var set = HashSet[AbsString]()

      this.undefval.foldUnit(set += AbsString.alpha("undefined"))
      this.nullval.foldUnit(set += AbsString.alpha("null"))

      this.boolval.gamma match {
        case ConSingleBot() => ()
        case ConSingleCon(true) => set += AbsString.alpha("true")
        case ConSingleCon(false) => set += AbsString.alpha("false")
        case ConSingleTop() =>
          set += AbsString.alpha("true")
          set += AbsString.alpha("false")
      }

      set += this.numval.toAbsString

      this.strval.foldUnit(set += this.strval)

      // remove redundancies
      set.filter(s => !set.exists(o => s != o && s <= o))
    }

    private val pList: List[Primitive] = List(undefval, nullval, boolval, numval, strval)

    def foreach(f: (Primitive => Unit)): Unit = pList.foreach(f)

    def foldLeft[T](default: T)(f: ((T, Primitive) => T)): T = pList.foldLeft(default)(f)

    def copyWith(prim: Primitive): AbsPValue = prim match {
      case (undefval: AbsUndef) => copy(undefval = undefval)
      case (nullval: AbsNull) => copy(nullval = nullval)
      case (boolval: AbsBool) => copy(boolval = boolval)
      case (numval: AbsNumber) => copy(numval = numval)
      case (strval: AbsString) => copy(strval = strval)
    }
  }
}
