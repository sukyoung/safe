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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap }

case class Initialize(cfg: CFG) {
  private val AT = AbsBool.True
  def state: State = {
    val globalLocSet = AbsLoc(BuiltinGlobal.loc)
    val globalPureLocalEnv = AbsLexEnv.newPureLocal(globalLocSet)
    val initHeap = AbsHeap(HashMap(
      SystemLoc("Dummy", Old) -> AbsObject.Bot // TODO If delete, not working because not allowed update to bottom heap
    ))

    val initCtx = AbsContext(HashMap[Loc, AbsLexEnv](
      PredefLoc.GLOBAL_ENV -> AbsLexEnv(AbsGlobalEnvRec.Top),
      PredefLoc.PURE_LOCAL -> globalPureLocalEnv,
      PredefLoc.COLLAPSED -> AbsLexEnv(AbsDecEnvRec.Empty)
    ), OldAddrSet.Empty, globalLocSet)

    val modeledHeap = BuiltinGlobal.initHeap(initHeap, cfg)

    State(modeledHeap, initCtx)
  }

  def testState: State = {
    val st = state
    val globalObj = st.heap.get(BuiltinGlobal.loc)
      .fold(AbsObject.Empty)(obj => obj)

    val boolBot = AbsBool.Bot

    val testGlobalObj =
      globalObj.initializeUpdate("__BOT", AbsDataProp(AbsValue.Bot))
        .initializeUpdate("__TOP", AbsDataProp(AbsPValue.Top))
        .initializeUpdate("__UInt", AbsDataProp(AbsNumber.UInt))
        .initializeUpdate("__Global", AbsDataProp(AbsValue(BuiltinGlobal.loc)))
        .initializeUpdate("__BoolTop", AbsDataProp(AbsBool.Top))
        .initializeUpdate("__NumTop", AbsDataProp(AbsNumber.Top))
        .initializeUpdate("__StrTop", AbsDataProp(AbsString.Top))
        .initializeUpdate("__EvalErrLoc", AbsDataProp(AbsValue(BuiltinEvalError.loc)))
        .initializeUpdate("__RangeErrLoc", AbsDataProp(AbsValue(BuiltinRangeError.loc)))
        .initializeUpdate("__RefErrLoc", AbsDataProp(AbsValue(BuiltinRefError.loc)))
        .initializeUpdate("__SyntaxErrLoc", AbsDataProp(AbsValue(BuiltinSyntaxError.loc)))
        .initializeUpdate("__TypeErrLoc", AbsDataProp(AbsValue(BuiltinTypeError.loc)))
        .initializeUpdate("__URIErrLoc", AbsDataProp(AbsValue(BuiltinURIError.loc)))
        .initializeUpdate("__EvalErrProtoLoc", AbsDataProp(AbsValue(BuiltinEvalErrorProto.loc)))
        .initializeUpdate("__RangeErrProtoLoc", AbsDataProp(AbsValue(BuiltinRangeErrorProto.loc)))
        .initializeUpdate("__RefErrProtoLoc", AbsDataProp(AbsValue(BuiltinRefErrorProto.loc)))
        .initializeUpdate("__SyntaxErrProtoLoc", AbsDataProp(AbsValue(BuiltinSyntaxErrorProto.loc)))
        .initializeUpdate("__TypeErrProtoLoc", AbsDataProp(AbsValue(BuiltinTypeErrorProto.loc)))
        .initializeUpdate("__URIErrProtoLoc", AbsDataProp(AbsValue(BuiltinURIErrorProto.loc)))
        .initializeUpdate("__ErrProtoLoc", AbsDataProp(AbsValue(BuiltinErrorProto.loc)))
        .initializeUpdate("__ObjConstLoc", AbsDataProp(AbsValue(BuiltinObject.loc)))
        .initializeUpdate("__ArrayConstLoc", AbsDataProp(AbsValue(BuiltinArray.loc)))

    val testHeap = st.heap.update(BuiltinGlobal.loc, testGlobalObj)
    State(testHeap, st.context)
  }
}
