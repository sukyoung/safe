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
  def state: State = {
    val afalse = AbsBool.alpha(false)

    val globalPureLocalEnv = DecEnvRecord.newPureLocal(AbsValue.alpha(Null), AbsLoc.alpha(BuiltinGlobal.loc)) - "@return"

    val initHeap = Heap(HashMap(
      SystemLoc("Dummy", Old) -> AbsObjectUtil.Bot // TODO If delete, not working because not allowed update to bottom heap
    ))

    val initCtx = ExecContext(HashMap(
      PredefLoc.PURE_LOCAL -> globalPureLocalEnv,
      PredefLoc.COLLAPSED -> DecEnvRecord.Empty
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
        .update("__TOP", PropValue(DataPropertyUtil(AbsPValue.Top)))
        .update("__UInt", PropValue(AbsNumber.UInt))
        .update("__Global", PropValue(DataPropertyUtil(BuiltinGlobal.loc)))
        .update("__BoolTop", PropValue(AbsBool.Top))
        .update("__NumTop", PropValue(AbsNumber.Top))
        .update("__StrTop", PropValue(AbsString.Top))
        .update("__RefErrLoc", PropValue(DataPropertyUtil(BuiltinReferenceError.loc)))
        .update("__RangeErrLoc", PropValue(DataPropertyUtil(BuiltinRangeError.loc)))
        .update("__TypeErrLoc", PropValue(DataPropertyUtil(BuiltinTypeError.loc)))
        .update("__URIErrLoc", PropValue(DataPropertyUtil(BuiltinURIError.loc)))
        .update("__RefErrProtoLoc", PropValue(DataPropertyUtil(BuiltinReferenceErrorProto.loc)))
        .update("__RangeErrProtoLoc", PropValue(DataPropertyUtil(BuiltinRangeErrorProto.loc)))
        .update("__TypeErrProtoLoc", PropValue(DataPropertyUtil(BuiltinTypeErrorProto.loc)))
        .update("__URIErrProtoLoc", PropValue(DataPropertyUtil(BuiltinURIErrorProto.loc)))
        .update("__ErrProtoLoc", PropValue(DataPropertyUtil(BuiltinErrorProto.loc)))
        .update("__ObjConstLoc", PropValue(DataPropertyUtil(BuiltinObject.loc)))
        .update("__ArrayConstLoc", PropValue(DataPropertyUtil(BuiltinArray.loc)))

    val testHeap = st.heap.update(BuiltinGlobal.loc, testGlobalObj)
    State(testHeap, st.context)
  }
}
