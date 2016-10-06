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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.nodes.cfg._

import scala.collection.immutable.HashMap

object Worklist {
  def compareNodeType(n1: CFGBlock, n2: CFGBlock): Boolean =
    (n1, n2) match {
      case (Entry(_), _) | (Exit(_), ExitExc(_)) => true
      case (_, Entry(_)) | (ExitExc(_), Exit(_)) => false
      case (_, Exit(_)) | (_, ExitExc(_)) => true
      case (Exit(_), _) | (ExitExc(_), _) => false
      case _ => n1.id < n2.id
    }

  def apply(cfg: CFG): Worklist = {
    val cfgBlockList: List[CFGBlock] = cfg.getAllBlocks.sortWith((n1, n2) => {
      if (n1.func.id == n2.func.id) compareNodeType(n1, n2)
      else n1.func.id < n2.func.id
    })
    val (orderMap, order) = cfgBlockList.foldLeft((HashMap[CFGBlock, Int](), 0)) {
      case ((tmpMap, tmpOrder), block) =>
        (tmpMap + (block -> tmpOrder), tmpOrder + 1)
    }
    new Worklist(orderMap)
  }
}

class Worklist(private var orderMap: Map[CFGBlock, Int]) {
  private var worklist: List[Work] = Nil

  def add(cp: ControlPoint): Unit = {
    val order = orderMap.getOrElse(cp.node, 0)
    insertWork(order, cp)
  }

  def insertWork(i: Int, cp: ControlPoint): Unit = {
    val newWork = Work(i, cp)
    if (worklist contains newWork) ()
    else worklist = (newWork :: worklist).sortWith((w1, w2) => w1 < w2)
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
    worklist.map(work => work.toString).reduce((s1, s2) => s1 + ", " + s2)
  }
}

case class Work(order: Int, cp: ControlPoint) {
  override def toString: String = s"(${cp.node.func.id}:${cp.node}, ${cp.callContext})"

  def <(that: Work): Boolean = {
    // 1. compare node's order
    if (this.order != that.order) this.order < that.order
    // 2. compare node's FunctionId
    else if (this.cp.node.func.id != that.cp.node.func.id) this.cp.node.func.id < that.cp.node.func.id
    else {
      // 3. compare node types
      Worklist.compareNodeType(this.cp.node, that.cp.node)
    }
  }
}