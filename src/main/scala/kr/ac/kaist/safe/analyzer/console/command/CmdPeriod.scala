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

// period
case object CmdPeriod extends Command("period", "Periodically break based on iterations.") {
  override val help: String = s"usage: $name {#period}"
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case Nil =>
        val period = c.period.fold("<none>")(_.toString)
        printResult(s"period: $period")
      case iter :: Nil if iter.forall(_.isDigit) =>
        c.period = Some(iter.toInt)
      case _ => printResult(help)
    }
    None
  }
}
