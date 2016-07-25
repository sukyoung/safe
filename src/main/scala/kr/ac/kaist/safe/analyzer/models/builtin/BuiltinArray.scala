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
import kr.ac.kaist.safe.util.{ Loc, Recent, SystemLoc }

case object BuiltinArray extends BuiltinModel {
  val PROTO_LOC: Loc = SystemLoc("ArrayProto", Recent)
  val CONSTRUCT_LOC: Loc = SystemLoc("ArrayConst", Recent)
  val prefix: String = "Array"

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val arrayProto = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Array"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinObject.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("constructor", PropValue(ObjectValue(Value(CONSTRUCT_LOC)(utils), afalse, afalse, afalse)))

    val arrayConstructor = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Function"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinFunction.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@scope", PropValue(utils.absNull.Top)(utils))
      .update("@hasinstance", PropValue(utils.absNull.Top)(utils))
      .update("prototype", PropValue(ObjectValue(Value(PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("length", PropValue(PValue(utils.absNumber.alpha(1))(utils), afalse, afalse, afalse))

    h.update(PROTO_LOC, arrayProto)
      .update(CONSTRUCT_LOC, arrayConstructor)
  }
}
