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

// Dummy for modeling
case class ModelFunc(
    info: ASTNodeInfo
) extends ASTNode {
  override def toString(indent: Int): String = ""
}
