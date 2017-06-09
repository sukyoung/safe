package kr.ac.kaist.safe.nodes

import java.math.BigInteger
import scala.collection.mutable.{HashMap => MHashMap}

import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{NodeUtil => NU, SourceLoc, Span}

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
      case ir: IRAbstractNode => irinfo2irMap.put(ir.getInfo.getUID, ir)
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

  def makeExprStmt(span: Span, expr: Expr) =
    new ExprStmt(NU.makeSpanInfoComment(span), expr, false)

  def makeAssignOpApp(span: Span, lhs: LHS, op: Op, right: Expr) =
    new AssignOpApp(NU.makeSpanInfoComment(span), lhs, op, right)

  def makeDot(span: Span, lhs: LHS, member: Id) =
    new Dot(NU.makeSpanInfoComment(span), lhs, member)

  def makeNew(span: Span, lhs: LHS) =
    new New(NU.makeSpanInfoComment(span), lhs)

  def makeFunApp(span: Span, lhs: LHS, args: List[Expr]) =
    new FunApp(NU.makeSpanInfoComment(span), lhs, args)

  def makeVarRef(span: Span, id: Id) =
    new VarRef(NU.makeSpanInfoComment(span), id)

  def makeIntLiteral(span: Span, intVal: BigInteger, radix: Int = 10) =
    new IntLiteral(NU.makeSpanInfoComment(span), intVal, radix)

  def makeId(span: Span, name: String, uniq: String): Id =
    makeId(span, name, Some(uniq))

  def makeId(span: Span, name: String): Id =
    makeId(span, name, None)

  def makeId(span: Span, name: String, uniq: Option[String]): Id =
    new Id(NU.makeSpanInfoComment(span), name, uniq, false)

  def makeOp(span: Span, name: String) =
    new Op(makeSpanInfo(span), name)

  def makeComment(span: Span, comment: String): Comment =
    new Comment(NU.makeSpanInfoComment(span), comment)

  def makeNoOp(span: Span, desc: String): NoOp =
    makeNoOp(NU.makeSpanInfoComment(span), desc)

  def makeNoOp(info: ASTNodeInfo, desc: String): NoOp =
    new NoOp(info, desc)

}
