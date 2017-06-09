/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe.concolic

object IRFilter extends IRWalker {
  def doit(program: IRRoot) = {
    var SIRRoot(info, vds, fds, irs) = program
    irs = irs.filter(walk(_))
    SIRRoot(info, vds, fds, irs)
  }

  override def walk(node: Any):Boolean = node match {
    case SIRExprStmt(info, lhs, right, ref) => false 
    case SIRDelete(info, lhs, id) => false
    case SIRDeleteProp(info, lhs, obj, index) => false
    case SIRStore(info, obj, index, rhs) => false
    case SIRObject(info, lhs, members, proto) => false
    case SIRArray(info, lhs, elements) => false
    case SIRArgs(info, lhs, elements) => false
    case SIRCall(info, lhs, fun, thisB, args) => false
    case SIRInternalCall(info, lhs, fun, first, second) => false
    case SIRNew(info, lhs, fun, args) => false
    case SIRFunExpr(info, lhs, ftn) => true
    case SIRFunDecl(info, ftn) => true
    case SIREval(info, lhs, arg) => false
    case SIRBreak(info, label) => false
    case SIRReturn(info, expr) => false
    case SIRWith(info, id, stmt) => walk(stmt)
    case SIRLabelStmt(info, label, stmt) => walk(stmt)
    case SIRVarStmt(info, lhs, fromParam) => true
    case SIRThrow(info, expr) => false
    case SIRSeq(info, stmts) =>
      stmts.foldLeft(false)((includeFunc, s) => includeFunc || walk(s)) 
    case SIRIf(info, expr, trueB, falseB) => 
      val r1 = falseB match {case Some(s) => walk(s); case None => false}
      walk(trueB) || r1
    case SIRWhile(info, cond, body) => walk(body)
    case SIRTry(info, body, name, catchB, finallyB) => 
      val r1 = catchB match {case Some(s) => walk(s); case None => false}
      val r2 = finallyB match {case Some(s) => walk(s);case None => false}
      walk(body) || r1 || r2
    case SIRStmtUnit(info, stmts) => 
      stmts.foldLeft(false)((includeFunc, s) => includeFunc || walk(s)) 
    case SIRNoOp(info, desc) => false
  }
}
