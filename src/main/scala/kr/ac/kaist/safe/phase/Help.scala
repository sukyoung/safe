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

package kr.ac.kaist.safe.phase

import kr.ac.kaist.safe.config.{ Command, Config }

// Help phase
case class Help() extends Phase(None, None) {
  override def apply(config: Config): Unit = Help.printHelpMessage
}

// Help phase helper.
object Help extends PhaseHelper {
  def create: Help = Help()

  // Print usage message.
  def printUsageMessage: Unit = {
    val s: StringBuilder = new StringBuilder
    s.append("Usage:").append(Config.LINE_SEP)
    for (cmd <- Command.cmdMap.keys.toList.sorted) {
      s.append(s" $cmd").append(Command.cmdMap(cmd).usage).append(Config.LINE_SEP)
    }
    Console.err.print(s.toString)
  }

  // Print help message.
  def printHelpMessage: Unit = {
    val s: StringBuilder = new StringBuilder
    s.append("Invoked as script: safe args").append(Config.LINE_SEP)
    s.append("Invoked by java: java ... kr.ac.kaist.safe.Safe args")
    s.append(Config.LINE_SEP).append(Config.LINE_SEP)
    for (cmd <- Command.cmdMap.keys.toList.sorted) {
      val command = Command.cmdMap(cmd)
      s.append(s"safe $cmd").append(command.usage).append(Config.LINE_SEP)
      s.append(command.help).append(Config.LINE_SEP)
    }
    Console.err.print(s.toString)
  }
}
