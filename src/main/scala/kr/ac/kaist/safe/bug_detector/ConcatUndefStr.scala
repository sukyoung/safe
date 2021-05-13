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
  val id = 3

  def concatUndefStr(expr: CFGExpr, l: CFGExpr, r: CFGExpr): String = {
    val span = expr.ir.span
    s"[$id] $span:$LINE_SEP    [Warning] Concatenation of undefined with string: $l and $r."
  }

  // Check expression-level rules: AbsentPropertyRead
  def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    case CFGBin(_, l, EJSPos, r) => {
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
}
