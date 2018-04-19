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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.ast.Functional
import kr.ac.kaist.safe.util.NodeUtil

// Common shape for functions
case class IRFunctional(
    override val ast: Functional,
    fromSource: Boolean,
    name: IRId,
    params: List[IRId],
    args: List[IRStmt],
    fds: List[IRFunDecl],
    vds: List[IRVarStmt],
    body: List[IRStmt]
) extends IRNode(ast) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(name.toString(indent)).append("(")
    s.append(NodeUtil.join(indent, params, ", ", new StringBuilder("")))
    s.append(") ").append(LINE_SEP).append(NodeUtil.getIndent(indent)).append("{").append(LINE_SEP)
    s.append(NodeUtil.getIndent(indent + 1))
    s.append(NodeUtil.join(indent + 1, fds ++ vds ++ args ++ body, LINE_SEP + NodeUtil.getIndent(indent + 1), new StringBuilder("")))
    s.append(LINE_SEP).append(NodeUtil.getIndent(indent)).append("}")
    s.toString
  }
}
