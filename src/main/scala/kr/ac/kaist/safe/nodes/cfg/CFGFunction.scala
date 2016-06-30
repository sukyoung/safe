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
import kr.ac.kaist.safe.config.Config
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.NodeUtil

class CFGFunction(
    override val ir: IRNode,
    val cfg: CFG,
    val argumentsName: String,
    val argVars: List[CFGId],
    val localVars: List[CFGId],
    val name: String,
    val body: String,
    val isUser: Boolean
) extends CFGNode(ir) {
  val id: FunctionId = cfg.getFId

  val entry = Entry(this)
  val exit = Exit(this)
  val exitExc = ExitExc(this)

  private var bidCount: BlockId = 0
  def getBId: BlockId = bidCount

  // create call
  def createCall(callInstCons: Call => CFGCallInst, retVar: CFGId): Call = {
    val call = Call(this, callInstCons, retVar)
    bidCount += 3
    blocks = call.afterCatch :: call.afterCall :: call :: blocks
    call
  }

  // all blocks in this function
  private var blocks: List[CFGBlock] = List(exitExc, exit, entry)
  private var blockMap: MMap[BlockId, CFGBlock] = MHashMap(
    -1 -> entry,
    -2 -> exit,
    -3 -> exitExc
  )
  def getBlock(bid: BlockId): Option[CFGBlock] = blockMap.get(bid)
  def getAllBlocks: List[CFGBlock] = blocks

  // create block
  def createBlock: NormalBlock = {
    val block = NormalBlock(this)
    bidCount += 1
    blocks ::= block
    blockMap(block.id) = block
    block
  }

  // TODO: PureLocal - may not need to distinguish Captured
  // captured variable list for each function
  private var captured: List[CFGId] = Nil
  def addCaptured(captId: CFGId): Unit = captured ::= captId
  def getCaptured: List[CFGId] = captured

  // equals
  override def equals(other: Any): Boolean = other match {
    case func: CFGFunction => (func.id == id)
    case _ => false
  }

  // toString
  override def toString: String = s"function[$id] $name"
  override def toString(indent: Int): String = {
    val pre = "  " * indent
    val s: StringBuilder = new StringBuilder
    s.append(pre).append(s"function[$id] ")
      .append(NodeUtil.isInternal(name) match {
        case true => name.dropRight(Config.SIGNIFICANT_BITS)
        case false => name
      })
      .append(" {").append(Config.LINE_SEP)
    blocks.reverseIterator.foreach {
      case Exit(_) | ExitExc(_) =>
      case block =>
        s.append(pre).append(block.toString(indent + 1)).append(Config.LINE_SEP)
    }
    s.append(pre).append(exit.toString(indent + 1)).append(Config.LINE_SEP)
    s.append(pre).append(exitExc.toString(indent + 1)).append(Config.LINE_SEP)
    s.append(pre).append("}").append(Config.LINE_SEP)
    s.toString
  }
}
