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

import com.microsoft.z3._
import scala.collection.mutable.{ HashMap, Map => MMap }

final class Z3 {

  case class TestFailedException() extends Exception("Check FAILED")

  var debug: Boolean = false

  def ConstraintSolver(
    ctx: Context,
    conslist: List[ConstraintForm],
    inum: Integer,
    objects: MMap[Integer, TypeInfo],
    thisProperties: List[String]
  ): HashMap[String, Integer] = {
    if (debug) {
      System.out.println("ConstraintSolver")
    }

    var mutConslist = conslist

    val exprMap: MMap[String, IntExpr] = scala.collection.mutable.HashMap[String, IntExpr]()
    val solver: Solver = ctx.mkSolver
    println(s"Solving constraints $mutConslist")
    while (mutConslist.nonEmpty) {
      val constraint: ConstraintForm = mutConslist.head
      //      println(s"Solving constraint ${constraint.getLhs} ${constraint.op} ${constraint.getRhs}")
      mutConslist = mutConslist.drop(1)
      if (constraint.getOp.isDefined) {
        val op: String = constraint.getOp.get
        val lhs: String = constraint.getLhs.getValue.get
        exprMap.put(lhs, ctx.mkIntConst(lhs))
        if (constraint.getRhs.isDefined) {
          val c: ConstraintForm = constraint.getRhs.get
          val rhs: String = c.getLhs.getValue.get
          if (rhs.contains("s") || rhs.contains("i") || rhs.contains("this")) exprMap.put(rhs, ctx.mkIntConst(rhs))
          else exprMap.put(rhs, ctx.mkInt(rhs.toInt))
          op.charAt(0) match {
            case '=' =>
              if (op.length > 1 && op.charAt(1) == '=') solver.assert_(ctx.mkEq(exprMap.get(lhs).get, exprMap.get(rhs).get))
              else {
                if (c.getOp.isDefined) {
                  if (c.getRhs.isDefined) {
                    val v: String = c.getRhs.get.getLhs.getValue.get
                    if (v.contains("s") || v.contains("i") || v.contains("this")) exprMap.put(v, ctx.mkIntConst(v))
                    else exprMap.put(v, ctx.mkInt(v.toInt))
                    val constraint_op: String = c.getOp.get
                    constraint_op.charAt(0) match {
                      case '+' =>
                        solver.assert_(ctx.mkEq(exprMap.get(lhs).get, ctx.mkAdd(exprMap.get(rhs).get, exprMap.get(v).get)))
                      case '-' =>
                        solver.assert_(ctx.mkEq(exprMap.get(lhs).get, ctx.mkSub(exprMap.get(rhs).get, exprMap.get(v).get)))
                      case '*' =>
                        solver.assert_(ctx.mkEq(exprMap.get(lhs).get, ctx.mkMul(exprMap.get(rhs).get, exprMap.get(v).get)))
                      case '/' =>
                        solver.assert_(ctx.mkEq(exprMap.get(lhs).get, ctx.mkDiv(exprMap.get(rhs).get, exprMap.get(v).get)))
                      case '%' =>
                        solver.assert_(ctx.mkEq(exprMap.get(lhs).get, ctx.mkMod(exprMap.get(rhs).get, exprMap.get(v).get)))
                      case '&' =>
                        val x: BitVecExpr = ctx.mkInt2BV(1, exprMap.get(rhs).get)
                        val y: BitVecExpr = ctx.mkInt2BV(1, exprMap.get(v).get)
                        solver.assert_(ctx.mkEq(exprMap.get(lhs).get, ctx.mkBV2Int(ctx.mkBVAND(x, y), false)))
                      case '!' =>
                        solver.assert_(ctx.mkITE(ctx.mkDistinct(exprMap.get(rhs).get, exprMap.get(v).get), ctx.mkDistinct(exprMap.get(lhs).get, ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs).get, ctx.mkInt(0))).asInstanceOf[BoolExpr])
                      case '=' =>
                        solver.assert_(ctx.mkITE(ctx.mkEq(exprMap.get(rhs).get, exprMap.get(v).get), ctx.mkDistinct(exprMap.get(lhs).get, ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs).get, ctx.mkInt(0))).asInstanceOf[BoolExpr])
                      case '>' =>
                        val condition: BoolExpr =
                          if (constraint_op.length > 1 && constraint_op.charAt(1) == '=')
                            ctx.mkGe(exprMap.get(rhs).get, exprMap.get(v).get)
                          else
                            ctx.mkGt(exprMap.get(rhs).get, exprMap.get(v).get)
                        solver.assert_(ctx.mkITE(condition, ctx.mkDistinct(exprMap.get(lhs).get, ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs).get, ctx.mkInt(0))).asInstanceOf[BoolExpr])
                      case '<' =>
                        val condition =
                          if (constraint_op.length > 1 && constraint_op.charAt(1) == '=')
                            ctx.mkLe(exprMap.get(rhs).get, exprMap.get(v).get)
                          else
                            ctx.mkLt(exprMap.get(rhs).get, exprMap.get(v).get)
                        solver.assert_(ctx.mkITE(condition, ctx.mkDistinct(exprMap.get(lhs).get, ctx.mkInt(0)), ctx.mkEq(exprMap.get(lhs).get, ctx.mkInt(0))).asInstanceOf[BoolExpr])
                      case _ =>
                        System.out.println("Not yet supported")
                        throw new TestFailedException
                    }
                  } else {
                    System.out.println("Wrong constraint form" + c)
                    throw new TestFailedException
                  }
                } else solver.assert_(ctx.mkEq(exprMap.get(lhs).get, exprMap.get(rhs).get))
              }
            case '<' =>
              if (op.length > 1 && op.charAt(1) == '=') {
                solver.assert_(ctx.mkLe(exprMap.get(lhs).get, exprMap.get(rhs).get))
              } else {
                solver.assert_(ctx.mkLt(exprMap.get(lhs).get, exprMap.get(rhs).get))
              }
            case '>' =>
              if (op.length > 1 && op.charAt(1) == '=') {
                solver.assert_(ctx.mkGe(exprMap.get(lhs).get, exprMap.get(rhs).get))
              } else {
                solver.assert_(ctx.mkGt(exprMap.get(lhs).get, exprMap.get(rhs).get))
              }
            case '!' =>
              if (op.length > 1 && op.charAt(1) == '=') {
                solver.assert_(ctx.mkDistinct(exprMap.get(lhs).get, exprMap.get(rhs).get))
              } else {
                System.out.println("Wrong constraint form" + op)
                throw new TestFailedException
              }
            case _ =>
              System.out.println("Not yet supported")
              throw new TestFailedException
          }
        } else {
          System.out.println("Wrong constraint form" + constraint)
          throw new TestFailedException
        }
      } else {
        System.out.println("Wrong constraint form" + constraint)
        throw new TestFailedException
      }
    }
    if (Status.SATISFIABLE eq solver.check) {
      val model: Model = solver.getModel
      if (debug) {
        System.out.println("Solver = " + solver)
        System.out.println("Model = " + model)
      }
      val result: HashMap[String, Integer] = new HashMap[String, Integer]
      if (exprMap.contains("this")) {
        result.put("this", model.getConstInterp(exprMap.get("this").get).toString.toInt)
      }
      var j: Int = 0
      while (j < thisProperties.size) {
        result.put("this." + thisProperties(j), model.getConstInterp(exprMap.get("this." + thisProperties(j)).get).toString.toInt)
        j += 1
      }
      var i: Int = 0
      while (i < inum) {
        if (exprMap.contains("i" + i)) {
          result.put("i" + i, model.getConstInterp(exprMap.get("i" + i).get).toString.toInt)
        }
        if (objects.contains(i)) {
          if (objects(i).getConstructor == "Array") {
            val length: String = objects(i).getProperties.head
            var j: Int = 0
            while (j < length.toInt) {
              result.put("i" + i + "." + Integer.toString(j), model.getConstInterp(exprMap.get("i" + i + "." + Integer.toString(j)).get).toString.toInt)
              j += 1
            }
          } else {
            val properties: List[String] = objects(i).properties
            var j: Int = 0
            while (j < properties.size) {
              result.put("i" + i + "." + properties(j), model.getConstInterp(exprMap.get("i" + i + "." + properties(j)).get).toString.toInt)
              j += 1
            }
          }
        }
        i += 1
      }
      result
    } else {
      System.out.println(solver.check)
      throw new TestFailedException
    }
  }

  def solve(
    constraints: List[ConstraintForm],
    inum: Integer,
    objects: Option[MMap[Integer, TypeInfo]],
    thisProperties: List[String]
  ): Option[HashMap[String, Integer]] = {
    try {
      val cfg: java.util.HashMap[String, String] = new java.util.HashMap[String, String]
      cfg.put("model", "true")
      val ctx: Context = new Context(cfg)
      if (constraints.nonEmpty) {
        Some[HashMap[String, Integer]](this.ConstraintSolver(ctx, constraints, inum, objects.get, thisProperties))
      } else {
        None
      }
    } catch {
      case ex: Z3Exception =>
        System.out.println("TEST CASE FAILED: " + ex.getMessage)
        System.out.println("Stack trace: ")
        ex.printStackTrace(System.out)
        None
      case ex: TestFailedException =>
        System.out.println(s"Error solving constraints $constraints")
        None
      case ex: Exception =>
        System.out.println("Unknown Exception: " + ex.getMessage)
        None
    }
  }
}
