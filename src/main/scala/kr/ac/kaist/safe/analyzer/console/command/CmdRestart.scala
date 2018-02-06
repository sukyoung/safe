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

// restart
case object CmdRestart extends Command("restart", "Restart the analysis.") {
  def run(c: Interactive, args: List[String]): Option[Target] = args match {
    case Nil =>
      c.sem.init
      Some(TargetStart)
    case _ =>
      printResult(help)
      None
  }
}
