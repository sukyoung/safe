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
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._

object CmpFunPrim extends BugDetector {
  val id = 2

  private def cmpFunPrim(expr: CFGExpr, l: CFGExpr, r: CFGExpr): String = {
    val span = expr.ir.span
    s"[$id] $span:$LINE_SEP    [Warning] Comparison between function and primitive value: $l and $r."
  }

  private def isFun(v: AbsValue, h: AbsHeap): Boolean = {
    // Check for each object location
    v.locset.foreach(objLoc => {
      if (h.get(objLoc)(ICall).fidset !⊑ AbsFId.Bot)
        return true
    })
    false
  }

  private def isPrim(v: AbsValue): Boolean = {
    !v.pvalue.numval.isBottom ||
      !v.pvalue.boolval.isBottom ||
      !v.pvalue.strval.isBottom
  }

  // Check expression-level rules: AbsentPropertyRead
  def checkExpr(expr: CFGExpr, state: AbsState,
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
}
