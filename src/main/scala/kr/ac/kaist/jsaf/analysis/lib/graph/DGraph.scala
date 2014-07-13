/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.lib.graph

import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import scala.math.min

/**
 * A general Directed Graph
 */
class DGraph[Node: ClassManifest](nodes: Set[Node], override var entry: Node) extends Graph[Node] {
  var succs: Map[Node, Set[Node]] = HashMap()
  var preds: Map[Node, Set[Node]] = HashMap()

  override def getNodes = nodes

  override def getSuccs(node: Node): Set[Node] = {
    succs.get(node) match {
      case Some(succs) => succs
      case None => HashSet()
    }
  }

  override def getPreds(node: Node): Set[Node] = {
    preds.get(node) match {
      case Some(preds) => preds
      case None => HashSet()
    }
  }

  def addEdge(src: Node, dst: Node) = {
    succs += (src -> (getSuccs(src) + dst))
    preds += (dst -> (getPreds(dst) + src))
  }

  def +(g: DGraph[Node]): DGraph[Node] = {
    val new_succs = g.succs.foldLeft(this.succs)((succs, kv) => {
      succs.get(kv._1) match {
        case Some(v) => succs + (kv._1 -> (v ++ kv._2))
        case None => succs + (kv._1 -> kv._2)
      }
    })
    val new_preds = g.preds.foldLeft(this.preds)((preds, kv) => {
      preds.get(kv._1) match {
        case Some(v) => preds + (kv._1 -> (v ++ kv._2))
        case None => preds + (kv._1 -> kv._2)
      }
    })
    var newgraph = new DGraph[Node](this.nodes ++ g.getNodes, this.entry)
    newgraph.succs = new_succs
    newgraph.preds = new_preds
    newgraph
  }

  def prunedGraph = {
    DGraph.pruning[Node](this, reachable)
  }

  def removeInedges(n: Node): HashSet[Node] = {
    var removed = HashSet[Node]()
    // find back-edges to a given node n.
    val inedges = succs.filter(kv => kv._2.contains(n))

    // removes back-edges to the node.
    inedges.foreach(kv => {
      removed += kv._1
      val newset = kv._2 - n
      if (newset.isEmpty) succs -= kv._1
      else succs += (kv._1 -> newset)
    })

    removed
  }
}

object DGraph {
  def makeGraph[Node: ClassManifest](nodes: Set[Node], entry: Node, succs: Node => Set[Node]) = {
    var succs_map = HashMap[Node, Set[Node]]()
    var preds_map = HashMap[Node, Set[Node]]()

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

    var g = new DGraph[Node](nodes, entry)
    g.succs = succs_map
    g.preds = preds_map
    g
  }

  def fromEdges[Node: ClassManifest](nodes: Set[Node], entry: Node, edges: Set[(Node, Node)]) = {
    var g = new DGraph[Node](nodes, entry)
    edges.foreach(kv => g.addEdge(kv._1, kv._2))
    g
  }

  /**
   * Prunes a given graph [g] excepts [nodes].
   * XXX: pruning returns one direction graph because it is not necessary so far.
   */
  def pruning[Node: ClassManifest](g: DGraph[Node], nodes: Set[Node]): DGraph[Node] = {
    var succs_map: Map[Node, Set[Node]] = HashMap()
    g.succs.foreach(kv => {
      if (nodes.contains(kv._1)) {
        val dsts = kv._2 & nodes
        if (!dsts.isEmpty) {
          succs_map += (kv._1 -> dsts)
        }
      }
    })

    var newgraph = new DGraph(nodes, g.entry)
    newgraph.succs = succs_map
    newgraph
  }
}
