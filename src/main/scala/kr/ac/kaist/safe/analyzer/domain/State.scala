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

import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.{ Address, Loc, Old, Recent }
import scala.collection.immutable.{ HashMap, HashSet }

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

  def raiseException(excSet: Set[Exception])(utils: Utils): State = {
    if (excSet.isEmpty) State.Bot
    else {
      val localEnv = context.pureLocal
      val oldValue = localEnv.getOrElse("@exception_all")(utils.value.Bot) { _.objval.value }
      val newExcSet = excSet.foldLeft(LocSetEmpty)((locSet, exc) => locSet + exc.getLoc)
      val excValue = Value(utils.pvalue.Bot, newExcSet)
      val newExcObjV = utils.dataProp(excValue)
      val newExcSetObjV = utils.dataProp(excValue + oldValue)
      val newCtx = context.subsPureLocal(localEnv
        .update("@exception", PropValue(newExcObjV))
        .update("@exception_all", PropValue(newExcSetObjV)))
      State(heap, newCtx)
    }
  }

  def oldify(addr: Address)(utils: Utils): State = {
    State(this.heap.oldify(addr)(utils), this.context.oldify(addr)(utils))
  }

  ////////////////////////////////////////////////////////////////
  // Lookup
  ////////////////////////////////////////////////////////////////
  def lookup(id: CFGId)(utils: Utils): (Value, Set[Exception]) = {
    val x = id.text
    val valueBot = utils.value.Bot
    val localEnv = context.pureLocal
    id.kind match {
      case PureLocalVar => (localEnv.getOrElse(x)(valueBot) { _.objval.value }, ExceptionSetEmpty)
      case CapturedVar =>
        val envLocSet = localEnv.getOrElse("@env")(LocSetEmpty) { _.objval.value.locset }
        val value = envLocSet.foldLeft(valueBot)((tmpVal, envLoc) => {
          tmpVal + context.lookupLocal(envLoc, x)(utils)
        })
        (value, ExceptionSetEmpty)
      case CapturedCatchVar =>
        val collapsedEnv = context.getOrElse(PredefLoc.COLLAPSED, DecEnvRecord.Bot)
        (collapsedEnv.getOrElse(x)(valueBot) { _.objval.value }, ExceptionSetEmpty)
      case GlobalVar => heap.lookupGlobal(x)(utils)
    }
  }

  def lookupBase(id: CFGId)(utils: Utils): Set[Loc] = {
    val x = id.text
    id.kind match {
      case PureLocalVar => HashSet(PredefLoc.PURE_LOCAL)
      case CapturedVar =>
        val localEnv = context.pureLocal
        val envLocSet = localEnv.getOrElse("@env")(LocSetEmpty) { _.objval.value.locset }
        envLocSet.foldLeft(LocSetEmpty)((tmpLocSet, l) => {
          tmpLocSet ++ context.lookupBaseLocal(l, x)(utils)
        })
      case CapturedCatchVar => HashSet(PredefLoc.COLLAPSED)
      case GlobalVar => heap.lookupBaseGlobal(x)(utils)
    }
  }

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def varStore(id: CFGId, value: Value)(utils: Utils): State = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val pv = PropValue(utils.dataProp(value)(utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False))
        val env = context.pureLocal
        State(heap, context.subsPureLocal(env.update(x, pv)))
      case CapturedVar =>
        val newCtx = context.pureLocal.getOrElse("@env")(ExecContext.Bot) { propv =>
          propv.objval.value.locset.foldLeft(context)((tmpCtx, loc) => {
            tmpCtx + tmpCtx.varStoreLocal(loc, x, value)(utils)
          })
        }
        State(heap, newCtx)
      case CapturedCatchVar =>
        val propV = PropValue(utils.dataProp(value)(utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False))
        val env = context.getOrElse(PredefLoc.COLLAPSED, DecEnvRecord.Bot)
        State(heap, context.update(PredefLoc.COLLAPSED, env.update(x, propV)))
      case GlobalVar => {
        val h1 =
          if (utils.absBool.True <= heap.canPutVar(x)(utils)) heap.varStoreGlobal(x, value)(utils)
          else Heap.Bot
        val h2 =
          if (utils.absBool.False <= heap.canPutVar(x)(utils)) heap
          else Heap.Bot
        State(h1 + h2, context)
      }
    }
  }

  ////////////////////////////////////////////////////////////////
  // Update location
  ////////////////////////////////////////////////////////////////
  def createMutableBinding(id: CFGId, value: Value)(utils: Utils): State = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val objV = utils.dataProp(value)(utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        State(heap, context.subsPureLocal(context.pureLocal.update(x, propV)))
      case CapturedVar =>
        val objV = utils.dataProp(value)(utils.absBool.True, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        val localEnv = context.pureLocal
        val newCtx = localEnv.getOrElse("@env")(ExecContext.Bot) { propv =>
          propv.objval.value.locset.foldLeft(ExecContext.Bot)((tmpCtx, loc) => {
            tmpCtx + context.update(loc, context.getOrElse(loc, DecEnvRecord.Bot).update(x, propV))
          })
        }
        State(heap, newCtx)
      case CapturedCatchVar =>
        val collapsedLoc = PredefLoc.COLLAPSED
        val objV = utils.dataProp(value)(utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        State(heap, context.update(collapsedLoc, context.getOrElse(collapsedLoc, DecEnvRecord.Bot).update(x, propV)))
      case GlobalVar =>
        val globalLoc = BuiltinGlobal.loc
        val objV = utils.dataProp(value)(utils.absBool.True, utils.absBool.True, utils.absBool.False)
        val propV = PropValue(objV)
        val newHeap =
          if (utils.absBool.True == heap.hasProperty(globalLoc, utils.absString.alpha(x))(utils)) heap
          else heap.update(globalLoc, heap.getOrElse(globalLoc, Obj.Bot(utils)).update(x, propV))
        State(newHeap, context)
    }
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, str: String)(utils: Utils): (State, AbsBool) = {
    val absStr = utils.absString.alpha(str)
    val (newHeap, b1) = heap.delete(loc, absStr)(utils)
    val (newCtx, b2) = context.delete(loc, str)(utils)
    (State(newHeap, newCtx), b1 + b2)
  }
}

object State {
  val Bot: State = State(Heap.Bot, ExecContext.Bot)
}
