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

package kr.ac.kaist.safe.analyzer.console

import jline.console.ConsoleReader
import kr.ac.kaist.safe.analyzer.{ ControlPoint, Worklist }
import kr.ac.kaist.safe.analyzer.domain.{ Loc, State }
import kr.ac.kaist.safe.config.Config
import kr.ac.kaist.safe.cfg_builder.DotWriter
import kr.ac.kaist.safe.nodes._

sealed abstract class Command(
    val name: String,
    val info: String = ""
) {
  def run(c: Console, args: List[String]): Option[Target]
  def help: Unit

  protected def showState(
    c: Console,
    state: State,
    desc: String = "state",
    withPred: Boolean = true
  ): Unit = {
    println(s"** $desc **")
    val heapStr = state.heap.toString
    println(heapStr)
    println
    if (withPred) showPredefLocMap(c)
  }

  protected def showPredefLocMap(c: Console): Unit = {
    println("** predefined location name info. **")
    println(c.semantics.helper.strPredefLocMap)
  }
}

// help
case object CmdHelp extends Command("help") {
  def help: Unit = println("usage: " + name + " (command)")
  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        println("Command list:")
        Console.commands.foreach {
          case (name, cmd) =>
            println("- " + name + "\t" + cmd.info)
        }
        println("For more information, see '" + name + " <command>'.")
      }
      case str :: Nil => Console.commands.get(str) match {
        case Some(cmd) => cmd.help
        case None =>
          println("* '" + str + "' is not a command. See '" + name + "'.")
      }
      case _ => help
    }
    None
  }
}

// next
case object CmdNext extends Command("next", "jump to the next iteration. (same as \"\")") {
  def help: Unit = println("usage: " + name)
  def run(c: Console, args: List[String]): Option[Target] = args match {
    case Nil => Some(TargetIter(c.getIter + 1))
    case _ => help; None
  }
}

// jump
case object CmdJump extends Command("jump", "Continue to analyze until the given iteration.") {
  def help: Unit = println("usage: " + name + " {#iteration}")
  def run(c: Console, args: List[String]): Option[Target] = args match {
    case iter :: Nil if iter.forall(_.isDigit) => Some(TargetIter(iter.toInt))
    case _ => help; None
  }
}

// print
case object CmdPrint extends Command("print", "Print out various information.") {
  def help: Unit = {
    println("usage: " + name + " state ({keyword})")
    println("       " + name + " block")
    println("       " + name + " loc {LocName} ({keyword})")
    println("       " + name + " fid {functionID}")
    println("       " + name + " worklist")
    println("       " + name + " ipsucc")
    println("       " + name + " trace")
    println("       " + name + " run_insts")
    // TODO println("       " + name + " cfg")
  }

  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case Nil => help
      case subcmd :: rest => subcmd match {
        case "state" =>
          val heapStr = c.getCurCP.getState.heap.toString
          rest match {
            case Nil =>
              showState(c, c.getCurCP.getState)
            case key :: Nil =>
              println(grep(key, heapStr))
              println
              showPredefLocMap(c)
            case _ => help
          }
        case "block" => rest match {
          case Nil => println(c.getCurCP.node.toString(0))
          case _ => help
        }
        case "loc" => rest match {
          case locStr :: rest if rest.length <= 1 =>
            parseLocName(c, locStr) match {
              case Some(loc) =>
                val heap = c.getCurCP.getState.heap
                heap(loc) match {
                  case Some(obj) =>
                    val objStr = obj.toString
                    println(s"$locStr -> ")
                    println(rest match {
                      case Nil => objStr
                      case key :: _ => grep(key, objStr)
                    })
                  case None => println(s"* not in heap : $locStr")
                }
              case None => println(s"* cannot find: $locStr")
            }
          case _ => help
        }
        case "fid" => rest match {
          case fidStr :: Nil if fidStr.forall(_.isDigit) =>
            val fid = fidStr.toInt
            c.cfg.funMap.get(fid) match {
              case Some(func) =>
                val name = func.name
                val span = func.span
                println(s"* function name: $name")
                println(s"* span info.   : $span")
              case None => println(s"unknown fid: $fid")
            }
          case _ => help
        }
        case "worklist" => rest match {
          case Nil =>
            System.out.println("* Worklist set")
            System.out.println(c.worklist.toString)
          case _ => help
        }
        case "ipsucc" => rest match {
          case Nil =>
            val curCP = c.getCurCP

            println("* successor map")
            val succs = c.semantics.getInterProcSucc(curCP)
            println(s"- src: $curCP")
            succs match {
              case Some(m) => {
                m.foreach {
                  case (cp, (ctxt, _)) =>
                    println(s"- dst: $cp, $ctxt")
                }
              }
              case None => println("- Nothing")
            }
          case _ => help
        }
        case "trace" =>
          rest match {
            case Nil =>
              // function info.
              def f(level: Int, cp: ControlPoint): Unit = {
                val block = cp.node
                val func = block.func
                val cc = cp.callContext
                println(s"$block of $func with $cc")

                // Follow up the trace (Call relation "1(callee) : n(caller)" is possible)
                val entryCP = ControlPoint(func.entry, cc)
                c.semantics.getInterProcPred(entryCP) match {
                  case Some(cpSet) => cpSet.foreach(predCP => predCP.node match {
                    case call @ Call(_) => i(level + 1, call, predCP)
                    case _ =>
                  })
                  case None =>
                }
              }

              // instruction info.
              def i(level: Int, call: Call, cp: ControlPoint): Unit = {
                val cInst = call.callInst
                val id = cInst.id
                val span = cInst.span
                print(s"  $level>" + "  " * level + s"[$id] $cInst $span @")
                f(level, cp)
              }

              println("* Call-Context Trace")
              f(0, c.getCurCP)
            case _ => help
          }
        case "cfg" => rest match {
          case Nil => {
            // computes reachable fid_set
            val cfg = c.cfg
            val sem = c.semantics
            val reachableFunSet = sem.getAllIPSucc.foldLeft(Set[CFGFunction]()) {
              case (set, (caller, calleeMap)) => {
                set ++ (calleeMap.toSeq.map {
                  case (callee, _) => callee.node.func
                }).toSet
              }
            } + cfg.globalFunc

            // dump each function node
            val reachableUserFunSet = reachableFunSet.filter(func => func.isUser)
            val wo = c.worklist
            val o = wo.getOrderMap
            val blocks = reachableUserFunSet.foldRight(List[CFGBlock]()) {
              case (func, lst) => func.getBlocks ++ lst
            }
            println(DotWriter.drawGraph(cfg, o, Some(blocks)))
          }
          case _ => help
        }
        case _ => help
      }
    }
    None
  }

  //  var cache: Map[Node, Set[Address]] = Map()
  //   def getDefAddrSet(c: Console, node: Node): Set[Address] = {
  //     //    cache.get(node) match {
  //     //      case Some(addrset) => addrset
  //     //      case None =>
  //     var addrset = Set[Address]()
  //     c.getCFG.getCmd(node) match {
  //       case Block(insts) => {
  //         insts.foreach(inst => {
  //           inst match {
  //             case CFGAlloc(_, _, x, e, a_new) => addrset += a_new
  //             case CFGAllocArray(_, _, x, n, a_new) => addrset += a_new
  //             case CFGAllocArg(_, _, x, n, a_new) => addrset += a_new
  //             case CFGFunExpr(_, _, lhs, None, fid, a_new1, a_new2, None) => addrset ++= Set(a_new1, a_new2)
  //             case CFGFunExpr(_, _, lhs, Some(name), fid, a_new1, a_new2, Some(a_new3)) => addrset ++= Set(a_new1, a_new2, a_new3)
  //             case CFGConstruct(_, info, cons, thisArg, arguments, a_new, b_new) => addrset ++= Set(a_new, b_new)
  //             case CFGCall(_, info, fun, thisArg, arguments, a_new, b_new) => addrset ++= Set(a_new, b_new)
  //             case CFGInternalCall(_, info, lhs, fun, arguments, Some(a_new)) => addrset += a_new
  //             case CFGAPICall(info, model, fun, args) =>
  //             //                  println("  apicall : " + fun)
  //             case CFGAsyncCall(_, _, model, call_type, addr1, addr2, addr3) => addrset ++= Set(addr1, addr2, addr3)
  //             case _ => ()
  //           }
  //         })
  //       }
  //       case _ => ()
  //     }
  //     //        cache += node -> addrset
  //     addrset
  //     //    }
  //   }
  //   def printDefNode(c: Console, node: Node, addr: Address): Unit = {
  //     c.getCFG.getCmd(node) match {
  //       case Block(insts) => {
  //         insts.foreach(inst => {
  //           inst match {
  //             case CFGAlloc(_, _, x, e, a_new) if a_new == addr => println("\t\t" + inst)
  //             case CFGAllocArray(_, _, x, n, a_new) if a_new == addr => println("\t\t" + inst)
  //             case CFGAllocArg(_, _, x, n, a_new) if a_new == addr => println("\t\t" + inst)
  //             case CFGFunExpr(_, _, lhs, None, fid, a_new1, a_new2, None) if (a_new1 == addr || a_new2 == addr) => println("\t\t" + inst)
  //             case CFGFunExpr(_, _, lhs, Some(name), fid, a_new1, a_new2, Some(a_new3)) if (a_new1 == addr || a_new2 == addr || a_new3 == addr) => println("\t\t" + inst)
  //             case CFGConstruct(_, info, cons, thisArg, arguments, a_new, b_new) if (a_new == addr || b_new == addr) => println("\t\t" + inst)
  //             case CFGCall(_, info, fun, thisArg, arguments, a_new, b_new) if (a_new == addr || b_new == addr) => println("\t\t" + inst)
  //             case CFGInternalCall(_, info, lhs, fun, arguments, Some(a_new)) if (a_new == addr) => println("\t\t" + inst)
  //             case CFGAPICall(info, model, fun, args) =>
  //             //                  println("  apicall : " + fun)
  //             case CFGAsyncCall(_, _, model, call_type, addr1, addr2, addr3) if (addr1 == addr || addr2 == addr || addr3 == addr) => println("\t\t" + inst)
  //             case _ => ()
  //           }
  //         })
  //       }
  //       case _ => ()
  //     }
  //   }

  private def parseLocName(c: Console, str: String): Option[Loc] =
    c.addrManager.parseLocName(str)

  private def grep(key: String, str: String): String = {
    str.split(Config.LINE_SEP)
      .filter(_.contains(key))
      .mkString(Config.LINE_SEP)
  }
}

// TODO break
// case object CmdBreak extends Command("break") {
//   def help: Unit = () // TODO
//   def run(c: Console, args: List[String]): Unit = {
//     // TODO adds a break point on code
//     val arg = arguments.head
//     val p1 = new Regex("""([^:]+):(\\d+)""", "filename", "line")
//
//     try {
//       val p1(filename, line) = arg
//       System.out.println(line+" @"+filename)
//     } catch {
//       case e => e.printStackTrace()
//     }
//   }
// }

// TODO home
// case object CmdHome extends Command("home", "Reset the current position.") {
//   def help: Unit = println("usage: " + name)
//   def run(c: Console, args: List[String]): Option[Target] = {
//     args match {
//       case Array() => c.goHome
//       case _ => help
//     }
//     None
//   }
// }

// TODO move
// case object CmdMove extends Command("move", "Change a current position.") {
//   def help: Unit = {
//     println("usage: move {CFGBlock}")
//     println("example: move entry0")
//     println("         move block23")
//     println("         move exitexc2")
//   }
//   override def run(c: Console, args: List[String]): Unit = {
//     try {
//       if (args.length > 0) {
//         // parse first argument(cp)
//         // TODO need to support a syntax for control point.
//         val arg0 = args(0).toLowerCase
//         parseNode(c, arg0) match {
//           case Some(node) => {
//             c.getTable.get(node) match {
//               case Some(cs) => {
//                 val contexts = cs.keySet.toArray
//                 if (contexts.length == 1) {
//                   c.current = (node, contexts.head)
//                 } else {
//                   System.out.println("* Contexts")
//                   (0 to contexts.length - 1).foreach(i => System.out.println("[" + i + "]: " + contexts(i).toString))
//                   val line = c.reader.readLine("[0 to " + (contexts.length - 1) + "]? ")
//                   val i = line.toInt
//                   c.current = (node, contexts(i))
//                 }
//               }
//               case None => {
//                 System.out.println(node + " doesn't have a state")
//               }
//             }
//           }
//           case None => {
//             System.out.println("Cannot parse: " + arg0)
//           }
//         }
//       }
//     } catch {
//       case _ =>
//         if (args.length > 0)
//           System.out.println("Cannot parse command : " + args(0))
//         else
//           System.out.println("Cannot parse command : ")
//     }
//   }
// }

// TODO result
// case object CmdPrintResult extends Command("result", "Print out various information.") {
//   override def help(): Unit = {
//     println("usage: result allstate")
//     println("       result allexcstate")
//     println("       result state")
//     println("       result excstate")
//     println("       result loc {LocName}")
//     println("       result excloc {LocName}")
//     println("       result id [idNum] loc {LocName}")
//     println("       result id [idNum] excloc {LocName}")
//     println("       result ip")
//   }
// 
//   override def run(c: Console, args: List[String]): Unit = {
//     //    val sem = new Semantics(c.getCFG, c.getWorklist, Shell.params.opt_LocClone)
//     val sem = c.getSemantics
//     try {
//       val subcmd = args(0)
//       subcmd.toLowerCase match {
//         case "allstate" => {
//           val inS = c.current.getState
//           val cmd = c.getCFG.getCmd(c.current._1)
// 
//           val (outS, _) = sem.C(c.current, cmd, inS)
//           System.out.println(DomainPrinter.printHeap(0, outS._1, c.getCFG, 3))
//           if (outS._2 == ContextBot) System.out.print("Context Bottom : ")
//           System.out.println(DomainPrinter.printContext(0, outS._2))
//         }
//         case "allexcstate" => {
//           val inS = c.current.getState
//           val cmd = c.getCFG.getCmd(c.current._1)
// 
//           val (_, outES) = sem.C(c.current, cmd, inS)
//           System.out.println(DomainPrinter.printHeap(0, outES._1, c.getCFG, 3))
//           if (outES._2 == ContextBot) System.out.print("Context Bottom : ")
//           System.out.println(DomainPrinter.printContext(0, outES._2))
//         }
//         case "state" => {
//           val inS = c.current.getState
//           val cmd = c.getCFG.getCmd(c.current._1)
// 
//           val (outS, _) = sem.C(c.current, cmd, inS)
//           System.out.println(DomainPrinter.printHeap(0, outS._1, c.getCFG))
//           if (outS._2 == ContextBot) System.out.print("Context Bottom : ")
//           System.out.println(DomainPrinter.printContext(0, outS._2))
//         }
//         case "excstate" => {
//           val inS = c.current.getState
//           val cmd = c.getCFG.getCmd(c.current._1)
// 
//           val (_, outES) = sem.C(c.current, cmd, inS)
// 
//           System.out.println(DomainPrinter.printHeap(0, outES._1, c.getCFG))
//           if (outES._2 == ContextBot) System.out.print("Context Bottom : ")
//           System.out.println(DomainPrinter.printContext(0, outES._2))
//         }
//         case "loc" if args.length > 1 => {
//           val arg1 = args(1)
//           val sloc = parseLocName(arg1)
//           sloc match {
//             case Some(loc) => {
//               val inS = c.current.getState
//               val cmd = c.getCFG.getCmd(c.current._1)
// 
//               val (outS, _) = sem.C(c.current, cmd, inS)
// 
//               if (outS._1.domIn(loc)) {
//                 val o = outS._1(loc)
//                 val name = DomainPrinter.printLoc(loc)
//                 System.out.println(name + " -> ")
//                 System.out.println(DomainPrinter.printObj(4 + name.length, o))
//               } else {
//                 println(" Not in heap : " + DomainPrinter.printLoc(loc))
//               }
// 
//             }
//             case None => {
//               System.err.println("cannot find: " + arg1)
//             }
//           }
//         }
//         case "excloc" if args.length > 1 => {
//           val arg1 = args(1)
//           val sloc = parseLocName(arg1)
//           sloc match {
//             case Some(loc) => {
//               val inS = c.current.getState
//               val cmd = c.getCFG.getCmd(c.current._1)
// 
//               val (_, outES) = sem.C(c.current, cmd, inS)
//               val o = outES._1(loc)
//               val name = DomainPrinter.printLoc(loc)
//               System.out.println(name + " -> ")
//               System.out.println(DomainPrinter.printObj(4 + name.length, o))
//             }
//             case None => {
//               System.err.println("cannot find: " + arg1)
//             }
//           }
//         }
//         case "id" if args.length <= 3 => {
//           try {
//             val arg1 = args(1)
//             val id = arg1.toInt
//             val cmd_ = c.getCFG.getCmd(c.current._1)
//             val inS = c.current.getState
// 
//             val cmd = cmd_ match {
//               case Block(insts) => Block(insts.filter(inst => inst.getInstId <= id))
//               case _ => cmd_
//             }
//             val (outS, outES) = sem.C(c.current, cmd, inS)
//             cmd match {
//               case Block(insts) =>
//                 System.out.println("- Command")
//                 for (inst <- insts) {
//                   System.out.println("    [" + inst.getInstId + "] " + inst.toString)
//                 }
//                 System.out.println()
//               case _ => System.out.println("- Nothing")
//             }
//             System.out.println(DomainPrinter.printHeap(0, outS._1, c.getCFG))
//             if (outS._2 == ContextBot) System.out.print("Context Bottom : ")
//             System.out.println(DomainPrinter.printContext(0, outS._2))
//             if (!(outES <= StateBot))
//               System.err.println(" * Exception state is not bottom.")
//           } catch {
//             case _ => println(" * Invalid input for result id ")
//           }
//         }
//         case "id" if (args.length > 3 && args(2).equals("loc")) => {
//           val arg1 = args(1)
//           val id = arg1.toInt
//           val arg2 = args(3)
//           val sloc = parseLocName(arg2)
//           val cmd_ = c.getCFG.getCmd(c.current._1)
//           val inS = c.current.getState
// 
//           sloc match {
//             case Some(loc) => {
//               val cmd = cmd_ match {
//                 case Block(insts) => Block(insts.filter(inst => inst.getInstId <= id))
//                 case _ => cmd_
//               }
//               cmd match {
//                 case Block(insts) =>
//                   System.out.println("- Command")
//                   for (inst <- insts) {
//                     System.out.println("    [" + inst.getInstId + "] " + inst.toString)
//                   }
//                   System.out.println()
//                 case _ => System.out.println("- Nothing")
//               }
//               val (outS, outES) = sem.C(c.current, cmd, inS)
//               val name = DomainPrinter.printLoc(loc)
// 
//               if (outS._1.domIn(loc)) {
//                 val o = outS._1(loc)
//                 System.out.println(name + " -> ")
//                 System.out.println(DomainPrinter.printObj(4 + name.length, o))
//               } else {
//                 println(" Not in heap : " + DomainPrinter.printLoc(loc))
//               }
//               if (!(outES <= StateBot))
//                 System.err.println(" * Exception state is not bottom.")
//             }
//             case None => {
//               System.err.println("cannot find: " + arg2)
//             }
//           }
//         }
//         case "id" if (args.length > 3 && args(2).equals("excloc")) => {
//           val arg1 = args(1)
//           val id = arg1.toInt
//           val arg2 = args(3)
//           val sloc = parseLocName(arg2)
//           val cmd_ = c.getCFG.getCmd(c.current._1)
//           val inS = c.current.getState
// 
//           sloc match {
//             case Some(loc) => {
//               val cmd = cmd_ match {
//                 case Block(insts) => Block(insts.filter(inst => inst.getInstId <= id))
//                 case _ => cmd_
//               }
//               cmd match {
//                 case Block(insts) =>
//                   System.out.println("- Command")
//                   for (inst <- insts) {
//                     System.out.println("    [" + inst.getInstId + "] " + inst.toString)
//                   }
//                   System.out.println()
//                 case _ => System.out.println("- Nothing")
//               }
//               val (_, outES) = sem.C(c.current, cmd, inS)
//               if ((outES <= StateBot))
//                 System.err.println(" * Exception state is bottom.")
//               else {
//                 val name = DomainPrinter.printLoc(loc)
//                 val o = outES._1(loc)
//                 System.out.println(name + " -> ")
//                 System.out.println(DomainPrinter.printObj(4 + name.length, o))
//               }
//             }
//             case None => {
//               System.err.println("cannot find: " + arg2)
//             }
//           }
//         }
//         case "ip" if args.length < 3 => {
//           val inS = c.current.getState
//           val cmd = c.getCFG.getCmd(c.current._1)
//           val (outS, _) = c.getSemantics.C(c.current, cmd, inS)
//           c.getSemantics.getIPSucc(c.current) match {
//             case Some(succMap) =>
//               succMap.foreach(kv => {
//                 val (ipSucc, (ctx, obj)) = kv
//                 val outS_E = sem.E(c.current, ipSucc, ctx, obj, outS)
//                 System.out.println(" * for IPSucc : " + ipSucc)
//                 System.out.println(DomainPrinter.printHeap(0, outS_E._1, c.getCFG))
//                 if (outS_E._2 == ContextBot) System.out.print("Context Bottom : ")
//                 System.out.println(DomainPrinter.printContext(0, outS_E._2))
//               })
//             case None => System.err.println(" * " + c.current + " does not have ipSucc.")
//           }
//         }
//         case "ip" if args.length >= 3 && args(1).equals("loc") => {
//           val sloc = parseLocName(args(2))
//           val inS = c.current.getState
//           val cmd = c.getCFG.getCmd(c.current._1)
//           val (outS, _) = c.getSemantics.C(c.current, cmd, inS)
//           System.out.println(DomainPrinter.printLoc(sloc.get) + " -> ")
//           System.out.println(DomainPrinter.printObj(4 + name.length, outS._1(sloc.get)))
// 
//           c.getSemantics.getIPSucc(c.current) match {
//             case Some(succMap) =>
//               succMap.foreach(kv => {
//                 val (ipSucc, (ctx, obj)) = kv
//                 val outS_E = c.getSemantics.E(c.current, ipSucc, ctx, obj, outS)
//                 sloc match {
//                   case Some(loc) =>
//                     if (outS_E._1.domIn(loc)) {
//                       val o = outS_E._1(loc)
//                       val name = DomainPrinter.printLoc(loc)
//                       System.out.println(" * for IPSucc : " + ipSucc)
//                       System.out.println(name + " -> ")
//                       System.out.println(DomainPrinter.printObj(4 + name.length, o))
//                     } else {
//                       println(" Not in heap : " + DomainPrinter.printLoc(loc))
//                     }
//                   case None =>
//                     System.err.println("cannot find: " + args(2))
//                 }
//               })
//             case None => System.err.println(" * " + c.current + " does not have ipSucc.")
//           }
//         }
//         case _ => {
//           System.err.println("Illegal arguments: " + subcmd)
//         }
//       }
//     } catch {
//       case e: ArrayIndexOutOfBoundsException => help()
//       case _ => System.err.println("Illegal arguments")
//     }
//   }
// }

// run
case object CmdRun extends Command("run") {
  def help: Unit = println("usage: " + name)
  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case Nil => Some(TargetIter(-1))
      case _ => help; None
    }
  }
}

// run instructions
case object CmdRunInsts extends Command("run_insts") {
  def help: Unit = println("usage: " + name)
  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        val cp = c.getCurCP
        val st = cp.getState
        val block = cp.node
        val insts = block.getInsts.reverse
        val reader = new ConsoleReader()
        insts match {
          case Nil => println("* no instructions")
          case _ => println(c.getCurCP.node.toString(0))
        }
        val (resSt, resExcSt, _) = insts.foldLeft((st, State.Bot, true)) {
          case ((oldSt, oldExcSt, true), inst) =>
            println
            reader.setPrompt(
              s"inst: [${inst.id}] $inst" + Config.LINE_SEP +
                s"('s': show / 'q': stop / 'n','': next)" + Config.LINE_SEP +
                s"> "
            )
            var line = ""
            while ({
              line = reader.readLine
              line match {
                case "s" => {
                  showState(c, oldSt, "state", false)
                  showState(c, oldExcSt, "exception state", false)
                  showPredefLocMap(c)
                  true
                }
                case "d" => true // TODO diff
                case "n" | "" => false
                case "q" => false
                case _ => true
              }
            }) {}
            line match {
              case "q" => (oldSt, oldExcSt, false)
              case _ =>
                val (st, excSt) = c.semantics.I(cp, inst, oldSt, oldExcSt)
                (st, excSt, true)
            }
          case (old @ (_, _, false), inst) => old
        }
        showState(c, resSt, "state", false)
        showState(c, resExcSt, "exception state", false)
        showPredefLocMap(c)
      }
      case _ => help
    }
    None
  }
}
