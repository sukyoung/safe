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

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeUtil => NU }
import java.lang.{ Double => JDouble }

object IRFactory {
  val dummyAst = NU.makeNoOp(NU.makeASTNodeInfo(NU.makeSpan("dummyAST")), "dummyAST")
  // For use only when there is no hope of attaching a true span.
  def dummySpan(villain: String): Span = {
    val name = if (villain.length != 0) villain else "dummySpan"
    val sl = new SourceLoc(name, 0, 0, 0)
    new Span(sl, sl)
  }
  def makeInfo(span: Span): IRNodeInfo = new IRNodeInfo(span, false)
  def dummyIRId(name: String): IRId = makeTId(dummyAst, dummySpan(name), name)
  def dummyIRId(id: Id): IRId = {
    val name = id.text
    makeTId(dummyAst, dummySpan(name), name)
  }
  def dummyIRId(label: Label): IRId = {
    val name = label.id.text
    makeTId(dummyAst, dummySpan(name), name)
  }
  def dummyIRStmt(ast: ASTNode, span: Span): IRSeq =
    makeSeq(ast, span, Nil.asInstanceOf[List[IRStmt]])
  def dummyIRExpr(): IRExpr = makeTId(dummyAst, dummySpan("_"), "_")
  def dummyIRStmt(ast: ASTNode, span: Span, msg: String): IRSeq =
    makeSeq(dummyAst, span, List(makeExprStmt(dummyAst, span, dummyIRId(msg), dummyIRExpr)))

  def makeNodeInfo(fromSource: Boolean, span: Span): IRNodeInfo =
    new IRNodeInfo(span, fromSource)

  def makeNodeInfo(fromSource: Boolean, span: Span, ast: ASTNode): IRNodeInfo =
    new IRNodeInfo(span, fromSource, ast)

  def makeNodeInfo(info: IRNodeInfo, ast: ASTNode): IRNodeInfo =
    new IRNodeInfo(info.span, info.fromSource, ast)

  def makeFunctional(info: IRNodeInfo, fromSource: Boolean, ast: ASTNode,
    name: IRId, params: List[IRId], args: List[IRStmt],
    fds: List[IRFunDecl], vds: List[IRVarStmt],
    body: List[IRStmt]): IRFunctional =
    new IRFunctional(makeNodeInfo(info, ast), fromSource, name, params, args, fds, vds, body)

  def makeFunctional(info: IRNodeInfo, fromSource: Boolean, ast: ASTNode,
    name: IRId, params: List[IRId], body: IRStmt): IRFunctional =
    makeFunctional(info, fromSource, ast, name, params, Nil, Nil, Nil, List(body))

  def makeFunctional(info: IRNodeInfo, fromSource: Boolean, ast: ASTNode, name: IRId, params: List[IRId],
    body: List[IRStmt]): IRFunctional =
    makeFunctional(info, fromSource, ast, name, params, Nil, Nil, Nil, body)

  def makeRoot(): IRRoot =
    new IRRoot(
      makeNodeInfo(false, dummySpan("disambiguatorOnly")),
      Nil, Nil, Nil
    )

  def makeRoot(fromSource: Boolean, ast: ASTNode, span: Span, irs: List[IRStmt]): IRRoot =
    makeRoot(fromSource, ast, span, Nil, Nil, irs)

  def makeRoot(fromSource: Boolean, ast: ASTNode, span: Span, fds: List[IRFunDecl], vds: List[IRVarStmt],
    irs: List[IRStmt]): IRRoot =
    new IRRoot(makeNodeInfo(fromSource, span, ast), fds, vds, irs)

  def makeFunExpr(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, name: IRId,
    params: List[IRId], body: IRStmt): IRFunExpr =
    makeFunExpr(fromSource, ast, span, lhs, name, params, Nil, Nil, Nil, List(body))

  def getBody(ast: ASTNode): String = ast match {
    case FunExpr(_, Functional(_, _, _, _, _, _, bodyS)) => bodyS
    case FunDecl(_, Functional(_, _, _, _, _, _, bodyS), _) => bodyS
    case GetProp(_, _, Functional(_, _, _, _, _, _, bodyS)) => bodyS
    case SetProp(_, _, Functional(_, _, _, _, _, _, bodyS)) => bodyS
    case _ => "Not a function body"
  }

  def makeFunExpr(fromSource: Boolean, ast: ASTNode,
    span: Span, lhs: IRId, name: IRId, params: List[IRId], args: List[IRStmt],
    fds: List[IRFunDecl], vds: List[IRVarStmt], body: List[IRStmt]): IRFunExpr = {
    val info = makeNodeInfo(fromSource, span, ast)
    new IRFunExpr(info, lhs,
      makeFunctional(info, fromSource, ast, name, params, args, fds, vds, body))
  }

  def makeEval(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, arg: IRExpr): IREval =
    new IREval(makeNodeInfo(fromSource, span, ast), lhs, arg)

  def makeUn(fromSource: Boolean, ast: ASTNode, span: Span, op: IROp, expr: IRExpr): IRUn =
    new IRUn(makeNodeInfo(fromSource, span, ast), op, expr)

  def makeDelete(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, expr: IRId): IRDelete =
    new IRDelete(makeNodeInfo(fromSource, span, ast), lhs, expr)

  def makeDeleteProp(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, obj: IRId, index: IRExpr): IRDeleteProp =
    new IRDeleteProp(makeNodeInfo(fromSource, span, ast), lhs, obj, index)

  def makeObject(fromSource: Boolean, ast: ASTNode, span: Span,
    lhs: IRId, members: List[IRMember], proto: IRId): IRObject =
    makeObject(fromSource, ast, span, lhs, members, Some(proto))

  def makeObject(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, members: List[IRMember]): IRObject =
    makeObject(fromSource, ast, span, lhs, members, None)

  def makeObject(fromSource: Boolean, ast: ASTNode, span: Span,
    lhs: IRId, members: List[IRMember], proto: Option[IRId]): IRObject =
    new IRObject(makeNodeInfo(fromSource, span, ast), lhs, members, proto)

  def makeArray(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, elements: List[Option[IRExpr]]): IRArray =
    new IRArray(makeNodeInfo(fromSource, span, ast), lhs, elements)

  def makeArrayNumber(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, elements: List[JDouble]): IRStmt =
    new IRArrayNumber(makeNodeInfo(fromSource, span, ast), lhs, elements)

  def makeArgs(ast: ASTNode, span: Span, lhs: IRId, elements: List[Option[IRExpr]]): IRArgs =
    new IRArgs(makeNodeInfo(false, span, ast), lhs, elements)

  def makeLoad(fromSource: Boolean, ast: ASTNode, span: Span, obj: IRId, index: IRExpr): IRLoad =
    new IRLoad(makeNodeInfo(fromSource, span, ast), obj, index)

  def makeInternalCall(ast: ASTNode, span: Span, lhs: IRId, fun: IRId, arg: IRExpr): IRInternalCall =
    makeInternalCall(ast, span, lhs, fun, arg, None)

  def makeInternalCall(ast: ASTNode, span: Span, lhs: IRId, fun: IRId, arg1: IRId, arg2: IRId): IRInternalCall =
    makeInternalCall(ast, span, lhs, fun, arg1, Some(arg2))

  def makeInternalCall(ast: ASTNode, span: Span, lhs: IRId, fun: IRId, arg1: IRExpr, arg2: Option[IRId]): IRInternalCall =
    new IRInternalCall(makeNodeInfo(false, span, ast), lhs, fun, arg1, arg2)

  def makeCall(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, fun: IRId, thisB: IRId, args: IRId): IRCall =
    new IRCall(makeNodeInfo(fromSource, span, ast), lhs, fun, thisB, args)

  def makeNew(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, fun: IRId, args: List[IRId]): IRNew =
    new IRNew(makeNodeInfo(fromSource, span, ast), lhs, fun, args)

  def makeBin(fromSource: Boolean, ast: ASTNode, span: Span, first: IRExpr, op: IROp, second: IRExpr): IRBin =
    new IRBin(makeNodeInfo(fromSource, span, ast), first, op, second)

  def makeLoadStmt(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, obj: IRId, index: IRExpr): IRExprStmt =
    makeExprStmt(ast, span, lhs, makeLoad(fromSource, ast, span, obj, index))

  def makeExprStmt(ast: ASTNode, span: Span, lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, span, lhs, right, false)

  def makeExprStmtIgnore(ast: ASTNode, span: Span, lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, span, lhs, right, true)

  def makeExprStmt(ast: ASTNode, span: Span, lhs: IRId, right: IRExpr, isRef: Boolean): IRExprStmt =
    new IRExprStmt(makeNodeInfo(false, span, ast), lhs, right, isRef)

  def makeFunDecl(fromSource: Boolean, ast: ASTNode, span: Span,
    name: IRId, params: List[IRId], body: IRStmt): IRFunDecl =
    makeFunDecl(fromSource, ast, span, name, params, Nil, Nil, Nil, List(body))

  def makeFunDecl(fromSource: Boolean, ast: ASTNode,
    span: Span, name: IRId, params: List[IRId], args: List[IRStmt],
    fds: List[IRFunDecl], vds: List[IRVarStmt], body: List[IRStmt]): IRFunDecl = {
    val info = makeNodeInfo(fromSource, span, ast)
    new IRFunDecl(
      info,
      makeFunctional(info, fromSource, ast, name, params, args, fds, vds, body)
    )
  }

  def makeBreak(fromSource: Boolean, ast: ASTNode, span: Span, label: IRId): IRBreak =
    new IRBreak(makeNodeInfo(fromSource, span, ast), label)

  def makeReturn(fromSource: Boolean, ast: ASTNode, span: Span, expr: Option[IRExpr]): IRReturn =
    new IRReturn(makeNodeInfo(fromSource, span, ast), expr)

  def makeLabelStmt(fromSource: Boolean, ast: ASTNode, span: Span, label: IRId, stmt: IRStmt): IRLabelStmt =
    new IRLabelStmt(makeNodeInfo(fromSource, span, ast), label, stmt)

  def makeWith(fromSource: Boolean, ast: ASTNode, span: Span, id: IRId, stmt: IRStmt): IRWith =
    new IRWith(makeNodeInfo(fromSource, span, ast), id, stmt)

  def makeThrow(fromSource: Boolean, ast: ASTNode, span: Span, expr: IRExpr): IRThrow =
    new IRThrow(makeNodeInfo(fromSource, span, ast), expr)

  def makeVarStmt(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, fromParam: Boolean): IRVarStmt =
    new IRVarStmt(makeNodeInfo(fromSource, span, ast), lhs, fromParam)

  def makeIf(fromSource: Boolean, ast: ASTNode, span: Span, cond: IRExpr, trueB: IRStmt, falseB: Option[IRStmt]): IRIf =
    new IRIf(makeNodeInfo(fromSource, span, ast), cond, trueB, falseB)

  def makeWhile(fromSource: Boolean, ast: ASTNode, span: Span, cond: IRExpr, body: IRStmt): IRWhile =
    new IRWhile(makeNodeInfo(fromSource, span, ast), cond, body)

  def makeTry(fromSource: Boolean, ast: ASTNode, span: Span,
    body: IRStmt, name: Option[IRId], catchB: Option[IRStmt], finallyB: Option[IRStmt]): IRTry =
    new IRTry(makeNodeInfo(fromSource, span, ast), body, name, catchB, finallyB)

  def makeStore(fromSource: Boolean, ast: ASTNode, span: Span, obj: IRId, index: IRExpr, rhs: IRExpr): IRStore =
    new IRStore(makeNodeInfo(fromSource, span, ast), obj, index, rhs)

  def makeSeq(ast: ASTNode, span: Span, first: IRStmt, second: IRStmt): IRSeq =
    makeSeq(ast, span, List(first, second))

  def makeSeq(ast: ASTNode, span: Span): IRSeq =
    makeSeq(ast, span, Nil)

  def makeSeq(ast: ASTNode, span: Span, stmt: IRStmt): IRSeq =
    makeSeq(ast, span, List(stmt))

  def makeSeq(ast: ASTNode, span: Span, stmts: List[IRStmt]): IRSeq =
    new IRSeq(makeNodeInfo(false, span, ast), stmts)

  def makeStmtUnit(ast: ASTNode, span: Span): IRStmtUnit =
    makeStmtUnit(ast, span, Nil)

  def makeStmtUnit(ast: ASTNode, span: Span, stmt: IRStmt): IRStmtUnit =
    makeStmtUnit(ast, span, List(stmt))

  def makeStmtUnit(ast: ASTNode, span: Span, first: IRStmt, second: IRStmt): IRStmtUnit =
    makeStmtUnit(ast, span, List(first, second))

  def makeStmtUnit(ast: ASTNode, span: Span, stmts: List[IRStmt]): IRStmtUnit =
    new IRStmtUnit(makeNodeInfo(true, span, ast), stmts)

  /*
  def makeGetProp(fromSource: Boolean, ast: ASTNode, span: Span, prop: IRId, body: IRStmt): IRGetProp =
    makeGetProp(fromSource, ast, span,
                makeFunctional(info, fromSource, ast, prop, Nil.asInstanceOf[List[IRId]], body))
        */

  def makeGetProp(fromSource: Boolean, ast: ASTNode,
    span: Span, name: IRId, params: List[IRId], args: List[IRStmt],
    fds: List[IRFunDecl], vds: List[IRVarStmt],
    body: List[IRStmt]): IRGetProp = {
    val info = makeNodeInfo(fromSource, span, ast)
    makeGetProp(info, makeFunctional(info, true, ast, name, params, args, fds, vds, body))
  }

  def makeGetProp(info: IRNodeInfo, functional: IRFunctional): IRGetProp =
    new IRGetProp(info, functional)

  def makeSetProp(fromSource: Boolean, ast: ASTNode,
    span: Span, name: IRId, params: List[IRId], args: List[IRStmt],
    fds: List[IRFunDecl], vds: List[IRVarStmt],
    body: List[IRStmt]): IRSetProp = {
    val info = makeNodeInfo(fromSource, span, ast)
    makeSetProp(info, makeFunctional(info, true, ast, name, params, args, fds, vds, body))
  }

  /*
  def makeSetProp(fromSource: Boolean, ast: ASTNode, span: Span, prop: IRId, id: IRId, body: IRStmt): IRSetProp =
    makeSetProp(fromSource, ast, span, makeFunctional(true, ast, prop, List(id), body))
        */

  def makeSetProp(info: IRNodeInfo, functional: IRFunctional): IRSetProp =
    new IRSetProp(info, functional)

  def makeField(fromSource: Boolean, ast: ASTNode, span: Span, prop: IRId, expr: IRExpr): IRField =
    new IRField(makeNodeInfo(fromSource, span, ast), prop, expr)

  val defaultSpan = NU.makeSpan("Default span for internally generated nodes")
  val defaultInfo = new IRNodeInfo(defaultSpan, false)
  def trueInfo(ast: ASTNode): IRNodeInfo = new IRNodeInfo(ast.info.span, true, ast)
  def makeSourceInfo(fromSource: Boolean, ast: ASTNode): IRNodeInfo =
    if (fromSource) trueInfo(ast) else defaultInfo
  def makeBool(fromSource: Boolean, ast: ASTNode, bool: Boolean): IRBool =
    new IRBool(makeSourceInfo(fromSource, ast), bool)
  val trueV = makeBool(false, dummyAst, true)
  val falseV = makeBool(false, dummyAst, false)
  def makeNull(ast: ASTNode): IRNull = new IRNull(trueInfo(ast))
  def makeUndef(ast: ASTNode): IRUndef = new IRUndef(trueInfo(ast))

  def makeNumber(fromSource: Boolean, text: String, num: Double): IRNumber =
    makeNumber(fromSource, dummyAst, text, num)
  def makeNumber(fromSource: Boolean, ast: ASTNode, text: String, num: Double): IRNumber =
    new IRNumber(makeSourceInfo(fromSource, ast), text, num)
  val oneV = makeNumber(false, "1", 1)

  val zero = new IRString(defaultInfo, "0")
  val one = new IRString(defaultInfo, "1")
  val two = new IRString(defaultInfo, "2")
  val three = new IRString(defaultInfo, "3")
  val four = new IRString(defaultInfo, "4")
  val five = new IRString(defaultInfo, "5")
  val six = new IRString(defaultInfo, "6")
  val seven = new IRString(defaultInfo, "7")
  val eight = new IRString(defaultInfo, "8")
  val nine = new IRString(defaultInfo, "9")
  def makeString(str: String, ast: ASTNode): IRString = makeString(false, ast, str)
  def makeString(fromSource: Boolean, ast: ASTNode, str1: String): IRString = {
    if (str1.equals("0")) zero
    else if (str1.equals("1")) one
    else if (str1.equals("2")) two
    else if (str1.equals("3")) three
    else if (str1.equals("4")) four
    else if (str1.equals("5")) five
    else if (str1.equals("6")) six
    else if (str1.equals("7")) seven
    else if (str1.equals("8")) eight
    else if (str1.equals("9")) nine
    else new IRString(makeSourceInfo(fromSource, ast), str1)
  }
  ////////////////////////////////////////////////////////////////////////////////////////

  def makeThis(ast: ASTNode, span: Span): IRThis =
    new IRThis(makeNodeInfo(true, span, ast))

  // make a user id
  def makeUId(originalName: String, uniqueName: String, isGlobal: Boolean,
    ast: ASTNode, span: Span, isWith: Boolean): IRUserId =
    new IRUserId(makeNodeInfo(true, span, ast), originalName, uniqueName, isGlobal, isWith)

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
    makeUId(uniqueName, uniqueName, true, ast, ast.info.span, false)

  // make a global user id
  def makeGId(ast: ASTNode, originalName: String, uniqueName: String, span: Span): IRUserId =
    makeUId(originalName, uniqueName, true, ast, span, false)

  // make a non-global temporary id
  def makeTId(span: Span, uniqueName: String): IRTmpId =
    makeTId(span, uniqueName, false)

  def makeTId(span: Span, uniqueName: String, isGlobal: Boolean): IRTmpId =
    new IRTmpId(makeNodeInfo(false, span), uniqueName, uniqueName, isGlobal)

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
    new IRTmpId(makeNodeInfo(fromSource, span, ast), originalName, uniqueName, isGlobal)

  def makeOp(name: String, kind: Int = 0): IROp =
    new IROp(makeNodeInfo(false, dummySpan(name)), name, if (kind == 0) EJSOp.strToEJSOp(name) else kind)

  def makeNoOp(ast: ASTNode, span: Span, desc: String): IRNoOp =
    new IRNoOp(makeNodeInfo(false, span, ast), desc)
}
