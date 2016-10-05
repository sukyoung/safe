/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain.State
import kr.ac.kaist.safe.analyzer.console.Console
import kr.ac.kaist.safe.nodes.cfg.{ CFGEdgeExc, CFGEdgeNormal }

class Fixpoint(
    semantics: Semantics,
    worklist: Worklist,
    consoleOpt: Option[Console]
) {
  def compute(): Int = {
    var iters = 0
    while (!worklist.isEmpty) {
      iters += 1
      consoleOpt.fold() { _.runFixpoint }
      val cp = worklist.pop
      val st = cp.getState
      val (nextSt, nextExcSt) = semantics.C(cp, st)
      propagateNormal(cp, nextSt)
      propagateException(cp, nextExcSt)
      propagateInterProc(cp, nextSt)
    }
    consoleOpt.fold() { _.runFinished }
    iters
  }

  def propagateNormal(cp: ControlPoint, nextSt: State): Unit = {
    // Propagate normal output state (outS) along normal edges.
    cp.node.getSucc(CFGEdgeNormal) match {
      case Nil => ()
      case lst => lst.foreach(node => {
        val succCP = ControlPoint(node, cp.callContext)
        val oldSt = succCP.getState
        if (!(nextSt <= oldSt)) {
          val allPredEdges = node.getPred(CFGEdgeNormal) ++ node.getPred(CFGEdgeExc)
          val newSt =
            if (allPredEdges.size <= 1) nextSt
            else oldSt + nextSt
          succCP.setState(newSt)
          worklist.add(succCP)
        }
      })
    }
  }

  def propagateException(cp: ControlPoint, nextExcSt: State): Unit = {
    // Propagate exception output state (outES) along exception edges.
    // 1) If successor is catch, current exception value is assigned to catch variable and
    //    previous exception values are restored.
    // 2) If successor is finally, current exception value is propagated further along
    //    finally block's "normal" edges.
    cp.node.getSucc(CFGEdgeExc) match {
      case Nil => ()
      case lst => lst.foreach(node => {
        val excSuccCP = ControlPoint(node, cp.callContext)
        val oldExcSt = excSuccCP.getState
        if (!(nextExcSt <= oldExcSt)) {
          val newExcSet = oldExcSt + nextExcSt
          excSuccCP.setState(newExcSet)
          worklist.add(excSuccCP)
        }
      })
    }
  }

  def propagateInterProc(cp: ControlPoint, nextSt: State): Unit = {
    // Propagate along inter-procedural edges
    // This step must be performed after evaluating abstract transfer function
    // because 'call' instruction can add inter-procedural edges.
    semantics.getInterProcSucc(cp) match {
      case None => ()
      case Some(succMap) => {
        succMap.foreach {
          case (succCP, data) => {
            val oldSt = succCP.getState
            val nextSt2 = semantics.E(cp, succCP, data, nextSt)
            if (!(nextSt2 <= nextSt)) {
              val newSt = oldSt + nextSt2
              succCP.setState(newSt)
              worklist.add(succCP)
            }
          }
        }
      }
    }
  }
}
