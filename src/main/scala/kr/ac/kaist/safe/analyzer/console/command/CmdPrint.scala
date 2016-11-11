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

import scala.util.{ Success, Failure }
import kr.ac.kaist.safe.analyzer.ControlPoint //, Worklist }
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.html_debugger._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.cfg_builder.DotWriter

// print
case object CmdPrint extends Command("print", "Print out various information.") {
  def help: Unit = {
    println("usage: " + name + " state(-all) ({keyword})")
    println("       " + name + " block")
    println("       " + name + " loc {LocName} ({keyword})")
    println("       " + name + " func ({functionID})")
    println("       " + name + " worklist")
    println("       " + name + " ipsucc")
    println("       " + name + " trace")
    println("       " + name + " cfg {name}")
    println("       " + name + " html {name}")
  }

  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case Nil => help
      case subcmd :: rest => subcmd match {
        case "state" =>
          val res = showState(c, c.getCurCP.getState)
          rest match {
            case Nil => println(res)
            case key :: Nil => println(grep(key, res))
            case _ => help
          }
        case "state-all" =>
          val res = showState(c, c.getCurCP.getState, true)
          rest match {
            case Nil => println(res)
            case key :: Nil => println(grep(key, res))
            case _ => help
          }
        case "block" => rest match {
          case Nil => println(c.getCurCP.node.toString(0))
          case _ => help
        }
        case "loc" => rest match {
          case locStr :: rest if rest.length <= 1 =>
            Loc.parse(locStr) match {
              case Success(loc) =>
                val state = c.getCurCP.getState
                val heap = state.heap
                state.heap.toStringLoc(loc) match {
                  case Some(res) => println(res)
                  case None => state.context.toStringLoc(loc) match {
                    case Some(res) => println(res)
                    case None => println(s"* not in state : $locStr")
                  }
                }
              case Failure(_) => println(s"* cannot find: $locStr")
            }
          case _ => help
        }
        case "func" => rest match {
          case Nil =>
            c.cfg.getAllFuncs.reverse.foreach {
              case func =>
                val fid = func.id
                val name = func.name
                println(s"[$fid] $name")
            }
          case fidStr :: Nil if fidStr.forall(_.isDigit) =>
            val fid = fidStr.toInt
            c.cfg.getFunc(fid) match {
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
                  case (cp, data) =>
                    println(s"- dst: $cp, ${data.old}")
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
          case name :: Nil => {
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
            val cur = c.getCurCP.node

            // dump each function node
            val reachableUserFunSet = reachableFunSet.filter(func => func.isUser)
            val wo = c.worklist
            val o = wo.getOrderMap
            val blocks = reachableUserFunSet.foldRight(List[CFGBlock]()) {
              case (func, lst) => func.getAllBlocks ++ lst
            }.reverse
            println(cfg.toString(0))
            DotWriter.spawnDot(cfg, Some(o), Some(cur), Some(blocks), s"$name.gv", s"$name.pdf")
          }
          case _ => help
        }
        case "html" => rest match {
          case name :: Nil => HTMLWriter.writeHTMLFile(c.cfg, Some(c.worklist), s"$name.html")
          case _ => help
        }
        case _ => help
      }
    }
    None
  }
}
