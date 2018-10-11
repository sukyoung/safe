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

// showIter
case object CmdShowIter extends Command("showIter", "Periodically print iterations.") {
  override val help: String = s"""usage: $name on
       $name off"""
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case "on" :: Nil => c.showIter = true
      case "off" :: Nil => c.showIter = false
      case _ => printResult(help)
    }
    None
  }
}
