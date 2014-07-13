/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import java.util.{HashMap=>JHashMap}
import java.util.{List => JList}
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.nodes.Program

class BugDetector(_ast: Program, _cfg: CFG, _typing: TypingInterface, quiet: Boolean, _fromHoister: JList[BugInfo]) {
  val params        = Shell.params
  val ast           = _ast
  val cfg           = _cfg
  val typing        = _typing
  val callGraph     = typing.computeCallGraph
  val fromHoister   = toList(_fromHoister)
  val env           = typing.env

  val quietFlag     = quiet
  val analyzeMode   = params.command
  val errorOnly     = params.opt_ErrorOnly
  val locclone      = params.opt_LocClone
  val trycatch      = params.opt_TryCatch
  val libMode       = params.opt_Library
  val devMode       = params.opt_DeveloperMode
   
  val semantics     = new Semantics(cfg, Worklist.computes(cfg, quiet), locclone)
  val varManager    = new VarManager(this)
  val stateManager  = new StateManager(cfg, typing, semantics, varManager)
  val bugStorage    = new BugStorage(this)
  val bugOption     = new BugOption(!devMode)
  //val bugOption     = new BugOption(devMode)

  val TSChecker     = new TSChecker(this)

  val ASTDetect     = new ASTDetect(this)
  val CommonDetect  = new CommonDetect(this)
  val ExprDetect    = new ExprDetect(this)
  val InstDetect    = new InstDetect(this)
  val FinalDetect   = new FinalDetect(this)

  def detectBug(): Unit = {
    bugStorage.recordStartTime(System.nanoTime)
    ASTDetect.check()
    traverseCFG; FinalDetect.check(cfg)
    bugStorage.recordEndTime(System.nanoTime)
    bugStorage.reportDetectedBugs(params.opt_ErrorOnly, quiet)
  }

  /* Traverse all nodes in CFG */
  def traverseCFG(): Unit = {
    for(node <- cfg.getNodes) { 
      C(node, cfg.getCmd(node))
    }

    def C(node: Node, cmd: Cmd): Unit = {
      cmd match {
        case Block(insts) =>
          for (inst <- insts) {
            val cstate = stateManager.getInputCState(node, inst.getInstId, CallContext._MOST_SENSITIVE)
            val newcstate = if(!Shell.params.opt_DeveloperMode){
              cstate.filter(p => {   
                 cfg.getInfeasibility((node, p._1)).isEmpty
               })
            }
              else cstate
            if(!newcstate.isEmpty)
              I(node, inst, newcstate)
          }
        case _ => Unit
      }
    }

    def I(node: Node, inst: CFGInst, stateMap: CState): Unit = {
      inst match {
        case CFGAlloc(_, _ , x, e, a_new) => 
          e match {
            case Some(expr) => V(inst, expr, stateMap)
            case None => Unit
          }
        case CFGAllocArray(_, _, x, n, a_new) => Unit
        case CFGAllocArg(_, _, x, n, a_new) => Unit
        case CFGAssert(_, info, expr, _) => V(inst, expr, stateMap)
        case CFGAPICall(_, model, fun, args) => Unit
        case CFGCall(_, _, fun, base, arguments, a_new, b_new) =>
          V(inst, fun, stateMap)
          V(inst, base, stateMap)
          V(inst, arguments, stateMap)
        case CFGConstruct(_, _, cons, base, arguments, a_new, b_new) => 
          V(inst, cons, stateMap)
          V(inst, base, stateMap)
          V(inst, arguments, stateMap)
        case CFGCatch(_, _, name) => Unit
        case CFGDelete(_, _, lhs, expr) => V(inst, expr, stateMap)
        case CFGDeleteProp(_, _, lhs, obj, index) =>
          V(inst, obj, stateMap)
          V(inst, index, stateMap)
        case CFGExprStmt(_, _,x, expr) =>
          V(inst, expr, stateMap)
          expr match {
            case CFGString(x) => x match {
              case "safe_print_node"    => cfg.dump(node)
              case "safe_print_states"  => stateManager.dump(node, inst, stateMap)
              case _ => // pass
            } case _ => // pass
          } 
        case CFGFunExpr(_, _, lhs, name, fid, a_new1, a_new2, a_new3) => Unit
        case CFGInternalCall(_, _, lhs, fun, arguments, loc) => 
          (fun.toString, arguments, loc)  match {
            case ("<>Global<>toObject", List(expr), Some(a_new)) => V(inst, expr, stateMap)
            case ("<>Global<>toNumber", List(expr), None) => V(inst, expr, stateMap)
            case ("<>Global<>isObject", List(expr), None) => V(inst, expr, stateMap)
            case ("<>Global<>getBase", List(expr), None) => V(inst, expr, stateMap)
            case ("<>Global<>iteratorInit", List(expr), None) => V(inst, expr, stateMap)
            case ("<>Global<>iteratorHasNext", List(expr_2, expr_3), None) =>
              V(inst, expr_2, stateMap)
              V(inst, expr_3, stateMap)
            case ("<>Global<>iteratorNext", List(expr_2, expr_3), None) =>
              V(inst, expr_2, stateMap)
              V(inst, expr_3, stateMap)
            case _ => Unit
          }
        case CFGReturn(_, _, expr) => 
          expr match {
            case Some(e) => V(inst, e, stateMap)
            case None => Unit
          }
        case CFGStore(_, _, obj, index, rhs) => 
          V(inst, obj, stateMap)
          V(inst, index, stateMap)
          V(inst, rhs, stateMap)
        case CFGThrow(_, _, expr) => V(inst, expr, stateMap)
        case _ => Unit
      }
      //System.out.println(inst)
      InstDetect.check(inst, stateMap)
    }

    def V(inst: CFGInst, expr: CFGExpr, stateMap: CState, typeof: Boolean = false): Unit = {
      ExprDetect.check(inst, expr, stateMap, typeof)
      expr match {
        case CFGBin(info, first, op, second) => 
          V(inst, first, stateMap)
          V(inst, second, stateMap)
        case CFGLoad(info, obj, index) => 
          V(inst, obj, stateMap)
          V(inst, index, stateMap)
        case CFGThis(info) => Unit
        case CFGUn(info, op, first) => op.getText match {
          case "typeof" => V(inst, first, stateMap, true) 
          case _ => V(inst, first, stateMap)
        }
        case CFGVarRef(info, id) => Unit
        case _ => Unit
      }
    }
  }

  def traverseInsts(f: (Node, CFGInst) => Unit): Unit = {
    for (node <- cfg.getNodes) {
      cfg.getCmd(node) match {
        case Block(insts) => for (inst <- insts) f(node, inst)
        case _ =>
      }
    }
  }
}
