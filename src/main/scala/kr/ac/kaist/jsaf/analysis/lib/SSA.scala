/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.lib

import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import scala.collection.mutable.Stack
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.cfg.Node
import kr.ac.kaist.jsaf.analysis.lib.graph.{DataDependencyGraph => DDGraph}
import kr.ac.kaist.jsaf.analysis.lib.graph.DomTree
import kr.ac.kaist.jsaf.analysis.lib.graph.EGraph
import kr.ac.kaist.jsaf.analysis.lib.graph.FlowGraph
import kr.ac.kaist.jsaf.analysis.lib.graph.Graph
import kr.ac.kaist.jsaf.analysis.lib.graph.KindI
import kr.ac.kaist.jsaf.analysis.lib.graph.KindO
import kr.ac.kaist.jsaf.analysis.lib.graph.KindOE
import kr.ac.kaist.jsaf.analysis.typing.domain.DomainPrinter
import kr.ac.kaist.jsaf.analysis.typing.domain.LocSet
import kr.ac.kaist.jsaf.analysis.typing.domain.LocSetBot
import kr.ac.kaist.jsaf.analysis.typing.domain.Loc
import kr.ac.kaist.jsaf.analysis.cfg.LEntry
import kr.ac.kaist.jsaf.analysis.lib.graph.ENode

/**
 * A set of algorithms about Static Single Assignment Form.
 */
object SSA {
  def recover_defuse(fg: FlowGraph, ddg: DDGraph, du: Map[Node, (LocSet,LocSet)], cfg: CFG, dst_node: Node, l: Loc): Set[Node] = {
    var recovered_nodes = Set[Node]()
//     val dt = buildDomTree(fg)
//     val preds = cfg.getPred(dst_node)
// //    System.out.println("  preds of dst_node : " + preds)
//     val def_node = preds.foldLeft(Set[Node]())((nodeSet, node) => nodeSet + reaching_def(dt, du, node, l))
//     def_node.foreach(src_node => ddg.addEdge(src_node, dst_node, l))

//     val preds_e = cfg.getExcPred(dst_node)
//     val def_node_e = preds_e.foldLeft(Set[Node]())((nodeSet, node) => nodeSet + reaching_def(dt, du, node, l))
// //    def_node_e.foreach(src_node => ddg.addExcEdge(src_node, dst_node, l))
//     def_node_e.foreach(src_node => ddg.addExcEdge(src_node, dst_node, l))

//     def_node ++ def_node_e
    recovered_nodes
  }
  /**
   * Returns a node which is the define node for the use location of the input node
   */
  def reaching_def(dt: DomTree[Node], du: Map[Node, (LocSet,LocSet)], n: Node, l: Loc): Node = {
    if( du(n)._2.contains(l) ) n
    else reaching_def(dt, du, dt.getParent(n), l)
  }

  def draw_defuse(g: EGraph,
                   dt: DomTree[ENode],
                   du: Map[Node, (LocSet, LocSet)],
                   joinpoints: Map[ENode,LocSet],
                   variables: LocSet
                ): DDGraph = {
    val ddg = new DDGraph(g.getNodes.map(n => n._1), g.entry._1)
    def add_du_edge_i(src: ENode, dst: ENode, l: Loc) = {
      (src._2, dst._2) match {
        case (KindO, KindI) => ddg.addEdge(src._1, dst._1, l)
        case (KindOE, KindI) => ddg.addExcEdge(src._1, dst._1, l)
        case _ => {
          // System.out.println(src + " => " + dst + "("+DomainPrinter.printLoc(l)+")")
          throw new InternalError("Impossible case.")
        }
      }
    }

    val du_ =
      du.foldLeft(Map[ENode, (LocSet, LocSet)]())((S, e) => {
        S +
        ((e._1,KindI) -> (LocSetBot, e._2._2)) +
        ((e._1,KindO) -> (e._2._1, LocSetBot)) +
        ((e._1,KindOE) -> (e._2._1, LocSetBot))
      })

    draw_defuse_i(g, dt, du_, joinpoints, variables, add_du_edge_i)
    ddg
  }

  def draw_defuse_(g: EGraph,
                    ddg: DDGraph,
                    du: Map[Node, (LocSet, LocSet)],
                    joinpoints: Map[ENode,LocSet],
                    variables: LocSet
                  ): Set[Node] = {
    var recovered_nodes = Set[Node]()
    def add_du_edge_i(src: ENode, dst: ENode, l: Loc) = {
      (src._2, dst._2) match {
        case (KindO, KindI) => {
          if (ddg.addEdge(src._1, dst._1, l))
            recovered_nodes += src._1
        }
        case (KindOE, KindI) => {
          if (ddg.addExcEdge(src._1, dst._1, l))
            recovered_nodes += src._1
        }
        case _ => {
          // System.out.println(src + " => " + dst + "("+DomainPrinter.printLoc(l)+")")
          throw new InternalError("Impossible case.")
        }
      }
    }

    val du_ =
      du.foldLeft(Map[ENode, (LocSet, LocSet)]())((S, e) => {
        S +
        ((e._1,KindI) -> (LocSetBot, e._2._2)) +
        ((e._1,KindO) -> (e._2._1, LocSetBot)) +
        ((e._1,KindOE) -> (e._2._1, LocSetBot))
      })

    val dt = buildDomTree(g)

    draw_defuse_i(g, dt, du_, joinpoints, variables, add_du_edge_i)

    recovered_nodes
  }

  def draw_defuse_i(fg: Graph[ENode],
                     dt: DomTree[ENode],
                     du: Map[ENode, (LocSet, LocSet)],
                     joinpoints: Map[ENode,LocSet],
                     variables: LocSet,
                     add_du_edge_i: (ENode, ENode, Loc) => Unit
                   ): Unit = {
    var s = Map[Loc, Stack[ENode]]()
    val phis = getLocSet(joinpoints, _: ENode)

    def rhsof(n: ENode) = {
      val set = du.get(n) match {
        case Some(s) => s._2
        case None => LocSetBot
      }
      set -- phis(n)
    }
    def lhsof(n: ENode) = {
      n._2 match {
        case KindO | KindOE => {
          val set = du.get(n) match {
            case Some(s) => s._1
            case None => LocSetBot
          }
          set ++ phis((n._1, KindI))
        }
        case KindI => LocSetBot
      }
    }

    // initialize
    variables.foreach(v => {
      // assumes that every variable is on entry.
      s += (v -> (new Stack[ENode]).push(fg.entry))
    })

    def search(x: ENode): Unit = {
      // draw edges from defs to uses.
      rhsof(x).foreach(v => add_du_edge_i(s(v).top, x, v))
      
      // reset a source of each def to current node.
      lhsof(x).foreach(v => s(v).push(x))

      // draw edges from defs to phi functions.
      fg.getSuccs(x).foreach(y => {
        phis(y).foreach(v => add_du_edge_i(s(v).top, y, v))
      })

      // search for children of x.
      dt.getChildren(x).foreach(search)
      // reset the source of each def to previous node.
      lhsof(x).foreach(v => s(v).pop())
    }

    search(fg.entry)
  }

  def computeDominanceFrontier[Node](g: Graph[Node], dt: DomTree[Node]): Map[Node, Set[Node]] = {
    var df = HashMap[Node, Set[Node]]()

    def rec_computeDF(n: Node): Unit = {
      var s = HashSet[Node]()
      s ++= g.getSuccs(n).filter(y => dt.getParent(y) != n)

      dt.getChildren(n).foreach(c => {
        rec_computeDF(c)
        s ++= df(c).filter(w => dt.getParent(w) != n)
      })

      df += (n -> s)
    }

    rec_computeDF(g.entry)
    df
  }

  def computesJoinpoints(g: EGraph, du: Map[Node, (LocSet, LocSet)], variables: LocSet, dt: DomTree[ENode]): Map[ENode, LocSet] = {
    val domfront = computeDominanceFrontier[ENode](g, dt)
    val nodes = g.reachable
    def getDU(node: ENode): (LocSet, LocSet) = {
      val locsets = du.get(node._1) match {
        case Some(s) => s
        case None => (LocSetBot, LocSetBot)
      }
      node._2 match {
        case KindI => (LocSetBot, locsets._2)
        case KindO | KindOE => (locsets._1, LocSetBot)
      }
    }

    val defsites =
      nodes.foldLeft[Map[Loc, Set[ENode]]](Map())((m, n) => {
        getDU(n)._1.foldLeft(m)((m2, a) => {
          m2.get(a) match {
            case Some(s) => m2 + (a -> (s + n))
            case None => m2 + (a -> Set(n))
          }
        })
      })

    var joinpoints = HashMap[ENode, LocSet]()
    var iterCount = 0
    var hasAlready = HashMap[ENode, Int]()
    var work = HashMap[ENode, Int]()
    nodes.foreach((x) => {
      hasAlready += (x -> 0)
      work += (x -> 0)
    })

    var w = HashSet[ENode]()
    variables.foreach(v => {
      iterCount += 1

      getSet(defsites, v).foreach(x => {
        work += (x -> iterCount)
        w += x
      })
      while (!w.isEmpty) {
        val x = w.head
        w -= x
        domfront(x).foreach(y => {
          if (hasAlready(y) < iterCount) {
            joinpoints += (y -> (getLocSet(joinpoints, y) + v))
            hasAlready += (y -> iterCount)
            if (work(y) < iterCount) {
              work += (y -> iterCount)
              w += y
            }
          }
        })
      }
    })
    joinpoints
  }
/*
  def computesGlobalJoinpoints(g: GEGraph, du: Map[FunctionId, Map[Node, (LocSet, LocSet)]], variables: LocSet, dt: DomTree[GENode]): Map[GENode, LocSet] = {
    val domfront = computeDominanceFrontier[GENode](g, dt)
    val nodes = g.reachable
    def getDU(node: ENode): (LocSet, LocSet) = {
      val locsets = du(node._1._1)(node._1)
      node._2 match {
        case KindI => (LocSetBot, locsets._2)
        case KindO | KindOE => (locsets._1, LocSetBot)
      }
    }
    def toENode(node:GENode):ENode = {
      (node._1._1, node._2)
    }

    val defsites =
      nodes.foldLeft[Map[Loc, Set[GENode]]](Map())((m, n) => {
        getDU(toENode(n))._1.foldLeft(m)((m2, a) => {
          m2.get(a) match {
            case Some(s) => m2 + (a -> (s + n))
            case None => m2 + (a -> Set(n))
          }
        })
      })

    var joinpoints = HashMap[GENode, LocSet]()
    var iterCount = 0
    var hasAlready = HashMap[GENode, Int]()
    var work = HashMap[GENode, Int]()
    nodes.foreach((x) => {
      hasAlready += (x -> 0)
      work += (x -> 0)
    })

    var w = HashSet[GENode]()
    variables.foreach(v => {
      iterCount += 1

      getSet(defsites, v).foreach(x => {
        work += (x -> iterCount)
        w += x
      })
      while (!w.isEmpty) {
        val x = w.head
        w -= x
        domfront(x).foreach(y => {
          if (hasAlready(y) < iterCount) {
            joinpoints += (y -> (getLocSet(joinpoints, y) + v))
            hasAlready += (y -> iterCount)
            if (work(y) < iterCount) {
              work += (y -> iterCount)
              w += y
            }
          }
        })
      }
    })
    joinpoints
  }
*/
  /* Cooper, Harvey and Kennedy(2001). A Simple, Fast Dominance Algorithm. */
  def buildDomTree[T: Manifest](g: Graph[T]): DomTree[T] = {
    val node2id: Map[T,Int] = g.getPostorder
    val id2node: Array[T] = new Array(node2id.size)
    val max_id = node2id.size - 1
    val nodes = node2id.keySet
    node2id.foreach(n => id2node(max_id - n._2) = n._1)

    // for all nodes, b. doms[b] <- Undefined
    val doms = new Array[Option[Int]](node2id.size)
    (0 to max_id).foreach((i) => doms(i) = None)
    doms(0) = Some(0)

    def getSome(v: Option[Int]): Int = {
      v match {
        case Some(i) => i
        case None => throw new InternalError("")
      }
    }

    def intersect(b1: Int, b2: Int): Int = {
      var finger1 = b1
      var finger2 = b2

      // the condition is reversed because we use reverse postorder numbers.
      while (finger1 != finger2) {
        while (finger1 > finger2)
          finger1 = getSome(doms(finger1))
        while (finger2 > finger1)
          finger2 = getSome(doms(finger2))
      }
      finger1
    }

    var changed = true
    while (changed) {
      changed = false
      // for all nodes, b, in reverse postorder (except start_node)
      (1 to max_id).foreach((b) => {
        val preds = g.getPreds(id2node(b)) & nodes
        val new_idom = preds.foldLeft[Option[Int]](None)((new_idom, p) => {
          val p_id = max_id - node2id(p)
          doms(p_id) match {
            case Some(x) =>
              new_idom match {
                case Some(nidom) => Some(intersect(p_id, nidom))
                case None => Some(p_id)
              }
            case None => new_idom
          }
        })
        if (new_idom != doms(b)) {
          doms(b) = new_idom
          changed = true
        }
      })
    }

    def getIDom(v: Option[Int]): Int = {
      v match {
        case Some(i) => i
        case None => throw new InternalError("immediate dominator for "+v+"does not exist.")
      }
    }

    var idom: HashMap[T, T] = HashMap()
    var dom_tree: HashMap[T, Set[T]] = HashMap()

    (0 to doms.length-1).foreach((i) => {
      val child = id2node(i)
      val parent = id2node(getIDom(doms(i)))
      if (child != parent) {
        idom += (child -> parent)
        dom_tree.get(parent) match {
          case Some(s) => dom_tree += (parent -> (s + (child)))
          case None => dom_tree += (parent -> HashSet(child))
        }
      }
    })

    new DomTree[T](idom, dom_tree)
  }
}
