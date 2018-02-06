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
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain.Loc

// result
case object CmdPrintResult extends Command("result", "Print out various information.") {
  override val help: String = {
    s"""usage: $name (exc-)state(-all) ({keyword})
       $name (exc-)loc {LocName}"""
    // TODO "       " + name + " id [idNum] loc {LocName}"
    // TODO "       " + name + " id [idNum] excloc {LocName}"
    // TODO "       " + name + " ip"
  }

  def run(c: Interactive, args: List[String]): Option[Target] = {
    val sem = c.sem
    val cp = c.getCurCP
    val st = sem.getState(cp)
    val (resSt, resExcSt) = sem.C(cp, st)
    val stPattern = "(exc-|)state(-all|)".r
    val locPattern = "(exc-|)loc".r
    args match {
      case Nil => printResult(help)
      case subcmd :: rest => subcmd match {
        case stPattern(exc, all) =>
          val state = exc match {
            case "exc-" => resExcSt
            case _ => resSt
          }
          val res =
            if (all == "-all") state.toStringAll
            else state.toString
          rest match {
            case Nil => printResult(res)
            case key :: Nil => printResult(grep(key, res))
            case _ => printResult(help)
          }
        case locPattern(exc) => rest match {
          case locStr :: rest if rest.length <= 1 =>
            Loc.parse(locStr) match {
              case Success(loc) =>
                val heap = (exc match {
                  case "exc-" => resExcSt
                  case _ => resSt
                }).heap
                heap.toStringLoc(loc) match {
                  case Some(res) => printResult(res)
                  case None => printResult(s"* not in heap : $locStr")
                }
              case Failure(_) => printResult(s"* cannot find: $locStr")
            }
          case _ => printResult(help)
        }
        case _ => printResult(help)
      }
    }
    // case "id" if args.length <= 3 => {
    //   try {
    //     val arg1 = args(1)
    //     val id = arg1.toInt
    //     val cmd_ = c.getCFG.getCmd(c.current._1)
    //     val inS = c.current.getState

    //     val cmd = cmd_ match {
    //       case Block(insts) => Block(insts.filter(inst => inst.getInstId <= id))
    //       case _ => cmd_
    //     }
    //     val (outS, outES) = sem.C(c.current, cmd, inS)
    //     cmd match {
    //       case Block(insts) =>
    //         println("- Command")
    //         for (inst <- insts) {
    //           println("    [" + inst.getInstId + "] " + inst.toString)
    //         }
    //         println()
    //       case _ => println("- Nothing")
    //     }
    //     println(DomainPrinter.printHeap(0, outS._1, c.getCFG))
    //     if (outS._2 == ContextBot) print("Context Bottom : ")
    //     println(DomainPrinter.printContext(0, outS._2))
    //     if (!(outES <= StateBot))
    //       System.err.println(" * Exception state is not bottom.")
    //   } catch {
    //     case _ => println(" * Invalid input for result id ")
    //   }
    // }
    // case "id" if (args.length > 3 && args(2).equals("loc")) => {
    //   val arg1 = args(1)
    //   val id = arg1.toInt
    //   val arg2 = args(3)
    //   val sloc = parseLocName(arg2)
    //   val cmd_ = c.getCFG.getCmd(c.current._1)
    //   val inS = c.current.getState

    //   sloc match {
    //     case Some(loc) => {
    //       val cmd = cmd_ match {
    //         case Block(insts) => Block(insts.filter(inst => inst.getInstId <= id))
    //         case _ => cmd_
    //       }
    //       cmd match {
    //         case Block(insts) =>
    //           println("- Command")
    //           for (inst <- insts) {
    //             println("    [" + inst.getInstId + "] " + inst.toString)
    //           }
    //           println()
    //         case _ => println("- Nothing")
    //       }
    //       val (outS, outES) = sem.C(c.current, cmd, inS)
    //       val name = DomainPrinter.printLoc(loc)

    //       if (outS._1.domIn(loc)) {
    //         val o = outS._1(loc)
    //         println(name + " -> ")
    //         println(DomainPrinter.printObj(4 + name.length, o))
    //       } else {
    //         println(" Not in heap : " + DomainPrinter.printLoc(loc))
    //       }
    //       if (!(outES <= StateBot))
    //         System.err.println(" * Exception state is not bottom.")
    //     }
    //     case None => {
    //       System.err.println("cannot find: " + arg2)
    //     }
    //   }
    // }
    // case "id" if (args.length > 3 && args(2).equals("excloc")) => {
    //   val arg1 = args(1)
    //   val id = arg1.toInt
    //   val arg2 = args(3)
    //   val sloc = parseLocName(arg2)
    //   val cmd_ = c.getCFG.getCmd(c.current._1)
    //   val inS = c.current.getState

    //   sloc match {
    //     case Some(loc) => {
    //       val cmd = cmd_ match {
    //         case Block(insts) => Block(insts.filter(inst => inst.getInstId <= id))
    //         case _ => cmd_
    //       }
    //       cmd match {
    //         case Block(insts) =>
    //           println("- Command")
    //           for (inst <- insts) {
    //             println("    [" + inst.getInstId + "] " + inst.toString)
    //           }
    //           println()
    //         case _ => println("- Nothing")
    //       }
    //       val (_, outES) = sem.C(c.current, cmd, inS)
    //       if ((outES <= StateBot))
    //         System.err.println(" * Exception state is bottom.")
    //       else {
    //         val name = DomainPrinter.printLoc(loc)
    //         val o = outES._1(loc)
    //         println(name + " -> ")
    //         println(DomainPrinter.printObj(4 + name.length, o))
    //       }
    //     }
    //     case None => {
    //       System.err.println("cannot find: " + arg2)
    //     }
    //   }
    // }
    // case "ip" if args.length < 3 => {
    //   val inS = c.current.getState
    //   val cmd = c.getCFG.getCmd(c.current._1)
    //   val (outS, _) = c.getSemantics.C(c.current, cmd, inS)
    //   c.getSemantics.getIPSucc(c.current) match {
    //     case Some(succMap) =>
    //       succMap.foreach(kv => {
    //         val (ipSucc, (ctx, obj)) = kv
    //         val outS_E = sem.E(c.current, ipSucc, ctx, obj, outS)
    //         println(" * for IPSucc : " + ipSucc)
    //         println(DomainPrinter.printHeap(0, outS_E._1, c.getCFG))
    //         if (outS_E._2 == ContextBot) print("Context Bottom : ")
    //         println(DomainPrinter.printContext(0, outS_E._2))
    //       })
    //     case None => System.err.println(" * " + c.current + " does not have ipSucc.")
    //   }
    // }
    // case "ip" if args.length >= 3 && args(1).equals("loc") => {
    //   val sloc = parseLocName(args(2))
    //   val inS = c.current.getState
    //   val cmd = c.getCFG.getCmd(c.current._1)
    //   val (outS, _) = c.getSemantics.C(c.current, cmd, inS)
    //   println(DomainPrinter.printLoc(sloc.get) + " -> ")
    //   println(DomainPrinter.printObj(4 + name.length, outS._1(sloc.get)))

    //   c.getSemantics.getIPSucc(c.current) match {
    //     case Some(succMap) =>
    //       succMap.foreach(kv => {
    //         val (ipSucc, (ctx, obj)) = kv
    //         val outS_E = c.getSemantics.E(c.current, ipSucc, ctx, obj, outS)
    //         sloc match {
    //           case Some(loc) =>
    //             if (outS_E._1.domIn(loc)) {
    //               val o = outS_E._1(loc)
    //               val name = DomainPrinter.printLoc(loc)
    //               println(" * for IPSucc : " + ipSucc)
    //               println(name + " -> ")
    //               println(DomainPrinter.printObj(4 + name.length, o))
    //             } else {
    //               println(" Not in heap : " + DomainPrinter.printLoc(loc))
    //             }
    //           case None =>
    //             System.err.println("cannot find: " + args(2))
    //         }
    //       })
    //     case None => System.err.println(" * " + c.current + " does not have ipSucc.")
    //   }
    // }
    None
  }
}
