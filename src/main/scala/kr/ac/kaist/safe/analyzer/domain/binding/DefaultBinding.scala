/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.errors.error.AbsBindingParseError
import scala.collection.immutable.HashSet
import spray.json._

// default binding abstract domain
object DefaultBinding extends BindingDomain {
  lazy val Bot: Elem = Elem(AbsValue.Bot, AbsAbsent.Bot, AbsBool.Bot)
  lazy val Top: Elem = Elem(AbsValue.Top, AbsAbsent.Top, AbsBool.Top)

  def alpha(bind: Binding): Elem = bind match {
    case MBinding(value) => Elem(AbsValue(value), AbsAbsent.Bot, AbsBool.True)
    case IBinding(None) => Elem(AbsValue.Bot, AbsAbsent.Top, AbsBool.False)
    case IBinding(Some(value)) => Elem(AbsValue(value), AbsAbsent.Bot, AbsBool.False)
  }

  def apply(
    value: AbsValue,
    uninit: AbsAbsent,
    mutable: AbsBool
  ): Elem = Elem(value, uninit, mutable)

  def fromJson(v: JsValue): Elem = v match {
    case JsObject(m) => (
      m.get("value").map(AbsValue.fromJson(_)),
      m.get("uninit").map(AbsAbsent.fromJson(_)),
      m.get("mutable").map(AbsBool.fromJson(_))
    ) match {
        case (Some(v), Some(u), Some(m)) => Elem(v, u, m)
        case _ => throw AbsBindingParseError(v)
      }
    case _ => throw AbsBindingParseError(v)
  }

  case class Elem(
      value: AbsValue,
      uninit: AbsAbsent,
      mutable: AbsBool
  ) extends ElemTrait {
    def gamma: ConSet[Binding] = value.gamma match {
      case ConInf => ConInf
      case ConFin(valSet) => {
        var bindSet: Set[Binding] = HashSet()
        if (AbsBool.True ⊑ mutable) {
          bindSet ++= valSet.map(MBinding(_))
        }
        if (AbsBool.False ⊑ mutable) {
          if (AbsAbsent.Top ⊑ uninit) bindSet += IBinding(None)
          bindSet ++= valSet.map(v => IBinding(Some(v)))
        }
        ConFin(bindSet)
      }
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

    def ⊑(that: Elem): Boolean = {
      val right = that
      this.value ⊑ right.value &&
        this.uninit ⊑ right.uninit &&
        this.mutable ⊑ right.mutable
    }

    /* join */
    def ⊔(that: Elem): Elem = {
      val right = that
      Elem(
        this.value ⊔ right.value,
        this.uninit ⊔ right.uninit,
        this.mutable ⊔ right.mutable
      )
    }

    /* meet */
    def ⊓(that: Elem): Elem = {
      val right = that
      Elem(
        this.value ⊓ right.value,
        this.uninit ⊓ right.uninit,
        this.mutable ⊓ right.mutable
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

    def copy(
      value: AbsValue = this.value,
      uninit: AbsAbsent = this.uninit,
      mutable: AbsBool = this.mutable
    ): Elem = Elem(value, uninit, mutable)

    def toJson: JsValue = JsObject(
      ("value", value.toJson),
      ("uninit", uninit.toJson),
      ("mutable", mutable.toJson)
    )
  }
}
