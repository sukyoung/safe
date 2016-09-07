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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.nodes.cfg.CFG

object Utils {
  def register(
    absUndef: AbsUndefUtil,
    absNull: AbsNullUtil,
    absBool: AbsBoolUtil,
    absNumber: AbsNumberUtil,
    absString: AbsStringUtil,
    cfg: CFG
  ): Unit = {
    AbsUndef = absUndef
    AbsNull = absNull
    AbsBool = absBool
    AbsNumber = absNumber
    AbsString = absString
    AbsPValue = new DefaultPValue(
      absUndef,
      absNull,
      absBool,
      absNumber,
      absString
    )
    AbsLoc = new DefaultLoc(cfg.getAllLocSet)
  }
  var AbsUndef: AbsUndefUtil = null
  var AbsNull: AbsNullUtil = null
  var AbsBool: AbsBoolUtil = null
  var AbsNumber: AbsNumberUtil = null
  var AbsString: AbsStringUtil = null
  var AbsPValue: AbsPValueUtil = null
  var AbsLoc: AbsLocUtil = null
}
