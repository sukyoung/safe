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

package kr.ac.kaist.safe.nodes

import kr.ac.kaist.safe.nodes.EdgeType._
import scala.collection.mutable.{ Map => MMap, HashMap => MHashMap }

class CFG(globalVars: List[CFGId], info: Info) {
  // global function
  val glboalFId: FunctionId = 0
  val globalFunc: CFGFunction = createFunction("", Nil, globalVars, "top-level", info, "", true)

  // access methods -----------------------------------------------------------
  val funMap: MMap[FunctionId, CFGFunction] = MHashMap(0 -> globalFunc)
  val blockMap: MMap[BlockId, Block] = MHashMap()

  // all functions in this cfg
  private var userFuncs: List[CFGFunction] = Nil
  private var modelFuncs: List[CFGFunction] = Nil
  def getUserFuncs: List[CFGFunction] = userFuncs
  def getModelFuncs: List[CFGFunction] = modelFuncs

  // TODO: delete this after refactoring dump
  // all blocks in this cfg
  private var blocks: List[Block] = Nil
  def addNode(block: Block): Unit = blocks ::= block

  // create function
  def createFunction(argumentsName: String, argVars: List[CFGId], localVars: List[CFGId],
    name: String, info: Info, body: String, isUser: Boolean): CFGFunction = {
    val func: CFGFunction =
      CFGFunction(this, argumentsName, argVars, localVars, name, info, body, isUser)
    funMap(func.id) = func
    isUser match {
      case true => userFuncs ::= func
      case false => modelFuncs ::= func
    }
    return func
  }

  // add edge
  def addEdge(fromList: List[CFGNode], toList: List[CFGNode], etype: EdgeType = EdgeNormal): Unit = {
    fromList.foreach((from) => toList.foreach((to) => {
      from.addSucc(etype, to)
      to.addPred(etype, from)
    }))
  }

  // dump cfg
  def dump(): Unit = {
    for (block <- blocks) block.dump
  }

  // init id counter
  CFGFunction.resetId
  Block.resetId
  CFGInst.resetId
}

class InternalError(msg: String) extends RuntimeException(msg)
class MaxLocCountError(msg: String = "") extends RuntimeException(msg)
