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
      var oneline: Boolean = NU.isOneline(fin)
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
    var oneline: Boolean = NU.isOneline(body)
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
  def walk(node: Any): Any = {
    node match {
      case ASTNodeInfo(span, comment) =>
        ASTNodeInfo(walk(span).asInstanceOf[Span], walk(comment).asInstanceOf[Option[Comment]])
      case AnonymousFnName(info, text) =>
        AnonymousFnName(walk(info).asInstanceOf[ASTNodeInfo], walk(text).asInstanceOf[String])
      case ArrayExpr(info, elements) =>
        ArrayExpr(walk(info).asInstanceOf[ASTNodeInfo], walk(elements).asInstanceOf[List[Option[Expr]]])
      case ArrayNumberExpr(info, elements) =>
        ArrayNumberExpr(walk(info).asInstanceOf[ASTNodeInfo], walk(elements).asInstanceOf[List[Double]])
      case AssignOpApp(info, lhs, op, right) =>
        AssignOpApp(walk(info).asInstanceOf[ASTNodeInfo], walk(lhs).asInstanceOf[LHS], walk(op).asInstanceOf[Op], walk(right).asInstanceOf[Expr])
      case ABlock(info, stmts, isInternal) =>
        ABlock(walk(info).asInstanceOf[ASTNodeInfo], walk(stmts).asInstanceOf[List[Stmt]], walk(isInternal).asInstanceOf[Boolean])
      case Bool(info, isBool) =>
        Bool(walk(info).asInstanceOf[ASTNodeInfo], walk(isBool).asInstanceOf[Boolean])
      case Bracket(info, obj, index) =>
        Bracket(walk(info).asInstanceOf[ASTNodeInfo], walk(obj).asInstanceOf[LHS], walk(index).asInstanceOf[Expr])
      case Break(info, target) =>
        Break(walk(info).asInstanceOf[ASTNodeInfo], walk(target).asInstanceOf[Option[Label]])
      case Case(info, cond, body) =>
        Case(walk(info).asInstanceOf[ASTNodeInfo], walk(cond).asInstanceOf[Expr], walk(body).asInstanceOf[List[Stmt]])
      case Catch(info, id, body) =>
        Catch(walk(info).asInstanceOf[ASTNodeInfo], walk(id).asInstanceOf[Id], walk(body).asInstanceOf[List[Stmt]])
      case Comment(info, comment) =>
        Comment(walk(info).asInstanceOf[ASTNodeInfo], walk(comment).asInstanceOf[String])
      case Cond(info, cond, trueB, falseB) =>
        Cond(walk(info).asInstanceOf[ASTNodeInfo], walk(cond).asInstanceOf[Expr], walk(trueB).asInstanceOf[Expr], walk(falseB).asInstanceOf[Expr])
      case Continue(info, target) =>
        Continue(walk(info).asInstanceOf[ASTNodeInfo], walk(target).asInstanceOf[Option[Label]])
      case Debugger(info) =>
        Debugger(walk(info).asInstanceOf[ASTNodeInfo])
      case DoWhile(info, body, cond) =>
        DoWhile(walk(info).asInstanceOf[ASTNodeInfo], walk(body).asInstanceOf[Stmt], walk(cond).asInstanceOf[Expr])
      case Dot(info, obj, member) =>
        Dot(walk(info).asInstanceOf[ASTNodeInfo], walk(obj).asInstanceOf[LHS], walk(member).asInstanceOf[Id])
      case DoubleLiteral(info, text, num) =>
        DoubleLiteral(walk(info).asInstanceOf[ASTNodeInfo], walk(text).asInstanceOf[String], walk(num).asInstanceOf[Double])
      case EmptyStmt(info) =>
        EmptyStmt(walk(info).asInstanceOf[ASTNodeInfo])
      case ExprList(info, exprs) =>
        ExprList(walk(info).asInstanceOf[ASTNodeInfo], walk(exprs).asInstanceOf[List[Expr]])
      case ExprStmt(info, expr, isInternal) =>
        ExprStmt(walk(info).asInstanceOf[ASTNodeInfo], walk(expr).asInstanceOf[Expr], walk(isInternal).asInstanceOf[Boolean])
      case Field(info, prop, expr) =>
        Field(walk(info).asInstanceOf[ASTNodeInfo], walk(prop).asInstanceOf[Property], walk(expr).asInstanceOf[Expr])
      case For(info, init, cond, action, body) =>
        For(walk(info).asInstanceOf[ASTNodeInfo], walk(init).asInstanceOf[Option[Expr]], walk(cond).asInstanceOf[Option[Expr]], walk(action).asInstanceOf[Option[Expr]], walk(body).asInstanceOf[Stmt])
      case ForIn(info, lhs, expr, body) =>
        ForIn(walk(info).asInstanceOf[ASTNodeInfo], walk(lhs).asInstanceOf[LHS], walk(expr).asInstanceOf[Expr], walk(body).asInstanceOf[Stmt])
      case ForVar(info, vars, cond, action, body) =>
        ForVar(walk(info).asInstanceOf[ASTNodeInfo], walk(vars).asInstanceOf[List[VarDecl]], walk(cond).asInstanceOf[Option[Expr]], walk(action).asInstanceOf[Option[Expr]], walk(body).asInstanceOf[Stmt])
      case ForVarIn(info, vari, expr, body) =>
        ForVarIn(walk(info).asInstanceOf[ASTNodeInfo], walk(vari).asInstanceOf[VarDecl], walk(expr).asInstanceOf[Expr], walk(body).asInstanceOf[Stmt])
      case FunApp(info, fun, args) =>
        FunApp(walk(info).asInstanceOf[ASTNodeInfo], walk(fun).asInstanceOf[LHS], walk(args).asInstanceOf[List[Expr]])
      case FunDecl(info, ftn, isStrict) =>
        FunDecl(walk(info).asInstanceOf[ASTNodeInfo], walk(ftn).asInstanceOf[Functional], walk(isStrict).asInstanceOf[Boolean])
      case FunExpr(info, ftn) =>
        FunExpr(walk(info).asInstanceOf[ASTNodeInfo], walk(ftn).asInstanceOf[Functional])
      case Functional(info, fds, vds, stmts, name, params, body) =>
        Functional(walk(info).asInstanceOf[ASTNodeInfo], walk(fds).asInstanceOf[List[FunDecl]], walk(vds).asInstanceOf[List[VarDecl]], walk(stmts).asInstanceOf[SourceElements], walk(name).asInstanceOf[Id], walk(params).asInstanceOf[List[Id]], walk(body).asInstanceOf[String])
      case GetProp(info, prop, ftn) =>
        GetProp(walk(info).asInstanceOf[ASTNodeInfo], walk(prop).asInstanceOf[Property], walk(ftn).asInstanceOf[Functional])
      case Id(info, text, uniqueName, isWith) =>
        Id(walk(info).asInstanceOf[ASTNodeInfo], walk(text).asInstanceOf[String], walk(uniqueName).asInstanceOf[Option[String]], walk(isWith).asInstanceOf[Boolean])
      case If(info, cond, trueB, falseB) =>
        If(walk(info).asInstanceOf[ASTNodeInfo], walk(cond).asInstanceOf[Expr], walk(trueB).asInstanceOf[Stmt], walk(falseB).asInstanceOf[Option[Stmt]])
      case InfixOpApp(info, left, op, right) =>
        InfixOpApp(walk(info).asInstanceOf[ASTNodeInfo], walk(left).asInstanceOf[Expr], walk(op).asInstanceOf[Op], walk(right).asInstanceOf[Expr])
      case IntLiteral(info, intVal, radix) =>
        IntLiteral(walk(info).asInstanceOf[ASTNodeInfo], walk(intVal).asInstanceOf[BigInteger], walk(radix).asInstanceOf[Int])
      case Label(info, id) =>
        Label(walk(info).asInstanceOf[ASTNodeInfo], walk(id).asInstanceOf[Id])
      case LabelStmt(info, label, stmt) =>
        LabelStmt(walk(info).asInstanceOf[ASTNodeInfo], walk(label).asInstanceOf[Label], walk(stmt).asInstanceOf[Stmt])
      case New(info, lhs) =>
        New(walk(info).asInstanceOf[ASTNodeInfo], walk(lhs).asInstanceOf[LHS])
      case NoOp(info, desc) =>
        NoOp(walk(info).asInstanceOf[ASTNodeInfo], walk(desc).asInstanceOf[String])
      case Null(info) =>
        Null(walk(info).asInstanceOf[ASTNodeInfo])
      case ObjectExpr(info, members) =>
        ObjectExpr(walk(info).asInstanceOf[ASTNodeInfo], walk(members).asInstanceOf[List[Member]])
      case Op(info, text) =>
        Op(walk(info).asInstanceOf[ASTNodeInfo], walk(text).asInstanceOf[String])
      case Parenthesized(info, expr) =>
        Parenthesized(walk(info).asInstanceOf[ASTNodeInfo], walk(expr).asInstanceOf[Expr])
      case PrefixOpApp(info, op, right) =>
        PrefixOpApp(walk(info).asInstanceOf[ASTNodeInfo], walk(op).asInstanceOf[Op], walk(right).asInstanceOf[Expr])
      case Program(info, body) =>
        Program(walk(info).asInstanceOf[ASTNodeInfo], walk(body).asInstanceOf[TopLevel])
      case PropId(info, id) =>
        PropId(walk(info).asInstanceOf[ASTNodeInfo], walk(id).asInstanceOf[Id])
      case PropNum(info, num) =>
        PropNum(walk(info).asInstanceOf[ASTNodeInfo], walk(num).asInstanceOf[NumberLiteral])
      case PropStr(info, str) =>
        PropStr(walk(info).asInstanceOf[ASTNodeInfo], walk(str).asInstanceOf[String])
      case RegularExpression(info, body, flag) =>
        RegularExpression(walk(info).asInstanceOf[ASTNodeInfo], walk(body).asInstanceOf[String], walk(flag).asInstanceOf[String])
      case Return(info, expr) =>
        Return(walk(info).asInstanceOf[ASTNodeInfo], walk(expr).asInstanceOf[Option[Expr]])
      case SetProp(info, prop, ftn) =>
        SetProp(walk(info).asInstanceOf[ASTNodeInfo], walk(prop).asInstanceOf[Property], walk(ftn).asInstanceOf[Functional])
      case SourceElements(info, body, isStrict) =>
        SourceElements(walk(info).asInstanceOf[ASTNodeInfo], walk(body).asInstanceOf[List[SourceElement]], walk(isStrict).asInstanceOf[Boolean])
      case StmtUnit(info, stmts) =>
        StmtUnit(walk(info).asInstanceOf[ASTNodeInfo], walk(stmts).asInstanceOf[List[Stmt]])
      case StringLiteral(info, quote, escaped, isRE) =>
        StringLiteral(walk(info).asInstanceOf[ASTNodeInfo], walk(quote).asInstanceOf[String], walk(escaped).asInstanceOf[String], isRE)
      case Switch(info, cond, frontCases, defi, backCases) =>
        Switch(walk(info).asInstanceOf[ASTNodeInfo], walk(cond).asInstanceOf[Expr], walk(frontCases).asInstanceOf[List[Case]], walk(defi).asInstanceOf[Option[List[Stmt]]], walk(backCases).asInstanceOf[List[Case]])
      case This(info) =>
        This(walk(info).asInstanceOf[ASTNodeInfo])
      case Throw(info, expr) =>
        Throw(walk(info).asInstanceOf[ASTNodeInfo], walk(expr).asInstanceOf[Expr])
      case TopLevel(info, fds, vds, stmts) =>
        TopLevel(walk(info).asInstanceOf[ASTNodeInfo], walk(fds).asInstanceOf[List[FunDecl]], walk(vds).asInstanceOf[List[VarDecl]], walk(stmts).asInstanceOf[List[SourceElements]])
      case Try(info, body, catchBlock, fin) =>
        Try(walk(info).asInstanceOf[ASTNodeInfo], walk(body).asInstanceOf[List[Stmt]], walk(catchBlock).asInstanceOf[Option[Catch]], walk(fin).asInstanceOf[Option[List[Stmt]]])
      case UnaryAssignOpApp(info, lhs, op) =>
        UnaryAssignOpApp(walk(info).asInstanceOf[ASTNodeInfo], walk(lhs).asInstanceOf[LHS], walk(op).asInstanceOf[Op])
      case VarDecl(info, name, expr, isStrict) =>
        VarDecl(walk(info).asInstanceOf[ASTNodeInfo], walk(name).asInstanceOf[Id], walk(expr).asInstanceOf[Option[Expr]], walk(isStrict).asInstanceOf[Boolean])
      case VarRef(info, id) =>
        VarRef(walk(info).asInstanceOf[ASTNodeInfo], walk(id).asInstanceOf[Id])
      case VarStmt(info, vds) =>
        VarStmt(walk(info).asInstanceOf[ASTNodeInfo], walk(vds).asInstanceOf[List[VarDecl]])
      case While(info, cond, body) =>
        While(walk(info).asInstanceOf[ASTNodeInfo], walk(cond).asInstanceOf[Expr], walk(body).asInstanceOf[Stmt])
      case With(info, expr, stmt) =>
        With(walk(info).asInstanceOf[ASTNodeInfo], walk(expr).asInstanceOf[Expr], walk(stmt).asInstanceOf[Stmt])
      case xs: List[_] => xs.map(walk _)
      case xs: Option[_] => xs.map(walk _)
      case _ => node
    }
  }
  def walkUnit(node: Any): Unit = {
    node match {
      case ASTNodeInfo(span, comment) =>
        walkUnit(span); walkUnit(comment)
      case AnonymousFnName(info, text) =>
        walkUnit(info, text)
      case ArrayExpr(info, elements) =>
        walkUnit(info); walkUnit(elements)
      case ArrayNumberExpr(info, elements) =>
        walkUnit(info); walkUnit(elements)
      case AssignOpApp(info, lhs, op, right) =>
        walkUnit(info); walkUnit(lhs); walkUnit(op); walkUnit(right)
      case ABlock(info, stmts, isInternal) =>
        walkUnit(info); walkUnit(stmts); walkUnit(isInternal)
      case Bool(info, isBool) =>
        walkUnit(info); walkUnit(isBool)
      case Bracket(info, obj, index) =>
        walkUnit(info); walkUnit(obj); walkUnit(index)
      case Break(info, target) =>
        walkUnit(info); walkUnit(target)
      case Case(info, cond, body) =>
        walkUnit(info); walkUnit(cond); walkUnit(body)
      case Catch(info, id, body) =>
        walkUnit(info); walkUnit(id); walkUnit(body)
      case Comment(info, comment) =>
        walkUnit(info); walkUnit(comment)
      case Cond(info, cond, trueB, falseB) =>
        walkUnit(info); walkUnit(cond); walkUnit(trueB); walkUnit(falseB)
      case Continue(info, target) =>
        walkUnit(info); walkUnit(target)
      case Debugger(info) =>
        walkUnit(info)
      case DoWhile(info, body, cond) =>
        walkUnit(info); walkUnit(body); walkUnit(cond)
      case Dot(info, obj, member) =>
        walkUnit(info); walkUnit(obj); walkUnit(member)
      case DoubleLiteral(info, text, num) =>
        walkUnit(info); walkUnit(text); walkUnit(num)
      case EmptyStmt(info) =>
        walkUnit(info)
      case ExprList(info, exprs) =>
        walkUnit(info); walkUnit(exprs)
      case ExprStmt(info, expr, isInternal) =>
        walkUnit(info); walkUnit(expr); walkUnit(isInternal)
      case Field(info, prop, expr) =>
        walkUnit(info); walkUnit(prop); walkUnit(expr)
      case For(info, init, cond, action, body) =>
        walkUnit(info); walkUnit(init); walkUnit(cond); walkUnit(action); walkUnit(body)
      case ForIn(info, lhs, expr, body) =>
        walkUnit(info); walkUnit(lhs); walkUnit(expr); walkUnit(body)
      case ForVar(info, vars, cond, action, body) =>
        walkUnit(info); walkUnit(vars); walkUnit(cond); walkUnit(action); walkUnit(body)
      case ForVarIn(info, vari, expr, body) =>
        walkUnit(info); walkUnit(vari); walkUnit(expr); walkUnit(body)
      case FunApp(info, fun, args) =>
        walkUnit(info); walkUnit(fun); walkUnit(args)
      case FunDecl(info, ftn, isStrict) =>
        walkUnit(info); walkUnit(ftn); walkUnit(isStrict)
      case FunExpr(info, ftn) =>
        walkUnit(info); walkUnit(ftn)
      case Functional(info, fds, vds, stmts, name, params, body) =>
        walkUnit(info); walkUnit(fds); walkUnit(vds); walkUnit(stmts); walkUnit(name); walkUnit(params); walkUnit(body)
      case GetProp(info, prop, ftn) =>
        walkUnit(info); walkUnit(prop); walkUnit(ftn)
      case Id(info, text, uniqueName, isWith) =>
        walkUnit(info); walkUnit(text); walkUnit(uniqueName); walkUnit(isWith)
      case If(info, cond, trueB, falseB) =>
        walkUnit(info); walkUnit(cond); walkUnit(trueB); walkUnit(falseB)
      case InfixOpApp(info, left, op, right) =>
        walkUnit(info); walkUnit(left); walkUnit(op); walkUnit(right)
      case IntLiteral(info, intVal, radix) =>
        walkUnit(info); walkUnit(intVal); walkUnit(radix)
      case Label(info, id) =>
        walkUnit(info); walkUnit(id)
      case LabelStmt(info, label, stmt) =>
        walkUnit(info); walkUnit(label); walkUnit(stmt)
      case New(info, lhs) =>
        walkUnit(info); walkUnit(lhs)
      case NoOp(info, desc) =>
        walkUnit(info); walkUnit(desc)
      case Null(info) =>
        walkUnit(info)
      case ObjectExpr(info, members) =>
        walkUnit(info); walkUnit(members)
      case Op(info, text) =>
        walkUnit(info); walkUnit(text)
      case Parenthesized(info, expr) =>
        walkUnit(info); walkUnit(expr)
      case PrefixOpApp(info, op, right) =>
        walkUnit(info); walkUnit(op); walkUnit(right)
      case Program(info, body) =>
        walkUnit(info); walkUnit(body)
      case PropId(info, id) =>
        walkUnit(info); walkUnit(id)
      case PropNum(info, num) =>
        walkUnit(info); walkUnit(num)
      case PropStr(info, str) =>
        walkUnit(info); walkUnit(str)
      case RegularExpression(info, body, flag) =>
        walkUnit(info); walkUnit(body); walkUnit(flag)
      case Return(info, expr) =>
        walkUnit(info); walkUnit(expr)
      case SetProp(info, prop, ftn) =>
        walkUnit(info); walkUnit(prop); walkUnit(ftn)
      case SourceElements(info, body, isStrict) =>
        walkUnit(info); walkUnit(body); walkUnit(isStrict)
      case StmtUnit(info, stmts) =>
        walkUnit(info); walkUnit(stmts)
      case StringLiteral(info, quote, escaped, _) =>
        walkUnit(info); walkUnit(quote); walkUnit(escaped)
      case Switch(info, cond, frontCases, defi, backCases) =>
        walkUnit(info); walkUnit(cond); walkUnit(frontCases); walkUnit(defi); walkUnit(backCases)
      case This(info) =>
        walkUnit(info)
      case Throw(info, expr) =>
        walkUnit(info); walkUnit(expr)
      case TopLevel(info, fds, vds, stmts) =>
        walkUnit(info); walkUnit(fds); walkUnit(vds); walkUnit(stmts)
      case Try(info, body, catchBlock, fin) =>
        walkUnit(info); walkUnit(body); walkUnit(catchBlock); walkUnit(fin)
      case UnaryAssignOpApp(info, lhs, op) =>
        walkUnit(info); walkUnit(lhs); walkUnit(op)
      case VarDecl(info, name, expr, isStrict) =>
        walkUnit(info); walkUnit(name); walkUnit(expr); walkUnit(isStrict)
      case VarRef(info, id) =>
        walkUnit(info); walkUnit(id)
      case VarStmt(info, vds) =>
        walkUnit(info); walkUnit(vds)
      case While(info, cond, body) =>
        walkUnit(info); walkUnit(cond); walkUnit(body)
      case With(info, expr, stmt) =>
        walkUnit(info); walkUnit(expr); walkUnit(stmt)
      case xs: List[_] => xs.foreach(walkUnit _)
      case xs: Option[_] => xs.foreach(walkUnit _)
      case _: Span =>
      case _ =>
    }
  }
}
