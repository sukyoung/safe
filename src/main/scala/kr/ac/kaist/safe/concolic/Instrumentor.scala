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

import _root_.java.util.{ List => JList }
import scala.collection.immutable.Map
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.concolic.{ ConcolicNodeUtil => CNU }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.util._

/* Instrumented IR statements:
 *   [IRRoot]
 *   [IRExprStmt]
 *   [IRCall]
 *   [IRFunExpr]
 *   [IRFunctional]
 *   [IRFunDecl]
 *   [IRReturn]
 *   [IRVarStmt]
 *   [IRSeq]
 *   [IRIf]
 *   [IRWhile]
 *   [IRStmtUnit]
 *   [IRStore]
 * Instead of the 'end' statement in LCT, we have to report the terminal
 * information at the end of Interpreter.
 *
 * IR statements in consideration to instrument:
 *   [IRArray]
 *   [IRBreak]
 *   [IRLabelStmt]
 *   [IRThrow]
 *   [IRTry]
 *
 * Not yet targeted for instrumentation which is related with function call/object:
 *   [IRDelete]
 *   [IRDeleteProp]
 *   [IRObject]
 *   [IRArgs]
 *   [IRInternalCall]
 *   [IRNew]
 *
 * Not instrumented IR statements:
 *   [IREval]
 *   [IRWith]
 */
class Instrumentor(program: IRRoot, coverage: Coverage) {

  var debug = false

  lazy val result: IRRoot = doit

  private def doit: IRRoot = walk(program, IF.dummyIRId(CNU.freshConcolicName("Main"))).asInstanceOf[IRRoot]

  val dummyId = IF.dummyIRId(CNU.freshConcolicName("Instrumentor"))

  private def storeEnvironment(ast: ASTNode, v: IRId, env: IRId): IRInternalCall =
    IRInternalCall(ast, dummyId, CNU.freshConcolicName("StoreEnvironment"), List(v, env))

  private def storeThis(ast: ASTNode, env: IRId): IRInternalCall =
    IRInternalCall(ast, dummyId, CNU.freshConcolicName("StoreThis"), List(dummyId, env))

  private def storeVariable(ast: ASTNode, lhs: IRId, rhs: IRId): IRInternalCall =
    IRInternalCall(ast, dummyId, CNU.freshConcolicName("StoreVariable"), List(lhs, rhs))

  private def executeAssignment(
    ast: ASTNode,
    e: IRExpr,
    v: IRId,
    env: IRId
  ): IRSeq =
    IRSeq(ast, List(
      storeEnvironment(ast, v, env),
      IRInternalCall(ast, dummyId, CNU.freshConcolicName("ExecuteAssignment"), List(e, v))
    ))

  private def executeStore(
    ast: ASTNode,
    obj: IRId,
    index: IRId,
    rhs: IRExpr,
    env: IRId
  ): IRSeq =
    IRSeq(ast, List(
      storeEnvironment(ast, obj, env),
      IRInternalCall(ast, obj, CNU.freshConcolicName("ExecuteStore"), List(rhs, index))
    ))

  private def executeCondition(ast: ASTNode, e: IRExpr, env: IRId): IRInternalCall =
    IRInternalCall(ast, dummyId, CNU.freshConcolicName("ExecuteCondition"), List(e, env))

  private def endCondition(ast: ASTNode, env: IRId): IRInternalCall =
    IRInternalCall(ast, dummyId, CNU.freshConcolicName("EndCondition"), List(dummyId, env))

  private def walkVarStmt(
    ast: ASTNode,
    v: IRId,
    n: EJSNumber,
    env: IRId
  ): IRInternalCall =
    IRInternalCall(ast, v, CNU.freshConcolicName("WalkVarStmt"), List(IRVal(n), env))

  private def walkFunctional(ast: ASTNode, node: IRFunctional): IRFunctional = node match {
    case IRFunctional(astFun, i, name, params, args, fds, vds, body) =>
      var n = 0
      val varstmt = vds.foldLeft[List[IRStmt]](List(storeThis(ast, name)))((list, vd) => {
        if (vd.fromParam) {
          n += 1
          list :+ walkVarStmt(vd, n - 1, name)
        } else
          list
      })
      IRFunctional(astFun, i, name, params,
        args.map(walk(_, name).asInstanceOf[IRStmt]),
        fds.map(walk(_, name).asInstanceOf[IRFunDecl]),
        vds,
        varstmt ++ body.map(walk(_, name).asInstanceOf[IRStmt]))
    //vds.filter(fromParam(_)).map(walkVarStmt(_, name))++body.map(walk(_, name).asInstanceOf[IRStmt]))
  }

  private def fromParam(node: IRVarStmt): Boolean = node match {
    case IRVarStmt(ast, lhs, fromparam) => fromparam
  }

  /* var x
   * ==>
   * var x;
   * walkVarStmt(x);
   */
  private def walkVarStmt(node: IRVarStmt, num: Int, env: IRId): IRStmt = node match {
    case IRVarStmt(ast, lhs, fromparam) => walkVarStmt(ast, lhs, IF.makeNumber(num.toString, num), env)
  }

  private def walkRoot(root: IRRoot, env: IRId): IRRoot = {
    IRRoot(root.ast, root.fds.map(walk(_, env).asInstanceOf[IRFunDecl]),
      root.vds, root.irs.map(walk(_, env).asInstanceOf[IRStmt]))
  }

  def walk(node: IRNode, env: IRId): IRNode = {
    if (debug) {
      println(node)
    }

    node match {
      /* begin
     * ==>
     * Initialize();
     *
     * SymbolicExecutor should perform "Initialize()"
     * when it encounters SIRRoot.
     */
      case r: IRRoot =>
        walkRoot(r, env)

      /* x = e
     * ==>
     * x = e
     * EXECUTE_ASSIGNMENT(x, e);
     *
     * x = e
     * SIRInternalCall(ast, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteAssignment", e, Some(x))
     */
      case IRExprStmt(ast, lhs, right, ref) =>
        IRSeq(ast, List(node.asInstanceOf[IRStmt], executeAssignment(ast, right, lhs, env)))

      /* x = x(x, x)
     * ==>
     * if (CHECK_FOCUS(env))
     *   x = x(x, x)
     * EXECUTE_RECURSIVE(x);
     *
     */
      case IRCall(ast, lhs, fun, thisB, args) =>
        IRSeq(ast, List(storeEnvironment(ast, lhs, env), node.asInstanceOf[IRStmt]))

      /* x = function f (x, x) {s*}
     * ==>
     * if (CHECK_FOCUS(f))
     *   args = GET_INPUTS(args)
     *   s*
     * }
     *
     * SymbolicExecutor should perform "CHECK_FOCUS()"
     */
      case IRFunExpr(ast, lhs, ftn) =>
        IRFunExpr(ast, lhs, walkFunctional(ast, ftn).asInstanceOf[IRFunctional])

      /* function f (x, x) {s*}
     * ==>
     * ADD_FUNCTION(f)
     * if (CHECK_FOCUS(f))
     *   args = GET_INPUTS(args)
     *   s*
     * }
     *
     * SymbolicExecutor should perform "CHECK_FOCUS()"
     */
      case IRFunDecl(ast, ftn) =>
        IRFunDecl(ast, walkFunctional(ast, ftn).asInstanceOf[IRFunctional])

      case IRFunctional(ast, i, name, params, args, fds, vds, body) =>
        node

      /* return e?
     * ==>
     * return e?
     */
      case IRReturn(ast, expr) =>
        node

      case IRSeq(ast, stmts) =>
        IRSeq(ast, stmts.map(walk(_, env).asInstanceOf[IRStmt]))

      /* if (e) then s (else s)?
     * ==>
     * EXECUTE_CONDITION(e);
     * if (e) then s (else s)?
     * END_CONDITION(e);
     *
     * SIRInternalCall(ast, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteCondition", e, None)
     * if (e) then s (else s?)
     * SIRInternalCall(ast, "<>Concolic<>Instrumentor", "<>Concolic<>EndCondition", e, None)
     */
      case IRIf(ast, expr, trueB, falseB) =>
        IRSeq(ast, List(
          executeCondition(ast, expr, env),
          IRIf(ast, expr, walk(trueB, env).asInstanceOf[IRStmt], falseB.map(walk(_, env).asInstanceOf[IRStmt])),
          endCondition(ast, env)
        ))

      /* while (e) s
     * ==>
     * while (e) {
     *   EXECUTE_CONDITION(e);
     *   CHECK_LOOP();
     *   s
     * }
     * EXECUTE_CONDITION(e);
     *
     * while (e) {
     *   SIRInternalCall(ast, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteCondition", e, None)
     *   s
     * }
     * SIRInternalCall(ast, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteCondition", e, None)
     *
     * SymbolicExecutor should perform "CHECK_LOOP()"
     * and "EXECUTE_CONDITION(e)" when it encounters SIRWhile.
     */
      //TODO: Report condition only once
      case IRWhile(ast, cond, body, breakLabel, contLabel) =>
        //coverage.printCondition(cond)
        IRWhile(ast, cond,
          IRSeq(ast, List(executeCondition(ast, cond, env), walk(body, env).asInstanceOf[IRStmt])),
          breakLabel, contLabel)

      /* x[x] = e
     * ==>
     * x[x] = e
     * EXECUTE_STORE(x, x, e);
     *
     * x[x] = e
     * SIRInternalCall(ast, x, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteStore",e, Some(x))
     */
      case IRStore(ast, obj, index, rhs) =>
        //println("Instrumentor: "+new kr.ac.kaist.jsaf.nodes_util.JSIRUnparser(node.asInstanceOf[IRStore]).doit)
        index match {
          case id: IRId => IRSeq(ast, List(node.asInstanceOf[IRStmt], executeStore(ast, obj, id, rhs, env)))
          case IRVal(EJSString(str)) => IRSeq(ast, List(node.asInstanceOf[IRStmt], executeStore(ast, obj, IF.dummyIRId(str), rhs, env)))
          case IRVal(EJSNumber(text, num)) => IRSeq(ast, List(node.asInstanceOf[IRStmt], executeStore(ast, obj, IF.dummyIRId(text), rhs, env)))
        }

      case IRArray(ast, lhs, elems) =>
        node

      case IRArrayNumber(ast, lhs, elements) =>
        node

      case IRBreak(ast, label) =>
        node

      case IRLabelStmt(ast, label, stmt) =>
        IRLabelStmt(ast, label, walk(stmt, env).asInstanceOf[IRStmt])

      case IRThrow(ast, expr) =>
        node

      case IRTry(ast, body, name, catchB, finallyB) =>
        IRTry(ast, walk(body, env).asInstanceOf[IRStmt], name,
          catchB match {
            case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt])
            case None => None
          },
          finallyB match {
            case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt])
            case None => None
          })

      case IRStmtUnit(ast, stmts) =>
        IRStmtUnit(ast, stmts.map(walk(_, env).asInstanceOf[IRStmt]))

      case IRDelete(ast, lhs, id) =>
        node

      case IRDeleteProp(ast, lhs, obj, index) =>
        node

      case IRObject(ast, lhs, members, proto) =>
        node

      case IRArgs(ast, lhs, elems) =>
        node

      /* Instrument Global<>toNumber internal function to store new variable in symbolic memory. */
      case IRInternalCall(ast, lhs, fun, args) if args.nonEmpty => fun match {
        case "<>Global<>toNumber" => args.head match {
          case id: IRId => IRSeq(ast, List(storeVariable(ast, lhs, id), node.asInstanceOf[IRInternalCall]))
          case _ => node
        }
        case _ => node
      }
      //node

      case IRNew(ast, lhs, fun, args) =>
        IRSeq(ast, List(storeEnvironment(ast, lhs, env), node.asInstanceOf[IRStmt]))
      //node

      case IREval(ast, lhs, arg) =>
        node

      case IRWith(ast, id, stmt) =>
        IRWith(ast, id, walk(stmt, env).asInstanceOf[IRStmt])

      case _ => node
    }
  }

  def debugOn(): Unit = debug = true
}
