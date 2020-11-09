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

package object analyzer {
  type Map[K, V] = HashMap[K, V]
  val Map = HashMap

  var stopAlreadyVisited = false
  var stopExitExc = false

  ////////////////////////////////////////////////////////////////////////////////
  // DynamicShortcut
  ////////////////////////////////////////////////////////////////////////////////
  var dynamicShortcut = false

  // count
  var dsCount = 0
  var dsSuccessCount = 0

  // time
  var totalDuration = 0L
  var dsDuration = 0L

  // touched functions
  var touchedFuncs: Set[Int] = Set()

  // fid to location
  var fidToName: Map[Int, String] = Map()
}
