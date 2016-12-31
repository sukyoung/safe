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

package kr.ac.kaist.safe.nodes.ir

import kr.ac.kaist.safe.nodes.ast.{ ASTNode, Label, Id }
import kr.ac.kaist.safe.SIGNIFICANT_BITS
import kr.ac.kaist.safe.util._

// Expression
abstract class IRExpr(
  override val ast: ASTNode
) extends IRNode(ast)

// Side-effect free expressions
abstract class IROpApp(
  override val ast: ASTNode
) extends IRExpr(ast)

// Binary expression
// Expr ::= e binop e
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

// Unary expression
// Expr ::= unop e
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

// Load
// Expr ::= x[e]
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

// Variable
abstract class IRId(
    override val ast: ASTNode,
    val originalName: String,
    val uniqueName: String,
    val global: Boolean
) extends IRExpr(ast) {
  override def toString(indent: Int): String = {
    val size = SIGNIFICANT_BITS
    val str = uniqueName match {
      case x if (NodeUtil.isInternal(x) && !NodeUtil.isGlobalName(x)) =>
        uniqueName.dropRight(size) + NodeUtil.getNodesE(uniqueName.takeRight(size))
      case _ => uniqueName
    }
    val s: StringBuilder = new StringBuilder
    s.append(NodeUtil.pp(str))
    s.toString
  }

  // When this IRId is a property string name, use toPropName instead of toString
  def toPropName(indent: Int): String = {
    val size = SIGNIFICANT_BITS
    val str = uniqueName match {
      case x if (NodeUtil.isInternal(x) && !NodeUtil.isGlobalName(x)) =>
        uniqueName.dropRight(size) + NodeUtil.getNodesE(uniqueName.takeRight(size))
      case _ => uniqueName
    }
    val s: StringBuilder = new StringBuilder
    s.append("\"")
    s.append(NodeUtil.pp(str.replaceAll("\\\\", "\\\\\\\\")))
    s.append("\"")
    s.toString
  }
}

// Variable
// Expr ::= x
case class IRUserId(
  override val ast: ASTNode,
  override val originalName: String,
  override val uniqueName: String,
  override val global: Boolean,
  isWith: Boolean
) extends IRId(ast, originalName, uniqueName, global)

// Internally generated identifiers by Translator
// Do not appear in the JavaScript source text.
case class IRTmpId(
    override val ast: ASTNode,
    override val originalName: String,
    override val uniqueName: String,
    override val global: Boolean = false
) extends IRId(ast, originalName, uniqueName, global) {
  // constructor
  def this(name: String) =
    this(NodeUtil.TEMP_AST, name, name, false)
  def this(id: Id) =
    this(NodeUtil.TEMP_AST, id.text, id.text, false)
  def this(label: Label) =
    this(NodeUtil.TEMP_AST, label.id.text, label.id.text, false)
}

// this
case class IRThis(
    override val ast: ASTNode
) extends IRExpr(ast) {
  override def toString(indent: Int): String = "this"
}

// internal value
case class IRInternalValue(
    override val ast: ASTNode,
    name: String
) extends IRExpr(ast) {
  override def toString(indent: Int): String = s"<>$name<>"
}

// Value
case class IRVal(
    value: EJSVal
) extends IRExpr(NodeUtil.TEMP_AST) {
  override def toString(indent: Int): String = value match {
    case EJSString(str) =>
      "\"" + NodeUtil.pp(str.replaceAll("\\\\", "\\\\\\\\")) + "\""
    case _ => value.toString
  }
}
object IRVal {
  def apply(text: String, num: Double): IRVal = IRVal(EJSNumber(text, num))
  def apply(str: String): IRVal = IRVal(EJSString(str))
  def apply(bool: Boolean): IRVal = IRVal(EJSBool(bool))
}
