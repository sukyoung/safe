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

import scala.collection.mutable.{ Queue, Stack }
import kr.ac.kaist.safe.errors.error.ConcolicError

class ConstraintExtractor {

  // Initially expanded node would be root.
  var root: SymbolicTree = Node(true, None, None, None, 0)
  var expanded: Node = null

  var leaves: Stack[Node] = null
  var unvisited: Queue[Node] = null

  // To distinquish the newly generated information from the existing information.
  var previous: List[Node] = null
  var branches: Stack[Node] = null

  var constraints: List[ConstraintForm] = null

  var necessaries: List[SymbolicValue] = null

  var debug = false

  def initialize() = {
    unvisited = new Queue
    root = Node(true, None, None, None, 0)
  }

  def modify(report: List[SymbolicInfo]) = {
    if (debug) {
      System.out.println("====================== Report ========================")
      System.out.println(report.map(_.toString))
      System.out.println("======================================================")
    }
    var affected = List[SymbolicInfo]()
    constraints = List[ConstraintForm]()

    leaves = Stack(root.asInstanceOf[Node])
    branches = Stack()
    setPrevious(root.asInstanceOf[Node])
    var newlyEnd = 0
    for (info <- report) {
      // Existing information just update the original tree.
      if (matchPrevious(info) && newlyEnd == 0)
        update(info)
      // New information should be built as a subtree of the original tree.
      else {
        // Newly added branch information should be ended before updating existing information.
        if (info.getType == SymbolicInfoTypes.branch) newlyEnd += 1
        if (info.getType == SymbolicInfoTypes.endBranch) newlyEnd -= 1
        affected = affected :+ info
        insert(info)
      }
    }

    //Remove a node from unvisited queue because it is visited by chance.
    unvisited = unvisited.filterNot(_.isVisit).toQueue

    if (debug) {
      System.out.println("================== Affected Report ===================")
      System.out.println(affected.map(_.toString))
      System.out.println("======================================================")
      System.out.println("============== Symbolic Execution Tree ===============")
      printTree(root)
      System.out.println("======================================================")
    }

    extract()
  }

  def extract(): Unit = {
    if (unvisited.isEmpty) System.out.println("DONE")
    else {
      expanded = unvisited.dequeue
      collect(expanded)

      // Extract only symbolic values that are necessary to explore a chosen branch.
      var targetValues = constraints.filter(_.isBranchConstraint).flatMap(_.getSymbolicValues.distinct)
      necessaries = List()
      val assignConstraints = constraints.filterNot(_.isBranchConstraint)
      while (targetValues.nonEmpty) {
        val sv = targetValues.head
        necessaries = necessaries :+ sv

        targetValues = targetValues.tail
        assignConstraints.find(_.getLhs == sv) match {
          case Some(x) =>
            if (x.getRhs.isDefined) {
              val temp = x.getRhs.get.getSymbolicValues ::: targetValues
              targetValues = temp.distinct
            }
          case None =>
        }
      }
      /*constraints = constraints.foldLeft[List[ConstraintForm]](List())((list, cons) => {
        if (necessaries.find(_ == cons.getLhs).isSome)
          list:+cons
        else
          list
      })*/
      necessaries = necessaries.filter(_.isInput)
    }

    if (debug) {
      System.out.println("=================== Expanded Node ====================")
      System.out.println(expanded)
      System.out.println("======================================================")
      System.out.println("==================== Constraints =====================")
      System.out.println(constraints)
      System.out.println("======================================================")
    }
  }

  def collect(node: Node): Unit = {
    if (node.constraint.isDefined) {
      constraints = node.constraint.get :: constraints
    }
    if (node.parents.isDefined) {
      val parents = node.parents.get
      val target = if (parents.length > 1 && !parents(1).isVisit) parents(1) else parents.head
      collect(target)
    }
  }

  def update(info: SymbolicInfo) = {
    val target = leaves.pop
    info.getType match {
      case SymbolicInfoTypes.statement =>
        val left = target.leftChild.get.asInstanceOf[Node]
        val newLeft = left.incDepth
        leaves.push(newLeft)

        setPrevious(newLeft)
      case SymbolicInfoTypes.branch =>
        val left1 = target.leftChild.get.asInstanceOf[Node]
        val right1 = target.rightChild.get.asInstanceOf[Node]
        val left2 = left1.incDepth
        val right2 = right1.incDepth

        val child = if (!info.branchTaken) right2 else left2
        val previousChild = if (!info.branchTaken) left2 else right2

        child.isVisit = true

        leaves.push(child)
        setPrevious(child)
        if (previous.isEmpty) {
          //setPrevious(previousChild)
          findProperPrevious(previousChild)
        }
      case SymbolicInfoTypes.endBranch =>
        val child1: Node = if (target.leftChild.isEmpty && target.rightChild.isEmpty) {
          val child1 = previous.head

          val previousParent = child1.getParent
          val depth = if (target.depth > previousParent.depth) {
            target.depth
          } else {
            previousParent.depth
          }
          val child2 = child1.incDepth

          if (previousParent.leftChild.isDefined) {
            target.rightChild = Some(child2)
          } else {
            target.leftChild = Some(child2)
          }

          if (previousParent.leftChild.isDefined) {
            child2.setParents(List(previousParent, target))
          } else {
            child2.setParents(List(target, previousParent))
          }
          child2
        } else if (target.leftChild.isDefined) {
          target.leftChild.get.asInstanceOf[Node]
        } else {
          target.rightChild.get.asInstanceOf[Node]
        }
        val child2 = child1.incDepth
        leaves.push(child2)
        setPrevious(child2)
    }
  }

  def insert(info: SymbolicInfo): Unit = {
    val cand1 = leaves.pop
    val target = cand1
    if (!target.isVisit)
      throw new ConcolicError("All of adjacent leaves are not visited.")

    info.getType match {
      case SymbolicInfoTypes.statement =>
        val cond = new ConstraintForm
        cond.makeConstraint(info._id, info._lhs, info._op, info._rhs)
        val left: Node = Node(true, Some(cond), None, None, target.depth + 1)
        target.leftChild = Some(left)
        left.setParents(List(target))

        leaves.push(left)
      case SymbolicInfoTypes.branch =>
        val depth = target.depth + 1
        // Put a true branch on the left side, and a false branch on the right side. 
        val visitNode = Node(true, negate(info.branchTaken, info), None, None, depth)
        val notvisitNode = Node(false, negate(!info.branchTaken, info), None, None, depth)
        val left: Node = if (info.branchTaken) visitNode else notvisitNode
        val right: Node = if (info.branchTaken) notvisitNode else visitNode

        target.leftChild = Some(left)
        target.rightChild = Some(right)

        left.setParents(List(target))
        right.setParents(List(target))

        leaves.push(visitNode)
        branches.push(visitNode)

        unvisited += notvisitNode
      case SymbolicInfoTypes.endBranch =>
        val depth = cand1.depth
        // cand2 could be null because SymbolicHelper records only if-statements under certain conditions, however records every end-if-statements.
        val left: Node = Node(true, None, None, None, depth + 1)
        cand1.leftChild = Some(left)
        left.setParents(List(cand1))

        if (branches.nonEmpty) {
          val branch = branches.pop
          branch.setBranchEnd(left)
        }

        leaves.push(left)
    }
  }

  def negate(trueBranch: Boolean, info: SymbolicInfo): Option[ConstraintForm] = {
    info._op match {
      case Some(op) =>
        val operator =
          if (!trueBranch) op match {
            case "<" => Some(">=")
            case "<=" => Some(">")
            case ">" => Some("<=")
            case ">=" => Some("<")
            case "==" => Some("!=")
            case "!=" => Some("==")
            case "===" => Some("!==")
            case "!==" => Some("===")
          }
          else Some(op)
        val cond = new ConstraintForm
        cond.makeConstraint(None, info._lhs, operator, info._rhs)
        cond.setBranchConstraint()
        Some(cond)
      case None => info._lhs match {
        case Some(lhs) =>
          val operator = if (trueBranch) Some("!=") else Some("==")
          // TODO: It should be "true" and "Boolean" instead of "0" and "Number"
          val tmp = new SymbolicValue
          tmp.makeSymbolicValue("0", "Number")
          val cond = new ConstraintForm
          cond.makeConstraint(None, Some(lhs), operator, Some(tmp))
          cond.setBranchConstraint()
          Some(cond)
        case None =>
          throw new ConcolicError("The 'lhs' part in the information should be filled in.")
      }
    }
  }

  def setPrevious(node: Node) = {
    previous = List()
    if (node.leftChild.isDefined)
      previous = previous :+ node.leftChild.get.asInstanceOf[Node]
    if (node.rightChild.isDefined)
      previous = previous :+ node.rightChild.get.asInstanceOf[Node]
  }

  def findProperPrevious(node: Node) = {
    if (node.branchEnd != null)
      previous = List(node.branchEnd)
  }

  def matchPrevious(info: SymbolicInfo) = {
    // Transform the information to check equality. 
    val cond = info.getType match {
      case SymbolicInfoTypes.statement =>
        val temp = new ConstraintForm
        temp.makeConstraint(info._id, info._lhs, info._op, info._rhs)
        temp.toString
      case SymbolicInfoTypes.branch =>
        negate(info.branchTaken, info).get.toString
      case SymbolicInfoTypes.endBranch =>
        ""
    }
    var result = false
    if (previous != null) {
      for (node <- previous) {
        val temp =
          if (node.constraint.isDefined) node.constraint.get.toString
          else ""
        if (temp == cond)
          result = true
      }
    }
    result
  }

  def printTree(tree: SymbolicTree): Unit = {
    val node = tree.asInstanceOf[Node]
    System.out.println(node)
    if (node.leftChild.isDefined) {
      for (i <- 0 until node.depth)
        System.out.print("\t")
      printTree(node.leftChild.get)
    }
    if (node.rightChild.isDefined) {
      for (i <- 0 until node.depth)
        System.out.print("\t")
      printTree(node.rightChild.get)
    }
  }
}
