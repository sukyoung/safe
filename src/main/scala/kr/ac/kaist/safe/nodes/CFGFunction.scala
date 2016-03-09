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

import kr.ac.kaist.safe.safe_util.AddressManager
import scala.collection.mutable.{ Map => MMap, HashMap => MHashMap }

case class CFGFunction(cfg: CFG, argumentsName: String, argVars: List[CFGId], localVars: List[CFGId], name: String, info: Info, body: String, isUser: Boolean) {
  val id: FunctionId = CFGFunction.getId

  val entry = Entry(this)
  val exit = Exit(this)
  val exitExc = ExitExc(this)

  // all blocks in this function
  private var blocks: List[Block] = Nil
  def getBlocks: List[Block] = blocks
  def createBlock: Block = {
    val block: Block = Block(this)
    blocks ::= block
    cfg.blockMap(block.id) = block
    cfg.addNode(block) // TODO delete this after refactoring dump
    block
  }

  // TODO: PureLocal - may not need to distinguish Captured
  // captured variable list for each function
  private var captured: List[CFGId] = Nil
  def addCaptured(captId: CFGId): Unit = captured ::= captId
  def getCaptured: List[CFGId] = captured

  // #PureLocal location for each control point
  private var pureLocalMap: MMap[ControlPoint, Loc] = MHashMap()
  def getPureLocal(cp: ControlPoint): Loc = {
    pureLocalMap.getOrElseUpdate(cp, AddressManager.newRecentLoc("PureLocal#" + id + "#" + cp))
  }

  // merged #PureLocal location
  private var mergedPureLocal: Option[Loc] = None
  def getMergedPureLocal: Loc = {
    mergedPureLocal.getOrElse({
      val loc = AddressManager.newRecentLoc("PureLocal#" + id)
      mergedPureLocal = Some(loc)
      loc
    })
  }

  // equals
  override def equals(other: Any): Boolean = other match {
    case func: CFGFunction => (func.id == id)
    case _ => false
  }
}

object CFGFunction {
  private var counter = 0
  private def getId: Int = { counter += 1; counter - 1 }
  def resetId: Unit = counter = 0
}
