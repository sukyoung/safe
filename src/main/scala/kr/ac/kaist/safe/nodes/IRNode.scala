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

/**
 * *************************
 * JavaScript IR
 * ECMAScript 5
 * *************************
 */

package kr.ac.kaist.safe.nodes

import kr.ac.kaist.safe.config.Config
import java.lang.Double
import java.math.BigInteger
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span, EJSOp, SourceLoc }

abstract class IRNode(val ast: ASTNode) extends Node {
  def span: Span = ast.span
  def fileName: String = ast.fileName
  def begin: SourceLoc = ast.begin
  def end: SourceLoc = ast.end
  def line: Int = ast.line
  def offset: Int = ast.offset
}

/**
 * IRRoot ::= Statement*
 */
case class IRRoot(override val ast: ASTNode, val fds: List[IRFunDecl], val vds: List[IRVarStmt], val irs: List[IRStmt])
    extends IRNode(ast) {
  override def toString(indent: Int): String = {
    NU.initNodesPrint
    val s: StringBuilder = new StringBuilder
    val indentString = NU.getIndent(indent)
    s.append(indentString).append(NU.join(indent, fds, Config.LINE_SEP + indentString, new StringBuilder("")))
    s.append(Config.LINE_SEP)
    s.append(indentString).append(NU.join(indent, vds, Config.LINE_SEP + indentString, new StringBuilder("")))
    s.append(Config.LINE_SEP)
    s.append(indentString).append(NU.join(indent, irs, Config.LINE_SEP + indentString, new StringBuilder("")))
    s.toString
  }
}
/**
 * Statement
 */
abstract class IRStmt(override val ast: ASTNode)
  extends IRNode(ast)
abstract class IRAssign(override val ast: ASTNode, val lhs: IRId)
  extends IRStmt(ast)

/**
 * Expression
 * Stmt ::= x = e
 */
case class IRExprStmt(override val ast: ASTNode, override val lhs: IRId, val right: IRExpr, val ref: Boolean = false)
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ").append(right.toString(indent))
    s.toString
  }
}

/**
 * Delete expression
 * Stmt ::= x = delete y
 */
case class IRDelete(override val ast: ASTNode, override val lhs: IRId, val id: IRId)
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = delete ").append(id.toString(indent))
    s.toString
  }
}

/**
 * Delete property expression
 * Stmt ::= x = delete y[e]
 */
case class IRDeleteProp(override val ast: ASTNode, override val lhs: IRId, val obj: IRId, val index: IRExpr)
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = delete ")
    s.append(obj.toString(indent)).append("[").append(index.toString(indent)).append("]")
    s.toString
  }
}

/**
 * Object literal
 * Stmt ::= x = { member, ... }
 */
case class IRObject(override val ast: ASTNode, override val lhs: IRId, val members: List[IRMember], val proto: Option[IRId])
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = {").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent, members, "," + Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    if (proto.isDefined) {
      s.append("[[Prototype]]=").append(proto.get.toString(indent + 1))
    }
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * Array literal
 * Stmt ::= x = [ e, ... ]
 */
case class IRArray(override val ast: ASTNode, override val lhs: IRId, val elements: List[Option[IRExpr]])
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    elements.foreach(e => s.append(if (e.isDefined) e.get.toString(indent) else "").append(", "))
    s.append("]")
    s.toString
  }
}

/**
 * Array literal with numbers
 * Stmt ::= x = [ n, ... ]
 */
case class IRArrayNumber(override val ast: ASTNode, override val lhs: IRId, val elements: List[Double])
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    s.append("A LOT!!! " + elements.size + " elements are not printed here.")
    s.append("]")
    s.toString
  }
}

/**
 * Arguments
 * Stmt ::= x = [ e, ... ]
 */
case class IRArgs(override val ast: ASTNode, override val lhs: IRId, val elements: List[Option[IRExpr]])
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    elements.foreach(e => s.append(if (e.isDefined) e.get.toString(indent) else "").append(", "))
    s.append("]")
    s.toString
  }
}

/**
 * Call
 * Stmt ::= x = f(this, arguments)
 */
case class IRCall(override val ast: ASTNode, override val lhs: IRId, val fun: IRId, val thisB: IRId, val args: IRId)
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append(fun.toString(indent)).append("(")
    s.append(thisB.toString(indent))
    s.append(", ").append(args.toString(indent))
    s.append(")")
    s.toString
  }
}

/**
 * Internal function call
 * toObject, toString, toNubmer, isObject, getBase,
 * iteratorInit, iteratorHasNext, iteratorKey
 */
case class IRInternalCall(override val ast: ASTNode, override val lhs: IRId, val fun: IRId, val first: IRExpr, val second: Option[IRId])
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append(fun.toString(indent)).append("(")
    s.append(first.toString(indent))
    if (second.isDefined) s.append(", ").append(second.get.toString(indent))
    s.append(")")
    s.toString
  }
}

/**
 * New
 * Stmt ::= x = new f(x, ...)
 */
case class IRNew(override val ast: ASTNode, override val lhs: IRId, val fun: IRId, val args: List[IRId])
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = new ")
    s.append(fun.toString(indent)).append("(")
    s.append(NU.join(indent, args, ", ", new StringBuilder("")))
    s.append(")")
    s.toString
  }
}

/**
 * Function expression
 * Stmt ::= x = function f (this, arguments) { s }
 */
case class IRFunExpr(override val ast: ASTNode, override val lhs: IRId, val fun: IRFunctional)
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ").append("function ")
    s.append(fun.toString(indent))
    s.toString
  }
}

/**
 * Eval
 * Stmt ::= x = eval(e)
 */
case class IREval(override val ast: ASTNode, override val lhs: IRId, val arg: IRExpr)
    extends IRAssign(ast, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = eval(").append(arg.toString(indent)).append(")")
    s.toString
  }
}

/**
 * AST statement unit
 * Stmt ::= s
 */
case class IRStmtUnit(override val ast: ASTNode, val stmts: List[IRStmt])
    extends IRStmt(ast) {
  override def toString(indent: Int): String = stmts match {
    case List(stmt) => stmt.toString(indent)
    case _ =>
      val s: StringBuilder = new StringBuilder
      s.append("{").append(Config.LINE_SEP)
      s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, stmts, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
      s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
      s.toString
  }
}
object IRStmtUnit {
  def apply(ast: ASTNode, stmts: IRStmt*): IRStmtUnit = IRStmtUnit(ast, stmts.toList)
}

/**
 * Store
 * Stmt ::= x[e] = e
 */
case class IRStore(override val ast: ASTNode, val obj: IRId, val index: IRExpr, val rhs: IRExpr)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(obj.toString(indent)).append("[").append(index.toString(indent))
    s.append("] = ").append(rhs.toString(indent))
    s.toString
  }
}

/**
 * Function declaration
 * Stmt ::= function f (this, arguments) { s }
 */
case class IRFunDecl(override val ast: ASTNode, val ftn: IRFunctional)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("function ")
    s.append(ftn.toString(indent))
    s.toString
  }
}

/**
 * Break
 * Stmt ::= break label
 */
case class IRBreak(override val ast: ASTNode, val label: IRId)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("break ") append (label.toString(indent))
    s.toString
  }
}

/**
 * Return
 * Stmt ::= return e?
 */
case class IRReturn(override val ast: ASTNode, val expr: Option[IRExpr])
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("return")
    if (expr.isDefined) s.append(" ").append(expr.get.toString(indent))
    s.toString
  }
}

/**
 * With
 * Stmt ::= with ( x ) s
 */
case class IRWith(override val ast: ASTNode, val id: IRId, val stmt: IRStmt)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("with(")
    s.append(id.toString(indent)).append(")").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent)).append(stmt.toString(indent))
    s.toString
  }
}

/**
 * Label
 * Stmt ::= l : { s }
 */
case class IRLabelStmt(override val ast: ASTNode, val label: IRId, val stmt: IRStmt)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(label.toString(indent)).append(" : ").append(stmt.toString(indent))
    s.toString
  }
}

/**
 * Var
 * Stmt ::= var x
 */
case class IRVarStmt(override val ast: ASTNode, val lhs: IRId, val fromParam: Boolean)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("var ").append(lhs.toString(indent))
    s.toString
  }
}

/**
 * Throw
 * Stmt ::= throw e
 */
case class IRThrow(override val ast: ASTNode, val expr: IRExpr)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("throw ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Sequence
 * Stmt ::= s; ...
 */
case class IRSeq(override val ast: ASTNode, val stmts: List[IRStmt])
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("{").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, stmts, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}
object IRSeq {
  def apply(ast: ASTNode, stmts: IRStmt*): IRSeq = IRSeq(ast, stmts.toList)
}

/**
 * If
 * Stmt ::= if (e) then s (else s)?
 */
case class IRIf(override val ast: ASTNode, val expr: IRExpr, val trueB: IRStmt, val falseB: Option[IRStmt])
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("if(").append(expr.toString(indent)).append(")").append(Config.LINE_SEP)
    NU.inlineIndent(trueB, s, indent)
    falseB match {
      case Some(f) =>
        s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("else").append(Config.LINE_SEP)
        NU.inlineIndent(f, s, indent)
      case None =>
    }
    s.toString
  }
}

/**
 * While
 * Stmt ::= while (e) s
 */
case class IRWhile(override val ast: ASTNode, val cond: IRExpr, val body: IRStmt)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("while(")
    s.append(cond.toString(indent)).append(")").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent)).append(body.toString(indent))
    s.toString
  }
}

/**
 * Try
 * Stmt ::= try { s } (catch (x) { s })? (finally { s })?
 */
case class IRTry(override val ast: ASTNode, val body: IRStmt, val name: Option[IRId], val catchB: Option[IRStmt], finallyB: Option[IRStmt])
    extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("try").append(Config.LINE_SEP)
    NU.inlineIndent(body, s, indent)
    catchB match {
      case Some(cb) =>
        s.append(Config.LINE_SEP).append(NU.getIndent(indent))
        s.append("catch(").append(name.get.toString(indent)).append(")").append(Config.LINE_SEP)
        NU.inlineIndent(cb, s, indent)
      case None =>
    }
    finallyB match {
      case Some(f) =>
        s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("finally").append(Config.LINE_SEP)
        NU.inlineIndent(f, s, indent)
      case None =>
    }
    s.toString
  }
}

/**
 * No operation
 */
case class IRNoOp(override val ast: ASTNode, val desc: String)
    extends IRStmt(ast) {
  override def toString(indent: Int): String = ""
}

/**
 * Member
 */
abstract class IRMember(override val ast: ASTNode)
  extends IRNode(ast)
/**
 * Member ::= x : e
 */
case class IRField(override val ast: ASTNode, val prop: IRId, val expr: IRExpr)
    extends IRMember(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(if (prop.ast.isInstanceOf[PropStr]) prop.toPropName(indent) else prop.toString(indent))
    s.append(" : ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Member ::= get x () { s }
 */
case class IRGetProp(override val ast: ASTNode, val ftn: IRFunctional)
    extends IRMember(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("get ").append(ftn)
    s.toString
  }
}

/**
 * Member ::= set x ( y ) { s }
 */
case class IRSetProp(override val ast: ASTNode, val ftn: IRFunctional)
    extends IRMember(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("set ").append(ftn)
    s.toString
  }
}

/**
 * Expression
 */
abstract class IRExpr(override val ast: ASTNode)
  extends IRNode(ast)
/**
 * Side-effect free expressions
 */
abstract class IROpApp(override val ast: ASTNode)
  extends IRExpr(ast)
/**
 * Binary expression
 * Expr ::= e binop e
 */
case class IRBin(override val ast: ASTNode, val first: IRExpr, val op: IROp, val second: IRExpr)
    extends IROpApp(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(first.toString(indent)).append(" ")
    s.append(op.toString(indent)).append(" ")
    s.append(second.toString(indent))
    s.toString
  }
}

/**
 * Unary expression
 * Expr ::= unop e
 */
case class IRUn(override val ast: ASTNode, val op: IROp, val expr: IRExpr)
    extends IROpApp(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(op.toString(indent)).append(" ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Load
 * Expr ::= x[e]
 */
case class IRLoad(override val ast: ASTNode, val obj: IRId, val index: IRExpr)
    extends IROpApp(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(obj.toString(indent)).append("[").append(index.toString(indent)).append("]")
    s.toString
  }
}

/**
 * Variable
 */
abstract class IRId(override val ast: ASTNode, val originalName: String, val uniqueName: String, val global: Boolean)
    extends IRExpr(ast) {
  override def toString(indent: Int): String = {
    val size = Config.SIGNIFICANT_BITS
    val str = if (!NU.isInternal(uniqueName)) uniqueName
    else if (!NU.isGlobalName(uniqueName)) uniqueName.dropRight(size) + NU.getNodesE(uniqueName.takeRight(size))
    else uniqueName
    val s: StringBuilder = new StringBuilder
    s.append(NU.pp(str))
    s.toString
  }

  // When this IRId is a property string name, use toPropName instead of toString
  def toPropName(indent: Int): String = {
    val size = Config.SIGNIFICANT_BITS
    val str = if (!NU.isInternal(uniqueName)) uniqueName
    else if (!NU.isGlobalName(uniqueName)) uniqueName.dropRight(size) + NU.getNodesE(uniqueName.takeRight(size))
    else uniqueName
    val s: StringBuilder = new StringBuilder
    s.append("\"")
    s.append(NU.pp(str.replaceAll("\\\\", "\\\\\\\\")))
    s.append("\"")
    s.toString
  }
}

/**
 * Variable
 * Expr ::= x
 */
case class IRUserId(override val ast: ASTNode, override val originalName: String, override val uniqueName: String, override val global: Boolean, val isWith: Boolean)
  extends IRId(ast, originalName, uniqueName, global)

/**
 * Internally generated identifiers by Translator
 * Do not appear in the JavaScript source text.
 */
case class IRTmpId(override val ast: ASTNode, override val originalName: String, override val uniqueName: String, override val global: Boolean = false)
  extends IRId(ast, originalName, uniqueName, global)

/**
 * this
 */
case class IRThis(override val ast: ASTNode)
    extends IRExpr(ast) {
  override def toString(indent: Int): String = "this"
}

/**
 * Value
 */
abstract class IRVal(override val ast: ASTNode)
  extends IRExpr(ast)

/**
 * Primitive value
 */
abstract class IRPVal(override val ast: ASTNode)
  extends IRExpr(ast)

/**
 * PVal ::= number literal
 */
case class IRNumber(override val ast: ASTNode, val text: String, val num: Double)
    extends IRVal(ast) {
  override def toString(indent: Int): String = text
}

/**
 * PVal ::= String
 */
case class IRString(override val ast: ASTNode, val str: String)
    extends IRVal(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("\"")
    s.append(NU.pp(str.replaceAll("\\\\", "\\\\\\\\")))
    s.append("\"")
    s.toString
  }
}

/**
 * PVal ::= true | false
 */
case class IRBool(override val ast: ASTNode, val bool: Boolean)
    extends IRVal(ast) {
  override def toString(indent: Int): String = if (bool) "true" else "false"
}

/**
 * PVal ::= undefined
 */
case class IRUndef(override val ast: ASTNode)
    extends IRVal(ast) {
  override def toString(indent: Int): String = "undefined"
}

/**
 * PVal ::= null
 */
case class IRNull(override val ast: ASTNode)
    extends IRVal(ast) {
  override def toString(indent: Int): String = "null"
}

/**
 * Operator
 */
case class IROp(override val ast: ASTNode, val kind: EJSOp)
    extends IRNode(ast) {
  override def toString(indent: Int): String = name
  val name: String = kind.name
}

/**
 * Common shape for functions
 */
case class IRFunctional(override val ast: ASTNode, val fromSource: Boolean,
  val name: IRId, val params: List[IRId], val args: List[IRStmt],
  val fds: List[IRFunDecl], val vds: List[IRVarStmt], val body: List[IRStmt])
    extends IRNode(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(name.toString(indent)).append("(")
    s.append(NU.join(indent, params, ", ", new StringBuilder("")))
    s.append(") ").append(Config.LINE_SEP).append(NU.getIndent(indent)).append("{").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1))
    s.append(NU.join(indent + 1, fds ++ vds ++ args ++ body, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

trait IRWalker {
  def walk(ast: ASTNode): ASTNode = ast

  def walk(node: IRRoot): IRRoot = node match {
    case IRRoot(ast, fds, vds, irs) =>
      IRRoot(walk(ast), fds.map(walk), vds.map(walk), irs.map(walk))
  }

  def walk(node: IRStmt): IRStmt = node match {
    case IRExprStmt(ast, lhs, right, isRef) =>
      IRExprStmt(walk(ast), walk(lhs), walk(right), isRef)
    case IRDelete(ast, lhs, id) =>
      IRDelete(walk(ast), walk(lhs), walk(id))
    case IRDeleteProp(ast, lhs, obj, index) =>
      IRDeleteProp(walk(ast), walk(lhs), walk(obj), walk(index))
    case IRObject(ast, lhs, members, proto) =>
      IRObject(walk(ast), walk(lhs), members.map(walk), proto.map(walk))
    case IRArray(ast, lhs, elements) =>
      IRArray(walk(ast), walk(lhs), elements.map(_.map(walk)))
    case IRArrayNumber(ast, lhs, elements) =>
      IRArrayNumber(walk(ast), walk(lhs), elements)
    case IRArgs(ast, lhs, elements) =>
      IRArgs(walk(ast), walk(lhs), elements.map(_.map(walk)))
    case IRCall(ast, lhs, fun, thisB, args) =>
      IRCall(walk(ast), walk(lhs), walk(fun), walk(thisB), walk(args))
    case IRInternalCall(ast, lhs, fun, first, second) =>
      IRInternalCall(walk(ast), walk(lhs), walk(fun), walk(first), second.map(walk))
    case IRNew(ast, lhs, fun, args) =>
      IRNew(walk(ast), walk(lhs), walk(fun), args.map(walk))
    case IRFunExpr(ast, lhs, ftn) =>
      IRFunExpr(walk(ast), walk(lhs), walk(ftn))
    case IREval(ast, lhs, arg) =>
      IREval(walk(ast), walk(lhs), walk(arg))
    case IRStmtUnit(ast, stmts) =>
      IRStmtUnit(walk(ast), stmts.map(walk))
    case IRStore(ast, obj, index, rhs) =>
      IRStore(walk(ast), walk(obj), walk(index), walk(rhs))
    case fd: IRFunDecl =>
      walk(fd)
    case IRBreak(ast, label) =>
      IRBreak(walk(ast), walk(label))
    case IRReturn(ast, expr) =>
      IRReturn(walk(ast), expr.map(walk))
    case IRWith(ast, id, stmt) =>
      IRWith(walk(ast), walk(id), walk(stmt))
    case IRLabelStmt(ast, label, stmt) =>
      IRLabelStmt(walk(ast), walk(label), walk(stmt))
    case vs: IRVarStmt =>
      walk(vs)
    case IRThrow(ast, expr) =>
      IRThrow(walk(ast), walk(expr))
    case IRSeq(ast, stmts) =>
      IRSeq(walk(ast), stmts.map(walk))
    case IRIf(ast, expr, trueB, falseB) =>
      IRIf(walk(ast), walk(expr), walk(trueB), falseB.map(walk))
    case IRWhile(ast, cond, body) =>
      IRWhile(walk(ast), walk(cond), walk(body))
    case IRTry(ast, body, name, catchB, finallyB) =>
      IRTry(walk(ast), walk(body), name.map(walk), catchB.map(walk), finallyB.map(walk))
    case IRNoOp(ast, desc) =>
      IRNoOp(walk(ast), desc)
  }

  def walk(node: IRExpr): IRExpr = node match {
    case IRBin(ast, first, op, second) =>
      IRBin(walk(ast), walk(first), walk(op), walk(second))
    case IRUn(ast, op, expr) =>
      IRUn(walk(ast), walk(op), walk(expr))
    case IRLoad(ast, obj, index) =>
      IRLoad(walk(ast), walk(obj), walk(index))
    case id: IRUserId =>
      walk(id)
    case id: IRTmpId =>
      walk(id)
    case IRThis(ast) =>
      IRThis(walk(ast))
    case IRNumber(ast, text, num) =>
      IRNumber(walk(ast), text, num)
    case IRString(ast, str) =>
      IRString(walk(ast), str)
    case IRBool(ast, isBool) =>
      IRBool(walk(ast), isBool)
    case IRUndef(ast) =>
      IRUndef(walk(ast))
    case IRNull(ast) =>
      IRNull(walk(ast))
  }

  def walk(node: IRMember): IRMember = node match {
    case IRField(ast, prop, expr) =>
      IRField(walk(ast), walk(prop), walk(expr))
    case IRGetProp(ast, ftn) =>
      IRGetProp(walk(ast), walk(ftn))
    case IRSetProp(ast, ftn) =>
      IRSetProp(walk(ast), walk(ftn))
  }

  def walk(node: IRFunctional): IRFunctional = node match {
    case IRFunctional(ast, isFromSource, name, params, args, fds, vds, body) =>
      IRFunctional(walk(ast), isFromSource, walk(name), params.map(walk),
        args.map(walk), fds.map(walk), vds.map(walk), body.map(walk))
  }

  def walk(node: IROp): IROp = node match {
    case IROp(ast, kind) =>
      IROp(walk(ast), kind)
  }

  def walk(node: IRFunDecl): IRFunDecl = node match {
    case IRFunDecl(ast, ftn) =>
      IRFunDecl(walk(ast), walk(ftn))
  }

  def walk(node: IRVarStmt): IRVarStmt = node match {
    case IRVarStmt(ast, lhs, isFromParam) =>
      IRVarStmt(walk(ast), walk(lhs), isFromParam)
  }

  def walk(node: IRId): IRId = node match {
    case IRUserId(ast, originalName, uniqueName, isGlobal, isWith) =>
      IRUserId(walk(ast), originalName, uniqueName, isGlobal, isWith)
    case IRTmpId(ast, originalName, uniqueName, isGlobal) =>
      IRTmpId(walk(ast), originalName, uniqueName, isGlobal)
  }
}

trait IRGeneralWalker[Result] {
  def join(args: Result*): Result

  def walkOpt(opt: Option[IRNode]): List[Result] =
    opt.fold(List[Result]()) { n: IRNode =>
      List(n match {
        case s: IRStmt => walk(s)
        case i: IRId => walk(i)
        case e: IRExpr => walk(e)
      })
    }

  def walk(ast: ASTNode): Result = join()

  def walk(node: IRRoot): Result = node match {
    case IRRoot(ast, fds, vds, irs) =>
      join(walk(ast) :: fds.map(walk) ++ vds.map(walk) ++ irs.map(walk): _*)
  }

  def walk(node: IRStmt): Result = node match {
    case IRExprStmt(ast, lhs, right, isRef) =>
      join(walk(ast), walk(lhs), walk(right))
    case IRDelete(ast, lhs, id) =>
      join(walk(ast), walk(lhs), walk(id))
    case IRDeleteProp(ast, lhs, obj, index) =>
      join(walk(ast), walk(lhs), walk(obj), walk(index))
    case IRObject(ast, lhs, members, proto) =>
      join(walk(ast) :: walk(lhs) :: members.map(walk) ++ walkOpt(proto): _*)
    case IRArray(ast, lhs, elements) =>
      join(walk(ast) :: walk(lhs) :: elements.flatMap(walkOpt): _*)
    case IRArrayNumber(ast, lhs, elements) =>
      join(walk(ast), walk(lhs))
    case IRArgs(ast, lhs, elements) =>
      join(walk(ast) :: walk(lhs) :: elements.flatMap(walkOpt): _*)
    case IRCall(ast, lhs, fun, thisB, args) =>
      join(walk(ast), walk(lhs), walk(fun), walk(thisB), walk(args))
    case IRInternalCall(ast, lhs, fun, first, second) =>
      join(walk(ast) :: walk(lhs) :: walk(fun) :: walk(first) :: walkOpt(second): _*)
    case IRNew(ast, lhs, fun, args) =>
      join(walk(ast) :: walk(lhs) :: walk(fun) :: args.map(walk): _*)
    case IRFunExpr(ast, lhs, ftn) =>
      join(walk(ast), walk(lhs), walk(ftn))
    case IREval(ast, lhs, arg) =>
      join(walk(ast), walk(lhs), walk(arg))
    case IRStmtUnit(ast, stmts) =>
      join(walk(ast) :: stmts.map(walk): _*)
    case IRStore(ast, obj, index, rhs) =>
      join(walk(ast), walk(obj), walk(index), walk(rhs))
    case fd: IRFunDecl =>
      walk(fd)
    case IRBreak(ast, label) =>
      join(walk(ast), walk(label))
    case IRReturn(ast, expr) =>
      join(walk(ast) :: walkOpt(expr): _*)
    case IRWith(ast, id, stmt) =>
      join(walk(ast), walk(id), walk(stmt))
    case IRLabelStmt(ast, label, stmt) =>
      join(walk(ast), walk(label), walk(stmt))
    case vs: IRVarStmt =>
      walk(vs)
    case IRThrow(ast, expr) =>
      join(walk(ast), walk(expr))
    case IRSeq(ast, stmts) =>
      join(walk(ast) :: stmts.map(walk): _*)
    case IRIf(ast, expr, trueB, falseB) =>
      join(walk(ast) :: walk(expr) :: walk(trueB) :: walkOpt(falseB): _*)
    case IRWhile(ast, cond, body) =>
      join(walk(ast), walk(cond), walk(body))
    case IRTry(ast, body, name, catchB, finallyB) =>
      join(walk(ast) :: walk(body) :: walkOpt(name) ++ walkOpt(catchB) ++ walkOpt(finallyB): _*)
    case IRNoOp(ast, desc) =>
      walk(ast)
  }

  def walk(node: IRExpr): Result = node match {
    case IRBin(ast, first, op, second) =>
      join(walk(ast), walk(first), walk(op), walk(second))
    case IRUn(ast, op, expr) =>
      join(walk(ast), walk(op), walk(expr))
    case IRLoad(ast, obj, index) =>
      join(walk(ast), walk(obj), walk(index))
    case id: IRUserId =>
      walk(id)
    case id: IRTmpId =>
      walk(id)
    case IRThis(ast) =>
      walk(ast)
    case IRNumber(ast, text, num) =>
      walk(ast)
    case IRString(ast, str) =>
      walk(ast)
    case IRBool(ast, isBool) =>
      walk(ast)
    case IRUndef(ast) =>
      walk(ast)
    case IRNull(ast) =>
      walk(ast)
  }

  def walk(node: IRMember): Result = node match {
    case IRField(ast, prop, expr) =>
      join(walk(ast), walk(prop), walk(expr))
    case IRGetProp(ast, ftn) =>
      join(walk(ast), walk(ftn))
    case IRSetProp(ast, ftn) =>
      join(walk(ast), walk(ftn))
  }

  def walk(node: IRFunctional): Result = node match {
    case IRFunctional(ast, isFromSource, name, params, args, fds, vds, body) =>
      join(walk(ast) :: walk(name) ::
        (params.map(walk) ++ args.map(walk) ++ fds.map(walk) ++ vds.map(walk) ++ body.map(walk)): _*)
  }

  def walk(node: IROp): Result = node match {
    case IROp(ast, kind) =>
      walk(ast)
  }

  def walk(node: IRFunDecl): Result = node match {
    case IRFunDecl(ast, ftn) =>
      join(walk(ast), walk(ftn))
  }

  def walk(node: IRVarStmt): Result = node match {
    case IRVarStmt(ast, lhs, isFromParam) =>
      join(walk(ast), walk(lhs))
  }

  def walk(node: IRId): Result = node match {
    case IRUserId(ast, originalName, uniqueName, isGlobal, isWith) =>
      walk(ast)
    case IRTmpId(ast, originalName, uniqueName, isGlobal) =>
      walk(ast)
  }
}
