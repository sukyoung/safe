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

import kr.ac.kaist.safe.analyzer.Semantics
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models.PredefLoc.SINGLE_PURE_LOCAL
import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGFunction, CFGEdgeExc, ModelBlock }
import kr.ac.kaist.safe.nodes.ir.IRModelFunc
import kr.ac.kaist.safe.nodes.ast.{ ASTNodeInfo, ModelFunc }
import kr.ac.kaist.safe.util.{ Loc, Span }
import scala.collection.immutable.HashSet

abstract class Model {
  protected val prefix: String

  def initHeap(h: Heap, cfg: CFG, utils: Utils): Heap

  protected def updateFunc(h: Heap, func: CFGFunction, constLoc: Loc, protoLoc: Loc, utils: Utils, prototypeLoc: Loc): Heap = {
    val n = utils.absNumber.alpha(0)
    val scope = Value.Bot(utils)
    val fVal = Value(PValue.Bot(utils), HashSet(constLoc))
    val F = utils.absBool.False
    val T = utils.absBool.True
    val fPropV = PropValue(ObjectValue(fVal, T, F, T))

    val const = Obj
      .newFunctionObject(func.id, scope, protoLoc, n)(utils)

    val proto = Obj
      .newObject(prototypeLoc)(utils)
      .update("constructor", fPropV, exist = true)

    h.update(constLoc, const)
      .update(protoLoc, proto)
  }

  protected def createSemanticFunc(funName: String, argsName: String, userFunction: (Value, Heap, Semantics) => Value, utils: Utils): SemanticFun = (sem, st) => st match {
    case State(heap, ctx) => {
      val stBotPair = (State.Bot, State.Bot)
      heap(SINGLE_PURE_LOCAL) match {
        case Some(localObj) => {
          localObj(argsName) match {
            case Some(pv) => {
              val retV = userFunction(pv.objval.value, heap, sem)
              val retObj = localObj.update("@return", PropValue(ObjectValue(retV)(utils)))
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

  protected def createCFGFunc(name: String, userFunction: (Value, Heap, Semantics) => Value, cfg: CFG, utils: Utils): CFGFunction = {
    val funName: String = s"$prefix.$name"
    val argsName: String = s"<>arguments<>$funName"
    val ir: IRModelFunc = IRModelFunc(ModelFunc(ASTNodeInfo(Span(funName))))
    val func: CFGFunction = cfg.createFunction(argsName, Nil, Nil, funName, ir, "", false)
    val sem: SemanticFun = createSemanticFunc(funName, argsName, userFunction, utils)
    val modelBlock: ModelBlock = func.createModelBlock(sem)
    cfg.addEdge(func.entry, modelBlock)
    cfg.addEdge(modelBlock, func.exit)
    cfg.addEdge(modelBlock, func.exitExc, CFGEdgeExc)
    func
  }

}
