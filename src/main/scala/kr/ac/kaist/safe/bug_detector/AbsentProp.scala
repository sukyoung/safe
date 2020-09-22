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

object AbsentProp extends BugChecker {
  def absentProp(expr: CFGExpr, name: AbsStr, obj: CFGExpr): String =
    expr.ir.span.toString + ":\n    [Warning] The property " + name + " of the object \"" + obj.ir.ast.toString(0) + "\" is absent."

  // Check expression-level rules: AbsentPropertyRead
  private def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    // Don't check if this instruction is "LHS = <>fun<>["prototype"]".
    case CFGLoad(_, CFGVarRef(_, CFGTempId(name, _)),
      CFGVal(EJSString("prototype"))) if name.startsWith("<>fun<>") =>
      List[String]()
    case CFGLoad(_, obj, index) =>
      val (objV, _) = semantics.V(obj, state)
      val (propV, _) = semantics.V(index, state)
      // Check for each object location
      objV.locset.foldLeft(List[String]())((bugs, objLoc) => {
        if (!propV.isBottom && !propV.pvalue.strval.isBottom) {
          val propStr = propV.pvalue.strval
          val heap = state.heap
          val propExist = heap.get(objLoc).HasProperty(propStr, heap)
          if (!propExist.isBottom && propExist ⊑ AbsBool.False)
            absentProp(expr, propStr, obj) :: bugs
          else bugs
        } else bugs
      })
    case _ => List[String]()
  }

  private def collectExprs(i: CFGNormalInst): List[CFGExpr] = i match {
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

  def getAlarmsFromInst(i: CFGNormalInst, state: AbsState, semantics: Semantics): List[String] = {
    val exprsBugs = collectExprs(i).foldRight(List[String]())((e, r) => checkExpr(e, state, semantics) ::: r)
    exprsBugs
  }

  def getAlarmsFromBlock(b: CFGBlock, state: AbsState, semantics: Semantics): List[String] = List()
}
