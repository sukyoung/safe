/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.html_debugger

import java.io.{ File, FileWriter }

import kr.ac.kaist.safe.analyzer.domain.AbsState
import kr.ac.kaist.safe.analyzer.{ Semantics, Worklist }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.web.domain._
import kr.ac.kaist.safe.{ LINE_SEP, SEP }
import org.apache.commons.io.FileUtils

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

  private def isReachable(block: CFGBlock, sem: Semantics): Boolean = sem.getState(block).exists {
    case (tp, oldSt) => block.getInsts.lastOption match {
      case None => true
      case Some(inst) => {
        val st = inst match {
          case inst: CFGAssert => {
            val (st, _) = sem.I(inst, oldSt, AbsState.Bot)
            st
          }
          case _ => oldSt
        }
        !st.isBottom
      }
    }
  }

  def connectEdge(from: CFGBlock, succs: Set[CFGBlock], edgeStyle: String = NORMAL_EDGE, sem: Semantics): String = {
    val fromId = getId(from)
    val sb = new StringBuilder
    succs.foreach(to => {
      val toId = getId(to)
      val color =
        if (!isReachable(from, sem) || !isReachable(to, sem)) UNREACHABLE_COLOR
        else REACHABLE_COLOR
      sb.append(s"""{data: {source: '$fromId', target: '$toId'$edgeStyle, color: '$color'}},""" + LINE_SEP)
    })
    sb.toString
  }

  def drawBlock(block: CFGBlock, wlOpt: Option[Worklist], sem: Semantics): String = {
    val id = getId(block)
    val label = getLabel(block)
    val inWL = wlOpt match {
      case Some(worklist) => worklist has block
      case None => false
    }
    val color =
      if (!isReachable(block, sem)) UNREACHABLE_COLOR
      else REACHABLE_COLOR
    (block match {
      case (entry: Entry) =>
        val func = entry.func
        val id = func.id
        val label = func.toString
        s"""{data: {id: '$id', content: '$label', border: 0, color: '$REACHABLE_COLOR', inWL: false, bc: 'white'} },""" + LINE_SEP
      case _ => ""
    }) +
      s"""{data: {id: '$id', content: '$label', border: 2, color: '$color', inWL: $inWL, bc: 'white'} },""" + LINE_SEP
  }

  def drawEdge(block: CFGBlock, sem: Semantics): String = {
    val sb = new StringBuilder
    block match {
      case entry @ Entry(func) =>
        val id = func.id
        val bid = getId(entry)
        sb.append(s"""{data: {source: '$id', target: '$bid'$FUNC_LABEL_EDGE, color: '$REACHABLE_COLOR'}},""" + LINE_SEP)
      case exit @ Exit(func) =>
        val exitExc = func.exitExc
        sb.append(connectEdge(exit, Set(exitExc), RELATED_EDGE, sem))
        sb.append(connectEdge(exitExc, Set(exit), RELATED_EDGE, sem))
      case (call: Call) =>
        val acall = call.afterCall
        val acatch = call.afterCatch
        sb.append(connectEdge(block, Set(acall, acatch), RELATED_EDGE, sem))
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
        }).toSet, EXC_EDGE, sem))
      case (_, blocks) =>
        sb.append(connectEdge(block, blocks.toSet, NORMAL_EDGE, sem))
    }
    sb.toString
  }

  def addInsts(block: CFGBlock): String = {
    val sb = new StringBuilder
    val id = getId(block)
    val label = getLabel(block)
    val func = block.func
    sb.append(s"'$id': [").append(LINE_SEP)
      .append(s"{ kind: 'Block', id: 'block', bid: '$id', value: '$label of $func' },").append(LINE_SEP)
    block.getInsts.reverse.foreach(inst => {
      val instStr = inst.toString()
        .replaceAll("\'", "\\\\\'")
      sb.append(s"{ kind: 'Instructions', value: '$instStr', id: '${inst.id}', bid: '${id}' },").append(LINE_SEP)
    })
    sb.append(s"]," + LINE_SEP)
    sb.toString
  }

  def addState(block: CFGBlock, sem: Semantics): String = {
    val sb = new StringBuilder
    val id = getId(block)
    if (isReachable(block, sem)) {
      val (_, st) = sem.getState(block).head // TODO it is working only when for each CFGBlock has only one control point.
      sb.append(addSingleState(id, st))
    }
    sb.toString
  }

  def addSingleState(id: String, st: AbsState): String = {
    val sb = new StringBuilder
    sb.append(s"'$id': [").append(LINE_SEP)
    // heap
    val h = st.heap
    sb.append("{ value: {value: 'Heap', open: true, id: 'heap'} },").append(LINE_SEP)
    h.getMap match {
      case None => {
        val value =
          if (h.isBottom) "Bot"
          else "Top"
        sb.append(s"{ value: '$value' },")
      }
      case Some(map) => {
        sb.append("{ value: {value: 'Predefined Locations', id: 'predLoc'}, parent: 'heap' },").append(LINE_SEP)
        map.toSeq
          .sortBy { case (loc, _) => loc }
          .foreach {
            case (loc, obj) =>
              val parent = loc match {
                case BuiltinGlobal.loc => "heap"
                case l if !l.isUser => "predLoc"
                case _ => "heap"
              }
              sb.append(s"{ value: {value: '$loc', id: '$loc'}, parent: '$parent' },").append(LINE_SEP)
              obj.toString.split(LINE_SEP).foreach(prop => {
                val propStr = prop.replaceAll("\'", "\\\\\'")
                sb.append(s"{ value: {value: '$propStr'}, parent: '$loc' },").append(LINE_SEP)
              })
          }
      }
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
            val propStr = prop.replaceAll("\'", "\\\\\'")
            sb.append(s"{ value: {value: '$propStr'}, parent: '$loc' },").append(LINE_SEP)
          })
      }

    val thisBinding = ctx.thisBinding
    sb.append(s"{ value: {value: 'this: $thisBinding'} },").append(LINE_SEP)
    // old allocation site set
    val old = ctx.old
    val mayOld = old.mayOld.mkString(", ")
    val mustOld =
      if (old.mustOld == null) "bottom"
      else old.mustOld.mkString(", ")
    sb.append(s"{ value: {value: 'mayOld: [$mayOld]'} },").append(LINE_SEP)
    sb.append(s"{ value: {value: 'mustOld: [$mustOld]'} },").append(LINE_SEP)
    sb.append(s"],").append(LINE_SEP)

    sb.toString
  }

  def renderGraphStates(cfg: CFG, sem: Semantics, wlOpt: Option[Worklist], simplified: Boolean = false): String = {
    // computes reachable fid_set
    val reachableFunSet = cfg.getAllFuncs.filter(f => isReachable(f.entry, sem))

    // dump each function node
    val blocks = reachableFunSet.foldRight(List[CFGBlock]()) {
      case (func, lst) => func.getAllBlocks ++ lst
    }.reverse

    if (simplified) {
      s"""{
        "nodes": [${blocks.map(block => drawBlock(block, wlOpt, sem)).mkString("")}],
        "edges": [${blocks.map(block => drawEdge(block, sem)).mkString("")}],
      }""".stripMargin
    } else {
      s"""{
        "nodes": [${blocks.map(block => drawBlock(block, wlOpt, sem)).mkString("")}],
        "edges": [${blocks.map(block => drawEdge(block, sem)).mkString("")}],
        "insts": {${blocks.map(block => addInsts(block)).mkString("")}},
        "state": {${blocks.map(block => addState(block, sem)).mkString("")}}
      }""".stripMargin
    }
  }

  def getBlockStates(cfg: CFG, sem: Semantics, bid: String): BlockStates = {
    // computes reachable fid_set
    val reachableFunSet = cfg.getAllFuncs.filter(f => isReachable(f.entry, sem))

    // dump each function node
    val blocks = reachableFunSet.foldRight(List[CFGBlock]()) {
      case (func, lst) => func.getAllBlocks ++ lst
    }.reverse

    val blockOpt = blocks.find(block => getId(block) == bid)
    if (blockOpt.isDefined) {
      BlockStates(
        "{" + addInsts(blockOpt.get) + "}",
        "{" + addState(blockOpt.get, sem) + "}"
      )
    } else {
      throw new IllegalArgumentException
    }
  }

  def drawGraph(
    cfg: CFG,
    sem: Semantics,
    wlOpt: Option[Worklist]
  ): String = {
    // computes reachable fid_set
    val reachableFunSet = cfg.getAllFuncs.filter(f => isReachable(f.entry, sem))

    // dump each function node
    val blocks = reachableFunSet.foldRight(List[CFGBlock]()) {
      case (func, lst) => func.getAllBlocks ++ lst
    }.reverse

    val sb = new StringBuilder
    sb.append(
      s"""<!doctype html>
<html>
    <head>
        <meta charset="UTF-8">
        <script src="assets/js/jquery-2.0.3.min.js"></script>
        <script src="assets/js/cytoscape.min.js"></script>
        <script src="assets/js/dagre.min.js"></script>
        <script src="assets/js/cytoscape-dagre.js"></script>
        <script src="assets/js/webix.js" type="text/javascript"></script>
        <link rel="stylesheet" href="assets/css/webix.css" type="text/css">
        <link rel="stylesheet" href="assets/css/core.css" type="text/css">
        <script>
          var safe_DB = ${renderGraphStates(cfg, sem, wlOpt)};
        </script>
        <script src="assets/js/core.js" type="text/javascript"></script>
    </head>
    <body>
        <div id="cy"></div>
    </body>
</html>"""
    )
    sb.toString
  }

  def writeHTMLFile(
    cfg: CFG,
    sem: Semantics,
    wlOpt: Option[Worklist] = None,
    htmlfile: String = "cfg.html"
  ): Unit = {
    try {
      // copy libraries
      val src = new File(Useful.path("src", "main", "resources", "assets"))
      val dest = new File("debugger" + SEP + "assets")
      FileUtils.copyDirectory(src, dest)
      println("* copy debugger libraries.")

      val f = new File("debugger" + SEP + htmlfile)
      val fw = new FileWriter(f)
      fw.write(drawGraph(cfg, sem, wlOpt))
      fw.close()
      println(s"* success writing HTML file $htmlfile.")
    } catch {
      case e: Throwable =>
        println(s"* error writing HTML file $htmlfile.")
        throw e
    }
  }
}
