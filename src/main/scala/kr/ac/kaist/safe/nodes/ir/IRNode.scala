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

import kr.ac.kaist.safe.util.{ SourceLoc, Span }
import kr.ac.kaist.safe.nodes.Node
import kr.ac.kaist.safe.nodes.ast.{ ASTNode, Comment }

/**
 * *************************
 * JavaScript IR
 * ECMAScript 5
 * *************************
 */

abstract class IRNode(
    val ast: ASTNode
) extends Node {
  def span: Span = ast.span
  def comment: Option[Comment] = ast.comment
  def fileName: String = ast.fileName
  def begin: SourceLoc = ast.begin
  def end: SourceLoc = ast.end
  def line: Int = ast.line
  def offset: Int = ast.offset
}
