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

// Program ::= SourceElement*
case class Program(
    info: ASTNodeInfo,
    body: TopLevel
) extends ASTNode {
  override def toString(indent: Int): String = {
    NU.initNodesPrint
    val s: StringBuilder = new StringBuilder
    s.append(body.toString(indent))
    comment.map(c => s.append(c.toString(indent)))
    s.toString
  }
}
object Program {
  def apply(info: ASTNodeInfo, ses: List[SourceElements]): Program =
    Program(info, new TopLevel(info, Nil, Nil, ses))
}
