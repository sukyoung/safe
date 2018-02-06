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

package kr.ac.kaist.safe.nodes.cfg

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.Node
import kr.ac.kaist.safe.nodes.ast.Comment
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.{ Span, SourceLoc }

/**
 * *************************
 * JavaScript CFG
 * ECMAScript 5
 * *************************
 */

trait CFGNode extends Node {
  val ir: IRNode
  def span: Span = ir.span
  def comment: Option[Comment] = ir.comment
  def fileName: String = ir.fileName
  def begin: SourceLoc = ir.begin
  def end: SourceLoc = ir.end
  def line: Int = ir.line
  def offset: Int = ir.offset
  def toString(indent: Int): String = "  " * indent + toString() + LINE_SEP
}
