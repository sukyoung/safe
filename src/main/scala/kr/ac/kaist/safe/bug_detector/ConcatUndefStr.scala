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

object ConcatUndefStr extends BugDetector {
  def concatUndefStr(expr: CFGExpr, l: CFGExpr, r: CFGExpr): String = {
    val span = expr.ir.span
    s"$span\n    [Warning] Concatenation of undefined to string: $l and $r."
  }

  // Check expression-level rules: AbsentPropertyRead
  private def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    case CFGBin(_, l, EJSPos, r) => {
      print("hello")
      val (v, _) = semantics.V(expr, state)
      val (lv, _) = semantics.V(l, state)
      val (rv, _) = semantics.V(r, state)

      def isStr(v: AbsValue) = !v.pvalue.strval.isBottom
      def isUndef(v: AbsValue) = !v.pvalue.undefval.isBottom

      if (isStr(v) && (isUndef(lv) || isUndef(rv)))
        List(concatUndefStr(expr, l, r))
      else
        List()
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
