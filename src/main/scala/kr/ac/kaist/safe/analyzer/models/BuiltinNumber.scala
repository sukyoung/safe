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
      .update("@class", PropValue(utils.absString.alpha("Number"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinObject.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@primitive", PropValue(utils.absNumber.alpha(0))(utils))
      .update("constructor", PropValue(ObjectValue(Value(CONSTRUCT_LOC)(utils), afalse, afalse, afalse)))

    val numberConstructor = utils.ObjEmpty
      .update("@class", PropValue(utils.absString.alpha("Function"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinFunction.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@scope", PropValue(utils.absNull.Top)(utils))
      //.update("@function", AbsInternalFunc("Number"))
      //.update("@construct", AbsInternalFunc("Number.constructor"))
      .update("@hasinstance", PropValue(utils.absNull.Top)(utils))
      .update("prototype", PropValue(ObjectValue(Value(PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("length", PropValue(PValue(utils.absNumber.alpha(1))(utils), afalse, afalse, afalse))
      .update("MAX_VALUE", PropValue(PValue(utils.absNumber.alpha(Double.MaxValue))(utils), afalse, afalse, afalse))
      .update("MIN_VALUE", PropValue(PValue(utils.absNumber.alpha(Double.MinValue))(utils), afalse, afalse, afalse))
      .update("NaN", PropValue(PValue(utils.absNumber.alpha(Double.NaN))(utils), afalse, afalse, afalse))
      .update("NEGATIVE_INFINITY", PropValue(PValue(utils.absNumber.alpha(Double.NegativeInfinity))(utils), afalse, afalse, afalse))
      .update("POSITIVE_INFINITY", PropValue(PValue(utils.absNumber.alpha(Double.PositiveInfinity))(utils), afalse, afalse, afalse))

    h.update(PROTO_LOC, numberProto)
      .update(CONSTRUCT_LOC, numberConstructor)
  }
}