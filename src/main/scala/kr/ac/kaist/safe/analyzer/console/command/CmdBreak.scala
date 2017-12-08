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

// add break
case object CmdBreak extends Command("break", "Add a break point.") {
  def help: Unit = {
    println("usage: " + name + " {fid}:{bid}")
    println("       " + name + " {fid}:entry")
    println("       " + name + " {fid}:exit")
    println("       " + name + " {fid}:exitExc")
  }

  def run(c: Console, args: List[String]): Option[Target] = {
    val cfg = c.cfg
    val idPattern = "(-?\\d+):(\\d+)".r
    val spPattern = "(-?\\d+):(entry|exit|exit-exc)".r
    args match {
      case subcmd :: Nil => subcmd match {
        case idPattern(fidStr, bidStr) => {
          val fid = fidStr.toInt
          val bid = bidStr.toInt
          cfg.getFunc(fid) match {
            case Some(func) => func.getBlock(bid) match {
              case Some(block) => c.addBreak(block)
              case None => println(s"* unknown bid in function[$fid]: $bid")
            }
            case None => println(s"* unknown fid: $fid")
          }
        }
        case spPattern(fidStr, sp) => {
          val fid = fidStr.toInt
          cfg.getFunc(fid) match {
            case Some(func) => {
              val block = sp match {
                case "entry" => func.entry
                case "exit" => func.exit
                case _ => func.exitExc
              }
              c.addBreak(block)
            }
            case None => println(s"* unknown fid: $fid")
          }
        }
        case _ => help
      }
      case _ => help
    }
    None
  }
}
