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

object BuiltinNumber extends BuiltinModel {
  val PROTO_LOC: Loc = SystemLoc("NumberProto", Recent)
  val CONSTRUCT_LOC: Loc = SystemLoc("NumberConst", Recent)

  def initHeap(h: Heap, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val numberProto = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Number"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinObject.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("@primitive", PropValue(utils.ObjectValueBot.copyWith(utils.absNumber.alpha(0))))
      .update("constructor", PropValue(ObjectValue(utils.ValueBot.copyWith(CONSTRUCT_LOC), afalse, afalse, afalse)))

    val numberConstructor = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Function"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinFunction.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(atrue)))
      .update("@scope", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      //.update("@function", AbsInternalFunc("Number"))
      //.update("@construct", AbsInternalFunc("Number.constructor"))
      .update("@hasinstance", PropValue(utils.ObjectValueBot.copyWith(utils.absNull.Top)))
      .update("prototype", PropValue(ObjectValue(utils.ValueBot.copyWith(PROTO_LOC), afalse, afalse, afalse)))
      .update("length", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(1)), afalse, afalse, afalse))
      .update("MAX_VALUE", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(Double.MaxValue)), afalse, afalse, afalse))
      .update("MIN_VALUE", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(Double.MinValue)), afalse, afalse, afalse))
      .update("NaN", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(Double.NaN)), afalse, afalse, afalse))
      .update("NEGATIVE_INFINITY", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(Double.NegativeInfinity)), afalse, afalse, afalse))
      .update("POSITIVE_INFINITY", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(Double.PositiveInfinity)), afalse, afalse, afalse))

    h.update(PROTO_LOC, numberProto)
      .update(CONSTRUCT_LOC, numberConstructor)
  }
}