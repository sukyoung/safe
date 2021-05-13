/**
 * *****************************************************************************
 * Copyright (c) 2016-2020, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.bug_detector

import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._

trait BugDetector {
  val id: Int

  def apply(cfg: CFG, semantics: Semantics): Unit = {
    cfg.getUserBlocks.foreach(b => {
      val bugs = checkBlock(b, semantics)
      bugs.foreach(println)
    })
  }

  protected def isReachableUserCode(block: CFGBlock, sem: Semantics): Boolean =
    !sem.getState(block).isEmpty && !NodeUtil.isModeled(block)

  // Detect bugs that can happen at block level
  def checkBlock(block: CFGBlock, semantics: Semantics): List[String] = {
    if (isReachableUserCode(block, semantics)) {
      semantics.getState(block).foldLeft(List[String]()) {
        case (bugs, (tp, st)) => {
          val cp = ControlPoint(block, tp)
          val (res, _) = block.getInsts.foldRight(bugs, st)((inst, r) => inst match {
            case inst: CFGNormalInst =>
              val (bs, state) = r
              val newAlarms = checkInst(inst, state, semantics)
              val (res, _) = semantics.I(cp, inst, state, AbsState.Bot)
              (newAlarms ::: bs, res)
            case inst: CFGCallInst =>
              val (bs, state) = r
              val newAlarms = checkCallInst(inst, state)
              (newAlarms ::: bs, state)
          })
          res
        }
      }
    } else List[String]()
  }

  private def collectExprs(expr: CFGExpr): List[CFGExpr] = {
    val subExprs = expr match {
      case CFGLoad(_, obj, index) => List(obj, index)
      case CFGBin(_, first, _, second) => List(first, second)
      case CFGUn(_, _, expr) => List(expr)
      case _ => Nil
    }
    subExprs.foldLeft(List(expr))((acc, expr) => {
      collectExprs(expr) ::: acc
    })
  }

  private def collectExprs(i: CFGNormalInst): List[CFGExpr] = {
    val exprs = i match {
      case CFGAlloc(_, _, _, Some(e), _) => List(e)
      case CFGEnterCode(_, _, _, e) => List(e)
      case CFGExprStmt(_, _, _, e) => List(e)
      case CFGDelete(_, _, _, e) => List(e)
      case CFGDeleteProp(_, _, _, e1, e2) => List(e1, e2)
      case CFGStore(_, _, e1, e2, e3) => List(e1, e2, e3)
      case CFGStoreStringIdx(_, _, e1, _, e2) => List(e1, e2)
      case CFGAssert(_, _, e, _) => List(e)
      case CFGReturn(_, _, Some(e)) => List(e)
      case CFGThrow(_, _, e) => List(e)
      case CFGInternalCall(_, _, _, _, es, _) => es
      case _ => Nil
    }
    exprs.foldLeft(List[CFGExpr]())((acc, expr) => {
      collectExprs(expr) ::: acc
    })
  }

  // Detect bugs that can happen at instruction level
  def checkInst(i: CFGNormalInst, state: AbsState, semantics: Semantics): List[String] = {
    val subExprs = collectExprs(i)
    subExprs.foldRight(List[String]())((e, r) => checkExpr(e, state, semantics) ::: r)
  }
  def checkCallInst(i: CFGCallInst, state: AbsState): List[String] = List()

  // Detect bugs that can happen at expression level
  def checkExpr(expr: CFGExpr, state: AbsState, semantics: Semantics): List[String]
}
