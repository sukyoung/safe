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

package kr.ac.kaist.safe.nodes.ast

import kr.ac.kaist.safe.LINE_SEP

abstract class Member(
    override val info: ASTNodeInfo,
    prop: Property
) extends ASTNode(info: ASTNodeInfo) {
  override def toString: String = prop.toString
}

// Member ::= Property : Expr
case class Field(
    override val info: ASTNodeInfo,
    prop: Property,
    expr: Expr
) extends Member(info: ASTNodeInfo, prop: Property) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(prop.toString(indent))
      .append(" : ")
      .append(expr.toString(indent))
    s.toString
  }
}

// Member ::= get Property () { FunctionBody }
case class GetProp(
    override val info: ASTNodeInfo,
    prop: Property,
    ftn: Functional
) extends Member(info: ASTNodeInfo, prop: Property) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("get ")
      .append(prop.toString(indent))
      .append("()")
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("{")
      .append(LINE_SEP)
    NU.prUseStrictDirective(s, indent, ftn.fds, ftn.vds, ftn.stmts)
    NU.prFtn(s, indent, ftn.fds, ftn.vds, ftn.stmts.body)
    s.append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("}")
    s.toString
  }
}

// Member ::= set Property ( Id ) { SourceElement* }
case class SetProp(
    override val info: ASTNodeInfo,
    prop: Property,
    ftn: Functional
) extends Member(info: ASTNodeInfo, prop: Property) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append("set ")
      .append(prop.toString(indent))
      .append("(")
      .append(ftn.params.head.toString(indent))
      .append(") ")
      .append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("{")
      .append(LINE_SEP)
    NU.prUseStrictDirective(s, indent, ftn.fds, ftn.vds, ftn.stmts)
    NU.prFtn(s, indent, ftn.fds, ftn.vds, ftn.stmts.body)
    s.append(LINE_SEP)
      .append(NU.getIndent(indent))
      .append("}")
    s.toString
  }
}
