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
import kr.ac.kaist.safe.analyzer.models.PredefLoc.SINGLE_PURE_LOCAL
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.nodes.cfg.CFGEdgeType
import kr.ac.kaist.safe.util.{ Loc, Recent, SystemLoc }

case object BuiltinMath extends BuiltinModel {
  val PROTO_LOC: Loc = SystemLoc("MathProto", Recent)
  val CONSTRUCT_LOC: Loc = SystemLoc("MathConst", Recent)

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val afalse = utils.absBool.False
    val atrue = utils.absBool.True

    val mathProto = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Math"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinObject.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("constructor", PropValue(ObjectValue(Value(CONSTRUCT_LOC)(utils), afalse, afalse, afalse)))
      .update("E", PropValue(PValue(utils.absNumber.alpha(2.7182818284590452354))(utils), afalse, afalse, afalse))
      .update("LN10", PropValue(PValue(utils.absNumber.alpha(2.302585092994046))(utils), afalse, afalse, afalse))
      .update("LN2", PropValue(PValue(utils.absNumber.alpha(0.6931471805599453))(utils), afalse, afalse, afalse))
      .update("LOG2E", PropValue(PValue(utils.absNumber.alpha(1.4426950408889634))(utils), afalse, afalse, afalse))
      .update("LOG10E", PropValue(PValue(utils.absNumber.alpha(0.4342944819032518))(utils), afalse, afalse, afalse))
      .update("PI", PropValue(PValue(utils.absNumber.alpha(3.1415926535897932))(utils), afalse, afalse, afalse))
      .update("SQRT1_2", PropValue(PValue(utils.absNumber.alpha(0.7071067811865476))(utils), afalse, afalse, afalse))
      .update("SQRT2", PropValue(PValue(utils.absNumber.alpha(1.4142135623730951))(utils), afalse, afalse, afalse))

    val mathConstructor = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Function"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinFunction.PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("@extensible", PropValue(atrue)(utils))
      .update("@scope", PropValue(utils.absNull.Top)(utils))
      //.update("@function", AbsInternalFunc("Boolean"))
      //.update("@construct", AbsInternalFunc("Boolean.constructor"))
      .update("@hasinstance", PropValue(utils.absNull.Top)(utils))
      .update("prototype", PropValue(ObjectValue(Value(PROTO_LOC)(utils), afalse, afalse, afalse)))
      .update("length", PropValue(PValue(utils.absNumber.alpha(1))(utils), afalse, afalse, afalse))

    h.update(PROTO_LOC, mathProto)
      .update(CONSTRUCT_LOC, mathConstructor)
  }
}
