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
import kr.ac.kaist.safe.LINE_SEP

object HTMLWriter {
  val EXC_EDGE = ", width: 2, style: 'dashed', arrow: 'triangle', label:'exc'"
  val CALL_TRIPLE_EDGE = ", width: 1, style: 'dashed', arrow: 'none', label:''"
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

  def connectEdge(from: CFGBlock, succs: Set[CFGBlock], edgeStyle: String = NORMAL_EDGE): String = {
    val fromId = getId(from)
    val sb = new StringBuilder
    succs.foreach(to => {
      val toId = getId(to)
      val color =
        if (from.getState.isEmpty || to.getState.isEmpty) UNREACHABLE_COLOR
        else REACHABLE_COLOR
      sb.append(s"""{data: {source: '$fromId', target: '$toId'$edgeStyle, color: '$color'}},""" + LINE_SEP)
    })
    sb.toString
  }

  def drawBlock(block: CFGBlock): String = {
    val id = getId(block)
    val label = getLabel(block)
    val color =
      if (block.getState.isEmpty) UNREACHABLE_COLOR
      else REACHABLE_COLOR
    (block match {
      case (entry: Entry) =>
        val func = entry.func
        val id = func.id
        val label = func.toString
        s"""{data: {id: '$id', content: '$label', border: 0, color: '$REACHABLE_COLOR'} },""" + LINE_SEP
      case _ => ""
    }) + s"""{data: {id: '$id', content: '$label', border: 2, color: '$color'} },""" + LINE_SEP
  }

  def drawEdge(block: CFGBlock): String = {
    val sb = new StringBuilder
    block match {
      case (entry: Entry) =>
        val func = entry.func
        val id = func.id
        val bid = getId(entry)
        sb.append(s"""{data: {source: '$id', target: '$bid'$FUNC_LABEL_EDGE, color: '$REACHABLE_COLOR'}},""" + LINE_SEP)
      case (call: Call) =>
        val acall = call.afterCall
        val acatch = call.afterCatch
        sb.append(connectEdge(block, Set(acall, acatch), CALL_TRIPLE_EDGE))
      case _ =>
    }
    block.getAllSucc.foreach {
      // case (typ, blocks) =>
      //   sb.append(connectEdge(block, blocks.toSet, typ match {
      //     case CFGEdgeExc => EXC_EDGE
      //     case _ => NORMAL_EDGE
      //   }))
      case (CFGEdgeExc, _) =>
      case (_, blocks) =>
        sb.append(connectEdge(block, blocks.toSet, NORMAL_EDGE))
    }
    sb.toString
  }

  def drawGraph(
    cfg: CFG
  ): String = {
    // computes reachable fid_set
    val reachableFunSet = cfg.getAllFuncs.filter(!_.entry.getState.isEmpty)

    // dump each function node
    val blocks = reachableFunSet.foldRight(List[CFGBlock]()) {
      case (func, lst) => func.getAllBlocks ++ lst
    }.reverse

    val sb = new StringBuilder
    sb.append("""<html>
  <head>
    <script src="http://code.jquery.com/jquery-2.0.3.min.js"></script>
    <script src="http://cytoscape.github.io/cytoscape.js/api/cytoscape.js-latest/cytoscape.min.js"></script>
    <script src="https://cdn.rawgit.com/cpettitt/dagre/v0.7.4/dist/dagre.min.js"></script>
    <script src="https://cdn.rawgit.com/cytoscape/cytoscape.js-dagre/1.1.2/cytoscape-dagre.js"></script>
    <style>
      #cy {
        width: 100%;
        height: 100%;
        position: absolute;
        left: 0;
        top: 0;
        z-index: 999;
      }
    </style>

    <script>
$(function(){

  var cy = window.cy = cytoscape({
    container: document.getElementById('cy'),
    layout: {
      name: 'dagre'
    },

    style: [
    {
      selector: 'node',
      style: {
        'shape': 'roundrectangle',
        'border-width': 'data(border)',
        'border-color': 'data(color)',
        'content': 'data(content)',
        'text-valign': 'center',
        'text-halign': 'center',
        'color': 'data(color)',
        'width': '120',
        'background-color': 'white',
      }
    },

    {
      selector: 'edge',
      style: {
        'width': 'data(width)',
        'target-arrow-shape': 'data(arrow)',
        'line-color': 'data(color)',
        'target-arrow-color': 'data(color)',
        'curve-style': 'bezier',
        'line-style': 'data(style)',
        'label': 'data(label)',
        'text-rotation': 'autorotate',
        'text-background-opacity': 1,
        'text-background-color': 'white',
      }
    }
    ],

    elements: {
      nodes: [
""")
    blocks.foreach(block => sb.append(drawBlock(block)))
    sb.append("""
      ],
      edges: [
""")
    blocks.foreach(block => sb.append(drawEdge(block)))
    sb.append("""      ]
    },
  });

});
    </script>
  </head>
  <body>
    <div id="cy"></div>
  </body>
</html>""")
    sb.toString
  }

  def writeHTMLFile(
    cfg: CFG,
    htmlfile: String = "cfg.html"
  ): Unit = {
    try {
      val f = new File(htmlfile)
      val fw = new FileWriter(f)
      fw.write(drawGraph(cfg))
      fw.close
    } catch {
      case e: Throwable =>
        println(s"* error writing HTML file $htmlfile.")
    } finally {
      println(s"* success writing HTML file $htmlfile.")
    }
  }
}
