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

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.util.{ Loc, SystemLoc, Recent, Old }

object ErrorLoc extends ModelLoc {
  val ERR: Loc = SystemLoc("Err", Old)
  val EVAL_ERR: Loc = SystemLoc("EvalErr", Old)
  val RANGE_ERR: Loc = SystemLoc("RangeErr", Old)
  val REF_ERR: Loc = SystemLoc("RefErr", Old)
  val SYNTAX_ERR: Loc = SystemLoc("SyntaxErr", Old)
  val TYPE_ERR: Loc = SystemLoc("TypeErr", Old)
  val URI_ERR: Loc = SystemLoc("URIErr", Old)
}
