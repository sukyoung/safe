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
import kr.ac.kaist.safe.util.{ Loc, Recent, SystemLoc }

object BuiltinFunction extends BuiltinModel {
  val PROTO_LOC: Loc = SystemLoc("FunctionProto", Recent)
  val CONSTRUCT_LOC = SystemLoc("FunctionConst", Recent)

  def initHeap(h: Heap, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val functionProto = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Function"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinObject.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      //      .update("@function", AbsInternalFunc("Function.prototype"))
      .update("constructor", PropValue(ObjectValue(utils.ValueBot.copyWith(CONSTRUCT_LOC), atrue, afalse, atrue)))
      .update("length", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(0)), afalse, afalse, afalse))

    val functionConstructor = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Function"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinFunction.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("@scope", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      //.update("@function", AbsInternalFunc("Function.constructor"))
      //.update("@construct", AbsInternalFunc("Function.constructor"))
      .update("@hasinstance", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("prototype", PropValue(ObjectValue(utils.ValueBot.copyWith(PROTO_LOC), afalse, afalse, afalse)))
      .update("length", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(1)), afalse, afalse, afalse))

    h.update(PROTO_LOC, functionProto)
      .update(CONSTRUCT_LOC, functionConstructor)
  }
}
