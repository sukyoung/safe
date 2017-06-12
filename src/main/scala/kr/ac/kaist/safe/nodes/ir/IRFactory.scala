package kr.ac.kaist.safe.nodes.ir

import _root_.java.lang.{Double => JDouble}
import _root_.java.util.{List => JList}
import kr.ac.kaist.safe.nodes.{NodeFactory => NF}
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.useful.Lists

object IRFactory {

  val dummyAST = NF.dummyAST
  // For use only when there is no hope of attaching a true span.
  def dummySpan(villain: String): Span = {
    val name = if (villain.length != 0) villain else "dummySpan"
    val sl = new SourceLoc(0,0,0)
    new Span(name, sl,sl)
  }
  def dummyIRId(name: String): IRId = makeTId(dummyAST, name)
  def dummyIRId(id: Id): IRId = {
    val name = id.text
    makeTId(dummyAST, name)
  }
  def dummyIRId(label: Label): IRId = {
    val name = label.id.text
    makeTId(dummyAST, name)
  }
  def dummyIRStmt(ast: ASTNode): IRSeq =
    makeSeq(ast, Nil.asInstanceOf[List[IRStmt]])
  def dummyIRExpr(): IRExpr = makeTId(dummyAST, "_")
  def dummyIRStmt(ast: ASTNode, msg: String): IRSeq =
    makeSeq(dummyAST, List(makeExprStmt(dummyAST, dummyIRId(msg), dummyIRExpr)))

  def makeFunctional(fromSource: Boolean, ast: Functional,
                     name: IRId, params: List[IRId], args: List[IRStmt],
                     fds: List[IRFunDecl], vds: List[IRVarStmt],
                     body: List[IRStmt]): IRFunctional =
    NF.putIr2ast(new IRFunctional(ast, fromSource, name, params, args, fds, vds, body), ast)

  def makeFunctional(fromSource: Boolean, ast: Functional,
                     name: IRId, params: List[IRId], body: IRStmt): IRFunctional =
    makeFunctional(fromSource, ast, name, params, Nil, Nil, Nil, List(body))

  def makeFunctional(fromSource: Boolean, ast: Functional, name: IRId, params: List[IRId],
                     body: List[IRStmt]): IRFunctional =
    makeFunctional(fromSource, ast, name, params, Nil, Nil, Nil, body)

  def makeFunExpr(fromSource: Boolean, ast: Functional, lhs: IRId, name: IRId,
                  params: List[IRId], body: IRStmt): IRFunExpr =
    makeFunExpr(fromSource, ast, lhs, name, params, Nil, Nil, Nil, List(body))

  def makeFunExpr(fromSource: Boolean, ast: Functional,
                  lhs: IRId, name: IRId, params: List[IRId], args: List[IRStmt],
                  fds: List[IRFunDecl], vds: List[IRVarStmt], body: List[IRStmt]): IRFunExpr =
    NF.putIr2ast(new IRFunExpr(ast, lhs, makeFunctional(fromSource, ast, name, params, args, fds, vds, body)), ast)

  def makeLoadStmt(fromSource: Boolean, ast: ASTNode, lhs: IRId, obj: IRId, index: IRExpr) =
    makeExprStmt(ast, lhs, makeLoad(fromSource, ast, obj, index))

  def makeExprStmt(ast: ASTNode,  lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, lhs, right, false)

  def makeExprStmtIgnore(ast: ASTNode, lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, lhs, right, true)

  def makeExprStmt(ast: ASTNode, lhs: IRId, right: IRExpr, isRef: Boolean): IRExprStmt =
    NF.putIr2ast(new IRExprStmt(ast, lhs, right, isRef), ast) // ASTNode ast field of IRExprStmt was originally
  // makeSpanInfo(false, span) with span passed as argument to this function

  def makeReturn(fromSource: Boolean, ast: ASTNode, expr: Option[IRExpr]) =
    NF.putIr2ast(new IRReturn(ast, expr), ast)

  def makeObject(fromSource: Boolean, ast: ASTNode,
                 lhs: IRId, members: List[IRMember], proto: IRId): IRObject =
    makeObject(fromSource, ast, lhs, members, Some(proto))

  def makeObject(fromSource: Boolean, ast: ASTNode, lhs: IRId, members: List[IRMember]): IRObject =
    makeObject(fromSource, ast, lhs, members, None)

  def makeObject(fromSource: Boolean, ast: ASTNode,
                 lhs: IRId, members: List[IRMember], proto: Option[IRId]): IRObject =
    NF.putIr2ast(new IRObject(ast, lhs, members, proto), ast)

  def makeArray(fromSource: Boolean, ast: ASTNode, lhs: IRId, elements: List[Option[IRExpr]]) : IRArray =
    NF.putIr2ast(new IRArray(ast, lhs, elements), ast)

  def makeArrayNumber(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, elements: List[JDouble]) : IRStmt =
    NF.putIr2ast(new IRArrayNumber(ast, lhs, elements), ast)

  def makeArgs(ast: ASTNode, lhs: IRId, elements: List[Option[IRExpr]]) : IRArgs =
    NF.putIr2ast(new IRArgs(ast, lhs, elements), ast)

  def makeLoad(fromSource: Boolean, ast: ASTNode, obj: IRId, index: IRExpr) =
    NF.putIr2ast(new IRLoad(ast, obj, index), ast)

  def makeInternalCall(ast: ASTNode, lhs: IRId, fun: IRId, arg: IRExpr) : IRInternalCall =
    makeInternalCall(ast, lhs, fun, arg, None)

  def makeInternalCall(ast: ASTNode, lhs: IRId, fun: IRId, arg1: IRId, arg2: IRId) : IRInternalCall =
    makeInternalCall(ast, lhs, fun, arg1, Some(arg2))

  def makeInternalCall(ast: ASTNode, lhs: IRId, fun: IRId, arg1: IRExpr, arg2: Option[IRId]) : IRInternalCall =
    NF.putIr2ast(new IRInternalCall(ast, lhs, fun.uniqueName, arg2.foldLeft(List(arg1))( (l, arg2) => l :+ arg2)), ast)

  def makeCall(fromSource: Boolean, ast: ASTNode, lhs: IRId, fun: IRId, thisB: IRId, args: IRId) : IRCall =
    NF.putIr2ast(new IRCall(ast, lhs, fun, thisB, args), ast)

  def makeNew(fromSource: Boolean, ast: ASTNode, lhs: IRId, fun: IRId, args: List[IRId]) : IRNew =
    NF.putIr2ast(new IRNew(ast, lhs, fun, args), ast)

  def makeIf(fromSource: Boolean, ast: ASTNode, cond: IRExpr, trueB: IRStmt, falseB: Option[IRStmt]) =
    NF.putIr2ast(new IRIf(ast, cond, trueB, falseB), ast)

  def makeWhile(fromSource: Boolean, ast: ASTNode, cond: IRExpr, body: IRStmt) =
    NF.putIr2ast(new IRWhile(ast, cond, body), ast)

  def makeTry(fromSource: Boolean, ast: ASTNode, body: IRStmt,
              name: Option[IRId], catchB: Option[IRStmt], finallyB:
  Option[IRStmt]) =
    NF.putIr2ast(new IRTry(ast, body, name, catchB, finallyB), ast)

  def makeStore(fromSource: Boolean, ast: ASTNode, obj: IRId, index: IRExpr, rhs: IRExpr) =
    NF.putIr2ast(new IRStore(ast, obj, index, rhs), ast)

  def makeSeq(ast: ASTNode, first: IRStmt, second: IRStmt): IRSeq =
    makeSeq(ast, List(first, second))

  def makeSeq(ast: ASTNode): IRSeq =
    makeSeq(ast, Nil)

  def makeSeq(ast: ASTNode, stmt: IRStmt): IRSeq =
    makeSeq(ast, List(stmt))

  def makeSeq(ast: ASTNode, stmts: List[IRStmt]): IRSeq =
    NF.putIr2ast(new IRSeq(ast, stmts), ast)

  def makeStmtUnit(ast: ASTNode): IRStmtUnit =
    makeStmtUnit(ast, Nil)

  def makeStmtUnit(ast: ASTNode, stmt: IRStmt): IRStmtUnit =
    makeStmtUnit(ast, List(stmt))

  def makeStmtUnit(ast: ASTNode, first: IRStmt, second: IRStmt): IRStmtUnit =
    makeStmtUnit(ast, List(first, second))

  def makeStmtUnit(ast: ASTNode, stmts: List[IRStmt]): IRStmtUnit =
    NF.putIr2ast(new IRStmtUnit(ast, stmts), ast)


  def makeField(fromSource: Boolean, ast: ASTNode, prop: IRId, expr: IRExpr) =
    NF.putIr2ast(new IRField(ast, prop, expr), ast)

  def makeBool(fromSource: Boolean, ast: ASTNode, bool: Boolean): IRVal =
    new IRVal(EJSBool(bool))
  val trueV = makeBool(false, dummyAST, true)
  val falseV = makeBool(false, dummyAST, false)
  def makeNull(ast: ASTNode) = new IRVal(EJSNull)
  def makeUndef(ast: ASTNode) = new IRVal(EJSUndef)

  def makeNumber(fromSource: Boolean, text: String, num: Double): IRVal =
    makeNumber(fromSource, dummyAST, text, num)
  def makeNumber(fromSource: Boolean, ast: ASTNode, text: String, num: Double): IRVal =
    new IRVal(EJSNumber(text, num))
  val oneV = makeNumber(false, "1", 1)

  val zero  = new IRVal(EJSString("0"))
  val one   = new IRVal(EJSString("1"))
  val two   = new IRVal(EJSString("2"))
  val three = new IRVal(EJSString("3"))
  val four  = new IRVal(EJSString("4"))
  val five  = new IRVal(EJSString("5"))
  val six   = new IRVal(EJSString("6"))
  val seven = new IRVal(EJSString("7"))
  val eight = new IRVal(EJSString("8"))
  val nine  = new IRVal(EJSString("9"))
  def makeString(str: String, ast: ASTNode): IRVal = makeString(false, ast, str)
  def makeString(fromSource: Boolean, ast: ASTNode, str1: String): IRVal = {
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
    else new IRVal(EJSString(str1))
  }
  ////////////////////////////////////////////////////////////////////////////////////////

  def makeThis(ast: ASTNode) =
    NF.putIr2ast(new IRThis(ast), ast)

  // make a user id
  def makeUId(originalName: String, uniqueName: String, isGlobal: Boolean,
              ast: ASTNode, isWith: Boolean): IRUserId =
    NF.putIr2ast(new IRUserId(ast, originalName, uniqueName, isGlobal, isWith), ast)

  // make a withRewriter-generated id
  def makeWId(originalName: String, uniqueName: String, isGlobal: Boolean,
              ast: ASTNode): IRUserId =
    makeUId(originalName, uniqueName, isGlobal, ast, true)

  // make a non-global user id
  def makeNGId(uniqueName: String, ast: ASTNode): IRUserId =
    makeUId(uniqueName, uniqueName, false, ast, false)

  def makeNGId(originalName: String, uniqueName: String, ast: ASTNode): IRUserId =
    makeUId(originalName, uniqueName, false, ast, false)

  // make a non-global temporary id
  def makeTId(info: ASTNodeInfo, uniqueName: String): IRTmpId =
    makeTId(info, uniqueName, false)

  def makeTId(info: ASTNodeInfo, uniqueName: String, isGlobal: Boolean): IRTmpId =
    new IRTmpId(NF.makeDummyAST(info, uniqueName), uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, uniqueName: String): IRTmpId =
    makeTId(ast, uniqueName, false)

  // make a temporary id
  def makeTId(ast: ASTNode, uniqueName: String, isGlobal: Boolean): IRTmpId =
    makeTId(ast, uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, originalName: String, uniqueName: String, isGlobal: Boolean): IRTmpId =
    NF.putIr2ast(new IRTmpId(ast, originalName, uniqueName, isGlobal), ast)

}
