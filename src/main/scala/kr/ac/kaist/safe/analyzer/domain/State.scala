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
import kr.ac.kaist.safe.util.{ Address, SystemAddr }
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
      val (oldValue, _) = context.pureLocal.record.decEnvRec.GetBindingValue("@exception_all")
      val (newSt: State, newExcSet: AbsLoc) = excSet.foldLeft((this, AbsLoc.Bot)) {
        case ((st, locSet), exc) => {
          val errModel = exc.getModel
          val errAddr = SystemAddr(errModel.name + "<instance>")
          val newSt = st.oldify(errAddr)
          val loc = Loc(errAddr, Recent)
          val (protoModel, _, _, _) = errModel.protoModel.get
          val newErrObj = AbsObjectUtil.newErrorObj(errModel.name, protoModel.loc)
          val retH = newSt.heap.update(loc, newErrObj)
          (State(retH, newSt.context), locSet + loc)
        }
      }
      val excValue = AbsValue(newExcSet)
      val localEnv = newSt.context.pureLocal
      val (envRec1, _) = localEnv.record.decEnvRec.SetMutableBinding("@exception", excValue)
      val (envRec2, _) = envRec1.SetMutableBinding("@exception_all", excValue + oldValue)
      val newCtx = newSt.context.subsPureLocal(localEnv.copyWith(record = envRec2))
      State(newSt.heap, newCtx)
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
    val localEnv = context.pureLocal
    id.kind match {
      case PureLocalVar =>
        localEnv.record.decEnvRec.GetBindingValue(x)
      case CapturedVar =>
        AbsLexEnv.getId(localEnv.outer, x, true)(this)
      case CapturedCatchVar =>
        val collapsedEnv = context.getOrElse(PredefLoc.COLLAPSED, AbsLexEnv.Bot)
        collapsedEnv.record.decEnvRec.GetBindingValue(x)
      case GlobalVar => AbsGlobalEnvRec.Top.GetBindingValue(x, true)(heap)
    }
  }

  def lookupBase(id: CFGId): AbsValue = {
    val x = id.text
    id.kind match {
      case PureLocalVar => AbsLoc(PredefLoc.PURE_LOCAL)
      case CapturedVar =>
        AbsLexEnv.getIdBase(context.pureLocal.outer, x, false)(this)
      case CapturedCatchVar => AbsLoc(PredefLoc.COLLAPSED)
      case GlobalVar => AbsLoc(BuiltinGlobal.loc)
    }
  }

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def varStore(id: CFGId, value: AbsValue): State = {
    val x = id.text
    val localEnv = context.pureLocal
    id.kind match {
      case PureLocalVar =>
        val envRec = localEnv.record.decEnvRec
        val (newEnvRec, _) = envRec
          .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        val newEnv = localEnv.copyWith(record = newEnvRec)
        State(heap, context.subsPureLocal(newEnv))
      case CapturedVar =>
        val (newSt, _) = AbsLexEnv.setId(localEnv.outer, x, value, false)(this)
        newSt
      case CapturedCatchVar =>
        val env = context.getOrElse(PredefLoc.COLLAPSED, AbsLexEnv.Bot).record.decEnvRec
        val (newEnv, _) = env
          .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        State(heap, context.update(PredefLoc.COLLAPSED, AbsLexEnv(newEnv)))
      case GlobalVar =>
        val (_, newH, _) = AbsGlobalEnvRec.Top
          .SetMutableBinding(x, value, false)(heap)
        State(newH, context)
    }
  }

  ////////////////////////////////////////////////////////////////
  // Update location
  ////////////////////////////////////////////////////////////////
  def createMutableBinding(id: CFGId, value: AbsValue): State = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val env = context.pureLocal
        val envRec = env.record.decEnvRec
        val (newEnv, _) = envRec
          .CreateMutableBinding(x).fold(envRec)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        State(heap, context.subsPureLocal(env.copyWith(record = newEnv)))
      case CapturedVar =>
        val bind = AbsBinding(value)
        val newCtx = context.pureLocal.outer.foldLeft(AbsContext.Bot)((tmpCtx, loc) => {
          val env = context.getOrElse(loc, AbsLexEnv.Bot).record.decEnvRec
          val (newEnv, _) = env
            .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
            .SetMutableBinding(x, value)
          tmpCtx + context.update(loc, AbsLexEnv(newEnv))
        })
        State(heap, newCtx)
      case CapturedCatchVar =>
        val collapsedLoc = PredefLoc.COLLAPSED
        val env = context.getOrElse(collapsedLoc, AbsLexEnv.Bot).record.decEnvRec
        val (newEnv, _) = env
          .CreateMutableBinding(x).fold(env)((e: AbsDecEnvRec) => e)
          .SetMutableBinding(x, value)
        State(heap, context.update(collapsedLoc, AbsLexEnv(newEnv)))
      case GlobalVar =>
        val globalLoc = BuiltinGlobal.loc
        val objV = AbsDataProp(value, AbsBool.True, AbsBool.True, AbsBool.False)
        val newHeap =
          if (AbsBool.True == heap.get(globalLoc).HasProperty(AbsString(x), heap)) heap
          else heap.update(globalLoc, heap.get(globalLoc).update(x, objV))
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
