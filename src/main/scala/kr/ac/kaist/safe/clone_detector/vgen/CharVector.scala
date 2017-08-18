/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 * *
 * Use is subject to license terms.
 * *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.clone_detector.vgen

import java.util

class CharVector {
  val NODE_COUNT = 63
  var nodeKind = 62
  var vec: util.Vector[Int] = new util.Vector[Int]()
  val _ = vec.clear()
  for (i <- 0 until NODE_COUNT) vec.add(0)
  var mergeable = false

  override def toString: String = {
    val s: StringBuilder = new StringBuilder
    if (!vec.isEmpty) {
      for (i <- 0 until NODE_COUNT) s.append(vec.elementAt(i)).append(" ")
    }
    s.toString
  }

  val at = ""

  val stringName = ""

  def addAt(index: Int): Unit = vec.setElementAt(vec.elementAt(index) + 1, index)

  def merge(childVec: util.Vector[Int]): Boolean =
    if (childVec.size != vec.size) false
    else {
      for (i <- 0 until NODE_COUNT)
        vec.setElementAt(vec.elementAt(i) + childVec.elementAt(i), i)
      true
    }

  def getNumOfTokens: Int = {
    var count: Int = 0
    for (i <- 0 until NODE_COUNT) count += vec.elementAt(i)
    count
  }

  def containsEnoughTokens(minT: Int): Boolean =
    if (getNumOfTokens > minT) true
    else false

  def getVector: util.Vector[Int] = vec

  def isMergeable: Boolean = mergeable

  def setMergeable(): Unit = {
    mergeable = true
  }

  def getNodeKind: Int = nodeKind

  def setNodeKind(kind: Int): Unit = {
    nodeKind = kind
  }
}
