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

trait IRWalker {
  def walk(node: IRRoot): IRRoot = node match {
    case IRRoot(ast, fds, vds, irs) =>
      IRRoot(ast, fds.map(walk), vds.map(walk), irs.map(walk))
  }

  def walk(node: IRStmt): IRStmt = node match {
    case IRExprStmt(ast, lhs, right, isRef) =>
      IRExprStmt(ast, walk(lhs), walk(right), isRef)
    case IRDelete(ast, lhs, id) =>
      IRDelete(ast, walk(lhs), walk(id))
    case IRDeleteProp(ast, lhs, obj, index) =>
      IRDeleteProp(ast, walk(lhs), walk(obj), walk(index))
    case IRObject(ast, lhs, members, proto) =>
      IRObject(ast, walk(lhs), members.map(walk), proto.map(walk))
    case IRArray(ast, lhs, elements) =>
      IRArray(ast, walk(lhs), elements.map(_.map(walk)))
    case IRArrayNumber(ast, lhs, elements) =>
      IRArrayNumber(ast, walk(lhs), elements)
    case IRArgs(ast, lhs, elements) =>
      IRArgs(ast, walk(lhs), elements.map(_.map(walk)))
    case IRCall(ast, lhs, fun, thisB, args) =>
      IRCall(ast, walk(lhs), walk(fun), walk(thisB), walk(args))
    case IRInternalCall(ast, lhs, fun, first, second) =>
      IRInternalCall(ast, walk(lhs), walk(fun), walk(first), second.map(walk))
    case IRNew(ast, lhs, fun, args) =>
      IRNew(ast, walk(lhs), walk(fun), args.map(walk))
    case IRFunExpr(ast, lhs, ftn) =>
      IRFunExpr(ast, walk(lhs), walk(ftn))
    case IREval(ast, lhs, arg) =>
      IREval(ast, walk(lhs), walk(arg))
    case IRStmtUnit(ast, stmts) =>
      IRStmtUnit(ast, stmts.map(walk))
    case IRStore(ast, obj, index, rhs) =>
      IRStore(ast, walk(obj), walk(index), walk(rhs))
    case fd: IRFunDecl =>
      walk(fd)
    case IRBreak(ast, label) =>
      IRBreak(ast, walk(label))
    case IRReturn(ast, expr) =>
      IRReturn(ast, expr.map(walk))
    case IRWith(ast, id, stmt) =>
      IRWith(ast, walk(id), walk(stmt))
    case IRLabelStmt(ast, label, stmt) =>
      IRLabelStmt(ast, walk(label), walk(stmt))
    case vs: IRVarStmt =>
      walk(vs)
    case IRThrow(ast, expr) =>
      IRThrow(ast, walk(expr))
    case IRSeq(ast, stmts) =>
      IRSeq(ast, stmts.map(walk))
    case IRIf(ast, expr, trueB, falseB) =>
      IRIf(ast, walk(expr), walk(trueB), falseB.map(walk))
    case IRWhile(ast, cond, body) =>
      IRWhile(ast, walk(cond), walk(body))
    case IRTry(ast, body, name, catchB, finallyB) =>
      IRTry(ast, walk(body), name.map(walk), catchB.map(walk), finallyB.map(walk))
    case IRNoOp(ast, desc) =>
      IRNoOp(ast, desc)
  }

  def walk(node: IRExpr): IRExpr = node match {
    case IRBin(ast, first, op, second) =>
      IRBin(ast, walk(first), walk(op), walk(second))
    case IRUn(ast, op, expr) =>
      IRUn(ast, walk(op), walk(expr))
    case IRLoad(ast, obj, index) =>
      IRLoad(ast, walk(obj), walk(index))
    case id: IRUserId =>
      walk(id)
    case id: IRTmpId =>
      walk(id)
    case IRThis(ast) =>
      IRThis(ast)
    case IRInternalValue(ast, name) => IRInternalValue(ast, name)
    case IRVal(value) => IRVal(value)
  }

  def walk(node: IRMember): IRMember = node match {
    case IRField(ast, prop, expr) =>
      IRField(ast, walk(prop), walk(expr))
    case IRGetProp(ast, ftn) =>
      IRGetProp(ast, walk(ftn))
    case IRSetProp(ast, ftn) =>
      IRSetProp(ast, walk(ftn))
  }

  def walk(node: IRFunctional): IRFunctional = node match {
    case IRFunctional(ast, isFromSource, name, params, args, fds, vds, body) =>
      IRFunctional(ast, isFromSource, walk(name), params.map(walk),
        args.map(walk), fds.map(walk), vds.map(walk), body.map(walk))
  }

  def walk(node: IROp): IROp = node match {
    case IROp(ast, kind) =>
      IROp(ast, kind)
  }

  def walk(node: IRFunDecl): IRFunDecl = node match {
    case IRFunDecl(ast, ftn) =>
      IRFunDecl(ast, walk(ftn))
  }

  def walk(node: IRVarStmt): IRVarStmt = node match {
    case IRVarStmt(ast, lhs, isFromParam) =>
      IRVarStmt(ast, walk(lhs), isFromParam)
  }

  def walk(node: IRId): IRId = node match {
    case IRUserId(ast, originalName, uniqueName, isGlobal, isWith) =>
      IRUserId(ast, originalName, uniqueName, isGlobal, isWith)
    case IRTmpId(ast, originalName, uniqueName, isGlobal) =>
      IRTmpId(ast, originalName, uniqueName, isGlobal)
  }
}
