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
  override def toString: String = toString(0)
  def span: Span
  def fileName: String
  def begin: SourceLoc
  def end: SourceLoc
  def line: Int
  def offset: Int
}
