/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.nodes.cfg.CFG
import scala.collection.immutable.HashSet

object Utils {
  def register(
    absUndef: AbsUndefUtil = DefaultUndef,
    absNull: AbsNullUtil = DefaultNull,
    absBool: AbsBoolUtil = DefaultBool,
    absNumber: AbsNumberUtil = DefaultNumber,
    absString: AbsStringUtil = StringSet(0),
    absHeap: AbsHeapUtil = DefaultHeap,
    absLoc: AbsLocUtil = DefaultLoc,
    aaddrType: AAddrType = RecencyAAddr
  ): Unit = {
    AbsUndef = absUndef
    AbsNull = absNull
    AbsBool = absBool
    AbsNumber = absNumber
    AbsString = absString
    AbsHeap = absHeap
    AbsLoc = absLoc
    AAddrType = aaddrType
  }

  // primitive values
  var AbsUndef: AbsUndefUtil = _
  var AbsNull: AbsNullUtil = _
  var AbsBool: AbsBoolUtil = _
  var AbsNumber: AbsNumberUtil = _
  var AbsString: AbsStringUtil = _
  var AbsPValue: AbsPValueUtil = DefaultPValue

  // abstract address type
  var AAddrType: AAddrType = _

  // location
  var AbsLoc: AbsLocUtil = _

  // value
  var AbsValue: AbsValueUtil = DefaultValue

  // data property
  var AbsDataProp: AbsDataPropUtil = DefaultDataProp

  // descriptor
  var AbsDesc: AbsDescUtil = DefaultDesc

  // absent value for parital map
  var AbsAbsent: AbsAbsentUtil = DefaultAbsent

  // execution context
  var AbsBinding: AbsBindingUtil = DefaultBinding
  var AbsDecEnvRec: AbsDecEnvRecUtil = DefaultDecEnvRec
  var AbsGlobalEnvRec: AbsGlobalEnvRecUtil = DefaultGlobalEnvRec
  var AbsEnvRec: AbsEnvRecUtil = DefaultEnvRec
  var AbsLexEnv: AbsLexEnvUtil = DefaultLexEnv
  var AbsContext: AbsContextUtil = DefaultContext

  // object
  var AbsObject: AbsObjectUtil = DefaultObject

  // heap
  var AbsHeap: AbsHeapUtil = DefaultHeap

  // state
  var AbsState: AbsStateUtil = DefaultState

  // concrete domains
  def ConSingle[T]: ConSingleUtil[T] = ConSingleUtil[T]
  def ConSet[T]: ConSetUtil[T] = ConSetUtil[T]
}
