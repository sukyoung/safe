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
 *
 */
class FlowGraph(entry: Node) extends TGraph[Node](entry) {
  private var recoveredCFG: HashSet[Node] = HashSet()
  private var recoveredEFG: HashSet[Node] = HashSet()
  // add for exception in a function call
  private var recoveredCallExc: HashSet[Node] = HashSet()

  def isRecovered(n: Node) = recoveredCFG.contains(n)
  def isExcRecovered(n: Node) = recoveredEFG.contains(n)
  def isCallExcRecovered(n: Node) = recoveredCallExc.contains(n)
  def recovered(n: Node) = recoveredCFG += n
  def excRecovered(n: Node) = recoveredEFG += n
  def callExcRecovered(n: Node) = recoveredCallExc += n

  def fid = entry._1

  implicit def getLabel(node: Node): String = {
    node._2 match {
      case LBlock(id) => "Block"+id
      case LEntry => "Entry" + node._1
      case LExit => "Exit" + node._1
      case LExitExc => "ExitExc" + node._1
    }
  }

  private def NormalEdgeStyle:String = "fontname=\"Consolas\" style=solid"

  private def ExcEdgeStyle:String = "fontname=\"Consolas\" style=dashed"

  def toDot_dugraph() = {
    System.out.println("digraph \"DirectedGraph\" {")
    succs.foreach{ case (src, dsts) => {
      dsts.foreach(dst => System.out.println(getLabel(src)+ "->"+getLabel(dst)+"["+NormalEdgeStyle+"];"))
    }}
    excSucc.foreach{ case (src, dst) => {
      System.out.println(getLabel(src)+ "->"+getLabel(dst)+"["+ExcEdgeStyle+"];")
    }}
    System.out.println("}")
  }
  def toDot_String(): String = {
    var str = ""
    var sourceNode = HashSet[Node]()
    var sinkNode = HashSet[Node]()
    succs.foreach{ case (src, dsts) => {
      dsts.foreach(dst => {
        str += (getLabel(src)+ "->"+getLabel(dst)+"["+NormalEdgeStyle+"];\n")
        if(src._2 == LEntry) sourceNode+=src
        else if(src._2 == LExit | src._2 == LExitExc) sinkNode+=src
      })
    }}
    excSucc.foreach{ case (src, dst) => {
      str += (getLabel(src)+ "->"+getLabel(dst)+"["+ExcEdgeStyle+"];\n")
    }}

    var ssStr = "{rank=source;"
    sourceNode.foreach(node => ssStr += getLabel(node)+";")
    ssStr += "}\n{rank=sink;"
    sinkNode.foreach(node => ssStr += getLabel(node)+";")
    ssStr += "}\n"

    ssStr + str
  }
}

object FlowGraph {
  def makeGraph(nodes: Set[Node], entry: Node): FlowGraph = {
    val g = new FlowGraph(entry)
    g.addNodes(nodes)
    g
  }
}
