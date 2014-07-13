/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import java.io.BufferedInputStream
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Arrays
import scala.collection.mutable.ListBuffer
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.cfg.LEntry
import kr.ac.kaist.jsaf.analysis.cfg.LExit
import kr.ac.kaist.jsaf.analysis.cfg.LExitExc
import kr.ac.kaist.jsaf.analysis.cfg.LBlock
import kr.ac.kaist.jsaf.analysis.cfg.Node
import kr.ac.kaist.jsaf.exceptions.JSAFError
import kr.ac.kaist.jsaf.analysis.typing.OrderMap
import kr.ac.kaist.jsaf.analysis.typing.Worklist

/**
 * Utilities for interfacing with DOT: http://www.graphviz.org/
 */
object DotUtil {
  /**
   * Currently, the possible output format for dot is PS.
   */
  private val MAX_LABEL_LENGTH = 75

  def dotify(g: CFG, dotFile: String, outputFile: String, dotExe: String) = {
    val wo = Worklist.computes(g)
    val o = wo.getOrder()
    spawnDot(dotExe, outputFile, writeDotFile(g, o, dotFile))
  }

  def writeDotFile(g: CFG, o: OrderMap, dotfile: String) =
    try {
      val f = new File(dotfile)
      val fw = new FileWriter(f)
      fw.write(dotOutput(g, o))
      fw.close
      f
    } catch {
      case e:Throwable =>
	e.printStackTrace();
        JSAFError.error("Error writing dot file " + dotfile + ".")
    }

  def getLabel(n: Node, o: OrderMap) = {
    val order = o.get(n) match {
      case Some(i) => "["+i+"]"
      case None => ""
    }
    var label = n match {
      case (fid, LEntry) => "\""+order+"("+fid+", "+"LEntry)\""
      case (fid, LExit) => "\""+order+"("+fid+", "+"LExit)\""
      case (fid, LExitExc) => "\""+order+"("+fid+", "+"LExitExc)\""
      case (fid, LBlock(id)) => "\""+order+"("+fid+", "+"LBlock("+id+"))\""
    }
    if (label.length >= MAX_LABEL_LENGTH) {
      label = label.substring(0, MAX_LABEL_LENGTH - 3) + "..."
    }
    label
  }

  def dotOutput(g: CFG, o: OrderMap) = {
    val s: StringBuilder = new StringBuilder
    s.append("digraph \"DirectedGraph\" {\n")
    s.append("center=true;fontsize=12;node [fontsize=12];edge [fontsize=12];\n")
    val dotNodes = g.getNodes
    for (n <- dotNodes)
      s.append("   ").append(getLabel(n,o)).append(decorateNode(n))
    for (n <- dotNodes)
      for (m <- g.getSucc(n))
        s.append(" ").append(getLabel(n,o)).append(" -> ").append(getLabel(m,o)).append(" \n")
    s.append("\n}")
    s.toString
  }

  def decorateNode(n: Node) = {
    val s: StringBuilder = new StringBuilder
    s.append(" [shape=\"box\" color=\"blue\"").append("]\n")
    s.toString
  }

  def spawnDot(dotExe: String, outputFile: String, dotFile: File) = {
    val cmdarray = Array(dotExe, "-Tps", "-o", outputFile, "-v", dotFile.getAbsolutePath)
    System.out.println("Spawning process" + cmdarray.foldLeft("")((r,s) => r + " " + s))
    try {
      val p = Runtime.getRuntime.exec(cmdarray)
      val output = new BufferedInputStream(p.getInputStream)
      val error = new BufferedInputStream(p.getErrorStream)
      var repeat = true
      while (repeat) {
        try {
          Thread.sleep(500)
        } catch {
          case e1:InterruptedException =>
          e1.printStackTrace
          // just ignore and continue
        }
        if (output.available > 0) {
          val data = ListBuffer[Byte]()
          val nRead = output.read(data.toArray)
          //System.err.println("read " + nRead + " bytes from output stream")
        }
        if (error.available > 0) {
          val data = ListBuffer[Byte]()
          val nRead = error.read(data.toArray)
          //System.err.println("read " + nRead + " bytes from error stream")
        }
        try {
          p.exitValue
          // if we get here, the process has terminated
          repeat = false
          //System.out.println("process terminated with exit code " + p.exitValue)
        } catch {
          case _:IllegalThreadStateException =>
          // this means the process has not yet terminated.
          repeat = true
        }
      }
    } catch {
      case e:IOException =>
        e.printStackTrace
        JSAFError.error("IOException DotUtil.")
    }
  }
}
