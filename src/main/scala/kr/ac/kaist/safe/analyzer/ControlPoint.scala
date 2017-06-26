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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._

case class ControlPoint(
    block: CFGBlock,
    tracePartition: TracePartition
) {
  def next(to: CFGBlock, edgeType: CFGEdgeType): ControlPoint = {
    ControlPoint(to, tracePartition.next(block, to, edgeType))
  }
  override def toString: String = {
    val fid = block.func.id
    s"($fid:$block, $tracePartition)"
  }
}
