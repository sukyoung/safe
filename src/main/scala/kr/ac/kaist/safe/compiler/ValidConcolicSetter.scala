/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.compiler

import kr.ac.kaist.safe.nodes.ir._

object ValidConcolicSetter {

  def setValidConcolic(expr: IRExpr, isValid: Boolean): IRExpr = expr match {
    case i @ IRBin(ast, first, op, second, _) =>
      i.copy(validConcolic = isValid)
    case _ =>
      expr
  }

}
