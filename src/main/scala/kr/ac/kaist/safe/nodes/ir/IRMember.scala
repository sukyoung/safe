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

import kr.ac.kaist.safe.nodes.ast.{ ASTNode, PropStr }

// Member
abstract class IRMember(
  override val ast: ASTNode
) extends IRNode(ast)

// Member ::= x : e
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

// Member ::= get x () { s }
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

// Member ::= set x ( y ) { s }
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
