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

package kr.ac.kaist.safe.analyzer.console.command

import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg.{ CFGInst, CFGCallInst, CFGNormalInst }

// find the instruction whose result is bottom
case object CmdFindBot extends Command("find-bot", "Find the instruction whose result is bottom.") {
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        val cp = c.getCurCP
        val st = c.sem.getState(cp)
        val block = cp.block
        val insts = block.getInsts.reverse
        if (st.isBottom) printResult("This block has bottom state.")
        else {
          val (_, _, result) = insts.foldLeft((st, AbsState.Bot, true)) {
            case ((oldSt, oldExcSt, true), inst) => {
              val (st, excSt) = inst match {
                case (i: CFGNormalInst) => c.sem.I(i, oldSt, oldExcSt)
                case (i: CFGCallInst) => c.sem.CI(cp, i, oldSt, oldExcSt)
              }
              if (st.isBottom) {
                printResult("The result of the following instruction is bottom:")
                printResult(s"  [${inst.id}] $inst")
                (st, excSt, false)
              } else (st, excSt, true)
            }
            case (other, _) => other
          }
          if (result) printResult("The result of this block is not bottom.")
        }
      }
      case _ => printResult(help)
    }
    None
  }
}
