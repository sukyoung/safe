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

abstract class Property(
    override val info: ASTNodeInfo
) extends ASTNode(info: ASTNodeInfo) {
  def toId: Id
}

// Property ::= Id
case class PropId(
    override val info: ASTNodeInfo,
    id: Id
) extends Property(info: ASTNodeInfo) {
  override def toString: String = id.text
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(id.toString(indent))
    s.toString
  }
  def toId: Id = id
}

// Property ::= String
case class PropStr(
    override val info: ASTNodeInfo,
    str: String
) extends Property(info: ASTNodeInfo) {
  override def toString: String = str
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(if (str.equals("\"")) "'\"'" else "\"" + str + "\"")
    s.toString
  }
  def toId: Id = Id(info, str, None, false)
}

// Property ::= Number
case class PropNum(
    override val info: ASTNodeInfo,
    num: NumberLiteral
) extends Property(info: ASTNodeInfo) {
  override def toString: String = num.toString
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(num.toString(indent))
    s.toString
  }
  def toId: Id = Id(info, num match {
    case DoubleLiteral(_, t, _) => t
    case IntLiteral(_, i, _) => i.toString
  }, None, false)
}
