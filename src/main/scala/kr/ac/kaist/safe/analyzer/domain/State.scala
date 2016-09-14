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
import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.Address
import scala.collection.immutable.{ HashMap }

case class State(heap: Heap, context: AbsContext) {
  /* partial order */
  def <=(that: State): Boolean =
    this.heap <= that.heap && this.context <= that.context

  /* not a partial order */
  def </(that: State): Boolean =
    !(this.heap <= that.heap) || !(this.context <= that.context)

  /* join */
  def +(that: State): State =
    State(this.heap + that.heap, this.context + that.context)

  /* meet */
  def <>(that: State): State =
    State(this.heap <> that.heap, this.context <> that.context)

  def isBottom: Boolean = heap.isBottom && context.isBottom

  def raiseException(excSet: Set[Exception]): State = {
    if (excSet.isEmpty) State.Bot
    else {
      val localEnv = context.pureLocal
      val (oldValue, _) = localEnv.normEnv.record.decEnvRec.GetBindingValue("@exception_all")
      val newExcSet = excSet.foldLeft(AbsLoc.Bot)((locSet, exc) => locSet + exc.getLoc)
      val excValue = AbsValue(newExcSet)
      val (localEnv2, _) = localEnv.normEnv.record.decEnvRec.SetMutableBinding("@exception", excValue)
      val (localEnv3, _) = localEnv2.SetMutableBinding("@exception_all", excValue + oldValue)
      val newCtx = context.subsPureLocal(AbsNormalEnv(localEnv3))
      State(heap, newCtx)
    }
  }

  def oldify(addr: Address): State = {
    State(this.heap.oldify(addr), this.context.oldify(addr))
  }

  ////////////////////////////////////////////////////////////////
  // Lookup
  ////////////////////////////////////////////////////////////////
  def lookup(id: CFGId): (AbsValue, Set[Exception]) = {
    val x = id.text
    val valueBot = AbsValue.Bot
    val localEnv = context.pureLocal
    id.kind match {
      case PureLocalVar =>
        val (v, _) = localEnv.normEnv.record.decEnvRec.GetBindingValue(x)
        (v, ExcSetEmpty)
      case CapturedVar =>
        val (envV, _) = localEnv.normEnv.record.decEnvRec.GetBindingValue("@env")
        val value = envV.locset.foldLeft(valueBot)((tmpVal, envLoc) => {
          tmpVal + context.lookupLocal(envLoc, x)
        })
        (value, ExcSetEmpty)
      case CapturedCatchVar =>
        val collapsedEnv = context.getOrElse(PredefLoc.COLLAPSED, AbsLexEnv.Bot)
        val (collapsedV, _) = collapsedEnv.normEnv.record.decEnvRec.GetBindingValue(x)
        (collapsedV, ExcSetEmpty)
      case GlobalVar => heap.lookupGlobal(x)
    }
  }

  def lookupBase(id: CFGId): AbsLoc = {
    val x = id.text
    id.kind match {
      case PureLocalVar => AbsLoc(PredefLoc.PURE_LOCAL)
      case CapturedVar =>
        val localEnv = context.pureLocal
        val (envV, _) = localEnv.normEnv.record.decEnvRec.GetBindingValue("@env")
        envV.locset.foldLeft(AbsLoc.Bot)((tmpLocSet, l) => {
          tmpLocSet + context.lookupBaseLocal(l, x)
        })
      case CapturedCatchVar => AbsLoc(PredefLoc.COLLAPSED)
      case GlobalVar => heap.lookupBaseGlobal(x)
    }
  }

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def varStore(id: CFGId, value: AbsValue): State = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val env = context.pureLocal.normEnv.record.decEnvRec
        val (newEnv, _) = env
          .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        State(heap, context.subsPureLocal(AbsNormalEnv(newEnv)))
      case CapturedVar =>
        val (envV, _) = context.pureLocal.normEnv.record.decEnvRec.GetBindingValue("@env")
        val newCtx = envV.locset.foldLeft(context)((tmpCtx, loc) => {
          tmpCtx + tmpCtx.varStoreLocal(loc, x, value)
        })
        State(heap, newCtx)
      case CapturedCatchVar =>
        val env = context.getOrElse(PredefLoc.COLLAPSED, AbsLexEnv.Bot).normEnv.record.decEnvRec
        val (newEnv, _) = env
          .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        State(heap, context.update(PredefLoc.COLLAPSED, AbsNormalEnv(newEnv)))
      case GlobalVar => {
        val h1 =
          if (AbsBool.True <= heap.canPutVar(x)) heap.varStoreGlobal(x, value)
          else Heap.Bot
        val h2 =
          if (AbsBool.False <= heap.canPutVar(x)) heap
          else Heap.Bot
        State(h1 + h2, context)
      }
    }
  }

  ////////////////////////////////////////////////////////////////
  // Update location
  ////////////////////////////////////////////////////////////////
  def createMutableBinding(id: CFGId, value: AbsValue): State = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val env = context.pureLocal.normEnv.record.decEnvRec
        val (newEnv, _) = env
          .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        State(heap, context.subsPureLocal(AbsNormalEnv(newEnv)))
      case CapturedVar =>
        val bind = AbsBinding(value)
        val (envV, _) = context.pureLocal.normEnv.record.decEnvRec.GetBindingValue("@env")
        val newCtx = envV.locset.foldLeft(AbsContext.Bot)((tmpCtx, loc) => {
          val env = context.getOrElse(loc, AbsLexEnv.Bot).normEnv.record.decEnvRec
          val (newEnv, _) = env
            .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, envV)
          tmpCtx + context.update(loc, AbsNormalEnv(newEnv))
        })
        State(heap, newCtx)
      case CapturedCatchVar =>
        val collapsedLoc = PredefLoc.COLLAPSED
        val env = context.getOrElse(collapsedLoc, AbsLexEnv.Bot).normEnv.record.decEnvRec
        val (newEnv, _) = env
          .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        State(heap, context.update(collapsedLoc, AbsNormalEnv(newEnv)))
      case GlobalVar =>
        val globalLoc = BuiltinGlobal.loc
        val objV = AbsDataProp(value, AbsBool.True, AbsBool.True, AbsBool.False)
        val propV = PropValue(objV)
        val newHeap =
          if (AbsBool.True == heap.hasProperty(globalLoc, AbsString(x))) heap
          else heap.update(globalLoc, heap.getOrElse(globalLoc, AbsObjectUtil.Bot).update(x, propV))
        State(newHeap, context)
    }
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, str: String): (State, AbsBool) = {
    val absStr = AbsString(str)
    val (newHeap, b1) = heap.delete(loc, absStr)
    val (newCtx, b2) = context.delete(loc, str)
    (State(newHeap, newCtx), b1 + b2)
  }
}

object State {
  val Bot: State = State(Heap.Bot, AbsContext.Bot)
}
