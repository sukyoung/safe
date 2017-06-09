package kr.ac.kaist.safe.nodes.ir

import kr.ac.kaist.safe.nodes.{NodeFactory => NF}
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{SourceLoc, Span}
import kr.ac.kaist.safe.util.useful.Lists

object IRFactory {

  // For use only when there is no hope of attaching a true span.
  def dummySpan(villain: String): Span = {
    val name = if (villain.length != 0) villain else "dummySpan"
    val sl = new SourceLoc(0,0,0)
    new Span(name, sl,sl)
  }
  def makeInfo(span: Span): IRSpanInfo = new IRSpanInfo(false, span)
  def dummyIRId(name: String): IRId = makeTId(NF.dummyAst, name)
  def dummyIRId(id: Id): IRId = {
    val name = id.text
    makeTId(NF.dummyAst, name)
  }
  def dummyIRId(label: Label): IRId = {
    val name = label.id.text
    makeTId(NF.dummyAst, name)
  }
  def dummyIRId(label: Label): IRId = {
    val name = label.id.text
    makeTId(NF.dummyAst, name)
  }
  def dummyIRStmt(ast: ASTNode): IRSeq =
    makeSeq(ast, Nil.asInstanceOf[List[IRStmt]])
  def dummyIRExpr(): IRExpr = makeTId(NF.dummyAst, "_")
  def dummyIRStmt(ast: ASTNode, msg: String): IRSeq =
    makeSeq(NF.dummyAst, List(makeExprStmt(NF.dummyAst, dummyIRId(msg), dummyIRExpr)))

  def makeSeq(ast: ASTNode, first: IRStmt, second: IRStmt): IRSeq =
    makeSeq(ast, List(first, second))

  def makeSeq(ast: ASTNode): IRSeq =
    makeSeq(ast, Nil)

  def makeSeq(ast: ASTNode, stmt: IRStmt): IRSeq =
    makeSeq(ast, List(stmt))

  def makeSeq(ast: ASTNode, stmts: List[IRStmt]): IRSeq =
    NF.putIr2ast(new IRSeq(ast, Lists.toJavaList(stmts)), ast)

  // make a non-global temporary id
  def makeTId(ast: ASTNode, uniqueName: String): IRTmpId =
    makeTId(ast, uniqueName, false)

  // make a temporary id
  def makeTId(ast: ASTNode, uniqueName: String, isGlobal: Boolean): IRTmpId =
    makeTId(ast, uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, originalName: String, uniqueName: String, isGlobal: Boolean): IRTmpId =
    makeTId(ast, originalName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, originalName: String, uniqueName: String,
              isGlobal: Boolean): IRTmpId =
    NF.putIr2ast(new IRTmpId(ast, originalName, uniqueName, isGlobal), ast)

}
