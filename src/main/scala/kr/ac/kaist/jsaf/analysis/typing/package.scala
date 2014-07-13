/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis

import scala.collection.mutable.{Map=>MMap, HashSet => MHashSet}
import collection.immutable.{HashSet, Map, TreeMap, Stack => IStack}
import kr.ac.kaist.jsaf.analysis.cfg.Node
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.cfg.CFGId

package object typing {
  type ControlPoint = (Node, CallContext)
  type CPEdgeList = List[(ControlPoint, ControlPoint)]
  type CPStack = IStack[ControlPoint]
  type CPStackSet = MHashSet[CPStack]

  type CState = Map[CallContext, State]
  type Table = MMap[Node, CState]
  
  type OrderEntry = (Int, ControlPoint)
  type OrderMap = TreeMap[Node, Int]

  type DUSet = Map[Node, (LPSet, LPSet)]

  implicit def CFGId2String(v: CFGId): String = v.toString
}
