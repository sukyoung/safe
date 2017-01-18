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

import java.io.{ File, FileWriter }
import scala.collection.immutable.TreeMap
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP

object DotWriter {
  private type OrderMap = Map[CFGBlock, Int]
  val CurBlockShape: String = "shape=record, style=bold, fontsize=11"
  val NormalBlockShape: String = "shape=record, fontsize=11"
  val NormalEdgeStyle: String = "style=solid"
  val ExcEdgeStyle: String = "style=dashed,label=\"exc\""
  val CallEdgeStyle: String = "style=solid,label=\"call\""
  val RetEdgeStyle: String = "style=solid,label=\"ret\""
  val call2AftcallEdgeStyle: String = "style=dotted,color=gray,dir=none"
  val exit2ExcExitEdgeStyle: String = "style=invis,dir=none"
  val newLine = ";\n"
  val prefix = "\t"

  def blockShape(shape: String): String = "[" + shape + "]"
  def edgeStyle(style: String): String = "[" + style + "]"
  def blockInstLabel(label: String, block: CFGBlock): String = {
    val sb = new StringBuilder
    val escapeChars = Array('<', '>', '|', '"', '\\', '{', '}')
    sb.append("[label=\"").append(label)
    block match {
      case AfterCall(_, x, _) =>
        sb.append("|{")
        sb.append("RET_VAR(")
        sb.append(escape(x.toString.toArray, escapeChars))
        sb.append(")}")
      case _ =>
    }
    block.getInsts.length match {
      case 0 =>
      case _ => {
        sb.append("|{")
        sb.append(block.getInsts.reverseIterator.map {
          case inst =>
            escape((s"[${inst.id}] $inst").toArray, escapeChars)
        }.mkString("\\l"))
        sb.append("\\l}")
      }
    }
    sb.append("\"]")
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

  def getName(block: CFGBlock): String = block match {
    case Entry(_) => "Entry"
    case Exit(_) => "Exit"
    case ExitExc(_) => "ExitExc"
    case Call(_) => "Call"
    case AfterCall(_, _, _) => "AfterCall"
    case AfterCatch(_, _) => "AfterCatch"
    case NormalBlock(_) => "Block"
    case LoopHead(_) => "LoopHead"
    case ModelBlock(_, _) => "Model"
  }

  def getLabel(block: CFGBlock): String = {
    val fid = block.func.id
    val bid = block.id
    val name = getName(block)
    "f" + fid + name + (block match {
      case Entry(_) | Exit(_) | ExitExc(_) => ""
      case _ => block.id
    })
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
  def drawBlock(cfg: CFG, block: CFGBlock, orderMap: Option[OrderMap], isCur: Boolean = false): String = {
    val order: Option[String] = orderMap.map(_.get(block).fold("")(_.toString))
    val bid = block.id
    val fid = block.func.id
    val label = getName(block) + (block match {
      case Entry(_) | Exit(_) | ExitExc(_) => s" \\[fid=$fid\\]"
      case _ => s" [$bid]"
    })
    prefix +
      getLabel(block) +
      blockShape(if (isCur) CurBlockShape else NormalBlockShape) +
      blockInstLabel(label, block) +
      newLine
  }

  // block1 -> block2 [style=...]
  def drawEdge(cfg: CFG, block: CFGBlock): String = {
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
          .append(" " + getLabel(func.exitExc) + "}")
          .append(newLine)
      case _ =>
    }
    block.getAllSucc.foreach {
      case (typ, blocks) =>
        sb.append(prefix)
          .append(connectEdge(getLabel(block), blocks.toSet, typ match {
            case CFGEdgeNormal => NormalEdgeStyle
            case CFGEdgeExc => ExcEdgeStyle
            case CFGEdgeCall => CallEdgeStyle
            case CFGEdgeRet => RetEdgeStyle
          })).append(newLine)
    }
    sb.toString()
  }

  def drawGraph(
    cfg: CFG,
    orderMap: Option[OrderMap] = None,
    cur: Option[CFGBlock] = None,
    blocksOpt: Option[List[CFGBlock]] = None
  ): String = {
    val blocks = blocksOpt.getOrElse(cfg.getAllBlocks.reverse)
    val sb = new StringBuilder
    sb.append("digraph \"DirectedGraph\" {").append(LINE_SEP)
    sb.append(prefix)
      .append("fontsize=12;node [fontsize=12];edge [fontsize=12]")
      .append(newLine)
    for (block <- blocks) {
      sb.append(drawBlock(cfg, block, orderMap, cur.fold(false)(_ == block))).append(drawEdge(cfg, block))
    }
    sb.append("}").toString
  }

  def writeDotFile(
    cfg: CFG,
    orderMap: Option[OrderMap] = None,
    cur: Option[CFGBlock] = None,
    blocksOpt: Option[List[CFGBlock]] = None,
    dotfile: String = "cfg.gv"
  ): Unit = {
    try {
      val f = new File(dotfile)
      val fw = new FileWriter(f)
      fw.write(drawGraph(cfg, orderMap, cur, blocksOpt))
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
    orderMap: Option[OrderMap] = None,
    cur: Option[CFGBlock] = None,
    blocksOpt: Option[List[CFGBlock]] = None,
    dotFile: String = "cfg.gv",
    outFile: String = "cfg.pdf"
  ): Unit = {
    val cmdarray = Array("dot", "-Tpdf", dotFile, "-o", outFile)
    writeDotFile(cfg, orderMap, cur, blocksOpt, dotFile)
    println("Spawning...: " + cmdarray.mkString(" "))
    try {
      val p = Runtime.getRuntime.exec(cmdarray)
      // TODO handling it takes long time to create pdf file
    } catch {
      case e: Throwable =>
        println(s"* error writing CFG pdf file $outFile.")
        e.printStackTrace
    } finally {
      println(s"* success writing CFG pdf file $outFile.")
    }
  }
}
