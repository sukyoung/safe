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
import kr.ac.kaist.safe.util.{ NodeUtil, Loc, Address }

import scala.collection.immutable.HashSet

case class Initialize(cfg: CFG, helper: Helper) {
  val utils = helper.utils

  def state: State = {
    val afalse = utils.absBool.False

    val globalPureLocalObj = Obj.newPureLocalObj(Value(PValue(utils.absNull.Top)(utils)), HashSet(PredefLoc.GLOBAL))(utils) - "@return"

    val initHeap = Heap.Bot
      .update(PredefLoc.SINGLE_PURE_LOCAL, globalPureLocalObj)
      .update(PredefLoc.COLLAPSED, Obj.Empty(utils))

    val modeledHeap = BuiltinGlobal.initHeap(initHeap, cfg, utils)

    val heap = modeledHeap(BuiltinGlobal.loc) match {
      case Some(obj) => {
        val globalObj = obj
          .update(NodeUtil.GLOBAL_NAME, PropValue(ObjectValue(Value(PredefLoc.GLOBAL)(utils), afalse, afalse, afalse)))
          .update(NodeUtil.VAR_TRUE, PropValue(utils.absBool.alpha(true))(utils))
        modeledHeap.update(BuiltinGlobal.loc, globalObj)
      }
      case None => Heap.Bot
    }
    State(heap, Context.Empty)
  }

  def testState: State = {
    val st = state
    val globalObj = st.heap.getOrElse(PredefLoc.GLOBAL, Obj.Empty(utils))

    val boolBot = utils.absBool.Bot

    val testGlobalObj =
      globalObj.update("__BOT", PropValue.Bot(utils))
        .update("__TOP", PropValue(ObjectValue(Value(PValue.Top(utils)))(utils)))
        .update("__UInt", PropValue(utils.absNumber.UInt)(utils))
        .update("__Global", PropValue(ObjectValue(PredefLoc.GLOBAL)(utils)))
        .update("__BoolTop", PropValue(utils.absBool.Top)(utils))
        .update("__NumTop", PropValue(utils.absNumber.Top)(utils))
        .update("__StrTop", PropValue(utils.absString.Top)(utils))
        .update("__RefErrLoc", PropValue(ObjectValue(BuiltinReferenceError.loc)(utils)))
        .update("__RangeErrLoc", PropValue(ObjectValue(BuiltinRangeError.loc)(utils)))
        .update("__TypeErrLoc", PropValue(ObjectValue(BuiltinTypeError.loc)(utils)))
        .update("__URIErrLoc", PropValue(ObjectValue(BuiltinURIError.loc)(utils)))
        .update("__RefErrProtoLoc", PropValue(ObjectValue(BuiltinReferenceError.protoLoc)(utils)))
        .update("__RangeErrProtoLoc", PropValue(ObjectValue(BuiltinRangeError.protoLoc)(utils)))
        .update("__TypeErrProtoLoc", PropValue(ObjectValue(BuiltinTypeError.protoLoc)(utils)))
        .update("__URIErrProtoLoc", PropValue(ObjectValue(BuiltinURIError.protoLoc)(utils)))
        .update("__ErrProtoLoc", PropValue(ObjectValue(BuiltinError.protoLoc)(utils)))
        .update("__ObjConstLoc", PropValue(ObjectValue(BuiltinObject.loc)(utils)))
        .update("__ArrayConstLoc", PropValue(ObjectValue(BuiltinArray.loc)(utils)))

    val testHeap = st.heap.update(PredefLoc.GLOBAL, testGlobalObj)
    State(testHeap, st.context)
  }
}
