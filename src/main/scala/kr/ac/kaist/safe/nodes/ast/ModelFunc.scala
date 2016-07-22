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

// Dummy for modeling
case class ModelFunc(
    override val info: ASTNodeInfo
) extends ASTNode(info: ASTNodeInfo) {
  override def toString(indent: Int): String = ""
}
