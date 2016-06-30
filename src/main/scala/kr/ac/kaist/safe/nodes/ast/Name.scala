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

import kr.ac.kaist.safe.util.{ NodeUtil => NU }
import kr.ac.kaist.safe.config.Config

abstract class Name(
  override val info: ASTNodeInfo
) extends ASTNode(info: ASTNodeInfo)

abstract class IdOrOpOrAnonymousName(
  override val info: ASTNodeInfo
) extends Name(info: ASTNodeInfo)

abstract class IdOrOp(
  override val info: ASTNodeInfo,
  text: String
) extends IdOrOpOrAnonymousName(info: ASTNodeInfo)

// Named identifier
case class Id(
    override val info: ASTNodeInfo,
    text: String,
    uniqueName: Option[String] = None,
    isWith: Boolean
) extends IdOrOp(info: ASTNodeInfo, text: String) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    uniqueName match {
      case Some(u) if isWith =>
        s.append(u.dropRight(Config.SIGNIFICANT_BITS) +
          NU.getNodesE(u.takeRight(Config.SIGNIFICANT_BITS)))
      case _ => s.append(text)
    }
    s.toString
  }
}

// Infix/prefix/postfix operator
case class Op(
    override val info: ASTNodeInfo,
    text: String
) extends IdOrOp(info: ASTNodeInfo, text: String) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    comment.map(c => s.append(c.toString(indent)))
    s.append(text)
    s.toString
  }
}

// Unnamed identifier
case class AnonymousFnName(
    override val info: ASTNodeInfo,
    text: String
) extends IdOrOpOrAnonymousName(info: ASTNodeInfo) {
  override def toString(indent: Int): String = ""
}
