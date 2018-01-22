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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP
import scala.collection.immutable.HashMap

case class Worklist(cfg: CFG) {
  def init(entryCP: ControlPoint): Unit = {
    worklist = Nil
    add(entryCP)
  }

  // work
  case class Work(order: Int, cp: ControlPoint) {
    def <(that: Work): Boolean = this.order < that.order
    override def toString: String = cp.toString
  }

  // work list
  private var worklist: List[Work] = Nil

  def getWorklist: List[Work] = worklist

  // order map for blocks
  private val orderMap: Map[CFGBlock, Int] = {
    val cfgBlockList: List[CFGBlock] = cfg.getAllBlocks.sortWith((b1, b2) => {
      if (b1.func.id == b2.func.id) compareBlockType(b1, b2)
      else b1.func.id < b2.func.id
    })
    val (orderMap, _) = cfgBlockList.foldLeft((HashMap[CFGBlock, Int](), 0)) {
      case ((tmpMap, tmpOrder), block) =>
        (tmpMap + (block -> tmpOrder), tmpOrder + 1)
    }
    orderMap
  }

  // get order of block
  private def getOrder(block: CFGBlock): Int = orderMap.getOrElse(block, 0)

  // compare types of blocks
  def compareBlockType(b1: CFGBlock, b2: CFGBlock): Boolean =
    (b1, b2) match {
      case (Entry(_), _) | (Exit(_), ExitExc(_)) => true
      case (_, Entry(_)) | (ExitExc(_), Exit(_)) => false
      case (_, Exit(_)) | (_, ExitExc(_)) => true
      case (Exit(_), _) | (ExitExc(_), _) => false
      case _ => b1.id < b2.id
    }

  // add control point to work list
  def add(cp: ControlPoint): Unit = {
    val order = getOrder(cp.block)
    val newWork = Work(order, cp)
    if (!(worklist contains newWork))
      worklist = (newWork :: worklist).sortWith((w1, w2) => w1 < w2)
  }

  def isEmpty: Boolean = {
    worklist.isEmpty
  }

  def pop: ControlPoint = {
    val removedHead = worklist.head.cp
    worklist = worklist.tail
    removedHead
  }

  def head: ControlPoint = worklist.head.cp

  def getOrderMap: Map[CFGBlock, Int] = orderMap

  override def toString: String = {
    worklist.map(work => work.toString).mkString(LINE_SEP)
  }

  def has(block: CFGBlock): Boolean = worklist.exists {
    case Work(_, cp) => cp.block == block
  }
}
