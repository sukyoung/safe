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

package kr.ac.kaist.safe.useful

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Random

object Useful {
  def shuffle[T](array: Array[T]): Array[T] = {
    val rnd = new Random
    for (n <- Iterator.range(array.length - 1, 0, -1)) {
      val k = rnd.nextInt(n + 1)
      val t = array(k); array(k) = array(n); array(n) = t
    }
    array
  }

  def windowPathToUnixPath(filename: String): String = {
    var result: String = filename.replaceAll("\\\\", "/")
    val drive = result.charAt(0)
    if (Character.isUpperCase(drive))
      result = Character.toLowerCase(drive) + result.substring(1)
    result
  }

  def filenameToWriters(fileName: String): (FileWriter, BufferedWriter) =
    try {
      val fw = filenameToFileWriter(fileName)
      (fw, new BufferedWriter(fw))
    } catch {
      case ex: IOException =>
        throw new IOException("IOException " + ex + "while writing " + fileName)
    }

  def filenameToFileWriter(fileName: String): FileWriter =
    try {
      new FileWriter(fileName)
    } catch {
      case ex: IOException =>
        // Probably the directory did not exist, therefore, make it so.
        // ONLY DEAL IN SLASHES.  THAT WORKS WITH WINDOWS.
        val last_slash = fileName.lastIndexOf('/')
        if (last_slash == -1) throw ex
        val dir = fileName.substring(0, last_slash)
        ensureDirectoryExists(dir)
        new FileWriter(fileName)
    }

  def ensureDirectoryExists(s: String): String = {
    val f = new File(s)
    if (f.exists) {
      if (f.isDirectory) {
        // ok
      } else {
        throw new Error("Necessary 'directory' " + s + " is not a directory.")
      }
    } else {
      if (f.mkdirs) {
        // ok
      } else {
        throw new Error("Failed to create directory " + s)
      }
    }
    s
  }

  def rmFile(name: String): Unit = {
    val file = new File(name)
    if (file.exists && !file.delete) throw new IOException
  }
}
