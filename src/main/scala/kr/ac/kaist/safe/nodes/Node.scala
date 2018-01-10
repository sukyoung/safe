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

package kr.ac.kaist.safe.nodes

import kr.ac.kaist.safe.util.{ Span, SourceLoc, Useful }
import kr.ac.kaist.safe.nodes.ast.Comment

abstract class Node {
  def toString(indent: Int): String

  // helper for info
  def span: Span
  def comment: Option[Comment]
  def fileName: String
  def relFileName: String = Useful.toRelativePath(fileName)
  def begin: SourceLoc
  def end: SourceLoc
  def line: Int
  def offset: Int
}
