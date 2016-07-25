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

package kr.ac.kaist.safe.analyzer.models.builtin

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models.Model
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util.{ Recent, Loc, SystemLoc, Old }

case object BuiltinError extends BuiltinModel {
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

  val prefix: String = "Error"

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val errObj = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Error"))(utils))
      .update("@extensible", PropValue(atrue)(utils))

    val defaultErr = errObj
      .update("@proto", PropValue(ObjectValue(Value(ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val evalErr = errObj
      .update("@proto", PropValue(ObjectValue(Value(EVAL_ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val rangeErr = errObj
      .update("@proto", PropValue(ObjectValue(Value(RANGE_ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val refErr = errObj
      .update("@proto", PropValue(ObjectValue(Value(REF_ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val syntaxErr = errObj
      .update("@proto", PropValue(ObjectValue(Value(SYNTAX_ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val typeErr = errObj
      .update("@proto", PropValue(ObjectValue(Value(TYPE_ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val uriErr = errObj
      .update("@proto", PropValue(ObjectValue(Value(URI_ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val errProtoObj = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Error"))(utils))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@proto", PropValue(ObjectValue(Value(ERR_PROTO_LOC)(utils), afalse, afalse, afalse)))

    val defaultErrProto = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Error"))(utils))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinObject.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("name", PropValue(PValue(utils.absString.alpha("Error"))(utils), atrue, afalse, atrue))
      .update("message", PropValue(PValue(utils.absString.alpha(""))(utils), atrue, afalse, atrue))

    val evalErrProto = errProtoObj
      .update("name", PropValue(PValue(utils.absString.alpha("EvalError"))(utils), atrue, afalse, atrue))
      .update("message", PropValue(PValue(utils.absString.alpha(""))(utils), atrue, afalse, atrue))

    val rangeErrProto = errProtoObj
      .update("name", PropValue(PValue(utils.absString.alpha("RangeError"))(utils), atrue, afalse, atrue))
      .update("message", PropValue(PValue(utils.absString.alpha(""))(utils), atrue, afalse, atrue))

    val refErrProto = errProtoObj
      .update("name", PropValue(PValue(utils.absString.alpha("ReferenceError"))(utils), atrue, afalse, atrue))
      .update("message", PropValue(PValue(utils.absString.alpha(""))(utils), atrue, afalse, atrue))

    val syntaxErrProto = errProtoObj
      .update("name", PropValue(PValue(utils.absString.alpha("SyntaxError"))(utils), atrue, afalse, atrue))
      .update("message", PropValue(PValue(utils.absString.alpha(""))(utils), atrue, afalse, atrue))

    val typeErrProto = errProtoObj
      .update("name", PropValue(PValue(utils.absString.alpha("TypeError"))(utils), atrue, afalse, atrue))
      .update("message", PropValue(PValue(utils.absString.alpha(""))(utils), atrue, afalse, atrue))

    val uriErrProto = errProtoObj
      .update("name", PropValue(PValue(utils.absString.alpha("URIError"))(utils), atrue, afalse, atrue))
      .update("message", PropValue(PValue(utils.absString.alpha(""))(utils), atrue, afalse, atrue))

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
