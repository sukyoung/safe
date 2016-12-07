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

package kr.ac.kaist.safe.nodes.ir

import kr.ac.kaist.safe.nodes.ast.ASTNode
import kr.ac.kaist.safe.util.NodeUtil

trait IRGeneralWalker[Result] {
  def join(args: Result*): Result

  def walkOpt(opt: Option[IRNode]): List[Result] =
    opt.fold(List[Result]()) { n: IRNode =>
      List(n match {
        case s: IRStmt => walk(s)
        case i: IRId => walk(i)
        case e: IRExpr => walk(e)
      })
    }

  def walk(ast: ASTNode): Result = join()

  def walk(node: IRRoot): Result = node match {
    case IRRoot(ast, fds, vds, irs) =>
      join(walk(ast) :: fds.map(walk) ++ vds.map(walk) ++ irs.map(walk): _*)
  }

  def walk(node: IRStmt): Result = node match {
    case IRExprStmt(ast, lhs, right, isRef) =>
      join(walk(ast), walk(lhs), walk(right))
    case IRDelete(ast, lhs, id) =>
      join(walk(ast), walk(lhs), walk(id))
    case IRDeleteProp(ast, lhs, obj, index) =>
      join(walk(ast), walk(lhs), walk(obj), walk(index))
    case IRObject(ast, lhs, members, proto) =>
      join(walk(ast) :: walk(lhs) :: members.map(walk) ++ walkOpt(proto): _*)
    case IRArray(ast, lhs, elements) =>
      join(walk(ast) :: walk(lhs) :: elements.flatMap(walkOpt): _*)
    case IRArrayNumber(ast, lhs, elements) =>
      join(walk(ast), walk(lhs))
    case IRArgs(ast, lhs, elements) =>
      join(walk(ast) :: walk(lhs) :: elements.flatMap(walkOpt): _*)
    case IRCall(ast, lhs, fun, thisB, args) =>
      join(walk(ast), walk(lhs), walk(fun), walk(thisB), walk(args))
    case IRInternalCall(ast, lhs, fun, first, second) =>
      join(walk(ast) :: walk(lhs) :: walk(fun) :: walk(first) :: walkOpt(second): _*)
    case IRNew(ast, lhs, fun, args) =>
      join(walk(ast) :: walk(lhs) :: walk(fun) :: args.map(walk): _*)
    case IRFunExpr(ast, lhs, ftn) =>
      join(walk(ast), walk(lhs), walk(ftn))
    case IREval(ast, lhs, arg) =>
      join(walk(ast), walk(lhs), walk(arg))
    case IRStmtUnit(ast, stmts) =>
      join(walk(ast) :: stmts.map(walk): _*)
    case IRStore(ast, obj, index, rhs) =>
      join(walk(ast), walk(obj), walk(index), walk(rhs))
    case fd: IRFunDecl =>
      walk(fd)
    case IRBreak(ast, label) =>
      join(walk(ast), walk(label))
    case IRReturn(ast, expr) =>
      join(walk(ast) :: walkOpt(expr): _*)
    case IRWith(ast, id, stmt) =>
      join(walk(ast), walk(id), walk(stmt))
    case IRLabelStmt(ast, label, stmt) =>
      join(walk(ast), walk(label), walk(stmt))
    case vs: IRVarStmt =>
      walk(vs)
    case IRThrow(ast, expr) =>
      join(walk(ast), walk(expr))
    case IRSeq(ast, stmts) =>
      join(walk(ast) :: stmts.map(walk): _*)
    case IRIf(ast, expr, trueB, falseB) =>
      join(walk(ast) :: walk(expr) :: walk(trueB) :: walkOpt(falseB): _*)
    case IRWhile(ast, cond, body) =>
      join(walk(ast), walk(cond), walk(body))
    case IRTry(ast, body, name, catchB, finallyB) =>
      join(walk(ast) :: walk(body) :: walkOpt(name) ++ walkOpt(catchB) ++ walkOpt(finallyB): _*)
    case IRNoOp(ast, desc) =>
      walk(ast)
  }

  def walk(node: IRExpr): Result = node match {
    case IRBin(ast, first, op, second) =>
      join(walk(ast), walk(first), walk(op), walk(second))
    case IRUn(ast, op, expr) =>
      join(walk(ast), walk(op), walk(expr))
    case IRLoad(ast, obj, index) =>
      join(walk(ast), walk(obj), walk(index))
    case id: IRUserId =>
      walk(id)
    case id: IRTmpId =>
      walk(id)
    case IRThis(ast) =>
      walk(ast)
    case IRInternalValue(ast, name) => walk(ast)
    case IRVal(value) => walk(NodeUtil.TEMP_AST)
  }

  def walk(node: IRMember): Result = node match {
    case IRField(ast, prop, expr) =>
      join(walk(ast), walk(prop), walk(expr))
    case IRGetProp(ast, ftn) =>
      join(walk(ast), walk(ftn))
    case IRSetProp(ast, ftn) =>
      join(walk(ast), walk(ftn))
  }

  def walk(node: IRFunctional): Result = node match {
    case IRFunctional(ast, isFromSource, name, params, args, fds, vds, body) =>
      join(walk(ast) :: walk(name) ::
        (params.map(walk) ++ args.map(walk) ++ fds.map(walk) ++ vds.map(walk) ++ body.map(walk)): _*)
  }

  def walk(node: IROp): Result = node match {
    case IROp(ast, kind) =>
      walk(ast)
  }

  def walk(node: IRFunDecl): Result = node match {
    case IRFunDecl(ast, ftn) =>
      join(walk(ast), walk(ftn))
  }

  def walk(node: IRVarStmt): Result = node match {
    case IRVarStmt(ast, lhs, isFromParam) =>
      join(walk(ast), walk(lhs))
  }

  def walk(node: IRId): Result = node match {
    case IRUserId(ast, originalName, uniqueName, isGlobal, isWith) =>
      walk(ast)
    case IRTmpId(ast, originalName, uniqueName, isGlobal) =>
      walk(ast)
  }
}
