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

import kr.ac.kaist.safe.analyzer.Semantics
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models.SemanticFun
import kr.ac.kaist.safe.analyzer.models.PredefLoc.SINGLE_PURE_LOCAL
import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGFunction, CFGEdgeExc, ModelBlock }
import kr.ac.kaist.safe.nodes.ir.IRModelFunc
import kr.ac.kaist.safe.nodes.ast.{ ASTNodeInfo, ModelFunc }
import kr.ac.kaist.safe.util.{ Loc, Recent, SystemLoc, Span }
import scala.collection.immutable.HashSet

case object BuiltinMath extends BuiltinModel {
  val CONSTRUCT_LOC: Loc = SystemLoc("MathConst", Recent)
  val ABS_CONST_LOC: Loc = SystemLoc("MathAbsConst", Recent)
  val ABS_PROTO_LOC: Loc = SystemLoc("MathAbsProto", Recent) // TODO remove after Context refactoring
  val prefix: String = "Math"

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val F = utils.absBool.False
    val T = utils.absBool.True

    val absUserFun: (Value, Heap, Semantics) => Value = (args: Value, h: Heap, sem: Semantics) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).abs
      Value(PValue(num)(utils))
    }
    val absFunc: CFGFunction = createCFGFunc("abs", absUserFun, cfg, utils)

    val mathConstructor = Obj.Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Math"))(utils))
      .update("@proto", PropValue(ObjectValue(Value(BuiltinObject.PROTO_LOC)(utils), F, F, F)))
      .update("@extensible", PropValue(T)(utils))
      .update("constructor", PropValue(ObjectValue(Value(CONSTRUCT_LOC)(utils), F, F, F)))
      .update("E", PropValue(PValue(utils.absNumber.alpha(2.7182818284590452354))(utils), F, F, F))
      .update("LN10", PropValue(PValue(utils.absNumber.alpha(2.302585092994046))(utils), F, F, F))
      .update("LN2", PropValue(PValue(utils.absNumber.alpha(0.6931471805599453))(utils), F, F, F))
      .update("LOG2E", PropValue(PValue(utils.absNumber.alpha(1.4426950408889634))(utils), F, F, F))
      .update("LOG10E", PropValue(PValue(utils.absNumber.alpha(0.4342944819032518))(utils), F, F, F))
      .update("PI", PropValue(PValue(utils.absNumber.alpha(3.1415926535897932))(utils), F, F, F))
      .update("SQRT1_2", PropValue(PValue(utils.absNumber.alpha(0.7071067811865476))(utils), F, F, F))
      .update("SQRT2", PropValue(PValue(utils.absNumber.alpha(1.4142135623730951))(utils), F, F, F))
      .update("abs", PropValue(ObjectValue(Value(ABS_CONST_LOC)(utils), T, F, T)))

    updateFunc(h, absFunc, ABS_CONST_LOC, ABS_PROTO_LOC, utils, BuiltinObject.PROTO_LOC)
      .update(CONSTRUCT_LOC, mathConstructor)
  }
}
