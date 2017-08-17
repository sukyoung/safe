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

package kr.ac.kaist.safe.nodes

import _root_.java.math.BigInteger
import scala.collection.mutable.{ HashMap => MHashMap, HashSet => MHashSet }

import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, SourceLoc, Span, UIDObject }

object NodeFactory {
  def makeDummyAST(name: String): ASTNode = makeDummyAST(dummyASTInfo(name), name)
  def makeDummyAST(info: ASTNodeInfo, name: String): ASTNode = makeNoOp(info, name)
  def makeDummyAST(span: Span): ASTNode = makeNoOp(NU.makeASTNodeInfo(span), span.fileName)
  val dummyAST: ASTNode = makeDummyAST("dummyAST")
  def dummyASTInfo(villain: String): ASTNodeInfo = NU.makeASTNodeInfo(makeSpan(villain))
  val dummyASTInfo: ASTNodeInfo = NU.makeASTNodeInfo(NU.dummySpan)

  val dummyFunctional = makeEmptyFunctional(dummyASTInfo, makeId(NU.dummySpan("dummyFunctional"), "dummyFunctional"),
    Nil,
    Nil,
    Nil,
    List(
      Id(dummyASTInfo, "dummyThis", isWith = false),
      Id(dummyASTInfo, "dummyArguments", isWith = false)
    ),
    false)

  ////////////////////////////////////////////////////////////////////////////////
  // IR nodes created
  ////////////////////////////////////////////////////////////////////////////////
  type IRSet = MHashSet[IRNode]
  val irSet = new IRSet
  def putIr[A <: IRNode](ir: A, ast: ASTNode): A = {
    irSet.add(ir)
    ir
  }

  def makeSpanInfo(span: Span): ASTNodeInfo =
    new ASTNodeInfo(span, None)

  // For use only when there is no hope of attaching a true span.
  def makeSpan(villain: String): Span = {
    val sl = new SourceLoc(0, 0, 0)
    new Span(villain, sl, sl)
  }

  def makeEmptyFunctional(
    info: ASTNodeInfo,
    name: Id,
    fds: List[FunDecl],
    vds: List[VarDecl],
    body: List[SourceElement],
    params: List[Id],
    strict: Boolean
  ): Functional =
    new Functional(info, fds, vds, new SourceElements(info, body, strict), name, params, "")

  def makeFunctional(
    info: ASTNodeInfo,
    name: Id,
    fds: List[FunDecl],
    vds: List[VarDecl],
    body: List[SourceElement],
    params: List[Id],
    strict: Boolean,
    bodyS: String
  ): Functional =
    new Functional(info, fds, vds, new SourceElements(info, body, strict), name, params, bodyS)

  def makeFunDecl(
    span: Span,
    name: Id,
    params: List[Id],
    body: List[SourceElement],
    strict: Boolean
  ): FunDecl = {
    val info = NU.makeASTNodeInfo(span)
    val functional = makeEmptyFunctional(info, name, Nil, Nil, body, params, strict)
    new FunDecl(info, functional, strict)
  }

  def makeFunExpr(
    span: Span,
    name: Id,
    params: List[Id],
    body: List[SourceElement],
    strict: Boolean
  ): FunExpr = {
    val info = NU.makeASTNodeInfo(span)
    new FunExpr(info, makeEmptyFunctional(info, name, Nil, Nil, body, params, strict))
  }

  def makeExprStmt(span: Span, expr: Expr): ExprStmt =
    new ExprStmt(NU.makeASTNodeInfo(span), expr, false)

  def makeAssignOpApp(span: Span, lhs: LHS, op: Op, right: Expr): AssignOpApp =
    new AssignOpApp(NU.makeASTNodeInfo(span), lhs, op, right)

  def makeBracket(span: Span, lhs: LHS, index: Expr): Bracket =
    new Bracket(NU.makeASTNodeInfo(span), lhs, index)

  def makeDot(span: Span, lhs: LHS, member: Id): Dot =
    new Dot(NU.makeASTNodeInfo(span), lhs, member)

  def makeNew(span: Span, lhs: LHS): New =
    new New(NU.makeASTNodeInfo(span), lhs)

  def makeFunApp(span: Span, lhs: LHS, args: List[Expr]): FunApp =
    new FunApp(NU.makeASTNodeInfo(span), lhs, args)

  def makeNull(span: Span): Null =
    new Null(NU.makeASTNodeInfo(span))

  def makeVarRef(span: Span, id: Id): VarRef =
    new VarRef(NU.makeASTNodeInfo(span), id)

  def makeIntLiteral(span: Span, intVal: BigInteger, radix: Int = 10): IntLiteral =
    new IntLiteral(NU.makeASTNodeInfo(span), intVal, radix)

  def makeStringLiteral(span: Span, str: String, quote: String): StringLiteral =
    new StringLiteral(NU.makeASTNodeInfo(span), quote, str, false)

  def makeRegularExpression(span: Span, body: String, flags: String): RegularExpression =
    new RegularExpression(NU.makeASTNodeInfo(span), body, flags)

  def makeId(span: Span, name: String, uniq: String): Id =
    makeId(span, name, Some(uniq))

  def makeId(span: Span, name: String): Id =
    makeId(span, name, None)

  def makeId(span: Span, name: String, uniq: Option[String]): Id =
    new Id(NU.makeASTNodeInfo(span), name, uniq, false)

  def makeOp(span: Span, name: String): Op =
    new Op(NU.makeASTNodeInfo(span), name)

  def makeComment(span: Span, comment: String): Comment =
    new Comment(NU.makeASTNodeInfo(span), comment)

  def makeNoOp(span: Span, desc: String): NoOp =
    makeNoOp(NU.makeASTNodeInfo(span), desc)

  def makeNoOp(info: ASTNodeInfo, desc: String): NoOp =
    new NoOp(info, desc)

}
