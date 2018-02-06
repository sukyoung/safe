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

import scala.util.{ Success, Failure }
import kr.ac.kaist.safe.analyzer.ControlPoint //, Worklist }
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
       $name func ({fid})
       $name function ({fid})
       $name worklist
       $name ipsucc"""

  def run(c: Interactive, args: List[String]): Option[Target] = {
    val idPattern = "(-?\\d+):(\\d+)".r
    val spPattern = "(-?\\d+):(entry|exit|exit-exc)".r
    args match {
      case Nil => printResult(help)
      case subcmd :: rest => subcmd match {
        case "state" =>
          val res = c.sem.getState(c.getCurCP).toString
          rest match {
            case Nil => printResult(res)
            case key :: Nil => printResult(grep(key, res))
            case _ => printResult(help)
          }
        case "state-all" =>
          val res = c.sem.getState(c.getCurCP).toStringAll
          rest match {
            case Nil => printResult(res)
            case key :: Nil => printResult(grep(key, res))
            case _ => printResult(help)
          }
        case "heap" =>
          val res = c.sem.getState(c.getCurCP).heap.toString
          rest match {
            case Nil => printResult(res)
            case key :: Nil => printResult(grep(key, res))
            case _ => printResult(help)
          }
        case "heap-all" =>
          val res = c.sem.getState(c.getCurCP).heap.toStringAll
          rest match {
            case Nil => printResult(res)
            case key :: Nil => printResult(grep(key, res))
            case _ => printResult(help)
          }
        case "context" =>
          val res = c.sem.getState(c.getCurCP).context.toString
          rest match {
            case Nil => printResult(res)
            case key :: Nil => printResult(grep(key, res))
            case _ => printResult(help)
          }
        case "block" => (rest match {
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
        case "loc" => rest match {
          case locStr :: rest if rest.length <= 1 =>
            Loc.parse(locStr) match {
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
        case "func" => rest match {
          case Nil =>
            c.cfg.getAllFuncs.reverse.foreach {
              case func =>
                val fid = func.id
                val name = func.simpleName
                printResult(s"[$fid] $name")
            }
          case fidStr :: Nil if fidStr.matches("-?\\d+") =>
            val fid = fidStr.toInt
            c.cfg.getFunc(fid) match {
              case Some(func) =>
                val name = func.simpleName
                val span = func.span
                printResult(s"* function name: $name")
                printResult(s"* span info.   : $span")
              case None => printResult(s"unknown fid: $fid")
            }
          case _ => printResult(help)
        }
        case "worklist" => rest match {
          case Nil =>
            printResult("* Worklist set")
            printResult(c.worklist.toString)
          case _ => printResult(help)
        }
        case "ipsucc" => rest match {
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
                    printResult(s"  $cp, ${data.old}")
                }
              }
              case None => printResult("- Nothing")
            }
          case _ => printResult(help)
        }
        // TODO case "trace" =>
        //   rest match {
        //     case Nil =>
        //       // function info.
        //       def f(level: Int, cp: ControlPoint): Unit = {
        //         val block = cp.block
        //         val func = block.func
        //         val tp = cp.tracePartition
        //         printResult(s"$block of $func with $tp")

        //         // Follow up the trace (Call relation "1(callee) : n(caller)" is possible)
        //         val exitCP = ControlPoint(func.exit, tp)
        //         c.sem.getInterProcSucc(exitCP) match {
        //           case Some(cpMap) => cpMap.keySet.foreach(predCP => predCP.block match {
        //             case call @ Call(_) => i(level + 1, call, predCP)
        //             case _ =>
        //           })
        //           case None =>
        //         }
        //       }

        //       // instruction info.
        //       def i(level: Int, call: Call, cp: ControlPoint): Unit = {
        //         val cInst = call.callInst
        //         val id = cInst.id
        //         val span = cInst.span
        //         print(s"  $level>" + "  " * level + s"[$id] $cInst $span @")
        //         f(level, cp)
        //       }

        //       printResult("* Call-Context Trace")
        //       f(0, c.getCurCP)
        //     case _ => printResult(help)
        //   }
        case "function" => rest match {
          case Nil =>
            val fid = c.getCurCP.block.func.id

            c.cfg.getFunc(fid) match {
              case Some(func) => println(func.toString(0))
              case None =>
            }
          case fidStr :: Nil if fidStr.matches("-?\\d+") =>
            val fid = fidStr.toInt
            c.cfg.getFunc(fid) match {
              case Some(func) => println(func.toString(0))
              case None => println(s"unknown fid: $fid")
            }
          case _ => help
        }
        // TODO case "html" => rest match {
        //   case name :: Nil => HTMLWriter.writeHTMLFile(c.cfg, c.sem, Some(c.worklist), s"$name.html")
        //   case _ => printResult(help)
        // }
        case _ => printResult(help)
      }
    }
    None
  }
}
