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

package kr.ac.kaist.safe.compiler

import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

/*
 * Move IRBin, IRUn, and IRLoad out of IRExpr.
 */

class IRSimplifier(program: IRRoot) extends IRWalker {

  lazy val result = doit

  private def doit: IRRoot = walk(program)

  def convert(expr: Option[IRExpr]): (List[IRStmt], Option[IRExpr]) = expr match {
    case Some(e) =>
      val (names, newe) = convertExpr(e)
      (names, Some(newe))
    case _ => (Nil, None)
  }
  def convertListOptExpr(elems: List[Option[IRExpr]]): (List[IRStmt], List[Option[IRExpr]]) =
    elems.foldLeft((List[IRStmt](), List[Option[IRExpr]]()))({
      case ((stmts, irexps), oe) =>
        val (ns, oee) = convert(oe)
        (stmts ++ ns, irexps :+ oee)
    })

  def convertFunctional(f: IRFunctional): IRFunctional = f match {
    case IRFunctional(ast, i, name, params, args, fds, vds, body) =>
      val newFds: List[IRFunDecl] = fds.map(walk)
      IRFunctional(ast, i, name, params,
        args.map(walk),
        newFds,
        vds,
        body.map(walk))
  }

  def dummyAst(span: Span): NoOp = NF.makeNoOp(span, "IRSimplifier.dummyAst")
  def freshId(span: Span): IRTmpId = IF.makeTId(NF.makeDummyAST(span), NU.freshName("temp"))
  def needMore(expr: IRExpr): Boolean = expr match {
    case _: IRBin | _: IRUn | _: IRLoad => true
    case _ => false
  }
  def convertExpr(expr: IRExpr): (List[IRStmt], IRExpr) = expr match {
    case IRBin(info, first, op, second) =>
      val (names1, expr1) = convertExpr(first)
      val (names2, expr2) = convertExpr(second)
      val (expr1s, expr1e) =
        if (needMore(expr1)) {
          val firstspan = first.span
          val firstname = freshId(firstspan)
          (
            List(IF.makeExprStmtIgnore(first.ast, firstname, expr1)),
            firstname
          )
        } else (Nil, expr1)
      val (expr2s, expr2e) =
        if (needMore(expr2)) {
          val secondspan = second.span
          val secondname = freshId(secondspan)
          (
            List(IF.makeExprStmtIgnore(second.ast, secondname, expr2)),
            secondname
          )
        } else (Nil, expr2)
      (((names1 ++ names2) ++ expr1s) ++ expr2s, IRBin(info, expr1e, op, expr2e))

    case IRUn(info, op, arg) =>
      val (names, expr) = convertExpr(arg)
      val (exprs, expre) =
        if (needMore(expr)) {
          val span = arg.span
          val name = freshId(span)
          (
            List(IF.makeExprStmtIgnore(arg.ast, name, expr)),
            name
          )
        } else (Nil, expr)
      (names ++ exprs, IRUn(info, op, expre))

    case IRLoad(info, obj, index) =>
      val (names, expr) = convertExpr(index)
      val (exprs, expre) =
        if (needMore(expr)) {
          val span = index.span
          val name = freshId(span)
          (
            List(IF.makeExprStmtIgnore(index.ast, name, expr)),
            name
          )
        } else (Nil, expr)
      (names ++ exprs, IRLoad(info, obj, expre))

    case _ => (Nil, expr)
  }

  def convert(mem: IRMember): (List[IRStmt], IRMember) = mem match {
    case IRField(info, prop, expr) =>
      val (names, newexpr) = convertExpr(expr)
      (names, IRField(info, prop, newexpr))
    case IRGetProp(info, ftn) =>
      (Nil, IRGetProp(info, convertFunctional(ftn)))
    case IRSetProp(info, ftn) =>
      (Nil, IRSetProp(info, convertFunctional(ftn)))
  }

  override def walk(node: IRRoot): IRRoot = node match {
    case IRRoot(info, fds, vds, irs) =>
      val newFds: List[IRFunDecl] = fds.map((fd: IRFunDecl) => walk(fd))
      IRRoot(info, newFds, vds, irs.map(walk))
  }

  override def walk(node: IRFunDecl): IRFunDecl = node match {
    case IRFunDecl(ast, ftn) =>
      IRFunDecl(ast, convertFunctional(ftn))
  }

  override def walk(node: IRStmt): IRStmt = node match {

    case IRExprStmt(ast, lhs, right, ref) =>
      val (names, newrhs) = convertExpr(right)
      IRSeq(ast, names :+ IRExprStmt(ast, lhs, newrhs, ref))

    case IRDelete(ast, lhs, id) => node

    case IRDeleteProp(ast, lhs, obj, index) =>
      val (names, newindex) = convertExpr(index)
      IRSeq(ast, names :+ IRDeleteProp(ast, lhs, obj, newindex))

    case IRObject(ast, lhs, members, proto) =>
      val (names, newMembers) = members.foldLeft((List[IRStmt](), List[IRMember]()))({
        case ((stmts, members), m) => {
          val (ns, mm) = convert(m)
          (stmts ++ ns, members :+ mm)
        }
      })
      IRSeq(ast, names :+ IRObject(ast, lhs, newMembers, proto))

    case IRArray(ast, lhs, elems) =>
      val (names, newelems) = convertListOptExpr(elems)
      IRSeq(ast, names :+ IRArray(ast, lhs, newelems))

    case IRArrayNumber(ast, lhs, elements) => node

    case IRArgs(ast, lhs, elems) =>
      val (names, newelems) = convertListOptExpr(elems)
      IRSeq(ast, names :+ IRArgs(ast, lhs, newelems))

    case IRCall(ast, lhs, fun, thisB, args) => node

    // Don't convert the arguments of IRInternalCall
    case IRInternalCall(ast, lhs, fun, args) => node

    case IRNew(ast, lhs, fun, args) => node

    case IRFunExpr(ast, lhs, ftn) =>
      IRFunExpr(ast, lhs, convertFunctional(ftn))

    case IREval(ast, lhs, arg) =>
      val (names, newarg) = convertExpr(arg)
      IRSeq(ast, names :+ IREval(ast, lhs, newarg))

    case IRStmtUnit(ast, stmts) =>
      IRStmtUnit(ast, stmts.map(walk))

    case IRStore(ast, obj, index, rhs) =>
      val (namesi, newindex) = convertExpr(IRLoad(ast, obj, index))
      val (namesr, newrhs) = convertExpr(rhs)
      IRSeq(ast, (namesi ++ namesr) :+ IRStore(ast, obj, newindex.asInstanceOf[IRLoad].index, newrhs))

    case n: IRFunDecl =>
      walk(n)

    case IRBreak(ast, label) => node

    case IRReturn(ast, Some(expr)) =>
      val (names, newexpr) = convertExpr(expr)
      IRSeq(ast, names :+ IRReturn(ast, Some(newexpr)))

    case IRReturn(ast, None) => node

    case IRWith(ast, id, stmt) =>
      IRWith(ast, id, walk(stmt))

    case IRLabelStmt(ast, label, stmt) =>
      IRLabelStmt(ast, label, walk(stmt))

    case IRVarStmt(ast, lhs, fromparam) => node

    case IRThrow(ast, expr) =>
      val (names, newexpr) = convertExpr(expr)
      IRSeq(ast, names :+ IRThrow(ast, newexpr))

    case IRSeq(ast, stmts) =>
      IRSeq(ast, stmts.map(walk))

    case IRIf(ast, expr, trueB, falseB) =>
      val (names, newexpr) = convertExpr(expr)
      val newfalseB = falseB match {
        case Some(stmt) => Some(walk(stmt))
        case None => None
      }
      IRSeq(ast, names :+ IRIf(ast, newexpr, walk(trueB),
        newfalseB))

    case IRWhile(ast, cond, body, breakLabel, contLabel) =>
      val (names, newcond) = convertExpr(cond)
      IRSeq(ast, names :+ IRWhile(ast, newcond,
        IRSeq(ast, walk(body) +: names),
        breakLabel, contLabel))

    case IRTry(info, body, name, catchB, finallyB) =>
      val newcatchB = catchB match {
        case Some(stmt) => Some(walk(stmt))
        case None => None
      }
      val newfinallyB = finallyB match {
        case Some(stmt) => Some(walk(stmt))
        case None => None
      }
      IRTry(info, walk(body), name, newcatchB, newfinallyB)

    case _ =>
      super.walk(node)
  }
}
