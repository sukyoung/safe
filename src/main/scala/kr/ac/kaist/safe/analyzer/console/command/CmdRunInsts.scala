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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.{ CFGCallInst, CFGNormalInst }
import org.jline.reader.LineReaderBuilder
import org.jline.terminal._

// run instructions
case object CmdRunInsts extends Command("run_insts", "Run instruction by instruction.") {
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        val cp = c.getCurCP
        val st = c.sem.getState(cp)
        val block = cp.block
        val insts = block.getInsts.reverse
        val builder: TerminalBuilder = TerminalBuilder.builder();
        val terminal: Terminal = builder.build();
        val reader = LineReaderBuilder.builder()
          .terminal(terminal)
          .build()
        printResult(insts match {
          case Nil => "* no instructions"
          case _ => c.getCurCP.block.toString(0)
        })
        flushTo(System.out)

        val (resSt, resExcSt, _) = insts.foldLeft((st, AbsState.Bot, true)) {
          case ((oldSt, oldExcSt, true), inst) =>
            printResult("\n")
            var line = ""
            while ({
              line = reader.readLine(
                s"inst: [${inst.id}] $inst" + LINE_SEP +
                  s"('s': state / 'q': stop / 'n','': next)" + LINE_SEP +
                  s"> "
              )
              val keep = line match {
                case "s" => {
                  printResult("*** state ***")
                  printResult(oldSt.toString)
                  printResult()
                  printResult("*** exception state ***")
                  printResult(oldExcSt.toString)
                  true
                }
                case "d" => true // TODO diff
                case "n" | "" => false
                case "q" => false
                case _ => true
              }
              flushTo(System.out)
              keep
            }) {}
            line match {
              case "q" => (oldSt, oldExcSt, false)
              case _ =>
                val (st, excSt) = inst match {
                  case i: CFGNormalInst => c.sem.I(cp, i, oldSt, oldExcSt)
                  case i: CFGCallInst => c.sem.CI(cp, i, oldSt, oldExcSt)
                }
                (st, excSt, true)
            }
          case (old @ (_, _, false), inst) => old
        }
        printResult("*** state ***")
        printResult(resSt.toString)
        printResult()
        printResult("*** exception state ***")
        printResult(resExcSt.toString)
      }
      case _ => printResult(help)
    }
    None
  }
}
