/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.domain.AbsState
import kr.ac.kaist.safe.analyzer.console.Console
import kr.ac.kaist.safe.nodes.cfg.{ CFGEdgeExc, CFGEdgeNormal }

class Fixpoint(
    semantics: Semantics,
    worklist: Worklist,
    consoleOpt: Option[Console]
) {
  def compute(initIters: Int = 0): Int = {
    var iters = initIters
    while (!worklist.isEmpty) {
      iters += 1
      consoleOpt.fold() { _.runFixpoint }
      val cp = worklist.pop
      val st = semantics.getState(cp)
      val n = iters
      val (nextSt, nextExcSt) = semantics.C(cp, st)
      propagateNormal(cp, nextSt)
      propagateException(cp, nextExcSt)
      propagateInterProc(cp, nextSt)
    }
    consoleOpt.fold() { _.runFinished }
    iters
  }

  def propagateNormal(cp: ControlPoint, nextSt: AbsState): Unit = {
    // Propagate normal output state (outS) along normal edges.
    cp.block.getSucc(CFGEdgeNormal) match {
      case Nil => ()
      case lst => lst.foreach(block => {
        val succCP = cp.next(block, CFGEdgeNormal)
        val oldSt = semantics.getState(succCP)
        if (!(nextSt <= oldSt)) {
          val newSt = oldSt + nextSt
          semantics.setState(succCP, newSt)
          worklist.add(succCP)
        }
      })
    }
  }

  def propagateException(cp: ControlPoint, nextExcSt: AbsState): Unit = {
    // Propagate exception output state (outES) along exception edges.
    // 1) If successor is catch, current exception value is assigned to catch variable and
    //    previous exception values are restored.
    // 2) If successor is finally, current exception value is propagated further along
    //    finally block's "normal" edges.
    cp.block.getSucc(CFGEdgeExc) match {
      case Nil => ()
      case lst => lst.foreach(block => {
        val excSuccCP = cp.next(block, CFGEdgeExc)
        val oldExcSt = semantics.getState(excSuccCP)
        if (!(nextExcSt <= oldExcSt)) {
          val newExcSet = oldExcSt + nextExcSt
          semantics.setState(excSuccCP, newExcSet)
          worklist.add(excSuccCP)
        }
      })
    }
  }

  def propagateInterProc(cp: ControlPoint, nextSt: AbsState): Unit = {
    // Propagate along inter-procedural edges
    // This step must be performed after evaluating abstract transfer function
    // because 'call' instruction can add inter-procedural edges.
    semantics.getInterProcSucc(cp) match {
      case None => ()
      case Some(succMap) => {
        succMap.foreach {
          case (succCP, data) => {
            val oldSt = semantics.getState(succCP)
            val nextSt2 = semantics.E(cp, succCP, data, nextSt)
            if (!(nextSt2 <= oldSt)) {
              val newSt = oldSt + nextSt2
              semantics.setState(succCP, newSt)
              worklist.add(succCP)
            }
          }
        }
      }
    }
  }
}

