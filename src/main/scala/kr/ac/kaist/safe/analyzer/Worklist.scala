package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.nodes.{ CFGBlock, CFG }

import scala.collection.immutable.HashMap

object Worklist {
  def apply(cfg: CFG): Worklist = {
    val cfgBlockList: List[CFGBlock] = cfg.getAllBlocks
    val (orderMap, order) = cfgBlockList.foldLeft((HashMap[CFGBlock, Int](), 0))((res, block) => {
      val (tmpMap, tmpOrder) = res
      (tmpMap + (block -> tmpOrder), tmpOrder + 1)
    })
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
}

case class Work(order: Int, cp: ControlPoint)
