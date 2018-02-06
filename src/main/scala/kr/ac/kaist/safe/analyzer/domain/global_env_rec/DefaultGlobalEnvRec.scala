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

import kr.ac.kaist.safe.errors.error.AbsGlobalEnvRecParseError
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import scala.collection.immutable.HashMap
import spray.json._

// default global environment abstract domain
object DefaultGlobalEnvRec extends GlobalEnvRecDomain {
  case object Bot extends Elem
  case object Top extends Elem

  def alpha(g: GlobalEnvRec): Elem = Top

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case JsString("⊥") => Bot
    case _ => throw AbsGlobalEnvRecParseError(v)
  }

  abstract class Elem extends ElemTrait {
    def gamma: ConSet[GlobalEnvRec] = this match {
      case Bot => ConFin()
      case Top => ConFin(GlobalEnvRec)
    }

    def getSingle: ConSingle[GlobalEnvRec] = this match {
      case Bot => ConZero()
      case Top => ConOne(GlobalEnvRec)
    }

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Top, Bot) => false
      case _ => true
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    override def toString: String = this match {
      case Bot => "⊥(global environment record)"
      case Top => "Top(global environment record)"
    }

    // 10.2.1.2.1 HasBinding(N)
    def HasBinding(name: String)(heap: AbsHeap): AbsBool = {
      // 1. Let envRec be the object environment record for
      //    which the method was invoked.
      // 2. Let bindings be the binding object for envRec.
      val bindings = getGlobalObj(heap)
      // 3. Return the result of calling the [[HasProperty]] internal method
      //    of bindings, passing N as the property name.
      heap.get(GLOBAL_LOC).HasProperty(AbsStr(name), heap)
    }

    // 10.2.1.2.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean
    )(heap: AbsHeap): (Elem, AbsHeap, Set[Exception]) = this match {
      case Bot => (Bot, AbsHeap.Bot, ExcSetEmpty)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Assert: The result of calling the [[HasProperty]] internal method
        //    of bindings, passing N as the property name, is false.
        if (AbsBool.False ⊑ heap.get(GLOBAL_LOC).HasProperty(AbsStr(name), heap)) {
          // 4. If D is true then let configValue be true
          //    otherwise let configValue be false.
          val configValue = del
          // 5. Call the [[DefineOwnProperty]] internal method of bindings,
          //    passing N, Property Descriptor {[[Value]]:undefined,
          //    [[Writable]]: true, [[Enumerable]]: true , [[Configurable]]:
          //    configValue}, and true as arguments.
          val (newObj, _, excSet) = bindings.DefineOwnProperty(
            AbsStr(name),
            AbsDesc(Desc(Some(Undef), Some(true), Some(true), Some(configValue))),
            true,
            heap
          )
          (this, heap.update(BuiltinGlobal.loc, newObj), excSet)
        } else { (Bot, AbsHeap.Bot, ExcSetEmpty) }
    }

    // 10.2.1.2.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean
    )(heap: AbsHeap): (Elem, AbsHeap, Set[Exception]) = this match {
      case Bot => (Bot, AbsHeap.Bot, ExcSetEmpty)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Call the [[Put]] internal method of bindings with
        //    arguments N, V, and S.
        val (newObj, excSet) = bindings.Put(AbsStr(name), v, strict, heap)
        (
          this,
          heap.update(BuiltinGlobal.loc, newObj),
          excSet
        )
    }

    // 10.2.1.2.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean
    )(heap: AbsHeap): (AbsValue, Set[Exception]) = this match {
      case Bot => (AbsValue.Bot, ExcSetEmpty)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Let value be the result of calling the [[HasProperty]]
        //    internal method of bindings, passing N as the property name.
        val value = heap.get(GLOBAL_LOC).HasProperty(AbsStr(name), heap)
        var excSet = ExcSetEmpty
        val b = value
        val f: AbsValue =
          if (AF ⊑ b) {
            // 4. If value is false, then
            //    a. If S is false, return the value undefined,
            //       otherwise throw a ReferenceError exception.
            if (strict) { excSet += ReferenceError; AbsValue.Bot }
            else { AbsUndef.Top }
          } else AbsValue.Bot
        val t =
          if (AT ⊑ b) {
            // 5. Return the result of calling the [[Get]] internal method of
            //    bindings, passing N for the argument.
            heap.get(GLOBAL_LOC).Get(name, heap)
          } else AbsValue.Bot
        val retV = f ⊔ t
        (retV, excSet)
    }

    // 10.2.1.2.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    )(heap: AbsHeap): (Elem, AbsHeap, AbsBool) = this match {
      case Bot => (Bot, AbsHeap.Bot, AbsBool.Bot)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Return the result of calling the [[Delete]] internal method
        //    of bindings, passing N and false as arguments.
        val (retH, retB) = heap.delete(GLOBAL_LOC, AbsStr(name))
        (this, retH, retB)
    }

    // 10.2.1.2.6 ImplicitThisValue()
    def ImplicitThisValue(heap: AbsHeap): AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. If the provideThis flag of envRec is true,
        //    return the binding object for envRec.
        //    (XXX: we do not supprot 'with' statement -> provideThis flag
        //          always false)
        // 3. Otherwise, return undefined.
        AbsUndef.Top
    }

    private val GLOBAL_LOC: Loc = BuiltinGlobal.loc
    private def getGlobalObj(heap: AbsHeap): AbsObj =
      // TODO refactoring after defining getter of AbsHeap.
      heap.get(GLOBAL_LOC)

    def toJson: JsValue = this match {
      case Top => JsString("⊤")
      case Bot => JsString("⊥")
    }
  }
}
