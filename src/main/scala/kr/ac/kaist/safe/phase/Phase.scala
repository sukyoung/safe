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

import scala.collection.immutable.HashMap
import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.config.{ Config, ConfigOption }

abstract class Phase(
    mayPrev: Option[Phase],
    mayConfig: Option[ConfigOption]
) {
  def apply(config: Config): Unit = ()
  lazy val optRegexMap: Try[OptRegexMap] = (mayConfig match {
    case Some(configOption) => configOption.optRegexMap
    case None => Success(HashMap())
  }) match {
    case Success(map) => (mayPrev match {
      case Some(prev) => prev.optRegexMap
      case None => Success(HashMap())
    }).map(_ ++ map)
    case Failure(e) => Failure(e)
  }
}

trait PhaseHelper {
  def create: Phase
}
