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

package kr.ac.kaist.safe.nodes.cfg

// edge type for cfg nodes
sealed abstract class CFGEdgeType
case object CFGEdgeNormal extends CFGEdgeType {
  // override def toString(): String = "Normal"
} // normal edges
case object CFGEdgeExc extends CFGEdgeType {
  // override def toString(): String = "Exc"
} // exception edges
case object CFGEdgeCall extends CFGEdgeType {
  // override  def toString(): String = "Call"
} // call edges
case object CFGEdgeRet extends CFGEdgeType {
  // override def toString(): String = "Ret"
} // return edges
