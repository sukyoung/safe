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

    AbsLoc = DefaultLoc(cfg.getAllAddrSet)
  }

  // primitive values
  var AbsUndef: AbsUndefUtil = null
  var AbsNull: AbsNullUtil = null
  var AbsBool: AbsBoolUtil = null
  var AbsNumber: AbsNumberUtil = null
  var AbsString: AbsStringUtil = null
  var AbsPValue: AbsPValueUtil = DefaultPValue

  // location
  var AbsLoc: AbsLocUtil = null

  // value
  var AbsValue: AbsValueUtil = DefaultValue

  // data property
  var AbsDataProp: AbsDataPropUtil = DefaultDataProp

  // absent value for parital map
  var AbsAbsent: AbsAbsentUtil = DefaultAbsent

  // execution context
  var AbsBinding: AbsBindingUtil = DefaultBinding
  var AbsDecEnvRec: AbsDecEnvRecUtil = DefaultDecEnvRec
}
