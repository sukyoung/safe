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

package kr.ac.kaist.safe.nodes.cfg

// edge type for cfg nodes
sealed abstract class CFGEdgeType
case object CFGEdgeNormal extends CFGEdgeType // normal edges
case object CFGEdgeExc extends CFGEdgeType // exception edges
case object CFGEdgeLoopCond extends CFGEdgeType // loop condition edges
case object CFGEdgeLoop extends CFGEdgeType // loop edges
case object CFGEdgeLoopIter extends CFGEdgeType // loop iteration edges
case object CFGEdgeLoopOut extends CFGEdgeType // loop out edges
case object CFGEdgeLoopBreak extends CFGEdgeType // loop break edges
case object CFGEdgeLoopReturn extends CFGEdgeType // looop return edges
