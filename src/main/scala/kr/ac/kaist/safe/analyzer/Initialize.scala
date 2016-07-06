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

  private def initObj(protoLoc: Loc, className: String): Obj = {
    val absFalse = utils.absBool.False
    val protoVal = Value(utils.PValueBot, HashSet(protoLoc))
    utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha(className))))
      .update("@proto", PropValue(ObjectValue(protoVal, absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(utils.absBool.True)))
  }

  def state: State = {
    val globalPureLocalObj = helper.newPureLocal(Value(utils.PValueBot.copyWith(utils.absNull.Top)), HashSet(PredefLoc.GLOBAL)) - "@return"

    val absFalse = utils.absBool.False
    val absTrue = utils.absBool.True
    val globalObjV = ObjectValue(Value(utils.PValueBot, HashSet(PredefLoc.GLOBAL)), absFalse, absFalse, absFalse)
    val globalObj = helper.newObject(ProtoLoc.OBJ)
      .update(NodeUtil.GLOBAL_NAME, PropValue(globalObjV))
      .update(NodeUtil.VAR_TRUE, PropValue(utils.ObjectValueBot.copyWith(utils.absBool.alpha(true))))
      .update("NaN", PropValue(utils.PValueBot.copyWith(utils.absNumber.NaN), absFalse, absFalse, absFalse))
      .update("Infinity", PropValue(utils.PValueBot.copyWith(utils.absNumber.PosInf), absFalse, absFalse, absFalse))
      .update("undefined", PropValue(utils.PValueBot.copyWith(utils.absUndef.Top), absFalse, absFalse, absFalse))
      .update("Object", PropValue(ObjectValue(Value(utils.PValueBot, HashSet(ConstructorLoc.OBJ)), absTrue, absFalse, absTrue)))
      .update("Array", PropValue(ObjectValue(Value(utils.PValueBot, HashSet(ConstructorLoc.ARRAY)), absTrue, absFalse, absTrue)))

    //TODO need modeling for initial values of Proto
    val protoObjProto = utils.ObjectValueBot.copyWith(utils.absNull.Top)
    val objPtoro = helper.newObject()
      .update("@proto", PropValue(protoObjProto))
      .update("constructor", PropValue(ObjectValue(Value(utils.PValueBot, HashSet(ConstructorLoc.OBJ)), absTrue, absFalse, absTrue)))

    val functionProto = initObj(ProtoLoc.FUNCTION, "Function")
    val arrayProto = initObj(ProtoLoc.OBJ, "Array")
    val booleanProto = initObj(ProtoLoc.OBJ, "Boolean")
    val numberProto = initObj(ProtoLoc.OBJ, "Number")
    val stringProto = initObj(ProtoLoc.OBJ, "String")

    val err = initObj(ProtoLoc.ERR, "Error")
    val evalErr = initObj(ProtoLoc.EVAL_ERR, "Error")
    val rangeErr = initObj(ProtoLoc.RANGE_ERR, "Error")
    val refErr = initObj(ProtoLoc.REF_ERR, "Error")
    val syntaxErr = initObj(ProtoLoc.SYNTAX_ERR, "Error")
    val typeErr = initObj(ProtoLoc.TYPE_ERR, "Error")
    val uriErr = initObj(ProtoLoc.URI_ERR, "Error")

    val errProto = initObj(ProtoLoc.OBJ, "Error")
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("Error")), absTrue, absFalse, absTrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), absTrue, absFalse, absTrue))
    val evalErrProto = initObj(ProtoLoc.ERR, "Error")
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("EvalError")), absTrue, absFalse, absTrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), absTrue, absFalse, absTrue))
    val rangeErrProto = initObj(ProtoLoc.ERR, "Error")
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("RangeError")), absTrue, absFalse, absTrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), absTrue, absFalse, absTrue))
    val refErrProto = initObj(ProtoLoc.ERR, "Error")
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("ReferenceError")), absTrue, absFalse, absTrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), absTrue, absFalse, absTrue))
    val syntaxErrProto = initObj(ProtoLoc.ERR, "Error")
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("SyntaxError")), absTrue, absFalse, absTrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), absTrue, absFalse, absTrue))
    val typeErrProto = initObj(ProtoLoc.ERR, "Error")
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("TypeError")), absTrue, absFalse, absTrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), absTrue, absFalse, absTrue))
    val uriErrProto = initObj(ProtoLoc.ERR, "Error")
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("URIError")), absTrue, absFalse, absTrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), absTrue, absFalse, absTrue))

    val objConstructor = initObj(ProtoLoc.FUNCTION, "Function")
      .update("@scope", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("@hasinstance", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("prototype", PropValue(ObjectValue(Value(utils.PValueBot, HashSet(ProtoLoc.OBJ)), absFalse, absFalse, absFalse)))
      .update("length", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(1)), absFalse, absFalse, absFalse))

    val arrayConstructor = initObj(ProtoLoc.OBJ, "Function")
      .update("@scope", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("@hasinstance", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("prototype", PropValue(ObjectValue(Value(utils.PValueBot, HashSet(ProtoLoc.ARRAY)), absFalse, absFalse, absFalse)))
      .update("length", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(1)), absFalse, absFalse, absFalse))

    val initHeap = Heap.Bot
      .update(PredefLoc.SINGLE_PURE_LOCAL, globalPureLocalObj)
      .update(PredefLoc.GLOBAL, globalObj)
      .update(PredefLoc.COLLAPSED, utils.ObjEmpty)

      .update(ProtoLoc.OBJ, objPtoro)
      .update(ProtoLoc.FUNCTION, functionProto)
      .update(ProtoLoc.ARRAY, arrayProto)
      .update(ProtoLoc.BOOLEAN, booleanProto)
      .update(ProtoLoc.NUMBER, numberProto)
      .update(ProtoLoc.STRING, stringProto)

      .update(ErrorLoc.ERR, err)
      .update(ErrorLoc.EVAL_ERR, evalErr)
      .update(ErrorLoc.RANGE_ERR, rangeErr)
      .update(ErrorLoc.REF_ERR, refErr)
      .update(ErrorLoc.SYNTAX_ERR, syntaxErr)
      .update(ErrorLoc.TYPE_ERR, typeErr)
      .update(ErrorLoc.URI_ERR, uriErr)

      .update(ProtoLoc.ERR, errProto)
      .update(ProtoLoc.EVAL_ERR, evalErrProto)
      .update(ProtoLoc.RANGE_ERR, rangeErrProto)
      .update(ProtoLoc.REF_ERR, refErrProto)
      .update(ProtoLoc.SYNTAX_ERR, syntaxErrProto)
      .update(ProtoLoc.TYPE_ERR, typeErrProto)
      .update(ProtoLoc.URI_ERR, uriErrProto)

      .update(ConstructorLoc.OBJ, objConstructor)
      .update(ConstructorLoc.ARRAY, arrayConstructor)

    State(initHeap, Context.Empty)
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
        .update("__RefErrLoc", PropValue(utils.ObjectValueBot.copyWith(ErrorLoc.REF_ERR)))
        .update("__RangeErrLoc", PropValue(utils.ObjectValueBot.copyWith(ErrorLoc.RANGE_ERR)))
        .update("__TypeErrLoc", PropValue(utils.ObjectValueBot.copyWith(ErrorLoc.TYPE_ERR)))
        .update("__URIErrLoc", PropValue(utils.ObjectValueBot.copyWith(ErrorLoc.URI_ERR)))
        .update("__RefErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(ProtoLoc.REF_ERR)))
        .update("__RangeErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(ProtoLoc.RANGE_ERR)))
        .update("__TypeErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(ProtoLoc.TYPE_ERR)))
        .update("__URIErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(ProtoLoc.URI_ERR)))
        .update("__ErrProtoLoc", PropValue(utils.ObjectValueBot.copyWith(ProtoLoc.ERR)))
        .update("__ObjConstLoc", PropValue(utils.ObjectValueBot.copyWith(ConstructorLoc.OBJ)))
        .update("__ArrayConstLoc", PropValue(utils.ObjectValueBot.copyWith(ConstructorLoc.OBJ))) //TODO check tests/js/semantics/load11.js

    val testHeap = st.heap.update(PredefLoc.GLOBAL, testGlobalObj)
    State(testHeap, st.context)
  }
}
