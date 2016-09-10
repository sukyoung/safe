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
    val (globalPureLocalEnv, _) = AbsDecEnvRec
      .newPureLocal(AbsValue(Null), AbsLoc(BuiltinGlobal.loc))
      .DeleteBinding("@return")

    val initHeap = Heap(HashMap(
      SystemLoc("Dummy", Old) -> AbsObjectUtil.Bot // TODO If delete, not working because not allowed update to bottom heap
    ))

    val initCtx = ExecContext(HashMap(
      PredefLoc.PURE_LOCAL -> globalPureLocalEnv,
      PredefLoc.COLLAPSED -> AbsDecEnvRec.Empty
    ), OldAddrSet.Empty)

    val modeledHeap = BuiltinGlobal.initHeap(initHeap, cfg)

    State(modeledHeap, initCtx)
  }

  def testState: State = {
    val st = state
    val globalObj = st.heap.getOrElse(BuiltinGlobal.loc, AbsObjectUtil.Empty)

    val boolBot = AbsBool.Bot

    val testGlobalObj =
      globalObj.update("__BOT", PropValue.Bot)
        .update("__TOP", PropValue(AbsDataProp.Top))
        .update("__UInt", PropValue(AbsNumber.UInt))
        .update("__Global", PropValue(AbsDataProp(AbsLoc(BuiltinGlobal.loc))))
        .update("__BoolTop", PropValue(AbsBool.Top))
        .update("__NumTop", PropValue(AbsNumber.Top))
        .update("__StrTop", PropValue(AbsString.Top))
        .update("__RefErrLoc", PropValue(AbsDataProp(AbsLoc(BuiltinReferenceError.loc))))
        .update("__RangeErrLoc", PropValue(AbsDataProp(AbsLoc(BuiltinRangeError.loc))))
        .update("__TypeErrLoc", PropValue(AbsDataProp(AbsLoc(BuiltinTypeError.loc))))
        .update("__URIErrLoc", PropValue(AbsDataProp(AbsLoc(BuiltinURIError.loc))))
        .update("__RefErrProtoLoc", PropValue(AbsDataProp(AbsLoc(BuiltinReferenceErrorProto.loc))))
        .update("__RangeErrProtoLoc", PropValue(AbsDataProp(AbsLoc(BuiltinRangeErrorProto.loc))))
        .update("__TypeErrProtoLoc", PropValue(AbsDataProp(AbsLoc(BuiltinTypeErrorProto.loc))))
        .update("__URIErrProtoLoc", PropValue(AbsDataProp(AbsLoc(BuiltinURIErrorProto.loc))))
        .update("__ErrProtoLoc", PropValue(AbsDataProp(AbsLoc(BuiltinErrorProto.loc))))
        .update("__ObjConstLoc", PropValue(AbsDataProp(AbsLoc(BuiltinObject.loc))))
        .update("__ArrayConstLoc", PropValue(AbsDataProp(AbsLoc(BuiltinArray.loc))))

    val testHeap = st.heap.update(BuiltinGlobal.loc, testGlobalObj)
    State(testHeap, st.context)
  }
}
