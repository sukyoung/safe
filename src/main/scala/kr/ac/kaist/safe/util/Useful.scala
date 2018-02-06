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

package kr.ac.kaist.safe.util

import scala.util.Try
import java.io.{ BufferedWriter, File, FileWriter, IOException }
import kr.ac.kaist.safe.{ LINE_SEP, CUR_DIR, BASE_DIR, SEP }

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

  def indentation(s: StringBuilder, str: String, indent: Int): Unit = {
    str.split(LINE_SEP) match {
      case Array(str, rest @ _*) => {
        s.append(str)
        rest.foreach(rStr => {
          s.append(LINE_SEP)
          for (i <- 0 until indent) { s.append(" ") }
          s.append(rStr)
        })
      }
      case _ =>
    }
  }

  def path(dirs: String*): String = BASE_DIR + SEP + dirs.mkString(SEP)
}
