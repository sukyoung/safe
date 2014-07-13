/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.cfg
import kr.ac.kaist.jsaf.exceptions.JSAFError
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.BufferedInputStream
import kr.ac.kaist.jsaf.analysis.typing.OrderMap
import kr.ac.kaist.jsaf.analysis.typing.Fixpoint
import kr.ac.kaist.jsaf.analysis.typing.Worklist
import kr.ac.kaist.jsaf.analysis.typing.Environment
import kr.ac.kaist.jsaf.analysis.typing.SparseEnv

object DotWriter {
  def NormalNodeShape:String = "shape=record, fontsize=11"
  def NormalEdgeStyle:String = "style=solid"
  def ExcEdgeStyle:String = "style=dashed,label=\"exc\""
  def LoopEdgeStyle:String = "style=solid,label=\"loop\""
  def LoopIterEdgeStyle:String = "style=solid,label=\"loop_iter\""
  def LoopOutEdgeStyle:String = "style=solid,label=\"loop_out\""
  def call2AftcallEdgeStyle:String = "style=dotted,color=gray,dir=none"
  def exit2ExcExitEdgeStyle:String = "style=invis,dir=none"
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
      sb.append(escape(("[" + inst.getInstId.toString + "] " + inst.toString).toArray, escapeChars))
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
      // after-catch
      if (cfg.getAftercatches.contains(node)) {
        val order = o.get(node) match {
          case Some(i) => "["+i+"]"
          case None => ""
        }
        val sb = new StringBuilder
        sb.append("[label=\"").append(getLabel(node)+"\\l"+order).append("|{")
        sb.append("[EDGE] after-catch")
        sb.append("\\l}\"]")
        
        getLabel(node) +
        nodeShape(NormalNodeShape) +
        sb.toString +
        newLine
      } else
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
            val ac_ = cfg.getAftercatchFromCall(node)
            sb.append(connectEdge(getLabel(node), Set(ac, ac_), call2AftcallEdgeStyle, o)).append(newLine).toString()
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
          if(!cfg.getLoopIterSucc(node).isEmpty) {
            sb.append(connectEdge(getLabel(node), cfg.getLoopIterSucc(node), LoopIterEdgeStyle, o)).append(newLine)
          }
          if(!cfg.getLoopOutSucc(node).isEmpty) {
            sb.append(connectEdge(getLabel(node), cfg.getLoopOutSucc(node), LoopOutEdgeStyle, o)).append(newLine)
          }



          sb.toString()
      }
    case LEntry|LExitExc if(!cfg.getSucc(node).isEmpty) => connectEdge(getLabel(node), cfg.getSucc(node), NormalEdgeStyle, o) + newLine
    case LExit => connectEdge(getLabel(node), Set((node._1,LExitExc)), exit2ExcExitEdgeStyle, o) + newLine + "{rank=same;" + getLabel(node) + " " + "ExitExc"+node._1 +"}" + newLine
    case _ => ""
  }

  def drawGraph(cfg:CFG, o:OrderMap) = {
    val nodes = cfg.getNodes.reverse
    val sb = new StringBuilder
    sb.append("digraph \"DirectedGraph\" {\n")
    sb.append("\tfontsize=12;node [fontsize=12];edge [fontsize=12];\n\t")
    for(node <-nodes) {
      sb.append(drawNode(cfg, node, o)).append(drawEdge(cfg, node, o))
    }
    sb.append("\n}\n").toString()
  }

  def spawnDot(dotExe: String, outputFile: String, dotFile: File) = {
    val cmdarray = Array(dotExe, "-Tsvg", "-o", outputFile, "-v", dotFile.getAbsolutePath)
    System.out.println("Spawning process" + cmdarray.foldLeft("")((r,s) => r + " " + s))
    try {
      val p = Runtime.getRuntime.exec(cmdarray)
      new BufferedInputStream(p.getInputStream).close
      new BufferedInputStream(p.getErrorStream).close
      //val output = new BufferedInputStream(p.getInputStream)
      //val error = new BufferedInputStream(p.getErrorStream)
      var repeat = true
      while (repeat) {
        try {
          Thread.sleep(100)
        } catch {
          case e1:InterruptedException =>
          e1.printStackTrace
          // just ignore and continue
        }
        /*val outputLength = output.available
        if (outputLength > 0) {
          output.skip(outputLength)
          //val data = ListBuffer[Byte]()
          //val nRead = output.read(data.toArray)
          //System.err.println("read " + nRead + " bytes from output stream")
        }
        val errorLength = error.available
        if (errorLength > 0) {
          error.skip(errorLength)
          //val data = ListBuffer[Byte]()
          //val nRead = error.read(data.toArray)
          //System.err.println("read " + nRead + " bytes from error stream")
        }*/
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

  def writeDotFile(g: CFG, o: OrderMap, dotfile: String) = {
    try {
      val f = new File(dotfile)
      val fw = new FileWriter(f)
      fw.write(drawGraph(g, o));
      fw.close
      f
    } catch {
      case e:Throwable =>
        JSAFError.error("Error writing dot file " + dotfile + ".")
    }
  }

  def write(g: CFG, dotFile: String, outputFile: String, dotExe: String) = {
    val wo = Worklist.computes(g)
    val o = wo.getOrder()
    spawnDot(dotExe, outputFile, writeDotFile(g, o, dotFile))
  }

  def ddgwrite(g: CFG, env: Environment, dotFile: String, outputFile: String, dotExe: String, ddg0: Boolean, global: Boolean) = {
    val wo = Worklist.computes(g)
    val o = wo.getOrder()
    if(!global) spawnDot(dotExe, "local"+outputFile, ddgwriteDotFile(env.asInstanceOf[SparseEnv].getDDGStr(ddg0), o, "local"+dotFile))
  }

  def ddgwriteDotFile(str: String, o: OrderMap, dotfile: String) = {
    try {
      val f = new File(dotfile)
      val fw = new FileWriter(f)
      fw.write(str);
      fw.close
      f
    } catch {
      case e:Throwable =>
        JSAFError.error("Error writing dot file " + dotfile + ".")
    }
  }

  def fgwrite(g: CFG, env:Environment, dotFile: String, outputFile: String, dotExe: String, global: Boolean) = {
    val wo = Worklist.computes(g)
    val o = wo.getOrder()
    spawnDot(dotExe, "local"+outputFile, ddgwriteDotFile(env.asInstanceOf[SparseEnv].getFGStr(global), o, "local"+dotFile))
  }
}
