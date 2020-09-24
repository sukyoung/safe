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

object CheckNaN extends BugDetector {
  private def checkNaN(v: AbsValue, span: Span): List[String] = {
    if (AbsNum.NaN ⊑ v) {
      List(span.toString + ":\n    [Warning] Observed NaN.")
    } else {
      List()
    }
  }

  def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    case _: CFGLoad | _: CFGBin | _: CFGUn =>
      val (v, _) = semantics.V(expr, state)
      checkNaN(v, expr.span)
    case _ => List[String]()
  }

  override def checkBlock(block: CFGBlock, semantics: Semantics): List[String] = block match {
    case AfterCall(_, retVar, _) => {
      if (isReachableUserCode(block, semantics)) {
        semantics.getState(block).foldLeft(List[String]()) {
          case (bugs, (tp, st)) => {
            val (retVal, excSet) = st.lookup(retVar)
            checkNaN(retVal, block.span)
          }
        }
      } else List()
    }
    case _ => super.checkBlock(block, semantics)
  }
}
