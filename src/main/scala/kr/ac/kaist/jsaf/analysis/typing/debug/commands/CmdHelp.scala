/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole

class CmdHelp extends Command {
  override val name = "help"

  override def run(c: DebugConsole, args: Array[String]): Unit = {
    val commands = c.command.keySet
    if (args.length == 0) {
      System.out.println("Command list:")
      commands.foreach(cmd => {
        if (!cmd.equals(""))
          System.out.println(cmd+"\t"+c.command(cmd).info)
      })
      System.out.println("For more information, see 'help <command>'.")
    } else {
      val str = args(0)
      c.command.get(str) match {
        case Some(cmd) => cmd.help()
        case None => System.out.println("'"+str+"' is not a command. See 'help'.")
      }
    }
  }
}
