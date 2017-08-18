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

import scala.collection.mutable.{ Queue, Stack }
import kr.ac.kaist.safe.errors.error.ConcolicError

/**
 * Represents a path constraint, in the form of a list of constraints, that was extracted, as well as all
 * input SymbolicValues on which the branch-conditions in the path constraint (directly or indirectly) depend.
 * @param constraints The path constraint.
 * @param necessaries The input SymbolicValues required to compute the branch-conditions in the path constraint.
 */
case class ExtractedConstraintInformations(constraints: List[ConstraintForm], necessaries: List[SymbolicValue])

class ConstraintExtractor {

  // Initially expanded node would be root.
  private var root: Node = Node(true, None, None, None, 0)

  private var leaves: Stack[Node] = null
  private var unvisited: Queue[Node] = null

  // To distinguish the newly generated information from the existing information.
  private var previous: List[Node] = null
  private var branches: Stack[Node] = null

  var debug = false

  def initialize(): Unit = {
    unvisited = new Queue
    root = Node(true, None, None, None, 0)
  }

  /**
   * Given a list of reported path conditions, updates the symbolic tree to generate a new path constraint
   * (Not sure if this description is entirely correct).
   * @param report The path conditions that were encountered during interpretation of the program.
   * @return An ExtractedConstraintInformations containing the new path constraint, in the form of a list of
   *         ConstraintForms, and the list of necessary SymbolicValues.
   */
  def modify(report: List[SymbolicInfo]): ExtractedConstraintInformations = {
    printReport(report)
    var affected = List[SymbolicInfo]()

    leaves = Stack(root)
    branches = Stack()
    setPrevious(root)
    var newlyEnd = 0
    for (info <- report) {
      // Existing information just update the original tree.
      if (matchPrevious(info) && newlyEnd == 0)
        update(info)
      // New information should be built as a subtree of the original tree.
      else {
        // Newly added branch information should be ended before updating existing information.
        info.getType match {
          case SymbolicInfoTypes.branch =>
            newlyEnd += 1
          case SymbolicInfoTypes.endBranch =>
            newlyEnd -= 1
          case _ =>
        }
        affected = affected :+ info
        insert(info)
      }
    }
    // Remove a node from unvisited queue because it is visited by chance.
    unvisited = unvisited.filterNot(_.isVisited).toQueue
    printAffectedAndSymbolicExecutionTree(affected, root)
    extract()
  }

  def extract(): ExtractedConstraintInformations = {
    if (unvisited.isEmpty) {
      System.out.println("DONE")
      ExtractedConstraintInformations(Nil, Nil)
    } else {
      // Take an unvisited node.
      val expanded: Node = unvisited.dequeue
      // Collect all constraints belonging to this node or its ancestors in the symbolic tree.
      val constraints: List[ConstraintForm] = collect(expanded)

      // Extract only symbolic values that are necessary to explore a chosen branch.
      var targetValues = constraints.filter(_.isBranchConstraint).flatMap(_.getSymbolicValues.distinct)
      var necessaries: List[SymbolicValue] = List()
      val assignConstraints = constraints.filterNot(_.isBranchConstraint)
      while (targetValues.nonEmpty) {
        val sv = targetValues.head
        targetValues = targetValues.tail
        necessaries = necessaries :+ sv

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
      printExpandedNodeAndConstraints(expanded, constraints)
      ExtractedConstraintInformations(constraints, necessaries)
    }
  }

  /**
   * Collects all constraints belonging to the given node and its ancestors in the symbolic tree.
   * @param node The node from which to start collecting constraints in the symbolic tree.
   * @return A list of all constraints that were collected.
   */
  def collect(node: Node): List[ConstraintForm] = {
    def loop(node: Node, collected: List[ConstraintForm]): List[ConstraintForm] = {
      val newCollected: List[ConstraintForm] = if (node.constraint.isDefined) {
        node.constraint.get :: collected
      } else {
        collected
      }
      if (node.parents.isDefined) {
        val parents = node.parents.get
        val target = if (parents.length > 1 && !parents(1).isVisited) {
          parents(1)
        } else {
          parents.head
        }
        loop(target, newCollected)
      } else {
        newCollected
      }
    }
    loop(node, Nil)
  }

  def update(info: SymbolicInfo): Unit = {
    val target = leaves.pop
    info.getType match {
      case SymbolicInfoTypes.statement =>
        val left = target.leftChild.get
        left.incDepth()
        leaves.push(left)
        setPrevious(left)
      case SymbolicInfoTypes.branch =>
        val left = target.leftChild.get
        val right = target.rightChild.get
        left.incDepth()
        right.incDepth()

        val child = if (!info.branchTaken) right else left
        val previousChild = if (!info.branchTaken) left else right

        child.isVisited = true

        leaves.push(child)
        setPrevious(child)
        if (previous.isEmpty) {
          //setPrevious(previousChild)
          findProperPrevious(previousChild)
        }
      case SymbolicInfoTypes.endBranch =>
        val child: Node = if (target.leftChild.isEmpty && target.rightChild.isEmpty) {
          val child = previous.head

          val previousParent = child.getParent
          val depth = if (target.depth > previousParent.depth) {
            target.depth
          } else {
            previousParent.depth
          }
          child.changeDepth(depth)

          if (previousParent.leftChild.isDefined) {
            target.setRightChild(Some(child))
          } else {
            target.setLeftChild(Some(child))
          }

          if (previousParent.leftChild.isDefined) {
            child.setParents(List(previousParent, target))
          } else {
            child.setParents(List(target, previousParent))
          }
          child
        } else if (target.leftChild.isDefined) {
          target.leftChild.get
        } else {
          target.rightChild.get
        }
        child.incDepth()
        leaves.push(child)
        setPrevious(child)
    }
  }

  def insert(info: SymbolicInfo): Unit = {
    val cand1 = leaves.pop
    val target = cand1
    if (!target.isVisited)
      throw new ConcolicError("All of adjacent leaves are not visited.")

    info.getType match {
      case SymbolicInfoTypes.statement =>
        val cond = ConstraintForm.makeConstraint(info.id, info.lhs, info.op, info.rhs)
        val left: Node = Node(true, Some(cond), None, None, target.depth + 1)
        target.setLeftChild(Some(left))
        left.setParents(List(target))

        leaves.push(left)
      case SymbolicInfoTypes.branch =>
        val depth = target.depth + 1
        // Put a true branch on the left side, and a false branch on the right side. 
        val visitNode = Node(true, negate(info.branchTaken, info), None, None, depth)
        val notvisitNode = Node(false, negate(!info.branchTaken, info), None, None, depth)
        val left: Node = if (info.branchTaken) visitNode else notvisitNode
        val right: Node = if (info.branchTaken) notvisitNode else visitNode

        target.setLeftChild(Some(left))
        target.setRightChild(Some(right))

        left.setParents(List(target))
        right.setParents(List(target))

        leaves.push(visitNode)
        branches.push(visitNode)

        unvisited += notvisitNode
      case SymbolicInfoTypes.endBranch =>
        val depth = cand1.depth
        // cand2 could be null because SymbolicHelper records only if-statements under certain conditions, however records every end-if-statements.
        val left: Node = Node(true, None, None, None, depth + 1)
        cand1.setLeftChild(Some(left))
        left.setParents(List(cand1))

        if (branches.nonEmpty) {
          val branch = branches.pop
          branch.setBranchEnd(left)
        }

        leaves.push(left)
    }
  }

  def negate(trueBranch: Boolean, info: SymbolicInfo): Option[ConstraintForm] = {
    info.op match {
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
        val cond = ConstraintForm.makeConstraint(None, info.lhs, operator, info.rhs)
        cond.setBranchConstraint()
        Some(cond)
      case None => info.lhs match {
        case Some(lhs) =>
          val operator = if (trueBranch) {
            Some("!=")
          } else {
            Some("==")
          }
          // TODO: It should be "true" and "Boolean" instead of "0" and "Number"
          val tmp = new SymbolicValue("0", "Number")
          val cond = ConstraintForm.makeConstraint(None, Some(lhs), operator, Some(tmp))
          cond.setBranchConstraint()
          Some(cond)
        case None =>
          throw new ConcolicError("The 'lhs' part in the information should be filled in.")
      }
    }
  }

  def setPrevious(node: Node): Unit = {
    previous = List()
    if (node.leftChild.isDefined)
      previous = previous :+ node.leftChild.get
    if (node.rightChild.isDefined)
      previous = previous :+ node.rightChild.get
  }

  def findProperPrevious(node: Node): Unit = {
    if (node.branchEnd.isDefined)
      previous = List(node.branchEnd.get)
  }

  def matchPrevious(info: SymbolicInfo): Boolean = {
    // Transform the information to check equality. 
    val cond: String = info.getType match {
      case SymbolicInfoTypes.statement =>
        val temp = ConstraintForm.makeConstraint(info.id, info.lhs, info.op, info.rhs)
        temp.toString
      case SymbolicInfoTypes.branch =>
        negate(info.branchTaken, info).get.toString
      case SymbolicInfoTypes.endBranch =>
        ""
    }
    // Check whether cond has already been generated previously.
    if (previous != null) {
      previous.exists((node: Node) => {
        val string = node.constraint.map(_.toString).getOrElse("")
        string == cond
      })
    } else {
      false
    }
  }

  /**
   * If debugging is enabled, prints the list of SymbolicInfos reported during interpretation.
   * @param report The list of SymbolicInfos that were reported.
   */
  def printReport(report: List[SymbolicInfo]): Unit = {
    if (debug) {
      System.out.println("====================== Report ========================")
      System.out.println(report.map(_.toString))
      System.out.println("======================================================")
    }
  }

  /**
   * If debugging is enabled, prints the list of affected SymbolicInfos and the symbolic execution tree.
   * @param affected The list of affected SymbolicInfos to print.
   * @param root The root of the symbolic execution tree to print.
   */
  def printAffectedAndSymbolicExecutionTree(affected: List[SymbolicInfo], root: Node): Unit = {
    if (debug) {
      System.out.println("================== Affected Report ===================")
      System.out.println(affected.map(_.toString))
      System.out.println("======================================================")
      System.out.println("============== Symbolic Execution Tree ===============")
      printTree(root)
      System.out.println("======================================================")
    }
  }

  /**
   * If debugging is enabled, prints the expanded node and the given path condition.
   * @param expanded The expanded node to print.
   * @param constraints The path condition (in the form of a list of ConstraintForms) to print.
   */
  def printExpandedNodeAndConstraints(expanded: Node, constraints: List[ConstraintForm]): Unit = {
    if (debug) {
      System.out.println("=================== Expanded Node ====================")
      System.out.println(expanded)
      System.out.println("======================================================")
      System.out.println("==================== Constraints =====================")
      System.out.println(constraints)
      System.out.println("======================================================")
    }
  }

  def printTree(tree: SymbolicTree): Unit = tree match {
    case node: Node =>
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
