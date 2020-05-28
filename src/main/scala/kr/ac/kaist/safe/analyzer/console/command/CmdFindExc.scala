/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.console.command

import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg.{ CFGInst, CFGCallInst, CFGNormalInst }

// find the instruction whose result throws an exception
case object CmdFindExc extends Command("find-exc", "Find the instruction whose result throws an exception.") {
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        val cp = c.getCurCP
        val st = c.sem.getState(cp)
        val block = cp.block
        val insts = block.getInsts.reverse
        val (_, _, result) = insts.foldLeft((st, AbsState.Bot, true)) {
          case ((oldSt, oldExcSt, prev), inst) => {
            val (st, excSt) = inst match {
              case (i: CFGNormalInst) => c.sem.I(cp, i, oldSt, oldExcSt)
              case (i: CFGCallInst) => c.sem.CI(cp, i, oldSt, oldExcSt)
            }
            if (excSt == oldExcSt) (st, excSt, true && prev)
            else {
              printResult("The following instruction throws an exception:")
              printResult(s"  [${inst.id}] $inst")
              (st, excSt, false && prev)
            }
          }
        }
        if (result) printResult("This block does not throw any exception.")
      }
      case _ => printResult(help)
    }
    None
  }
}
