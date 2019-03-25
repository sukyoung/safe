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
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.ControlPoint
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.html_debugger._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.errors.error.IllFormedBlockStr
import kr.ac.kaist.safe.nodes.cfg._

// print
case object CmdPrint extends Command("print", "Print out various information.") {
  override val help: String = s"""usage: $name state(-all) ({keyword})
       $name heap(-all) ({keyword})
       $name context ({keyword})
       $name block ({fid}:{bid})
       $name loc {LocName} ({keyword})
       $name function ({fid})
       $name worklist
       $name ipsucc
       $name sens"""

  def run(c: Interactive, args: List[String]): Option[Target] = {
    val idPattern = "(-?\\d+):(\\d+)".r
    val spPattern = "(-?\\d+):(entry|exit|exit-exc)".r
    args match {
      case Nil => printResult(help)
      case subcmd :: args => subcmd match {
        case "state" => printState(c, args)
        case "state-all" => printState(c, args, all = true)
        case "heap" => printHeap(c, args)
        case "heap-all" => printHeap(c, args, all = true)
        case "context" => printContext(c, args)
        case "block" => printBlock(c, args)
        case "loc" => printLoc(c, args)
        case "function" => printFunc(c, args)
        case "worklist" => printWorklist(c, args)
        case "ipsucc" => printIPSucc(c, args)
        case "sens" => printSens(c, args)
        case _ => printResult(help)
      }
    }
    None
  }

  def printState(c: Interactive, args: List[String], all: Boolean = false): Unit = {
    val st = c.sem.getState(c.getCurCP)
    val res = if (all) st.toStringAll else st.toString
    args match {
      case Nil => printResult(res)
      case key :: Nil => printResult(grep(key, res))
      case _ => printResult(help)
    }
  }

  def printHeap(c: Interactive, args: List[String], all: Boolean = false): Unit = {
    val heap = c.sem.getState(c.getCurCP).heap
    val res = if (all) heap.toStringAll else heap.toString
    args match {
      case Nil => printResult(res)
      case key :: Nil => printResult(grep(key, res))
      case _ => printResult(help)
    }
  }

  def printContext(c: Interactive, args: List[String]): Unit = {
    val res = c.sem.getState(c.getCurCP).context.toString
    args match {
      case Nil => printResult(res)
      case key :: Nil => printResult(grep(key, res))
      case _ => printResult(help)
    }
  }

  def printBlock(c: Interactive, args: List[String]): Unit = (args match {
    case Nil => Some(c.getCurCP.block)
    case subcmd :: Nil => c.cfg.findBlock(subcmd) match {
      case Success(block) => Some(block)
      case Failure(e) => e match {
        case IllFormedBlockStr => {
          printResult("usage: print block {fid}:{bid}")
          printResult("       print block {fid}:entry")
          printResult("       print block {fid}:exit")
          printResult("       print block {fid}:exitExc")
          None
        }
        case _ => printResult(s"* ${e.getMessage}"); None
      }
    }
    case _ => printResult(help); None
  }) match {
    case Some(block) =>
      val span = block.span
      printResult(s"span: $span")
      printResult(block.toString(0))
    case None =>
  }

  def printLoc(c: Interactive, args: List[String]): Unit = args match {
    case locStr :: args if args.length <= 1 =>
      Loc.parse(locStr, c.cfg) match {
        case Success(loc) =>
          val state = c.sem.getState(c.getCurCP)
          val heap = state.heap
          state.heap.toStringLoc(loc) match {
            case Some(res) => printResult(res)
            case None => state.context.toStringLoc(loc) match {
              case Some(res) => printResult(res)
              case None => printResult(s"* not in state : $locStr")
            }
          }
        case Failure(_) => printResult(s"* cannot find: $locStr")
      }
    case _ => printResult(help)
  }

  def printFunc(c: Interactive, args: List[String]): Unit = args match {
    case Nil =>
      c.cfg.getAllFuncs.reverse.foreach(printFunc)
    case fidStr :: Nil if fidStr.matches("-?\\d+") =>
      val fid = fidStr.toInt
      c.cfg.getFunc(fid)
        .fold(printResult(s"unknown fid: $fid"))(printFunc)
    case _ => printResult(help)
  }

  def printWorklist(c: Interactive, args: List[String]): Unit = args match {
    case Nil =>
      printResult("* Worklist set")
      printResult(c.worklist.toString)
    case _ => printResult(help)
  }

  def printIPSucc(c: Interactive, args: List[String]): Unit = args match {
    case Nil =>
      val curCP = c.getCurCP

      printResult("* successor map")
      val succs = c.sem.getInterProcSucc(curCP)
      printResult(s"- src: $curCP")
      succs match {
        case Some(m) => {
          printResult("- dst:")
          m.foreach {
            case (cp, data) =>
              printResult(s"  $cp, $data")
          }
        }
        case None => printResult("- Nothing")
      }
    case _ => printResult(help)
  }

  def printSens(c: Interactive, args: List[String]): Unit = args match {
    case Nil => c.getCurCP.tracePartition.toStringList.foreach(printResult)
    case _ => printResult(help)
  }
}
