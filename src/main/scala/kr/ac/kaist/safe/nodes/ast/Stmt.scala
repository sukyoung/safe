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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

// SourceElement ::= Stmt
abstract class Stmt(
    override val info: ASTNodeInfo
) extends SourceElement(info: ASTNodeInfo) {
  def getIndent(indent: Int): Int = indent + 1
}

/**
 * Internally generated NoOperation
 * currently to denote the end of a file by Shell
 * Do not appear in the JavaScript source text
 */
case class NoOp(
    override val info: ASTNodeInfo,
    desc: String
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = ""
}

/**
 * Internally generated statement unit by Hoister
 * Do not appear in the JavaScript source text
 */
case class StmtUnit(
    override val info: ASTNodeInfo,
    stmts: List[Stmt]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("{")
      .append(LINE_SEP)
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1, stmts,
        LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("}")
    s.toString
  }
}

// Stmt ::= function Id ( (Id,)* ) { SourceElement* }
case class FunDecl(
    override val info: ASTNodeInfo,
    ftn: Functional,
    strict: Boolean
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("function ")
      .append(ftn.toString(indent))
    s.toString
  }
}

// Stmt ::= { Stmt* }
case class ABlock(
    override val info: ASTNodeInfo,
    stmts: List[Stmt],
    internal: Boolean
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("{")
      .append(LINE_SEP)
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1,
        stmts,
        LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("}")
    s.toString
  }
  override def getIndent(indent: Int): Int = indent
}

// Stmt ::= var VarDecl(, VarDecl)* ;
case class VarStmt(
    override val info: ASTNodeInfo,
    vds: List[VarDecl]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    vds match {
      case Nil => s.toString
      case _ =>
        s.append("var ")
          .append(NU.join(indent, vds, ", ", new StringBuilder("")))
          .append(";")
        s.toString
    }
  }
}

// Stmt ::= Id (= Expr)?
case class VarDecl(
    override val info: ASTNodeInfo,
    name: Id,
    expr: Option[Expr],
    strict: Boolean
) extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(name.toString(indent))
    expr.map(e => s.append(" = ").append(e.toString(indent)))
    s.toString
  }
}

// Stmt ::= ;
case class EmptyStmt(
    override val info: ASTNodeInfo
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(";")
    s.toString
  }
}

// Stmt ::= Expr ;
case class ExprStmt(
    override val info: ASTNodeInfo,
    expr: Expr,
    internal: Boolean
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(expr.toString(indent) + ";")
    s.toString
  }
}

// Stmt ::= if ( Expr ) Stmt (else Stmt)?
case class If(
    override val info: ASTNodeInfo,
    cond: Expr,
    trueBranch: Stmt,
    falseBranch: Option[Stmt]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    val trueIndent = trueBranch.getIndent(indent)
    s.append("if (")
      .append(cond.toString(indent))
      .append(")")
      .append(LINE_SEP)
      .append(NU.getIndent(trueIndent))
      .append(trueBranch.toString(trueIndent))
    falseBranch match {
      case Some(fb) =>
        val falseIndent = fb.getIndent(indent)
        s.append(LINE_SEP)
          .append(NU.getIndent(indent))
          .append("else")
          .append(LINE_SEP)
          .append(NU.getIndent(falseIndent))
          .append(fb.toString(falseIndent))
      case _ =>
    }
    s.toString
  }
}

// Stmt ::= do Stmt while ( Expr ) ;
case class DoWhile(
    override val info: ASTNodeInfo,
    body: Stmt,
    cond: Expr
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    var bodyIndent = body.getIndent(indent)
    s.append("do")
      .append(LINE_SEP)
      .append(NU.getIndent(bodyIndent))
      .append(body.toString(bodyIndent))
      .append("while (")
      .append(cond.toString(indent))
      .append(");")
    s.toString
  }
}

// Stmt ::= while ( Expr ) Stmt
case class While(
    override val info: ASTNodeInfo,
    cond: Expr,
    body: Stmt
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    val bodyIndent = body.getIndent(indent)
    s.append("while (")
    s.append(cond.toString(indent))
      .append(")")
      .append(LINE_SEP)
      .append(NU.getIndent(bodyIndent))
      .append(body.toString(bodyIndent))
    s.toString
  }
}

// Stmt ::= for ( Expr? ; Expr? ; Expr? ) Stmt
case class For(
    override val info: ASTNodeInfo,
    init: Option[Expr],
    cond: Option[Expr],
    action: Option[Expr],
    body: Stmt
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    val bodyIndent = body.getIndent(indent)
    s.append("for (")
    init.map(i => s.append(i.toString(indent)))
    s.append(";")
    cond.map(c => s.append(c.toString(indent)))
    s.append(";")
    action.map(a => s.append(a.toString(indent)))
    s.append(")")
      .append(LINE_SEP)
      .append(NU.getIndent(bodyIndent))
      .append(body.toString(bodyIndent))
    s.toString
  }
}

// Stmt ::= for ( lhs in Expr ) Stmt
case class ForIn(
    override val info: ASTNodeInfo,
    lhs: LHS,
    expr: Expr,
    body: Stmt
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    val bodyIndent = body.getIndent(indent)
    s.append("for (")
      .append(lhs.toString(indent))
      .append(" in ")
      .append(expr.toString(indent))
      .append(")")
      .append(LINE_SEP)
      .append(NU.getIndent(bodyIndent))
      .append(body.toString(bodyIndent))
    s.toString
  }
}

// Stmt ::= for ( var VarDecl(, VarDecl)* ; Expr? ; Expr? ) Stmt
case class ForVar(
    override val info: ASTNodeInfo,
    vars: List[VarDecl],
    cond: Option[Expr],
    action: Option[Expr],
    body: Stmt
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    val bodyIndent = body.getIndent(indent)
    s.append("for(var ")
      .append(NU.join(
        indent,
        vars,
        ", ",
        new StringBuilder("")
      ))
      .append(";")
    cond.map(c => s.append(c.toString(indent)))
    s.append(";")
    action.map(a => s.append(a.toString(indent)))
    s.append(")")
      .append(LINE_SEP)
      .append(NU.getIndent(bodyIndent))
      .append(body.toString(bodyIndent))
    s.toString
  }
}

// Stmt ::= for ( var VarDecl in Expr ) Stmt
case class ForVarIn(
    override val info: ASTNodeInfo,
    vd: VarDecl,
    expr: Expr,
    body: Stmt
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    val bodyIndent = body.getIndent(indent)
    s.append("for(var ")
      .append(vd.toString(indent))
      .append(" in ")
      .append(expr.toString(indent))
      .append(")")
      .append(LINE_SEP)
      .append(NU.getIndent(bodyIndent))
      .append(body.toString(bodyIndent))
    s.toString
  }
}

// Stmt ::= continue Label? ;
case class Continue(
    override val info: ASTNodeInfo,
    target: Option[Label]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("continue")
    target.map(t => s.append(" ").append(t.toString(indent)))
    s.append(";")
    s.toString
  }
}

// Stmt ::= break Label? ;
case class Break(
    override val info: ASTNodeInfo,
    target: Option[Label]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("break")
    target.map(t => s.append(" ").append(t.toString(indent)))
    s.append(";")
    s.toString
  }
}

// Stmt ::= return Expr? ;
case class Return(
    override val info: ASTNodeInfo,
    expr: Option[Expr]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("return")
    expr.map(e => s.append(" ").append(e.toString(indent)))
    s.append(";")
    s.toString
  }
}

// Stmt ::= with ( Expr ) Stmt
case class With(
    override val info: ASTNodeInfo,
    expr: Expr,
    stmt: Stmt
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    val stmtIndent = stmt.getIndent(indent)
    s.append("with (")
      .append(expr.toString(indent))
      .append(")")
      .append(LINE_SEP)
      .append(NU.getIndent(stmtIndent))
      .append(stmt.toString(stmtIndent))
    s.toString
  }
}

// Stmt ::= switch ( Expr ) { CaseClause* (default : Stmt*)? CaseClause* }
case class Switch(
    override val info: ASTNodeInfo,
    cond: Expr,
    frontCases: List[Case],
    defopt: Option[List[Stmt]],
    backCases: List[Case]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("switch (")
      .append(cond.toString(indent))
      .append("){")
      .append(LINE_SEP)
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1,
        frontCases,
        LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
    defopt.map(d => {
      s.append(LINE_SEP)
        .append(NU.getIndent(indent + 1))
        .append("default:")
        .append(LINE_SEP)
        .append(NU.getIndent(indent + 2))
        .append(NU.join(
          indent + 2,
          d,
          LINE_SEP + NU.getIndent(indent + 2),
          new StringBuilder("")
        ))
    })
    s.append(LINE_SEP)
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1,
        backCases,
        LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("}")
    s.toString
  }
}

// CaseClause ::= case Expr : Stmt*
case class Case(
    override val info: ASTNodeInfo,
    cond: Expr,
    body: List[Stmt]
) extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("case ")
      .append(cond.toString(indent))
      .append(":")
      .append(LINE_SEP)
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1,
        body,
        LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
      .append(LINE_SEP)
    s.toString
  }
}

// Stmt ::= Label : Stmt
case class LabelStmt(
    override val info: ASTNodeInfo,
    label: Label,
    stmt: Stmt
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(label.toString(indent))
      .append(" : ")
      .append(stmt.toString(indent))
    s.toString
  }
}

// Stmt ::= throw Expr ;
case class Throw(
    override val info: ASTNodeInfo,
    expr: Expr
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("throw ")
      .append(expr.toString(indent))
      .append(";")
    s.toString
  }
}

// Stmt ::= try { Stmt* } (catch ( Id ) { Stmt* })? (finally { Stmt* })?
case class Try(
    override val info: ASTNodeInfo,
    body: List[Stmt],
    catchBlock: Option[Catch],
    fin: Option[List[Stmt]]
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("try")
      .append(LINE_SEP)
      .append("{")
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1,
        body,
        LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
      .append("}")
    catchBlock.map(c => {
      s.append(LINE_SEP)
        .append(NU.getIndent(indent))
        .append(c.toString(indent))
    })
    fin.map(f => {
      s.append(LINE_SEP)
        .append(NU.getIndent(indent))
        .append("finally")
        .append(LINE_SEP)
        .append("{")
        .append(NU.getIndent(indent + 1))
        .append(NU.join(
          indent + 1,
          f,
          LINE_SEP + NU.getIndent(indent + 1),
          new StringBuilder("")
        ))
        .append("}")
        .append(LINE_SEP)
    })
    s.toString
  }
}

// Catch ::= catch ( Id ) { Stmt* }
case class Catch(
    override val info: ASTNodeInfo,
    id: Id,
    body: List[Stmt]
) extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("catch (")
      .append(id.toString(indent))
      .append(")")
      .append(LINE_SEP)
      .append("{")
      .append(NU.getIndent(indent + 1))
      .append(NU.join(
        indent + 1,
        body,
        LINE_SEP + NU.getIndent(indent + 1),
        new StringBuilder("")
      ))
      .append("}")
      .append(LINE_SEP)
    s.toString
  }
}

// Stmt ::= debugger ;
case class Debugger(
    override val info: ASTNodeInfo
) extends Stmt(info: ASTNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("debugger;")
    s.toString
  }
}
