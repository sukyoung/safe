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

object ConstructorLoc extends ModelLoc {
  //TODO: temporal definitions of builtin constructor locations
  val OBJ: Loc = SystemLoc("ObjectConst", Recent)
  val ARRAY: Loc = SystemLoc("ArrayProto", Recent)
}
