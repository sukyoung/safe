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
  def apply(cfg: CFG): Worklist = {
    val cfgBlockList: List[CFGBlock] = cfg.getAllBlocks.sortWith((b1, b2) => {
      if (b1.func.id == b2.func.id) {
        (b1, b2) match {
          case (Entry(_), _) | (Exit(_), ExitExc(_)) => true
          case (_, Entry(_)) | (ExitExc(_), Exit(_)) => false
          case (_, Exit(_)) | (_, ExitExc(_)) => true
          case (Exit(_), _) | (ExitExc(_), _) => false
          case _ => b1.id < b2.id
        }
      } else b1.func.id < b2.func.id
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
    worklist = (Work(i, cp) :: worklist).sortWith((w1, w2) => w1.order < w2.order)
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
}
