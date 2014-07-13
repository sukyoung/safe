/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler

import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.Coverage
import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF}
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, NodeFactory => NF}
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.scala_src.useful.Sets._

/* Move IRBin, IRUn, and IRLoad out of IRExpr.
 */

object IRSimplifier extends IRWalker {

  def doit(program: IRRoot) = walk(program).asInstanceOf[IRRoot]

  def convert(expr: Option[IRExpr]): (List[IRStmt], Option[IRExpr]) = expr match {
    case Some(e) =>
      val (names, newe) = convertExpr(e)
      (names, Some(newe))
    case _ => (Nil, None)
  }
  def convertListOptExpr(elems: List[Option[IRExpr]]): (List[IRStmt], List[Option[IRExpr]]) =
    elems.foldLeft((List[IRStmt](), List[Option[IRExpr]]()))((p, oe) => {
                    val (ns, oee) = convert(oe)
                    (p._1 ++ ns, p._2 :+ oee)
                  })

  def convertFunctional(f: IRFunctional): IRFunctional = f match {
    case SIRFunctional(i, name, params, args, fds, vds, body) => 
      SIRFunctional(i, name, params,
                    args.map(walk(_).asInstanceOf[IRStmt]),
                    fds.map(walk(_).asInstanceOf[IRFunDecl]),
                    vds,
                    body.map(walk(_).asInstanceOf[IRStmt]))
  }
   
  def dummyAst(span: Span) = NF.makeNoOp(span, "IRSimplifier.dummyAst")
  def freshId(span: Span): IRTmpId = IF.makeTId(span, NU.freshName("temp"))
  def getAst(ir: IRNode, span: Span): ASTNode = NF.ir2ast(ir) match {
    case Some(ast) => ast
    case _ => dummyAst(span)
  }
  def needMore(expr: IRExpr): Boolean = expr match {
    case _:IRBin | _:IRUn | _:IRLoad => true
    case _ => false
  }
  def convertExpr(expr: IRExpr): (List[IRStmt], IRExpr) = expr match {
    case SIRBin(info, first, op, second) =>
      val (names1, expr1) = convertExpr(first)
      val (names2, expr2) = convertExpr(second)
      val (expr1s, expr1e) =
          if (needMore(expr1)) {
            val firstspan = NU.getSpan(first)
            val firstname = freshId(firstspan)
            (List(IF.makeExprStmtIgnore(getAst(first, firstspan), firstspan, firstname, expr1)),
             firstname)
          } else (Nil, expr1)
      val (expr2s, expr2e) =
          if (needMore(expr2)) {
            val secondspan = NU.getSpan(second)
            val secondname = freshId(secondspan)
            (List(IF.makeExprStmtIgnore(getAst(second, secondspan), secondspan, secondname, expr2)),
             secondname)
          } else (Nil, expr2)
      (((names1++names2)++expr1s)++expr2s, SIRBin(info, expr1e, op, expr2e))

    case SIRUn(info, op, arg) =>
      val (names, expr) = convertExpr(arg)
      val (exprs, expre) =
          if (needMore(expr)) {
            val span = NU.getSpan(arg)
            val name = freshId(span)
            (List(IF.makeExprStmtIgnore(getAst(arg, span), span, name, expr)),
             name)
          } else (Nil, expr)
      (names++exprs, SIRUn(info, op, expre))

    case SIRLoad(info, obj, index) =>
      val (names, expr) = convertExpr(index)
      val (exprs, expre) =
          if (needMore(expr)) {
            val span = NU.getSpan(index)
            val name = freshId(span)
            (List(IF.makeExprStmtIgnore(getAst(index, span), span, name, expr)),
             name)
          } else (Nil, expr)
      (names++exprs, SIRLoad(info, obj, expre))

    case _ => (Nil, expr)
  }

  def convert(mem: IRMember): (List[IRStmt], IRMember) = mem match {
    case SIRField(info, prop, expr) =>
      val (names, newexpr) = convertExpr(expr)
      (names, SIRField(info, prop, newexpr))
    case SIRGetProp(info, ftn) =>
      (Nil, SIRGetProp(info, convertFunctional(ftn)))
    case SIRSetProp(info, ftn) =>
      (Nil, SIRSetProp(info, convertFunctional(ftn)))
  }

  override def walk(node: Any):Any = node match {

    case SIRRoot(info, fds, vds, irs) =>
      SIRRoot(info, fds.map(walk(_).asInstanceOf[IRFunDecl]),
              vds, 
              irs.map(walk(_).asInstanceOf[IRStmt]))

    case SIRExprStmt(info, lhs, right, ref) =>
      val (names, newrhs) = convertExpr(right)
      SIRSeq(info, names:+SIRExprStmt(info, lhs, newrhs, ref))

    case SIRDelete(info, lhs, id) => node

    case SIRDeleteProp(info, lhs, obj, index) => 
      val (names, newindex) = convertExpr(index)
      SIRSeq(info, names:+SIRDeleteProp(info, lhs, obj, newindex))

    case SIRObject(info, lhs, members, proto) => 
      val (names, newmembers) = members.foldLeft((List[IRStmt](), List[IRMember]()))((p, m) => {
                                                  val (ns, mm) = convert(m)
                                                  (p._1 ++ ns, p._2 :+ mm)
                                                })
      SIRSeq(info, names:+SIRObject(info, lhs, newmembers, proto))

    case SIRArray(info, lhs, elems) => 
      val (names, newelems) = convertListOptExpr(elems)
      SIRSeq(info, names:+SIRArray(info, lhs, newelems))
    
    case SIRArrayNumber(info, lhs, elements) => node

    case SIRArgs(info, lhs, elems) => 
      val (names, newelems) = convertListOptExpr(elems)
      SIRSeq(info, names:+SIRArgs(info, lhs, newelems))

    case SIRCall(info, lhs, fun, thisB, args) => node

    // Don't conver the arguments of IRInternalCall
    case SIRInternalCall(info, lhs, fun, first, second) => node

    case SIRNew(info, lhs, fun, args) => node

    case SIRFunExpr(info, lhs, ftn) =>  
      SIRFunExpr(info, lhs, convertFunctional(ftn))

    case SIREval(info, lhs, arg) =>
      val (names, newarg) = convertExpr(arg)
      SIRSeq(info, names:+SIREval(info, lhs, newarg))

    case SIRStmtUnit(info, stmts) =>
      SIRStmtUnit(info, stmts.map(walk(_).asInstanceOf[IRStmt]))

    case SIRStore(info, obj, index, rhs) =>
      val (namesi, newindex) = convertExpr(SIRLoad(info, obj, index))
      val (namesr, newrhs) = convertExpr(rhs)
      SIRSeq(info, (namesi++namesr):+SIRStore(info, obj, newindex.asInstanceOf[IRLoad].getIndex, newrhs))

    case SIRFunDecl(info, ftn) =>
      SIRFunDecl(info, convertFunctional(ftn))

    case SIRBreak(info, label) => node

    case SIRReturn(info, Some(expr)) =>
      val (names, newexpr) = convertExpr(expr)
      SIRSeq(info, names:+SIRReturn(info, Some(newexpr)))

    case SIRReturn(info, None) => node

    case SIRWith(info, id, stmt) =>
      SIRWith(info, id, walk(stmt).asInstanceOf[IRStmt])

    case SIRLabelStmt(info, label, stmt) =>
      SIRLabelStmt(info, label, walk(stmt).asInstanceOf[IRStmt])

    case SIRVarStmt(info, lhs, fromparam) => node

    case SIRThrow(info, expr) =>
      val (names, newexpr) = convertExpr(expr)
      SIRSeq(info, names:+SIRThrow(info, newexpr))

    case SIRSeq(info, stmts) =>
      SIRSeq(info, stmts.map(walk(_).asInstanceOf[IRStmt]))

    case SIRIf(info, expr, trueB, falseB) =>
      val (names, newexpr) = convertExpr(expr)
      val newfalseB = falseB match {
        case Some(stmt) => Some(walk(stmt).asInstanceOf[IRStmt])
        case None => None
      }
      SIRSeq(info, names:+SIRIf(info, newexpr, walk(trueB).asInstanceOf[IRStmt],
                                newfalseB))

    case SIRWhile(info, cond, body) =>
      val (names, newcond) = convertExpr(cond)
      SIRSeq(info, names:+SIRWhile(info, newcond,
                                   SIRSeq(info, walk(body).asInstanceOf[IRStmt]+:names)))

    case SIRTry(info, body, name, catchB, finallyB) =>
      val newcatchB = catchB match {
        case Some(stmt) => Some(walk(stmt).asInstanceOf[IRStmt])
        case None => None
      }
      val newfinallyB = finallyB match {
        case Some(stmt) => Some(walk(stmt).asInstanceOf[IRStmt])
        case None => None
      }
      SIRTry(info, walk(body).asInstanceOf[IRStmt], name, newcatchB, newfinallyB)

    case _ => super.walk(node)
  }
}
