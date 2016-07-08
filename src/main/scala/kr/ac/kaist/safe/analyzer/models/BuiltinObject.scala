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

object BuiltinObject extends BuiltinModel {
  val PROTO_LOC: Loc = SystemLoc("ObjProto", Recent)
  val CONSTRUCT_LOC: Loc = SystemLoc("ObjectConst", Recent)

  def initHeap(h: Heap, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val objPtoro = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Object"))))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("@proto", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("constructor", PropValue(ObjectValue(utils.ValueBot.copyWith(CONSTRUCT_LOC), atrue, afalse, atrue)))

    val objConstructor = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Function"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinFunction.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(utils.absBool.True)))
      .update("@scope", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("@hasinstance", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      //.update("@function", AbsInternalFunc("Object"))
      //.update("@construct", AbsInternalFunc("Object.constructor"))
      .update("prototype", PropValue(ObjectValue(utils.ValueBot.copyWith(PROTO_LOC), afalse, afalse, afalse)))
      .update("length", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(1)), afalse, afalse, afalse))

    h.update(PROTO_LOC, objPtoro)
      .update(CONSTRUCT_LOC, objConstructor)
  }
}
