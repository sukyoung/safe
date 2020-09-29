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

object Always extends BugDetector {
  val id = 99

  def always(expr: CFGExpr, cond: Boolean): String =
    expr.ir.span.toString + ":\n    [Warning] The conditional expression \"" + expr.ir.ast.toString(0) + "\" is always " + cond + "."

  override def checkInst(i: CFGNormalInst, state: AbsState, semantics: Semantics): List[String] = i match {
    case i @ CFGAssert(_, _, cond, true) =>
      val (v, _) = semantics.V(cond, state)
      val bv = TypeConversionHelper.ToBoolean(v)
      if (!bv.isBottom && ((bv StrictEquals AbsBool.True) ⊑ AbsBool.True))
        List(always(cond, true))
      else if (!bv.isBottom && ((bv StrictEquals AbsBool.False) ⊑ AbsBool.True))
        List(always(cond, false))
      else List()
    case _ => List()
  }

  def checkExpr(expr: CFGExpr, state: AbsState, semantics: Semantics): List[String] = List()
}
