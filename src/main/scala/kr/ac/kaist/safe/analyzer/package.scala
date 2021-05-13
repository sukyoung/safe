/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.util.HashMap
import kr.ac.kaist.safe.nodes.cfg.CFG

package object analyzer {
  type Map[K, V] = HashMap[K, V]
  val Map = HashMap

  var stopAlreadyVisited = false
  var stopExitExc = false

  ////////////////////////////////////////////////////////////////////////////////
  // DynamicShortcut
  ////////////////////////////////////////////////////////////////////////////////
  var dynamicShortcut = false

  // debug mode
  var analysisDebug = false
  var toJSONFailed = false

  // count
  var dsCount = 0
  var dsSuccessCount = 0

  // time
  var totalDuration = 0L
  var dsDuration = 0L

  // touched functions
  var touchedFuncs: Set[Int] = Set()

  // fid to location
  case class FidNameCase(isCall: Boolean, name: String)
  var fidToName: Map[Int, FidNameCase] = Map()

  // CFG
  var globalCFG: CFG = null

  def debug(msg: String): Unit = System.err.println(msg)
}
