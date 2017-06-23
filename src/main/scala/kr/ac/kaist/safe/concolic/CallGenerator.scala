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

import _root_.java.math.BigInteger
import kr.ac.kaist.safe.concolic.{ ConcolicNodeUtil => CNU }
import kr.ac.kaist.safe.errors.error.ConcolicError
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.util.{ Coverage, NodeUtil => NU }
import kr.ac.kaist.safe.util.{ NodeRelation => NR }

import scala.collection.mutable.HashMap
import scala.util.Random

class CallGenerator(coverage: Coverage) {
  val dummySpan = NU.dummySpan("CallGenerator")

  var input: Map[String, Id] = null
  var functions: HashMap[String, FunctionInfo] = coverage.functions

  var additional: List[Stmt] = null

  val dummyId = IF.dummyIRId(CNU.freshConcolicName("CallGenerator"))
  val none: Option[IRId] = None
  // TODO Not sure if startConcolic and endConcolic were ported correctly
  val startConcolic = new IRInternalCall(NF.dummyAST, dummyId, "StartConcolic", List(dummyId))
  val endConcolic = new IRInternalCall(NF.dummyAST, dummyId, "EndConcolic", List(dummyId))

  def setupCall(target: String): Option[IRStmt] = {
    input = coverage.input
    additional = coverage.additional

    if (target == null) return None
    var env = List[(String, IRId)]()
    // For prototype functions.
    if (target.contains(".")) {
      // Tokenize target name to generate each object and function.
      val token = target.substring(0, target.indexOf("<")).split('.')
      if (token.length > 3)
        throw new ConcolicError("Only a.x function forms are supported.")
      val constructors = functions(target).getThisConstructors
      //TODO: Handle multiple type
      val first = constructors(0)
      val second = if (target.contains("prototype")) token(2) else token(1)

      val objRef = input.get("this") match {
        case Some(id) => id
        case None => makeFunApp(first, true) match {
          case Some(x) =>
            val obj = NF.makeNew(dummySpan, x)
            val fresh = NU.freshName("this")
            val objRef = NF.makeId(dummySpan, fresh, fresh)

            var stmts = List[Stmt]()
            stmts = stmts :+ (new ConcolicSolver(coverage)).assignValue(objRef, obj)

            stmts = functions(target).getThisProperties.foldLeft[List[Stmt]](stmts)((list, p) => {
              if (input.contains("this." + p)) {
                val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, objRef.text, objRef.text))
                val lhs = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, p, p))
                val rhs = NF.makeIntLiteral(dummySpan, new BigInteger(input("this." + p).toString))
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
            })
            additional = additional ::: stmts
            objRef
          case None => return None
        }
      }

      val ref = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, objRef.text, objRef.text))
      val fun = NF.makeDot(dummySpan, ref, NF.makeId(dummySpan, second, second))

      makeArgs(target, false) match {
        case Some(args) =>
          val ir = additional.foldLeft[List[IRStmt]](List())((list, s) => {
            list :+ IRGenerator.additional2ir(s, env)
          })
          val funapp = NF.makeFunApp(dummySpan, fun, args)
          val res = IF.makeTId(funapp, NU.IGNORE_NAME)
          val funir = IRGenerator.funapp2ir(funapp, env, res, target)
          val core = List(startConcolic, funir, endConcolic)
          // Make the cutline to calculate coverage.
          //val cutline = new IRInternalCall(makeSpanInfo(false, span), lhs, fun, arg1, toJavaOption(arg2))
          Some(new IRStmtUnit(NF.dummyAST, ir ::: core))
        case None => None
      }
    } else {
      makeFunApp(target, false) match {
        case Some(funapp) =>
          val ir = additional.foldLeft[List[IRStmt]](List())((list, s) => {
            list :+ IRGenerator.additional2ir(s, env)
          })
          val res = IF.makeTId(funapp, NU.IGNORE_NAME)
          val funir = IRGenerator.funapp2ir(funapp, env, res, target)
          val core = List(startConcolic, funir, endConcolic)

          Some(new IRStmtUnit(NF.dummyAST, ir ::: core))
        case None => None
      }
    }
  }

  def makeFunApp(target: String, isObject: Boolean): Option[FunApp] = {
    makeArgs(target, isObject) match {
      case Some(args) =>
        val fun = NF.makeVarRef(dummySpan, NF.makeId(dummySpan, target, target))
        Some(NF.makeFunApp(dummySpan, fun, args))
      case None =>
        None
    }
  }

  def makeArgs(target: String, isObject: Boolean): Option[List[Expr]] = {
    for (k <- NF.irSet) {
      k match {
        case IRFunctional(_, _, name, params, args, fds, vds, body) =>
          if (name.uniqueName == target) {
            val p = functions(target).params.size
            // calculate the number of input to generate
            if (!isObject)
              coverage.setInputNumber(p)

            //TODO: Handle multiple types in arugments
            var args = List[Expr]()
            for (n <- 0 until p)
              functions(target).getObjectProperties(n) match {
                case Some(props) =>
                  if (n < input.size && !isObject)
                    args = args :+ NF.makeVarRef(dummySpan, input("i" + n))
                  else {
                    val fresh = NU.freshName("a")
                    val arg = NF.makeId(dummySpan, fresh, fresh)

                    var addstmt: List[Stmt] = (new ConcolicSolver(coverage)).assignEmptyObject(arg)
                    var constructors = functions(target).getObjectConstructors(n)
                    if (constructors.nonEmpty)
                      addstmt = (new ConcolicSolver(coverage)).assignObject(false, n, arg, constructors(0), props, Map[String, Int](), true)
                    additional = additional ::: addstmt
                    args = args :+ NF.makeVarRef(dummySpan, arg)
                  }
                case None =>
                  args =
                    if (n < input.size && !isObject)
                      args :+ NF.makeVarRef(dummySpan, input("i" + n))
                    else
                      args :+ NF.makeIntLiteral(dummySpan, new BigInteger(new Random().nextInt(5).toString))
              }
            return Some(args)
          }
        case _ =>
      }
    }
    return None
  }
}
