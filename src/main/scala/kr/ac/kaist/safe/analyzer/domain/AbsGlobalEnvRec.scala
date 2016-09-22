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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import scala.collection.immutable.HashMap

/* 10.2.1.2 Object Environment Records */
// XXX: we only support object environment record for global object
//      because other object environments with 'with' statments
//      are rewritten by using WithRewriter.

////////////////////////////////////////////////////////////////////////////////
// concrete global environment record type
////////////////////////////////////////////////////////////////////////////////
abstract class GlobalEnvRec extends EnvRec
case object GlobalEnvRec extends GlobalEnvRec

////////////////////////////////////////////////////////////////////////////////
// global environment abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsGlobalEnvRec extends AbsDomain[GlobalEnvRec, AbsGlobalEnvRec] {
  // 10.2.1.2.1 HasBinding(N)
  def HasBinding(name: String)(heap: Heap): AbsBool

  // 10.2.1.2.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(
    name: String,
    del: Boolean
  )(heap: Heap): (AbsGlobalEnvRec, Heap)

  // 10.2.1.2.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: AbsValue,
    strict: Boolean
  )(heap: Heap): (AbsGlobalEnvRec, Heap, Set[Exception])

  // 10.2.1.2.4 GetBindingValue(N, S)
  def GetBindingValue(
    name: String,
    strict: Boolean
  )(heap: Heap): (AbsValue, Set[Exception])

  // 10.2.1.2.5 DeleteBinding(N)
  def DeleteBinding(
    name: String
  )(heap: Heap): (AbsGlobalEnvRec, Heap, AbsBool)

  // 10.2.1.2.6 ImplicitThisValue()
  def ImplicitThisValue(heap: Heap): AbsValue
}

trait AbsGlobalEnvRecUtil extends AbsDomainUtil[GlobalEnvRec, AbsGlobalEnvRec]

////////////////////////////////////////////////////////////////////////////////
// default global environment abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultGlobalEnvRec extends AbsGlobalEnvRecUtil {
  case object Bot extends Dom
  case object Top extends Dom

  def alpha(g: GlobalEnvRec): Dom = Top

  abstract class Dom extends AbsGlobalEnvRec {
    def gamma: ConSet[GlobalEnvRec] = this match {
      case Bot => ConFin()
      case Top => ConFin(GlobalEnvRec)
    }

    def getSingle: ConSingle[GlobalEnvRec] = this match {
      case Bot => ConZero()
      case Top => ConOne(GlobalEnvRec)
    }

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsGlobalEnvRec): Boolean = (this, check(that)) match {
      case (Top, Bot) => false
      case _ => true
    }

    def +(that: AbsGlobalEnvRec): Dom = (this, check(that)) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    def <>(that: AbsGlobalEnvRec): Dom = (this, check(that)) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    override def toString: String = this match {
      case Bot => "⊥(global environment record)"
      case Top => "Top(global environment record)"
    }

    // 10.2.1.2.1 HasBinding(N)
    def HasBinding(name: String)(heap: Heap): AbsBool = {
      // 1. Let envRec be the object environment record for
      //    which the method was invoked.
      // 2. Let bindings be the binding object for envRec.
      val bindings = getGlobalObj(heap)
      // 3. Return the result of calling the [[HasProperty]] internal method
      //    of bindings, passing N as the property name.
      // TODO refactoring after defining [[HasProperty]] for Obj.
      heap.get(GLOBAL_LOC).HasProperty(AbsString(name), heap)
    }

    // 10.2.1.2.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean
    )(heap: Heap): (AbsGlobalEnvRec, Heap) = this match {
      case Bot => (Bot, Heap.Bot)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Assert: The result of calling the [[HasProperty]] internal method
        //    of bindings, passing N as the property name, is false.
        if (AbsBool.False <= heap.get(GLOBAL_LOC).HasProperty(AbsString(name), heap)) {
          // 4. If D is true then let configValue be true
          //    otherwise let configValue be false.
          val configValue = AbsBool(del)
          // 5. Call the [[DefineOwnProperty]] internal method of bindings,
          //    passing N, Property Descriptor {[[Value]]:undefined,
          //    [[Writable]]: true, [[Enumerable]]: true , [[Configurable]]:
          //    configValue}, and true as arguments.
          // TODO refactoring after defining [[DefineOwnProperty]] for Obj.
          (this, heap.propStore(BuiltinGlobal.loc, AbsString(name), AbsUndef.Top))
        } else { (Bot, Heap.Bot) }
    }

    // 10.2.1.2.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean
    )(heap: Heap): (AbsGlobalEnvRec, Heap, Set[Exception]) = this match {
      case Bot => (Bot, Heap.Bot, ExcSetEmpty)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Call the [[Put]] internal method of bindings with
        //    arguments N, V, and S.
        // TODO refactoring after defining [[Put]] for Obj.
        (this, heap.propStore(BuiltinGlobal.loc, AbsString(name), v), ExcSetEmpty)
    }

    // 10.2.1.2.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean
    )(heap: Heap): (AbsValue, Set[Exception]) = this match {
      case Bot => (AbsValue.Bot, ExcSetEmpty)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Let value be the result of calling the [[HasProperty]]
        //    internal method of bindings, passing N as the property name.
        val value = heap.get(GLOBAL_LOC).HasProperty(AbsString(name), heap)
        var excSet = ExcSetEmpty
        val retV = value.map[AbsValue](
          // 4. If value is false, then
          //    a. If S is false, return the value undefined,
          //       otherwise throw a ReferenceError exception.
          elseV =
          if (strict) { excSet += ReferenceError; AbsValue.Bot }
          else { AbsUndef.Top },
          // 5. Return the result of calling the [[Get]] internal method of
          //    bindings, passing N for the argument.
          // TODO refactoring after defining [[Get]] for Obj.
          thenV = heap.get(GLOBAL_LOC).Get(name, heap)
        )(AbsValue)
        (retV, excSet)
    }

    // 10.2.1.2.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    )(heap: Heap): (AbsGlobalEnvRec, Heap, AbsBool) = this match {
      case Bot => (Bot, Heap.Bot, AbsBool.Bot)
      case Top =>
        // 1. Let envRec be the object environment record for
        //    which the method was invoked.
        // 2. Let bindings be the binding object for envRec.
        val bindings = getGlobalObj(heap)
        // 3. Return the result of calling the [[Delete]] internal method
        //    of bindings, passing N and false as arguments.
        val (retH, retB) = heap.delete(GLOBAL_LOC, AbsString(name))
        (this, retH, retB)
    }

    // 10.2.1.2.6 ImplicitThisValue()
    def ImplicitThisValue(heap: Heap): AbsValue = this match {
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
    private def getGlobalObj(heap: Heap): AbsObject =
      // TODO refactoring after defining getter of Heap.
      heap.get(GLOBAL_LOC)
  }
}
