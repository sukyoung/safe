/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.clone_detector

import java.io.{ File, PrintWriter, StringWriter }
import java.util.regex.Pattern
import java.util.{ Collections, List => JList }

import kr.ac.kaist.safe.clone_detector.util.CloneConfiguration
import kr.ac.kaist.safe.clone_detector.vgen.VectorGenerator
import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

object CloneDetector {
  def doit(config: CloneConfiguration): Int = {
    val SEP = File.separator
    val directory = config.getSourceDirectory
    val extensions = Array("js", "html", "htm", "xhtml", "shtml", "shtm", "phtml")
    val vectorDir = config.getVectorDirectory.getPath
    val timeDir = config.getTimeDirectory.getPath

    for (i <- 0 until config.getMinimumTokens.size) {
      for (j <- 0 until config.getStride.size) {
        val start = System.currentTimeMillis

        System.out.print("vgen: " + config.getMinimumTokens.elementAt(i) + " " + config.getStride.elementAt(j) + " ...")
        val vdb = new java.io.File(vectorDir + SEP + "vdb_" + config.getMinimumTokens.elementAt(i) + "_" + config.getStride.elementAt(j))
        if (vdb.exists) vdb.delete

        val files = FileUtils.listFiles(directory, extensions, true)
        Collections.sort(files.asInstanceOf[JList[File]])

        val vgen = new java.io.File(timeDir + SEP + "vgen_" + config.getMinimumTokens.elementAt(i) + "_" + config.getStride.elementAt(j))
        val fstream = new java.io.FileWriter(vgen)
        val out = new java.io.BufferedWriter(fstream)

        for (file <- files.asScala) {
          try {
            val htmlPattern = Pattern.compile("[p|s|x]{0,1}htm[l]{0,1}")
            val disallowPattern = Array(Pattern.compile("[p|s|x]{0,1}htm[l]{0,1}.html"), Pattern.compile(".[p|s|x]{0,1}htm[l]{0,1}(.[0-9]+_[0-9]+.js)"))
            val filename = file.toString
            val extension = filename.substring(filename.lastIndexOf("."), filename.length)

            if (!disallowPattern(1).matcher(filename).find()) {
              if (extension.equals(".js"))
                new VectorGenerator(filename, config.getMinimumTokens.elementAt(i), config.getStride.elementAt(j), vectorDir)
              else if (htmlPattern.matcher(filename).find && !disallowPattern(0).matcher(filename).find)
                new VectorGenerator(filename, config.getMinimumTokens.elementAt(i), config.getStride.elementAt(j), vectorDir, false)
            }
          } catch {
            case e: Throwable =>
              val sw = new StringWriter
              e.printStackTrace(new PrintWriter(sw))
              out.write(sw.toString)
              out.newLine()
          }
        }

        if (vdb.exists && vdb.length > 0)
          System.out.println("Done")
        else {
          out.write("Error: no vector is generated. Please check your configuration.")
          out.newLine()
          out.close()
          System.exit(1)
        }

        val end = System.currentTimeMillis
        val duration = end - start

        out.write(duration / 60000 + "m" + (duration % 60000) / 1000.0 + "s")
        out.newLine()
        out.close()
      }
    }
    if (new File(vectorDir).list().length > 0) 0 else 1
  }
}
