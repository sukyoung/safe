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

object BuiltinString extends BuiltinModel {
  val PROTO_LOC: Loc = SystemLoc("StringProto", Recent)
  val CONSTRUCT_LOC: Loc = SystemLoc("StringConst", Recent)

  def initHeap(h: Heap, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val stringProto = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("String"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinObject.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("constructor", PropValue(ObjectValue(utils.ValueBot.copyWith(CONSTRUCT_LOC), afalse, afalse, afalse)))

    val stringConstructor = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Function"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinFunction.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("@scope", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      //.update("@function", AbsInternalFunc("String"))
      //.update("@construct",  AbsInternalFunc("String.constructor"))
      .update("@hasinstance", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("prototype", PropValue(ObjectValue(utils.ValueBot.copyWith(PROTO_LOC), afalse, afalse, afalse)))
      .update("length", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(1)), afalse, afalse, afalse))

    h.update(PROTO_LOC, stringProto)
      .update(CONSTRUCT_LOC, stringConstructor)
  }
}
