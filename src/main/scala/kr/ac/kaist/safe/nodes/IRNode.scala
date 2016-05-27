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
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span, EJSOp, EJSEqType, SourceLoc }

abstract class IRNode(
    val ast: ASTNode
) extends Node {
  def span: Span = ast.span
  def comment: Option[Comment] = ast.comment
  def fileName: String = ast.fileName
  def begin: SourceLoc = ast.begin
  def end: SourceLoc = ast.end
  def line: Int = ast.line
  def offset: Int = ast.offset
}

/**
 * IRRoot ::= Statement*
 */
case class IRRoot(
    override val ast: ASTNode,
    fds: List[IRFunDecl],
    vds: List[IRVarStmt],
    irs: List[IRStmt]
) extends IRNode(ast) {
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
abstract class IRStmt(
  override val ast: ASTNode
) extends IRNode(ast)
abstract class IRAssign(
  override val ast: ASTNode,
  val lhs: IRId
) extends IRStmt(ast)

/**
 * Expression
 * Stmt ::= x = e
 */
case class IRExprStmt(
    override val ast: ASTNode,
    override val lhs: IRId,
    right: IRExpr,
    ref: Boolean = false
) extends IRAssign(ast, lhs) {
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
case class IRDelete(
    override val ast: ASTNode,
    override val lhs: IRId,
    id: IRId
) extends IRAssign(ast, lhs) {
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
case class IRDeleteProp(
    override val ast: ASTNode,
    override val lhs: IRId,
    obj: IRId,
    index: IRExpr
) extends IRAssign(ast, lhs) {
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
case class IRObject(
    override val ast: ASTNode,
    override val lhs: IRId,
    members: List[IRMember],
    proto: Option[IRId]
) extends IRAssign(ast, lhs) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = {").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent, members, "," + Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    proto.map(p => s.append("[[Prototype]]=").append(p.toString(indent + 1)))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * Array literal
 * Stmt ::= x = [ e, ... ]
 */
case class IRArray(
    override val ast: ASTNode,
    override val lhs: IRId,
    elements: List[Option[IRExpr]]
) extends IRAssign(ast, lhs) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    elements.foreach(e => s.append(e.fold("") { _.toString(indent) }).append(", "))
    s.append("]")
    s.toString
  }
}

/**
 * Array literal with numbers
 * Stmt ::= x = [ n, ... ]
 */
case class IRArrayNumber(
    override val ast: ASTNode,
    override val lhs: IRId,
    elements: List[Double]
) extends IRAssign(ast, lhs) {
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
case class IRArgs(
    override val ast: ASTNode,
    override val lhs: IRId,
    elements: List[Option[IRExpr]]
) extends IRAssign(ast, lhs) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    elements.foreach(e => s.append(e.fold("") { _.toString(indent) }).append(", "))
    s.append("]")
    s.toString
  }
}

/**
 * Call
 * Stmt ::= x = f(this, arguments)
 */
case class IRCall(
    override val ast: ASTNode,
    override val lhs: IRId,
    fun: IRId,
    thisB: IRId,
    args: IRId
) extends IRAssign(ast, lhs) {
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
case class IRInternalCall(
    override val ast: ASTNode,
    override val lhs: IRId,
    fun: IRId,
    first: IRExpr,
    second: Option[IRId]
) extends IRAssign(ast, lhs) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append(fun.toString(indent)).append("(")
    s.append(first.toString(indent))
    second.map(e => s.append(", ").append(e.toString(indent)))
    s.append(")")
    s.toString
  }
}

/**
 * New
 * Stmt ::= x = new f(x, ...)
 */
case class IRNew(
    override val ast: ASTNode,
    override val lhs: IRId,
    fun: IRId,
    args: List[IRId]
) extends IRAssign(ast, lhs) {
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
case class IRFunExpr(
    override val ast: ASTNode,
    override val lhs: IRId,
    fun: IRFunctional
) extends IRAssign(ast, lhs) {
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
case class IREval(
    override val ast: ASTNode,
    override val lhs: IRId,
    arg: IRExpr
) extends IRAssign(ast, lhs) {
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
case class IRStmtUnit(
    override val ast: ASTNode,
    stmts: List[IRStmt]
) extends IRStmt(ast) {
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
case class IRStore(
    override val ast: ASTNode,
    obj: IRId,
    index: IRExpr,
    rhs: IRExpr
) extends IRStmt(ast) {
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
case class IRFunDecl(
    override val ast: ASTNode,
    ftn: IRFunctional
) extends IRStmt(ast) {
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
case class IRBreak(
    override val ast: ASTNode,
    label: IRId
) extends IRStmt(ast) {
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
case class IRReturn(
    override val ast: ASTNode,
    expr: Option[IRExpr]
) extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("return")
    expr.map(e => s.append(" ").append(e.toString(indent)))
    s.toString
  }
}

/**
 * With
 * Stmt ::= with ( x ) s
 */
case class IRWith(
    override val ast: ASTNode,
    id: IRId,
    stmt: IRStmt
) extends IRStmt(ast) {
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
case class IRLabelStmt(
    override val ast: ASTNode,
    label: IRId,
    stmt: IRStmt
) extends IRStmt(ast) {
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
case class IRVarStmt(
    override val ast: ASTNode,
    lhs: IRId,
    fromParam: Boolean
) extends IRStmt(ast) {
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
case class IRThrow(
    override val ast: ASTNode,
    expr: IRExpr
) extends IRStmt(ast) {
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
case class IRSeq(
    override val ast: ASTNode,
    stmts: List[IRStmt]
) extends IRStmt(ast) {
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
case class IRIf(
    override val ast: ASTNode,
    expr: IRExpr,
    trueB: IRStmt,
    falseB: Option[IRStmt]
) extends IRStmt(ast) {
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
case class IRWhile(
    override val ast: ASTNode,
    cond: IRExpr,
    body: IRStmt
) extends IRStmt(ast) {
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
case class IRTry(
    override val ast: ASTNode,
    body: IRStmt,
    name: Option[IRId],
    catchB: Option[IRStmt],
    finallyB: Option[IRStmt]
) extends IRStmt(ast) {
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
case class IRNoOp(
    override val ast: ASTNode,
    desc: String
) extends IRStmt(ast) {
  override def toString(indent: Int): String = ""
}

/**
 * Member
 */
abstract class IRMember(
  override val ast: ASTNode
) extends IRNode(ast)
/**
 * Member ::= x : e
 */
case class IRField(
    override val ast: ASTNode,
    prop: IRId,
    expr: IRExpr
) extends IRMember(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(prop.ast match {
      case PropStr(_, _) => prop.toPropName(indent)
      case _ => prop.toString(indent)
    })
    s.append(" : ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Member ::= get x () { s }
 */
case class IRGetProp(
    override val ast: ASTNode,
    ftn: IRFunctional
) extends IRMember(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("get ").append(ftn)
    s.toString
  }
}

/**
 * Member ::= set x ( y ) { s }
 */
case class IRSetProp(
    override val ast: ASTNode,
    ftn: IRFunctional
) extends IRMember(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("set ").append(ftn)
    s.toString
  }
}

/**
 * Expression
 */
abstract class IRExpr(
  override val ast: ASTNode
) extends IRNode(ast)
/**
 * Side-effect free expressions
 */
abstract class IROpApp(
  override val ast: ASTNode
) extends IRExpr(ast)
/**
 * Binary expression
 * Expr ::= e binop e
 */
case class IRBin(
    override val ast: ASTNode,
    first: IRExpr,
    op: IROp,
    second: IRExpr
) extends IROpApp(ast) {
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
case class IRUn(
    override val ast: ASTNode,
    op: IROp,
    expr: IRExpr
) extends IROpApp(ast) {
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
case class IRLoad(
    override val ast: ASTNode,
    obj: IRId,
    index: IRExpr
) extends IROpApp(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(obj.toString(indent)).append("[").append(index.toString(indent)).append("]")
    s.toString
  }
}

/**
 * Variable
 */
abstract class IRId(
    override val ast: ASTNode,
    val originalName: String,
    val uniqueName: String,
    val global: Boolean
) extends IRExpr(ast) {
  override def toString(indent: Int): String = {
    val size = Config.SIGNIFICANT_BITS
    val str = uniqueName match {
      case x if (NU.isInternal(x) && !NU.isGlobalName(x)) =>
        uniqueName.dropRight(size) + NU.getNodesE(uniqueName.takeRight(size))
      case _ => uniqueName
    }
    val s: StringBuilder = new StringBuilder
    s.append(NU.pp(str))
    s.toString
  }

  // When this IRId is a property string name, use toPropName instead of toString
  def toPropName(indent: Int): String = {
    val size = Config.SIGNIFICANT_BITS
    val str = uniqueName match {
      case x if (NU.isInternal(x) && !NU.isGlobalName(x)) =>
        uniqueName.dropRight(size) + NU.getNodesE(uniqueName.takeRight(size))
      case _ => uniqueName
    }
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
case class IRUserId(
  override val ast: ASTNode,
  override val originalName: String,
  override val uniqueName: String,
  override val global: Boolean,
  isWith: Boolean
) extends IRId(ast, originalName, uniqueName, global)

/**
 * Internally generated identifiers by Translator
 * Do not appear in the JavaScript source text.
 */
case class IRTmpId(
  override val ast: ASTNode,
  override val originalName: String,
  override val uniqueName: String,
  override val global: Boolean = false
) extends IRId(ast, originalName, uniqueName, global)

/**
 * this
 */
case class IRThis(
    override val ast: ASTNode
) extends IRExpr(ast) {
  override def toString(indent: Int): String = "this"
}

/**
 * Value
 */
abstract class IRVal(
  override val ast: ASTNode
) extends IRExpr(ast)

/**
 * Primitive value
 */
abstract class IRPVal(
  override val ast: ASTNode
) extends IRExpr(ast)

/**
 * PVal ::= number literal
 */
case class IRNumber(
    override val ast: ASTNode,
    text: String,
    num: Double
) extends IRVal(ast) {
  override def toString(indent: Int): String = text
}

/**
 * PVal ::= String
 */
case class IRString(
    override val ast: ASTNode,
    str: String
) extends IRVal(ast) {
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
case class IRBool(
    override val ast: ASTNode,
    bool: Boolean
) extends IRVal(ast) {
  override def toString(indent: Int): String = if (bool) "true" else "false"
}

/**
 * PVal ::= undefined
 */
case class IRUndef(
    override val ast: ASTNode
) extends IRVal(ast) {
  override def toString(indent: Int): String = "undefined"
}

/**
 * PVal ::= null
 */
case class IRNull(
    override val ast: ASTNode
) extends IRVal(ast) {
  override def toString(indent: Int): String = "null"
}

/**
 * Operator
 */
case class IROp(
    override val ast: ASTNode,
    kind: EJSOp
) extends IRNode(ast) {
  override def toString(indent: Int): String = name
  val name: String = kind.name
  def isAssertOperator: Boolean = kind.typ == EJSEqType
}

/**
 * Common shape for functions
 */
case class IRFunctional(
    override val ast: Functional,
    fromSource: Boolean,
    name: IRId,
    params: List[IRId],
    args: List[IRStmt],
    fds: List[IRFunDecl],
    vds: List[IRVarStmt],
    body: List[IRStmt]
) extends IRNode(ast) {
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
  def walk(node: IRRoot): IRRoot = node match {
    case IRRoot(ast, fds, vds, irs) =>
      IRRoot(ast, fds.map(walk), vds.map(walk), irs.map(walk))
  }

  def walk(node: IRStmt): IRStmt = node match {
    case IRExprStmt(ast, lhs, right, isRef) =>
      IRExprStmt(ast, walk(lhs), walk(right), isRef)
    case IRDelete(ast, lhs, id) =>
      IRDelete(ast, walk(lhs), walk(id))
    case IRDeleteProp(ast, lhs, obj, index) =>
      IRDeleteProp(ast, walk(lhs), walk(obj), walk(index))
    case IRObject(ast, lhs, members, proto) =>
      IRObject(ast, walk(lhs), members.map(walk), proto.map(walk))
    case IRArray(ast, lhs, elements) =>
      IRArray(ast, walk(lhs), elements.map(_.map(walk)))
    case IRArrayNumber(ast, lhs, elements) =>
      IRArrayNumber(ast, walk(lhs), elements)
    case IRArgs(ast, lhs, elements) =>
      IRArgs(ast, walk(lhs), elements.map(_.map(walk)))
    case IRCall(ast, lhs, fun, thisB, args) =>
      IRCall(ast, walk(lhs), walk(fun), walk(thisB), walk(args))
    case IRInternalCall(ast, lhs, fun, first, second) =>
      IRInternalCall(ast, walk(lhs), walk(fun), walk(first), second.map(walk))
    case IRNew(ast, lhs, fun, args) =>
      IRNew(ast, walk(lhs), walk(fun), args.map(walk))
    case IRFunExpr(ast, lhs, ftn) =>
      IRFunExpr(ast, walk(lhs), walk(ftn))
    case IREval(ast, lhs, arg) =>
      IREval(ast, walk(lhs), walk(arg))
    case IRStmtUnit(ast, stmts) =>
      IRStmtUnit(ast, stmts.map(walk))
    case IRStore(ast, obj, index, rhs) =>
      IRStore(ast, walk(obj), walk(index), walk(rhs))
    case fd: IRFunDecl =>
      walk(fd)
    case IRBreak(ast, label) =>
      IRBreak(ast, walk(label))
    case IRReturn(ast, expr) =>
      IRReturn(ast, expr.map(walk))
    case IRWith(ast, id, stmt) =>
      IRWith(ast, walk(id), walk(stmt))
    case IRLabelStmt(ast, label, stmt) =>
      IRLabelStmt(ast, walk(label), walk(stmt))
    case vs: IRVarStmt =>
      walk(vs)
    case IRThrow(ast, expr) =>
      IRThrow(ast, walk(expr))
    case IRSeq(ast, stmts) =>
      IRSeq(ast, stmts.map(walk))
    case IRIf(ast, expr, trueB, falseB) =>
      IRIf(ast, walk(expr), walk(trueB), falseB.map(walk))
    case IRWhile(ast, cond, body) =>
      IRWhile(ast, walk(cond), walk(body))
    case IRTry(ast, body, name, catchB, finallyB) =>
      IRTry(ast, walk(body), name.map(walk), catchB.map(walk), finallyB.map(walk))
    case IRNoOp(ast, desc) =>
      IRNoOp(ast, desc)
  }

  def walk(node: IRExpr): IRExpr = node match {
    case IRBin(ast, first, op, second) =>
      IRBin(ast, walk(first), walk(op), walk(second))
    case IRUn(ast, op, expr) =>
      IRUn(ast, walk(op), walk(expr))
    case IRLoad(ast, obj, index) =>
      IRLoad(ast, walk(obj), walk(index))
    case id: IRUserId =>
      walk(id)
    case id: IRTmpId =>
      walk(id)
    case IRThis(ast) =>
      IRThis(ast)
    case IRNumber(ast, text, num) =>
      IRNumber(ast, text, num)
    case IRString(ast, str) =>
      IRString(ast, str)
    case IRBool(ast, isBool) =>
      IRBool(ast, isBool)
    case IRUndef(ast) =>
      IRUndef(ast)
    case IRNull(ast) =>
      IRNull(ast)
  }

  def walk(node: IRMember): IRMember = node match {
    case IRField(ast, prop, expr) =>
      IRField(ast, walk(prop), walk(expr))
    case IRGetProp(ast, ftn) =>
      IRGetProp(ast, walk(ftn))
    case IRSetProp(ast, ftn) =>
      IRSetProp(ast, walk(ftn))
  }

  def walk(node: IRFunctional): IRFunctional = node match {
    case IRFunctional(ast, isFromSource, name, params, args, fds, vds, body) =>
      IRFunctional(ast, isFromSource, walk(name), params.map(walk),
        args.map(walk), fds.map(walk), vds.map(walk), body.map(walk))
  }

  def walk(node: IROp): IROp = node match {
    case IROp(ast, kind) =>
      IROp(ast, kind)
  }

  def walk(node: IRFunDecl): IRFunDecl = node match {
    case IRFunDecl(ast, ftn) =>
      IRFunDecl(ast, walk(ftn))
  }

  def walk(node: IRVarStmt): IRVarStmt = node match {
    case IRVarStmt(ast, lhs, isFromParam) =>
      IRVarStmt(ast, walk(lhs), isFromParam)
  }

  def walk(node: IRId): IRId = node match {
    case IRUserId(ast, originalName, uniqueName, isGlobal, isWith) =>
      IRUserId(ast, originalName, uniqueName, isGlobal, isWith)
    case IRTmpId(ast, originalName, uniqueName, isGlobal) =>
      IRTmpId(ast, originalName, uniqueName, isGlobal)
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
