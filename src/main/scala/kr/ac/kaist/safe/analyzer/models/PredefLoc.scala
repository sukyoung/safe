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

object PredefLoc extends ModelLoc {
  val GLOBAL: Loc = SystemLoc("Global", Recent)
  val SINGLE_PURE_LOCAL: Loc = SystemLoc("PureLocal", Recent)
  val COLLAPSED: Loc = SystemLoc("Collapsed", Old)
}
