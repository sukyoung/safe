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

import kr.ac.kaist.safe.nodes.ir._

object IRFilter extends IRGeneralWalker[Boolean] {

  def join(args: Boolean*): Boolean = args.exists((arg) => arg)

  def doit(program: IRRoot) = {
    var IRRoot(info, vds, fds, irs) = program
    irs = irs.filter(walk)
    IRRoot(info, vds, fds, irs)
  }

  override def walk(node: IRStmt): Boolean = node match {
    case IRExprStmt(info, lhs, right, ref) => false
    case IRDelete(info, lhs, id) => false
    case IRDeleteProp(info, lhs, obj, index) => false
    case IRStore(info, obj, index, rhs) => false
    case IRObject(info, lhs, members, proto) => false
    case IRArray(info, lhs, elements) => false
    case IRArgs(info, lhs, elements) => false
    case IRCall(info, lhs, fun, thisB, args) => false
    case IRInternalCall(info, lhs, fun, args) => false
    case IRNew(info, lhs, fun, args) => false
    case IRFunExpr(info, lhs, ftn) => true
    case IRFunDecl(info, ftn) => true
    case IREval(info, lhs, arg) => false
    case IRBreak(info, label) => false
    case IRReturn(info, expr) => false
    case IRWith(info, id, stmt) => walk(stmt)
    case IRLabelStmt(info, label, stmt) => walk(stmt)
    case IRVarStmt(info, lhs, fromParam) => true
    case IRThrow(info, expr) => false
    case IRSeq(info, stmts) =>
      stmts.foldLeft(false)((includeFunc, s) => includeFunc || walk(s))
    case IRIf(info, expr, trueB, falseB) =>
      val r1 = falseB match { case Some(s) => walk(s); case None => false }
      walk(trueB) || r1
    case IRWhile(info, cond, body, breakLabel, contLabel) => walk(body)
    case IRTry(info, body, name, catchB, finallyB) =>
      val r1 = catchB match { case Some(s) => walk(s); case None => false }
      val r2 = finallyB match { case Some(s) => walk(s); case None => false }
      walk(body) || r1 || r2
    case IRStmtUnit(info, stmts) =>
      stmts.foldLeft(false)((includeFunc, s) => includeFunc || walk(s))
    case IRNoOp(info, desc) => false
  }
}
