/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import domain._
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.lib._
import kr.ac.kaist.jsaf.analysis.lib.graph.{DataDependencyGraph => DDGraph, _}
import scala.collection.immutable.HashSet

class DSparseEnv(cfg: CFG) extends SparseEnv(cfg) {
  private var interDDG_ : DDGraph = null
  override def drawDDG(cg: Map[CFGInst, Set[FunctionId]], du: DUSet, quiet: Boolean): Unit = {
    if (!quiet)
      System.out.println("* Computes defuse graph")
    val reachableFuncs = cg.foldLeft(Set(getCFG.getGlobalFId))((s, kv) => s ++ kv._2)

    // computes callgraph
    val callgraph_node: Map[Node, Set[FunctionId]] =
      cg.foldLeft[Map[Node, Set[FunctionId]]](Map())((m, kv) => {
        val caller = getCFG.findEnclosingNode(kv._1)
        m.get(caller) match {
          case Some(callees) => m + (caller -> (kv._2 ++ callees))
          case None => m + (caller -> kv._2)
        }
      })
    val callgraph: Map[FunctionId, Set[FunctionId]] =
      callgraph_node.foldLeft[Map[FunctionId, Set[FunctionId]]](Map())((m, kv) => {
        val caller = kv._1._1
        m.get(caller) match {
          case Some(callees) => m + (caller -> (kv._2 ++ callees))
          case None => m + (caller -> kv._2)
        }
      })

    // computes defuse set for each function.
    val fdu =
      reachableFuncs.foldLeft[Map[FunctionId, (LPSet, LPSet)]](Map())((m, fid) => {
        val nodes = getCFG.getReachableNodes(fid)
        val duset = nodes.foldLeft((LPBot,LPBot))((S, n) => {
          (S._1 ++ du(n)._1, S._2 ++ du(n)._2)
        })
        m + (fid -> duset)
      })

    // fixpoint computation for defuse set considering call relation.
    val afdu = interFuncDefuse(callgraph, fdu)
/*
        System.out.println("* afDUMap")
        afdu.foreach(kv => {
          System.out.println(" == Function ID : " + kv._1)
          System.out.println("      defset : + " + DomainPrinter.printLocSet(kv._2._1.toLSet))
          System.out.println("      useset : + " + DomainPrinter.printLocSet(kv._2._2.toLSet))
        })
*/
        // computes intra-procedural defuse graph
    // for each reachable functions,
    reachableFuncs.foreach((fid) => {
      //System.out.println("* For each reachable function "+fid+",")
      val nodes = getCFG.getReachableNodes(fid).toSet

      // computes intra-procedural defuse set considering call relation.
      val intra_defuse = computesIntraDefUseSet(fid, du, afdu, callgraph_node)
      intraDefuseMap += (fid -> intra_defuse)

      // constructs new control-flow graph including call/after-call, exception edges.
      def succs(node: Node): Set[Node] = {
        val n_1 = getCFG.getAftercallFromCallMap.get(node) match {
          case Some(n) => HashSet(n)
          case None => HashSet()
        }
        val n_2 = getCFG.getAftercatchFromCallMap.get(node) match {
          case Some(n) => HashSet(n)
          case None => HashSet()
        }
        getCFG.getSucc(node) ++ n_1 ++ n_2
      }
      def succs_e(node: Node): Set[Node] = {
        getCFG.getExcSucc.get(node) match {
          case Some(n) => HashSet[Node](n)
          case None => HashSet[Node]()
        }
      }

      val cfg = TGraph.makeGraph(nodes, (fid, LEntry), succs, succs_e)
      val ecfg = EGraph.makeGraph(cfg)

      // variable set
      val variables = intra_defuse.foldLeft(LBot)((s, du) => s ++ du._2._1 ++ du._2._2)
      //System.out.println("* Draw intra def/use graph for "+fid)
      //System.out.println("  The number of du entries: "+variables.size)

      // computes dominance frontier
      val dt = SSA.buildDomTree[ENode](ecfg)

      // computes joinpoints for entry of this function.
      val joinpoints = SSA.computesJoinpoints(ecfg, intra_defuse, variables, dt)
      joinpointsMap += (fid -> joinpoints)

      val ddg = SSA.draw_defuse(ecfg, dt, intra_defuse, joinpoints, variables)

      if (interDDG_ == null) interDDG_ = ddg
      else interDDG_ = interDDG_ + ddg

      intraCFGMap += (fid -> cfg)
    })
    interDDG = interDDG_.toEdgeOnlyGraph
    // interDDG_.toDot_dugraph()
    // dump_duset
    
    callgraph_node.foreach(kv => {
      val call = kv._1
      val aftercall = getCFG.getAftercallFromCall(call)
      val aftercatch = getCFG.getAftercatchFromCall(call)
      kv._2.foreach(callee => {
        val entry = (callee, LEntry)
        val exit = (callee, LExit)
        val exitexc = (callee, LExitExc)

        interDDG.addEdge(call, entry)
        interDDG.addEdge(exit, aftercall)
        interDDG.addEdge(exitexc, aftercatch)
      })
    })
    interDDG.entry = (0, LEntry)
/*
    System.out.println(" ***** dump duset")
    reachableFuncs.foreach(fid =>{
      cfg.getReachableNodes(fid).foreach(node => {
        System.out.println(" = " + node)
        val (defset, useset) = du.get(node) match {
          case Some(defuse) => (defuse._1.toLSet, defuse._2.toLSet)
          case None => (LocSetBot, LocSetBot)
        }
        joinpointsMap(fid).get((node, KindI)) match {
          case Some(lset) => System.out.println("   phiset : " + DomainPrinter.printLocSet(lset))
          case None => System.out.println("   phiset : ")
        }
        System.out.println("   defset : " + DomainPrinter.printLocSet(defset))
        System.out.println("   useset : " + DomainPrinter.printLocSet(useset))
      })
      
    })
*/
  }

  def recover_intra_dugraph(fg: FlowGraph, ddg: DDGraph, edges: Set[(Node,Node)], excEdges: Set[(Node,Node)]): Set[Node] = {
    val fid = fg.fid
    var recovered_nodes = Set[Node]()

    // add new edges
    edges.foreach(edge => fg.addEdge(edge._1, edge._2))
    excEdges.foreach(edge => fg.addExcEdge(edge._1, edge._2))
    val g = EGraph.makeGraph(fg)

    // find target nodes to be recovered
    val cs_normal = edges.foldLeft(Set[Node]())((cs, edge) => cs + edge._2)
    val cs_exc = excEdges.foldLeft(Set[Node]())((cs, edge) => cs + edge._2)

    val du = intraDefuseMap(fid)
    val joinpoints = joinpointsMap(fid)
    val dt = SSA.buildDomTree[ENode](g)
    val du_ =
      du.foldLeft(Map[ENode, (LocSet, LocSet)]())((S, e) => {
        S +
          ((e._1,KindI) -> (LocSetBot, e._2._2)) +
          ((e._1,KindO) -> (e._2._1, LocSetBot)) +
          ((e._1,KindOE) -> (e._2._1, LocSetBot))
      })
    val phis = getLocSet(joinpoints, _: ENode)
    def lhsof(n: ENode) = {
      n._2 match {
        case KindO | KindOE => du_(n)._1 ++ phis((n._1, KindI))
        case KindI => LocSetBot
      }
    }
    // add DDG edges for each of locs from esrc to dst.
    def connect(esrc: ENode, dst: Node, locs: LocSet): Unit = {
      if (!locs.isEmpty) {
        esrc match {
          case (src,KindO) => {
//            System.out.println("connect: "+esrc+" -> "+dst+" with "+DomainPrinter.printLocSet(locs))
            if (ddg.addEdges(src, dst, locs))
              recovered_nodes += src
          }
          case (src,KindOE) => {
//            System.out.println("connect exc: "+esrc+" -> "+dst+" with "+DomainPrinter.printLocSet(locs))
            if (ddg.addExcEdges(src, dst, locs))
              recovered_nodes += src
          }
          case _ => throw new InternalError("Impossible case")
        }
      }
    }

    // recover reaching defs for each of locs.
    def reaching_def(dst: Node, src: ENode, locs: LocSet): Unit = {
      if (!locs.isEmpty) {
        val defs = lhsof(src)

        val defined_here =
          if (dt.hasParent(src)) {
            defs.intersect(locs)
          } else {
            // assumes that every variable is defined at entry node.
            locs
          }

        connect(src, dst, defined_here)

        val rest = (locs -- defined_here)
        if (!rest.isEmpty) {
          reaching_def(dst, dt.getParent(src), rest)
        }
      }
    }

    // for each nodes to be recovered,
    (cs_exc ++ cs_normal).foreach(dst => {
      val dest = (dst, KindI)
      val locs = du(dst)._2 ++ phis(dest)

      g.getPreds(dest).foreach(pred => {
        reaching_def(dst, pred, locs)
      })
    })

    recovered_nodes
  }

  override def getDDGStr(ddg0: Boolean): String = {
    var str = "digraph \"DirectedGraph\" {\n"
    if(ddg0) {
      str += interDDG_.toDot_String
    }
    else {
      var i = 0
      flowGraphMap.foreach(kv => {
        str += "subgraph cluster" + i + " {\n"
        str += "label = \"fid : " + getCFG.getFuncName(kv._1._1) + ", CallContext : " + kv._1._2 + "\";\n"
        i += 1
        str += kv._2._2.toDot_String
        str += "\n}\n"
      })
    }
    str += "\n}"
    str
  }

  // removable
  def toDot_DDG(): Unit = {
    System.out.println("==Original Candidates==")
    interDDG_.dump_candidates()
    System.out.println("==Original==")
    interDDG_.toDot_dugraph()
    System.out.println("==After==")
    dump_dugraph()
    System.out.println("==JoinpointsMap==")
    joinpointsMap.foreach(fid_map => {
      val (_, map) = fid_map
      map.foreach(nl => {
        val (node, locSet) = nl
        System.out.println(" joinpoint : " + node + " locset : " + locSet)
      })
    })
  }
}
