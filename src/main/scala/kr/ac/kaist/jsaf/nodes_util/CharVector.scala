/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import java.io.Serializable
import java.util.Vector
import kr.ac.kaist.jsaf.useful.HasAt

class CharVector extends HasAt {
  val NODE_COUNT = 63
  var node_kind = 62
  var vec: Vector[Int] = new Vector[Int]()
  val _ = vec.clear(); for (i <- 0 to NODE_COUNT-1) vec.add(0)
  var mergeable = false

  override def toString = {
    val s: StringBuilder = new StringBuilder
    if (!vec.isEmpty) {
      for (i <- 0 to NODE_COUNT-1) s.append(vec.elementAt(i)).append(" ")
    }
    s.toString
  }

  def at = ""
  def stringName = ""
  def addAt(index: Int) = vec.setElementAt(vec.elementAt(index)+1, index)
  def merge(childVec: Vector[Int]): Boolean =
    if (childVec.size != vec.size) false
    else {
      for (i <- 0 to NODE_COUNT-1)
        vec.setElementAt(vec.elementAt(i)+childVec.elementAt(i), i)
      true
    }
  def getNumOfTokens = {
    var count: Int = 0
    for (i <- 0 to NODE_COUNT-1) count += vec.elementAt(i)
    count
  }
  def containsEnoughTokens(minT: Int): Boolean =
    if (getNumOfTokens > minT) true
    else false
  def getVector = vec
  def isMergeable = mergeable
  def setMergeable = { mergeable = true }
  def getNodeKind = node_kind
  def setNodeKind(kind: Int) = { node_kind = kind }
}
