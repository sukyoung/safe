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
import kr.ac.kaist.safe.config.Config

// cfg node
sealed abstract class CFGNode {
  val func: CFGFunction

  // edges incident with this cfg node
  protected val succs: MMap[EdgeType, List[CFGNode]] = MHashMap()
  protected val preds: MMap[EdgeType, List[CFGNode]] = MHashMap()
  def getSucc(edgeType: EdgeType): List[CFGNode] = succs.getOrElse(edgeType, Nil)
  def getPred(edgeType: EdgeType): List[CFGNode] = preds.getOrElse(edgeType, Nil)

  // add edge
  def addSucc(edgeType: EdgeType, node: CFGNode): Unit = succs(edgeType) = node :: succs.getOrElse(edgeType, Nil)
  def addPred(edgeType: EdgeType, node: CFGNode): Unit = preds(edgeType) = node :: preds.getOrElse(edgeType, Nil)
}
object CFGNode {
  implicit def node2nodelist(node: CFGNode): List[CFGNode] = List(node)
}

// entry, exit, exception exit
case class Entry(func: CFGFunction) extends CFGNode
case class Exit(func: CFGFunction) extends CFGNode
case class ExitExc(func: CFGFunction) extends CFGNode

// block trait
trait Block extends CFGNode

// call, after-call, after-catch
case class Call(func: CFGFunction) extends Block {
  private var iAfterCall: AfterCall = _
  private var iAfterCatch: AfterCatch = _
  private var iCallInst: CFGCallInst = _
  def afterCall: AfterCall = iAfterCall
  def afterCatch: AfterCatch = iAfterCatch
  def callInst: CFGCallInst = iCallInst
}
object Call {
  def apply(func: CFGFunction, callInstCons: Call => CFGCallInst, retVar: CFGId): Call = {
    val call = Call(func)
    call.iAfterCall = AfterCall(func, retVar, call)
    call.iAfterCatch = AfterCatch(func, call)
    call.iCallInst = callInstCons(call)
    call
  }
}
case class AfterCall(func: CFGFunction, retVar: CFGId, call: Call) extends Block
case class AfterCatch(func: CFGFunction, call: Call) extends Block

// block
case class NormalBlock(func: CFGFunction) extends Block {
  val id: BlockId = NormalBlock.getId

  // inst list
  private var insts: List[CFGNormalInst] = Nil
  def getInsts: List[CFGNormalInst] = insts

  // create inst
  def createInst(instCons: CFGNode => CFGNormalInst): CFGNormalInst = {
    val inst: CFGNormalInst = instCons(this)
    insts ::= inst
    inst
  }

  // equals
  override def equals(other: Any): Boolean = other match {
    case (block: NormalBlock) => (block.id == id)
    case _ => false
  }

  // toString
  override def toString: String = s"(${func.id},LBlock($id))"

  // dump node
  def dump: String = {
    var str: String = s"$this" + Config.LINE_SEP
    str += (preds.get(EdgeNormal) match {
      case Some(List(AfterCall(_, retVar, _))) => s"    [EDGE] after-call($retVar)" + Config.LINE_SEP
      case _ => ""
    })
    insts.length > Config.MAX_INST_PRINT_SIZE match {
      case true => str + "    A LOT!!! " + ((succs.get(EdgeNormal) match {
        case Some(List(call: Call)) => 1
        case _ => 0
      }) + insts.length) + " instructions are not printed here." + Config.LINE_SEP + Config.LINE_SEP
      case false =>
        insts.reverseIterator.foldLeft(str) {
          case (s, inst) => s + s"    [${inst.id}] $inst" + Config.LINE_SEP
        } + (succs.get(EdgeNormal) match {
          case Some(List(call: Call)) =>
            val inst = call.callInst
            s"    [${inst.id}] $inst" + Config.LINE_SEP
          case _ => ""
        }) + Config.LINE_SEP + Config.LINE_SEP
    }
  }
}
object NormalBlock {
  private var counter: Int = 0
  private def getId: Int = { counter += 1; counter - 1 }
  def resetId: Unit = counter = 0
}
