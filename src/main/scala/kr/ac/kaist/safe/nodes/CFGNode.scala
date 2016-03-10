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

import scala.collection.mutable.{ Map => MMap, HashMap => MHashMap }
import kr.ac.kaist.safe.nodes.EdgeType._

// cfg node
sealed abstract class CFGNode {
  val func: CFGFunction

  // edges incident with this cfg node
  private val succs: MMap[EdgeType, List[CFGNode]] = MHashMap()
  private val preds: MMap[EdgeType, List[CFGNode]] = MHashMap()
  def getSucc(edgeType: EdgeType): List[CFGNode] = succs.getOrElse(edgeType, Nil)
  def getPred(edgeType: EdgeType): List[CFGNode] = preds.getOrElse(edgeType, Nil)

  // add edge
  def addSucc(edgeType: EdgeType, node: CFGNode): Unit = succs(edgeType) = node :: succs.getOrElse(edgeType, Nil)
  def addPred(edgeType: EdgeType, node: CFGNode): Unit = preds(edgeType) = node :: preds.getOrElse(edgeType, Nil)
}
object CFGNode {
  // TODO: used to simplify addEdge of cfg; modify if there's a better way
  implicit def node2nodelist(node: CFGNode): List[CFGNode] = List(node)
}

case class Entry(func: CFGFunction) extends CFGNode
case class Exit(func: CFGFunction) extends CFGNode
case class ExitExc(func: CFGFunction) extends CFGNode

// TODO: consider using mutable insts
case class Block(func: CFGFunction) extends CFGNode {
  val id: BlockId = Block.getId

  // inst list
  private var insts: List[CFGInst] = Nil
  def getInsts: List[CFGInst] = insts

  // call info
  private var callOpt: Option[Call] = None
  private var afterCallOpt: Option[AfterCall] = None
  private var afterCatchOpt: Option[AfterCatch] = None
  def getCall: Option[Call] = callOpt
  def getAfterCall: Option[AfterCall] = afterCallOpt
  def getAfterCatch: Option[AfterCatch] = afterCatchOpt

  // change into call block / link with new after-call(catch) block
  def createCall(callInstCons: CFGNode => CFGCallInst, retVar: CFGId): Call = {
    val (aftercall, aftercatch) = (func.createBlock, func.createBlock)
    val callInst = createInst(callInstCons).asInstanceOf[CFGCallInst]
    val call = Call(callInst, aftercall, aftercatch)
    callOpt = Some(call)
    aftercall.afterCallOpt = Some(AfterCall(retVar, this))
    aftercatch.afterCatchOpt = Some(AfterCatch(this))
    call
  }

  // create inst
  def createInst(instCons: CFGNode => CFGInst): CFGInst = {
    val inst: CFGInst = instCons(this)
    insts ::= inst
    inst
  }

  // equals
  override def equals(other: Any): Boolean = other match {
    case block: Block => (block.id == id)
    case _ => false
  }

  // toString
  override def toString: String = s"(${func.id},LBlock($id))"

  // dump node
  def dump: String = {
    var str: String = s"$this\n"
    str += (afterCallOpt match {
      case Some(AfterCall(retVar, _)) => s"    [EDGE] after-call($retVar)\n"
      case None => ""
    })
    str = insts.reverseIterator.foldLeft(str) {
      case (s, inst) => s + s"    [${inst.id}] $inst\n"
    }
    str + "\n\n"
  }
}
object Block {
  private var counter: Int = 0
  private def getId: Int = { counter += 1; counter - 1 }
  def resetId: Unit = counter = 0
}

case class Call(callInst: CFGCallInst, afterCall: Block, afterCatch: Block)
case class AfterCall(retVar: CFGId, call: Block)
case class AfterCatch(call: Block)
