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

object CmpFunPrim extends BugDetector {

  def cmpFunPrim(expr: CFGExpr, l: CFGExpr, r: CFGExpr): String = {
    val span = expr.ir.span
    s"$span\n    [Warning] Comparison between function and primitive value: $l and $r."
  }

  def isFun(v: AbsValue, h: AbsHeap): Boolean = {
    // Check for each object location
    v.locset.foreach(objLoc => {
      if (h.get(objLoc)(ICall).fidset !⊑ AbsFId.Bot)
        return true
    })
    false
  }

  def isPrim(v: AbsValue): Boolean = {
    !v.pvalue.numval.isBottom ||
      !v.pvalue.boolval.isBottom ||
      !v.pvalue.strval.isBottom
  }

  // Check expression-level rules: AbsentPropertyRead
  private def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    case CFGBin(_, l, op, r) => op match {
      case EJSEq |
        EJSNEq |
        EJSSEq |
        EJSSNEq |
        EJSLt |
        EJSGt |
        EJSLte |
        EJSGte => {

        val (lv, _) = semantics.V(l, state)
        val (rv, _) = semantics.V(r, state)
        val h = state.heap

        if (isPrim(lv) && isFun(rv, h) || isFun(lv, h) && isPrim(rv))
          List(cmpFunPrim(expr, l, r))
        else
          List()
        //TODO: Message is printed twice. (both == and !==)
      }
      case _ => List()
    }
    case _ => List()
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
    val subExprs = collectExprs(i)
    subExprs.foldRight(List[String]())((e, r) => checkExpr(e, state, semantics) ::: r)
  }

  def getAlarmsFromBlock(b: CFGBlock, state: AbsState, semantics: Semantics): List[String] = List()
}
