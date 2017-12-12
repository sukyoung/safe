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

// run
case object CmdRun extends Command("run", "Run until meet some break point.") {
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case Nil => Some(TargetIter(-1))
      case _ => printResult(help); None
    }
  }
}
