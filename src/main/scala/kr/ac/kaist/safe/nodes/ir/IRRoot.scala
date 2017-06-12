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

package kr.ac.kaist.safe.nodes.ir

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.ast.ASTNode
import kr.ac.kaist.safe.util.NodeUtil

// IRRoot ::= Statement*
case class IRRoot(
    override val ast: ASTNode,
    fds: List[IRFunDecl],
    vds: List[IRVarStmt],
    irs: List[IRStmt]
) extends IRNode(ast) {
  override def toString(indent: Int): String = {
    NodeUtil.initNodesPrint
    val s: StringBuilder = new StringBuilder
    val indentString = NodeUtil.getIndent(indent)
    s.append(indentString).append(NodeUtil.join(indent, fds, LINE_SEP + indentString, new StringBuilder("")))
    s.append(LINE_SEP)
    s.append(indentString).append(NodeUtil.join(indent, vds, LINE_SEP + indentString, new StringBuilder("")))
    s.append(LINE_SEP)
    s.append(indentString).append(NodeUtil.join(indent, irs, LINE_SEP + indentString, new StringBuilder("")))
    s.append(LINE_SEP)
    s.toString
  }
}
