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

import kr.ac.kaist.safe.nodes.ast.ASTNode
import kr.ac.kaist.safe.util._

// Operator
case class IROp(
    override val ast: ASTNode,
    kind: EJSOp
) extends IRNode(ast) {
  override def toString(indent: Int): String = name
  val name: String = kind.name
  def isAssertOperator: Boolean = kind.typ == EJSEqType
}
