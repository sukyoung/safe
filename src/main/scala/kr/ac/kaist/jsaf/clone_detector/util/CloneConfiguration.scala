/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.clone_detector.util

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Double
import java.lang.Integer
import java.util.Properties
import java.util.Vector
import kr.ac.kaist.jsaf.ProjectProperties

class CloneConfiguration(file: File) {
  val SEP = File.separator
  val base = ProjectProperties.BASEDIR + SEP

  val props = new Properties()
  props.load(new FileInputStream(file))
  val src_dir = new File(props.getProperty("SRC_DIR").replace("\'", "").replace("\"", ""))
  val vector_dir = new File(props.getProperty("VECTOR_DIR").replace("\'", "").replace("\"", "")replace("$JS_HOME/", base))
  val cluster_dir = new File(props.getProperty("CLUSTER_DIR").replace("\'", "").replace("\"", "").replace("$JS_HOME/", base))
  val time_dir = new File(props.getProperty("TIME_DIR").replace("\'", "").replace("\"", "").replace("$JS_HOME/", base))
  var min_tokens = new Vector[Int]()
  var stride = new Vector[Int]()
  var similarity = new Vector[Double]()
  val grouping_s = Integer.parseInt(props.getProperty("GROUPING_S").replace("\'", "").replace("\"", ""))
  init
  /*
   * debugging...
   *
  System.out.println("src_dir="+src_dir)
  System.out.println("vector_dir="+vector_dir)
  System.out.println("cluster_dir="+cluster_dir)
  System.out.println("time_dir="+time_dir)
   */

  def init = {
    if (! vector_dir.exists) vector_dir.mkdir
    if (! cluster_dir.exists) cluster_dir.mkdir
    if (! time_dir.exists) time_dir.mkdir
    val min_tokens_arr = props.getProperty("MIN_TOKENS").replace("\'", "").split(" ")
    for (i <- 0 to min_tokens_arr.length-1)
      min_tokens.add(Integer.parseInt(min_tokens_arr(i)))
    val stride_arr = props.getProperty("STRIDE").replace("\'", "").split(" ")
    for (i <- 0 to stride_arr.length-1)
      stride.add(Integer.parseInt(stride_arr(i)))
    val similarity_arr = props.getProperty("SIMILARITY").replace("\'", "").split(" ")
    for (i <- 0 to similarity_arr.length-1)
      similarity.add(Double.parseDouble(similarity_arr(i)))

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
    for (i <- 0; to similarity.size-1)
      System.out.print(similarity.elementAt(i) + " ")
    System.out.println
    System.out.println(grouping_s)
     */
  }

  def getSourceDirectory = src_dir
  def getVectorDirectory = vector_dir
  def getClusterDirectory = cluster_dir
  def getTimeDirectory = time_dir
  def getMinimumTokens = min_tokens
  def getStride = stride
  def getSimilarity = similarity
  def getGroupingSize = grouping_s
}
