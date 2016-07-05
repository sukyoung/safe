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

import scala.util.Try
import java.io.{ BufferedWriter, File, FileWriter, IOException }
import kr.ac.kaist.safe.{ LINE_SEP, CUR_DIR }

object Useful {
  def toRelativePath(fileName: String): String = {
    fileName startsWith CUR_DIR match {
      case true => fileName.substring(CUR_DIR.length + 1)
      case false => fileName
    }
  }
  def windowPathToUnixPath(filename: String): String = {
    var result: String = filename.replaceAll("\\\\", "/")
    val drive = result.charAt(0)
    if (Character.isUpperCase(drive))
      result = Character.toLowerCase(drive) + result.substring(1)
    result
  }

  def fileNameToWriters(fileName: String): (FileWriter, BufferedWriter) = {
    val fw = fileNameToFileWriter(fileName)
    (fw, new BufferedWriter(fw))
  }

  def fileNameToFileWriter(fileName: String): FileWriter =
    new FileWriter(fileName)

  def indentation(str: String, indent: Int): String = {
    str.split(LINE_SEP).mkString(LINE_SEP + " " * indent)
  }
}
