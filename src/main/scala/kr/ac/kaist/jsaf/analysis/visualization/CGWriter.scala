/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.visualization

import java.io.File
import java.io.FileWriter
import kr.ac.kaist.jsaf.exceptions.JSAFError
import java.io.BufferedInputStream
import java.io.IOException
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import kr.ac.kaist.jsaf.analysis.cfg.FunctionId
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.cfg.LEntry

object CGWriter {
  def NormalNodeShape:String = "fontname=\"Consolas\" shape=record, fontsize=10"
  def NormalEdgeStyle:String = "fontname=\"Consolas\" style=solid"
  def ExcEdgeStyle:String = "fontname=\"Consolas\" style=dashed,label=\"exc\""
  def call2AftcallEdgeStyle:String = "fontname=\"Consolas\" style=dotted,color=gray,dir=none"
  def exit2ExcExitEdgeStyle:String = "fontname=\"Consolas\" style=invis,dir=none"
  def newLine = ";\n\t"
  
  def nodeShape(shape:String):String = "[" + shape + "]"
  def edgeStyle(style:String):String = "[" + style + "]"

  def escape(src:Array[Char]):String = {
    val cs = Array('<','>','|','"','\\')
    var sb = new StringBuilder
    for(ch <-src) {
      if(cs.contains(ch))	sb.append("\\".+(ch))
      else	sb.append(ch);
    }
    sb.toString();
  }
  
  def name(cfg:CFG, fid:FunctionId):String = {
    escape(cfg.getFuncName(fid).toArray)
  }

  // node1 -> node2 [style=...]
  def drawEdge(cfg:CFG, caller:FunctionId, callees:Set[FunctionId]):String = {
    val sb = new StringBuilder
    sb.append("\""+name(cfg, caller)+"\"").append("->{")
    for(callee <- callees) {
      sb.append("\""+name(cfg, callee)+"\"").append(";")
    }
    sb.append("}").append(edgeStyle(NormalEdgeStyle))    
    sb.toString
  }

  // node [label=...]
  def drawNode(cfg:CFG, caller:FunctionId, callees:Set[FunctionId]):String = {
    var sb = new StringBuilder()
    
    sb.append("\"" + name(cfg, caller) + "\"]" + nodeShape(NormalNodeShape) + newLine)
    for (callee <- callees) {
      sb.append("\"" + name(cfg, callee)+"\"" + nodeShape(NormalNodeShape) + newLine)
    }
    sb.toString
  }

  def drawGraph(cfg:CFG, m:Map[FunctionId, Set[FunctionId]]) = {
    val sb = new StringBuilder
    sb.append("digraph \"CallGraph\" {\n")
    sb.append("\tfontsize=12;node [fontsize=12];edge [fontsize=12];\n\t")

    for (call <- m) {
      sb.append(drawNode(cfg, call._1, call._2))
      sb.append(drawEdge(cfg, call._1, call._2))
    }
    sb.append("\n}\n").toString()
  }
  
  def spawnDot(dotExe: String, outputFile: String, dotFile: File) = {
    val cmdarray = Array(dotExe, "-Tsvg", "-o", outputFile, "-v", dotFile.getAbsolutePath)
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
  
  def writeDotFile(cfg:CFG, m:Map[FunctionId, Set[FunctionId]], dotfile: String) = {
    try {
      val f = new File(dotfile)
      val fw = new FileWriter(f)
      var str = drawGraph(cfg, m)
      fw.write(str)
      fw.close
      f
    } catch {
      case e:Throwable =>
        JSAFError.error("Error writing dot file " + dotfile + ".")
    }
  }
  
  def write(cfg:CFG, callgraph:Map[FunctionId, Set[FunctionId]], dotFile: String, outputFile:String, dotExe:String) = {
    spawnDot(dotExe, outputFile, writeDotFile(cfg, callgraph, dotFile))
  }
}