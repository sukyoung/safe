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

package kr.ac.kaist.safe.cfg_builder

import java.io.{ File, FileWriter, BufferedInputStream }
import scala.collection.immutable.TreeMap
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.config.Config

object DotWriter {
  private type OrderMap = Map[CFGBlock, Int]
  val NormalBlockShape: String = "shape=record, fontsize=11"
  val NormalEdgeStyle: String = "style=solid"
  val ExcEdgeStyle: String = "style=dashed,label=\"exc\""
  val LoopEdgeStyle: String = "style=solid,label=\"loop\""
  val LoopIterEdgeStyle: String = "style=solid,label=\"loop_iter\""
  val LoopOutEdgeStyle: String = "style=solid,label=\"loop_out\""
  val LoopCondEdgeStyle: String = "style=solid,label=\"loop_cond\""
  val LoopBreakEdgeStyle: String = "style=solid,label=\"loop_break\""
  val LoopReturnEdgeStyle: String = "style=solid,label=\"loop_ret\""
  val call2AftcallEdgeStyle: String = "style=dotted,color=gray,dir=none"
  val exit2ExcExitEdgeStyle: String = "style=invis,dir=none"
  val newLine = ";\n"
  val prefix = "\t"

  def blockShape(shape: String): String = "[" + shape + "]"
  def edgeStyle(style: String): String = "[" + style + "]"
  def blockInstLabel(label: String, block: CFGBlock): String = {
    val sb = new StringBuilder
    val escapeChars = Array('<', '>', '|', '"', '\\')
    sb.append("[label=\"").append(label).append("|{")
    var first = true;
    block match {
      case AfterCall(_, x, _) =>
        sb.append("RET_VAR(")
        sb.append(escape(x.toString.toArray, escapeChars))
        sb.append(")")
        first = false;
      case _ =>
    }
    for (inst <- block.getInsts.reverse) {
      if (first) first = false
      else sb.append("\\l")
      sb.append(escape((s"[${inst.id}] $inst").toArray, escapeChars))
    }
    sb.append("\\l}\"]")
    sb.toString
  }

  def escape(src: Array[Char], cs: Array[Char]): String = {
    var sb = new StringBuilder
    for (ch <- src) {
      if (cs.contains(ch)) sb.append("\\".+(ch))
      else sb.append(ch);
    }
    sb.toString;
  }

  def getLabel(block: CFGBlock): String = {
    block match {
      case e @ Entry(_) => "Entry" + e.func.id
      case e @ Exit(_) => "Exit" + e.func.id
      case e @ ExitExc(_) => "ExitExc" + e.func.id
      case ca @ Call(_) => "Call" + ca.callInst.id
      case ac @ AfterCall(_, _, _) => "AfterCall" + ac.call.callInst.id
      case ac @ AfterCatch(_, _) => "AfterCatch" + ac.call.callInst.id
      case b @ NormalBlock(_) => "Block" + b.id
    }
  }

  def connectEdge(label: String, succs: Set[CFGBlock], edgStyle: String): String = succs.size match {
    case 0 => ""
    case _ =>
      val sb = new StringBuilder
      sb.append(label).append("->{")
      for (succ <- succs) {
        sb.append(getLabel(succ)).append(";")
      }
      sb.append("}").append(edgeStyle(edgStyle))
      sb.toString()
  }

  // block [label=...]
  def drawBlock(cfg: CFG, block: CFGBlock, o: OrderMap): String = {
    val order = o.get(block) match {
      case Some(i) => s"[$i]"
      case None => ""
    }
    prefix +
      getLabel(block) +
      blockShape(NormalBlockShape) +
      blockInstLabel(getLabel(block) + "\\l" + order, block) +
      newLine
  }

  // block1 -> block2 [style=...]
  def drawEdge(cfg: CFG, block: CFGBlock, o: OrderMap): String = {
    val sb = new StringBuilder
    block match {
      case call @ Call(_) =>
        val acall = call.afterCall
        val acatch = call.afterCatch
        sb.append(prefix)
          .append(connectEdge(getLabel(block), Set(acall, acatch), call2AftcallEdgeStyle))
          .append(newLine)
      case exit @ Exit(func) =>
        sb.append(prefix)
          .append(connectEdge(getLabel(block), Set(func.exitExc), exit2ExcExitEdgeStyle))
          .append(newLine)
          .append(prefix)
          .append("{rank=same;" + getLabel(block))
          .append(s" ExitExc${func.id}}")
          .append(newLine)
      case _ =>
    }
    block.getAllSucc.foreach {
      case (typ, blocks) =>
        sb.append(prefix)
          .append(connectEdge(getLabel(block), blocks.toSet, typ match {
            case CFGEdgeNormal => NormalEdgeStyle
            case CFGEdgeExc => ExcEdgeStyle
            case CFGEdgeLoop => LoopEdgeStyle
            case CFGEdgeLoopIter => LoopIterEdgeStyle
            case CFGEdgeLoopOut => LoopOutEdgeStyle
            case CFGEdgeLoopCond => LoopCondEdgeStyle
            case CFGEdgeLoopBreak => LoopBreakEdgeStyle
            case CFGEdgeLoopReturn => LoopReturnEdgeStyle
          })).append(newLine)
    }
    sb.toString()
  }

  def drawGraph(
    cfg: CFG,
    o: OrderMap,
    blocksOpt: Option[List[CFGBlock]] = None
  ): String = {
    val blocks = blocksOpt.getOrElse(cfg.getAllBlocks.reverse)
    val sb = new StringBuilder
    sb.append("digraph \"DirectedGraph\" {").append(Config.LINE_SEP)
    sb.append(prefix)
      .append("fontsize=12;node [fontsize=12];edge [fontsize=12]")
      .append(newLine)
    for (block <- blocks) {
      sb.append(drawBlock(cfg, block, o)).append(drawEdge(cfg, block, o))
    }
    sb.append("}").toString
  }

  def writeDotFile(
    cfg: CFG,
    o: OrderMap,
    blocksOpt: Option[List[CFGBlock]] = None,
    dotfile: String = "cfg.gv"
  ): Unit = {
    try {
      val f = new File(dotfile)
      val fw = new FileWriter(f)
      fw.write(drawGraph(cfg, o, blocksOpt))
      fw.close
    } catch {
      case e: Throwable =>
        println(s"* error writing dot file $dotfile.")
    } finally {
      println(s"* success writing dot file $dotfile.")
    }
  }

  def spawnDot(
    cfg: CFG,
    o: OrderMap,
    blocksOpt: Option[List[CFGBlock]] = None,
    dotFile: String = "cfg.gv",
    outFile: String = "cfg.pdf"
  ): Unit = {
    val cmdarray = Array("dot", "-Tpdf", dotFile, "-o", outFile)
    writeDotFile(cfg, o, blocksOpt, dotFile)
    println("Spawning...: " + cmdarray.mkString(" "))
    try {
      val p = Runtime.getRuntime.exec(cmdarray)
      // TODO handling it takes long time to create pdf file
    } catch {
      case e: Throwable =>
        e.printStackTrace
        println(s"* error writing CFG pdf file $outFile.")
    } finally {
      println(s"* success writing CFG pdf file $outFile.")
    }
  }
}
