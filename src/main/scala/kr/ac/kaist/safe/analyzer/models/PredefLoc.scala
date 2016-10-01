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

import kr.ac.kaist.safe.analyzer.domain.{ Loc, SystemLoc, Recent, Old }

object PredefLoc {
  val GLOBAL_ENV: Loc = SystemLoc("GlobalEnv", Recent)
  val PURE_LOCAL: Loc = SystemLoc("PureLocal", Recent)
  val COLLAPSED: Loc = SystemLoc("Collapsed", Old)
}
