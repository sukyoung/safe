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

package kr.ac.kaist.safe.concolic

trait SymbolicTree

case class Node(
    var isVisited: Boolean,
    constraint: Option[ConstraintForm],
    var leftChild: Option[Node],
    var rightChild: Option[Node],
    var depth: Int,
    var parents: Option[List[Node]] = None,
    var branchEnd: Option[Node] = None
) extends SymbolicTree {

  def setParents(p: List[Node]): Unit = {
    parents = Some(p)
  }
  def getParent: Node = {
    parents.get.head
  }
  def changeParent(from: Node, to: Node): Unit = {
    var temp = parents.get
    for (i <- temp.indices) {
      if (from.constraint == temp(i).constraint)
        temp = temp.updated(i, to)
    }
    parents = Some(temp)
  }

  def setBranchEnd(end: Node): Unit = {
    branchEnd = Some(end)
  }

  override def toString: String =
    s"Node with a constraint: $constraint and with visited flag: $isVisited"

  def changeDepth(newDepth: Int): Unit = {
    depth = newDepth
  }
  def incDepth(): Unit = {
    changeDepth(depth + 1)
  }

  def setLeftChild(newLeftChild: Option[Node]): Unit = {
    leftChild = newLeftChild
  }

  def setRightChild(newRightChild: Option[Node]): Unit = {
    rightChild = newRightChild
  }
}
