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

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.analyzer.domain.Loc

// allocation site
abstract sealed class AllocSite extends Loc

// allocation site defined in user code
case class UserAllocSite(id: Int) extends AllocSite {
  override def toString: String = id.toString
}

// predefined allocation site
case class PredAllocSite(name: String) extends AllocSite {
  override def toString: String = name
}
object PredAllocSite {
  // global environment
  val GLOBAL_ENV: PredAllocSite = PredAllocSite("GlobalEnv")
  // pure local environment
  val PURE_LOCAL: PredAllocSite = PredAllocSite("PureLocal")
  // collapsed environment for try-catch
  val COLLAPSED: PredAllocSite = PredAllocSite("Collapsed")
}
