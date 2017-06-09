/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe.concolic

import _root_.java.util.{List => JList}
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.concolic.{ConcolicNodeUtil => CNU}
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{IRFactory => IF}
import kr.ac.kaist.safe.util.Coverage
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
    IRSeq(info, List(storeEnvironment(info, v, env),
        IRInternalCall(info, dummyId, "ExecuteAssignment", List(e, v))))

  def executeStore(info: ASTNode, obj: IRId, index: IRId, rhs: IRExpr, env: IRId) =
    IRSeq(info, List(storeEnvironment(info, obj, env),
      IRInternalCall(info, obj, "ExecuteStore", List(rhs, index))))

  def executeCondition(info: ASTNode, e: IRExpr, env: IRId) =
    IRInternalCall(info, dummyId, "ExecuteCondition", List(e, env))
  def endCondition(info: ASTNode, env: IRId) =
    IRInternalCall(info, dummyId, "EndCondition", List(dummyId, env))

  def walkVarStmt(info: ASTNode, v: IRId, n: IRNumber, env: IRId) =
    IRInternalCall(info, v, "WalkVarStmt", List(n, env))

  def walkFunctional(info: ASTNode, node: IRFunctional): IRFunctional = node match {
    case IRFunctional(ast, fromSource, name, params, args, fds, vds, body) =>
      var n = 0
      val varstmt = vds.foldLeft[List[IRStmt]](List(storeThis(info, name)))((list, vd) => {
        if (vd.fromParam) {
          n += 1
          list:+walkVarStmt(vd, n-1, name)
        }
        else 
          list
      })
      SIRFunctional(i, name, params,
        args.map(walk(_, name).asInstanceOf[IRStmt]),
        fds.map(walk(_, name).asInstanceOf[IRFunDecl]),
        vds,
        varstmt++body.map(walk(_, name).asInstanceOf[IRStmt]))
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
  def walkVarStmt(node: IRVarStmt, num: Int, env: IRId):IRStmt = node match {
    case IRVarStmt(info, lhs, fromparam) => walkVarStmt(info, lhs, IF.makeNumber(false, num.toString, num), env)
  }

  def walk(node: Any, env: IRId):Any = {
    if (debug) println(node)
    
    node match {
    /* begin
     * ==>
     * Initialize();
     *
     * SymbolicExecutor should perform "Initialize()"
     * when it encounters SIRRoot.
     */
    case SIRRoot(info, fds, vds, irs) =>
      SIRRoot(info, fds.map(walk(_, env).asInstanceOf[IRFunDecl]),
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
    case SIRExprStmt(info, lhs, right, ref) =>
      SIRSeq(info, List(node.asInstanceOf[IRStmt], executeAssignment(info, right, lhs, env)))

    /* x = x(x, x)
     * ==>
     * if (CHECK_FOCUS(env))
     *   x = x(x, x)
     * EXECUTE_RECURSIVE(x);
     *
     */
    case SIRCall(info, lhs, fun, thisB, args) =>
      SIRSeq(info, List(storeEnvironment(info, lhs, env), node.asInstanceOf[IRStmt]))

    /* x = function f (x, x) {s*}
     * ==>
     * if (CHECK_FOCUS(f))
     *   args = GET_INPUTS(args)
     *   s*
     * }
     *
     * SymbolicExecutor should perform "CHECK_FOCUS()" 
     */
    case SIRFunExpr(info, lhs, ftn) =>  
      SIRFunExpr(info, lhs, walkFunctional(info, ftn).asInstanceOf[IRFunctional])

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
    case SIRFunDecl(info, ftn) => 
      SIRFunDecl(info, walkFunctional(info, ftn).asInstanceOf[IRFunctional])

    case SIRFunctional(i, name, params, args, fds, vds, body) => 
      node

    /* return e?
     * ==>
     * return e?
     */
    case SIRReturn(info, expr) => 
      node

    case SIRSeq(info, stmts) =>
      SIRSeq(info, stmts.map(walk(_, env).asInstanceOf[IRStmt]))
    
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
    case SIRIf(info, expr, trueB, falseB) =>
      if (info.isFromSource)
        SIRSeq(info, List(executeCondition(info, expr, env), 
          SIRIf(info, expr, walk(trueB, env).asInstanceOf[IRStmt],
            falseB match { case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt]); case None => None}), endCondition(info, env)))
      else
        SIRIf(info, expr, trueB, falseB)

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
        SIRSeq(info, List(executeCondition(info, cond, env), walk(body, env).asInstanceOf[IRStmt])))

    /* x[x] = e
     * ==>
     * x[x] = e
     * EXECUTE_STORE(x, x, e);
     *
     * x[x] = e
     * SIRInternalCall(info, x, "<>Concolic<>Instrumentor", "<>Concolic<>ExecuteStore",e, Some(x)) 
     */
    case SIRStore(info, obj, index, rhs) => 
      //println("Instrumentor: "+new kr.ac.kaist.jsaf.nodes_util.JSIRUnparser(node.asInstanceOf[IRStore]).doit)
      index match {
        case id: IRId => SIRSeq(info, List(node.asInstanceOf[IRStmt], executeStore(info, obj, id, rhs, env))) 
        case SIRString(_, str) => SIRSeq(info, List(node.asInstanceOf[IRStmt], executeStore(info, obj, IF.dummyIRId(str), rhs, env)))
        case SIRNumber(_, text, num) => SIRSeq(info, List(node.asInstanceOf[IRStmt], executeStore(info, obj, IF.dummyIRId(text), rhs, env)))
      }

    case SIRArray(info, lhs, elems) => 
      node
    
    case SIRArrayNumber(info, lhs, elements) => 
      node

    case SIRBreak(info, label) => 
      node

    case SIRLabelStmt(info, label, stmt) =>
      SIRLabelStmt(info, label, walk(stmt, env).asInstanceOf[IRStmt])

    case SIRThrow(info, expr) => 
      node

    case SIRTry(info, body, name, catchB, finallyB) =>
      SIRTry(info, walk(body, env).asInstanceOf[IRStmt], name,
             catchB match { case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt])
                            case None => None },
             finallyB match { case Some(s) => Some(walk(s, env).asInstanceOf[IRStmt])
                              case None => None })

    case SIRStmtUnit(info, stmts) =>
      SIRStmtUnit(info, stmts.map(walk(_, env).asInstanceOf[IRStmt]))

    case SIRDelete(info, lhs, id) => 
      node

    case SIRDeleteProp(info, lhs, obj, index) => 
      node

    case SIRObject(info, lhs, members, proto) => 
      node

    case SIRArgs(info, lhs, elems) => 
      node

    /* Instrument Global<>toNumber internal function to store new variable in symbolic memory. */
    case SIRInternalCall(info, lhs, fun, first, second) => fun.getOriginalName match {
      case "<>Global<>toNumber" => first match {
        case id: IRId => SIRSeq(info, List(storeVariable(info, lhs, id), node.asInstanceOf[IRInternalCall]))  
        case _ => node
      }
      case _ => node
    }
      //node

    case SIRNew(info, lhs, fun, args) => 
      SIRSeq(info, List(storeEnvironment(info, lhs, env), node.asInstanceOf[IRStmt]))
      //node

    case SIREval(info, lhs, arg) => 
      node

    case SIRWith(info, id, stmt) =>
      SIRWith(info, id, walk(stmt, env).asInstanceOf[IRStmt])

    case _ => node
    }
  }

  def debugOn = debug = true
}
