/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.clone_detector

import _root_.java.util.{List => JList}
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.Collection
import java.util.Collections
import scala.collection.JavaConverters._
import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.clone_detector.util.CloneConfiguration
import kr.ac.kaist.jsaf.clone_detector.vgen.VectorGenerator
import kr.ac.kaist.jsaf.nodes_util.JSFromFile
import edu.rice.cs.plt.tuple.Option
import java.io.StringWriter
import java.io.PrintWriter
import java.util.regex.Pattern

object CloneDetector {
  val SEP = File.separator
  val jscd_config = ProjectProperties.BASEDIR + SEP + "bin/jscd_config"
  
  def main(args: Array[String]) {
    doit()
  }
  
  def doit() = {
    val file = new File(jscd_config)
    if (! file.exists)
      System.err.println("Error: no config file in the current directory")
    else {
      val config = new CloneConfiguration(file)
      val directory = config.getSourceDirectory
      val extensions: Array[String] = Array("js", "html", "htm", "xhtml", "shtml", "shtm", "phtml")
      val vector_dir = config.getVectorDirectory.getPath
      val time_dir = config.getTimeDirectory.getPath
      
      for (i <- 0 to config.getMinimumTokens.size-1) {
        for (j <- 0 to config.getStride.size-1) {
          val start = System.currentTimeMillis
          
          System.out.print("vgen: " + config.getMinimumTokens.elementAt(i) + " " + config.getStride.elementAt(j) + " ...")
          val vdb = new java.io.File(vector_dir + "/vdb_" + config.getMinimumTokens.elementAt(i) + "_" + config.getStride.elementAt(j))
          if (vdb.exists) vdb.delete
          
          val files: Collection[File] = FileUtils.listFiles(directory, extensions, true)
          Collections.sort(files.asInstanceOf[JList[File]])
          
          val vgen = new java.io.File(time_dir + "/vgen_" + config.getMinimumTokens.elementAt(i) + "_" + config.getStride.elementAt(j))
          val fstream = new java.io.FileWriter(vgen)
  		  val out = new java.io.BufferedWriter(fstream)
          
          for (file <- files.asScala) {
            try {
              val html_pattern = Pattern.compile("[p|s|x]{0,1}htm[l]{0,1}")
              val disallow_pattern = Array(Pattern.compile("[p|s|x]{0,1}htm[l]{0,1}.html"), Pattern.compile(".[p|s|x]{0,1}htm[l]{0,1}(.[0-9]+_[0-9]+.js)"))
              val filename = file.toString
              val extension = filename.substring(filename.lastIndexOf("."), filename.length)
              
              if (!disallow_pattern(1).matcher(filename).find()) {
                if (extension.equals(".js"))
                  new VectorGenerator(filename, config.getMinimumTokens.elementAt(i), config.getStride.elementAt(j), vector_dir)
                else if (html_pattern.matcher(filename).find && !disallow_pattern(0).matcher(filename).find)
                  new VectorGenerator(filename, config.getMinimumTokens.elementAt(i), config.getStride.elementAt(j), vector_dir, false)
              }
            } catch {
              case e:Throwable =>
              	val sw = new StringWriter
            	e.printStackTrace(new PrintWriter(sw))
                out.write(sw.toString)
                out.newLine
            }
          }
          
          if (vdb.exists && vdb.length > 0)
            System.out.println("Done")
          else {
	        out.write("Error: no vector is generated. Please check your configuration.")
	        out.newLine
	        out.close
	        System.exit(1)
          }
        	
          val end = System.currentTimeMillis
          val duration = end - start
          
  		  out.write(duration/60000 + "m" + (duration % 60000) / 1000.0 + "s")
  		  out.newLine
  		  out.close
        }
      }
   }
  }
}
