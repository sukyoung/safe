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

package kr.ac.kaist.safe.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.safe.config.{ Config, ConfigOption }

abstract class Phase(
    mayPrev: Option[Phase],
    mayConfig: Option[ConfigOption]
) {
  def apply(config: Config): Unit = ()
  def getOptMap: Try[OptRegexMap] = mayConfig match {
    case Some(config) => config.getOptMap
    case None => Success(Map())
  }
}

trait PhaseHelper {
  def create: Phase
}
