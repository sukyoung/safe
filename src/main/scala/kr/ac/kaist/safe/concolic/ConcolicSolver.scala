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

import java.math.BigInteger
import scala.util.Random

import kr.ac.kaist.safe.errors.error.ConcolicError
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{ Coverage, NodeUtil => NU, Span }

class ConcolicSolver(coverage: Coverage) {
  var debug = false

  val z3 = new Z3
  if (debug)
    z3.debug = true

  var result: Map[String, (Id, List[Stmt])] = null
  var primitiveResult: Map[String, Int] = null
  var objectResult: Map[String, String] = null

  val dummySpan = Span("forConcolicSolver")

  def solve(constraints: List[ConstraintForm], num: Int, function: FunctionInfo): Map[String, (Id, List[Stmt])] = {
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
    var init = function.getThisProperties.length
    for (i <- 0 until num by 1) {
      init += 1
      function.getObjectProperties(i) match {
        case Some(props) => init += props.length
        case None =>
      }
    }
    val actualConstraints = constraints.drop(init)
    val initialConstraints = constraints diff actualConstraints
    primitiveConstraints = initialConstraints
    for (const <- initialConstraints) {
      if (const.objectRelated) {
        val op = const.getOp.get
        val lhs = const.getLhs.getValue.get
        val rhs = const.getRhs.get.getLhs.getValue.get
        if (rhs.contains("i") || rhs.contains("this"))
          objectTracking += rhs -> List(lhs)
      }
    }

    for (const <- actualConstraints) {
      if (const.objectRelated) {
        if (const.getOp.isDefined) {
          val op = const.getOp.get
          val lhs = const.getLhs
          val rhs = const.getRhs.get.getLhs
          op.charAt(0) match {
            case '=' =>
              // Store local result in temporary map
              if (lhs.fromConcrete || rhs.fromConcrete) {
                if (lhs.isNull)
                  // rhs should be object in this case
                  temp += rhs.getValue.orNull -> "null"
                else if (rhs.isNull)
                  temp += lhs.getValue.orNull -> "null"
              } // Tracking
              else {
                if (op.charAt(0) == '=' && op.length <= 1) {
                  if (rhs.getValue.get.contains("s")) {
                    for (k <- objectTracking.keySet)
                      if (objectTracking(k).contains(rhs.getValue.get))
                        objectTracking += rhs.getValue.orNull -> (objectTracking(k) :+ lhs.getValue.get)
                  }
                }
              }
            case '!' =>
              if (op.length > 1 && op.charAt(1) == '=')
                if (lhs.fromConcrete || rhs.fromConcrete) {
                  if (lhs.isNull)
                    // rhs should be object in this case
                    temp += rhs.getValue.orNull -> "!null"
                  else if (rhs.isNull)
                    temp += lhs.getValue.orNull -> "!null"
                } else
                  throw new ConcolicError("Wrong constraint form")
          }
        }
      } else
        primitiveConstraints = primitiveConstraints :+ const
    }
    // Make a object result based on temporary store.
    for (k <- objectTracking.keySet) {
      for (x <- objectTracking(k)) {
        if (temp.get(x).isDefined) {
          if (objectResult.get(k).isDefined) {
            if (temp(x) != objectResult(k))
              throw new ConcolicError("Cant' solve these constraints.")
          } else
            objectResult += k -> temp(x)
        }
      }
    }
    // Make a primitive result.
    val tmp = z3.solve(
      primitiveConstraints,
      num,
      Some(function.getObjects),
      function.getThisProperties
    )
    if (tmp.isDefined) {
      primitiveResult = tmp.get.map({ case (str, int) => (str, int.intValue) }).toMap
    }

    // Build actual result combined object related result and primitive realted one.
    if (function.hasThisObject) {
      val fresh = NU.freshName("this")
      val arg = NF.makeId(dummySpan, fresh, fresh)
      //TODO: Handle multiple type
      val thisNames = function.getThisConstructors
      result += "this" -> (arg, assignObject(true, 0, arg, thisNames.head, function.getThisProperties, primitiveResult, true))
    }
    for (i <- 0 until num by 1) {
      val fresh = NU.freshName("a")
      val arg = NF.makeId(dummySpan, fresh, fresh)
      function.getObjectProperties(i) match {
        case Some(props) =>
          if (objectResult.contains("i" + i) && objectResult("i" + i) == "null") {
            result += "i" + i -> (arg, List(assignValue(arg, NF.makeNull(dummySpan))))
          } else {
            /* Change to use VarRef
             * var x = new f()
             * x.y = 3
             */
            var stmts: List[Stmt] = assignEmptyObject(arg)
            //TODO: Handle multiple type
            val constructors = function.getObjectConstructors(i)
            if (constructors.nonEmpty)
              stmts = assignObject(false, i, arg, constructors.head, props, primitiveResult, true)

            result += "i" + i -> (arg, stmts)
          }
        case None =>
          var value = NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(10).toString))
          if (i < primitiveResult.size)
            value = NF.makeIntLiteral(dummySpan, new BigInteger(primitiveResult("i" + i).toString))
          result += "i" + i -> (arg, List(assignValue(arg, value)))
      }
    }
    if (debug)
      printResult(function, num)
    result
  }

  def assignValue(lhs: Id, rhs: LHS): Stmt = {
    val assign = NF.makeAssignOpApp(
      dummySpan,
      NF.makeVarRef(dummySpan, lhs),
      NF.makeOp(dummySpan, "="),
      rhs
    )
    NF.makeExprStmt(dummySpan, assign)
  }

  def assignObject(
    isThis: Boolean,
    argnum: Int,
    arg: Id,
    constructor: String,
    props: List[String],
    res: Map[String, Int],
    hasArguments: Boolean
  ): List[Stmt] = {
    var stmts = List[Stmt]()
    // Generate appropriate arguments for object constructor.
    var args = List[Expr]()
    if (hasArguments) {
      val (temp, additional) = assignArgs(constructor)
      if (temp.isDefined) args = temp.get
      stmts = stmts ::: additional
    }

    val fun = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, constructor, constructor))
    val obj = NF.makeNew(dummySpan, NF.makeFunApp(dummySpan, fun, args))
    stmts = stmts :+ assignValue(arg, obj)

    if (constructor == "Array") {
      val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, arg.text, arg.text))
      val lhs = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, "length", "length"))
      var rhs = NF.makeIntLiteral(dummySpan, new BigInteger(props.head))
      stmts = stmts :+ NF.makeExprStmt(dummySpan, NF.makeAssignOpApp(dummySpan, lhs,
        NF.makeOp(dummySpan, "="),
        rhs))
      for (p <- 0 until props.head.toInt) {
        val elem = NF.makeBracket(dummySpan, ref, NF.makeIntLiteral(dummySpan, new BigInteger(p.toString)))
        rhs = NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(10).toString))
        if (res.contains("i" + argnum + "." + p))
          rhs = NF.makeIntLiteral(dummySpan, new BigInteger(res("i" + argnum + "." + p).toString))
        stmts = stmts :+ NF.makeExprStmt(dummySpan, NF.makeAssignOpApp(dummySpan, elem,
          NF.makeOp(dummySpan, "="),
          rhs))
      }
    } else {
      stmts = props.foldLeft[List[Stmt]](stmts)((list, p) => {
        val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, arg.text, arg.text))
        val lhs = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, p, p))
        var key = "i" + argnum + "."
        if (isThis)
          key = "this."
        if (res.contains(key + p) && coverage.isNecessary(key + p)) {
          val rhs = NF.makeIntLiteral(dummySpan, new BigInteger(res(key + p).toString))
          list :+ NF.makeExprStmt(
            dummySpan,
            NF.makeAssignOpApp(
              dummySpan,
              lhs,
              NF.makeOp(dummySpan, "="),
              rhs
            )
          )
        } else
          list
        //      NF.makeAssignOpApp(dummySpan, lhs, NF.makeOp(dummySpan, "="), rhs)
      })
    }
    stmts
  }

  def assignEmptyObject(arg: Id): List[Stmt] = {
    val name = NF.makeId(dummySpan, "", "")
    val obj = NF.makeFunExpr(dummySpan, name, List(), List(), false)
    List(assignValue(arg, obj))
  }

  def assignArgs(constructor: String): (Option[List[Expr]], List[Stmt]) = {
    var additional = List[Stmt]()
    for (k <- NF.irSet) {
      k match {
        case IRFunctional(_, _, name, params, args, fds, vds, body) =>
          if (name.uniqueName == constructor) {
            val constructorFunction = coverage.functions(constructor)
            val psize = constructorFunction.params.size
            var args = List[Expr]()
            for (n <- 0 until psize)
              constructorFunction.getObjectProperties(n) match {
                case Some(props) =>
                  val fresh = NU.freshName("a")
                  val arg = NF.makeId(dummySpan, fresh, fresh)

                  var addstmt: List[Stmt] = assignEmptyObject(arg)
                  val temp = constructorFunction.getObjectConstructors(n)
                  if (temp.nonEmpty) {
                    val argumentConstructor = temp.head
                    // To prevent recursive generation, put a limit.
                    val hasArguments =
                      if (argumentConstructor == constructor) false
                      else true

                    addstmt = assignObject(false, n, arg, argumentConstructor, props, Map[String, Int](), hasArguments)
                  }
                  additional = additional ::: addstmt
                  args = args :+ NF.makeVarRef(dummySpan, arg)

                case None =>
                  args = args :+ NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(5).toString))
              }
            return (Some(args), additional)
          }
        case _ =>
      }
    }
    (None, additional)
  }

  def printResult(function: FunctionInfo, num: Int): Unit = {
    System.out.println("======================= Result =======================")
    for (i <- 0 until num by 1) {
      function.getObjectProperties(i) match {
        case Some(props) =>
          if (objectResult.contains("i" + i) && objectResult("i" + i) == "null")
            System.out.println("%6s => %6s".format("i" + i, "null"))
          else {
            if (i < primitiveResult.size) {
              val constructors = function.getObjectConstructors(i)
              if (constructors.head == "Array") {
                for (p <- 0 until props.head.toInt)
                  System.out.println("%6s => %6s".format("i" + i + "." + p, primitiveResult("i" + i + "." + p).toString))
              } else {
                for (p <- props)
                  System.out.println("%6s => %6s".format("i" + i + "." + p, primitiveResult("i" + i + "." + p).toString))
              }
            }
          }
        case None =>
          if (i < primitiveResult.size)
            System.out.println("%6s => %6s".format("i" + i, primitiveResult("i" + i).toString))
      }
    }
    System.out.println("======================================================")
  }
}
