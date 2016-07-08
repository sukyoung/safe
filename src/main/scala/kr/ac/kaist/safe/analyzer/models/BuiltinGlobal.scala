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

object BuiltinGlobal extends BuiltinModel {
  def initHeap(h: Heap, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True
    val globalObj = h.getOrElse(PredefLoc.GLOBAL, utils.ObjEmpty)
      .update("@class", PropValue(utils.ObjectValueBot.copyWith(utils.absString.alpha("Object"))))
      .update("@proto", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinObject.PROTO_LOC), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.ObjectValueBot.copyWith(utils.absBool.True)))
      .update("NaN", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(Double.NaN)), afalse, afalse, afalse))
      .update("Infinity", PropValue(utils.PValueBot.copyWith(utils.absNumber.alpha(Double.PositiveInfinity)), afalse, afalse, afalse))
      .update("undefined", PropValue(utils.PValueBot.copyWith(utils.absUndef.Top), afalse, afalse, afalse))
      .update("Object", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinObject.CONSTRUCT_LOC), atrue, afalse, atrue)))
      .update("Array", PropValue(ObjectValue(utils.ValueBot.copyWith(BuiltinArray.CONSTRUCT_LOC), atrue, afalse, atrue)))
    h.update(PredefLoc.GLOBAL, globalObj)
  }
}
