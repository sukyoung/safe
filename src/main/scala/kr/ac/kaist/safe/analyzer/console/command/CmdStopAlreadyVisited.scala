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

import kr.ac.kaist.safe.analyzer.console._

// add break
case object CmdStopAlreadyVisited extends Command("stop-already-visited", "Switch about stopping on already visited controlpoints.") {
  override val help: String = {
    s"""usage: $name on
       $name off"""
  }

  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case "on" :: Nil => c.stopAlreadyVisited = true
      case "off" :: Nil => c.stopAlreadyVisited = false
      case _ => printResult(help)
    }
    None
  }
}
