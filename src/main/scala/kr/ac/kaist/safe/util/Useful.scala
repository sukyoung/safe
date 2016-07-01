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
import kr.ac.kaist.safe.config.Config

object Useful {
  def toRelativePath(fileName: String): String = {
    fileName startsWith Config.CUR_DIR match {
      case true => fileName.substring(Config.CUR_DIR.length + 1)
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

  def fileNameToWriters(fileName: String): Try[(FileWriter, BufferedWriter)] =
    fileNameToFileWriter(fileName).map(fw => (fw, new BufferedWriter(fw)))

  def fileNameToFileWriter(fileName: String): Try[FileWriter] =
    Try(new FileWriter(fileName))
}
