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

case class Node(visited: Boolean,
                content: Option[ConstraintForm],
                left: Option[SymbolicTree],
                right: Option[SymbolicTree],
                depth: Int) extends SymbolicTree {
  var isVisit = visited
  val constraint = content
  var parents: Option[List[Node]] = None
  var leftChild = left
  var rightChild = right

  var branchEnd: Node = null

  def setParents(p: List[Node]) = parents = Some(p)
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

  def setBranchEnd(end: Node) = {
    branchEnd = end
  }

  override def toString = "Node with a constraint: " + constraint + ", and with visited flag: " + isVisit

  def changeDepth(newDepth: Int): Node = {
    val newNode = this.copy(depth = newDepth)
    // We don't have to set the constraint-member, as that member is a constant
    newNode.isVisit = isVisit
    newNode.parents = parents
    newNode.leftChild = leftChild
    newNode.rightChild = rightChild
    newNode.branchEnd = branchEnd
    newNode
  }
  def incDepth: Node = {
    changeDepth(depth + 1)
  }
}
