/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole

class CmdNext extends Command {
  override val name = ""

  override def run(c: DebugConsole, args: Array[String]): Unit = {
    c.target = c.iter + 1
  }
}
