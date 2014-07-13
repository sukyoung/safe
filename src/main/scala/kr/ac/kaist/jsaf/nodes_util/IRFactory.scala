/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU}
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF}
import kr.ac.kaist.jsaf.nodes_util._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.useful.Useful

import edu.rice.cs.plt.tuple.{Option => JOption}

import _root_.java.lang.{Integer => JInt}
import _root_.java.lang.{Double => JDouble}
import _root_.java.util.{List => JList}
import _root_.java.io.BufferedWriter
import _root_.java.io.File
import _root_.java.math.BigInteger
import _root_.java.util.ArrayList
import _root_.java.util.Arrays
import _root_.java.util.Collections
import _root_.java.util.Set
import _root_.java.util.StringTokenizer

object IRFactory {
  val dummyAst = NF.makeNoOp(NF.makeSpanInfo(NF.makeSpan("dummyAST")), "dummyAST")
  // For use only when there is no hope of attaching a true span.
  def dummySpan(villain: String): Span = {
    val name = if (villain.length != 0) villain else "dummySpan"
    val sl = new SourceLocRats(name,0,0,0)
    new Span(sl,sl)
  }
  def makeInfo(span: Span): IRSpanInfo = new IRSpanInfo(false, span)
  def dummyIRId(name: String): IRId = makeTId(dummyAst, dummySpan(name), name)
  def dummyIRId(id: Id): IRId = {
    val name = id.getText
    makeTId(dummyAst, dummySpan(name), name)
  }
  def dummyIRId(label: Label): IRId = {
    val name = label.getId.getText
    makeTId(dummyAst, dummySpan(name), name)
  }
  def dummyIRStmt(ast: ASTNode, span: Span): IRSeq =
    makeSeq(ast, span, Nil.asInstanceOf[List[IRStmt]])
  def dummyIRExpr(): IRExpr = makeTId(dummyAst, dummySpan("_"), "_")
  def dummyIRStmt(ast: ASTNode, span: Span, msg: String): IRSeq =
    makeSeq(dummyAst, span, List(makeExprStmt(dummyAst, span, dummyIRId(msg), dummyIRExpr)))

  def makeSpanInfo(fromSource: Boolean, span: Span): IRSpanInfo =
    new IRSpanInfo(fromSource, span)

  def makeFunctional(fromSource: Boolean, ast: ASTNode,
                     name: IRId, params: JList[IRId], args: JList[IRStmt],
                     fds: JList[IRFunDecl], vds: JList[IRVarStmt],
                     body: JList[IRStmt]): IRFunctional =
    NF.putIr2ast(new IRFunctional(fromSource, name, params, args, fds, vds, body), ast)

  def makeFunctional(fromSource: Boolean, ast: ASTNode,
                     name: IRId, params: JList[IRId], body: IRStmt): IRFunctional =
    makeFunctional(fromSource, ast, name, params, toJavaList(Nil), toJavaList(Nil),
                   toJavaList(Nil), toJavaList(List(body)))

  def makeFunctional(fromSource: Boolean, ast: ASTNode, name: IRId, params: JList[IRId],
                     body: JList[IRStmt]): IRFunctional =
    makeFunctional(fromSource, ast, name, params, toJavaList(Nil), toJavaList(Nil),
                   toJavaList(Nil), body)

  def makeRoot(): IRRoot =
    new IRRoot(makeSpanInfo(false, dummySpan("disambiguatorOnly")),
               toJavaList(Nil), toJavaList(Nil), toJavaList(Nil))

  def makeRoot(fromSource: Boolean, ast: ASTNode, span: Span, irs: JList[IRStmt]): IRRoot =
    makeRoot(fromSource, ast, span, toJavaList(Nil), toJavaList(Nil), irs)

  def makeRoot(fromSource: Boolean, ast: ASTNode, span: Span, fds: JList[IRFunDecl], vds: JList[IRVarStmt],
               irs: JList[IRStmt]): IRRoot =
    NF.putIr2ast(new IRRoot(makeSpanInfo(fromSource, span), fds, vds, irs), ast)

  def makeFunExpr(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, name: IRId,
                  params: JList[IRId], body: IRStmt): IRFunExpr =
    makeFunExpr(fromSource, ast, span, lhs, name, params, toJavaList(Nil), toJavaList(Nil),
                toJavaList(Nil), toJavaList(List(body)))

  def makeFunExpr(fromSource: Boolean, ast: ASTNode,
                  span: Span, lhs: IRId, name: IRId, params: JList[IRId], args: JList[IRStmt],
                  fds: JList[IRFunDecl], vds: JList[IRVarStmt], body: JList[IRStmt]): IRFunExpr =
    NF.putIr2ast(new IRFunExpr(makeSpanInfo(fromSource, span), lhs,
                               makeFunctional(fromSource, ast, name, params, args, fds, vds, body)), ast)

  def makeEval(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, arg: IRExpr) =
    NF.putIr2ast(new IREval(makeSpanInfo(fromSource, span), lhs, arg), ast)

  def makeUn(fromSource: Boolean, ast: ASTNode, span: Span, op: IROp, expr: IRExpr) =
    NF.putIr2ast(new IRUn(makeSpanInfo(fromSource, span), op, expr), ast)

  def makeDelete(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, expr: IRId) =
    NF.putIr2ast(new IRDelete(makeSpanInfo(fromSource, span), lhs, expr), ast)

  def makeDeleteProp(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, obj: IRId, index: IRExpr) =
    NF.putIr2ast(new IRDeleteProp(makeSpanInfo(fromSource, span), lhs, obj, index), ast)

  def makeObject(fromSource: Boolean, ast: ASTNode, span: Span,
                 lhs: IRId, members: List[IRMember], proto: IRId): IRObject =
    makeObject(fromSource, ast, span, lhs, toJavaList(members), Some(proto))

  def makeObject(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, members: List[IRMember]): IRObject =
    makeObject(fromSource, ast, span, lhs, toJavaList(members), None)

  def makeObject(fromSource: Boolean, ast: ASTNode, span: Span,
                 lhs: IRId, members: JList[IRMember], proto: Option[IRId]): IRObject =
    NF.putIr2ast(new IRObject(makeSpanInfo(fromSource, span), lhs, members, proto), ast)

  def makeArray(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, elements: List[Option[IRExpr]]) : IRArray = {
    val new_elements = toJavaList(elements.map(toJavaOption(_)))
    makeArray(fromSource, ast, span, lhs, new_elements)
  }

  def makeArray(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, elements: JList[JOption[IRExpr]]) : IRArray =
    NF.putIr2ast(new IRArray(makeSpanInfo(fromSource, span), lhs, elements), ast)

  def makeArrayNumber(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, elements: JList[JDouble]) : IRStmt =
    NF.putIr2ast(new IRArrayNumber(makeSpanInfo(fromSource, span), lhs, elements), ast)

  def makeArgs(ast: ASTNode, span: Span, lhs: IRId, elements: List[Option[IRExpr]]) : IRArgs = {
    val new_elements = toJavaList(elements.map(toJavaOption(_)))
    makeArgs(ast, span, lhs, new_elements)
  }

  def makeArgs(ast: ASTNode, span: Span, lhs: IRId, elements: JList[JOption[IRExpr]]) : IRArgs =
    NF.putIr2ast(new IRArgs(makeSpanInfo(false, span), lhs, elements), ast)

  def makeLoad(fromSource: Boolean, ast: ASTNode, span: Span, obj: IRId, index: IRExpr) =
    NF.putIr2ast(new IRLoad(makeSpanInfo(fromSource, span), obj, index), ast)

  def makeInternalCall(ast: ASTNode, span: Span, lhs: IRId, fun: IRId, arg: IRExpr) : IRInternalCall =
    makeInternalCall(ast, span, lhs, fun, arg, None)

  def makeInternalCall(ast: ASTNode, span: Span, lhs: IRId, fun: IRId, arg1: IRId, arg2: IRId) : IRInternalCall =
    makeInternalCall(ast, span, lhs, fun, arg1, Some(arg2))

  def makeInternalCall(ast: ASTNode, span: Span, lhs: IRId, fun: IRId, arg1: IRExpr, arg2: Option[IRId]) : IRInternalCall =
    NF.putIr2ast(new IRInternalCall(makeSpanInfo(false, span), lhs, fun, arg1, toJavaOption(arg2)), ast)

  def makeCall(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, fun: IRId, thisB: IRId, args: IRId) : IRCall =
    NF.putIr2ast(new IRCall(makeSpanInfo(fromSource, span), lhs, fun, thisB, args), ast)

  def makeNew(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, fun: IRId, args: List[IRId]) : IRNew =
    makeNew(fromSource, ast, span, lhs, fun, toJavaList(args))

  def makeNew(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, fun: IRId, args: JList[IRId]) : IRNew =
    NF.putIr2ast(new IRNew(makeSpanInfo(fromSource, span), lhs, fun, args), ast)

  def makeBin(fromSource: Boolean, ast: ASTNode, span: Span, first: IRExpr, op: IROp, second: IRExpr) =
    NF.putIr2ast(new IRBin(makeSpanInfo(fromSource, span), first, op, second), ast)

  def makeLoadStmt(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, obj: IRId, index: IRExpr) =
    makeExprStmt(ast, span, lhs, makeLoad(fromSource, ast, span, obj, index))

  def makeExprStmt(ast: ASTNode, span: Span, lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, span, lhs, right, false)

  def makeExprStmtIgnore(ast: ASTNode, span: Span, lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, span, lhs, right, true)

  def makeExprStmt(ast: ASTNode, span: Span, lhs: IRId, right: IRExpr, isRef: Boolean): IRExprStmt =
    NF.putIr2ast(new IRExprStmt(makeSpanInfo(false, span), lhs, right, isRef), ast)

  def makeFunDecl(fromSource: Boolean, ast: ASTNode, span: Span,
                  name: IRId, params: JList[IRId], body: IRStmt): IRFunDecl =
    makeFunDecl(fromSource, ast, span, name, params, toJavaList(Nil), toJavaList(Nil),
                toJavaList(Nil), toJavaList(List(body)))

  def makeFunDecl(fromSource: Boolean, ast: ASTNode,
                  span: Span, name: IRId, params: JList[IRId], args: JList[IRStmt],
                  fds: JList[IRFunDecl], vds: JList[IRVarStmt], body: JList[IRStmt]): IRFunDecl =
    NF.putIr2ast(new IRFunDecl(makeSpanInfo(fromSource, span),
                               makeFunctional(fromSource, ast, name, params, args, fds, vds, body)), ast)

  def makeBreak(fromSource: Boolean, ast: ASTNode, span: Span, label: IRId): IRBreak =
    NF.putIr2ast(new IRBreak(makeSpanInfo(fromSource, span), label), ast)

  def makeReturn(fromSource: Boolean, ast: ASTNode, span: Span, expr: JOption[IRExpr]) =
    NF.putIr2ast(new IRReturn(makeSpanInfo(fromSource, span), expr), ast)

  def makeLabelStmt(fromSource: Boolean, ast: ASTNode, span: Span, label: IRId, stmt: IRStmt): IRLabelStmt =
    NF.putIr2ast(new IRLabelStmt(makeSpanInfo(fromSource, span), label, stmt), ast)

  def makeWith(fromSource: Boolean, ast: ASTNode, span: Span, id: IRId, stmt: IRStmt) =
    NF.putIr2ast(new IRWith(makeSpanInfo(fromSource, span), id, stmt), ast)

  def makeThrow(fromSource: Boolean, ast: ASTNode, span: Span, expr: IRExpr) =
    NF.putIr2ast(new IRThrow(makeSpanInfo(fromSource, span), expr), ast)

  def makeVarStmt(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, fromParam: Boolean): IRVarStmt =
    NF.putIr2ast(new IRVarStmt(makeSpanInfo(fromSource, span), lhs, fromParam), ast)

  def makeIf(fromSource: Boolean, ast: ASTNode, span: Span, cond: IRExpr, trueB: IRStmt, falseB: JOption[IRStmt]) =
    NF.putIr2ast(new IRIf(makeSpanInfo(fromSource, span), cond, trueB, falseB), ast)

  def makeWhile(fromSource: Boolean, ast: ASTNode, span: Span, cond: IRExpr, body: IRStmt) =
    NF.putIr2ast(new IRWhile(makeSpanInfo(fromSource, span), cond, body), ast)

  def makeTry(fromSource: Boolean, ast: ASTNode, span: Span,
              body: IRStmt, name: JOption[IRId], catchB: JOption[IRStmt], finallyB: JOption[IRStmt]) =
    NF.putIr2ast(new IRTry(makeSpanInfo(fromSource, span), body, name, catchB, finallyB), ast)

  def makeStore(fromSource: Boolean, ast: ASTNode, span: Span, obj: IRId, index: IRExpr, rhs: IRExpr) =
    NF.putIr2ast(new IRStore(makeSpanInfo(fromSource, span), obj, index, rhs), ast)

  def makeSeq(ast: ASTNode, span: Span, first: IRStmt, second: IRStmt): IRSeq =
    makeSeq(ast, span, List(first, second))

  def makeSeq(ast: ASTNode, span: Span): IRSeq =
    makeSeq(ast, span, Nil)

  def makeSeq(ast: ASTNode, span: Span, stmt: IRStmt): IRSeq =
    makeSeq(ast, span, List(stmt))

  def makeSeq(ast: ASTNode, span: Span, stmts: List[IRStmt]): IRSeq =
    NF.putIr2ast(new IRSeq(makeSpanInfo(false, span), toJavaList(stmts)), ast)

  def makeStmtUnit(ast: ASTNode, span: Span): IRStmtUnit =
    makeStmtUnit(ast, span, Useful.list().asInstanceOf[JList[IRStmt]])

  def makeStmtUnit(ast: ASTNode, span: Span, stmt: IRStmt): IRStmtUnit =
    makeStmtUnit(ast, span, Useful.list(stmt))

  def makeStmtUnit(ast: ASTNode, span: Span, first: IRStmt, second: IRStmt): IRStmtUnit =
    makeStmtUnit(ast, span, Useful.list(first, second))

  def makeStmtUnit(ast: ASTNode, span: Span, stmts: List[IRStmt]): IRStmtUnit =
    makeStmtUnit(ast, span, toJavaList(stmts))

  def makeStmtUnit(ast: ASTNode, span: Span, stmts: JList[IRStmt]): IRStmtUnit =
    NF.putIr2ast(new IRStmtUnit(makeSpanInfo(true, span), stmts), ast)

  def makeGetProp(fromSource: Boolean, ast: ASTNode, span: Span, prop: IRId, body: IRStmt): IRGetProp =
    makeGetProp(fromSource, ast, span,
                makeFunctional(fromSource, ast, prop, toJavaList(Nil).asInstanceOf[JList[IRId]], body))

  def makeGetProp(fromSource: Boolean, ast: ASTNode,
                  span: Span, name: IRId, params: JList[IRId], args: JList[IRStmt],
                  fds: JList[IRFunDecl], vds: JList[IRVarStmt],
                  body: JList[IRStmt]): IRGetProp =
    makeGetProp(fromSource, ast, span, makeFunctional(true, ast, name, params, args, fds, vds, body))

  def makeGetProp(fromSource: Boolean, ast: ASTNode, span: Span, functional: IRFunctional): IRGetProp =
    NF.putIr2ast(new IRGetProp(makeSpanInfo(fromSource, span), functional), ast)

  def makeSetProp(fromSource: Boolean, ast: ASTNode,
                  span: Span, name: IRId, params: JList[IRId], args: JList[IRStmt],
                  fds: JList[IRFunDecl], vds: JList[IRVarStmt],
                  body: JList[IRStmt]): IRSetProp =
    makeSetProp(fromSource, ast, span, makeFunctional(true, ast, name, params, args, fds, vds, body))

  def makeSetProp(fromSource: Boolean, ast: ASTNode, span: Span, prop: IRId, id: IRId, body: IRStmt): IRSetProp =
    makeSetProp(fromSource, ast, span, makeFunctional(true, ast, prop, toJavaList(List(id)), body))

  def makeSetProp(fromSource: Boolean, ast: ASTNode, span: Span, functional: IRFunctional) =
    NF.putIr2ast(new IRSetProp(makeSpanInfo(fromSource, span), functional), ast)

  def makeField(fromSource: Boolean, ast: ASTNode, span: Span, prop: IRId, expr: IRExpr) =
    NF.putIr2ast(new IRField(makeSpanInfo(fromSource, span), prop, expr), ast)

  val defaultSpan = NF.makeSpan("Default span for internally generated nodes")
  val defaultInfo = new IRSpanInfo(false, defaultSpan)
  def trueInfo(ast: ASTNode) = NF.putIr2ast(new IRSpanInfo(true, ast.getInfo.getSpan), ast)
  def makeSourceInfo(fromSource: Boolean, ast: ASTNode) =
    if (fromSource) trueInfo(ast) else defaultInfo
  def makeBool(fromSource: Boolean, ast: ASTNode, bool: Boolean): IRBool =
    new IRBool(makeSourceInfo(fromSource, ast), bool)
  val trueV = makeBool(false, dummyAst, true)
  val falseV = makeBool(false, dummyAst, false)
  def makeNull(ast: ASTNode) = new IRNull(trueInfo(ast))
  def makeUndef(ast: ASTNode) = new IRUndef(trueInfo(ast))

  def makeNumber(fromSource: Boolean, text: String, num: Double): IRNumber =
    makeNumber(fromSource, dummyAst, text, num)
  def makeNumber(fromSource: Boolean, ast: ASTNode, text: String, num: Double): IRNumber =
    new IRNumber(makeSourceInfo(fromSource, ast), text, num)
  val oneV = makeNumber(false, "1", 1)

  val zero  = new IRString(defaultInfo, "0")
  val one   = new IRString(defaultInfo, "1")
  val two   = new IRString(defaultInfo, "2")
  val three = new IRString(defaultInfo, "3")
  val four  = new IRString(defaultInfo, "4")
  val five  = new IRString(defaultInfo, "5")
  val six   = new IRString(defaultInfo, "6")
  val seven = new IRString(defaultInfo, "7")
  val eight = new IRString(defaultInfo, "8")
  val nine  = new IRString(defaultInfo, "9")
  def makeString(str: String, ast: ASTNode): IRString = makeString(false, ast, str)
  def makeString(fromSource: Boolean, ast: ASTNode, str1: String): IRString = {
      if(str1.equals("0")) zero
      else if(str1.equals("1")) one
      else if(str1.equals("2")) two
      else if(str1.equals("3")) three
      else if(str1.equals("4")) four
      else if(str1.equals("5")) five
      else if(str1.equals("6")) six
      else if(str1.equals("7")) seven
      else if(str1.equals("8")) eight
      else if(str1.equals("9")) nine
      else new IRString(makeSourceInfo(fromSource, ast), str1)
  }
////////////////////////////////////////////////////////////////////////////////////////

  def makeThis(ast: ASTNode, span: Span) =
    NF.putIr2ast(new IRThis(makeSpanInfo(true, span)), ast)

  // make a user id
  def makeUId(originalName: String, uniqueName: String, isGlobal: Boolean,
              ast: ASTNode, span: Span, isWith: Boolean): IRUserId =
    NF.putIr2ast(new IRUserId(makeSpanInfo(true, span), originalName, uniqueName, isGlobal, isWith), ast)

  // make a withRewriter-generated id
  def makeWId(originalName: String, uniqueName: String, isGlobal: Boolean,
              ast: ASTNode, span: Span): IRUserId =
    makeUId(originalName, uniqueName, isGlobal, ast, span, true)

  // make a non-global user id
  def makeNGId(uniqueName: String, ast: ASTNode, span: Span): IRUserId =
    makeUId(uniqueName, uniqueName, false, ast, span, false)

  def makeNGId(originalName: String, uniqueName: String, ast: ASTNode, span: Span): IRUserId =
    makeUId(originalName, uniqueName, false, ast, span, false)

  // make a global user id
  def makeGId(ast: ASTNode, uniqueName: String): IRUserId =
    makeUId(uniqueName, uniqueName, true, ast, ast.getInfo.getSpan, false)

  // make a global user id
  def makeGId(ast: ASTNode, originalName: String, uniqueName: String, span: Span): IRUserId =
    makeUId(originalName, uniqueName, true, ast, span, false)

  // make a non-global temporary id
  def makeTId(span: Span, uniqueName: String): IRTmpId =
    makeTId(span, uniqueName, false)

  def makeTId(span: Span, uniqueName: String, isGlobal: Boolean): IRTmpId =
    new IRTmpId(makeSpanInfo(false, span), uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, span: Span, uniqueName: String): IRTmpId =
    makeTId(false, ast, span, uniqueName, uniqueName, false)

  def makeTId(fromSource: Boolean, ast: ASTNode, span: Span, uniqueName: String): IRTmpId =
    makeTId(fromSource, ast, span, uniqueName, uniqueName, false)

  // make a temporary id
  def makeTId(ast: ASTNode, span: Span, uniqueName: String, isGlobal: Boolean): IRTmpId =
    makeTId(false, ast, span, uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, span: Span, originalName: String, uniqueName: String, isGlobal: Boolean): IRTmpId =
    makeTId(false, ast, span, originalName, uniqueName, isGlobal)

  def makeTId(fromSource: Boolean, ast: ASTNode, span: Span, originalName: String, uniqueName: String,
              isGlobal: Boolean): IRTmpId =
    NF.putIr2ast(new IRTmpId(makeSpanInfo(fromSource, span), originalName, uniqueName, isGlobal), ast)

  def makeOp(name: String, kind: Int = 0) = {
    new IROp(name, if(kind == 0) EJSOp.strToEJSOp(name) else kind)
  }

  def makeNoOp(ast: ASTNode, span: Span, desc: String) =
    NF.putIr2ast(new IRNoOp(makeSpanInfo(false, span), desc), ast)
}
