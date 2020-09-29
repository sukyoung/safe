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

object UndefOffset extends BugDetector {
  val id = 6

  def undefOffset(expr: CFGExpr, index: CFGExpr): String = {
    val span = expr.span
    s"[$id] $span:$LINE_SEP    [Warning] Offset $index may be undefined."
  }
  def undefOffset(inst: CFGNormalInst, index: CFGExpr): String = {
    val span = inst.ir.span
    s"[$id] $span:$LINE_SEP    [Warning] Offset $index may be undefined."
  }

  def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    case CFGLoad(_, obj, index) => {
      val (iv, _) = semantics.V(index, state)
      if (!iv.pvalue.undefval.isBottom)
        List(undefOffset(expr, index))
      else
        List()
    }
    case _ => {
      List()
    }
  }

  override def checkInst(inst: CFGNormalInst, state: AbsState, semantics: Semantics) = (inst match {
    case CFGStore(_, _, _, index, _) => {
      val (iv, _) = semantics.V(index, state)
      if (!iv.pvalue.undefval.isBottom)
        List(undefOffset(inst, index))
      else
        List()
    }
    case _ => List()
  }) ::: super.checkInst(inst, state, semantics)
}
