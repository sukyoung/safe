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
import kr.ac.kaist.safe.util.{ NodeUtil, Loc, Address }

import scala.collection.immutable.HashSet

case class Initialize(helper: Helper) {
  val utils = helper.utils

  def state: State = {
    val afalse = utils.absBool.False

    val globalPureLocalObj = Obj.newPureLocalObj(Value(utils.PValueBot.copyWith(utils.absNull.Top)), HashSet(PredefLoc.GLOBAL))(utils) - "@return"

    val globalObj = utils.ObjEmpty
      .update(NodeUtil.GLOBAL_NAME, PropValue(ObjectValue(utils.ValueBot.copyWith(PredefLoc.GLOBAL), afalse, afalse, afalse)))
      .update(NodeUtil.VAR_TRUE, PropValue(utils.ObjectValueBot.copyWith(utils.absBool.alpha(true))))

    val initHeap = Heap.Bot
      .update(PredefLoc.SINGLE_PURE_LOCAL, globalPureLocalObj)
      .update(PredefLoc.GLOBAL, globalObj)
      .update(PredefLoc.COLLAPSED, utils.ObjEmpty)

    val modeledHeap = BuiltinModel.models.foldLeft(initHeap)((h, m) => m.initHeap(h, utils))
    State(modeledHeap, Context.Empty)
  }

  def testState: State = {

    val st = state
    val globalObj = st.heap.getOrElse(PredefLoc.GLOBAL, utils.ObjEmpty)

    val testGlobalObj =
      globalObj.update("__BOT", utils.PropValueBot)
        .update("__TOP", PropValue(utils.ObjectValueBot.copyWith(Value(utils.PValueTop))))
        .update("__UInt", PropValue(utils.ObjectValueBot.copyWith(utils.absNumber.UInt)))
        .update("__Global", PropValue(utils.ObjectValueBot.copyWith(PredefLoc.GLOBAL)))
        .update("__BoolTop", PropValue(utils.ObjectValueBot.copyWith(utils.absBool.Top)))
        .update("__NumTop", PropValue(utils.ObjectValueBot.copyWith(utils.absNumber.Top)))
        .update("__StrTop", PropValue(utils.ObjectValueBot.copyWith(utils.absString.Top)))
        .update("__RefErrLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.REF_ERR_LOC)))
        .update("__RangeErrLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.RANGE_ERR_LOC)))
        .update("__TypeErrLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.TYPE_ERR_LOC)))
        .update("__URIErrLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.URI_ERR_LOC)))
        .update("__RefErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.REF_ERR_PROTO_LOC)))
        .update("__RangeErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.RANGE_ERR_PROTO_LOC)))
        .update("__TypeErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.TYPE_ERR_PROTO_LOC)))
        .update("__URIErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.URI_ERR_PROTO_LOC)))
        .update("__ErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinError.ERR_PROTO_LOC)))
        .update("__ObjConstLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinObject.CONSTRUCT_LOC)))
        .update("__ArrayConstLoc", PropValue(utils.ObjectValueBot.copyWith(BuiltinArray.CONSTRUCT_LOC)))

    val testHeap = st.heap.update(PredefLoc.GLOBAL, testGlobalObj)
    State(testHeap, st.context)
  }
}
