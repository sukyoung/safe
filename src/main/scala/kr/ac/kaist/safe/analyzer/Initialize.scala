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
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap, HashSet }

case class Initialize(cfg: CFG, helper: Helper) {
  val utils = helper.utils
  val pvalueU = utils.pvalue
  val valueU = utils.value
  val dataPropU = utils.dataProp
  val objU = utils.absObject

  def state: State = {
    val afalse = utils.absBool.False

    val globalPureLocalEnv = DecEnvRecord.newPureLocal(valueU.alpha(null), HashSet(BuiltinGlobal.loc))(utils) - "@return"

    val initHeap = Heap(HashMap(
      SystemLoc("Dummy", Old) -> objU.Bot // TODO If delete, not working because not allowed update to bottom heap
    ))

    val initCtx = ExecContext(HashMap(
      PredefLoc.PURE_LOCAL -> globalPureLocalEnv,
      PredefLoc.COLLAPSED -> DecEnvRecord.Empty
    ), OldAddrSet.Empty)

    val modeledHeap = BuiltinGlobal.initHeap(initHeap, cfg, utils)

    State(modeledHeap, initCtx)
  }

  def testState: State = {
    val st = state
    val globalObj = st.heap.getOrElse(BuiltinGlobal.loc, objU.Empty)

    val boolBot = utils.absBool.Bot

    val testGlobalObj =
      globalObj.update("__BOT", PropValue.Bot(utils))
        .update("__TOP", PropValue(dataPropU(pvalueU.Top)))
        .update("__UInt", PropValue(utils.absNumber.UInt)(utils))
        .update("__Global", PropValue(dataPropU(BuiltinGlobal.loc)))
        .update("__BoolTop", PropValue(utils.absBool.Top)(utils))
        .update("__NumTop", PropValue(utils.absNumber.Top)(utils))
        .update("__StrTop", PropValue(utils.absString.Top)(utils))
        .update("__RefErrLoc", PropValue(dataPropU(BuiltinReferenceError.loc)))
        .update("__RangeErrLoc", PropValue(dataPropU(BuiltinRangeError.loc)))
        .update("__TypeErrLoc", PropValue(dataPropU(BuiltinTypeError.loc)))
        .update("__URIErrLoc", PropValue(dataPropU(BuiltinURIError.loc)))
        .update("__RefErrProtoLoc", PropValue(dataPropU(BuiltinReferenceErrorProto.loc)))
        .update("__RangeErrProtoLoc", PropValue(dataPropU(BuiltinRangeErrorProto.loc)))
        .update("__TypeErrProtoLoc", PropValue(dataPropU(BuiltinTypeErrorProto.loc)))
        .update("__URIErrProtoLoc", PropValue(dataPropU(BuiltinURIErrorProto.loc)))
        .update("__ErrProtoLoc", PropValue(dataPropU(BuiltinErrorProto.loc)))
        .update("__ObjConstLoc", PropValue(dataPropU(BuiltinObject.loc)))
        .update("__ArrayConstLoc", PropValue(dataPropU(BuiltinArray.loc)))

    val testHeap = st.heap.update(BuiltinGlobal.loc, testGlobalObj)
    State(testHeap, st.context)
  }
}
