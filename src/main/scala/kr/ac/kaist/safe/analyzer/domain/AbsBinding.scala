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

////////////////////////////////////////////////////////////////////////////////
// concrete binding type
////////////////////////////////////////////////////////////////////////////////
abstract class Binding
// mutable binding
case class MBinding(value: Value) extends Binding
// immutable binding
case class IBinding(valueOpt: Option[Value]) extends Binding

////////////////////////////////////////////////////////////////////////////////
// binding abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsBinding extends AbsDomain[Binding, AbsBinding] {
  val value: AbsValue
  val uninit: AbsAbsent
  val mutable: AbsBool

  def copyWith(
    value: AbsValue = this.value,
    uninit: AbsAbsent = this.uninit,
    mutable: AbsBool = this.mutable
  ): AbsBinding
}

trait AbsBindingUtil extends AbsDomainUtil[Binding, AbsBinding] {
  def apply(
    value: AbsValue,
    uninit: AbsAbsent = AbsAbsent.Bot,
    mutable: AbsBool = AbsBool.True
  ): AbsBinding
}

////////////////////////////////////////////////////////////////////////////////
// default binding abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultBinding extends AbsBindingUtil {
  lazy val Bot: AbsDom = AbsDom(AbsValue.Bot, AbsAbsent.Bot, AbsBool.Bot)
  lazy val Top: AbsDom = AbsDom(AbsValue.Top, AbsAbsent.Top, AbsBool.Top)

  def alpha(bind: Binding): AbsBinding = bind match {
    case MBinding(value) => AbsDom(AbsValue(value), AbsAbsent.Bot, AbsBool.True)
    case IBinding(None) => AbsDom(AbsValue.Bot, AbsAbsent.Top, AbsBool.False)
    case IBinding(Some(value)) => AbsDom(AbsValue(value), AbsAbsent.Bot, AbsBool.False)
  }

  def apply(
    value: AbsValue,
    uninit: AbsAbsent,
    mutable: AbsBool
  ): AbsBinding = AbsDom(value, uninit, mutable)

  case class AbsDom(
      value: AbsValue,
      uninit: AbsAbsent,
      mutable: AbsBool
  ) extends AbsBinding {
    def gamma: ConSet[Binding] = value.gamma match {
      case ConInf() => ConInf()
      case ConFin(valSet) => {
        var bindSet: Set[Binding] = HashSet()
        if (AbsBool.True <= mutable) {
          bindSet ++= valSet.map(MBinding(_))
        }
        if (AbsBool.False <= mutable) {
          if (AbsAbsent.Top <= uninit) bindSet += IBinding(None)
          bindSet ++= valSet.map(v => IBinding(Some(v)))
        }
        ConFin(bindSet)
      }
    }

    def isBottom: Boolean = {
      value.isBottom &&
        uninit.isBottom &&
        mutable.isBottom
    }

    def getSingle: ConSingle[Binding] = {
      (value.getSingle, uninit.getSingle, mutable.getSingle) match {
        case (ConZero(), ConZero(), ConZero()) => ConZero()
        case (ConOne(value), ConZero(), ConOne(Bool(true))) => ConOne(MBinding(value))
        case (ConZero(), ConOne(Absent), ConOne(Bool(false))) => ConOne(IBinding(None))
        case (ConOne(value), ConZero(), ConOne(Bool(false))) => ConOne(IBinding(Some(value)))
        case _ => ConMany()
      }
    }

    def <=(that: AbsBinding): Boolean = {
      val right = check(that)
      this.value <= right.value &&
        this.uninit <= right.uninit &&
        this.mutable <= right.mutable
    }

    /* join */
    def +(that: AbsBinding): AbsBinding = {
      val right = check(that)
      AbsDom(
        this.value + right.value,
        this.uninit + right.uninit,
        this.mutable + right.mutable
      )
    }

    /* meet */
    def <>(that: AbsBinding): AbsBinding = {
      val right = check(that)
      AbsDom(
        this.value <> right.value,
        this.uninit <> right.uninit,
        this.mutable <> right.mutable
      )
    }

    override def toString: String = {
      s"[${mutable.toString.take(1)}]" + (
        if (isBottom) "⊥(binding)"
        else (value.isBottom, uninit.isBottom) match {
          case (true, true) => "⊥(binding)"
          case (false, true) => value.toString
          case (true, false) => "uninitialized"
          case (false, false) => value.toString + ", uninitialized"
        }
      )
    }

    def copyWith(
      value: AbsValue = this.value,
      uninit: AbsAbsent = this.uninit,
      mutable: AbsBool = this.mutable
    ): AbsBinding = AbsDom(value, uninit, mutable)
  }
}
