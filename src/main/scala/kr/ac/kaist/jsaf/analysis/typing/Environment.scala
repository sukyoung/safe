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
import graph._
import kr.ac.kaist.jsaf.analysis.cfg.CFGAssert
import kr.ac.kaist.jsaf.analysis.cfg.Block
import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.cfg.CFGAssert
import kr.ac.kaist.jsaf.analysis.cfg.Block

abstract class Environment(cfg: CFG) {
  // defuse set of the function
  protected var joinpointsMap: Map[FunctionId, Map[ENode, LocSet]] = HashMap()
  protected var intraDefuseMap: Map[FunctionId, Map[Node, (LocSet,LocSet)]] = HashMap()
  def getCFG = cfg
  
  def drawDDG(cg: Map[CFGInst, Set[FunctionId]], du: DUSet): Unit = drawDDG(cg, du, false)
  def drawDDG(cg: Map[CFGInst, Set[FunctionId]], du: DUSet, quiet: Boolean): Unit
  def getIntraDefuse(fid: FunctionId): Map[Node, (LocSet,LocSet)] = {
    intraDefuseMap.get(fid) match {
      case Some(m) => m
      case None => Map()
    }
  }

  // Compute the defuse set between the functions
  def interFuncDefuse(callgraph: Map[FunctionId, Set[FunctionId]], fdu: Map[FunctionId, (LPSet, LPSet)]): Map[FunctionId, (LPSet, LPSet)] = {
    // reachableFuncs = globalFid + callgraph's FunctionIds
    val reachableFuncs = callgraph.foldLeft(Set(cfg.getGlobalFId))((s, kv) => s ++ kv._2)
    val numOfFuncs = reachableFuncs.size

    // Number the functions in reverse post-order
    val fid2id: Map[FunctionId,Int] = Utils.get_reverse_postorder(cfg.getGlobalFId, numOfFuncs, ((fid) => callgraph(fid)))
    // Build an array to access(Int -> FunctionId) in O(1)
    // (Reverse numbers again...)
    val id2fid: Array[FunctionId] = new Array(numOfFuncs)
    fid2id.foreach((n) => id2fid(numOfFuncs - n._2 - 1) = n._1)

    /**
     * If there exists a call edge from A to B (A -> B) then do this. (A and B are functions.)
     *   - A's def set += B's def set
     *   - A's use set += B's use set
     * until no change.
     * (To reach the fix-point quickly, traverses the functions in DFS order.)
     */
    def fixpoint(afdu: Map[FunctionId, (LPSet, LPSet)]): Map[FunctionId, (LPSet, LPSet)] = {
      val afdu2 = (0 to numOfFuncs - 1).foldLeft(afdu)((m, id) => {
        val fid = id2fid(id) // caller FunctionId
        val callees = getSet(callgraph, fid) // callee FunctionId set
        val du = callees.foldLeft(m(fid))((du2, succ) => {
            val succ_du = m(succ) // callee's (def, use) set
            (du2._1 ++ succ_du._1, du2._2 ++ succ_du._2) // "caller's (def, use) set" += "callee's (def, use) set"
          })
        m + (fid -> du)
      })
      if (afdu == afdu2)
        afdu
      else fixpoint(afdu2)
    }

    fixpoint(fdu)
  }

  def isEmptyNode(node: Node) = {
    if (getCFG.getAftercalls.contains(node)) false
    else {
      getCFG.getCmd(node) match {
        case Block(i) if (i.length == 1) => i.head match {
          case CFGAssert(_,_,_,_) => false
          case _ => (intraDefuseMap(node._1)(node)._1.isEmpty)
        }
        case Block(i) => (i.length == 0) || (intraDefuseMap(node._1)(node)._1.isEmpty)
        case _ => false
      }
    }
  }
}
