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

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.util.{ Recent, Loc, SystemLoc, Old }

object BuiltinError extends BuiltinModel {
  val ERR_PROTO_LOC: Loc = SystemLoc("ErrProto", Recent)
  val EVAL_ERR_PROTO_LOC: Loc = SystemLoc("EvalErrProto", Recent)
  val RANGE_ERR_PROTO_LOC: Loc = SystemLoc("RangeErrProto", Recent)
  val REF_ERR_PROTO_LOC: Loc = SystemLoc("RefErrProto", Recent)
  val SYNTAX_ERR_PROTO_LOC: Loc = SystemLoc("SyntaxErrProto", Recent)
  val TYPE_ERR_PROTO_LOC: Loc = SystemLoc("TypeErrProto", Recent)
  val URI_ERR_PROTO_LOC: Loc = SystemLoc("URIErrProto", Recent)

  val ERR_LOC: Loc = SystemLoc("Err", Old)
  val EVAL_ERR_LOC: Loc = SystemLoc("EvalErr", Old)
  val RANGE_ERR_LOC: Loc = SystemLoc("RangeErr", Old)
  val REF_ERR_LOC: Loc = SystemLoc("RefErr", Old)
  val SYNTAX_ERR_LOC: Loc = SystemLoc("SyntaxErr", Old)
  val TYPE_ERR_LOC: Loc = SystemLoc("TypeErr", Old)
  val URI_ERR_LOC: Loc = SystemLoc("URIErr", Old)

  def initHeap(h: Heap, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val errObj = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Error"))))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))

    val defaultErr = errObj
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(ERR_PROTO_LOC), afalse, afalse, afalse)))

    val evalErr = errObj
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(EVAL_ERR_PROTO_LOC), afalse, afalse, afalse)))

    val rangeErr = errObj
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(RANGE_ERR_PROTO_LOC), afalse, afalse, afalse)))

    val refErr = errObj
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(REF_ERR_PROTO_LOC), afalse, afalse, afalse)))

    val syntaxErr = errObj
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(SYNTAX_ERR_PROTO_LOC), afalse, afalse, afalse)))

    val typeErr = errObj
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(TYPE_ERR_PROTO_LOC), afalse, afalse, afalse)))

    val uriErr = errObj
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(URI_ERR_PROTO_LOC), afalse, afalse, afalse)))

    val errProtoObj = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Error"))))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(ERR_PROTO_LOC), afalse, afalse, afalse)))

    val defaultErrProto = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Error"))))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinObject.PROTO_LOC), afalse, afalse, afalse)))
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("Error")), atrue, afalse, atrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), atrue, afalse, atrue))

    val evalErrProto = errProtoObj
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("EvalError")), atrue, afalse, atrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), atrue, afalse, atrue))

    val rangeErrProto = errProtoObj
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("RangeError")), atrue, afalse, atrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), atrue, afalse, atrue))

    val refErrProto = errProtoObj
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("ReferenceError")), atrue, afalse, atrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), atrue, afalse, atrue))

    val syntaxErrProto = errProtoObj
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("SyntaxError")), atrue, afalse, atrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), atrue, afalse, atrue))

    val typeErrProto = errProtoObj
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("TypeError")), atrue, afalse, atrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), atrue, afalse, atrue))

    val uriErrProto = errProtoObj
      .update("name", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("URIError")), atrue, afalse, atrue))
      .update("message", PropValue(utils.PValueBot.copyWith(utils.absString.alpha("")), atrue, afalse, atrue))

    h.update(ERR_LOC, defaultErr)
      .update(EVAL_ERR_LOC, evalErr)
      .update(RANGE_ERR_LOC, rangeErr)
      .update(REF_ERR_LOC, refErr)
      .update(SYNTAX_ERR_LOC, syntaxErr)
      .update(TYPE_ERR_LOC, typeErr)
      .update(URI_ERR_LOC, uriErr)
      .update(ERR_PROTO_LOC, defaultErrProto)
      .update(EVAL_ERR_PROTO_LOC, evalErrProto)
      .update(RANGE_ERR_PROTO_LOC, rangeErrProto)
      .update(REF_ERR_PROTO_LOC, refErrProto)
      .update(SYNTAX_ERR_PROTO_LOC, syntaxErrProto)
      .update(TYPE_ERR_PROTO_LOC, typeErrProto)
      .update(URI_ERR_PROTO_LOC, uriErrProto)
  }
}
