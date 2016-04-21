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
 * JavaScript AST
 * ECMAScript 5
 * *************************
 */

package kr.ac.kaist.safe.nodes

import java.lang.Double
import java.math.BigInteger
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }
import kr.ac.kaist.safe.config.Config

abstract class ASTNode(override val info: ASTNodeInfo)
  extends Node(info: NodeInfo)

/**
 * Program ::= SourceElement*
 */
case class Program(override val info: ASTNodeInfo, body: TopLevel)
    extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    NU.initNodesPrint
    val s: StringBuilder = new StringBuilder
    s.append(body.toString(indent))
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.toString
  }
}

/**
 * SourceElement ::= Stmt
 */
abstract class SourceElement(override val info: ASTNodeInfo)
  extends ASTNode(info: ASTNodeInfo)

abstract class Stmt(override val info: ASTNodeInfo)
  extends SourceElement(info: ASTNodeInfo)

/**
 * Internally generated NoOperation
 * currently to denote the end of a file by Shell
 * Do not appear in the JavaScript source text
 */
case class NoOp(override val info: ASTNodeInfo, desc: String)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = ""
}

/**
 * Internally generated statement unit by Hoister
 * Do not appear in the JavaScript source text
 */
case class StmtUnit(override val info: ASTNodeInfo, stmts: List[Stmt])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("{").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, stmts, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * SourceElement ::= function Id ( (Id,)* ) { SourceElement* }
 */
case class FunDecl(override val info: ASTNodeInfo, ftn: Functional, strict: Boolean)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("function ").append(ftn.toString(indent))
    s.toString
  }
}

/**
 * Stmt ::= { Stmt* }
 */
case class ABlock(override val info: ASTNodeInfo, stmts: List[Stmt], internal: Boolean)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("{").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, stmts, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * Stmt ::= var VarDecl(, VarDecl)* ;
 */
case class VarStmt(override val info: ASTNodeInfo, vds: List[VarDecl])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    vds match {
      case Nil => s.toString
      case _ =>
        s.append("var ")
        s.append(NU.join(indent, vds, ", ", new StringBuilder(""))).append(";")
        s.toString
    }
  }
}

/**
 * Stmt ::= ;
 */
case class EmptyStmt(override val info: ASTNodeInfo)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(";")
    s.toString
  }
}

/**
 * Stmt ::= Expr ;
 */
case class ExprStmt(override val info: ASTNodeInfo, expr: Expr, internal: Boolean)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(expr.toString(indent) + ";")
    s.toString
  }
}

/**
 * Stmt ::= if ( Expr ) Stmt (else Stmt)?
 */
case class If(override val info: ASTNodeInfo, cond: Expr, trueBranch: Stmt, falseBranch: Option[Stmt])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(trueBranch)
    s.append("if (").append(cond.toString(indent)).append(")").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(trueBranch.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(trueBranch.toString(indent))
    if (falseBranch.isDefined) {
      oneline = NU.isOneline(falseBranch.get)
      s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("else").append(Config.LINE_SEP)
      if (oneline)
        s.append(NU.getIndent(indent + 1)).append(falseBranch.get.toString(indent + 1))
      else
        s.append(NU.getIndent(indent)).append(falseBranch.get.toString(indent))
    }
    s.toString
  }
}

/**
 * Stmt ::= do Stmt while ( Expr ) ;
 */
case class DoWhile(override val info: ASTNodeInfo, body: Stmt, cond: Expr)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(body)
    s.append("do").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(body.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(body.toString(indent))
    s.append("while (")
    s.append(cond.toString(indent)).append(");")
    s.toString
  }
}

/**
 * Stmt ::= while ( Expr ) Stmt
 */
case class While(override val info: ASTNodeInfo, cond: Expr, body: Stmt)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(body)
    s.append("while (")
    s.append(cond.toString(indent)).append(")").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(body.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(body.toString(indent))
    s.toString
  }
}

/**
 * Stmt ::= for ( Expr? ; Expr? ; Expr? ) Stmt
 */
case class For(override val info: ASTNodeInfo, init: Option[Expr], cond: Option[Expr], action: Option[Expr], body: Stmt)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(body)
    s.append("for (")
    if (init.isDefined) s.append(init.get.toString(indent))
    s.append(";")
    if (cond.isDefined) s.append(cond.get.toString(indent))
    s.append(";")
    if (action.isDefined) s.append(action.get.toString(indent))
    s.append(")").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(body.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(body.toString(indent))
    s.toString
  }
}

/**
 * Stmt ::= for ( lhs in Expr ) Stmt
 */
case class ForIn(override val info: ASTNodeInfo, lhs: LHS, expr: Expr, body: Stmt)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(body)
    s.append("for (")
    s.append(lhs.toString(indent)).append(" in ")
    s.append(expr.toString(indent)).append(")").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(body.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(body.toString(indent))
    s.toString
  }
}

/**
 * Stmt ::= for ( var VarDecl(, VarDecl)* ; Expr? ; Expr? ) Stmt
 */
case class ForVar(override val info: ASTNodeInfo, vars: List[VarDecl], cond: Option[Expr], action: Option[Expr], body: Stmt)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(body)
    s.append("for(var ")
    s.append(NU.join(indent, vars, ", ", new StringBuilder("")))
    s.append(";")
    if (cond.isDefined) s.append(cond.get.toString(indent))
    s.append(";")
    if (action.isDefined) s.append(action.get.toString(indent))
    s.append(")").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(body.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(body.toString((indent)))
    s.toString
  }
}

/**
 * Stmt ::= for ( var VarDecl in Expr ) Stmt
 */
case class ForVarIn(override val info: ASTNodeInfo, vd: VarDecl, expr: Expr, body: Stmt)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(body)
    s.append("for(var ")
    s.append(vd.toString(indent)).append(" in ")
    s.append(expr.toString(indent)).append(")").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(body.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(body.toString(indent))
    s.toString
  }
}

/**
 * Stmt ::= continue Label? ;
 */
case class Continue(override val info: ASTNodeInfo, target: Option[Label])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("continue")
    if (target.isDefined) s.append(" ").append(target.get.toString(indent))
    s.append(";")
    s.toString
  }
}

/**
 * Stmt ::= break Label? ;
 */
case class Break(override val info: ASTNodeInfo, target: Option[Label])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("break")
    if (target.isDefined) s.append(" ").append(target.get.toString(indent))
    s.append(";")
    s.toString
  }
}

/**
 * Stmt ::= return Expr? ;
 */
case class Return(override val info: ASTNodeInfo, expr: Option[Expr])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("return")
    if (expr.isDefined) s.append(" ").append(expr.get.toString(indent))
    s.append(";")
    s.toString
  }
}

/**
 * Stmt ::= with ( Expr ) Stmt
 */
case class With(override val info: ASTNodeInfo, expr: Expr, stmt: Stmt)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    var oneline: Boolean = NU.isOneline(stmt)
    s.append("with (")
    s.append(expr.toString(indent)).append(")").append(Config.LINE_SEP)
    if (oneline) s.append(NU.getIndent(indent + 1)).append(stmt.toString(indent + 1))
    else s.append(NU.getIndent(indent)).append(stmt.toString(indent))
    s.toString
  }
}

/**
 * Stmt ::= switch ( Expr ) { CaseClause* (default : Stmt*)? CaseClause* }
 */
case class Switch(override val info: ASTNodeInfo, cond: Expr, frontCases: List[Case], defopt: Option[List[Stmt]], backCases: List[Case])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("switch (").append(cond.toString(indent)).append("){").append(Config.LINE_SEP)

    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, frontCases, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    if (defopt.isDefined) {
      s.append(Config.LINE_SEP).append(NU.getIndent(indent + 1)).append("default:")
      s.append(Config.LINE_SEP).append(NU.getIndent(indent + 2)).append(NU.join(indent + 2, defopt.get, Config.LINE_SEP + NU.getIndent(indent + 2), new StringBuilder("")))
    }
    s.append(Config.LINE_SEP).append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, backCases, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * Stmt ::= Label : Stmt
 */
case class LabelStmt(override val info: ASTNodeInfo, label: Label, stmt: Stmt)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(label.toString(indent)).append(" : ").append(stmt.toString(indent))
    s.toString
  }
}

/**
 * Stmt ::= throw Expr ;
 */
case class Throw(override val info: ASTNodeInfo, expr: Expr)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("throw ").append(expr.toString(indent)).append(";")
    s.toString
  }
}

/**
 * Stmt ::= try { Stmt* } (catch ( Id ) { Stmt* })? (finally { Stmt* })?
 */
case class Try(override val info: ASTNodeInfo, body: List[Stmt], catchBlock: Option[Catch], fin: Option[List[Stmt]])
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("try").append(Config.LINE_SEP).append("{")
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, body, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append("}")
    if (catchBlock.isDefined)
      s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append(catchBlock.get.toString(indent))
    if (fin.isDefined) {
      s.append(Config.LINE_SEP).append(NU.getIndent(indent))
      s.append("finally").append(Config.LINE_SEP).append("{")
      s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, fin.get, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
      s.append("}").append(Config.LINE_SEP)
    }
    s.toString
  }
}

/**
 * Stmt ::= debugger ;
 */
case class Debugger(override val info: ASTNodeInfo)
    extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("debugger;")
    s.toString
  }
}

/**
 * Program ::= SourceElement*
 */
case class SourceElements(override val info: ASTNodeInfo, body: List[SourceElement], strict: Boolean)
    extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = ""
}

/**
 * Stmt ::= Id (= Expr)?
 */
case class VarDecl(override val info: ASTNodeInfo, name: Id, expr: Option[Expr], strict: Boolean)
    extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(name.toString(indent))
    if (expr.isDefined) s.append(" = ").append(expr.get.toString(indent))
    s.toString
  }
}

/**
 * CaseClause ::= case Expr : Stmt*
 */
case class Case(override val info: ASTNodeInfo, cond: Expr, body: List[Stmt])
    extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("case ").append(cond.toString(indent))
    s.append(":").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, body, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP)
    s.toString
  }
}

/**
 * Catch ::= catch ( Id ) { Stmt* }
 */
case class Catch(override val info: ASTNodeInfo, id: Id, body: List[Stmt])
    extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("catch (").append(id.toString(indent)).append(")").append(Config.LINE_SEP)
    s.append("{")
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, body, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append("}").append(Config.LINE_SEP)
    s.toString
  }
}

abstract class Expr(override val info: ASTNodeInfo)
  extends ASTNode(info: ASTNodeInfo)

/**
 * Expr ::= Expr, Expr
 */
case class ExprList(override val info: ASTNodeInfo, exprs: List[Expr])
    extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    NU.join(indent, exprs, ", ", s).toString
    s.toString
  }
}

/**
 * Expr ::= Expr ? Expr : Expr
 */
case class Cond(override val info: ASTNodeInfo, cond: Expr, trueBranch: Expr, falseBranch: Expr)
    extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(cond.toString(indent)).append(" ? ").append(trueBranch.toString(indent)).append(" : ").append(falseBranch.toString(indent))
    s.toString
  }
}

/**
 * Expr ::= Expr Op Expr
 */
case class InfixOpApp(override val info: ASTNodeInfo, left: Expr, op: Op, right: Expr)
    extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(left.toString(indent)).append(" ")
    s.append(op.toString(indent)).append(" ")
    s.append(right.toString(indent))
    s.toString
  }
}

/**
 * Expr ::= Op Expr
 */
case class PrefixOpApp(override val info: ASTNodeInfo, op: Op, right: Expr)
    extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(op.toString(indent)).append(" ").append(right.toString(indent))
    s.toString
  }
}

/**
 * Expr ::= Lhs Op
 */
case class UnaryAssignOpApp(override val info: ASTNodeInfo, lhs: LHS, op: Op)
    extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(lhs.toString(indent)).append(op.toString(indent))
    s.toString
  }
}

/**
 * Expr ::= Lhs Op Expr
 */
case class AssignOpApp(override val info: ASTNodeInfo, lhs: LHS, op: Op, right: Expr)
    extends Expr(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(lhs.toString(indent)).append(" ")
    s.append(op.toString(indent)).append(" ")
    s.append(right.toString(indent))
    s.toString
  }
}

/**
 * Expr ::= Lhs
 */
abstract class LHS(override val info: ASTNodeInfo)
  extends Expr(info: ASTNodeInfo)

/**
 * Lhs ::= Literal
 */
abstract class Literal(override val info: ASTNodeInfo)
  extends LHS(info: ASTNodeInfo)

/**
 * Literal ::= this
 */
case class This(override val info: ASTNodeInfo)
    extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("this")
    s.toString
  }
}

/**
 * Literal ::= null
 */
case class Null(override val info: ASTNodeInfo)
    extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("null")
    s.toString
  }
}

/**
 * Literal ::= true | false
 */
case class Bool(override val info: ASTNodeInfo, bool: Boolean)
    extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(if (bool) "true" else "false")
    s.toString
  }
}

/**
 * number literal
 */
abstract class NumberLiteral(override val info: ASTNodeInfo)
  extends Literal(info: ASTNodeInfo)

/**
 * float literal
 * e.g.) 3.5
 */
case class DoubleLiteral(override val info: ASTNodeInfo, text: String, num: Double)
    extends NumberLiteral(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(text)
    s.toString
  }
}

/**
 * int literal
 * e.g.) 7
 */
case class IntLiteral(override val info: ASTNodeInfo, intVal: BigInteger, radix: Integer)
    extends NumberLiteral(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(if (radix == 8) "0" + intVal.toString(8)
    else if (radix == 16) "0x" + intVal.toString(16)
    else intVal.toString)
    s.toString
  }
}

/**
 * Literal ::= String
 */
case class StringLiteral(override val info: ASTNodeInfo, quote: String, escaped: String, isRE: Boolean)
    extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(quote)
    NU.ppAST(s, escaped)
    s.append(quote)
    s.toString
  }
}

/**
 * Literal ::= RegularExpression
 */
case class RegularExpression(override val info: ASTNodeInfo, body: String, flag: String)
    extends Literal(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("/" + NU.unescapeJava(body) + "/" + NU.unescapeJava(flag))
    s.toString
  }
}

/**
 * PrimaryExpr ::= Id
 */
case class VarRef(override val info: ASTNodeInfo, id: Id)
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(id.toString(indent))
    s.toString
  }
}

/**
 * PrimaryExpr ::= [ (Expr,)* ]
 */
case class ArrayExpr(override val info: ASTNodeInfo, elements: List[Option[Expr]])
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("[")
    elements.foreach(e => s.append(if (e.isDefined) e.get.toString(indent) else "").append(", "))
    s.append("]")
    s.toString
  }
}

/**
 * PrimaryExpr ::= [ (Number,)* ]
 */
case class ArrayNumberExpr(override val info: ASTNodeInfo, elements: List[Double])
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("[")
    s.append("\"A LOT!!! " + elements.size + " elements are not printed here.\", ")
    s.append("]")
    s.toString
  }
}

/**
 * PrimaryExpr ::= { (Member,)* }
 */
case class ObjectExpr(override val info: ASTNodeInfo, members: List[Member])
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("{").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, members, "," + Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * PrimaryExpr ::= ( Expr )
 */
case class Parenthesized(override val info: ASTNodeInfo, expr: Expr)
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(NU.inParentheses(expr.toString(indent)))
    s.toString
  }
}

/**
 * LHS ::= function Id? ( (Id,)* ) { SourceElement }
 */
case class FunExpr(override val info: ASTNodeInfo, ftn: Functional)
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("(function ")
    if (!NU.isFunExprName(ftn.name.text)) s.append(ftn.name.toString(indent))
    s.append("(")
    s.append(NU.join(indent, ftn.params, ", ", new StringBuilder("")))
    s.append(") ").append(Config.LINE_SEP).append(NU.getIndent(indent)).append("{").append(Config.LINE_SEP)
    NU.prUseStrictDirective(s, indent, ftn.fds, ftn.vds, ftn.stmts)
    NU.prFtn(s, indent, ftn.fds, ftn.vds, ftn.stmts.body)
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("})")
    s.toString
  }
}

/**
 * LHS ::= Lhs [ Expr ]
 */
case class Bracket(override val info: ASTNodeInfo, obj: LHS, index: Expr)
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(obj.toString(indent)).append("[").append(index.toString(indent)).append("]")
    s.toString
  }
}

/**
 * LHS ::= Lhs . Id
 */
case class Dot(override val info: ASTNodeInfo, obj: LHS, member: Id)
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(obj.toString(indent)).append(".").append(member.toString(indent))
    s.toString
  }
}

/**
 * LHS ::= new Lhs
 */
case class New(override val info: ASTNodeInfo, lhs: LHS)
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("new ").append(lhs.toString(indent))
    s.toString
  }
}

/**
 * LHS ::= Lhs ( (Expr,)* )
 */
case class FunApp(override val info: ASTNodeInfo, fun: LHS, args: List[Expr])
    extends LHS(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(fun.toString(indent)).append("(")
    s.append(NU.join(indent, args, ", ", new StringBuilder("")))
    s.append(")")
    s.toString
  }
}

abstract class Property(override val info: ASTNodeInfo)
  extends ASTNode(info: ASTNodeInfo)

/**
 * Property ::= Id
 */
case class PropId(override val info: ASTNodeInfo, id: Id)
    extends Property(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(id.toString(indent))
    s.toString
  }
}

/**
 * Property ::= String
 */
case class PropStr(override val info: ASTNodeInfo, str: String)
    extends Property(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(if (str.equals("\"")) "'\"'" else "\"" + str + "\"")
    s.toString
  }
}

/**
 * Property ::= Number
 */
case class PropNum(override val info: ASTNodeInfo, num: NumberLiteral)
    extends Property(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(num.toString(indent))
    s.toString
  }
}

abstract class Member(override val info: ASTNodeInfo, prop: Property)
  extends ASTNode(info: ASTNodeInfo)

/**
 * Member ::= Property : Expr
 */
case class Field(override val info: ASTNodeInfo, prop: Property, expr: Expr)
    extends Member(info: ASTNodeInfo, prop: Property) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(prop.toString(indent)).append(" : ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Member ::= get Property () { FunctionBody }
 */
case class GetProp(override val info: ASTNodeInfo, prop: Property, ftn: Functional)
    extends Member(info: ASTNodeInfo, prop: Property) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("get ").append(prop.toString(indent)).append("()").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent)).append("{").append(Config.LINE_SEP)
    NU.prUseStrictDirective(s, indent, ftn.fds, ftn.vds, ftn.stmts)
    NU.prFtn(s, indent, ftn.fds, ftn.vds, ftn.stmts.body)
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * Member ::= set Property ( Id ) { SourceElement* }
 */
case class SetProp(override val info: ASTNodeInfo, prop: Property, ftn: Functional)
    extends Member(info: ASTNodeInfo, prop: Property) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append("set ").append(prop.toString(indent)).append("(")
    s.append(ftn.params.head.toString(indent)).append(") ").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent)).append("{").append(Config.LINE_SEP)
    NU.prUseStrictDirective(s, indent, ftn.fds, ftn.vds, ftn.stmts)
    NU.prFtn(s, indent, ftn.fds, ftn.vds, ftn.stmts.body)
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

abstract class Name(override val info: ASTNodeInfo)
  extends ASTNode(info: ASTNodeInfo)

abstract class IdOrOpOrAnonymousName(override val info: ASTNodeInfo)
  extends Name(info: ASTNodeInfo)

abstract class IdOrOp(override val info: ASTNodeInfo, text: String)
  extends IdOrOpOrAnonymousName(info: ASTNodeInfo)

/**
 * Named identifier
 */
case class Id(override val info: ASTNodeInfo, text: String, uniqueName: Option[String] = None, isWith: Boolean)
    extends IdOrOp(info: ASTNodeInfo, text: String) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    if (uniqueName.isDefined && isWith)
      s.append(uniqueName.get.dropRight(NU.significantBits) +
        NU.getNodesE(uniqueName.get.takeRight(NU.significantBits)))
    else s.append(text)
    s.toString
  }
}

/**
 * Infix/prefix/postfix operator
 */
case class Op(override val info: ASTNodeInfo, text: String)
    extends IdOrOp(info: ASTNodeInfo, text: String) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(text)
    s.toString
  }
}

/**
 * Unnamed identifier
 */
case class AnonymousFnName(override val info: ASTNodeInfo, text: String)
    extends IdOrOpOrAnonymousName(info: ASTNodeInfo) {
  override def toString(indent: Int): String = ""
}

/**
 * label
 */
case class Label(override val info: ASTNodeInfo, id: Id)
    extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    if (info.comment.isDefined) s.append(info.comment.get.toString(indent))
    s.append(id.toString(indent))
    s.toString
  }
}

/**
 * comment
 */
case class Comment(override val info: ASTNodeInfo, txt: String)
    extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(txt + Config.LINE_SEP)
    s.toString
  }
}

/**
 * Common body for program and functions
 */
abstract class ScopeBody(override val info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl])
  extends ASTNode(info: ASTNodeInfo)

/**
 * Program top level
 */
case class TopLevel(override val info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl], stmts: List[SourceElements])
    extends ScopeBody(info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl]) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    NU.prUseStrictDirective(s, indent, fds, vds, stmts)
    NU.prFtn(s, indent, fds, vds,
      stmts.foldLeft(List[Stmt]())((l, s) => l ++ s.body.asInstanceOf[List[Stmt]]))
    s.toString
  }
}

/**
 * Common shape for functions
 */
case class Functional(override val info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl], stmts: SourceElements, name: Id, params: List[Id], body: String)
    extends ScopeBody(info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl]) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(name.toString(indent)).append("(")
    s.append(NU.join(indent, params, ", ", new StringBuilder("")))
    s.append(") ").append(Config.LINE_SEP).append(NU.getIndent(indent)).append("{").append(Config.LINE_SEP)
    NU.prUseStrictDirective(s, indent, fds, vds, stmts)
    NU.prFtn(s, indent, fds, vds, stmts.body)
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

trait ASTWalker {
  def walk(info: ASTNodeInfo): ASTNodeInfo = info match {
    case ASTNodeInfo(span, comment) =>
      ASTNodeInfo(span, comment.map(walk))
  }

  def walk(node: ASTNode): ASTNode = node match {
    case p: Program => walk(p)
    case s: SourceElement => walk(s)
    case s: SourceElements => walk(s)
    case v: VarDecl => walk(v)
    case c: Case => walk(c)
    case c: Catch => walk(c)
    case e: Expr => walk(e)
    case p: Property => walk(p)
    case m: Member => walk(m)
    case i: Id => walk(i)
    case o: Op => walk(o)
    case a: AnonymousFnName => walk(a)
    case l: Label => walk(l)
    case c: Comment => walk(c)
    case t: TopLevel => walk(t)
    case f: Functional => walk(f)
  }

  def walk(node: Program): Program = node match {
    case Program(info, body) =>
      Program(walk(info), walk(body))
  }

  def walk(node: Stmt): Stmt = node match {
    case NoOp(info, desc) =>
      NoOp(walk(info), desc)
    case StmtUnit(info, stmts) =>
      StmtUnit(walk(info), stmts.map(walk))
    case fd: FunDecl =>
      walk(fd)
    case ABlock(info, stmts, isInternal) =>
      ABlock(walk(info), stmts.map(walk), isInternal)
    case VarStmt(info, vds) =>
      VarStmt(walk(info), vds.map(walk))
    case EmptyStmt(info) =>
      EmptyStmt(walk(info))
    case ExprStmt(info, expr, isInternal) =>
      ExprStmt(walk(info), walk(expr), isInternal)
    case If(info, cond, trueB, falseB) =>
      If(walk(info), walk(cond), walk(trueB), falseB.map(walk))
    case DoWhile(info, body, cond) =>
      DoWhile(walk(info), walk(body), walk(cond))
    case While(info, cond, body) =>
      While(walk(info), walk(cond), walk(body))
    case For(info, init, cond, action, body) =>
      For(walk(info), init.map(walk), cond.map(walk), action.map(walk), walk(body))
    case ForIn(info, lhs, expr, body) =>
      ForIn(walk(info), walk(lhs), walk(expr), walk(body))
    case ForVar(info, vars, cond, action, body) =>
      ForVar(walk(info), vars.map(walk), cond.map(walk), action.map(walk), walk(body))
    case ForVarIn(info, vari, expr, body) =>
      ForVarIn(walk(info), walk(vari), walk(expr), walk(body))
    case Continue(info, target) =>
      Continue(walk(info), target.map(walk))
    case Break(info, target) =>
      Break(walk(info), target.map(walk))
    case Return(info, expr) =>
      Return(walk(info), expr.map(walk))
    case With(info, expr, stmt) =>
      With(walk(info), walk(expr), walk(stmt))
    case Switch(info, cond, frontCases, defi, backCases) =>
      Switch(walk(info), walk(cond), frontCases.map(walk), defi.map(_.map(walk)), backCases.map(walk))
    case LabelStmt(info, label, stmt) =>
      LabelStmt(walk(info), walk(label), walk(stmt))
    case Throw(info, expr) =>
      Throw(walk(info), walk(expr))
    case Try(info, body, catchBlock, fin) =>
      Try(walk(info), body.map(walk), catchBlock.map(walk), fin.map(_.map(walk)))
    case Debugger(info) =>
      Debugger(walk(info))
  }

  def walk(node: Expr): Expr = node match {
    case ExprList(info, exprs) =>
      ExprList(walk(info), exprs.map(walk))
    case Cond(info, cond, trueB, falseB) =>
      Cond(walk(info), walk(cond), walk(trueB), walk(falseB))
    case InfixOpApp(info, left, op, right) =>
      InfixOpApp(walk(info), walk(left), walk(op), walk(right))
    case PrefixOpApp(info, op, right) =>
      PrefixOpApp(walk(info), walk(op), walk(right))
    case UnaryAssignOpApp(info, lhs, op) =>
      UnaryAssignOpApp(walk(info), walk(lhs), walk(op))
    case AssignOpApp(info, lhs, op, right) =>
      AssignOpApp(walk(info), walk(lhs), walk(op), walk(right))
    case l: LHS =>
      walk(l)
  }

  def walk(node: LHS): LHS = node match {
    case This(info) =>
      This(walk(info))
    case Null(info) =>
      Null(walk(info))
    case Bool(info, isBool) =>
      Bool(walk(info), isBool)
    case n: NumberLiteral =>
      walk(n)
    case StringLiteral(info, quote, escaped, isRE) =>
      StringLiteral(walk(info), quote, escaped, isRE)
    case RegularExpression(info, body, flag) =>
      RegularExpression(walk(info), body, flag)
    case VarRef(info, id) =>
      VarRef(walk(info), walk(id))
    case ArrayExpr(info, elements) =>
      ArrayExpr(walk(info), elements.map(_.map(walk)))
    case ArrayNumberExpr(info, elements) =>
      ArrayNumberExpr(walk(info), elements)
    case ObjectExpr(info, members) =>
      ObjectExpr(walk(info), members.map(walk))
    case Parenthesized(info, expr) =>
      Parenthesized(walk(info), walk(expr))
    case FunExpr(info, ftn) =>
      FunExpr(walk(info), walk(ftn))
    case Bracket(info, obj, index) =>
      Bracket(walk(info), walk(obj), walk(index))
    case Dot(info, obj, member) =>
      Dot(walk(info), walk(obj), walk(member))
    case New(info, lhs) =>
      New(walk(info), walk(lhs))
    case FunApp(info, fun, args) =>
      FunApp(walk(info), walk(fun), args.map(walk))
  }

  def walk(node: NumberLiteral): NumberLiteral = node match {
    case DoubleLiteral(info, text, num) =>
      DoubleLiteral(walk(info), text, num)
    case IntLiteral(info, intVal, radix) =>
      IntLiteral(walk(info), intVal, radix)
  }

  def walk(node: SourceElement): SourceElement = node match {
    case s: Stmt =>
      walk(s)
  }

  def walk(node: SourceElements): SourceElements = node match {
    case SourceElements(info, body, isStrict) =>
      SourceElements(walk(info), body.map(walk), isStrict)
  }

  def walk(node: FunDecl): FunDecl = node match {
    case FunDecl(info, ftn, isStrict) =>
      FunDecl(walk(info), walk(ftn), isStrict)
  }

  def walk(node: VarDecl): VarDecl = node match {
    case VarDecl(info, name, expr, isStrict) =>
      VarDecl(walk(info), walk(name), expr.map(walk), isStrict)
  }

  def walk(node: Case): Case = node match {
    case Case(info, cond, body) =>
      Case(walk(info), walk(cond), body.map(walk))
  }

  def walk(node: Catch): Catch = node match {
    case Catch(info, id, body) =>
      Catch(walk(info), walk(id), body.map(walk))
  }

  def walk(node: Property): Property = node match {
    case PropId(info, id) =>
      PropId(walk(info), walk(id))
    case PropStr(info, str) =>
      PropStr(walk(info), str)
    case PropNum(info, num) =>
      PropNum(walk(info), walk(num))
  }

  def walk(node: Member): Member = node match {
    case Field(info, prop, expr) =>
      Field(walk(info), walk(prop), walk(expr))
    case GetProp(info, prop, ftn) =>
      GetProp(walk(info), walk(prop), walk(ftn))
    case SetProp(info, prop, ftn) =>
      SetProp(walk(info), walk(prop), walk(ftn))
  }

  def walk(node: Id): Id = node match {
    case Id(info, text, uniqueName, isWith) =>
      Id(walk(info), text, uniqueName, isWith)
  }

  def walk(node: Op): Op = node match {
    case Op(info, text) =>
      Op(walk(info), text)
  }

  def walk(node: AnonymousFnName): AnonymousFnName = node match {
    case AnonymousFnName(info, text) =>
      AnonymousFnName(walk(info), text)
  }

  def walk(node: Label): Label = node match {
    case Label(info, id) =>
      Label(walk(info), walk(id))
  }

  def walk(node: Comment): Comment = node match {
    case Comment(info, comment) =>
      Comment(walk(info), comment)
  }

  def walk(node: TopLevel): TopLevel = node match {
    case TopLevel(info, fds, vds, stmts) =>
      TopLevel(walk(info), fds.map(walk), vds.map(walk), stmts.map(walk))
  }

  def walk(node: Functional): Functional = node match {
    case Functional(info, fds, vds, stmts, name, params, body) =>
      Functional(walk(info), fds.map(walk), vds.map(walk), walk(stmts), walk(name),
        params.map(walk), body)
  }
}

trait ASTGeneralWalker[Result] {
  def join(args: Result*): Result

  def walkOptList(opt: Option[List[ASTNode]]): List[Result] = opt match {
    case Some(l) => l.map(walk)
    case _ => List[Result]()
  }

  def walkOpt(opt: Option[ASTNode]): List[Result] =
    opt.fold(List[Result]()) { n: ASTNode =>
      List(n match {
        case s: Stmt => walk(s)
        case e: Expr => walk(e)
        case c: Catch => walk(c)
        case l: Label => walk(l)
      })
    }

  def walk(info: ASTNodeInfo): Result = join()

  def walk(node: ASTNode): Result = node match {
    case p: Program => walk(p)
    case s: SourceElement => walk(s)
    case s: SourceElements => walk(s)
    case v: VarDecl => walk(v)
    case c: Case => walk(c)
    case c: Catch => walk(c)
    case e: Expr => walk(e)
    case p: Property => walk(p)
    case m: Member => walk(m)
    case i: Id => walk(i)
    case o: Op => walk(o)
    case a: AnonymousFnName => walk(a)
    case l: Label => walk(l)
    case c: Comment => walk(c)
    case t: TopLevel => walk(t)
    case f: Functional => walk(f)
  }

  def walk(node: Program): Result = node match {
    case Program(info, body) =>
      join(walk(info), walk(body))
  }

  def walk(node: Stmt): Result = node match {
    case NoOp(info, desc) =>
      walk(info)
    case StmtUnit(info, stmts) =>
      join(walk(info) :: stmts.map(walk): _*)
    case fd: FunDecl =>
      walk(fd)
    case ABlock(info, stmts, isInternal) =>
      join(walk(info) :: stmts.map(walk): _*)
    case VarStmt(info, vds) =>
      join(walk(info) :: vds.map(walk): _*)
    case EmptyStmt(info) =>
      walk(info)
    case ExprStmt(info, expr, isInternal) =>
      join(walk(info), walk(expr))
    case If(info, cond, trueB, falseB) =>
      join(walk(info) :: walk(cond) :: walk(trueB) :: walkOpt(falseB): _*)
    case DoWhile(info, body, cond) =>
      join(walk(info), walk(body), walk(cond))
    case While(info, cond, body) =>
      join(walk(info), walk(cond), walk(body))
    case For(info, init, cond, action, body) =>
      join(walk(info) :: walkOpt(init) ++ walkOpt(cond) ++ walkOpt(action) ++ List(walk(body)): _*)
    case ForIn(info, lhs, expr, body) =>
      join(walk(info), walk(lhs), walk(expr), walk(body))
    case ForVar(info, vars, cond, action, body) =>
      join(walk(info) :: vars.map(walk) ++ walkOpt(cond) ++ walkOpt(action) ++ List(walk(body)): _*)
    case ForVarIn(info, vari, expr, body) =>
      join(walk(info), walk(vari), walk(expr), walk(body))
    case Continue(info, target) =>
      join(walk(info) :: walkOpt(target): _*)
    case Break(info, target) =>
      join(walk(info) :: walkOpt(target): _*)
    case Return(info, expr) =>
      join(walk(info) :: walkOpt(expr): _*)
    case With(info, expr, stmt) =>
      join(walk(info), walk(expr), walk(stmt))
    case Switch(info, cond, frontCases, defi, backCases) =>
      join(walk(info) :: walk(cond) :: frontCases.map(walk) ++ walkOptList(defi) ++ backCases.map(walk): _*)
    case LabelStmt(info, label, stmt) =>
      join(walk(info), walk(label), walk(stmt))
    case Throw(info, expr) =>
      join(walk(info), walk(expr))
    case Try(info, body, catchBlock, fin) =>
      join(walk(info) :: body.map(walk) ++ walkOpt(catchBlock) ++ walkOptList(fin): _*)
    case Debugger(info) =>
      walk(info)
  }

  def walk(node: Expr): Result = node match {
    case ExprList(info, exprs) =>
      join(walk(info) :: exprs.map(walk): _*)
    case Cond(info, cond, trueB, falseB) =>
      join(walk(info), walk(cond), walk(trueB), walk(falseB))
    case InfixOpApp(info, left, op, right) =>
      join(walk(info), walk(left), walk(op), walk(right))
    case PrefixOpApp(info, op, right) =>
      join(walk(info), walk(op), walk(right))
    case UnaryAssignOpApp(info, lhs, op) =>
      join(walk(info), walk(lhs), walk(op))
    case AssignOpApp(info, lhs, op, right) =>
      join(walk(info), walk(lhs), walk(op), walk(right))
    case l: LHS =>
      walk(l)
  }

  def walk(node: LHS): Result = node match {
    case This(info) =>
      walk(info)
    case Null(info) =>
      walk(info)
    case Bool(info, isBool) =>
      walk(info)
    case n: NumberLiteral =>
      walk(n)
    case StringLiteral(info, quote, escaped, isRE) =>
      walk(info)
    case RegularExpression(info, body, flag) =>
      walk(info)
    case VarRef(info, id) =>
      join(walk(info), walk(id))
    case ArrayExpr(info, elements) =>
      join(walk(info) :: elements.flatMap(walkOpt): _*)
    case ArrayNumberExpr(info, elements) =>
      walk(info)
    case ObjectExpr(info, members) =>
      join(walk(info) :: members.map(walk): _*)
    case Parenthesized(info, expr) =>
      join(walk(info), walk(expr))
    case FunExpr(info, ftn) =>
      join(walk(info), walk(ftn))
    case Bracket(info, obj, index) =>
      join(walk(info), walk(obj), walk(index))
    case Dot(info, obj, member) =>
      join(walk(info), walk(obj), walk(member))
    case New(info, lhs) =>
      join(walk(info), walk(lhs))
    case FunApp(info, fun, args) =>
      join(walk(info) :: walk(fun) :: args.map(walk): _*)
  }

  def walk(node: NumberLiteral): Result = node match {
    case DoubleLiteral(info, text, num) =>
      walk(info)
    case IntLiteral(info, intVal, radix) =>
      walk(info)
  }

  def walk(node: SourceElement): Result = node match {
    case s: Stmt =>
      walk(s)
  }

  def walk(node: SourceElements): Result = node match {
    case SourceElements(info, body, isStrict) =>
      join(walk(info) :: body.map(walk): _*)
  }

  def walk(node: FunDecl): Result = node match {
    case FunDecl(info, ftn, isStrict) =>
      join(walk(info), walk(ftn))
  }

  def walk(node: VarDecl): Result = node match {
    case VarDecl(info, name, expr, isStrict) =>
      join(walk(info) :: walk(name) :: walkOpt(expr): _*)
  }

  def walk(node: Case): Result = node match {
    case Case(info, cond, body) =>
      join(walk(info) :: walk(cond) :: body.map(walk): _*)
  }

  def walk(node: Catch): Result = node match {
    case Catch(info, id, body) =>
      join(walk(info) :: walk(id) :: body.map(walk): _*)
  }

  def walk(node: Property): Result = node match {
    case PropId(info, id) =>
      join(walk(info), walk(id))
    case PropStr(info, str) =>
      walk(info)
    case PropNum(info, num) =>
      join(walk(info), walk(num))
  }

  def walk(node: Member): Result = node match {
    case Field(info, prop, expr) =>
      join(walk(info), walk(prop), walk(expr))
    case GetProp(info, prop, ftn) =>
      join(walk(info), walk(prop), walk(ftn))
    case SetProp(info, prop, ftn) =>
      join(walk(info), walk(prop), walk(ftn))
  }

  def walk(node: Id): Result = node match {
    case Id(info, text, uniqueName, isWith) =>
      walk(info)
  }

  def walk(node: Op): Result = node match {
    case Op(info, text) =>
      walk(info)
  }

  def walk(node: AnonymousFnName): Result = node match {
    case AnonymousFnName(info, text) =>
      walk(info)
  }

  def walk(node: Label): Result = node match {
    case Label(info, id) =>
      join(walk(info), walk(id))
  }

  def walk(node: Comment): Result = node match {
    case Comment(info, comment) =>
      walk(info)
  }

  def walk(node: TopLevel): Result = node match {
    case TopLevel(info, fds, vds, stmts) =>
      join(walk(info) :: fds.map(walk) ++ vds.map(walk) ++ stmts.map(walk): _*)
  }

  def walk(node: Functional): Result = node match {
    case Functional(info, fds, vds, stmts, name, params, body) =>
      join(walk(info) :: fds.map(walk) ++ vds.map(walk) ++ List(walk(stmts), walk(name)) ++ params.map(walk): _*)
  }
}

class ASTUnitWalker extends ASTGeneralWalker[Unit] {
  def join(args: Unit*): Unit = {}
}
