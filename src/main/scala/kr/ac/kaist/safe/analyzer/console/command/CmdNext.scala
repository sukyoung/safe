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

// next
case object CmdNext extends Command("next", "Jump to the next iteration. (same as \"\")") {
  def run(c: Interactive, args: List[String]): Option[Target] = args match {
    case Nil => Some(TargetIter(c.getIter + 1))
    case _ => printResult(help); None
  }
}
