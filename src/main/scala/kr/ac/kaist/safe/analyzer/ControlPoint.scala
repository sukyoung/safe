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
}

case class CallContext(depth: Int, callsiteList: List[Call]) extends TracePartition {
  def next(from: CFGBlock, to: CFGBlock, edgeType: CFGEdgeType): TracePartition = (from, to, edgeType) match {
    case (call: Call, _: Entry, CFGEdgeCall) => CallContext(depth, (call :: callsiteList).take(depth))
    case _ => this
  }

  override def toString: String = callsiteList.mkString(", ")
}

