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
import kr.ac.kaist.safe.analyzer.Worklist
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.{ LINE_SEP, BASE_DIR }

object HTMLWriter {
  val EXC_EDGE = ", width: 2, style: 'dashed', arrow: 'triangle', label:'exc'"
  val RELATED_EDGE = ", width: 1, style: 'dashed', arrow: 'none', label:''"
  val FUNC_LABEL_EDGE = ", width: 1, style: 'dashed', arrow: 'none', label:''"
  val NORMAL_EDGE = ", width: 2, style: 'solid', arrow: 'triangle', label:''"
  val UNREACHABLE_COLOR = "#CCCCCC"
  val REACHABLE_COLOR = "black"

  def getId(block: CFGBlock): String =
    s"${block.func.id}:${block.id}"

  def getLabel(block: CFGBlock): String = {
    val fid = block.func.id
    val bid = block.id
    block match {
      case Entry(_) => s"Entry"
      case Exit(_) => s"Exit"
      case ExitExc(_) => s"ExitExc"
      case _ => s"$block"
    }
  }

  private def isReachable(block: CFGBlock): Boolean =
    !block.getState.isEmpty

  def connectEdge(from: CFGBlock, succs: Set[CFGBlock], edgeStyle: String = NORMAL_EDGE): String = {
    val fromId = getId(from)
    val sb = new StringBuilder
    succs.foreach(to => {
      val toId = getId(to)
      val color =
        if (!isReachable(from) || !isReachable(to)) UNREACHABLE_COLOR
        else REACHABLE_COLOR
      sb.append(s"""{data: {source: '$fromId', target: '$toId'$edgeStyle, color: '$color'}},""" + LINE_SEP)
    })
    sb.toString
  }

  def drawBlock(block: CFGBlock, wlOpt: Option[Worklist]): String = {
    val id = getId(block)
    val label = getLabel(block)
    val inWL = wlOpt match {
      case Some(worklist) => worklist has block
      case None => false
    }
    val color =
      if (!isReachable(block)) UNREACHABLE_COLOR
      else REACHABLE_COLOR
    (block match {
      case (entry: Entry) =>
        val func = entry.func
        val id = func.id
        val label = func.toString
        s"""{data: {id: '$id', content: '$label', border: 0, color: '$REACHABLE_COLOR', inWL: $inWL, bc: 'white'} },""" + LINE_SEP
      case _ => ""
    }) + s"""{data: {id: '$id', content: '$label', border: 2, color: '$color', inWL: $inWL, bc: 'white'} },""" + LINE_SEP
  }

  def drawEdge(block: CFGBlock): String = {
    val sb = new StringBuilder
    block match {
      case entry @ Entry(func) =>
        val id = func.id
        val bid = getId(entry)
        sb.append(s"""{data: {source: '$id', target: '$bid'$FUNC_LABEL_EDGE, color: '$REACHABLE_COLOR'}},""" + LINE_SEP)
      case exit @ Exit(func) =>
        val exitExc = func.exitExc
        sb.append(connectEdge(exit, Set(exitExc), RELATED_EDGE))
        sb.append(connectEdge(exitExc, Set(exit), RELATED_EDGE))
      case (call: Call) =>
        val acall = call.afterCall
        val acatch = call.afterCatch
        sb.append(connectEdge(block, Set(acall, acatch), RELATED_EDGE))
      case _ =>
    }
    block.getAllSucc.foreach {
      // case (typ, blocks) =>
      //   sb.append(connectEdge(block, blocks.toSet, typ match {
      //     case CFGEdgeExc => EXC_EDGE
      //     case _ => NORMAL_EDGE
      //   }))
      case (CFGEdgeExc, blocks) =>
        sb.append(connectEdge(block, blocks.filter(_ match {
          case ExitExc(_) => false
          case _ => true
        }).toSet, EXC_EDGE))
      case (_, blocks) =>
        sb.append(connectEdge(block, blocks.toSet, NORMAL_EDGE))
    }
    sb.toString
  }

  def addInsts(block: CFGBlock): String = {
    val sb = new StringBuilder
    val id = getId(block)
    val label = getLabel(block)
    val func = block.func
    sb.append(s"'$id': [").append(LINE_SEP)
      .append(s"{ kind: 'Block', id: 'block', value: '$label of $func' },").append(LINE_SEP)
    block.getInsts.reverse.foreach(inst => {
      sb.append(s"{ kind: 'Instruction', value: '$inst' },").append(LINE_SEP)
    })
    sb.append(s"]," + LINE_SEP)
    sb.toString
  }

  def addState(block: CFGBlock): String = {
    val sb = new StringBuilder
    val id = getId(block)
    val label = getLabel(block)
    val func = block.func
    if (isReachable(block)) {
      val (_, st) = block.getState.head // TODO it is working only when for each CFGBlock has only one control point.
      sb.append(s"'$id': [").append(LINE_SEP)
      // heap
      val h = st.heap
      sb.append("{ value: {value: 'Heap', open: true, id: 'heap'} },").append(LINE_SEP)
      sb.append("{ value: {value: 'System Locations', id: 'sysLoc'}, parent: 'heap' },").append(LINE_SEP)
      h.map.toSeq
        .sortBy { case (loc, _) => loc }
        .foreach {
          case (loc, obj) =>
            val parent = loc match {
              case BuiltinGlobal.loc => "heap"
              case Loc(SystemAddr(_), _) => "sysLoc"
              case _ => "heap"
            }
            sb.append(s"{ value: {value: '$loc', id: '$loc'}, parent: '$parent' },").append(LINE_SEP)
            obj.toString.split(LINE_SEP).foreach(prop => {
              sb.append(s"{ value: {value: '$prop'}, parent: '$loc' },").append(LINE_SEP)
            })
        }
      // context
      val ctx = st.context
      sb.append("{ value: {value: 'Context', open: true, id: 'ctx'} },").append(LINE_SEP)
      ctx.getMap.toSeq
        .sortBy { case (loc, _) => loc }
        .foreach {
          case (loc, obj) =>
            sb.append(s"{ value: {value: '$loc', id: '$loc'}, parent: 'ctx' },").append(LINE_SEP)
            obj.toString.split(LINE_SEP).foreach(prop => {
              sb.append(s"{ value: {value: '$prop'}, parent: '$loc' },").append(LINE_SEP)
            })
        }

      // old address set
      val old = ctx.old
      val mayOld = old.mayOld.mkString(", ")
      val mustOld = old.mustOld.mkString(", ")
      sb.append(s"{ value: {value: 'mayOld: [$mayOld]'} },").append(LINE_SEP)
      sb.append(s"{ value: {value: 'mustOld: [$mustOld]'} },").append(LINE_SEP)
      sb.append(s"],").append(LINE_SEP)
    }
    sb.toString
  }

  def drawGraph(
    cfg: CFG,
    wlOpt: Option[Worklist]
  ): String = {
    // computes reachable fid_set
    val reachableFunSet = cfg.getAllFuncs.filter(f => isReachable(f.entry))

    // dump each function node
    val blocks = reachableFunSet.foldRight(List[CFGBlock]()) {
      case (func, lst) => func.getAllBlocks ++ lst
    }.reverse

    val base = BASE_DIR + File.separator

    val sb = new StringBuilder
    sb.append(s"""<!DOCTYPE HTML>
<html>
    <head>
        <meta charset="UTF-8">
        <script src="${base}lib/debugger/jquery-2.0.3.min.js"></script>
        <script src="${base}lib/debugger/cytoscape.min.js"></script>
        <script src="${base}lib/debugger/dagre.min.js"></script>
        <script src="${base}lib/debugger/cytoscape-dagre.js"></script>
        <script src="${base}lib/debugger/webix.js" type="text/javascript"></script>
        <link rel="stylesheet" href="${base}lib/debugger/css/webix.css" type="text/css">
        <link rel="stylesheet" href="${base}lib/debugger/css/core.css" type="text/css">
        <script>
var safe_DB = {
  nodes: [
""")
    blocks.foreach(block => sb.append(drawBlock(block, wlOpt)))
    sb.append("""  ],
  edges: [
""")
    blocks.foreach(block => sb.append(drawEdge(block)))
    sb.append("""  ],
  insts: {
""")
    blocks.foreach(block => sb.append(addInsts(block)))
    sb.append("""  },
  state: {
""")
    blocks.foreach(block => sb.append(addState(block)))
    sb.append(s"""  },
};
        </script>
        <script src="${base}lib/debugger/core.js" type="text/javascript"></script>
    </head>
    <body>
        <div id='cy'></div>
    </body>
</html>""")
    sb.toString
  }

  def writeHTMLFile(
    cfg: CFG,
    wlOpt: Option[Worklist] = None,
    htmlfile: String = "cfg.html"
  ): Unit = {
    try {
      val f = new File(htmlfile)
      val fw = new FileWriter(f)
      fw.write(drawGraph(cfg, wlOpt))
      fw.close
    } catch {
      case e: Throwable =>
        println(s"* error writing HTML file $htmlfile.")
    } finally {
      println(s"* success writing HTML file $htmlfile.")
    }
  }
}
