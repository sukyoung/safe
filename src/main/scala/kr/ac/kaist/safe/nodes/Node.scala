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

package kr.ac.kaist.safe.nodes

import kr.ac.kaist.safe.util.{ Span, UIDObject }

abstract class Node(val info: NodeInfo) extends UIDObject {
  def toString(indent: Int): String
}

abstract class NodeInfo(val span: Span)

case class ASTNodeInfo(override val span: Span, comment: Option[Comment] = None)
  extends NodeInfo(span)
case class IRNodeInfo(override val span: Span, fromSource: Boolean = true, ast: ASTNode = null)
  extends NodeInfo(span)
