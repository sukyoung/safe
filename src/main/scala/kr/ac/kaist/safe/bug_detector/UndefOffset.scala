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
  def undefOffset(expr: CFGExpr): String = {
    val span = expr.ir.span
    s"$span:$LINE_SEP    [Warning] Offset $expr may be undefined."
  }

  def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    case CFGLoad(_, obj, index) => {
      val (iv, _) = semantics.V(index, state)
      if (!iv.pvalue.undefval.isBottom)
        List(undefOffset(index))
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
        List(undefOffset(index))
      else
        List()
    }
    case _ => List()
  }) ::: super.checkInst(inst, state, semantics)
}
