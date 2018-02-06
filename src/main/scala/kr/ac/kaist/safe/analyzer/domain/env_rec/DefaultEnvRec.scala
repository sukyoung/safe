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

import kr.ac.kaist.safe.errors.error.AbsEnvRecParseError
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._
import spray.json._

// default environment record abstract domain
object DefaultEnvRec extends EnvRecDomain {
  lazy val Bot: Elem = Elem(AbsDecEnvRec.Bot, AbsGlobalEnvRec.Bot)
  lazy val Top: Elem = Elem(AbsDecEnvRec.Top, AbsGlobalEnvRec.Top)

  def alpha(envRec: EnvRec): Elem = envRec match {
    case (envRec: DecEnvRec) => AbsDecEnvRec(envRec)
    case (envRec: GlobalEnvRec) => AbsGlobalEnvRec(envRec)
  }

  def apply(envRec: AbsDecEnvRec): Elem = Bot.copy(decEnvRec = envRec)
  def apply(envRec: AbsGlobalEnvRec): Elem = Bot.copy(globalEnvRec = envRec)

  def fromJson(v: JsValue): Elem = v match {
    case JsObject(m) => (
      m.get("decEnvRec").map(AbsDecEnvRec.fromJson _),
      m.get("globalEnvRec").map(AbsGlobalEnvRec.fromJson _)
    ) match {
        case (Some(d), Some(g)) => Elem(d, g)
        case _ => throw AbsEnvRecParseError(v)
      }
    case _ => throw AbsEnvRecParseError(v)
  }

  case class Elem(
      decEnvRec: AbsDecEnvRec,
      globalEnvRec: AbsGlobalEnvRec
  ) extends ElemTrait {
    def gamma: ConSet[EnvRec] = ConInf // TODO more precise

    def getSingle: ConSingle[EnvRec] = ConMany() // TODO more precise

    def ⊑(that: Elem): Boolean = {
      val right = that
      this.decEnvRec ⊑ right.decEnvRec &&
        this.globalEnvRec ⊑ right.globalEnvRec
    }

    def ⊔(that: Elem): Elem = {
      val right = that
      Elem(
        this.decEnvRec ⊔ right.decEnvRec,
        this.globalEnvRec ⊔ right.globalEnvRec
      )
    }

    def ⊓(that: Elem): Elem = {
      val right = that
      Elem(
        this.decEnvRec ⊓ right.decEnvRec,
        this.globalEnvRec ⊓ right.globalEnvRec
      )
    }

    override def toString: String = {
      var lst: List[String] = Nil
      if (!globalEnvRec.isBottom) lst ::= globalEnvRec.toString
      if (!decEnvRec.isBottom) lst ::= decEnvRec.toString
      if (decEnvRec.isBottom && globalEnvRec.isBottom) lst ::= "⊥(environment)"
      lst.mkString(LINE_SEP)
    }

    // 10.2.1.2.1 HasBinding(N)
    def HasBinding(name: String)(heap: AbsHeap): AbsBool =
      decEnvRec.HasBinding(name) ⊔ globalEnvRec.HasBinding(name)(heap)

    // 10.2.1.2.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean
    )(heap: AbsHeap): (Elem, AbsHeap, Set[Exception]) = {
      val newD = decEnvRec.CreateMutableBinding(name, del)
      val (newG, newH, excSet) = globalEnvRec.CreateMutableBinding(name, del)(heap)
      (Elem(newD, newG), newH, excSet)
    }

    // 10.2.1.2.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean
    )(heap: AbsHeap): (Elem, AbsHeap, Set[Exception]) = {
      val (newD, excSet1) = decEnvRec.SetMutableBinding(name, v, strict)
      val (newG, newH, excSet2) = globalEnvRec.SetMutableBinding(name, v, strict)(heap)
      (Elem(newD, newG), newH, excSet1 ++ excSet2)
    }

    // 10.2.1.2.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean
    )(heap: AbsHeap): (AbsValue, Set[Exception]) = {
      val (v1, excSet1) = decEnvRec.GetBindingValue(name, strict)
      val (v2, excSet2) = globalEnvRec.GetBindingValue(name, strict)(heap)
      (v1 ⊔ v2, excSet1 ++ excSet2)
    }

    // 10.2.1.2.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    )(heap: AbsHeap): (Elem, AbsHeap, AbsBool) = {
      val (newD, b1) = decEnvRec.DeleteBinding(name)
      val (newG, newH, b2) = globalEnvRec.DeleteBinding(name)(heap)
      (Elem(newD, newG), newH, b1 ⊔ b2)
    }

    // 10.2.1.2.6 ImplicitThisValue()
    def ImplicitThisValue(heap: AbsHeap): AbsValue =
      decEnvRec.ImplicitThisValue ⊔ globalEnvRec.ImplicitThisValue(heap)

    def subsLoc(locR: Recency, locO: Recency): Elem =
      Elem(decEnvRec.subsLoc(locR, locO), globalEnvRec)

    def weakSubsLoc(locR: Recency, locO: Recency): Elem =
      Elem(decEnvRec.weakSubsLoc(locR, locO), globalEnvRec)

    def toJson: JsValue = JsObject(
      ("decEnvRec", decEnvRec.toJson),
      ("globalEnvRec", globalEnvRec.toJson)
    )
  }
}
