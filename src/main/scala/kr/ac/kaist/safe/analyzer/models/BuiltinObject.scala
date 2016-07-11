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
      .update("@class", PropValue(utils.absString.alpha("Object"))((utils)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@proto", PropValue(utils.absNull.Top)(utils))
      .update("constructor", PropValue(ObjectValue(Value(CONSTRUCT_LOC)(utils), atrue, afalse, atrue)))

    val objConstructor = utils.ObjEmpty
      .update("@class", PropValue(utils.absString.alpha("Function"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinFunction.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@scope", PropValue(utils.absNull.Top)(utils))
      .update("@hasinstance", PropValue(utils.absNull.Top)(utils))
      //.update("@function", AbsInternalFunc("Object"))
      //.update("@construct", AbsInternalFunc("Object.constructor"))
      .update("prototype", PropValue(ObjectValue(Value(PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("length", PropValue(PValue(utils.absNumber.alpha(1))(utils), afalse, afalse, afalse))

    h.update(PROTO_LOC, objPtoro)
      .update(CONSTRUCT_LOC, objConstructor)
  }
}
