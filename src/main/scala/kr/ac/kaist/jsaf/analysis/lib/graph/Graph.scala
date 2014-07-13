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
import scala.math.min

abstract class Graph[Node: ClassManifest] {
  var entry: Node

  def getNodes: Set[Node]
  def getSuccs(node: Node): Set[Node]
  def getPreds(node: Node): Set[Node]

  def getPostorder: Map[Node,Int] = {
    var map = HashMap[Node,Int]()
    var visited = HashSet[Node]()
    var i = 0

    def dfs(n: Node): Unit = {
      visited += n
      val children = getSuccs(n)

      children.foreach(c => {
        if (!visited.contains(c))
          dfs(c)
      })
      map += (n -> i)
      i += 1
    }

    dfs(entry)
    map
  }

  def topologicalOrder: Array[Node] = {
    val fid2id: Map[Node,Int] = getPostorder
    val size = fid2id.size
    val max_id = size - 1
    var id2fid: Array[Node] = new Array(size)
    fid2id.foreach(n => id2fid(max_id - n._2) = n._1)
    id2fid
  }

  def sccs: HashSet[HashSet[Node]] = {
    var scc = HashSet[HashSet[Node]]()
    var index = 0;
    var stack = Stack[Node]()
    var indexMap = HashMap[Node, Int]()
    var lowlinkMap = HashMap[Node, Int]()

    def tarjan(v: Node): Unit = {
      indexMap += (v -> index)
      lowlinkMap += (v -> index)
      index += 1
      stack.push(v)
      val edges = getSuccs(v)
      edges.foreach(n => {
        indexMap.get(n) match {
          case None => {
            tarjan(n)
            val vlowlink = lowlinkMap(v)
            val nlowlink = lowlinkMap(n)
            val lowlink = min(vlowlink, nlowlink)
            lowlinkMap += (v -> lowlink)
          }
          case Some(s) if (stack.contains(n)) => {
            val vlowlink = lowlinkMap(v)
            val nindex = indexMap(n)
            val lowlink = min(vlowlink, nindex)
            lowlinkMap += (v -> lowlink)
          }
          case _ => ()
        }
      })
      val vlowlink = lowlinkMap(v)
      val vindex = indexMap(v)
      if (vlowlink == vindex) {
        var n = stack.top
        var set = HashSet[Node]()
        do {
          n = stack.pop()
          set += n
        } while(n != v)
        scc += set
      }
    }

    getNodes.foreach(n => if (!indexMap.contains(n)) tarjan(n))

    scc
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

  def dump() = {
    System.out.println("entry: "+entry)
    getNodes.foreach(src => {
      getSuccs(src).foreach(dst => System.out.println(src+ " => "+dst))
    })
  }

  def toDot_dugraph() = {
    System.out.println("digraph \"DirectedGraph\" {")
    getNodes.foreach(src => {
      getSuccs(src).foreach(dst => System.out.println(src+ "->"+dst)+";")
    })
    System.out.println("}")
  }
}
