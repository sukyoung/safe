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

package kr.ac.kaist.safe.util

import java.io.File
import java.io.Serializable

case class SourceLoc(
    line: Int = 0,
    column: Int = 0,
    offset: Int = 0
) {
  override def toString: String = line + ":" + column

  def addLines(l: Int, o: Int): SourceLoc =
    SourceLoc(line + l, column, offset + o)

  // comparison operators
  def <(s: SourceLoc): Boolean = s match {
    case SourceLoc(line, column, offset) =>
      this.line < line ||
        (this.line == line && (this.column < column ||
          (this.column == column && this.offset < offset)))
  }
  def <=(s: SourceLoc): Boolean = (this < s || this == s)
  def >(s: SourceLoc): Boolean = !(this <= s)
  def >=(s: SourceLoc): Boolean = !(this < s)
}
