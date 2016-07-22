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
  private val prefix: String = "Math"

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap = {
    val F = utils.absBool.False
    val T = utils.absBool.True

    var heap: Heap = h

    val absSem: SemanticFun = (sem, st) => st match {
      case State(heap, ctx) => {
        val stBotPair = (State.Bot, State.Bot)
        val name: String = "abs"
        val funName: String = s"$prefix.$name"
        val argsName: String = s"<>arguments<>$funName"
        heap(SINGLE_PURE_LOCAL) match {
          case Some(localObj) => {
            localObj(argsName) match {
              case Some(pv) => {
                val locSet = pv.objval.value.locset
                val resPV = locSet.foldLeft(PropValue.Bot(utils)) {
                  case (prev, loc) => {
                    heap(loc) match {
                      case Some(argsObj) => argsObj("0") match {
                        case Some(arg) => prev + arg
                        case None => prev
                      }
                      case None => prev
                    }
                  }
                }
                val prim = resPV.objval.value.toPrimitiveBetter(heap, "Number")(utils)
                val num = prim.toAbsNumber(utils.absNumber).abs
                val retPV = PropValue(num)(utils)
                val retObj = localObj.update("@return", retPV)
                val retHeap = heap.update(SINGLE_PURE_LOCAL, retObj)
                (State(retHeap, ctx), State.Bot)
              }
              case None => stBotPair // TODO dead code
            }
          }
          case None => stBotPair // TODO dead code
        }
      }
    }

    val absFunc: CFGFunction = {
      val name: String = "abs"
      // TODO check whether full function name or just name
      val funName: String = s"$prefix.$name"
      // TODO is it best way? how about optional IR in CFGNode
      val argsName: String = s"<>arguments<>$funName"
      val ir: IRModelFunc = IRModelFunc(ModelFunc(ASTNodeInfo(Span(funName))))
      val func: CFGFunction = cfg.createFunction(argsName, Nil, Nil, funName, ir, "", false)
      val modelBlock: ModelBlock = func.createModelBlock(absSem)
      cfg.addEdge(func.entry, modelBlock)
      cfg.addEdge(modelBlock, func.exit)
      cfg.addEdge(modelBlock, func.exitExc, CFGEdgeExc)
      func
    }

    val absFunObj: Obj = {
      val n = utils.absNumber.alpha(0)
      val scope = Value.Bot(utils)
      val fVal = Value(PValue.Bot(utils), HashSet(ABS_CONST_LOC))
      val fPropV = PropValue(ObjectValue(fVal, T, F, T))

      val const = Obj
        .newFunctionObject(absFunc.id, scope, ABS_PROTO_LOC, n)(utils)

      val proto = Obj
        .newObject(BuiltinObject.PROTO_LOC)(utils)
        .update("constructor", fPropV, exist = true)

      heap = heap
        .update(ABS_CONST_LOC, const)
        .update(ABS_PROTO_LOC, proto)

      const
    }

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

    heap.update(CONSTRUCT_LOC, mathConstructor)
  }
}
