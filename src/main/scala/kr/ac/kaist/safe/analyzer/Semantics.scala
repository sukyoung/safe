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

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.cfg_builder._
import kr.ac.kaist.safe.nodes.{ CFGExpr, CFGInst, CFGNode }

class Semantics(cfg: CFG, utils: Utils, addressManager: AddressManager) {
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
      case (Exit(_), _) =>
        val c1 = st.context
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else throw new InternalError("Inter-procedural edge from Exit node must be connected with After-Call node") //TODO
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
      case (ExitExc(_), _) =>
        val c1 = st.context
        val (c2, obj1) = helper.fixOldify(ctx, obj, c1.mayOld, c1.mustOld)
        if (c2.isBottom) State.Bot
        else throw new InternalError("Inter-procedural edge from Exit node must be connected with After-Call node") //TODO
      case _ => st
    }
  }

  def C(cp: ControlPoint, cmd: CFGNode, s: State): (State, State) = {
    (State.Bot, State.Bot)
  }

  def I(cp: ControlPoint, i: CFGInst, s: State, se: State): (State, State) = {
    (State.Bot, State.Bot)
  }

  def B(expr: CFGExpr, s: State, se: State, inst: CFGInst, cfg: CFG, cp: ControlPoint): (State, State) = {
    (State.Bot, State.Bot)
  }
}