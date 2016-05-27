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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.cfg_builder._
import kr.ac.kaist.safe.nodes.{ CFGExpr, CFGInst, CFGNode }

class Semantics(cfg: CFG, utils: Utils, addressManager: AddressManager) {
  lazy val excLog: ExcLog = new ExcLog

  val helper: Helper = Helper(utils, addressManager)

  def E(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj, st: State): State = {
    (cp1.node, cp2.node) match {
      case (_, Entry(f)) => st.heap match {
        case Heap.Bot => State.Bot
        case h1: Heap => {
          val objEnv = obj("@scope") match {
            case Some(propV) => helper.newDeclEnvRecord(propV.objval.value)
            case None => helper.newDeclEnvRecord(utils.ValueBot)
          }
          val obj2 = obj - "@scope"
          val h2 = h1.remove(addressManager.SINGLE_PURE_LOCAL_LOC).update(addressManager.SINGLE_PURE_LOCAL_LOC, obj2)
          val h3 = obj2("@env") match {
            case Some(propV) =>
              propV.objval.value.locset.foldLeft(Heap.Bot)((hi, locEnv) => {
                hi + h2.update(locEnv, objEnv)
              })
            case None => Heap.Bot
          }
          State(h3, ctx)
        }
      }
      case (Exit(_), _) if st.heap.isBottom => State.Bot
      case (Exit(_), _) if st.context.isBottom => State.Bot
      case (Exit(f1), AfterCall(f2, retVar, call)) =>
        val (h1, c1) = (st.heap, st.context)
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(addressManager.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
          val returnV = localObj.getOrElse("@return", utils.PropValueBot).objval.value
          val h2 = h1.update(addressManager.SINGLE_PURE_LOCAL_LOC, obj1)
          val h3 = helper.varStore(h2, retVar, returnV)
          State(h3, c2)
        }
      case (Exit(f), _) =>
        val c1 = st.context
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case (ExitExc(_), _) if st.heap.isBottom => State.Bot
      case (ExitExc(_), _) if st.context.isBottom => State.Bot
      case (ExitExc(_), AfterCatch(_, _)) =>
        val (h1, c1) = (st.heap, st.context)
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          val localObj = h1.getOrElse(addressManager.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
          val excValue = localObj.getOrElse("@exception", utils.PropValueBot).objval.value
          val excObjV = ObjectValue(excValue, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
          val oldExcAllValue = obj1.getOrElse("@exception_all", utils.PropValueBot).objval.value
          val newExcAllObjV = ObjectValue(excValue + oldExcAllValue, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
          val h2 = h1.update(
            addressManager.SINGLE_PURE_LOCAL_LOC,
            obj1.update("@exception", PropValue(excObjV))
              .update("@exception_all", PropValue(newExcAllObjV))
          )
          State(h2, c2)
        }
      case (ExitExc(f), _) =>
        val c1 = st.context
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else {
          excLog.signal(IPFromExitToNoneError(f.ir))
          State.Bot
        }
      case _ => st
    }
  }

  def C(cp: ControlPoint, cmd: CFGBlock, st: State): (State, State) = {
    (st.heap, st.context) match {
      case (Heap.Bot, Context.Bot) => (State.Bot, State.Bot)
      case (h: Heap, ctx: Context) =>
        cmd match {
          case Entry(_) => {
            val fun = cp.node.func
            val xArgVars = fun.argVars
            val xLocalVars = fun.localVars
            val localObj = h.getOrElse(addressManager.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
            val locSetArg = localObj.getOrElse(fun.argumentsName, utils.PropValueBot).objval.value.locset
            val (nHeap, _) = xArgVars.foldLeft((h, 0))((res, x) => {
              val (iHeap, i) = res
              val vi = locSetArg.foldLeft(utils.ValueBot)((vk, lArg) => {
                vk + helper.proto(iHeap, lArg, utils.absString.alpha(i.toString))
              })
              (helper.createMutableBinding(iHeap, x, vi), i + 1)
            })
            val hm = xLocalVars.foldLeft(nHeap)((hj, x) => {
              val undefPV = PValue(utils.absUndef.Top, utils.absNull.Bot, utils.absBool.Bot, utils.absNumber.Bot, utils.absString.Bot)
              helper.createMutableBinding(hj, x, Value(undefPV, LocSetEmpty))
            })
            (State(hm, ctx), State.Bot)
          }
          case Exit(_) => (st, State.Bot)
          case ExitExc(_) => (st, State.Bot)
          case call: Call => I(cp, call.callInst, st, State.Bot)
          case afterCall: AfterCall => (st, State.Bot)
          case afterCatch: AfterCatch => (st, State.Bot)
          case block: CFGNormalBlock =>
            block.getInsts.foldLeft((st, State.Bot))((states, inst) => {
              val (oldSt, oldExcSt) = states
              I(cp, inst, oldSt, oldExcSt)
            })
        }
    }
  }

  def I(cp: ControlPoint, i: CFGInst, s: State, se: State): (State, State) = {
    (State.Bot, State.Bot)
  }

  def B(expr: CFGExpr, s: State, se: State, inst: CFGInst, cfg: CFG, cp: ControlPoint): (State, State) = {
    (State.Bot, State.Bot)
  }
}
