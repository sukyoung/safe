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
import kr.ac.kaist.safe.errors.error.OptConflictError

abstract class Phase(
    mayPrev: Option[Phase],
    mayConfig: Option[ConfigOption]
) {
  def apply(config: Config): Unit = ()
  lazy val optRegexMap: Try[OptRegexMap] = {
    val empty: OptRegexMap = HashMap()
    (mayConfig match {
      case Some(configOption) => configOption.optRegexMap
      case None => Success(empty)
    }).flatMap(map => (mayPrev match {
      case Some(prev) => prev.optRegexMap
      case None => Success(empty)
    }).flatMap(pMap => (pMap.keySet intersect map.keySet).isEmpty match {
      case true => Success(pMap ++ map)
      case false => Failure(OptConflictError)
    }))
  }
}

trait PhaseHelper {
  def create: Phase
}
