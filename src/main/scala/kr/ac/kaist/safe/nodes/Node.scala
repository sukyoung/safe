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

import kr.ac.kaist.safe.util.UIDObject
import kr.ac.kaist.safe.util.{ Span, SourceLoc }

abstract class Node extends UIDObject {
  def toString(indent: Int): String

  // helper for info
  def span: Span
  def comment: Option[Comment]
  def fileName: String
  def begin: SourceLoc
  def end: SourceLoc
  def line: Int
  def offset: Int
}
