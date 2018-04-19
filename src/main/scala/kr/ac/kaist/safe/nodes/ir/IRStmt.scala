/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.nodes.ir

import java.lang.Double
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.ast.ASTNode
import kr.ac.kaist.safe.util.NodeUtil

// Statement
abstract class IRStmt(
  override val ast: ASTNode
) extends IRNode(ast)

// Assignment
abstract class IRAssign(
  override val ast: ASTNode,
  val lhs: IRId
) extends IRStmt(ast)

// Expression
// Stmt ::= x = e
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

// Delete expression
// Stmt ::= x = delete y
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

// Delete property expression
// Stmt ::= x = delete y[e]
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

// Object literal
// Stmt ::= x = { member, ... }
case class IRObject(
    override val ast: ASTNode,
    override val lhs: IRId,
    members: List[IRMember],
    proto: Option[IRId]
) extends IRAssign(ast, lhs) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = {").append(LINE_SEP)
    s.append(NodeUtil.getIndent(indent + 1)).append(NodeUtil.join(indent, members, "," + LINE_SEP + NodeUtil.getIndent(indent + 1), new StringBuilder("")))
    proto.map(p => s.append("[[Prototype]]=").append(p.toString(indent + 1)))
    s.append(LINE_SEP).append(NodeUtil.getIndent(indent)).append("}")
    s.toString
  }
}

// Array literal
// Stmt ::= x = [ e, ... ]
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

// Array literal with numbers
// Stmt ::= x = [ n, ... ]
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

// Arguments
// Stmt ::= x = [ e, ... ]
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

// Call
// Stmt ::= x = f(this, arguments)
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
    name: String,
    args: List[IRExpr]
) extends IRAssign(ast, lhs) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append(name).append("(")
    s.append(args.map(_.toString(indent)).mkString(", "))
    s.append(")")
    s.toString
  }
}

// New
// Stmt ::= x = new f(x, ...)
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
    s.append(NodeUtil.join(indent, args, ", ", new StringBuilder("")))
    s.append(")")
    s.toString
  }
}

// Function expression
// Stmt ::= x = function f (this, arguments) { s }
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

// Eval
// Stmt ::= x = eval(e)
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

// AST statement unit
// Stmt ::= s
case class IRStmtUnit(
    override val ast: ASTNode,
    stmts: List[IRStmt]
) extends IRStmt(ast) {
  override def toString(indent: Int): String = stmts match {
    case List(stmt) => stmt.toString(indent)
    case _ =>
      val s: StringBuilder = new StringBuilder
      s.append("{").append(LINE_SEP)
      s.append(NodeUtil.getIndent(indent + 1)).append(NodeUtil.join(indent + 1, stmts, LINE_SEP + NodeUtil.getIndent(indent + 1), new StringBuilder("")))
      s.append(LINE_SEP).append(NodeUtil.getIndent(indent)).append("}")
      s.toString
  }
}
object IRStmtUnit {
  def apply(ast: ASTNode, stmts: IRStmt*): IRStmtUnit = IRStmtUnit(ast, stmts.toList)
}

// Store
// Stmt ::= x[e] = e
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

// Function declaration
// Stmt ::= function f (this, arguments) { s }
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

// Break
// Stmt ::= break label
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

// Return
// Stmt ::= return e?
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

// With
// Stmt ::= with ( x ) s
case class IRWith(
    override val ast: ASTNode,
    id: IRId,
    stmt: IRStmt
) extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("with(")
    s.append(id.toString(indent)).append(")").append(LINE_SEP)
    s.append(NodeUtil.getIndent(indent)).append(stmt.toString(indent))
    s.toString
  }
}

// Label
// Stmt ::= l : { s }
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

// Var
// Stmt ::= var x
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

// Throw
// Stmt ::= throw e
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

// Sequence
// Stmt ::= s; ...
case class IRSeq(
    override val ast: ASTNode,
    stmts: List[IRStmt]
) extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("{").append(LINE_SEP)
    s.append(NodeUtil.getIndent(indent + 1)).append(NodeUtil.join(indent + 1, stmts, LINE_SEP + NodeUtil.getIndent(indent + 1), new StringBuilder("")))
    s.append(LINE_SEP).append(NodeUtil.getIndent(indent)).append("}")
    s.toString
  }
}
object IRSeq {
  def apply(ast: ASTNode, stmts: IRStmt*): IRSeq = IRSeq(ast, stmts.toList)
}

// If
// Stmt ::= if (e) then s (else s)?
case class IRIf(
    override val ast: ASTNode,
    expr: IRExpr,
    trueB: IRStmt,
    falseB: Option[IRStmt]
) extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("if(").append(expr.toString(indent)).append(")").append(LINE_SEP)
    NodeUtil.inlineIndent(trueB, s, indent)
    falseB match {
      case Some(f) =>
        s.append(LINE_SEP).append(NodeUtil.getIndent(indent)).append("else").append(LINE_SEP)
        NodeUtil.inlineIndent(f, s, indent)
      case None =>
    }
    s.toString
  }
}

// While
// Stmt ::= while (e) s
case class IRWhile(
    override val ast: ASTNode,
    cond: IRExpr,
    body: IRStmt,
    breakLabel: IRId,
    contLabel: IRId
) extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("while(")
    s.append(cond.toString(indent)).append(")").append(LINE_SEP)
    s.append(NodeUtil.getIndent(indent)).append(body.toString(indent))
    s.toString
  }
}

// Try
// Stmt ::= try { s } (catch (x) { s })? (finally { s })?
case class IRTry(
    override val ast: ASTNode,
    body: IRStmt,
    name: Option[IRId],
    catchB: Option[IRStmt],
    finallyB: Option[IRStmt]
) extends IRStmt(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("try").append(LINE_SEP)
    NodeUtil.inlineIndent(body, s, indent)
    catchB match {
      case Some(cb) =>
        s.append(LINE_SEP).append(NodeUtil.getIndent(indent))
        s.append("catch(").append(name.get.toString(indent)).append(")").append(LINE_SEP)
        NodeUtil.inlineIndent(cb, s, indent)
      case None =>
    }
    finallyB match {
      case Some(f) =>
        s.append(LINE_SEP).append(NodeUtil.getIndent(indent)).append("finally").append(LINE_SEP)
        NodeUtil.inlineIndent(f, s, indent)
      case None =>
    }
    s.toString
  }
}

// No operation
case class IRNoOp(
    override val ast: ASTNode,
    desc: String
) extends IRStmt(ast) {
  override def toString(indent: Int): String = ""
}
