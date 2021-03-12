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

case class ControlPoint(
    block: CFGBlock,
    tracePartition: TracePartition
) {
  def next(
    to: CFGBlock,
    edgeType: CFGEdgeType,
    sem: Semantics,
    st: AbsState
  ): List[ControlPoint] = {
    tracePartition.next(block, to, edgeType, sem, st).map(ControlPoint(to, _))
  }
  override def toString: String = {
    val fid = block.func.id
    s"($fid:$block, $tracePartition)"
  }
}
