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

package kr.ac.kaist.safe.safe_util

import kr.ac.kaist.safe.useful.MagicNumbers
import kr.ac.kaist.safe.exceptions.SAFEError
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.lang.StringBuilder

class Span(b: SourceLoc, e: SourceLoc) extends UIDObject with Serializable {
  var begin: SourceLoc = b
  var end: SourceLoc = e

  override def hashCode: Int =
    begin.hashCode * MagicNumbers.p + end.hashCode * MagicNumbers.a

  override def equals(o: Any): Boolean =
    if (o.isInstanceOf[Span]) {
      val sp: Span = o.asInstanceOf[Span]
      begin.equals(sp.begin) && end.equals(sp.end)
    } else false

  def addLines(line: Int, offset: Int): Span =
    new Span(begin.addLines(line, offset), end.addLines(line, offset))

  def beginsEarlierThan(a: Span, b: Span): Boolean =
    (a.begin.line < b.begin.line ||
      (a.begin.line == b.begin.line &&
        a.begin.column < b.begin.column))

  def endsLaterThan(a: Span, b: Span): Boolean =
    (a.end.line > b.end.line ||
      (a.end.line == b.end.line &&
        a.end.column > b.end.column))

  /**
   * Span which includes both the given spans.  Assumption: they're
   * from the same file.  If this is not true, the results will be
   * unpredictable.
   */
  def span(a: Span, b: Span): Unit = {
    if (beginsEarlierThan(a, b))
      begin = a.begin
    else
      begin = b.begin
    if (endsLaterThan(a, b))
      end = a.end
    else
      end = b.end
  }

  def fileName: String = begin.fileName

  def fileNameOnly: String = begin.fileNameOnly

  def convertNameSeparatorToSlash(fileName: String): String =
    if (File.separatorChar == '/') fileName
    else fileName.replace(File.separatorChar, '/')

  override def toString: String =
    try {
      appendToStr(new StringBuilder, true).toString
    } catch {
      case e: IOException => SAFEError.np.asInstanceOf[String]
    }

  def toStringWithoutFiles: String =
    try {
      appendToStr(new StringBuilder, false).toString
    } catch {
      case e: IOException => SAFEError.np.asInstanceOf[String]
    }

  override def at: String = toString

  def stringName: String = ""

  def appendToStr(w: StringBuilder, do_files: Boolean): StringBuilder = {
    val left_col = begin.column
    val right_col = end.column
    val file_names_differ = !(begin.fileName.equals(end.fileName))
    if (do_files | file_names_differ) {
      // Need to add escapes to the file name
      var beginFileName: String = ""
      beginFileName = convertNameSeparatorToSlash(begin.fileName)
      w.append(beginFileName)
      w.append(":")
    }
    w.append(String.valueOf(begin.line))
    w.append(":")
    w.append(String.valueOf(left_col))
    if (file_names_differ || begin.line != end.line || left_col != right_col) {
      w.append("-")
      if (file_names_differ) {
        // Need to add escapes to the file name
        var endFileName: String = ""
        endFileName = convertNameSeparatorToSlash(end.fileName)
        w.append(endFileName)
        w.append(":")
      }
      if (file_names_differ || begin.line != end.line) {
        w.append(String.valueOf(end.line))
        w.append(":")
      }
      w.append(String.valueOf(right_col))
    }
    w
  }
}
