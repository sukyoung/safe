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
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object Useful {
  def windowPathToUnixPath(filename: String): String = {
    var result: String = filename.replaceAll("\\\\", "/")
    val drive = result.charAt(0)
    if (Character.isUpperCase(drive))
      result = Character.toLowerCase(drive) + result.substring(1)
    result
  }

  def fileNameToWriters(fileName: String): Try[(FileWriter, BufferedWriter)] =
    fileNameToFileWriter(fileName).map(fw => (fw, new BufferedWriter(fw)))

  def fileNameToFileWriter(fileName: String): Try[FileWriter] =
    Try(new FileWriter(fileName))
}
