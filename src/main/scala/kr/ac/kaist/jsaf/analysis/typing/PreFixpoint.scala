/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import scala.collection.mutable.{Map=> MMap, HashMap => MHashMap, Set => MSet}
import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet

class PreFixpoint(cfg: CFG, worklist: Worklist, state: State, isBugDetector: Boolean) {
  private val sem = new Semantics(cfg, worklist, false)

  // Flow-Insensitive
  def getSemantics = sem
  var count = 0
  var result = StateBot

  def getState(): State = {
    result
  }

  def compute(): Unit = {
    Config.setPreAnalysisMode(true)
    loop()
    Config.setPreAnalysisMode(false)
    if(!isBugDetector) System.out.println()
  }

  private def loop(): Unit = {
    var inState = state
    var outState = state
    var first = true

    // initialize reachable function and calling contexts (global)
    var reachableFIds: Map[FunctionId, Set[CallContext]] = 
      HashMap((cfg.getGlobalFId, HashSet(CallContext.globalCallContext)))
    
    // initialize reachable nodes
    var reachableNodes = cfg.getReachableNodes(cfg.getGlobalFId)

    // fixpoint loop
    while( !(outState <= inState) || first) {
      // loop initialization
      inState = outState
      first = false
      count = count+1

      // iterate over each node
      reachableNodes.foreach(node => {
        if (!isBugDetector)
          System.out.print("\r  PreAnalysis Iteration: "+count+" for " + node + "        ")
        val fid = node._1
        
        // iterate over each calling context
        // note that pre-analysis is context-sensitive for PureLocal objects of built-in calls
        reachableFIds(fid).foreach(cc => {
          val cp = (node, cc)
          outState = sem.PreC(cp, cfg.getCmd(cp._1), outState);
          
          sem.getIPSucc(cp) match {
            case None => ()
            case Some(succMap) =>
              succMap.foreach(kv => {
                // bypassing if IP edge is exception flow.
                val cp_succ = kv._1
/*
                  cp._1._2 match {
                    case LExitExc => {
                      val n_aftercall = kv._1._1
                      cfg.getExcSucc.get(n_aftercall) match {
                        case None => throw new InternalError("After-call node must have exception successor")
                        case Some(node) => (node, kv._1._2)
                      }
                    }
                    case _ => kv._1
                  }
*/
                val succ_fid = cp_succ._1._1
                // if there is a new callee function, update reachable node set.
                reachableFIds.get(succ_fid) match {
                  case Some(cc_set) =>
                    reachableFIds = reachableFIds.updated(succ_fid, cc_set + cp_succ._2)
                  case None =>
                    reachableFIds = reachableFIds.updated(succ_fid, HashSet(cp_succ._2))
                    reachableNodes ++= cfg.getReachableNodes(succ_fid)
                }
                
                // merge context (kv._2._1 is a context for function call)
                outState = State(outState._1, outState._2 + kv._2._1)
                outState = sem.PreE(cp, kv._1, kv._2._1, kv._2._2, outState)
              })
          }
        })
      })
      // System.out.println("== Flow-insensitive analysis result for #"+count+"==")
      // System.out.println(DomainPrinter.printHeap(4,outState._1))
      // System.out.println(DomainPrinter.printContext(4,outState._2))
    }
    
    // merge context-sensitive PureLocal objects for each function.
    // Access analysis requires context-insensitive result.
    outState = cfg.mergePureLocal(outState)
    
    // make analysis results to contain both Recent and Old locations (contents are same)
    result = State(outState._1.oldify, outState._2.oldify)
  }
}
