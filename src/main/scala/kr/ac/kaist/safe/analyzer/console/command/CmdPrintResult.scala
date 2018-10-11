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

import scala.util.{ Success, Failure }
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain.{ AbsState, Loc }

// result
case object CmdPrintResult extends Command("result", "Print out various information.") {
  override val help: String = {
    s"""usage: $name (exc-)state(-all) ({keyword})
       $name (exc-)loc {LocName}"""
  }

  def run(c: Interactive, args: List[String]): Option[Target] = {
    val stPattern = "(exc-|)state(-all|)".r
    val locPattern = "(exc-|)loc".r
    args match {
      case Nil => printResult(help)
      case subcmd :: args => subcmd match {
        case stPattern(exc, all) => printState(c, args, exc == "exc-", all == "all")
        case locPattern(exc) => printLoc(c, args, exc == "exc-")
        case _ => printResult(help)
      }
    }
    None
  }

  def printState(c: Interactive, args: List[String], exc: Boolean, all: Boolean): Unit = {
    val (resSt, resExcSt) = c.getResult
    val st = if (exc) resExcSt else resSt
    val str = if (all) st.toStringAll else st.toString
    args match {
      case Nil => printResult(str)
      case key :: Nil => printResult(grep(key, str))
      case _ => printResult(help)
    }
  }

  def printLoc(c: Interactive, args: List[String], exc: Boolean): Unit = args match {
    case locStr :: rest if rest.length <= 1 =>
      Loc.parse(locStr, c.cfg) match {
        case Success(loc) =>
          val (resSt, resExcSt) = c.getResult
          val st = if (exc) resExcSt else resSt
          val heap = st.heap
          heap.toStringLoc(loc)
            .fold(printResult(s"* not in heap : $locStr"))(printResult)
        case Failure(_) => printResult(s"* cannot find: $locStr")
      }
    case _ => printResult(help)
  }
}
