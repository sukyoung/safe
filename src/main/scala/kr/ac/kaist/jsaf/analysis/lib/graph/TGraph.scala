/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.lib.graph

import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import scala.collection.mutable.{ Stack }
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.lib._
import scala.math.min

/**
 * Control-flow Graph.
 * TGraph has two kinds of edges: normal edge and exception edge.
 * In TGraph, call and after-call node are connected with a normal edge.
 */
class TGraph[Node](var entry: Node) {
  protected var succs: Map[Node, Set[Node]] = HashMap()
  protected var preds: Map[Node, Set[Node]] = HashMap()
  protected var excSucc: Map[Node, Node] = HashMap()
  protected var excPreds: Map[Node, Set[Node]] = HashMap()
  protected var nodes: Set[Node] = HashSet()

  def getNodes = nodes
  def addNodes(s: Set[Node]): Unit = {
    nodes ++= s
  }
  def addNode(n: Node): Unit = {
    nodes += n
  }

  def getNormalSuccs(node: Node): Set[Node] = getSet(succs, node)
  def getNormalPreds(node: Node): Set[Node] = getSet(preds, node)
  def getExcSuccs(node: Node): Set[Node] = {
    excSucc.get(node) match {
      case Some(n) => Set(n)
      case None => Set()
    }
  }
  def getExcPreds(node: Node): Set[Node] = getSet(excPreds, node)

  def addEdge(src: Node, dst: Node) = {
    succs += (src -> (getNormalSuccs(src) + dst))
    preds += (dst -> (getNormalPreds(dst) + src))
  }

  def addExcEdge(src: Node, dst: Node) = {
    excSucc += (src -> dst)
    excPreds += (dst -> (getSet(excPreds, dst) + src))
  }

  private def NormalEdgeStyle:String = "fontname=\"Consolas\" style=solid"

  private def ExcEdgeStyle:String = "fontname=\"Consolas\" style=dashed"

  def toDot_String(getLabel:(Node => String)): String = {
    var str = ""
    succs.foreach{ case (src, dsts) => {
      dsts.foreach(dst => str += (getLabel(src)+ "->"+getLabel(dst)+"["+NormalEdgeStyle+"];\n"))
    }}
    excSucc.foreach{ case (src, dst) => {
      str += (getLabel(src)+ "->"+getLabel(dst)+"["+ExcEdgeStyle+"];\n")
    }}
    str
  }
}

object TGraph {
  def makeGraph[Node](nodes: Set[Node], entry: Node, succs: Node => Set[Node], succs_e: Node => Set[Node]) = {
    var succs_map = HashMap[Node, Set[Node]]()
    var preds_map = HashMap[Node, Set[Node]]()
    var esuccs_map = HashMap[Node, Node]()
    var epreds_map = HashMap[Node, Set[Node]]()

    nodes.foreach((n) => {
      val succs_set = succs(n)
      if (!succs_set.isEmpty) {
        succs_map += (n -> succs_set)
        succs_set.foreach((succ) => {
          preds_map.get(succ) match {
            case Some(s) => preds_map += (succ -> (s + n))
            case None => preds_map += (succ -> HashSet(n))
          }
        })
      }
    })
    nodes.foreach((n) => {
      val esuccs_set = succs_e(n)
      if (!esuccs_set.isEmpty) {
        esuccs_map += (n -> esuccs_set.head)
        esuccs_set.foreach(succ => {
          epreds_map.get(succ) match {
            case Some(s) => epreds_map += (succ -> (s + n))
            case None => epreds_map += (succ -> HashSet(n))
          }
        })
      }
    })

    val g = new TGraph(entry)
    g.addNodes(nodes)
    g.succs = succs_map
    g.preds = preds_map
    g.excSucc = esuccs_map
    g.excPreds = epreds_map
    g
  }
}
