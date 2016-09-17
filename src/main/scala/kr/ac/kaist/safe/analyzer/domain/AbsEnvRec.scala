/**
 * *****************************************************************************
 * Copyright With(c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.LINE_SEP

/* 10.2.1 Environment Records */

////////////////////////////////////////////////////////////////////////////////
// concrete environment record type
////////////////////////////////////////////////////////////////////////////////
trait EnvRec

////////////////////////////////////////////////////////////////////////////////
// environment record abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsEnvRec extends AbsDomain[EnvRec, AbsEnvRec] {
  val decEnvRec: AbsDecEnvRec
  val globalEnvRec: AbsGlobalEnvRec

  // 10.2.1.2.1 HasBinding(N)
  def HasBinding(name: String)(heap: Heap): AbsBool

  // 10.2.1.2.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(
    name: String,
    del: Boolean
  )(heap: Heap): (AbsEnvRec, Heap)

  // 10.2.1.2.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: AbsValue,
    strict: Boolean
  )(heap: Heap): (AbsEnvRec, Heap, Set[Exception])

  // 10.2.1.2.4 GetBindingValue(N, S)
  def GetBindingValue(
    name: String,
    strict: Boolean
  )(heap: Heap): (AbsValue, Set[Exception])

  // 10.2.1.2.5 DeleteBinding(N)
  def DeleteBinding(
    name: String
  )(heap: Heap): (AbsEnvRec, Heap, AbsBool)

  // 10.2.1.2.6 ImplicitThisValue()
  def ImplicitThisValue(heap: Heap): AbsValue

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): AbsEnvRec

  // weak substitute locR by locO
  def weakSubsLoc(locR: Loc, locO: Loc): AbsEnvRec
}

trait AbsEnvRecUtil extends AbsDomainUtil[EnvRec, AbsEnvRec] {
  def apply(envRec: AbsDecEnvRec): AbsEnvRec
  def apply(envRec: AbsGlobalEnvRec): AbsEnvRec
}

////////////////////////////////////////////////////////////////////////////////
// default environment record abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultEnvRec extends AbsEnvRecUtil {
  lazy val Bot: Dom = Dom(AbsDecEnvRec.Bot, AbsGlobalEnvRec.Bot)
  lazy val Top: Dom = Dom(AbsDecEnvRec.Top, AbsGlobalEnvRec.Top)

  def alpha(envRec: EnvRec): AbsEnvRec = envRec match {
    case (envRec: DecEnvRec) => AbsDecEnvRec(envRec)
    case (envRec: GlobalEnvRec) => AbsGlobalEnvRec(envRec)
  }

  def apply(envRec: AbsDecEnvRec): AbsEnvRec = Bot.copy(decEnvRec = envRec)
  def apply(envRec: AbsGlobalEnvRec): AbsEnvRec = Bot.copy(globalEnvRec = envRec)

  case class Dom(
      decEnvRec: AbsDecEnvRec,
      globalEnvRec: AbsGlobalEnvRec
  ) extends AbsEnvRec {
    def gamma: ConSet[EnvRec] = ConInf() // TODO more precise

    def getSingle: ConSingle[EnvRec] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsEnvRec): Boolean = {
      val right = check(that)
      this.decEnvRec <= right.decEnvRec &&
        this.globalEnvRec <= right.globalEnvRec
    }

    def +(that: AbsEnvRec): AbsEnvRec = {
      val right = check(that)
      Dom(
        this.decEnvRec + right.decEnvRec,
        this.globalEnvRec + right.globalEnvRec
      )
    }

    def <>(that: AbsEnvRec): AbsEnvRec = {
      val right = check(that)
      Dom(
        this.decEnvRec <> right.decEnvRec,
        this.globalEnvRec <> right.globalEnvRec
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
    def HasBinding(name: String)(heap: Heap): AbsBool =
      decEnvRec.HasBinding(name) + globalEnvRec.HasBinding(name)(heap)

    // 10.2.1.2.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean
    )(heap: Heap): (AbsEnvRec, Heap) = {
      val newD = decEnvRec.CreateMutableBinding(name, del)
      val (newG, newH) = globalEnvRec.CreateMutableBinding(name, del)(heap)
      (Dom(newD, newG), newH)
    }

    // 10.2.1.2.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean
    )(heap: Heap): (AbsEnvRec, Heap, Set[Exception]) = {
      val (newD, excSet1) = decEnvRec.SetMutableBinding(name, v, strict)
      val (newG, newH, excSet2) = globalEnvRec.SetMutableBinding(name, v, strict)(heap)
      (Dom(newD, newG), newH, excSet1 ++ excSet2)
    }

    // 10.2.1.2.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean
    )(heap: Heap): (AbsValue, Set[Exception]) = {
      val (v1, excSet1) = decEnvRec.GetBindingValue(name, strict)
      val (v2, excSet2) = globalEnvRec.GetBindingValue(name, strict)(heap)
      (v1 + v2, excSet1 ++ excSet2)
    }

    // 10.2.1.2.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    )(heap: Heap): (AbsEnvRec, Heap, AbsBool) = {
      val (newD, b1) = decEnvRec.DeleteBinding(name)
      val (newG, newH, b2) = globalEnvRec.DeleteBinding(name)(heap)
      (Dom(newD, newG), newH, b1 + b2)
    }

    // 10.2.1.2.6 ImplicitThisValue()
    def ImplicitThisValue(heap: Heap): AbsValue =
      decEnvRec.ImplicitThisValue + globalEnvRec.ImplicitThisValue(heap)

    def subsLoc(locR: Loc, locO: Loc): AbsEnvRec =
      Dom(decEnvRec.subsLoc(locR, locO), globalEnvRec)

    def weakSubsLoc(locR: Loc, locO: Loc): AbsEnvRec =
      Dom(decEnvRec.weakSubsLoc(locR, locO), globalEnvRec)
  }
}
