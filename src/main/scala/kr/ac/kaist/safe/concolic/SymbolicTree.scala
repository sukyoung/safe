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

package kr.ac.kaist.safe.concolic

trait SymbolicTree

case class Node(
  var isVisited: Boolean,
    constraint: Option[ConstraintForm],
    var leftChild: Option[Node],
    var rightChild: Option[Node],
    depth: Int,
    var parents: Option[List[Node]] = None,
    var branchEnd: Option[Node] = None
) extends SymbolicTree {
  def setParents(p: List[Node]) = {
    parents = Some(p)
  }
  def getParent: Node = {
    parents.get.head
  }
  def changeParent(from: Node, to: Node) = {
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

  override def toString = "Node with a constraint: " + constraint + ", and with visited flag: " + isVisited

  private def copyMembers(newNode: Node): Unit = {
    // We don't have to set the constraint-member, as that member is a constant
    newNode.isVisited = isVisited
    newNode.parents = parents
    newNode.branchEnd = branchEnd
  }

  def changeDepth(newDepth: Int): Node = {
    val newNode = this.copy(depth = newDepth)
    copyMembers(newNode)
    newNode
  }
  def incDepth: Node = {
    changeDepth(depth + 1)
  }

  def setLeftChild(newLeftChild: Option[Node]): Unit = {
    leftChild = newLeftChild
  }

  def setRightChild(newRightChild: Option[Node]): Unit = {
    rightChild = newRightChild
  }
}
