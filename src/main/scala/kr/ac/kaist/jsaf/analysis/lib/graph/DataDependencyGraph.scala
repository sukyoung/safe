/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.lib.graph

import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain.{Loc, LocSet, LocSetBot, DomainPrinter}
import kr.ac.kaist.jsaf.analysis.lib._

class DataDependencyGraph(nodes: Set[Node], var entry: Node) {
  private var succs: Map[Node, Set[Node]] = HashMap()
  private var excSucc: Map[Node, Node] = HashMap()
  private var preds: Map[Node, Set[Node]] = HashMap()
  private var excPred: Map[Node, Set[Node]] = HashMap()
  private var dusetMap: Map[(Node,Node), LocSet] = HashMap()
  private var excdusetMap: Map[(Node,Node), LocSet] = HashMap()

  def getNormalSuccs(node: Node) = getSet(succs, node)
  def getExcSucc(node: Node) = excSucc.get(node)
  def getSuccs(node: Node) = {
    excSucc.get(node) match {
      case Some(n) => getNormalSuccs(node) + n
      case None => getNormalSuccs(node)
    }
  }
  def getNormalPreds(node: Node) = getSet(preds, node)
  def getExcPred(node: Node) = getSet(excPred, node)
  def getPreds(node: Node) = {
    getSet(preds, node) ++ getSet(excPred, node)
  }

  def toEdgeOnlyGraph : DGraph[Node] = {
    val ssa = new DGraph[Node](nodes, entry)
    succs.foreach(kv => {
      val src = kv._1
      kv._2.foreach(dst => ssa.addEdge(src, dst))
    })
    excSucc.foreach(kv => {
      val src = kv._1
      val dst = kv._2
      ssa.addEdge(src, dst)
    })
    ssa
  }

  def +(g: DataDependencyGraph): DataDependencyGraph = {
    val new_succs = g.succs.foldLeft(this.succs)((succs, kv) => {
      succs.get(kv._1) match {
        case Some(v) => succs + (kv._1 -> (v ++ kv._2))
        case None => succs + (kv._1 -> kv._2)
      }
    })
    val new_excSucc = g.excSucc.foldLeft(this.excSucc)((succs, kv) => {
      succs + (kv._1 -> kv._2)
    })
    val new_dusetMap = g.dusetMap.foldLeft(this.dusetMap)((map, kv) => {
      map + (kv._1 -> kv._2)
    })
    val new_preds = g.preds.foldLeft(this.preds)((preds, kv) => {
      preds.get(kv._1) match {
        case Some(v) => preds + (kv._1 -> (v ++ kv._2))
        case None => preds + (kv._1 -> kv._2)
      }
    })
    val new_excPred = g.excPred.foldLeft(this.excPred)((excPred, kv) => {
      excPred.get(kv._1) match {
        case Some(v) => excPred + (kv._1 -> (v ++ kv._2))
        case None => excPred + (kv._1 -> kv._2)
      }
    })
    val new_excdusetMap = g.dusetMap.foldLeft(this.excdusetMap)((map, kv) => {
      map + (kv._1 -> kv._2)
    })

    val newgraph = new DataDependencyGraph(this.nodes ++ g.getNodes, this.entry)
    newgraph.succs = new_succs
    newgraph.excSucc = new_excSucc
    newgraph.preds = new_preds
    newgraph.excPred = new_excPred
    newgraph.dusetMap = new_dusetMap
    newgraph.excdusetMap = new_excdusetMap
    newgraph
  }

  def getDUSet(src: Node, dest: Node): LocSet =
    dusetMap.get(src,dest) match {
      case Some(s) => s
      case None => LocSetBot
    }

  def getExcDUSet(src: Node, dest: Node): LocSet =
    excdusetMap.get(src,dest) match {
      case Some(s) => s
      case None => LocSetBot
    }

  def dump_candidates(): Unit = {
    System.out.println("Preds===")
    preds.foreach(nl => System.out.println(nl))
    System.out.println("Candidates===")
    nodes.foreach(n => System.out.println(getPreds(n)))
    System.out.println("dusetMap===")
    dusetMap.foreach(dul => {
      val (edge, lset) = dul
      System.out.print(edge + ",Set(")
      lset.foreach(loc => System.out.print(DomainPrinter.printLoc(loc) + ","))
      System.out.println(")")
    })
  }

  def getNodes = nodes

  def addEdge(src: Node, dest: Node, l: Loc) = {
    val oldSet = getDUSet(src, dest)
    if (oldSet.contains(l)) {
      false
    } else {
//      System.err.println("Edge: "+src+" -> "+dest+" { "+ DomainPrinter.printLoc(l) +" }")
      succs += (src -> (getNormalSuccs(src) +dest))
      preds += (dest -> (getSet(preds, dest) + src))
      dusetMap += ((src, dest) -> (oldSet + l))
      true
    }
  }

  def addEdges(src: Node, dest: Node, locs: LocSet) = {
    val oldSet = getDUSet(src, dest)
    if (locs.subsetOf(oldSet)) {
      false
    } else {
      succs += (src -> (getNormalSuccs(src) +dest))
      preds += (dest -> (getSet(preds, dest) + src))
      dusetMap += ((src, dest) -> (oldSet ++ locs))
      true
    }
  }

  def addExcEdge(src: Node, dest: Node, l: Loc) = {
    val oldSet = getExcDUSet(src, dest)
    if (oldSet.contains(l)) {
      false
    } else {
//      System.err.println("ExcEdge: "+src+" -> "+dest+" { "+ DomainPrinter.printLoc(l) +" }")
      excSucc += (src -> dest)
      excPred += (dest -> (getSet(excPred, dest) + src))
      excdusetMap += ((src, dest) -> (oldSet + l))
      true
    }
  }

  def addExcEdges(src: Node, dest: Node, locs: LocSet) = {
    val oldSet = getExcDUSet(src, dest)
    if (locs.subsetOf(oldSet)) {
      false
    } else {
      excSucc += (src -> dest)
      excPred += (dest -> (getSet(excPred, dest) + src))
      excdusetMap += ((src, dest) -> (oldSet ++ locs))
      true
    }
  }

  def reachable: Set[Node] = {
    val e = entry
    var visited = HashSet[Node]()

    def dfs(n: Node): Unit = {
      visited += (n)
      getSuccs(n).foreach((c) => {
        if (!visited.contains(c))
          dfs(c)
      })
    }
    dfs(e)

    visited
  }

  def dump(): Unit = {
    System.out.println("entry: "+entry)
    succs.foreach(kv => {
      val src = kv._1
      kv._2.foreach(dst => System.out.println(src+ " => "+dst))
    })
  }

  def getLabel(node: Node): String = {
    node._2 match {
      case LBlock(id) => "Block"+id
      case LEntry => "Entry" + node._1
      case LExit => "Exit" + node._1
      case LExitExc => "ExitExc" + node._1
    }
  }

  private def NormalEdgeStyle:String = "fontname=\"Consolas\" style=solid"

  private def ExcEdgeStyle:String = "fontname=\"Consolas\" style=dashed"

  private def ppLocs(set: LocSet) = {
    "{ " + set.foldLeft("")((S,l) => S + DomainPrinter.printLoc(l) + ", ") + " }"
  }

  def toDot_dugraph(): Unit = {
    System.out.println("digraph \"DirectedGraph\" {")
    getNodes.foreach(src => {
      getNormalSuccs(src).foreach(dst => {
        val duset = getDUSet(src, dst)
        System.out.println(getLabel(src)+ "->"+getLabel(dst)+"["+NormalEdgeStyle+", label=\"" + ppLocs(duset) + "\"];")
      })
    })
    getNodes.foreach(src => {
      getExcSucc(src) match {
        case Some(dst) => {
          val duset = getExcDUSet(src, dst)
          System.out.println(getLabel(src)+ "->"+getLabel(dst)+"["+ExcEdgeStyle+", label=\"" + ppLocs(duset) + "\"];")
        }
        case _ => ()
      }
    })
    System.out.println("}")
  }

  def toDot_String = {
    var str = ""
    getNodes.foreach(src => {
      getNormalSuccs(src).foreach(dst => {
        val duset = getDUSet(src, dst)
        // filtering #PureLocal#n because it is valid only in pre-analysis
        val label = ppLocs(duset).replaceAll("##?PureLocal#[0-9]+,", "").replaceAll(" ", "")
        if(!label.equals("{}"))
          str += (getLabel(src)+ "->"+getLabel(dst)+"["+NormalEdgeStyle+", label=\"" + label + "\"];\n")
      })
    })
    getNodes.foreach(src => {
      getExcSucc(src) match {
        case Some(dst) => {
          val duset = getExcDUSet(src, dst)
          val label = ppLocs(duset).replaceAll("##?PureLocal#[0-9]+,", "").replaceAll(" ", "")
          if(!label.equals("{}"))
            str += (getLabel(src)+ "->"+getLabel(dst)+"["+ExcEdgeStyle+", label=\"" + label + "\"];\n")
        }
        case _ => ()
      }
    })
    str
  }
}
