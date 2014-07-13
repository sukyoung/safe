/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.visualization

import kr.ac.kaist.jsaf.analysis.typing.OrderMap
import java.io.File
import java.io.FileWriter
import kr.ac.kaist.jsaf.exceptions.JSAFError
import kr.ac.kaist.jsaf.analysis.typing.Worklist
import scala.collection.mutable.ListBuffer
import java.io.IOException
import java.io.BufferedInputStream
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import kr.ac.kaist.jsaf.analysis.cfg._


object FunCFGWriter {
  def NormalNodeShape:String = "fontname=\"Consolas\" shape=record, fontsize=10"
  def NormalEdgeStyle:String = "fontname=\"Consolas\" style=solid"
  def ExcEdgeStyle:String = "fontname=\"Consolas\" style=dashed,label=\"exc\""
  def LoopEdgeStyle:String = "fontname=\"Consolas\" style=solid,label=\"loop\""
  def call2AftcallEdgeStyle:String = "fontname=\"Consolas\" style=dotted,color=gray,dir=none"
  def exit2ExcExitEdgeStyle:String = "fontname=\"Consolas\" style=invis,dir=none"
  def newLine = ";\n\t"
  
  def nodeShape(shape:String):String = "[" + shape + "]"
  def edgeStyle(style:String):String = "[" + style + "]"
  def nodeInstLabel(label:String, insts:List[CFGInst], returnVar:Option[CFGId]):String = {
    val sb = new StringBuilder
    val escapeChars = Array('<','>','|','"','\\')
    sb.append("[label=\"").append(label).append("|{")
    var first = true;
    returnVar match {
      case Some(x) =>
        sb.append("[EDGE] after-call(")
        sb.append(escape(x.toString().toArray, escapeChars))
        sb.append(")")
        first = false;
      case None => ()
    }
    for(inst<-insts) {
      if (first) first = false
      else sb.append("\\l")
      sb.append(escape(inst.toString().toArray, escapeChars))
    }
    sb.append("\\l}\"]")
    sb.toString
  }
  
  def escape(src:Array[Char], cs:Array[Char]):String = {
    var sb = new StringBuilder
    for(ch <-src) {
      if(cs.contains(ch))	sb.append("\\".+(ch))
      else	sb.append(ch);
    }
    sb.toString();
  }

  def getLabel(node:Node):String = {
    node._2 match {
      case LBlock(id) => "Block" + id
      case LEntry => "Entry" + node._1
      case LExit => "Exit" + node._1
      case LExitExc => "ExitExc" + node._1
    }
  }

  def connectEdge(label:String, succs:Set[Node], edgStyle:String, o:OrderMap):String = {
    val sb = new StringBuilder
    sb.append(label).append("->{")
    for(succ <-succs) {
      sb.append(getLabel(succ)).append(";")
    }
    sb.append("}").append(edgeStyle(edgStyle))
    sb.toString()
  }

  // node [label=...]
  def drawNode(cfg:CFG, node:Node, o:OrderMap):String = node._2 match {
    case LBlock(id) =>
      cfg.getCmd(node) match {
        case Block(insts) =>
          val order = o.get(node) match {
		        case Some(i) => "["+i+"]"
		        case None => ""
          }
          getLabel(node) + 
          nodeShape(NormalNodeShape) + 
          nodeInstLabel(getLabel(node)+"\\l"+order, insts, cfg.getReturnVar(node)) + 
          newLine
      }
    case _ => 	
      val order = o.get(node) match {
        case Some(i) => "["+i+"]"
        case None => ""
      }
      getLabel(node) + 
      nodeShape(NormalNodeShape) + 
      "[label=\"" + getLabel(node)+"\\l" + order + "\"]" +  
      newLine
  }
  
  // node1 -> node2 [style=...]
  def drawEdge(cfg:CFG, node:Node, o:OrderMap):String = node._2 match {
    case LBlock(id) =>
      cfg.getCmd(node) match {
        case Block(insts) =>
          val sb = new StringBuilder
          if (cfg.getCalls.contains(node)) {
            val ac = cfg.getAftercallFromCall(node)
            sb.append(connectEdge(getLabel(node), Set(ac), call2AftcallEdgeStyle, o)).append(newLine).toString()
          }

          if(!cfg.getSucc(node).isEmpty) {
            sb.append(connectEdge(getLabel(node), cfg.getSucc(node), NormalEdgeStyle, o)).append(newLine)
          }
          cfg.getExcSucc.get(node) match {
            case Some(succ) => 
              sb.append(connectEdge(getLabel(node), Set(succ), ExcEdgeStyle, o)).append(newLine)
            case None => ()
          }
          if(!cfg.getLoopSucc(node).isEmpty) {
            sb.append(connectEdge(getLabel(node), cfg.getLoopSucc(node), LoopEdgeStyle, o)).append(newLine)
          }
          sb.toString()
      }
    case LEntry|LExitExc if(!cfg.getSucc(node).isEmpty) => connectEdge(getLabel(node), cfg.getSucc(node), NormalEdgeStyle, o) + newLine 
    case LExit => connectEdge(getLabel(node), Set((node._1,LExitExc)), exit2ExcExitEdgeStyle, o) + newLine + "{rank=same;" + getLabel(node) + " " + "ExitExc"+node._1 +"}" + newLine
    case _ => ""
  }
  
  def drawGraph(cfg:CFG, nodes:List[Node], o:OrderMap) = {
    //val nodes = cfg.getNodes.reverse
    val sb = new StringBuilder
    sb.append("digraph \"DirectedGraph\" {\n")
    sb.append("\tfontsize=12;node [fontsize=12];edge [fontsize=12];\n\t")
    for(node <-nodes) {
      sb.append(drawNode(cfg, node, o)).append(drawEdge(cfg, node, o))
    }
    sb.append("\n}\n").toString()
  }
  
  def spawnDot(dotExe: String, outputFile: String, dotFile: File): Unit = {
    val cmdarray = Array(dotExe, "-Tsvg", "-o", outputFile, "-v", dotFile.getAbsolutePath)
    System.out.println("Spawning process" + cmdarray.foldLeft("")((r,s) => r + " " + s))
    try {
      val p = Runtime.getRuntime.exec(cmdarray)
      val output = new BufferedInputStream(p.getInputStream)
      val error = new BufferedInputStream(p.getErrorStream)
      var repeat = true
      var repeatCount = 0
      while (repeat) {
        try {
          Thread.sleep(500)
          repeatCount = repeatCount + 1
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
          if (repeatCount > 120) {
            p.destroy
            System.out.println("Drawing %s takes more than one minute. Aborted.".format(outputFile))
            return
          }
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

  def writeDotFile(cfg:CFG, nodes:List[Node], o: OrderMap, dotpath: String, funcId:FunctionId) = {
    try {
      val f = new File(dotpath+"/f"+funcId+".dot")
      val fw = new FileWriter(f)
      fw.write(drawGraph(cfg, nodes, o));
      fw.close
      f
    } catch {
      case e:Throwable =>
        JSAFError.error("Error writing dot file " + dotpath + ".")
    }
  }
  
  def getIdNodes(nodes:List[Node], funcId:Int):List[Node] = {
    var newNodes:List[Node] = List()
    for (node <- nodes) {
      if (node._1 == funcId)
        newNodes ::= node
    }
    newNodes
  }

  def write(cfg:CFG, callgraph:List[FunctionId], nodes:List[Node], outputPath: String, dotExe: String) = {
    val o = Worklist.computes(cfg).getOrder()
    nodes.par.map(node => node._2 match {
      case LEntry  if callgraph.contains(node._1) =>
        val funcId:FunctionId = node._1
        spawnDot(dotExe, outputPath+"/f"+funcId+".svg", writeDotFile(cfg, getIdNodes(nodes, funcId), o, outputPath, funcId))
      case _ =>
    })
  }
}
