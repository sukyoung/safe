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

package kr.ac.kaist.safe.analyzer.console.command

import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.nodes.cfg.{ CFGInst, CFGCallInst, CFGNormalInst }

// find the instruction whose result is bottom
case object CmdFindBot extends Command("find-bot", "Find the instruction whose result is bottom.") {
  def help: Unit = {
    println("usage: " + name)
  }

  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        val cp = c.getCurCP
        val st = cp.getState
        val block = cp.block
        val insts = block.getInsts.reverse
        if (st.isBottom) println("This block has bottom state.")
        else {
          val (_, _, result) = insts.foldLeft((st, AbsState.Bot, true)) {
            case ((oldSt, oldExcSt, true), inst) => {
              val (st, excSt) = inst match {
                case (i: CFGNormalInst) => c.semantics.I(i, oldSt, oldExcSt)
                case (i: CFGCallInst) => c.semantics.CI(cp, i, oldSt, oldExcSt)
              }
              if (st.isBottom) {
                println("The result of the following instruction is bottom:")
                println(s"  [${inst.id}] $inst")
                (st, excSt, false)
              } else (st, excSt, true)
            }
            case (other, _) => other
          }
          if (result) println("The result of this block is not bottom.")
        }
      }
      case _ => help
    }
    None
  }
}
