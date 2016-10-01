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
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir.IRModelFunc
import kr.ac.kaist.safe.nodes.ast.{ ASTNodeInfo, ModelFunc }
import kr.ac.kaist.safe.util.{ Address, Span, SystemAddr }

abstract class Code {
  val argLen: Int = 0
  def getCFGFunc(cfg: CFG, name: String): CFGFunction
  protected def createCFGFunc(
    cfg: CFG,
    name: String
  ): (String, String, CFGFunction) = {
    val funName: String = s"[]$name"
    val argsName: String = s"<>arguments<>$funName"
    val ir: IRModelFunc = IRModelFunc(ModelFunc(ASTNodeInfo(Span(funName))))
    val func: CFGFunction =
      cfg.createFunction(argsName, Nil, Nil, funName, ir, "", false)
    (funName, argsName, func)
  }
}

class EmptyCode(
    override val argLen: Int = 0
) extends Code {
  def getCFGFunc(cfg: CFG, name: String): CFGFunction = {
    val (_, _, func) = createCFGFunc(cfg, name)
    cfg.addEdge(func.entry, func.exit)
    func
  }
}
object EmptyCode {
  def apply(argLen: Int = 0): EmptyCode = new EmptyCode(argLen)
}

class BasicCode(
    override val argLen: Int = 0,
    code: (AbsValue, State) => (State, State, AbsValue)
) extends Code {
  def getCFGFunc(cfg: CFG, name: String): CFGFunction = {
    val (funName, argsName, func) = createCFGFunc(cfg, name)
    val sem: SemanticFun = createSemanticFunc(argsName)
    val modelBlock: ModelBlock = func.createModelBlock(sem)
    cfg.addEdge(func.entry, modelBlock)
    cfg.addEdge(modelBlock, func.exit)
    cfg.addEdge(modelBlock, func.exitExc, CFGEdgeExc)
    func
  }

  private def createSemanticFunc(argsName: String): SemanticFun = st => st match {
    case State(heap, context) => {
      val stBotPair = (State.Bot, State.Bot)
      val localEnv = context.pureLocal.record.decEnvRec
      val (argV, _) = localEnv.GetBindingValue(argsName)
      val (retSt, retSte, retV) = code(argV, st)
      val (retObj, _) = localEnv.SetMutableBinding("@return", retV)
      val retCtx = retSt.context.subsPureLocal(AbsLexEnv(retObj))
      (State(retSt.heap, retCtx), retSte)
    }
  }
}
object BasicCode {
  def apply(
    argLen: Int = 0,
    code: (AbsValue, State) => (State, State, AbsValue)
  ): BasicCode = new BasicCode(argLen, code)
}

class CallCode(
    override val argLen: Int = 0,
    funcId: CFGId, thisId: CFGId, argsId: CFGId, retId: CFGId,
    beforeCallCode: (AbsValue, State, Address) => (State, State),
    afterCallCode: (AbsValue, State) => (State, State, AbsValue)
) extends Code {
  def getCFGFunc(cfg: CFG, name: String): CFGFunction = {
    val (funName, argsName, func) = createCFGFunc(cfg, name)
    val beforeSem: SemanticFun = createBeforeFunc(argsName, cfg.newProgramAddr)
    val beforeBlock: ModelBlock = func.createModelBlock(beforeSem)

    val callBlock: Call = func.createCall(
      CFGCall(func.ir, _, CFGVarRef(func.ir, funcId), CFGVarRef(func.ir, thisId), CFGVarRef(func.ir, argsId), SystemAddr(name + "<BuiltinCall>1"), SystemAddr(name + "<BuiltinCall>2")),
      retId
    )

    val afterSem: SemanticFun = createAfterFunc(argsName)
    val afterBlock: ModelBlock = func.createModelBlock(afterSem)

    cfg.addEdge(func.entry, beforeBlock)
    cfg.addEdge(beforeBlock, callBlock)
    cfg.addEdge(callBlock.afterCall, afterBlock)
    cfg.addEdge(afterBlock, func.exit)
    cfg.addEdge(callBlock.afterCatch, func.exitExc, CFGEdgeExc)
    func
  }

  private def createBeforeFunc(argsName: String, newAddr: Address): SemanticFun = st => st match {
    case State(heap, context) => {
      val localEnv = context.pureLocal.record.decEnvRec
      val (argV, _) = localEnv.GetBindingValue(argsName)
      val (newSt, newExcSt) = beforeCallCode(argV, st, newAddr)
      (newSt, newExcSt)
    }
  }

  private def createAfterFunc(argsName: String): SemanticFun = st => st match {
    case State(heap, context) => {
      val localEnv = context.pureLocal.record.decEnvRec
      val (argV, _) = localEnv.GetBindingValue(argsName)
      val (newSt, newExcSt, retV) = afterCallCode(argV, st)
      val (retObj, _) = localEnv.SetMutableBinding("@return", retV)
      val retCtx = newSt.context.subsPureLocal(AbsLexEnv(retObj))
      (State(newSt.heap, retCtx), newExcSt)
    }
  }
}

object CallCode {
  def apply(
    argLen: Int = 0,
    funcId: CFGId, thisId: CFGId, argsId: CFGId, retId: CFGId,
    beforeCallCode: (AbsValue, State, Address) => (State, State),
    afterCallCode: (AbsValue, State) => (State, State, AbsValue)
  ): CallCode = new CallCode(argLen, funcId, thisId, argsId, retId, beforeCallCode, afterCallCode)
}

class PureCode(
  override val argLen: Int = 0,
  code: (AbsValue, State) => AbsValue
) extends BasicCode(argLen, (v: AbsValue, st: State) => {
  (st, State.Bot, code(v, st))
})
object PureCode {
  def apply(
    argLen: Int = 0,
    code: (AbsValue, State) => AbsValue
  ): PureCode = new PureCode(argLen, code)
}
