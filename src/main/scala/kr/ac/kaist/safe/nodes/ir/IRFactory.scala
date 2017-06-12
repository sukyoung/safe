package kr.ac.kaist.safe.nodes.ir

import kr.ac.kaist.safe.nodes.{NodeFactory => NF}
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.useful.Lists

object IRFactory {

  val dummyAst = NF.makeNoOp(NF.makeSpanInfo(NF.makeSpan("dummyAST")), "dummyAST")
  // For use only when there is no hope of attaching a true span.
  def dummySpan(villain: String): Span = {
    val name = if (villain.length != 0) villain else "dummySpan"
    val sl = new SourceLoc(0,0,0)
    new Span(name, sl,sl)
  }
  def dummyIRId(name: String): IRId = makeTId(dummyAst, name)
  def dummyIRId(id: Id): IRId = {
    val name = id.text
    makeTId(dummyAst, name)
  }
  def dummyIRId(label: Label): IRId = {
    val name = label.id.text
    makeTId(dummyAst, name)
  }
  def dummyIRStmt(ast: ASTNode): IRSeq =
    makeSeq(ast, Nil.asInstanceOf[List[IRStmt]])
  def dummyIRExpr(): IRExpr = makeTId(dummyAst, "_")
  def dummyIRStmt(ast: ASTNode, msg: String): IRSeq =
    makeSeq(dummyAst, List(makeExprStmt(dummyAst, dummyIRId(msg), dummyIRExpr)))

  def makeExprStmt(ast: ASTNode,  lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, lhs, right, false)

  def makeExprStmtIgnore(ast: ASTNode, lhs: IRId, right: IRExpr): IRExprStmt =
    makeExprStmt(ast, lhs, right, true)

  def makeExprStmt(ast: ASTNode, lhs: IRId, right: IRExpr, isRef: Boolean): IRExprStmt =
    NF.putIr2ast(new IRExprStmt(ast, lhs, right, isRef), ast) // ASTNode ast field of IRExprStmt was originally
  // makeSpanInfo(false, span) with span passed as argument to this function



  def makeInternalCall(ast: ASTNode, lhs: IRId, fun: IRId, arg: IRExpr) : IRInternalCall =
    makeInternalCall(ast, lhs, fun, arg, None)

  def makeInternalCall(ast: ASTNode, lhs: IRId, fun: IRId, arg1: IRId, arg2: IRId) : IRInternalCall =
    makeInternalCall(ast, lhs, fun, arg1, Some(arg2))

  def makeInternalCall(ast: ASTNode, lhs: IRId, fun: IRId, arg1: IRExpr, arg2: Option[IRId]) : IRInternalCall =
    NF.putIr2ast(new IRInternalCall(ast, lhs, fun.uniqueName, arg2.foldLeft(List(arg1))( (l, arg2) => l :+ arg2)), ast)


  def makeBool(fromSource: Boolean, ast: ASTNode, bool: Boolean): IRVal =
    new IRVal(EJSBool(bool))
  val trueV = makeBool(false, dummyAst, true)
  val falseV = makeBool(false, dummyAst, false)
  def makeNull(ast: ASTNode) = new IRVal(EJSNull)
  def makeUndef(ast: ASTNode) = new IRVal(EJSUndef)

  def makeSeq(ast: ASTNode, first: IRStmt, second: IRStmt): IRSeq =
    makeSeq(ast, List(first, second))

  def makeSeq(ast: ASTNode): IRSeq =
    makeSeq(ast, Nil)

  def makeSeq(ast: ASTNode, stmt: IRStmt): IRSeq =
    makeSeq(ast, List(stmt))

  def makeSeq(ast: ASTNode, stmts: List[IRStmt]): IRSeq =
    NF.putIr2ast(new IRSeq(ast, stmts), ast)

  def makeNumber(fromSource: Boolean, text: String, num: Double): IRVal =
    makeNumber(fromSource, dummyAst, text, num)
  def makeNumber(fromSource: Boolean, ast: ASTNode, text: String, num: Double): IRVal =
    IRVal(EJSNumber(text, num))


  // make a non-global temporary id
  def makeTId(ast: ASTNode, uniqueName: String): IRTmpId =
    makeTId(ast, uniqueName, false)

  // make a temporary id
  def makeTId(ast: ASTNode, uniqueName: String, isGlobal: Boolean): IRTmpId =
    makeTId(ast, uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, originalName: String, uniqueName: String,
              isGlobal: Boolean): IRTmpId =
    NF.putIr2ast(new IRTmpId(ast, originalName, uniqueName, isGlobal), ast)

}
