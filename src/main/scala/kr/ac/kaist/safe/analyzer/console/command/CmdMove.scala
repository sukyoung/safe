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
  override val help: String = s"""usage: $name {fid}:{bid}
           $name {fid}:entry
           $name {fid}:exit
           $name {fid}:exitExc"""

  def run(c: Interactive, args: List[String]): Option[Target] = {
    val cfg = c.cfg
    args match {
      case subcmd :: Nil => cfg.findBlock(subcmd) match {
        case Success(block) => c.moveCurCP(block)
        case Failure(e) => e match {
          case IllFormedBlockStr => printResult(help)
          case _ => printResult(s"* ${e.getMessage}")
        }
      }
      case _ => printResult(help)
    }
    None
  }
}
