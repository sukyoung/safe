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

package kr.ac.kaist.safe.nodes

// edge type for cfg nodes
object EdgeType extends Enumeration {
  type EdgeType = Value
  val EdgeNormal, // normal edges
  EdgeExc, // exception edges
  EdgeLoopCond, // loop condition edges
  EdgeLoop, // loop edges
  EdgeLoopIter, // loop iteration edges
  EdgeLoopOut, // loop out edges
  EdgeLoopBreak, // loop break edges
  EdgeLoopReturn // looop return edges
  = Value
}
