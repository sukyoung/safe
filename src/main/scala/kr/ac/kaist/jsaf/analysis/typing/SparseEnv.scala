/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.HashSet
import scala.collection.immutable.HashMap
import domain._
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.lib.graph.{DataDependencyGraph => DDGraph, _}
import kr.ac.kaist.jsaf.analysis.lib._
import kr.ac.kaist.jsaf.analysis.lib.graph.KindI

class SparseEnv(cfg: CFG) extends Environment(cfg) {
  private var bypasses: HashMap[ControlPoint, LocSet] = HashMap()
  private var bypassesUpdated: HashSet[(ControlPoint, FunctionId)] = HashSet()
  private var bypassesExc: HashMap[ControlPoint, LocSet] = HashMap()
  private var bypassesExcUpdated: HashSet[(ControlPoint, FunctionId)] = HashSet()
  protected var bypassingMap: HashMap[Node, LocSet] = HashMap()

  // flow and data dependency graph for each (function, context)
  protected var flowGraphMap: HashMap[(FunctionId, CallContext), (FlowGraph, DDGraph)] = HashMap()

  protected var intraCFGMap: HashMap[FunctionId, TGraph[Node]] = HashMap()
  protected var usesetForFuncMap: Map[FunctionId, LocSet] = HashMap()
  protected var localization: Boolean = false

  // def set of the function
  protected var afdset: HashMap[FunctionId, LocSet] = HashMap()
  protected var afuset: HashMap[FunctionId, LocSet] = HashMap()
  // inter-procedural Data Dependency Graph for computing worklist order.
  protected var interDDG: DGraph[Node] = null

  def getInterDDG = interDDG

  def getFlowGraph(fid: FunctionId, cc: CallContext) = {
    flowGraphMap.get((fid, cc)) match {
      case Some(pair) => pair
      case None => {
        val nodes = getCFG.getReachableNodes(fid).toSet // in other words, these nodes are belong to the function. (node.fid == fid)
        val g = FlowGraph.makeGraph(nodes, (fid, LEntry)) // create a flow graph (new cfg)
        val ddg = new DDGraph(nodes, (fid, LEntry)) // create a data dependency graph
        flowGraphMap += ((fid,cc) -> (g, ddg))
        // System.out.println("==== make new Call context : fid : " + fid + ", C-C : " + cc)

        (g, ddg)
      }
    }
  }

  override def drawDDG(cg: Map[CFGInst, Set[FunctionId]], du: DUSet, quiet: Boolean): Unit = {
    if (!quiet)
      System.out.println("* Computes defuse graph")
    val reachableFuncs = cg.foldLeft(Set(getCFG.getGlobalFId))((s, kv) => s ++ kv._2)

    /*
     * Computes callgraph
     *   from: Map[CFGInst, Set[FunctionId]]
     *     to: Map[Node, Set[FunctionId]]
     */
    val callgraph_node: Map[Node, Set[FunctionId]] =
      cg.foldLeft[Map[Node, Set[FunctionId]]](Map())((m, kv) => {
        val caller = getCFG.findEnclosingNode(kv._1)
        m.get(caller) match {
          case Some(callees) => m + (caller -> (kv._2 ++ callees))
          case None => m + (caller -> kv._2)
        }
      })
    /*
     * Computes callgraph
     *   from: Map[Node, Set[FunctionId]]
     *     to: Map[FunctionId, Set[FunctionId]]
     */
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

      val cfg = TGraph.makeGraph[Node](nodes, (fid, LEntry), succs, succs_e)
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
      val ddg_edgeonly = ddg.toEdgeOnlyGraph

      if (interDDG == null) interDDG = ddg_edgeonly
      else interDDG = interDDG + ddg_edgeonly

      intraCFGMap += (fid -> cfg)
    })
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
  }

  /**
   * fid : FunctionId to compute intra defuse set
   * du : defuse set of nodes
   * afdu : defuse set of functions
   * callgraph : call edges from Node A(in fid) to callee(FunctionId set)
   */
  def computesIntraDefUseSet(fid: FunctionId, du: DUSet, afdu: Map[FunctionId, (LPSet, LPSet)], callgraph: Map[Node, Set[FunctionId]]) = {
    localization = true

    // collects all callee FunctionIds
    val callees = callgraph.foldLeft(HashSet[FunctionId]())((S, kv) => S ++ kv._2)
    // collects all def sets of callee functions
    callees.foreach(fid => afdset += (fid -> afdu(fid)._1.toLSet))

    val nodes = getCFG.getReachableNodes(fid)
    val intraDU =
      nodes.foldLeft[Map[Node,(LocSet,LocSet)]](Map())((m, n) => {
        n._2 match { // n._2: Label
          case LEntry => {
            // get the use set of this function and callees.
            val func_du = afdu(fid)
            val useset = func_du._2.toLSet
            updateUseset(fid, useset)
            // useset = (useset_for_func ++ useset_of_succs_func)
            // defset = all.
            m + (n -> (LBot, LBot))
          }
          case LExit | LExitExc=> {
            // get the def set of this function and callees
            val func_du = afdu(fid)
            val node_du = du(n)

            // useset = (defset_for_func ++ defset_of_succs_func) ++ useset_for_node + #PureLocal
            // defset = empty
            m + (n -> (LBot, func_du._1.toLSet ++ node_du._2.toLSet + SinglePureLocalLoc))
          }
          case _ => {
            val duset = du(n)
            val defset_1 = duset._1.toLSet
            val useset_1 = duset._2.toLSet

            // call node
            val (defset_2, useset_2) =
              if (getCFG.getCalls.contains(n)) {
                val succs_du =
                  callgraph.get(n) match {
                    case Some(succs) => {
                      succs.foldLeft((LBot, LBot))((S, succ) => {
                        val afdu_succ = afdu(succ)
                        (S._1 ++ afdu_succ._1.toLSet, S._2 ++ afdu_succ._2.toLSet)
                      })
                    }
                    case None => (LBot, LBot)
                  }
                // useset = useset_for_node ++ defset_for_succs_func ++ useset_for_succs_func
                // defset = defset_for_node
                val useset = useset_1 ++ succs_du._1 ++ succs_du._2
                val defset = defset_1
                (defset, useset)
              } else {
                (defset_1, useset_1)
              }

            // after-call node
            val (defset_3, useset_3) =
              if (getCFG.getAftercalls.contains(n)) {
                val call_node = getCFG.getCallFromAftercall(n)
                val succs_du =
                  callgraph.get(call_node) match {
                    case Some(succs) => {
                      succs.foldLeft((LBot, LBot))((S, succ) => {
                        val afdu_succ = afdu(succ)
                        (S._1 ++ afdu_succ._1.toLSet, S._2 ++ afdu_succ._2.toLSet)
                      })
                    }
                    case None => (LBot, LBot)
                  }
                // -- defset_for_succs_func in useset: defined values in succs functions will be come through IP edges.
                // useset = useset_for_node -- defset_for_succs_func - #PureLocal - #ContextLoc
                // defset = defset_for_succs_func ++ defset_for_node + #PureLocal + #ContextLoc
                val call_lset = bypassingMap.get(call_node) match {
                  case Some(lset) => lset
                  case None => LocSetBot
                }
                bypassingMap += (call_node -> (succs_du._1 ++ call_lset)) // sets which will be used by IP edges.
                
                (defset_2 ++ succs_du._1 + SinglePureLocalLoc + ContextLoc, useset_2 -- succs_du._1 - SinglePureLocalLoc - ContextLoc)
              } else {
                (defset_2, useset_2)
              }
            
            // after-catch node
            val (defset_4, useset_4) =
              if (getCFG.getAftercatches.contains(n)) {
                val call_node = getCFG.getCallFromAftercatch(n)
                val succs_du =
                  callgraph.get(call_node) match {
                    case Some(succs) => {
                      succs.foldLeft((LBot, LBot))((S, succ) => {
                        val afdu_succ = afdu(succ)
                        (S._1 ++ afdu_succ._1.toLSet, S._2 ++ afdu_succ._2.toLSet)
                      })
                    }
                    case None => (LBot, LBot)
                  }
                // -- defset_for_succs_func in useset: defined values in succs functions will be come through IP edges.
                // useset = useset_for_node -- defset_for_succs_func - #PureLocal - #ContextLoc
                // defset = defset_for_succs_func ++ defset_for_node + #PureLocal + #ContextLoc
                val call_lset = bypassingMap.get(call_node) match {
                  case Some(lset) => lset
                  case None => LocSetBot
                }
                bypassingMap += (call_node -> (succs_du._1 ++ call_lset)) // sets which will be used by IP edges.
                (defset_3 ++ succs_du._1 + SinglePureLocalLoc + ContextLoc, useset_3 -- succs_du._1 - SinglePureLocalLoc - ContextLoc)
              } else {
                (defset_3, useset_3)
              }
            m + (n -> (defset_4, useset_4))
          }
        }
      })
    // XXX: there are some missing defs which is from callee's exit-exc.
    // fortunately, after-call's defs provide it now.
    // LExitExc should only contain a def set in a function
    val catches = getCFG.getExcPred.keySet.filter(node => node._2 != LExitExc)
    catches.foldLeft(intraDU)((m, cn) => {
      val preds = getSet(getCFG.getExcPred, cn)
      val defs =
        preds.foldLeft(LocSetBot)((S, pred) => {
          m.get(pred) match {
            case Some(s) => S ++ s._1//  ++ s._2
            case None => S
          }
        })
      // preds' defs must be included in use set since they are may-def.
      m.get(cn) match {
        case Some(defuse) => m + (cn -> (defuse._1 ++ defs, defuse._2 ++ defs))
        case None => m
      }
    })
  }

  // use set of the function
  def optionLocalization = localization
  def updateUseset(fid: FunctionId, set: LocSet): Unit = {
    usesetForFuncMap.get(fid) match {
      case Some(s) => usesetForFuncMap += (fid -> (s ++ set))
      case None => usesetForFuncMap += (fid -> set)
    }
  }
  def getLocalizationSet(fid: FunctionId) = {
    usesetForFuncMap.get(fid) match {
      case Some(n) => n
      case None => throw new InternalError("DU set for the following function is an empty: "+cfg.getFuncName(fid)+"("+fid+")")
    }
  }

  def getBypassingSet(cp: ControlPoint) = {
    bypasses.get(cp) match {
      case Some(s) => s
      case None => LocSetBot
    }
  }

  def getBypassingExcSet(cp: ControlPoint) = {
    bypassesExc.get(cp) match {
      case Some(s) => s
      case None => LocSetBot
    }
  }

  def updateBypassing(cp: ControlPoint, callee: FunctionId): Boolean = {
    if (!bypassesUpdated.contains((cp, callee))) {
      bypassesUpdated += ((cp, callee))
      // sound approximation passing set from call to after-call.
      val bypass_set = bypassingMap(cp._1) // candidate loc set to bypass
      // bypassed set by callee
      val bypassed_set = afdset(callee) // def set from the function
      // locset which must be bypassed.
      val need_set = bypass_set -- bypassed_set // candidate loc set - def set

      // insert bypassing locset
      bypasses.get(cp) match {
        case Some(s) => bypasses += (cp -> (s ++ need_set)) // is here a dead code?
        case None => bypasses += (cp -> need_set)
      }
      true
    } else {
      false
    }
  }

  def updateBypassingExc(cp: ControlPoint, callee: FunctionId): Boolean = {
    if (!bypassesExcUpdated.contains((cp, callee))) {
      bypassesExcUpdated += ((cp, callee))
      // sound approximation passing set from call to after-catch.
      val bypass_set = bypassingMap(cp._1)
      // bypassed set by callee
      val bypassed_set = afdset(callee)
      // locset which must be bypassed.
      val need_set = bypass_set -- bypassed_set

      bypassesExc.get(cp) match {
        case Some(s) => bypassesExc += (cp -> (s ++ need_set))
        case None => bypassesExc += (cp -> need_set)
      }
      true
    } else {
      false
    }
  }
  def recoverOutAftercall(fg: FlowGraph, call: Node): HashSet[(Node,Node)] = {
    if (!fg.isRecovered(call)) {
      fg.recovered(call)
      val after_call = cfg.getAftercallFromCall(call)
      val recovered_set =
        if (isEmptyNode(after_call)) {
          recoverOutEdges(fg, after_call)
        }
        else
          HashSet[(Node,Node)]()
      recovered_set + ((call, after_call))
    } else {
      HashSet()
    }
  }
  def recoverOutAftercatch(fg: FlowGraph, call: Node): HashSet[(Node,Node)] = {
    if (!fg.isCallExcRecovered(call)) {
      fg.callExcRecovered(call)
      val after_catch = cfg.getAftercatchFromCall(call)

      val recovered_set =
        if (isEmptyNode(after_catch)) {
          recoverOutEdges(fg, after_catch)
        }
        else
          HashSet[(Node,Node)]()
      recovered_set + ((call, after_catch))
    } else {
      HashSet()
    }
  }
  def recoverOutEdges(fg: FlowGraph, src: Node): HashSet[(Node,Node)] = {
    if (!fg.isRecovered(src)) {
      fg.recovered(src)
      val fid = src._1
      val dsts = intraCFGMap(fid).getNormalSuccs(src)
//      System.out.println("recover out edges from "+src+" to "+dsts)
      dsts.foldLeft[HashSet[(Node,Node)]](HashSet())((S, dst) => {
        val S_2 =
          if (isEmptyNode(dst)) {
//            System.out.println("node "+dst+" is empty.")
            recoverOutEdges(fg, dst)
          }
          else
            HashSet()
        S ++ S_2 + ((src, dst))
      })
    } else {
      HashSet()
    }
  }

  def recoverOutExcEdge(fg: FlowGraph, src: Node): HashSet[(Node,Node)] = {
    if (!fg.isExcRecovered(src)) {
      fg.excRecovered(src)
      val fid = src._1
      val dsts = intraCFGMap(fid).getExcSuccs(src)
      // System.out.println("recover out exc-edges from "+src+" to "+dsts)
      dsts.foldLeft[HashSet[(Node,Node)]](HashSet())((S, dst) => S + ((src, dst)))
    } else {
      HashSet()
    }
  }

  def draw_intra_dugraph_incremental(fg: FlowGraph, ddg: DDGraph, edges: Set[(Node,Node)], excEdges: Set[(Node,Node)]): Set[Node] = {
    val fid = fg.fid
    val du = intraDefuseMap(fid)
    val joinpoints = joinpointsMap(fid)

    val variables = du.foldLeft(LBot)((s, du) => s ++ du._2._1 ++ du._2._2)
    
    // add new edges
    edges.foreach(edge => fg.addEdge(edge._1, edge._2))
    excEdges.foreach(edge => fg.addExcEdge(edge._1, edge._2))

    val g = EGraph.makeGraph(fg)
    SSA.draw_defuse_(g, ddg, du, joinpoints, variables)
  }

  // dump FlowGraph & DDGraph to .dot file
  def dump_dugraph(): Unit = {
    flowGraphMap.foreach(kv => {
      System.out.println("== FG for " + getCFG.getFuncName(kv._1._1) + " at " + kv._1._2 + " ==")
      kv._2._1.toDot_dugraph()
      System.out.println("== DDG for " + getCFG.getFuncName(kv._1._1) + " at " + kv._1._2 + " ==")
      kv._2._2.toDot_dugraph()
    })
  }

  def getDDGStr(ddg0: Boolean): String = {
    var str = "digraph \"DirectedGraph\" {\n"
    var i = 0
    flowGraphMap.foreach(kv => {
      str += "subgraph cluster" + i + " {\n"
      str += "label = \"fid : " + getCFG.getFuncName(kv._1._1) + ", CallContext : " + kv._1._2 + "\";\n"
      i += 1
      str += kv._2._2.toDot_String
      str += "\n}\n"
    })
    str += "\n}"
    str
  }
  def dumpIntraCFGMap(): Unit = {
    implicit def getLabel(node: Node): String = {
      node._2 match {
        case LBlock(id) => "Block"+id
        case LEntry => "Entry" + node._1
        case LExit => "Exit" + node._1
        case LExitExc => "ExitExc" + node._1
      }
    }
    
    var str = "digraph \"DirectedGraph\" {\n"
    var i = 0
    intraCFGMap.foreach(kv => {
      str += "subgraph cluster" + i + " {\n"
      str += ("label = \"fid : " + getCFG.getFuncName(kv._1) + "(" + kv._1 + ")\";\n")
      str += kv._2.toDot_String(getLabel)
      str += "\n}\n"
      i += 1
    })
    str + "\n}"
    System.out.println(str)
  }
  def getFGStr(global: Boolean): String = {
    var str = "digraph \"DirectedGraph\" {\n"
    var i = 0
    flowGraphMap.foreach(kv => {
      str += "subgraph cluster" + i + " {\n"
      str += ("label = \"fid : " + getCFG.getFuncName(kv._1._1) + "(" + kv._1._1 + "), CallContext : " + kv._1._2 + "\";\n")
      i += 1
      str += kv._2._1.toDot_String
      str += "\n}\n"
    })
    str += "\n}"
    str
  }
  def dump_duset(): Unit = {
    System.out.println("== DU Set ==")
    getCFG.getNodes.foreach(n => {
      val du = intraDefuseMap.get(n._1) match {
        case None => HashMap[Node, (LocSet, LocSet)]()
        case Some(du_) => du_
      }
      val joinpoints = joinpointsMap.get(n._1) match {
        case None => HashMap[ENode, LocSet]()
        case Some(j) => j
      }
      // entry point might be a joinpoint
      def phis(n:Node) = getLocSet(joinpoints, (n, KindI))
      def getDUSet(n:Node):(LocSet, LocSet) = {
        du.get(n) match {
          case None => (LocSetBot, LocSetBot)
          case Some(duset) => duset
        }
      }
      if((getDUSet(n)._1 ++ getDUSet(n)._2 ++ phis(n)) != LocSetBot) {
        System.out.println("* Node "+n)
        System.out.println("defset: "+DomainPrinter.printLocSet(getDUSet(n)._1) + "\n\t&& phis: " + DomainPrinter.printLocSet(phis(n)))
        System.out.println("useset: "+DomainPrinter.printLocSet(getDUSet(n)._2) + "\n\t&& phis: " + DomainPrinter.printLocSet(phis(n)))
      }
    })
  }
}
