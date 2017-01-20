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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._

case class ControlPoint(
    block: CFGBlock,
    tracePartition: TracePartition
) {
  def getState: AbsState = block.getState(tracePartition)
  def setState(st: AbsState): Unit = block.setState(tracePartition, st)
  def next(to: CFGBlock, edgeType: CFGEdgeType): ControlPoint = {
    ControlPoint(to, tracePartition.next(block, to, edgeType))
  }
  override def toString: String = {
    val fid = block.func.id
    s"($fid:$block, $tracePartition)"
  }
}

sealed abstract class TracePartition {
  def next(from: CFGBlock, to: CFGBlock, edgeType: CFGEdgeType): TracePartition
  def *(other: TracePartition): ProductTP = ProductTP(this, other)
}

// Product of trace partitions
case class ProductTP(ltp: TracePartition, rtp: TracePartition) extends TracePartition {
  def next(from: CFGBlock, to: CFGBlock, edgeType: CFGEdgeType): ProductTP = {
    ProductTP(ltp.next(from, to, edgeType), rtp.next(from, to, edgeType))
  }
  override def toString: String = s"$ltp x $rtp"
}

// Call-Context Sensitivity
case class CallContext(depth: Int, callsiteList: List[Call]) extends TracePartition {
  def next(from: CFGBlock, to: CFGBlock, edgeType: CFGEdgeType): CallContext = (from, to, edgeType) match {
    case (call: Call, _: Entry, CFGEdgeCall) => CallContext(depth, (call :: callsiteList).take(depth))
    case _ => this
  }
  override def toString: String = callsiteList.mkString("Call[", ", ", "]")
}

// Loop Sensitivity
case class LoopContext(
    depth: Int,
    infoOpt: Option[LoopInfo],
    excOuter: Option[(LoopContext, NormalBlock)]
) extends TracePartition {
  def next(from: CFGBlock, to: CFGBlock, edgeType: CFGEdgeType): LoopContext = (from, to, edgeType) match {
    // function call
    case (_, _: Entry, CFGEdgeCall) => LoopContext(depth, None, None)
    // try start
    case (_, tryB @ NormalBlock(_, TryLabel), CFGEdgeNormal) => copy(excOuter = Some(this, tryB))
    // catch
    case (_, NormalBlock(_, CatchLabel), _) => excOuter match {
      case Some((e, _)) => e
      case None => this
    }
    // catch
    case (_, NormalBlock(_, FinallyLabel(tryBlock)), _) => excOuter match {
      case Some((e, tb)) if tb == tryBlock => e
      case _ => this
    }
    // loop
    case (_, loopHead: LoopHead, CFGEdgeNormal) => infoOpt match {
      // loop iteration
      case Some(info @ LoopInfo(l, k, outer)) if l == loopHead => {
        LoopContext(depth, Some(info.nextIter(depth)), excOuter)
      }
      // loop start
      case _ => LoopContext(depth, Some(LoopInfo(loopHead, 0, this)), excOuter)
    }
    // loop break
    case (_, NormalBlock(_, LoopBreakLabel), _) => infoOpt match {
      case Some(LoopInfo(_, _, outer)) => outer
      case None => this
    }
    // exception / return
    case (_, ExitExc(_) | Exit(_), _) => LoopContext(depth, None, None)
    case _ => this
  }
  override def toString: String = infoOpt match {
    case Some(LoopInfo(loopHead, k, outer)) => s"Loop($loopHead, $k/$depth)"
    case None => s"NoLoop"
  }
  override def equals(other: Any): Boolean = other match {
    case LoopContext(oDepth, oInfoOpt, _) if oDepth == depth => (infoOpt, oInfoOpt) match {
      case (None, None) => true
      case (Some(LoopInfo(llh, lk, _)), Some(LoopInfo(rlh, rk, _))) if llh == rlh && lk == rk => true
      case _ => false
    }
    case _ => false
  }
}

case class LoopInfo(loopHead: LoopHead, k: Int, outer: LoopContext) {
  def nextIter(depth: Int): LoopInfo = copy(k = Math.min(k + 1, depth))
}
