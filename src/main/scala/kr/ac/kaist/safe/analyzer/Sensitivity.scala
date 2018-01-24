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

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._

// analysis sensitivity
sealed trait Sensitivity {
  val initTP: TracePartition

  def *(that: Sensitivity): Sensitivity =
    if (this.isInsensitive) that
    else if (that.isInsensitive) this
    else ProductSensitivity(this, that)

  def isInsensitive: Boolean
}

// trace partition
trait TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType,
    sem: Semantics
  ): List[TracePartition]
}

////////////////////////////////////////////////////////////////////////////////
// no analysis sensitivity
////////////////////////////////////////////////////////////////////////////////
case object EmptyTP extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType,
    sem: Semantics
  ): List[EmptyTP.type] = List(EmptyTP)
  override def toString: String = s"Empty"
}

////////////////////////////////////////////////////////////////////////////////
// product of analysis sensitivities
////////////////////////////////////////////////////////////////////////////////
case class ProductTP(
    ltp: TracePartition,
    rtp: TracePartition
) extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType,
    sem: Semantics
  ): List[ProductTP] =
    ltp.next(from, to, edgeType, sem).foldLeft(List[ProductTP]()) {
      case (list, l) =>
        rtp.next(from, to, edgeType, sem).map(ProductTP(l, _)) ++ list
    }
  override def toString: String = s"$ltp x $rtp"
}

case class ProductSensitivity(
    lsens: Sensitivity,
    rsens: Sensitivity
) extends Sensitivity {
  val initTP = ProductTP(lsens.initTP, rsens.initTP)
  def isInsensitive: Boolean = lsens.isInsensitive && rsens.isInsensitive
}

////////////////////////////////////////////////////////////////////////////////
// call-site sensitivity
////////////////////////////////////////////////////////
case class CallSiteContext(callsiteList: List[Call], depth: Int) extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType,
    sem: Semantics
  ): List[CallSiteContext] = List((from, to, edgeType) match {
    case (call: Call, _: Entry, CFGEdgeCall) =>
      CallSiteContext((call :: callsiteList).take(depth), depth)
    case _ => this
  })
  override def toString: String = callsiteList match {
    case Nil => "NoCall"
    case _ => callsiteList
      .map(call => s"${call.func.id}:${call.id}")
      .mkString("Call[", ", ", "]")
  }
}

case class CallSiteSensitivity(depth: Int) extends Sensitivity {
  val initTP = CallSiteContext(Nil, depth)
  def isInsensitive: Boolean = depth == 0
}

////////////////////////////////////////////////////////////////////////////////
// loop sensitivity (unrolling)
////////////////////////////////////////////////////////////////////////////////
case class LoopIter(
  head: LoopHead,
  iter: Int
)

case class LoopContext(
    iterList: List[LoopIter],
    maxIter: Int,
    maxDepth: Int
) extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType,
    sem: Semantics
  ): List[LoopContext] = (from, to, edgeType) match {
    // function start
    case (entry: Entry, _, CFGEdgeNormal) =>
      entry.func.breakBlocks.foreach(sem.addBreakCtxt(_, this))
      sem.addBreakCtxt(entry.func.exit, this)
      List(this)
    // continue
    case (_, head: LoopHead, CFGEdgeNormal) if from.outerLoop == Some(head) =>
      iterList match {
        case Nil => List(this)
        case LoopIter(head, iter) :: rest =>
          val newCtxt = copy(iterList = LoopIter(head, math.min(iter + 1, maxIter)) :: rest)
          head.breakBlocks.foreach(sem.addBreakCtxt(_, newCtxt))
          List(newCtxt)
      }
    // loop entry
    case (_, head: LoopHead, CFGEdgeNormal) =>
      val newCtxt = copy(iterList = (LoopIter(head, 0) :: iterList).take(maxDepth))
      head.breakBlocks.foreach(sem.addBreakCtxt(_, newCtxt))
      List(newCtxt)
    // break
    case (_, break @ NormalBlock(_, LoopBreakLabel | UserLabel(_)), CFGEdgeNormal) =>
      val fromLoop = from.outerLoop
      val breakLoop = break.outerLoop
      val dist: Int = getDist(breakLoop, fromLoop)
      if (dist == 0) List(this)
      else findPrev(sem, break, dist)
    // return
    case (_, exit @ Exit(_), CFGEdgeNormal) =>
      val fromLoop = from.outerLoop
      val dist: Int = getDist(None, fromLoop)
      if (dist == 0) List(this)
      else findPrev(sem, exit, dist)
    case _ => List(this)
  }
  private def getDist(target: Option[LoopHead], cur: Option[LoopHead], diff: Int = 0): Int = {
    if (target == cur) diff
    else cur match {
      case Some(head) => getDist(target, head.outerLoop, diff + 1)
      case None => diff
    }
  }
  private def findPrev(sem: Semantics, block: CFGBlock, dist: Int): List[LoopContext] = {
    val reduced = iterList.drop(dist)
    sem.getBreakCtxtSet(block).filter {
      case LoopContext(iters, _, _) =>
        if (iterList.length < maxIter) iters == reduced
        else iters.startsWith(reduced)
    }.toList
  }

  override def toString: String = iterList match {
    case Nil => "NoLoop"
    case _ => iterList
      .map { case LoopIter(head, iter) => s"${head.func.id}:${head.id}($iter/$maxIter)" }
      .mkString("Loop[", ", ", "]")
  }
}

case class LoopSensitivity(maxIter: Int, maxDepth: Int) extends Sensitivity {
  val initTP = LoopContext(Nil, maxIter, maxDepth)
  def isInsensitive: Boolean = maxDepth == 0
}
