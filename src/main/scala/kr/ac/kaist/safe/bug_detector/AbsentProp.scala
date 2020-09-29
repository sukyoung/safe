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

object AbsentProp extends BugDetector {
  val id = 99

  def absentProp(expr: CFGExpr, name: AbsStr, obj: CFGExpr): String =
    expr.ir.span.toString + ":\n    [Warning] The property " + name + " of the object \"" + obj.ir.ast.toString(0) + "\" is absent."

  // Check expression-level rules: AbsentPropertyRead
  def checkExpr(expr: CFGExpr, state: AbsState,
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
}
