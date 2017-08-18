/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 * *
 * Use is subject to license terms.
 * *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */
package kr.ac.kaist.safe.clone_detector.util

import java.io.{ File, FileInputStream, FileWriter }
import java.nio.charset.Charset
import java.nio.file.{ Files, Paths }
import java.util
import java.util.Properties

import kr.ac.kaist.safe

class CloneConfiguration(file: File) {
  val base: String = getBase

  val props = new Properties()
  props.load(new FileInputStream(file))
  val srcDir = new File(props.getProperty("SRC_DIR").replace("\'", "").replace("\"", ""))
  val vectorDir = new File(props.getProperty("VECTOR_DIR").replace("\'", "").replace("\"", "") replace ("$SAFE_HOME/", base))
  val clusterDir = new File(props.getProperty("CLUSTER_DIR").replace("\'", "").replace("\"", "").replace("$SAFE_HOME/", base))
  val timeDir = new File(props.getProperty("TIME_DIR").replace("\'", "").replace("\"", "").replace("$SAFE_HOME/", base))
  val groupingS: Int = Integer.parseInt(props.getProperty("GROUPING_S").replace("\'", "").replace("\"", ""))
  var minTokens = new util.Vector[Int]()
  var stride = new util.Vector[Int]()
  var similarity = new util.Vector[Double]()
  init()

  /*
   * debugging...
   *
  System.out.println("src_dir="+src_dir)
  System.out.println("vector_dir="+vector_dir)
  System.out.println("cluster_dir="+cluster_dir)
  System.out.println("time_dir="+time_dir)
   */

  def init(): Unit = {
    if (!vectorDir.exists) vectorDir.mkdir
    if (!clusterDir.exists) clusterDir.mkdir
    if (!timeDir.exists) timeDir.mkdir
    val minTokensArr = props.getProperty("MIN_TOKENS").replace("\'", "").split(" ")
    for (i <- 0 until minTokensArr.length)
      minTokens.add(Integer.parseInt(minTokensArr(i)))
    val strideArr = props.getProperty("STRIDE").replace("\'", "").split(" ")
    for (i <- 0 until strideArr.length)
      stride.add(Integer.parseInt(strideArr(i)))
    val similarityArr = props.getProperty("SIMILARITY").replace("\'", "").split(" ")
    for (i <- 0 until similarityArr.length)
      similarity.add(similarityArr(i).toDouble)

    /*
     * debugging...
     *
    System.out.println(src_dir)
    for (i <- 0 to min_tokens.size-1)
      System.out.print(min_tokens.elementAt(i) + " ")
    System.out.println
    for (i <- 0 to stride.size-1)
      System.out.print(stride.elementAt(i) + " ");
    System.out.println
    for (i <- 0 to similarity.size-1)
      System.out.print(similarity.elementAt(i) + " ")
    System.out.println
    System.out.println(grouping_s)
     */
  }

  /*
  def getPluginBase: String = {
    TaskManager.getPluginBase
  }
  */

  def getBase: String = {
    try {
      safe.BASE_DIR + File.separator
    } catch {
      case e: Throwable => "" // getPluginBase
    }
  }

  def writeToConfig(key: String, value: String): Unit = {
    val config = file.getAbsolutePath

    try {
      val path = Paths.get(config)
      val charset = Charset.forName("UTF-8")
      val lines = Files.readAllLines(path, charset)
      val writer = new FileWriter(config)

      val itr = lines.iterator()
      while (itr.hasNext) {
        val line = itr.next()
        if (line.startsWith(key)) {
          writer.write(key + "='" + value + "'" + "\n")
        } else {
          writer.write(line + "\n")
        }
      }
      writer.close();
    } catch {
      case e: Exception =>
      // UserInterface.printStackTraceToConsole(e)
    }
  }

  def getSourceDirectory: File = srcDir

  def getVectorDirectory: File = vectorDir

  def getClusterDirectory: File = clusterDir

  def getTimeDirectory: File = timeDir

  def getMinimumTokens: util.Vector[Int] = minTokens

  def getStride: util.Vector[Int] = stride

  def getSimilarity: util.Vector[Double] = similarity

  def getGroupingSize: Int = groupingS
}
