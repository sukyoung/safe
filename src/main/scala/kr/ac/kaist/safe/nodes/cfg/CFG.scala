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

package kr.ac.kaist.safe.nodes.cfg

import scala.collection.mutable.{ HashMap => MHashMap, Map => MMap }
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.ir.IRNode

class CFG(
    override val ir: IRNode,
    globalVars: List[CFGId]
) extends CFGNode(ir) {
  // cfg id
  val id: Int = CFG.getId

  // all functions / blocks in this cfg
  private var funcs: List[CFGFunction] = Nil
  def getAllFuncs: List[CFGFunction] = funcs
  def getAllBlocks: List[CFGBlock] = funcs.foldRight(List[CFGBlock]()) {
    case (func, lst) => func.getAllBlocks ++ lst
  }

  // function / block map from id
  private val funMap: MMap[FunctionId, CFGFunction] = MHashMap()
  def getFunc(fid: FunctionId): Option[CFGFunction] = funMap.get(fid)
  def getBlock(fid: FunctionId, bid: BlockId): Option[CFGBlock] =
    funMap.get(fid).fold[Option[CFGBlock]](None) { _.getBlock(bid) }

  private var fidCount: FunctionId = 0
  def getFId: FunctionId = fidCount

  // global function
  lazy val globalFunc: CFGFunction =
    createFunction("", Nil, globalVars, "top-level", ir, "", true)

  // create function
  def createFunction(
    argumentsName: String,
    argVars: List[CFGId],
    localVars: List[CFGId],
    name: String,
    ir: IRNode,
    body: String,
    isUser: Boolean
  ): CFGFunction = {
    val func: CFGFunction =
      new CFGFunction(ir, this, argumentsName, argVars, localVars, name, body, isUser)
    fidCount += 1
    funcs ::= func
    funMap(func.id) = func
    func
  }

  // add edge
  def addEdge(
    fromList: List[CFGBlock],
    toList: List[CFGBlock],
    etype: CFGEdgeType = CFGEdgeNormal
  ): Unit = {
    fromList.foreach((from) => toList.foreach((to) => {
      from.addSucc(etype, to)
      to.addPred(etype, from)
    }))
  }

  // toString
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    funcs.reverseIterator.foreach {
      case func => s.append(func.toString(indent)).append(LINE_SEP)
    }
    s.toString
  }
}

object CFG {
  private var idCount: Int = 0
  private def getId: Int = idCount
}
