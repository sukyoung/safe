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

object CheckNaN extends BugDetector {
  val id = 1

  private def checkNaN(block: AfterCall): String = {
    val span = block.call.span
    val call = block.call.callInst.ir.ast.toString(0)
    s"[$id] $span:$LINE_SEP    [Warning] NaN might be returned by $call."
  }
  private def checkNaN(expr: CFGExpr): String = {
    val span = expr.span
    s"[$id] $span:$LINE_SEP    [Warning] The result of ${expr.ir.ast.toString(0)} might be NaN."
  }

  def isNaN(v: AbsValue): Boolean = AbsNum.NaN ⊑ v

  def checkExpr(expr: CFGExpr, state: AbsState, semantics: Semantics): List[String] = expr match {
    case _: CFGLoad | _: CFGBin | _: CFGUn => {
      val (v, _) = semantics.V(expr, state)
      if (isNaN(v))
        List(checkNaN(expr))
      else
        List()
    }
    case _ => List()
  }

  override def checkBlock(block: CFGBlock, semantics: Semantics): List[String] = block match {
    case b @ AfterCall(_, retVar, _) => {
      if (isReachableUserCode(b, semantics)) {
        semantics.getState(b).foldLeft(List[String]()) {
          case (bugs, (tp, st)) => {
            val (retVal, excSet) = st.lookup(retVar)
            if (isNaN(retVal))
              List(checkNaN(b))
            else
              List()
          }
        }
      } else List()
    }
    case _ => super.checkBlock(block, semantics)
  }
}
