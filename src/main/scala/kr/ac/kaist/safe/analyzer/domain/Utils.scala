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
    absString: AbsStringUtil
  ): Unit = {
    AbsUndef = absUndef
    AbsNull = absNull
    AbsBool = absBool
    AbsNumber = absNumber
    AbsString = absString
  }

  // primitive values
  var AbsUndef: AbsUndefUtil = null
  var AbsNull: AbsNullUtil = null
  var AbsBool: AbsBoolUtil = null
  var AbsNumber: AbsNumberUtil = null
  var AbsString: AbsStringUtil = null
  var AbsPValue: AbsPValueUtil = DefaultPValue

  // location
  var AbsLoc: AbsLocUtil = DefaultLoc

  // value
  var AbsValue: AbsValueUtil = DefaultValue

  // data property
  var AbsDataProp: AbsDataPropUtil = DefaultDataProp

  // absent value for parital map
  var AbsAbsent: AbsAbsentUtil = DefaultAbsent

  // execution context
  var AbsBinding: AbsBindingUtil = DefaultBinding
  var AbsDecEnvRec: AbsDecEnvRecUtil = DefaultDecEnvRec
  var AbsGlobalEnvRec: AbsGlobalEnvRecUtil = DefaultGlobalEnvRec
  var AbsEnvRec: AbsEnvRecUtil = DefaultEnvRec
  var AbsLexEnv: AbsLexEnvUtil = DefaultLexEnv
  var AbsContext: AbsContextUtil = DefaultContext

  // concrete domains
  def ConSingle[T]: ConSingleUtil[T] = ConSingleUtil[T]
  def ConSet[T]: ConSetUtil[T] = ConSetUtil[T]
}
