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

package kr.ac.kaist.safe.nodes.ir

import _root_.java.lang.{ Double => JDouble }

import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.util._

object IRFactory {

  val dummyAST = NF.dummyAST
  val trueV = makeBoolIR(true)
  val falseV = makeBoolIR(false)
  val oneV = makeNumber("1", 1)
  val zero = EJSString("0")
  val one = EJSString("1")
  val two = EJSString("2")
  val three = EJSString("3")
  val four = EJSString("4")
  val five = EJSString("5")
  val six = EJSString("6")
  val seven = EJSString("7")
  val eight = EJSString("8")
  val nine = EJSString("9")

  def dummyIRId(name: String): IRId = {
    makeTId(dummyAST, name)
  }

  def dummyIRId(id: Id): IRId = {
    val name = id.text
    makeTId(dummyAST, name)
  }

  // makeSpanInfo(false, span) with span passed as argument to this function

  def makeTId(ast: ASTNode, uniqueName: String): IRTmpId = {
    makeTId(ast, uniqueName, false)
  }

  // make a temporary id
  def makeTId(
    ast: ASTNode,
    uniqueName: String,
    isGlobal: Boolean
  ): IRTmpId = {
    makeTId(ast, uniqueName, uniqueName, isGlobal)
  }

  def makeTId(
    ast: ASTNode,
    originalName: String,
    uniqueName: String,
    isGlobal: Boolean
  ): IRTmpId = {
    NF.putIr(new IRTmpId(ast, originalName, uniqueName, isGlobal), ast)
  }

  def dummyIRId(label: Label): IRId = {
    val name = label.id.text
    makeTId(dummyAST, name)
  }

  def dummyIRStmt(ast: ASTNode): IRSeq = {
    makeSeq(ast, Nil.asInstanceOf[List[IRStmt]])
  }

  def dummyIRExpr: IRExpr = {
    makeTId(dummyAST, "_")
  }

  def dummyIRStmt(ast: ASTNode, msg: String): IRSeq = {
    makeSeq(dummyAST, List(makeExprStmt(dummyAST, dummyIRId(msg), dummyIRExpr)))
  }

  def makeFunctional(
    fromSource: Boolean,
    ast: Functional,
    name: IRId,
    params: List[IRId],
    body: IRStmt
  ): IRFunctional = {
    makeFunctional(fromSource, ast, name, params, Nil, Nil, Nil, List(body))
  }

  def makeFunctional(
    fromSource: Boolean,
    ast: Functional,
    name: IRId,
    params: List[IRId],
    body: List[IRStmt]
  ): IRFunctional = {
    makeFunctional(fromSource, ast, name, params, Nil, Nil, Nil, body)
  }

  def makeFunctional(
    fromSource: Boolean,
    ast: Functional,
    name: IRId,
    params: List[IRId],
    args: List[IRStmt],
    fds: List[IRFunDecl],
    vds: List[IRVarStmt],
    body: List[IRStmt]
  ): IRFunctional = {
    NF.putIr(new IRFunctional(ast, fromSource, name, params, args, fds, vds, body), ast)
  }

  def makeFunExpr(
    fromSource: Boolean,
    ast: Functional,
    lhs: IRId,
    name: IRId,
    params: List[IRId],
    body: IRStmt
  ): IRFunExpr = {
    makeFunExpr(fromSource, ast, lhs, name, params, Nil, Nil, Nil, List(body))
  }

  def makeFunExpr(
    fromSource: Boolean,
    ast: Functional,
    lhs: IRId,
    name: IRId,
    params: List[IRId],
    args: List[IRStmt],
    fds: List[IRFunDecl],
    vds: List[IRVarStmt],
    body: List[IRStmt]
  ): IRFunExpr = {
    NF.putIr(new IRFunExpr(ast, lhs, makeFunctional(fromSource, ast, name, params, args, fds, vds, body)), ast)
  }

  def makeLoadStmt(
    fromSource: Boolean,
    ast: ASTNode,
    lhs: IRId,
    obj: IRId,
    index: IRExpr
  ): IRExprStmt = {
    makeExprStmt(ast, lhs, makeLoad(fromSource, ast, obj, index))
  }

  def makeExprStmt(ast: ASTNode, lhs: IRId, right: IRExpr): IRExprStmt = {
    makeExprStmt(ast, lhs, right, false)
  }

  def makeLoad(
    fromSource: Boolean,
    ast: ASTNode,
    obj: IRId,
    index: IRExpr
  ): IRLoad = {
    NF.putIr(new IRLoad(ast, obj, index), ast)
  }

  def makeExprStmtIgnore(ast: ASTNode, lhs: IRId, right: IRExpr): IRExprStmt = {
    makeExprStmt(ast, lhs, right, true)
  }

  def makeExprStmt(
    ast: ASTNode,
    lhs: IRId,
    right: IRExpr,
    isRef: Boolean
  ): IRExprStmt = {
    NF.putIr(new IRExprStmt(ast, lhs, right, isRef), ast)
  } // ASTNode ast field of IRExprStmt was originally

  def makeReturn(fromSource: Boolean, ast: ASTNode, expr: Option[IRExpr]): IRReturn = {
    NF.putIr(new IRReturn(ast, expr), ast)
  }

  def makeObject(
    fromSource: Boolean,
    ast: ASTNode,
    lhs: IRId,
    members: List[IRMember],
    proto: IRId
  ): IRObject = {
    makeObject(fromSource, ast, lhs, members, Some(proto))
  }

  def makeObject(
    fromSource: Boolean,
    ast: ASTNode,
    lhs: IRId,
    members: List[IRMember]
  ): IRObject = {
    makeObject(fromSource, ast, lhs, members, None)
  }

  def makeObject(
    fromSource: Boolean,
    ast: ASTNode,
    lhs: IRId,
    members: List[IRMember],
    proto: Option[IRId]
  ): IRObject = {
    NF.putIr(new IRObject(ast, lhs, members, proto), ast)
  }

  def makeArray(
    fromSource: Boolean,
    ast: ASTNode,
    lhs: IRId,
    elements: List[Option[IRExpr]]
  ): IRArray = {
    NF.putIr(new IRArray(ast, lhs, elements), ast)
  }

  def makeArrayNumber(
    fromSource: Boolean,
    ast: ASTNode,
    span: Span,
    lhs: IRId,
    elements: List[JDouble]
  ): IRStmt = {
    NF.putIr(new IRArrayNumber(ast, lhs, elements), ast)
  }

  def makeArgs(ast: ASTNode, lhs: IRId, elements: List[Option[IRExpr]]): IRArgs = {
    NF.putIr(new IRArgs(ast, lhs, elements), ast)
  }

  def makeInternalCall(
    ast: ASTNode,
    lhs: IRId,
    fun: IRId,
    arg: IRExpr
  ): IRInternalCall = {
    makeInternalCall(ast, lhs, fun, arg, None)
  }

  def makeInternalCall(
    ast: ASTNode,
    lhs: IRId,
    fun: IRId,
    arg1: IRExpr,
    arg2: Option[IRId]
  ): IRInternalCall = {
    NF.putIr(new IRInternalCall(ast, lhs, fun.uniqueName, arg2.foldLeft(List(arg1))((l, arg2) => l :+ arg2)), ast)
  }

  def makeInternalCall(
    ast: ASTNode,
    lhs: IRId,
    fun: IRId,
    arg1: IRId,
    arg2: IRId
  ): IRInternalCall = {
    makeInternalCall(ast, lhs, fun, arg1, Some(arg2))
  }

  def makeCall(
    fromSource: Boolean,
    ast: ASTNode,
    lhs: IRId,
    fun: IRId,
    thisB: IRId,
    args: IRId
  ): IRCall = {
    NF.putIr(new IRCall(ast, lhs, fun, thisB, args), ast)
  }

  def makeNew(
    fromSource: Boolean,
    ast: ASTNode,
    lhs: IRId,
    fun: IRId,
    args: List[IRId]
  ): IRNew = {
    NF.putIr(new IRNew(ast, lhs, fun, args), ast)
  }

  def makeIf(
    fromSource: Boolean,
    ast: ASTNode,
    cond: IRExpr,
    trueB: IRStmt,
    falseB: Option[IRStmt]
  ): IRIf = {
    NF.putIr(new IRIf(ast, cond, trueB, falseB), ast)
  }

  def makeWhile(
    fromSource: Boolean,
    ast: ASTNode,
    cond: IRExpr,
    body: IRStmt,
    breakLabel: IRId,
    contLabel: IRId
  ): IRWhile = {
    NF.putIr(new IRWhile(ast, cond, body, breakLabel, contLabel), ast)
  }

  def makeTry(
    fromSource: Boolean,
    ast: ASTNode,
    body: IRStmt,
    name: Option[IRId],
    catchB: Option[IRStmt],
    finallyB: Option[IRStmt]
  ): IRTry = {
    NF.putIr(new IRTry(ast, body, name, catchB, finallyB), ast)
  }

  def makeStore(
    fromSource: Boolean,
    ast: ASTNode,
    obj: IRId,
    index: IRExpr,
    rhs: IRExpr
  ): IRStore = {
    NF.putIr(new IRStore(ast, obj, index, rhs), ast)
  }

  def makeSeq(ast: ASTNode, first: IRStmt, second: IRStmt): IRSeq = {
    makeSeq(ast, List(first, second))
  }

  def makeSeq(ast: ASTNode, stmts: List[IRStmt]): IRSeq = {
    NF.putIr(new IRSeq(ast, stmts), ast)
  }

  def makeSeq(ast: ASTNode): IRSeq = {
    makeSeq(ast, Nil)
  }

  def makeSeq(ast: ASTNode, stmt: IRStmt): IRSeq = {
    makeSeq(ast, List(stmt))
  }

  def makeStmtUnit(ast: ASTNode): IRStmtUnit = {
    makeStmtUnit(ast, Nil)
  }

  def makeStmtUnit(ast: ASTNode, stmt: IRStmt): IRStmtUnit = {
    makeStmtUnit(ast, List(stmt))
  }

  def makeStmtUnit(ast: ASTNode, stmts: List[IRStmt]): IRStmtUnit = {
    NF.putIr(new IRStmtUnit(ast, stmts), ast)
  }

  def makeStmtUnit(ast: ASTNode, first: IRStmt, second: IRStmt): IRStmtUnit = {
    makeStmtUnit(ast, List(first, second))
  }

  def makeField(
    fromSource: Boolean,
    ast: ASTNode,
    prop: IRId,
    expr: IRExpr
  ): IRField = {
    NF.putIr(new IRField(ast, prop, expr), ast)
  }

  def makeBoolIR(bool: Boolean): IRVal = {
    new IRVal(makeBool(bool))
  }

  def makeBool(bool: Boolean): EJSBool = {
    EJSBool(bool)
  }

  def makeNull(ast: ASTNode): IRVal = {
    new IRVal(EJSNull)
  }

  def makeUndef(ast: ASTNode): IRVal = {
    new IRVal(EJSUndef)
  }

  def makeNumberIR(text: String, num: Double): IRVal = {
    new IRVal(makeNumber(text, num))
  }

  ////////////////////////////////////////////////////////////////////////////////////////

  def makeNumber(text: String, num: Double): EJSNumber = {
    EJSNumber(text, num)
  }

  def makeStringIR(str: String): IRVal = {
    IRVal(makeString(str))
  }

  def makeString(str1: String): EJSString = {
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
    else EJSString(str1)
  }

  def makeThis(ast: ASTNode): IRThis = {
    NF.putIr(new IRThis(ast), ast)
  }

  // make a withRewriter-generated id
  def makeWId(
    originalName: String,
    uniqueName: String,
    isGlobal: Boolean,
    ast: ASTNode
  ): IRUserId = {
    makeUId(originalName, uniqueName, isGlobal, ast, true)
  }

  // make a non-global user id
  def makeNGId(uniqueName: String, ast: ASTNode): IRUserId = {
    makeUId(uniqueName, uniqueName, false, ast, false)
  }

  // make a user id
  def makeUId(
    originalName: String,
    uniqueName: String,
    isGlobal: Boolean,
    ast: ASTNode,
    isWith: Boolean
  ): IRUserId = {
    NF.putIr(new IRUserId(ast, originalName, uniqueName, isGlobal, isWith), ast)
  }

  def makeNGId(originalName: String, uniqueName: String, ast: ASTNode): IRUserId = {
    makeUId(originalName, uniqueName, false, ast, false)
  }

  // make a non-global temporary id
  def makeTId(info: ASTNodeInfo, uniqueName: String): IRTmpId = {
    makeTId(info, uniqueName, false)
  }

  def makeTId(info: ASTNodeInfo, uniqueName: String, isGlobal: Boolean): IRTmpId = {
    new IRTmpId(NF.makeDummyAST(info, uniqueName), uniqueName, uniqueName, isGlobal)
  }

}
