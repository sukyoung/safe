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

import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.errors.error.IllFormedBlockStr

// move
case object CmdMove extends Command("move", "Change a current position.") {
  def help: Unit = {
    println("usage: " + name + " {fid}:{bid}")
    println("       " + name + " {fid}:entry")
    println("       " + name + " {fid}:exit")
    println("       " + name + " {fid}:exitExc")
  }

  def run(c: Console, args: List[String]): Option[Target] = {
    val cfg = c.cfg
    args match {
      case subcmd :: Nil => cfg.findBlock(subcmd) match {
        case Success(block) => c.moveCurCP(block)
        case Failure(e) => e match {
          case IllFormedBlockStr => help
          case _ => println(s"* ${e.getMessage}")
        }
      }
      case _ => help
    }
    None
  }
}
