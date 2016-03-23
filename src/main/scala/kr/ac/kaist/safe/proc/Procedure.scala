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

package kr.ac.kaist.safe.proc

import kr.ac.kaist.safe.Config

abstract class Procedure(
    mayPrev: Option[Procedure],
    mayConfig: Option[ConfigOption]
) {
  def apply(config: Config): Unit = ()
  def getOptMap: Option[OptRegexMap] = mayConfig match {
    case Some(config) => config.getOptMap
    case None => Some(Map())
  }
}
