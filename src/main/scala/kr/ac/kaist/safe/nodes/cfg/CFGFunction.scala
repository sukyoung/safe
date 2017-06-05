/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.nodes.cfg

import scala.collection.mutable.{ HashMap => MHashMap, Map => MMap }
import kr.ac.kaist.safe.{ LINE_SEP, SIGNIFICANT_BITS }
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.NodeUtil
import kr.ac.kaist.safe.analyzer.models.SemanticFun

import spray.json._

case class CFGFunction(
    override val ir: IRNode,
    argumentsName: String,
    argVars: List[CFGId],
    localVars: List[CFGId],
    name: String,
    isUser: Boolean
) extends CFGNode(ir) {
  var id: FunctionId = 0 // XXX should be a value but for JS model for a while.

  val entry = Entry(this)
  val exit = Exit(this)
  val exitExc = ExitExc(this)

  private var bidCount: BlockId = 0
  def getBId: BlockId = bidCount

  // all blocks in this function
  private var blocks: List[CFGBlock] = List(exitExc, exit, entry)
  private var blockMap: MMap[BlockId, CFGBlock] = MHashMap(
    -1 -> entry,
    -2 -> exit,
    -3 -> exitExc
  )
  def getBlock(bid: BlockId): Option[CFGBlock] = blockMap.get(bid)
  def getAllBlocks: List[CFGBlock] = blocks

  // used when create CFGFunction from JSON
  var blockData: Vector[JsValue] = _
  var capturedData: Vector[JsValue] = _

  // append block
  private def addBlock(block: CFGBlock): CFGBlock = {
    bidCount += 1
    blocks ::= block
    blockMap(block.id) = block
    block
  }

  // create call
  def createCall(callInstCons: Call => CFGCallInst, retVar: CFGId): Call = {
    val call = Call(this, callInstCons, retVar)
    val afterCall = call.afterCall
    val afterCatch = call.afterCatch
    addBlock(call)
    addBlock(afterCall)
    addBlock(afterCatch)
    call
  }

  // create block
  def createBlock: NormalBlock = createBlock(NoLabel)
  def createBlock(label: LabelKind): NormalBlock = {
    val block = NormalBlock(this, label)
    addBlock(block)
    block
  }
  def createLoopHead: LoopHead = {
    val loopHead = LoopHead(this)
    addBlock(loopHead)
    loopHead
  }

  // create model block
  def createModelBlock(sem: SemanticFun): ModelBlock = {
    val block = ModelBlock(this, sem)
    addBlock(block)
    block
  }

  // TODO: PureLocal - may not need to distinguish Captured
  // captured variable list for each function
  private var captured: List[CFGId] = Nil
  def addCaptured(captId: CFGId): Unit = captured ::= captId
  def getCaptured: List[CFGId] = captured

  // equals
  override def equals(other: Any): Boolean = other match {
    case (func: CFGFunction) => func.id == id
    case _ => false
  }

  // toString
  override def toString: String = s"function[$id] $name"
  override def toString(indent: Int): String = {
    val pre = "  " * indent
    val s: StringBuilder = new StringBuilder
    s.append(pre).append(s"function[$id] ")
      .append(NodeUtil.isInternal(name) match {
        case true => name.dropRight(SIGNIFICANT_BITS)
        case false => name
      })
      .append(" {").append(LINE_SEP)
    blocks.reverseIterator.foreach {
      case Exit(_) | ExitExc(_) =>
      case block =>
        s.append(pre).append(block.toString(indent + 1)).append(LINE_SEP)
    }
    s.append(pre).append(exit.toString(indent + 1)).append(LINE_SEP)
    s.append(pre).append(exitExc.toString(indent + 1)).append(LINE_SEP)
    s.append(pre).append("}").append(LINE_SEP)
    s.toString
  }
}
