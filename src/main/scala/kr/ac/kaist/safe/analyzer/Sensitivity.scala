/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
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
    sem: Semantics,
    st: AbsState
  ): List[TracePartition]

  def toStringList: List[String]
}

////////////////////////////////////////////////////////////////////////////////
// no analysis sensitivity
////////////////////////////////////////////////////////////////////////////////
case object EmptyTP extends TracePartition {
  def next(
    from: CFGBlock,
    to: CFGBlock,
    edgeType: CFGEdgeType,
    sem: Semantics,
    st: AbsState
  ): List[EmptyTP.type] = List(EmptyTP)

  override def toString: String = s"Empty"

  def toStringList: List[String] = Nil
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
    sem: Semantics,
    st: AbsState
  ): List[ProductTP] =
    ltp.next(from, to, edgeType, sem, st).foldLeft(List[ProductTP]()) {
      case (list, l) =>
        rtp.next(from, to, edgeType, sem, st).map(ProductTP(l, _)) ++ list
    }

  override def toString: String = s"$ltp||$rtp"

  def toStringList: List[String] = ltp.toStringList ++ rtp.toStringList
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
    sem: Semantics,
    st: AbsState
  ): List[CallSiteContext] = List((from, to, edgeType) match {
    case (call: Call, _: Entry, CFGEdgeCall) =>
      CallSiteContext((call :: callsiteList).take(depth), depth)
    case _ => this
  })

  override def toString: String = callsiteList
    .map(call => s"${call.func.id}:${call.id}")
    .mkString("Call[", ",", "]")

  def toStringList: List[String] = callsiteList.reverse.map(call => {
    val func = call.func
    val fname = func.simpleName
    val fid = func.id
    val bid = call.id
    val span = call.span
    s"Call[$bid] of function[$fid] $fname @ $span"
  })
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
    sem: Semantics,
    st: AbsState
  ): List[LoopContext] = (from, to, edgeType) match {
    // function start
    case (entry: Entry, _, CFGEdgeNormal) =>
      entry.func.outBlocks.foreach(sem.addOutCtxt(_, this))
      sem.addOutCtxt(entry.func.exit, this)
      List(this)
    // continue
    case (_, head: LoopHead, CFGEdgeNormal) if from.outerLoop == Some(head) =>
      iterList match {
        case Nil => List(this)
        case LoopIter(head, iter) :: rest =>
          val (condV, _) = sem.V(head.cond, st)
          val newIter =
            if (TypeConversionHelper.ToBoolean(condV).isTop) iter
            else iter + 1
          val newCtxt = copy(iterList = LoopIter(head, math.min(newIter, maxIter)) :: rest)
          head.outBlocks.foreach(sem.addOutCtxt(_, newCtxt))
          List(newCtxt)
      }
    // loop entry
    case (_, head: LoopHead, CFGEdgeNormal) =>
      val newCtxt = copy(iterList = (LoopIter(head, 0) :: iterList).take(maxDepth))
      head.outBlocks.foreach(sem.addOutCtxt(_, newCtxt))
      List(newCtxt)
    case _ if to.isOutBlock =>
      val fromLoop = from.outerLoop
      val toLoop = to.outerLoop
      val dist: Int = getDist(toLoop, fromLoop)
      val result =
        if (dist == 0) List(this)
        else findPrev(sem, to, dist)
      result
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
    sem.getOutCtxtSet(block).filter {
      case LoopContext(iters, _, _) =>
        if (iterList.length < maxDepth) iters == reduced
        else iters.startsWith(reduced)
    }.toList
  }

  override def toString: String = iterList
    .map { case LoopIter(head, iter) => s"${head.func.id}:${head.id}($iter/$maxIter)" }
    .mkString("Loop[", ",", "]")

  def toStringList: List[String] = iterList.reverse.map(loop => {
    val head = loop.head
    val iter = loop.iter
    val bid = head.id
    val func = head.func
    val fid = func.id
    val fname = func.simpleName
    val span = head.span
    s"Loop[$bid] ($iter/$maxIter) function[$fid] $fname @ $span"
  })
}

case class LoopSensitivity(maxIter: Int, maxDepth: Int) extends Sensitivity {
  val initTP = LoopContext(Nil, maxIter, maxDepth)
  def isInsensitive: Boolean = maxDepth == 0
}
