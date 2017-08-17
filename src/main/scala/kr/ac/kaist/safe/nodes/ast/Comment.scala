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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

// comment
case class Comment(
    override val info: ASTNodeInfo,
    txt: String
) extends ASTNode(info: ASTNodeInfo) {
  def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(NU.getIndent(indent))
    s.append(txt).append(LINE_SEP)
    s.toString
  }
}
