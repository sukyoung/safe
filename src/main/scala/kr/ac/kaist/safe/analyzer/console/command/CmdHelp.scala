/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
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
  def help: Unit = println("usage: " + name + " (command)")
  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case Nil => {
        println("Command list:")
        Console.commands.foreach {
          case (name, cmd) =>
            println("- " + name + "\t" + cmd.info)
        }
        println("For more information, see '" + name + " <command>'.")
      }
      case str :: Nil => Console.commands.get(str) match {
        case Some(cmd) => cmd.help
        case None =>
          println("* '" + str + "' is not a command. See '" + name + "'.")
      }
      case _ => help
    }
    None
  }
}
