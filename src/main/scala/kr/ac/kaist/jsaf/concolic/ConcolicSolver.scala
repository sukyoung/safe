/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

import _root_.java.math.BigInteger
import kr.ac.kaist.jsaf.exceptions.ConcolicError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF, NodeFactory => NF}
import kr.ac.kaist.jsaf.nodes_util.NodeUtil
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Maps._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import scala.util.Random

class ConcolicSolver {
  var debug = false

  val z3 = new Z3
  if (debug)
    z3.debug = true
 
  var result: Map[String, (Id, List[Stmt])] = null 
  //var result: Map[String, LHS] = null 
  var primitiveResult: Map[String, Int] = null 
  var objectResult: Map[String, String] = null 
  
  val dummySpan = IF.dummySpan("forConcolicSolver")

  def solve(constraints: List[ConstraintForm], num: Int, function: FunctionInfo): Map[String, (Id, List[Stmt])] = { //Map[String, LHS] = {
    var primitiveConstraints: List[ConstraintForm] = List[ConstraintForm]()
    var objectTracking: Map[String, List[String]] = Map[String, List[String]]()
    // Filter out constraints on entire object 
    // Especially for a null constraint
    var temp: Map[String, String] = Map[String, String]()

    // Initialization
    result = Map[String, (Id, List[Stmt])]() 
    primitiveResult = Map[String, Int]() 
    objectResult = Map[String, String]() 

    if (constraints.isEmpty) 
      return result
    
    // Filter out initialization
    var init = function.thisObject.length
    for (i <- 0 until num by 1) {
      init += 1
      function.getObjectProperties(i) match {
        case Some(props) => init += props.length
        case None =>
      }
    }
    var actualConstraints = constraints.drop(init)
    var initialConstraints = constraints diff actualConstraints
    primitiveConstraints = initialConstraints 
    for (const <- initialConstraints) {
      if (const.objectRelated) {
        val op = const.getOp.unwrap
        val lhs = const.getLhs.getValue
        val rhs = const.getRhs.get.getLhs.getValue
        if (rhs.contains("i") || rhs.contains("this")) 
          objectTracking += rhs -> List(lhs)
      }
    }

    for (const <- actualConstraints) {
      if (const.objectRelated) {
        if (const.getOp.isSome) {
          val op = const.getOp.unwrap
          val lhs = const.getLhs
          val rhs = const.getRhs.get.getLhs
          op.charAt(0) match {
            case '=' =>
              // Store local result in temporary map
              if (lhs.fromConcrete || rhs.fromConcrete) {
                if (lhs.isNull) 
                  // rhs should be object in this case
                  temp += rhs.getValue -> "null" 
                else if (rhs.isNull) 
                  temp += lhs.getValue -> "null" 
              }
              // Tracking
              else {
                if (op.charAt(0) == '=' && op.length <= 1) {
                  if (rhs.getValue.contains("s")) {
                    for (k <- objectTracking.keySet) 
                      if (objectTracking(k).contains(rhs.getValue)) 
                        objectTracking += rhs.getValue -> (objectTracking(k):+lhs.getValue)
                  }
                }
              }
            case '!' =>
              if (op.length > 1 && op.charAt(1) == '=')
                if (lhs.fromConcrete || rhs.fromConcrete) {
                  if (lhs.isNull) 
                    // rhs should be object in this case
                    temp += rhs.getValue -> "!null" 
                  else if (rhs.isNull) 
                    temp += lhs.getValue -> "!null" 
                }
              else
                throw new ConcolicError("Wrong constraint form")
          }
        }
      }
      else 
        primitiveConstraints = primitiveConstraints :+ const
    }
    // Make a object result based on temporary store. 
    for (k <- objectTracking.keySet) {
      for (x <- objectTracking(k)) {
        if (temp.get(x).isSome) { 
          if (objectResult.get(k).isSome) {
            if (temp(x) != objectResult(k))
              throw new ConcolicError("Cant' solve these constraints.")
          }
          else 
            objectResult +=  k -> temp(x)
        }
      }
    }
    // Make a primitive result. 
    var tmp = z3.solve(toJavaList(primitiveConstraints), num, toJavaOption(Some(function.toJavaObjects)), function.toJavaThisObject) 
    if (tmp.isSome) 
      primitiveResult = toMap(tmp.unwrap).map(x => (x._1, x._2.intValue))
    
    // Build actual result combined object related result and primitive realted one.
    if (function.thisObject.nonEmpty) {
      val fresh = NodeUtil.freshName("this")
      val arg = NF.makeId(dummySpan, fresh, fresh) 
      result += "this" -> Pair(arg, assignObject(true, 0, arg, function.thisName, function.thisObject, primitiveResult)) 
    }

    for (i <- 0 until num by 1) {
      val fresh = NodeUtil.freshName("a")
      val arg = NF.makeId(dummySpan, fresh, fresh) 
      function.getObjectProperties(i) match {
        case Some(props) =>
          if (objectResult.contains("i"+i) && objectResult("i"+i) == "null") {
            result += "i"+i -> Pair(arg, List(assignValue(arg, NF.makeNull(dummySpan)))) 
          }
          else {
            /* Change to use VarRef
             * var x = new f()
             * x.y = 3
             */
            result += "i"+i -> Pair(arg, assignObject(false, i, arg, function.getObjectConstruct(i), props, primitiveResult)) 
          }
        case None =>
          var value = NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(10).toString))
          if (i < primitiveResult.size)  
            value = NF.makeIntLiteral(dummySpan, new BigInteger(primitiveResult("i"+i).toString))
          result += "i"+i -> (arg, List(assignValue(arg, value))) 
      }
    }
    if (debug)
      printResult(function, num)
    result
  }

  def assignValue(lhs: Id, rhs: LHS): Stmt = {
    val assign = NF.makeAssignOpApp(dummySpan, 
                                    NF.makeVarRef(dummySpan, lhs), 
                                    NF.makeOp(dummySpan, "="), 
                                    rhs)
    NF.makeExprStmt(dummySpan, assign)
  }

  def assignObject(isThis: Boolean, argnum: Int, arg: Id, constructor: String, props: List[(String, String)], res: Map[String, Int]): List[Stmt] =  {
    var stmts = List[Stmt]()
    val fun = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, constructor, constructor))
    val obj = NF.makeNew(dummySpan, NF.makeFunApp(dummySpan, fun, List[Expr]()))
    stmts = stmts:+assignValue(arg, obj)

    if (constructor == "Array") {
      val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, arg.getText, arg.getText))
      var lhs = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, "length", "length"))
      var rhs = NF.makeIntLiteral(dummySpan, new BigInteger(props(0)._1))
      stmts = stmts:+NF.makeExprStmt(dummySpan, NF.makeAssignOpApp(dummySpan, lhs, 
                                                            NF.makeOp(dummySpan, "="),
                                                            rhs))
      for (p <- 0 until props(0)._1.toInt) {
        val elem = NF.makeBracket(dummySpan, ref, NF.makeIntLiteral(dummySpan, new BigInteger(p.toString)))
        rhs = NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(10).toString))
        if (res.contains("i"+argnum+"."+p))
          rhs = NF.makeIntLiteral(dummySpan, new BigInteger(res("i"+argnum+"."+p).toString)) 
        stmts = stmts:+NF.makeExprStmt(dummySpan, NF.makeAssignOpApp(dummySpan, elem, 
                                                              NF.makeOp(dummySpan, "="),
                                                              rhs))
      }
    }
    else {
      stmts = props.foldLeft[List[Stmt]](stmts)((list, p) => {
        val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, arg.getText, arg.getText))
        val lhs = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, p._1, p._1))
        var rhs = NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(10).toString))
        var key = "i"+argnum+"."
        if (isThis)
          key = "this."
        if (res.contains(key+p._1))
          rhs = NF.makeIntLiteral(dummySpan, new BigInteger(res(key+p._1).toString)) 
  //      NF.makeAssignOpApp(dummySpan, lhs, NF.makeOp(dummySpan, "="), rhs)
        list:+NF.makeExprStmt(dummySpan, 
                              NF.makeAssignOpApp(dummySpan, 
                                                lhs, 
                                                NF.makeOp(dummySpan, "="),
                                                rhs))
      })
    }
    stmts
  }


  def printResult(function: FunctionInfo, num: Int) = {
    System.out.println("======================= Result =======================")
    for (i <- 0 until num by 1) {
      function.getObjectProperties(i) match {
        case Some(props) =>
          if (objectResult.contains("i"+i) && objectResult("i"+i) == "null")
            System.out.println("%6s => %6s".format("i"+i, "null")) 
          else {
            if (i < primitiveResult.size) {
              if (function.getObjectConstruct(i) == "Array") {
                for (p <- 0 until props(0)._1.toInt)
                  System.out.println("%6s => %6s".format("i"+i+"."+p, primitiveResult("i"+i+"."+p).toString)) 
              }
              else {
                for (p <- props) 
                  System.out.println("%6s => %6s".format("i"+i+"."+p._1, primitiveResult("i"+i+"."+p._1).toString)) 
              }
            }
          }
        case None =>
          if (i < primitiveResult.size) 
            System.out.println("%6s => %6s".format("i"+i, primitiveResult("i"+i).toString)) 
      }
    }
    System.out.println("======================================================")
  }
}
