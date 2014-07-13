/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole

class CmdBreak extends Command {
  override val name = "break"

  override def run(c: DebugConsole, args: Array[String]): Unit = {
    // adds a break point on code

    //      val arg = arguments.head
    //      val p1 = new Regex("""([^:]+):(\\d+)""", "filename", "line")
    //
    //      try {
    //        val p1(filename, line) = arg
    //        System.out.println(line+" @"+filename)
    //      } catch {
    //        case e => e.printStackTrace()
    //      }
  }
}
