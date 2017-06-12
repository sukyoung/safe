package kr.ac.kaist.safe.nodes

import _root_.java.util.{List => JList}
import _root_.java.math.BigInteger
import scala.collection.mutable.{HashMap => MHashMap}

import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{NodeUtil => NU, SourceLoc, Span}
import kr.ac.kaist.safe.util.useful.Lists

object NodeFactory {

  val dummyAst = makeNoOp(makeSpanInfo(makeSpan("dummyAST")), "dummyAST")

  // Maps the unique ids for IR nodes to their corresponding AST nodes
  private var ir2astMap = new MHashMap[Long, ASTNode] // IRNode.uid -> ASTNode
  private var irinfo2irMap = new MHashMap[Long, IRNode] // IRInfoNode.uid -> IRNode
  def initIr2ast: Unit = {ir2astMap = new MHashMap; irinfo2irMap = new MHashMap}
  def ir2ast(ir: IRNode): Option[ASTNode] = ir2astMap.get(ir.asInstanceOf[UIDObject].getUID)
  def irinfo2ir(info: IRInfoNode): Option[IRNode] = irinfo2irMap.get(info.getUID)
  def putIr2ast[A <: IRNode](ir: A, ast: ASTNode): A = {
    ir2astMap.put(ir.asInstanceOf[UIDObject].getUID, ast)
    ir match {
      case ir: IRAbstractNode => irinfo2irMap.put(ir.info.getUID, ir)
      case ir: IRExpr => irinfo2irMap.put(ir.getInfo.getUID, ir)
      case ir: IRInfoNode => irinfo2irMap.put(ir.getUID, ir)
      case _ =>
    }
    ir
  }

  def makeSpanInfo(span: Span, comment: String): ASTNodeInfo =
    new ASTNodeInfo(span, Some[Comment](makeComment(span, comment)))
  def makeSpanInfo(span: Span): ASTNodeInfo =
    new ASTNodeInfo(span, None[Comment])

  // For use only when there is no hope of attaching a true span.
  def makeSpan(villain: String): Span = {
    val sl = new SourceLoc(0,0,0)
    new Span(villain, sl,sl)
  }



  def makeEmptyFunctional(info: ASTNodeInfo, name: Id, fds: List[FunDecl], vds: List[VarDecl],
                          body: List[SourceElement], params: List[Id], strict: Boolean) =
    new Functional(info, fds, vds, new SourceElements(info, body, strict), name, params, "")

  def makeFunctional(info: ASTNodeInfo, name: Id, fds: List[FunDecl], vds: List[VarDecl],
                     body: List[SourceElement], params: List[Id], strict: Boolean, bodyS: String) =
    new Functional(info,fds, vds, new SourceElements(info, body, strict), name, params, bodyS)

  def makeFunDecl(span: Span, name: Id, params: JList[Id],
                  body: JList[SourceElement], strict: Boolean) = {
    val info = NU.makeASTNodeInfo(span)
    val functional = makeEmptyFunctional(info, name, Nil, Nil, Lists.toList(body), Lists.toList(params), strict)
    new FunDecl(info, functional, strict)
  }

  def makeFunExpr(span: Span, name: Id, params: List[Id], body: List[SourceElement], strict: Boolean) = {
    val info = NU.makeASTNodeInfo(span)
    new FunExpr(info, makeEmptyFunctional(info, name, Nil, Nil, body, params, strict))
  }

  def makeExprStmt(span: Span, expr: Expr) =
    new ExprStmt(NU.makeASTNodeInfo(span), expr, false)

  def makeAssignOpApp(span: Span, lhs: LHS, op: Op, right: Expr) =
    new AssignOpApp(NU.makeASTNodeInfo(span), lhs, op, right)

  def makeBracket(span: Span, lhs: LHS, index: Expr) =
    new Bracket(NU.makeASTNodeInfo(span), lhs, index)

  def makeDot(span: Span, lhs: LHS, member: Id) =
    new Dot(NU.makeASTNodeInfo(span), lhs, member)

  def makeNew(span: Span, lhs: LHS) =
    new New(NU.makeASTNodeInfo(span), lhs)

  def makeFunApp(span: Span, lhs: LHS, args: List[Expr]) =
    new FunApp(NU.makeASTNodeInfo(span), lhs, args)

  def makeNull(span: Span) =
    new Null(NU.makeASTNodeInfo(span))

  def makeVarRef(span: Span, id: Id) =
    new VarRef(NU.makeASTNodeInfo(span), id)

  def makeIntLiteral(span: Span, intVal: BigInteger, radix: Int = 10) =
    new IntLiteral(NU.makeASTNodeInfo(span), intVal, radix)

  def makeId(span: Span, name: String, uniq: String): Id =
    makeId(span, name, Some(uniq))

  def makeId(span: Span, name: String): Id =
    makeId(span, name, None)

  def makeId(span: Span, name: String, uniq: Option[String]): Id =
    new Id(NU.makeASTNodeInfo(span), name, uniq, false)

  def makeOp(span: Span, name: String) =
    new Op(makeSpanInfo(span), name)

  def makeComment(span: Span, comment: String): Comment =
    new Comment(NU.makeASTNodeInfo(span), comment)

  def makeNoOp(span: Span, desc: String): NoOp =
    makeNoOp(NU.makeASTNodeInfo(span), desc)

  def makeNoOp(info: ASTNodeInfo, desc: String): NoOp =
    new NoOp(info, desc)

}
