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

package kr.ac.kaist.safe.nodes.ast

import java.lang.Double
import java.math.BigInteger
import kr.ac.kaist.safe.util.{ NodeUtil => NU }
import kr.ac.kaist.safe.LINE_SEP

abstract class Expr(
    override val info: ASTNodeInfo
) extends ASTNode(info: ASTNodeInfo) {
  def isEval: Boolean = this match {
    case VarRef(info, Id(_, text, _, _)) => text.equals("eval")
    case _ => false
  }

  def unwrapParen: Expr = this match {
    case Parenthesized(info, body) => body
    case _ => this
  }
}

// Expr ::= Expr, Expr
case class ExprList(
    override val info: ASTNodeInfo,
    exprs: List[Expr]
) extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    NU.join(indent, exprs, ", ", s)
    s.toString
  }
}

// Expr ::= Expr ? Expr : Expr
case class Cond(
    override val info: ASTNodeInfo,
    cond: Expr,
    trueBranch: Expr,
    falseBranch: Expr
) extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(cond.toString(indent))
      .append(" ? ")
      .append(trueBranch.toString(indent))
      .append(" : ")
      .append(falseBranch.toString(indent))
    s.toString
  }
}

// Expr ::= Expr Op Expr
case class InfixOpApp(
    override val info: ASTNodeInfo,
    left: Expr,
    op: Op,
    right: Expr
) extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(left.toString(indent))
      .append(" ")
      .append(op.toString(indent))
      .append(" ")
      .append(right.toString(indent))
    s.toString
  }
}

// Expr ::= Op Expr
case class PrefixOpApp(
    override val info: ASTNodeInfo,
    op: Op,
    right: Expr
) extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(op.toString(indent))
      .append(" ")
      .append(right.toString(indent))
    s.toString
  }
}

// Expr ::= Lhs Op
case class UnaryAssignOpApp(
    override val info: ASTNodeInfo,
    lhs: LHS,
    op: Op
) extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(lhs.toString(indent))
      .append(op.toString(indent))
    s.toString
  }
}

// Expr ::= Lhs Op Expr
case class AssignOpApp(
    override val info: ASTNodeInfo,
    lhs: LHS,
    op: Op,
    right: Expr
) extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(lhs.toString(indent))
      .append(" ")
      .append(op.toString(indent))
      .append(" ")
      .append(right.toString(indent))
    s.toString
  }
}

// Expr ::= Lhs
abstract class LHS(
    override val info: ASTNodeInfo
) extends Expr(info: ASTNodeInfo) {
  def isName: Boolean = this match {
    case _: VarRef => true
    case _: Dot => true
    case _ => false
  }
}

// Lhs ::= Literal
abstract class Literal(
  override val info: ASTNodeInfo
) extends LHS(info: ASTNodeInfo)

// Literal ::= this
case class This(
    override val info: ASTNodeInfo
) extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("this")
    s.toString
  }
}

// Literal ::= null
case class Null(
    override val info: ASTNodeInfo
) extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("null")
    s.toString
  }
}

// Literal ::= true | false
case class Bool(
    override val info: ASTNodeInfo, bool: Boolean
) extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(if (bool) "true" else "false")
    s.toString
  }
}

// number literal
abstract class NumberLiteral(
  override val info: ASTNodeInfo
) extends Literal(info: ASTNodeInfo)

// float literal
case class DoubleLiteral(
    override val info: ASTNodeInfo,
    text: String,
    num: Double
) extends NumberLiteral(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(text)
    s.toString
  }
}

// int literal
case class IntLiteral(
    override val info: ASTNodeInfo,
    intVal: BigInteger,
    radix: Integer
) extends NumberLiteral(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(radix.toInt match {
      case 8 => "0" + intVal.toString(8)
      case 16 => "0x" + intVal.toString(16)
      case _ => intVal.toString
    })
    s.toString
  }
}

// Literal ::= String
case class StringLiteral(
    override val info: ASTNodeInfo,
    quote: String,
    escaped: String,
    isRE: Boolean
) extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(quote)
    NU.ppAST(s, escaped)
    s.append(quote)
    s.toString
  }
}

// Literal ::= RegularExpression
case class RegularExpression(
    override val info: ASTNodeInfo,
    body: String,
    flag: String
) extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("/" + body + "/" + flag)
    s.toString
  }
}

// PrimaryExpr ::= Id
case class VarRef(
    override val info: ASTNodeInfo,
    id: Id
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(id.toString(indent))
    s.toString
  }
}

// PrimaryExpr ::= [ (Expr,)* ]
case class ArrayExpr(
    override val info: ASTNodeInfo,
    elements: List[Option[Expr]]
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("[")
    elements.foreach(e => s.append(e.fold("") {
      _.toString(indent)
    }).append(", "))
    s.append("]")
    s.toString
  }
}

// PrimaryExpr ::= [ (Number,)* ]
case class ArrayNumberExpr(
    override val info: ASTNodeInfo,
    elements: List[Double]
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("[")
    s.append("\"A LOT!!! " +
      elements.size +
      " elements are not printed here.\", ")
    s.append("]")
    s.toString
  }
}

// PrimaryExpr ::= { (Member,)* }
case class ObjectExpr(
    override val info: ASTNodeInfo,
    members: List[Member]
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("{")
      .append(LINE_SEP)
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1,
        members,
        "," + LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("}")
    s.toString
  }
}

// PrimaryExpr ::= ( Expr )
case class Parenthesized(
    override val info: ASTNodeInfo,
    expr: Expr
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(NU.inParentheses(expr.toString(indent)))
    s.toString
  }
}

// LHS ::= function Id? ( (Id,)* ) { SourceElement }
case class FunExpr(
    override val info: ASTNodeInfo,
    ftn: Functional
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("(function ")
    if (!NU.isFunExprName(ftn.name.text)) s.append(ftn.name.toString(indent))
    s.append("(")
      .append(NU.join(
        indent,
        ftn.params,
        ", ",
        new StringBuilder("")
      ))
      .append(") ")
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("{")
      .append(LINE_SEP)
    NU.prUseStrictDirective(s, indent, ftn.fds, ftn.vds, ftn.stmts)
    NU.prFtn(s, indent, ftn.fds, ftn.vds, ftn.stmts.body)
    s.append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("})")
    s.toString
  }
}

// LHS ::= Lhs [ Expr ]
case class Bracket(
    override val info: ASTNodeInfo,
    obj: LHS,
    index: Expr
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(obj.toString(indent))
      .append("[")
      .append(index.toString(indent))
      .append("]")
    s.toString
  }
}

// LHS ::= Lhs . Id
case class Dot(
    override val info: ASTNodeInfo,
    obj: LHS,
    member: Id
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(obj.toString(indent))
      .append(".")
      .append(member.toString(indent))
    s.toString
  }
}

// LHS ::= new Lhs
case class New(
    override val info: ASTNodeInfo,
    lhs: LHS
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("new ")
      .append(lhs.toString(indent))
    s.toString
  }
}

// LHS ::= Lhs ( (Expr,)* )
case class FunApp(
    override val info: ASTNodeInfo,
    fun: LHS,
    args: List[Expr]
) extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(fun.toString(indent))
      .append("(")
      .append(NU.join(indent, args, ", ", new StringBuilder("")))
      .append(")")
    s.toString
  }
}
