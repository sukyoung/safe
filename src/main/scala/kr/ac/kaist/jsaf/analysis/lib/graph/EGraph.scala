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
 * Expanded Control-flow Graph.
 * 
 * EGraph = ENode x EEdge
 * ENode = Node x Kind
 * EEdge = ENode x ENode x EdgeType
 *
 * Kind ::= I | O | OE
 * EdgeType ::= Normal | Exception
 *
 * Each of CFG node consists of three kinds of ENode: I, O, OE.
 * - I: Input node.
 * - O: Normal Out node.
 * - OE: Exception Out node.
 *
 *     input
 *      edge
 *       |
 *       I
 *      / \
 *     O  OE
 *     |   |
 * normal  exception
 * out     out
 * edge    edge
 */
sealed abstract class Kind
case object KindI extends Kind
case object KindO extends Kind
case object KindOE extends Kind

class EGraph(nodes: Set[ENode], override var entry: ENode, graph: TGraph[Node]) extends Graph[ENode]{
  def getNodes: Set[ENode] = nodes

  def getSuccs(enode: ENode): Set[ENode] = {
    val node = enode._1
    enode._2 match {
      case KindI => Set((node, KindO), (node, KindOE))
      case KindO => graph.getNormalSuccs(node).map(n => (n, KindI))
      case KindOE => graph.getExcSuccs(node).map(n => (n, KindI))
    }
  }

  def getPreds(enode: ENode): Set[ENode] = {
    val node = enode._1
    enode._2 match {
      case KindI => graph.getNormalPreds(node).map(n => (n, KindO)) ++ graph.getExcPreds(node).map(n => (n, KindOE))
      case KindO => Set((node, KindI))
      case KindOE => Set((node, KindI))
    }
  }
}

object EGraph {
  def makeGraph(graph: TGraph[Node]): EGraph = {
    val nodes =
      graph.getNodes.foldLeft(Set[(Node,Kind)]())((N, n) => {
        N + ((n, KindI)) + ((n, KindO)) + ((n, KindOE))
      })

    new EGraph(nodes, (graph.entry, KindO), graph)
  }
}
