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

import kr.ac.kaist.safe.nodes.Node
import kr.ac.kaist.safe.util.{ SourceLoc, Span }

/**
 * *************************
 * JavaScript AST
 * ECMAScript 5
 * *************************
 */

// AST Node
trait ASTNode extends Node {
  val info: ASTNodeInfo
  def span: Span = info.span
  def comment: Option[Comment] = info.comment
  def fileName: String = span.fileName
  def begin: SourceLoc = span.begin
  def end: SourceLoc = span.end
  def line: Int = begin.line
  def offset: Int = begin.offset
}

// AST Node Information
case class ASTNodeInfo(
  span: Span, comment: Option[Comment] = None
)
