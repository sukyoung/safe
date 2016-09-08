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

case class State(heap: Heap, context: ExecContext) {
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
      val oldValue = localEnv.getOrElse("@exception_all")(AbsValue.Bot) { _.value }
      val newExcSet = excSet.foldLeft(AbsLoc.Bot)((locSet, exc) => locSet + exc.getLoc)
      val excValue = AbsValue(newExcSet)
      val newExcBind = BindingUtil(excValue)
      val newExcSetBind = BindingUtil(excValue + oldValue)
      val newCtx = context.subsPureLocal(localEnv
        .update("@exception", newExcBind)
        .update("@exception_all", newExcSetBind))
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
      case PureLocalVar => (localEnv.getOrElse(x)(valueBot) { _.value }, ExceptionSetEmpty)
      case CapturedVar =>
        val envLocSet = localEnv.getOrElse("@env")(AbsLoc.Bot) { _.value.locset }
        val value = envLocSet.foldLeft(valueBot)((tmpVal, envLoc) => {
          tmpVal + context.lookupLocal(envLoc, x)
        })
        (value, ExceptionSetEmpty)
      case CapturedCatchVar =>
        val collapsedEnv = context.getOrElse(PredefLoc.COLLAPSED, DecEnvRecord.Bot)
        (collapsedEnv.getOrElse(x)(valueBot) { _.value }, ExceptionSetEmpty)
      case GlobalVar => heap.lookupGlobal(x)
    }
  }

  def lookupBase(id: CFGId): AbsLoc = {
    val x = id.text
    id.kind match {
      case PureLocalVar => AbsLoc.alpha(PredefLoc.PURE_LOCAL)
      case CapturedVar =>
        val localEnv = context.pureLocal
        val envLocSet = localEnv.getOrElse("@env")(AbsLoc.Bot) { _.value.locset }
        envLocSet.foldLeft(AbsLoc.Bot)((tmpLocSet, l) => {
          tmpLocSet + context.lookupBaseLocal(l, x)
        })
      case CapturedCatchVar => AbsLoc.alpha(PredefLoc.COLLAPSED)
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
        val bind = BindingUtil(value)
        val env = context.pureLocal
        State(heap, context.subsPureLocal(env.update(x, bind)))
      case CapturedVar =>
        val newCtx = context.pureLocal.getOrElse("@env")(ExecContext.Bot) { bind =>
          bind.value.locset.foldLeft(context)((tmpCtx, loc) => {
            tmpCtx + tmpCtx.varStoreLocal(loc, x, value)
          })
        }
        State(heap, newCtx)
      case CapturedCatchVar =>
        val bind = BindingUtil(value)
        val env = context.getOrElse(PredefLoc.COLLAPSED, DecEnvRecord.Bot)
        State(heap, context.update(PredefLoc.COLLAPSED, env.update(x, bind)))
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
        val bind = BindingUtil(value)
        State(heap, context.subsPureLocal(context.pureLocal.update(x, bind)))
      case CapturedVar =>
        val bind = BindingUtil(value)
        val localEnv = context.pureLocal
        val newCtx = localEnv.getOrElse("@env")(ExecContext.Bot) { bind =>
          bind.value.locset.foldLeft(ExecContext.Bot)((tmpCtx, loc) => {
            tmpCtx + context.update(loc, context.getOrElse(loc, DecEnvRecord.Bot).update(x, bind))
          })
        }
        State(heap, newCtx)
      case CapturedCatchVar =>
        val collapsedLoc = PredefLoc.COLLAPSED
        val bind = BindingUtil(value)
        State(heap, context.update(collapsedLoc, context.getOrElse(collapsedLoc, DecEnvRecord.Bot).update(x, bind)))
      case GlobalVar =>
        val globalLoc = BuiltinGlobal.loc
        val objV = DataPropertyUtil(value)(AbsBool.True, AbsBool.True, AbsBool.False)
        val propV = PropValue(objV)
        val newHeap =
          if (AbsBool.True == heap.hasProperty(globalLoc, AbsString.alpha(x))) heap
          else heap.update(globalLoc, heap.getOrElse(globalLoc, AbsObjectUtil.Bot).update(x, propV))
        State(newHeap, context)
    }
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, str: String): (State, AbsBool) = {
    val absStr = AbsString.alpha(str)
    val (newHeap, b1) = heap.delete(loc, absStr)
    val (newCtx, b2) = context.delete(loc, str)
    (State(newHeap, newCtx), b1 + b2)
  }
}

object State {
  val Bot: State = State(Heap.Bot, ExecContext.Bot)
}
