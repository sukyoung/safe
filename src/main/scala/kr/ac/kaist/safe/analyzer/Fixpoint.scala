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

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.console.Interactive
import kr.ac.kaist.safe.nodes.cfg._
import scala.collection.immutable.HashSet

import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP
class Fixpoint(
    semantics: Semantics,
    val consoleOpt: Option[Interactive]
) {
  def worklist: Worklist = semantics.worklist

  def compute(initIters: Int = 0, displayBot: Boolean = false): Int = {
    var iters = initIters
    while (!worklist.isEmpty) {
      iters += 1
      computeOneStep(displayBot, iters)

    }
    consoleOpt.foreach(_.runFinished)
    iters
  }

  var cpSet: Set[CFGBlock] = HashSet()

  def computeOneStep(displayBot: Boolean = false, iters: Int = 0): Unit = {
    consoleOpt.foreach(_.runFixpoint)
    val cp = worklist.pop
    val st = semantics.getState(cp)
    val (nextSt, nextExcSt) = semantics.C(cp, st)
    propagateNormal(cp, nextSt)
    propagateException(cp, nextExcSt)
    propagateInterProc(cp, nextSt)

    //print if bottom exits
    if (displayBot) {
      checkBottom(cp, st, semantics, iters - 1)
    }
  }

  def propagateNormal(cp: ControlPoint, nextSt: AbsState): Unit = {
    // Propagate normal output state (outS) along normal edges.
    cp.block.getSucc(CFGEdgeNormal) match {
      case Nil => ()
      case lst => lst.foreach(block => {
        cp.next(block, CFGEdgeNormal, semantics).foreach(succCP => {
          val oldSt = semantics.getState(succCP)
          if (!(nextSt ⊑ oldSt)) {
            val newSt = oldSt ⊔ nextSt
            semantics.setState(succCP, newSt)
            if (isReachable(succCP)) {
              worklist.add(succCP)
            }
          }
        })
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
        cp.next(block, CFGEdgeExc, semantics).foreach(excSuccCP => {
          val oldExcSt = semantics.getState(excSuccCP)
          if (!(nextExcSt ⊑ oldExcSt)) {
            val newExcSet = oldExcSt ⊔ nextExcSt
            semantics.setState(excSuccCP, newExcSet)
            if (isReachable(excSuccCP)) {
              worklist.add(excSuccCP)
            }
          }
        })
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
            succCP.block match {
              case Entry(f) =>
                val tp = succCP.tracePartition
                val exitCP = ControlPoint(f.exit, tp)
                val exitExcCP = ControlPoint(f.exitExc, tp)
                if (!semantics.getState(exitCP).isBottom) worklist.add(exitCP)
                if (!semantics.getState(exitExcCP).isBottom) worklist.add(exitExcCP)
              case _ =>
            }
            if (!(nextSt2 ⊑ oldSt)) {
              val newSt = oldSt ⊔ nextSt2
              semantics.setState(succCP, newSt)
              worklist.add(succCP)
            }
          }
        }
      }
    }
  }

  def checkBottom(cp: ControlPoint, st: AbsState, sem: Semantics, iters: Int): Boolean = {
    val block = cp.block
    val insts = block.getInsts.reverse
    if (st.isBottom) true
    else {
      val (_, _, result) = insts.foldLeft((st, AbsState.Bot, false)) {
        case ((oldSt, oldExcSt, false), inst) => {
          val (st, excSt) = inst match {
            case (i: CFGNormalInst) => sem.I(i, oldSt, oldExcSt)
            case (i: CFGCallInst) => sem.CI(cp, i, oldSt, oldExcSt)

          }
          if (st.isBottom) {
            inst match {
              case (i: CFGAssert) => (st, excSt, false)
              case _ => {
                println(s"The result of the following instruction is bottom at iteration: $iters")
                println(s"  [${inst.id}] $inst")
                println(s"<$block> @${block.span.toString}" + LINE_SEP)

                (st, excSt, true)
              }
            }
          } else (st, excSt, false)
        }

        case (other, _) => other
      }
      result
    }
  }

  def isReachable(cp: ControlPoint): Boolean = {

    val oldSt = semantics.getState(cp)
    val block = cp.block
    block.getInsts.lastOption match {
      case None => true
      case Some(inst) => {
        val st = inst match {
          case inst: CFGAssert => {
            val (st, _) = semantics.I(inst, oldSt, AbsState.Bot)
            st
          }
          case _ => oldSt
        }
        !st.isBottom
      }
    }
  }
}
