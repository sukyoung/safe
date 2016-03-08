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

/**
 * *************************
 * JavaScript IR
 * ECMAScript 5
 * *************************
 */

package kr.ac.kaist.safe.nodes

import java.lang.Double
import java.math.BigInteger
import kr.ac.kaist.safe.safe_util.Span

abstract class IRNode(override val info: IRNodeInfo)
  extends Node(info: NodeInfo)

/**
 * IRRoot ::= Statement*
 */
case class IRRoot(override val info: IRNodeInfo, val fds: List[IRFunDecl], val vds: List[IRVarStmt], val irs: List[IRStmt])
  extends IRNode(info: IRNodeInfo)
/**
 * Statement
 */
abstract class IRStmt(override val info: IRNodeInfo)
  extends IRNode(info: IRNodeInfo)
abstract class IRAssign(override val info: IRNodeInfo, val lhs: IRId)
  extends IRStmt(info: IRNodeInfo)

/**
 * Expression
 * Stmt ::= x = e
 */
case class IRExprStmt(override val info: IRNodeInfo, override val lhs: IRId, val right: IRExpr, val ref: Boolean = false)
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Delete expression
 * Stmt ::= x = delete y
 */
case class IRDelete(override val info: IRNodeInfo, override val lhs: IRId, val id: IRId)
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Delete property expression
 * Stmt ::= x = delete y[e]
 */
case class IRDeleteProp(override val info: IRNodeInfo, override val lhs: IRId, val obj: IRId, val index: IRExpr)
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Object literal
 * Stmt ::= x = { member, ... }
 */
case class IRObject(override val info: IRNodeInfo, override val lhs: IRId, val members: List[IRMember], val proto: Option[IRId])
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Array literal
 * Stmt ::= x = [ e, ... ]
 */
case class IRArray(override val info: IRNodeInfo, override val lhs: IRId, val elements: List[Option[IRExpr]])
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Array literal with numbers
 * Stmt ::= x = [ n, ... ]
 */
case class IRArrayNumber(override val info: IRNodeInfo, override val lhs: IRId, val elements: List[Double])
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Arguments
 * Stmt ::= x = [ e, ... ]
 */
case class IRArgs(override val info: IRNodeInfo, override val lhs: IRId, val elements: List[Option[IRExpr]])
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Call
 * Stmt ::= x = f(this, arguments)
 */
case class IRCall(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRId, val thisB: IRId, val args: IRId)
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Internal function call
 * toObject, toString, toNubmer, isObject, getBase,
 * iteratorInit, iteratorHasNext, iteratorKey
 */
case class IRInternalCall(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRId, val first: IRExpr, val second: Option[IRId])
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * New
 * Stmt ::= x = new f(x, ...)
 */
case class IRNew(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRId, val args: List[IRId])
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Function expression
 * Stmt ::= x = function f (this, arguments) { s }
 */
case class IRFunExpr(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRFunctional)
  extends IRAssign(info: IRNodeInfo, lhs: IRId)
/**
 * Eval
 * Stmt ::= x = eval(e)
 */
case class IREval(override val info: IRNodeInfo, override val lhs: IRId, val arg: IRExpr)
  extends IRAssign(info: IRNodeInfo, lhs: IRId)

/**
 * AST statement unit
 * Stmt ::= s
 */
case class IRStmtUnit(override val info: IRNodeInfo, val stmts: List[IRStmt])
  extends IRStmt(info: IRNodeInfo)
/**
 * Store
 * Stmt ::= x[e] = e
 */
case class IRStore(override val info: IRNodeInfo, val obj: IRId, val index: IRExpr, val rhs: IRExpr)
  extends IRStmt(info: IRNodeInfo)
/**
 * Function declaration
 * Stmt ::= function f (this, arguments) { s }
 */
case class IRFunDecl(override val info: IRNodeInfo, val ftn: IRFunctional)
  extends IRStmt(info: IRNodeInfo)
/**
 * Break
 * Stmt ::= break label
 */
case class IRBreak(override val info: IRNodeInfo, val label: IRId)
  extends IRStmt(info: IRNodeInfo)
/**
 * Return
 * Stmt ::= return e?
 */
case class IRReturn(override val info: IRNodeInfo, val expr: Option[IRExpr])
  extends IRStmt(info: IRNodeInfo)
/**
 * With
 * Stmt ::= with ( x ) s
 */
case class IRWith(override val info: IRNodeInfo, val id: IRId, val stmt: IRStmt)
  extends IRStmt(info: IRNodeInfo)
/**
 * Label
 * Stmt ::= l : { s }
 */
case class IRLabelStmt(override val info: IRNodeInfo, val label: IRId, val stmt: IRStmt)
  extends IRStmt(info: IRNodeInfo)
/**
 * Var
 * Stmt ::= var x
 */
case class IRVarStmt(override val info: IRNodeInfo, val lhs: IRId, val fromParam: Boolean)
  extends IRStmt(info: IRNodeInfo)
/**
 * Throw
 * Stmt ::= throw e
 */
case class IRThrow(override val info: IRNodeInfo, val expr: IRExpr)
  extends IRStmt(info: IRNodeInfo)
/**
 * Sequence
 * Stmt ::= s; ...
 */
case class IRSeq(override val info: IRNodeInfo, val stmts: List[IRStmt])
  extends IRStmt(info: IRNodeInfo)
/**
 * If
 * Stmt ::= if (e) then s (else s)?
 */
case class IRIf(override val info: IRNodeInfo, val expr: IRExpr, val trueB: IRStmt, val falseb: Option[IRStmt])
  extends IRStmt(info: IRNodeInfo)
/**
 * While
 * Stmt ::= while (e) s
 */
case class IRWhile(override val info: IRNodeInfo, val cond: IRExpr, val body: IRStmt)
  extends IRStmt(info: IRNodeInfo)
/**
 * Try
 * Stmt ::= try { s } (catch (x) { s })? (finally { s })?
 */
case class IRTry(override val info: IRNodeInfo, val body: IRStmt, val name: Option[IRId], val catchB: Option[IRStmt], finallyB: Option[IRStmt])
  extends IRStmt(info: IRNodeInfo)
/**
 * No operation
 */
case class IRNoOp(override val info: IRNodeInfo, val desc: String)
  extends IRStmt(info: IRNodeInfo)

/**
 * Member
 */
abstract class IRMember(override val info: IRNodeInfo)
  extends IRNode(info: IRNodeInfo)
/**
 * Member ::= x : e
 */
case class IRField(override val info: IRNodeInfo, val prop: IRId, val expr: IRExpr)
  extends IRMember(info: IRNodeInfo)
/**
 * Member ::= get x () { s }
 */
case class IRGetProp(override val info: IRNodeInfo, val ftn: IRFunctional)
  extends IRMember(info: IRNodeInfo)
/**
 * Member ::= set x ( y ) { s }
 */
case class IRSetProp(override val info: IRNodeInfo, val ftn: IRFunctional)
  extends IRMember(info: IRNodeInfo)

/**
 * Expression
 */
abstract class IRExpr(override val info: IRNodeInfo)
  extends IRNode(info: IRNodeInfo)
/**
 * Side-effect free expressions
 */
abstract class IROpApp(override val info: IRNodeInfo)
  extends IRExpr(info: IRNodeInfo)
/**
 * Binary expression
 * Expr ::= e binop e
 */
case class IRBin(override val info: IRNodeInfo, val first: IRExpr, val op: IROp, val second: IRExpr)
  extends IROpApp(info: IRNodeInfo)
/**
 * Unary expression
 * Expr ::= unop e
 */
case class IRUn(override val info: IRNodeInfo, val op: IROp, val expr: IRExpr)
  extends IROpApp(info: IRNodeInfo)
/**
 * Load
 * Expr ::= x[e]
 */
case class IRLoad(override val info: IRNodeInfo, val obj: IRId, val index: IRExpr)
  extends IROpApp(info: IRNodeInfo)
/**
 * Variable
 */
abstract class IRId(override val info: IRNodeInfo, val originalName: String, val uniqueName: String, val global: Boolean)
  extends IRExpr(info: IRNodeInfo)
/**
 * Variable
 * Expr ::= x
 */
case class IRUserId(override val info: IRNodeInfo, override val originalName: String, override val uniqueName: String, override val global: Boolean, val isWith: Boolean)
  extends IRId(info: IRNodeInfo, originalName, uniqueName, global)
/**
 * Internally generated identifiers by Translator
 * Do not appear in the JavaScript source text.
 */
case class IRTmpId(override val info: IRNodeInfo, override val originalName: String, override val uniqueName: String, override val global: Boolean)
  extends IRId(info: IRNodeInfo, originalName, uniqueName, global)
/**
 * this
 */
case class IRThis(override val info: IRNodeInfo)
  extends IRExpr(info: IRNodeInfo)
/**
 * Value
 */
abstract class IRVal(override val info: IRNodeInfo)
  extends IRExpr(info: IRNodeInfo)
/**
 * Primitive value
 */
abstract class IRPVal(override val info: IRNodeInfo)
  extends IRExpr(info: IRNodeInfo)
/**
 * PVal ::= number literal
 */
case class IRNumber(override val info: IRNodeInfo, val text: String, val num: Double)
  extends IRVal(info: IRNodeInfo)
/**
 * PVal ::= String
 */
case class IRString(override val info: IRNodeInfo, val str: String)
  extends IRVal(info: IRNodeInfo)
/**
 * PVal ::= true | false
 */
case class IRBool(override val info: IRNodeInfo, val bool: Boolean)
  extends IRVal(info: IRNodeInfo)
/**
 * PVal ::= undefined
 */
case class IRUndef(override val info: IRNodeInfo)
  extends IRVal(info: IRNodeInfo)
/**
 * PVal ::= null
 */
case class IRNull(override val info: IRNodeInfo)
  extends IRVal(info: IRNodeInfo)

/**
 * Operator
 */
case class IROp(override val info: IRNodeInfo, val text: String, val kind: Int)
  extends IRNode(info: IRNodeInfo)

/**
 * Common shape for functions
 */
case class IRFunctional(override val info: IRNodeInfo, val fromSource: Boolean,
  val name: IRId, val params: List[IRId], val args: List[IRStmt],
  val fds: List[IRFunDecl], val vds: List[IRVarStmt], val body: List[IRStmt])
    extends IRNode(info: IRNodeInfo)

trait IRWalker {
  def walk(node: Any): Any = {
    node match {
      case IRArgs(info, lhs, elements) =>
        IRArgs(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(elements).asInstanceOf[List[Option[IRExpr]]])
      case IRArray(info, lhs, elements) =>
        IRArray(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(elements).asInstanceOf[List[Option[IRExpr]]])
      case IRArrayNumber(info, lhs, elements) =>
        IRArrayNumber(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(elements).asInstanceOf[List[_root_.java.lang.Double]])
      case IRBin(info, first, op, second) =>
        IRBin(walk(info).asInstanceOf[IRNodeInfo], walk(first).asInstanceOf[IRExpr], walk(op).asInstanceOf[IROp], walk(second).asInstanceOf[IRExpr])
      case IRBool(info, isBool) =>
        IRBool(walk(info).asInstanceOf[IRNodeInfo], walk(isBool).asInstanceOf[Boolean])
      case IRBreak(info, label) =>
        IRBreak(walk(info).asInstanceOf[IRNodeInfo], walk(label).asInstanceOf[IRId])
      case IRCall(info, lhs, fun, thisB, args) =>
        IRCall(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(fun).asInstanceOf[IRId], walk(thisB).asInstanceOf[IRId], walk(args).asInstanceOf[IRId])
      case IRDelete(info, lhs, id) =>
        IRDelete(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(id).asInstanceOf[IRId])
      case IRDeleteProp(info, lhs, obj, index) =>
        IRDeleteProp(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(obj).asInstanceOf[IRId], walk(index).asInstanceOf[IRExpr])
      case IREval(info, lhs, arg) =>
        IREval(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(arg).asInstanceOf[IRExpr])
      case IRExprStmt(info, lhs, right, isRef) =>
        IRExprStmt(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(right).asInstanceOf[IRExpr], walk(isRef).asInstanceOf[Boolean])
      case IRField(info, prop, expr) =>
        IRField(walk(info).asInstanceOf[IRNodeInfo], walk(prop).asInstanceOf[IRId], walk(expr).asInstanceOf[IRExpr])
      case IRFunDecl(info, ftn) =>
        IRFunDecl(walk(info).asInstanceOf[IRNodeInfo], walk(ftn).asInstanceOf[IRFunctional])
      case IRFunExpr(info, lhs, ftn) =>
        IRFunExpr(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(ftn).asInstanceOf[IRFunctional])
      case IRFunctional(info, isFromSource, name, params, args, fds, vds, body) =>
        IRFunctional(walk(info).asInstanceOf[IRNodeInfo], walk(isFromSource).asInstanceOf[Boolean], walk(name).asInstanceOf[IRId], walk(params).asInstanceOf[List[IRId]], walk(args).asInstanceOf[List[IRStmt]], walk(fds).asInstanceOf[List[IRFunDecl]], walk(vds).asInstanceOf[List[IRVarStmt]], walk(body).asInstanceOf[List[IRStmt]])
      case IRGetProp(info, ftn) =>
        IRGetProp(walk(info).asInstanceOf[IRNodeInfo], walk(ftn).asInstanceOf[IRFunctional])
      case IRIf(info, expr, trueB, falseB) =>
        IRIf(walk(info).asInstanceOf[IRNodeInfo], walk(expr).asInstanceOf[IRExpr], walk(trueB).asInstanceOf[IRStmt], walk(falseB).asInstanceOf[Option[IRStmt]])
      case IRInternalCall(info, lhs, fun, first, second) =>
        IRInternalCall(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(fun).asInstanceOf[IRId], walk(first).asInstanceOf[IRExpr], walk(second).asInstanceOf[Option[IRId]])
      case IRLabelStmt(info, label, stmt) =>
        IRLabelStmt(walk(info).asInstanceOf[IRNodeInfo], walk(label).asInstanceOf[IRId], walk(stmt).asInstanceOf[IRStmt])
      case IRLoad(info, obj, index) =>
        IRLoad(walk(info).asInstanceOf[IRNodeInfo], walk(obj).asInstanceOf[IRId], walk(index).asInstanceOf[IRExpr])
      case IRNew(info, lhs, fun, args) =>
        IRNew(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(fun).asInstanceOf[IRId], walk(args).asInstanceOf[List[IRId]])
      case IRNoOp(info, desc) =>
        IRNoOp(walk(info).asInstanceOf[IRNodeInfo], walk(desc).asInstanceOf[String])
      case IRNull(info) =>
        IRNull(walk(info).asInstanceOf[IRNodeInfo])
      case IRNumber(info, text, num) =>
        IRNumber(walk(info).asInstanceOf[IRNodeInfo], walk(text).asInstanceOf[String], walk(num).asInstanceOf[_root_.java.lang.Double])
      case IRObject(info, lhs, members, proto) =>
        IRObject(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(members).asInstanceOf[List[IRMember]], walk(proto).asInstanceOf[Option[IRId]])
      case IROp(info, text, kind) =>
        IROp(walk(info).asInstanceOf[IRNodeInfo], walk(text).asInstanceOf[String], walk(kind).asInstanceOf[Int])
      case IRReturn(info, expr) =>
        IRReturn(walk(info).asInstanceOf[IRNodeInfo], walk(expr).asInstanceOf[Option[IRExpr]])
      case IRRoot(info, fds, vds, irs) =>
        IRRoot(walk(info).asInstanceOf[IRNodeInfo], walk(fds).asInstanceOf[List[IRFunDecl]], walk(vds).asInstanceOf[List[IRVarStmt]], walk(irs).asInstanceOf[List[IRStmt]])
      case IRSeq(info, stmts) =>
        IRSeq(walk(info).asInstanceOf[IRNodeInfo], walk(stmts).asInstanceOf[List[IRStmt]])
      case IRSetProp(info, ftn) =>
        IRSetProp(walk(info).asInstanceOf[IRNodeInfo], walk(ftn).asInstanceOf[IRFunctional])
      case IRNodeInfo(span, fromSource) =>
        IRNodeInfo(walk(span).asInstanceOf[Span], walk(fromSource).asInstanceOf[Boolean])
      case IRStmtUnit(info, stmts) =>
        IRStmtUnit(walk(info).asInstanceOf[IRNodeInfo], walk(stmts).asInstanceOf[List[IRStmt]])
      case IRStore(info, obj, index, rhs) =>
        IRStore(walk(info).asInstanceOf[IRNodeInfo], walk(obj).asInstanceOf[IRId], walk(index).asInstanceOf[IRExpr], walk(rhs).asInstanceOf[IRExpr])
      case IRString(info, str) =>
        IRString(walk(info).asInstanceOf[IRNodeInfo], walk(str).asInstanceOf[String])
      case IRThis(info) =>
        IRThis(walk(info).asInstanceOf[IRNodeInfo])
      case IRThrow(info, expr) =>
        IRThrow(walk(info).asInstanceOf[IRNodeInfo], walk(expr).asInstanceOf[IRExpr])
      case IRTmpId(info, originalName, uniqueName, isGlobal) =>
        IRTmpId(walk(info).asInstanceOf[IRNodeInfo], walk(originalName).asInstanceOf[String], walk(uniqueName).asInstanceOf[String], walk(isGlobal).asInstanceOf[Boolean])
      case IRTry(info, body, name, catchB, finallyB) =>
        IRTry(walk(info).asInstanceOf[IRNodeInfo], walk(body).asInstanceOf[IRStmt], walk(name).asInstanceOf[Option[IRId]], walk(catchB).asInstanceOf[Option[IRStmt]], walk(finallyB).asInstanceOf[Option[IRStmt]])
      case IRUn(info, op, expr) =>
        IRUn(walk(info).asInstanceOf[IRNodeInfo], walk(op).asInstanceOf[IROp], walk(expr).asInstanceOf[IRExpr])
      case IRUndef(info) =>
        IRUndef(walk(info).asInstanceOf[IRNodeInfo])
      case IRUserId(info, originalName, uniqueName, isGlobal, isWith) =>
        IRUserId(walk(info).asInstanceOf[IRNodeInfo], walk(originalName).asInstanceOf[String], walk(uniqueName).asInstanceOf[String], walk(isGlobal).asInstanceOf[Boolean], walk(isWith).asInstanceOf[Boolean])
      case IRVarStmt(info, lhs, isFromParam) =>
        IRVarStmt(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(isFromParam).asInstanceOf[Boolean])
      case IRWhile(info, cond, body) =>
        IRWhile(walk(info).asInstanceOf[IRNodeInfo], walk(cond).asInstanceOf[IRExpr], walk(body).asInstanceOf[IRStmt])
      case IRWith(info, id, stmt) =>
        IRWith(walk(info).asInstanceOf[IRNodeInfo], walk(id).asInstanceOf[IRId], walk(stmt).asInstanceOf[IRStmt])
      case xs: List[_] => xs.map(walk _)
      case xs: Option[_] => xs.map(walk _)
      case _ => node
    }
  }
  def walkUnit(node: Any): Unit = {
    node match {
      case IRArgs(info, lhs, elements) =>
        walkUnit(info); walkUnit(lhs); walkUnit(elements)
      case IRArray(info, lhs, elements) =>
        walkUnit(info); walkUnit(lhs); walkUnit(elements)
      case IRArrayNumber(info, lhs, elements) =>
        walkUnit(info); walkUnit(lhs); walkUnit(elements)
      case IRBin(info, first, op, second) =>
        walkUnit(info); walkUnit(first); walkUnit(op); walkUnit(second)
      case IRBool(info, isBool) =>
        walkUnit(info); walkUnit(isBool)
      case IRBreak(info, label) =>
        walkUnit(info); walkUnit(label)
      case IRCall(info, lhs, fun, thisB, args) =>
        walkUnit(info); walkUnit(lhs); walkUnit(fun); walkUnit(thisB); walkUnit(args)
      case IRDelete(info, lhs, id) =>
        walkUnit(info); walkUnit(lhs); walkUnit(id)
      case IRDeleteProp(info, lhs, obj, index) =>
        walkUnit(info); walkUnit(lhs); walkUnit(obj); walkUnit(index)
      case IREval(info, lhs, arg) =>
        walkUnit(info); walkUnit(lhs); walkUnit(arg)
      case IRExprStmt(info, lhs, right, isRef) =>
        walkUnit(info); walkUnit(lhs); walkUnit(right); walkUnit(isRef)
      case IRField(info, prop, expr) =>
        walkUnit(info); walkUnit(prop); walkUnit(expr)
      case IRFunDecl(info, ftn) =>
        walkUnit(info); walkUnit(ftn)
      case IRFunExpr(info, lhs, ftn) =>
        walkUnit(info); walkUnit(lhs); walkUnit(ftn)
      case IRFunctional(info, isFromSource, name, params, args, fds, vds, body) =>
        walkUnit(info); walkUnit(isFromSource); walkUnit(name); walkUnit(params); walkUnit(args); walkUnit(fds); walkUnit(vds); walkUnit(body)
      case IRGetProp(info, ftn) =>
        walkUnit(info); walkUnit(ftn)
      case IRIf(info, expr, trueB, falseB) =>
        walkUnit(info); walkUnit(expr); walkUnit(trueB); walkUnit(falseB)
      case IRInternalCall(info, lhs, fun, first, second) =>
        walkUnit(info); walkUnit(lhs); walkUnit(fun); walkUnit(first); walkUnit(second)
      case IRLabelStmt(info, label, stmt) =>
        walkUnit(info); walkUnit(label); walkUnit(stmt)
      case IRLoad(info, obj, index) =>
        walkUnit(info); walkUnit(obj); walkUnit(index)
      case IRNew(info, lhs, fun, args) =>
        walkUnit(info); walkUnit(lhs); walkUnit(fun); walkUnit(args)
      case IRNoOp(info, desc) =>
        walkUnit(info); walkUnit(desc)
      case IRNull(info) =>
        walkUnit(info)
      case IRNumber(info, text, num) =>
        walkUnit(info); walkUnit(text); walkUnit(num)
      case IRObject(info, lhs, members, proto) =>
        walkUnit(info); walkUnit(lhs); walkUnit(members); walkUnit(proto)
      case IROp(info, text, kind) =>
        walkUnit(info); walkUnit(text); walkUnit(kind)
      case IRReturn(info, expr) =>
        walkUnit(info); walkUnit(expr)
      case IRRoot(info, fds, vds, irs) =>
        walkUnit(info); walkUnit(fds); walkUnit(vds); walkUnit(irs)
      case IRSeq(info, stmts) =>
        walkUnit(info); walkUnit(stmts)
      case IRSetProp(info, ftn) =>
        walkUnit(info); walkUnit(ftn)
      case IRNodeInfo(span, fromSource) =>
        walkUnit(span); walkUnit(fromSource)
      case IRStmtUnit(info, stmts) =>
        walkUnit(info); walkUnit(stmts)
      case IRStore(info, obj, index, rhs) =>
        walkUnit(info); walkUnit(obj); walkUnit(index); walkUnit(rhs)
      case IRString(info, str) =>
        walkUnit(info); walkUnit(str)
      case IRThis(info) =>
        walkUnit(info)
      case IRThrow(info, expr) =>
        walkUnit(info); walkUnit(expr)
      case IRTmpId(info, originalName, uniqueName, isGlobal) =>
        walkUnit(info); walkUnit(originalName); walkUnit(uniqueName); walkUnit(isGlobal)
      case IRTry(info, body, name, catchB, finallyB) =>
        walkUnit(info); walkUnit(body); walkUnit(name); walkUnit(catchB); walkUnit(finallyB)
      case IRUn(info, op, expr) =>
        walkUnit(info); walkUnit(op); walkUnit(expr)
      case IRUndef(info) =>
        walkUnit(info)
      case IRUserId(info, originalName, uniqueName, isGlobal, isWith) =>
        walkUnit(info); walkUnit(originalName); walkUnit(uniqueName); walkUnit(isGlobal); walkUnit(isWith)
      case IRVarStmt(info, lhs, isFromParam) =>
        walkUnit(info); walkUnit(lhs); walkUnit(isFromParam)
      case IRWhile(info, cond, body) =>
        walkUnit(info); walkUnit(cond); walkUnit(body)
      case IRWith(info, id, stmt) =>
        walkUnit(info); walkUnit(id); walkUnit(stmt)
      case xs: List[_] => xs.foreach(walkUnit _)
      case xs: Option[_] => xs.foreach(walkUnit _)
      case _: Span =>
      case _ =>
    }
  }
}
