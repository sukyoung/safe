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
import kr.ac.kaist.safe.analyzer.models.{ Model, PredefLoc }
import kr.ac.kaist.safe.nodes.cfg.CFG

case object BuiltinGlobal extends BuiltinModel {
  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True
    val globalObj = h.getOrElse(PredefLoc.GLOBAL, Obj.Empty(utils))
      .update("@class", PropValue(utils.absString.alpha("Object"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinObject.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(utils.absBool.True)(utils))
      .update("NaN", PropValue(PValue(utils.absNumber.alpha(Double.NaN))(utils), afalse, afalse, afalse))
      .update("Infinity", PropValue(PValue(utils.absNumber.alpha(Double.PositiveInfinity))(utils), afalse, afalse, afalse))
      .update("undefined", PropValue(PValue(utils.absUndef.Top)(utils), afalse, afalse, afalse))
      .update("Object", PropValue(ObjectValue(Value(BuiltinObject.CONSTRUCT_LOC)(utils), atrue, afalse, atrue)))
      .update("Array", PropValue(ObjectValue(Value(BuiltinArray.CONSTRUCT_LOC)(utils), atrue, afalse, atrue)))
      .update("Function", PropValue(ObjectValue(Value(BuiltinFunction.CONSTRUCT_LOC)(utils), atrue, afalse, atrue)))
      .update("Boolean", PropValue(ObjectValue(Value(BuiltinBoolean.CONSTRUCT_LOC)(utils), atrue, afalse, atrue)))
      .update("Number", PropValue(ObjectValue(Value(BuiltinNumber.CONSTRUCT_LOC)(utils), atrue, afalse, atrue)))
      .update("String", PropValue(ObjectValue(Value(BuiltinString.CONSTRUCT_LOC)(utils), atrue, afalse, atrue)))
    h.update(PredefLoc.GLOBAL, globalObj)
  }
}
