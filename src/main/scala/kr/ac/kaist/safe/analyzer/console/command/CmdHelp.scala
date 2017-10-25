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

// help
case object CmdHelp extends Command("help") {
  override val help: String = "usage: " + name + " (command)"
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        printResult("Command list:")
        Command.commands.foreach(cmd =>
          printResult("- %-15s%s".format(cmd.name, cmd.info)))
        printResult("For more information, see '" + name + " <command>'.")
      }
      case str :: Nil => Command.cmdMap.get(str) match {
        case Some(cmd) => printResult(cmd.help)
        case None =>
          printResult("* '" + str + "' is not a command. See '" + name + "'.")
      }
      case _ => printResult(help)
    }
    None
  }
}
