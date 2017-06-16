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

import _root_.java.util.{ List => JList }
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.concolic.{ ConcolicNodeUtil => CNU }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.util._
import scala.collection.mutable.Map

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

class Instrumentor(program: IRRoot, coverage: Coverage) extends IRWalker {

  var debug = false

  def doit() = walk(program, IF.dummyIRId(CNU.freshConcolicName("Main"))).asInstanceOf[IRRoot]

  val dummyId = IF.dummyIRId(CNU.freshConcolicName("Instrumentor"))

  def storeEnvironment(info: ASTNode, v: IRId, env: IRId) =
    IRInternalCall(info, dummyId, "StoreEnvironment", List(v, env))

  def storeThis(info: ASTNode, env: IRId) =
    IRInternalCall(info, dummyId, "StoreThis", List(dummyId, env))

  def storeVariable(info: ASTNode, lhs: IRId, rhs: IRId) =
    IRInternalCall(info, dummyId, "StoreVariable", List(lhs, rhs))

  def executeAssignment(info: ASTNode, e: IRExpr, v: IRId, env: IRId) =
    IRSeq(info, List(
      storeEnvironment(info, v, env),
      IRInternalCall(info, dummyId, "ExecuteAssignment", List(e, v))
    ))

  def executeStore(info: ASTNode, obj: IRId, index: IRId, rhs: IRExpr, env: IRId) =
    IRSeq(info, List(
      storeEnvironment(info, obj, env),
      IRInternalCall(info, obj, "ExecuteStore", List(rhs, index))
    ))

  def executeCondition(info: ASTNode, e: IRExpr, env: IRId) =
    IRInternalCall(info, dummyId, "ExecuteCondition", List(e, env))
  def endCondition(info: ASTNode, env: IRId) =
    IRInternalCall(info, dummyId, "EndCondition", List(dummyId, env))

  def walkVarStmt(info: ASTNode, v: IRId, n: EJSNumber, env: IRId) =
    IRInternalCall(info, v, "WalkVarStmt", List(IRVal(n), env))

  def walkFunctional(info: ASTNode, node: IRFunctional): IRFunctional = node match {
    case IRFunctional(ast, i, name, params, args, fds, vds, body) =>
      var n = 0
      val varstmt = vds.foldLeft[List[IRStmt]](List(storeThis(info, name)))((list, vd) => {
        if (vd.fromParam) {
          n += 1
          list :+ walkVarStmt(vd, n - 1, name)
        } else
          list
      })
      IRFunctional(ast, i, name, params,
        args.map(walk(_, name).asInstanceOf[IRStmt]),
        fds.map(walk(_, name).asInstanceOf[IRFunDecl]),
        vds,
        varstmt ++ body.map(walk(_, name).asInstanceOf[IRStmt]))
    //vds.filter(fromParam(_)).map(walkVarStmt(_, name))++body.map(walk(_, name).asInstanceOf[IRStmt]))  
  }

  def fromParam(node: IRVarStmt) = node match {
    case IRVarStmt(info, lhs, fromparam) => fromparam
  }
  /* var x
   * ==>
   * var x;
   * walkVarStmt(x);
   */
  def walkVarStmt(node: IRVarStmt, num: Int, env: IRId): IRStmt = node match {
    case IRVarStmt(info, lhs, fromparam) => walkVarStmt(info, lhs, IF.makeNumber(false, num.toString, num), env)
  }

  def walk(node: Any, env: IRId): Any = {
    if (debug) println(node)

    node match {
      /* begin
     * ==>
     * Initialize();
     *
     * SymbolicExecutor should perform "Initialize()"
     * when it encounters SIRRoot.
     */
      case IRRoot(info, fds, vds, irs) =>
        IRRoot(info, fds.map(walk(_, env).asInstanceOf[IRFunDecl]),
          vds,
          irs.map(walk(_, env).asInstanceOf[IRStmt]))

      /* x = e
     * ==>
     * x = e
     * EXECUTE_ASSIGNMENT(x, e);
     *
     * x = e
     * SIRInternalCall(info, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteAssignment", e, Some(x))
     */
      case IRExprStmt(info, lhs, right, ref) =>
        IRSeq(info, List(node.asInstanceOf[IRStmt], executeAssignment(info, right, lhs, env)))

      /* x = x(x, x)
     * ==>
     * if (CHECK_FOCUS(env))
     *   x = x(x, x)
     * EXECUTE_RECURSIVE(x);
     *
     */
      case IRCall(info, lhs, fun, thisB, args) =>
        IRSeq(info, List(storeEnvironment(info, lhs, env), node.asInstanceOf[IRStmt]))

      /* x = function f (x, x) {s*}
     * ==>
     * if (CHECK_FOCUS(f))
     *   args = GET_INPUTS(args)
     *   s*
     * }
     *
     * SymbolicExecutor should perform "CHECK_FOCUS()" 
     */
      case IRFunExpr(info, lhs, ftn) =>
        IRFunExpr(info, lhs, walkFunctional(info, ftn).asInstanceOf[IRFunctional])

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
      case IRFunDecl(info, ftn) =>
        IRFunDecl(info, walkFunctional(info, ftn).asInstanceOf[IRFunctional])

      case IRFunctional(info, i, name, params, args, fds, vds, body) =>
        node

      /* return e?
     * ==>
     * return e?
     */
      case IRReturn(info, expr) =>
        node

      case IRSeq(info, stmts) =>
        IRSeq(info, stmts.map(walk(_, env).asInstanceOf[IRStmt]))

      /* if (e) then s (else s)?
     * ==>
     * EXECUTE_CONDITION(e);
     * if (e) then s (else s)?
     * END_CONDITION(e);
     *
     * SIRInternalCall(info, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteCondition", e, None)
     * if (e) then s (else s?)
     * SIRInternalCall(info, "<>Concolic<>Instrumentor", "<>Concolic<>EndCondition", e, None)
     */
      case IRIf(info, expr, trueB, falseB) =>
        if (info.isFromSource)
          IRSeq(info, List(
            executeCondition(info, expr, env),
            IRIf(info, expr, walk(trueB, env).asInstanceOf[IRStmt],
              falseB match { case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt]); case None => None }), endCondition(info, env)
          ))
        else
          IRIf(info, expr, trueB, falseB)

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
     *   SIRInternalCall(info, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteCondition", e, None)
     *   s
     * }
     * SIRInternalCall(info, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteCondition", e, None)
     *
     * SymbolicExecutor should perform "CHECK_LOOP()" 
     * and "EXECUTE_CONDITION(e)" when it encounters SIRWhile.
     */
      //TODO: Report condition only once
      case SIRWhile(info, cond, body) =>
        //coverage.printCondition(cond)
        SIRWhile(info, cond,
          IRSeq(info, List(executeCondition(info, cond, env), walk(body, env).asInstanceOf[IRStmt])))

      /* x[x] = e
     * ==>
     * x[x] = e
     * EXECUTE_STORE(x, x, e);
     *
     * x[x] = e
     * SIRInternalCall(info, x, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteStore",e, Some(x)) 
     */
      case IRStore(info, obj, index, rhs) =>
        //println("Instrumentor: "+new kr.ac.kaist.jsaf.nodes_util.JSIRUnparser(node.asInstanceOf[IRStore]).doit)
        index match {
          case id: IRId => IRSeq(info, List(node.asInstanceOf[IRStmt], executeStore(info, obj, id, rhs, env)))
          case IRVal(EJSString(str)) => IRSeq(info, List(node.asInstanceOf[IRStmt], executeStore(info, obj, IF.dummyIRId(str), rhs, env)))
          case IRVal(EJSNumber(text, num)) => IRSeq(info, List(node.asInstanceOf[IRStmt], executeStore(info, obj, IF.dummyIRId(text), rhs, env)))
        }

      case IRArray(info, lhs, elems) =>
        node

      case IRArrayNumber(info, lhs, elements) =>
        node

      case IRBreak(info, label) =>
        node

      case IRLabelStmt(info, label, stmt) =>
        IRLabelStmt(info, label, walk(stmt, env).asInstanceOf[IRStmt])

      case IRThrow(info, expr) =>
        node

      case IRTry(info, body, name, catchB, finallyB) =>
        IRTry(info, walk(body, env).asInstanceOf[IRStmt], name,
          catchB match {
            case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt])
            case None => None
          },
          finallyB match {
            case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt])
            case None => None
          })

      case IRStmtUnit(info, stmts) =>
        IRStmtUnit(info, stmts.map(walk(_, env).asInstanceOf[IRStmt]))

      case IRDelete(info, lhs, id) =>
        node

      case IRDeleteProp(info, lhs, obj, index) =>
        node

      case IRObject(info, lhs, members, proto) =>
        node

      case IRArgs(info, lhs, elems) =>
        node

      /* Instrument Global<>toNumber internal function to store new variable in symbolic memory. */
      case IRInternalCall(info, lhs, fun, first, second) => fun.getOriginalName match {
        case "<>Global<>toNumber" => first match {
          case id: IRId => IRSeq(info, List(storeVariable(info, lhs, id), node.asInstanceOf[IRInternalCall]))
          case _ => node
        }
        case _ => node
      }
      //node

      case IRNew(info, lhs, fun, args) =>
        IRSeq(info, List(storeEnvironment(info, lhs, env), node.asInstanceOf[IRStmt]))
      //node

      case IREval(info, lhs, arg) =>
        node

      case IRWith(info, id, stmt) =>
        IRWith(info, id, walk(stmt, env).asInstanceOf[IRStmt])

      case _ => node
    }
  }

  def debugOn = debug = true
}
