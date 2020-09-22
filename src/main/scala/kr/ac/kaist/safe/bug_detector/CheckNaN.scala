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

import scala.util.{ Failure, Success, Try }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._

object CheckNaN extends BugChecker {
  def checkNaN(v: AbsValue, span: Span): List[String] = {
    if (AbsNum.NaN ⊑ v) {
      List(span.toString + ":\n    [Warning] Observed NaN.")
    } else {
      List()
    }
  }

  // Check expression-level rules: AbsentPropertyRead
  private def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    case _: CFGLoad | _: CFGBin | _: CFGUn =>
      val (v, _) = semantics.V(expr, state)
      checkNaN(v, expr.span)
    case _ => List[String]()
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

  def getAlarmsFromInst(i: CFGNormalInst, state: AbsState, semantics: Semantics): List[String] = {
    val exprsBugs = collectExprs(i).foldRight(List[String]())((e, r) => checkExpr(e, state, semantics) ::: r)
    exprsBugs
  }

  def getAlarmsFromBlock(b: CFGBlock, state: AbsState, semantics: Semantics): List[String] = b match {
    case AfterCall(func, retVar, call) =>
      val (retVal, excSet) = state.lookup(retVar)
      checkNaN(retVal, b.span)
    case _ => List()
  }
}
