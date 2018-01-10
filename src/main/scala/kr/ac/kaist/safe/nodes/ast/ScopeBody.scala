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

import kr.ac.kaist.safe.util.{ NodeUtil => NU }
import kr.ac.kaist.safe.LINE_SEP

// Common body for program and functions
trait ScopeBody extends ASTNode {
  val fds: List[FunDecl]
  val vds: List[VarDecl]
}

// Program top level
case class TopLevel(
    info: ASTNodeInfo,
    fds: List[FunDecl],
    vds: List[VarDecl],
    stmts: List[SourceElements]
) extends ScopeBody {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    NU.prUseStrictDirective(s, indent, fds, vds, stmts)
    NU.prFtn(s, indent, fds, vds,
      stmts.foldLeft(List[Stmt]()) {
        case (l, s) => l ++ s.body.asInstanceOf[List[Stmt]]
      })
    s.toString
  }
}

// Common shape for functions
case class Functional(
    info: ASTNodeInfo,
    fds: List[FunDecl],
    vds: List[VarDecl],
    stmts: SourceElements,
    name: Id,
    params: List[Id],
    body: String
) extends ScopeBody {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(name.toString(indent))
      .append("(")
      .append(NU.join(indent, params, ", ", new StringBuilder("")))
      .append(") ")
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("{")
      .append(LINE_SEP)
    NU.prUseStrictDirective(s, indent, fds, vds, stmts)
    NU.prFtn(s, indent, fds, vds, stmts.body)
    s.append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("}")
    s.toString
  }
}
