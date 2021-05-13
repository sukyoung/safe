/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._

import spray.json._
import kr.ac.kaist.safe.nodes.cfg.CFG

// default environment record abstract domain
object DefaultEnvRec extends EnvRecDomain {
  lazy val Bot: Elem = Elem(AbsDecEnvRec.Bot, AbsGlobalEnvRec.Bot)
  lazy val Top: Elem = Elem(AbsDecEnvRec.Top, AbsGlobalEnvRec.Top)

  def alpha(envRec: EnvRec): Elem = envRec match {
    case (envRec: DecEnvRec) => AbsDecEnvRec(envRec)
    case (envRec: GlobalEnvRec) => AbsGlobalEnvRec(envRec)
  }

  def apply(envRec: AbsDecEnvRec): Elem = Bot.copy(decEnvRec = envRec)
  def apply(global: AbsGlobalEnvRec): Elem = Bot.copy(globalEnvRec = global)
  def apply(envRec: AbsDecEnvRec, global: AbsGlobalEnvRec): Elem =
    Elem(envRec, global)

  case class Elem(
      decEnvRec: AbsDecEnvRec,
      globalEnvRec: AbsGlobalEnvRec
  ) extends ElemTrait {
    def gamma: ConSet[EnvRec] = ConInf // TODO more precise

    def getSingle: ConSingle[EnvRec] = ConMany // TODO more precise

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

    def toJSON(implicit uomap: UIdObjMap): JsValue = {
      (decEnvRec.toJSON, globalEnvRec.toJSON) match {
        case (dec: JsObject, JsNull) => dec
        case (JsNull, global: JsObject) => JsNull
        case _ => fail
      }
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

    def subsLoc(from: Loc, to: Loc): Elem =
      Elem(decEnvRec.subsLoc(from, to), globalEnvRec)

    def weakSubsLoc(from: Loc, to: Loc): Elem =
      Elem(decEnvRec.weakSubsLoc(from, to), globalEnvRec)

    def remove(locs: Set[Loc]): Elem =
      Elem(decEnvRec.remove(locs), globalEnvRec)
  }

  def fromJSON(json: JsValue, cfg: CFG)(implicit uomap: UIdObjMap): Elem = uomap.symbolCheck(json, {
    val fields = json.asJsObject().fields
    Elem(AbsDecEnvRec.fromJSON(fields("decEnvRec"), cfg), AbsGlobalEnvRec.fromJSON(fields("globalEnvRec")))
  })
}
