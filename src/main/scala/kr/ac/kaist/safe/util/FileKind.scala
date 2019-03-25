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

sealed abstract class FileKind
object FileKind {
  def apply(fileName: String): FileKind = {
    if (fileName.endsWith(".js")) JSFile
    else if (fileName.endsWith(".js.err")) JSErrFile
    else if (fileName.endsWith(".js.todo")) JSTodoFile
    else if (fileName.endsWith(".html") ||
      fileName.endsWith(".xhtml") ||
      fileName.endsWith(".htm")) HTMLFile
    else NormalFile
  }
}

case object JSFile extends FileKind
case object JSErrFile extends FileKind
case object JSTodoFile extends FileKind
case object HTMLFile extends FileKind
case object NormalFile extends FileKind
