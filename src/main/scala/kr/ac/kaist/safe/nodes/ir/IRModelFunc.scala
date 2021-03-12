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

import kr.ac.kaist.safe.nodes.ast.ModelFunc

// Dummy for modeling
case class IRModelFunc(
    override val ast: ModelFunc
) extends IRNode(ast) {
  override def toString(indent: Int): String = ""
}
