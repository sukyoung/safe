/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

import _root_.java.util.BitSet
import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.exceptions.ConcolicError
import kr.ac.kaist.jsaf.interpreter.Interpreter
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.EJSOp
import kr.ac.kaist.jsaf.nodes_util.{ IRFactory => IF }
import kr.ac.kaist.jsaf.nodes_util.{ NodeUtil => NU }
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.scala_src.useful.Sets._

import scala.collection.mutable.Queue

class ConstraintExtractor(I: Interpreter) {
  abstract class SymbolicTree 
  //case class Node(v: Boolean, e: Option[Tuple2[String, String]], pc: Option[ConstraintForm], vector: BitSet) extends SymbolicTree {
  case class Node(_visited: Boolean, _constraint: Option[ConstraintForm], _bitvector: BitSet) extends SymbolicTree {
      var visited = _visited
      //var expr = e
      var constraint = _constraint 
      var bitvector = _bitvector
  }
  case class Statement(node: Node, children: SymbolicTree) extends SymbolicTree
  case class Condition(node: Node, left: SymbolicTree, right: SymbolicTree) extends SymbolicTree
  
  var debug = false 

  // store where node locates in the symbolic execution tree 
  var bitset: BitSet = null
  var tree: SymbolicTree = null
  var unvisited: Queue[Node] = null
  // indicate expanded node in the symbolic execution tree
  var target: Node = null
  // target's bitset
  var expanded: BitSet = null 
  var constraints: List[ConstraintForm] = null

  def initialize() = {
    bitset = new BitSet()
    bitset.set(0)
    //tree = new Node(true, None, None, bitset)
    tree = new Node(true, None, bitset)
    unvisited = new Queue[Node]
    target = null
    expanded = new BitSet()
    expanded.set(0)
    constraints = List[ConstraintForm]()
  }
  
  def extract(report: List[Info]) = {
    val previousConstraints = constraints
    constraints = List[ConstraintForm]()
    /* Construct the symbolic execution tree */
    var subtree:SymbolicTree = Node(true, None, expanded)
    var r:List[Info] = report.drop(expanded.length-1)
    // FOR DUBUGGING
    if (debug) {
      System.out.println("Report: " + report.map(_.toString))
      System.out.println("Affected Report: " + r.map(_.toString))
      System.out.println("Expanded Node: " + expanded)
    }
    
    // TODO: Check the explored path is what we wanted.
    /*if (target != null) {
      var tmp = report(expanded.length-2)
      var tmpConstraint = negate(tmp.branchTaken, tmp) 
      if (tmpConstraint.isSome) {
        if (!tmp.toString.equals(target.constraint.toString)) {
          System.out.println("One more try")
          constraints = previousConstraints
        }
      }
    }*/

    for (info <- r) 
      subtree = insert(subtree, info)
    tree = combine(tree, expanded.length-1, subtree)
    
    if (unvisited.isEmpty) 
      System.out.println("DONE")
    else {
      target = unvisited.dequeue
      expanded = target.bitvector
      collect(tree, target.bitvector, target.bitvector.length-2)
    }
  }

  def combine(origin: SymbolicTree, index: Int, additional: SymbolicTree):SymbolicTree = {
    if (index > 0) {
      origin match {
        case Condition(node, left, right) =>
            if (expanded.get(index-1))
              Condition(node, combine(left, index-1, additional), right)
            else
              Condition(node, left, combine(right, index-1, additional))
        case Statement(node, child) => 
            Statement(node, combine(child, index-1, additional))
        //case Node(v, e, pc, vector) =>
        case Node(visited, constraint, bitvector) =>
            System.out.println("WRONG COMBINATION 1")
            //Node(v, e, pc, vector)
            Node(visited, constraint, bitvector)
      }
    }
    else {
      origin match {
        case Condition(node, left, right) =>
            System.out.println("WRONG COMBINATION 2")
            Condition(node, left, right)
        case Statement(node, child) =>  
            System.out.println("WRONG COMBINATION 3")
            Statement(node, child)
        //case Node(v, e, pc, vector) =>
        case Node(visited, constraint, bitvector) =>
            additional match {
              case Condition(node, left, right) => //Condition(Node(!v, e, pc, vector), left, right)
                Condition(Node(!visited, constraint, bitvector), left, right)
              case Statement(node, child) => //Statement(Node(!v, e, pc, vector), child)
                Statement(Node(!visited, constraint, bitvector), child)
              //case Node(v2, e2, pc2, vector2) =>  Node(!v, e, pc, vector)
              case Node(_, _, _) =>  Node(!visited, constraint, bitvector)
            }
      }
    }  
  }

  def insert(t: SymbolicTree, info: Info):SymbolicTree = t match {
    //case Node(v, e, pc, vector) => 
    case Node(visited, constraint, bitvector) => 
      if (info.isCond) {
        var b1 = bitShift(bitvector)
        b1.set(0, info.branchTaken)
        var n1 = Node(true, negate(info.branchTaken, info), b1)
        
        var b2 = bitShift(bitvector)
        b2.set(0, !info.branchTaken)
        var n2 = Node(false, negate(!info.branchTaken, info), b2)
        
        unvisited += n2
        // Left for true branch and right for false branch
        if (info.branchTaken)
          Condition(Node(visited, constraint, bitvector), n1, n2)
        else
          Condition(Node(visited, constraint, bitvector), n2, n1)
      }
      else {
        var b = bitShift(bitvector)
        b.set(0)
        var cond = new ConstraintForm
        cond.makeConstraint(info._id, info._lhs, info._op, info._rhs)
        //var rhs = new ConstraintForm
        //rhs = parsing(info.expr._2)

        //cond.makeConstraint(info.expr._1, "=", rhs)
        //Statement(Node(v, e, pc, vector), Node(true, Some(info.expr), Some(cond), b))
        Statement(Node(visited, constraint, bitvector), Node(true, Some(cond), b))
      }
    case Statement(node, child) => Statement(node, insert(child, info))
    case Condition(node, left, right) =>
      if(isVisit(left))
        Condition(node, insert(left, info), right)
      else 
        Condition(node, left, insert(right, info))
  }

  def collect(t: SymbolicTree, bitset: BitSet, index: Int):List[ConstraintForm] = t match {
    //TODO: combine disffused symbolic expression to one single constraint 
    //according to the supporting form of z3 solver.
    //case Node(v, e, pc, vector) => 
    case Node(visited, constraint, bitvector) => 
      if (!bitset.equals(bitvector)) 
        System.out.println(bitset.toString + ", " + bitvector.toString)
      constraint match { case Some(c) => constraints = constraints:+c; case None => }
      return constraints
    case Statement(node, child) => 
      if (!bitset.get(index))
        System.out.println(index.toString)
      node.constraint match { case Some(c) => constraints = constraints:+c; case None => }
      collect(child, bitset, index-1)
    case Condition(node, left, right) =>
      node.constraint match { case Some(c) => constraints = constraints:+c; case None => }
      if (bitset.get(index))
        collect(left, bitset, index-1)
      else
        collect(right, bitset, index-1)
  }

  /* Helper functions */
  def bitShift(v: BitSet):BitSet = {
    var res = new BitSet()
    var i = 0
    for (i <- 0 to v.length)
      res.set(i+1, v.get(i))
    return res
  }

  def isVisit(t: SymbolicTree):Boolean = t match {
    //case Node(v, e, pc, vector) => v
    case Node(visited, constraint, bitvector) => visited
    case Statement(node, child) => node.visited
    case Condition(node, right, left) =>  node.visited
  }
    
  def negate(trueBranch: Boolean, info: Info):Option[ConstraintForm] = info._op match {
    case Some(op) => 
      var operator = 
        if (!trueBranch)
          op match {
            case "<" => Some(">=")
            case "<=" => Some(">")
            case ">" => Some("<=")
            case ">=" => Some("<")
            case "==" => Some("!=")
            case "!=" => Some("==")
            case "===" => Some("!==")
            case "!==" => Some("===")
          }
        else
          Some(op)
      var cond = new ConstraintForm
      cond.makeConstraint(None, info._lhs, operator, info._rhs) 
      return Some(cond)
    case None => info._lhs match {
      case Some(lhs) =>
        var operator = 
          if (trueBranch) Some("!=")
          else Some("==")
        // TODO: It should be "true" and "Boolean" instead of "0" and "Number"
        var tmp = new SymbolicValue
        tmp.makeSymbolicValue("0", "Number")
        var cond = new ConstraintForm
        cond.makeConstraint(None, Some(lhs), operator, Some(tmp)) 
        return Some(cond)
      case None =>
        throw new ConcolicError("The 'lhs' part in the information should be completed.")
    }
      /*var cond = new ConstraintForm
      cond.lhs = info.expr._2
      var rhs = new ConstraintForm
      // TODO: It should be "true" instead of "0"
      rhs.makeConstraint("0")
      cond.rhs = Some(rhs)
      cond.op = Some("==")
      if (!trueBranch) 
        cond.op = Some("!=") 
      return Some(cond)*/
  }
  
  def height(t: SymbolicTree):Int = t match {
    //case Node(v, e, pc, vector) => 0
    case Node(visited, condition, bitvector) => 0
    case Statement(node, child) => 1 + height(child)
    case Condition(node, left, right) =>
      if(isVisit(left)) 
        1 + height(left)
      else 
        1 + height(right)
  }

  /*def parsing(expr: String):ConstraintForm = {
    var res = new ConstraintForm()
    var operations = Array('+', '-', '*', '/', '%')
    var isop = false
    for (op <- operations) {
      if (expr.contains(op)) {
        isop = true
        var parse = expr.split(op)    
        var rhs = new ConstraintForm()
        rhs.makeConstraint(parse(1))
        res.makeConstraint(parse(0), op.toString, rhs)
      }
    }
    if (!isop)
      res.makeConstraint(expr)
    return res
  }*/

  def toString(t: SymbolicTree):String = t match {
    //case Node(v, e, pc, vector) =>
    case Node(visited, constraint, bitvector) =>
      val const = constraint match { case Some(c) => c.toString; case None => "root" }
      //"(" + v.toString + "/ " + expr + "/ " + cond + "/ " + vector.toString + ") "
      "(" + visited.toString + "/ " + const + "/ " + bitvector.toString + ") "
    case Statement(node, child) => 
      toString(node) + "(" + toString(child) + ")"
    case Condition(node, left, right) =>
      toString(node) + "(" + toString(left) + "," + toString(right) + ") "
  }

  def print() = {
    System.out.println("============== Symbolic Execution Tree ===============")
    System.out.println(toString(tree))
    System.out.println("======================================================")
    System.out.println("================ Selected Constraint =================")
    for (elem <- constraints)
        System.out.println(elem.toString)
    System.out.println("======================================================")
  }
}
      
        
  
