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
case object CFGEdgeCall extends CFGEdgeType // call edges
case object CFGEdgeRet extends CFGEdgeType // return edges
