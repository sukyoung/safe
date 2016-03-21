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

package kr.ac.kaist.safe.util

import java.io.Serializable

// /
// / type 'a node = 'a Node.node
// /
class SourceLoc(f: String, val line: Int, val column: Int, val offset: Int) extends Serializable {
  val fileName = Useful.windowPathToUnixPath(f)

  val at = fileName + ":" + line + "." + column
  val stringName = ""

  def fileNameOnly: String = {
    val index = fileName.lastIndexOf('/' /*File.separatorChar*/ )
    if (index != -1) fileName.substring(index + 1)
    else fileName
  }

  def addLines(l: Int, o: Int): SourceLoc =
    new SourceLoc(fileName, line + l, column, offset + o)

  override def hashCode: Int =
    line * MagicNumbers.S + column * MagicNumbers.o + fileName.hashCode + MagicNumbers.u

  override def equals(o: Any): Boolean = {
    if (o.isInstanceOf[SourceLoc]) {
      val sl = o.asInstanceOf[SourceLoc]
      line == sl.line && column == sl.column && fileName.equals(sl.fileName)
    } else false
  }
}
