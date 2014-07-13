/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole
import scala.util.matching.Regex

class CmdJump extends Command {
  override val name = "jump"
  override val info: String = "Continue to analyze until the given iteration."

  override def help(): Unit = {
    System.out.println("usage: jump {iteration#}")
  }

  override def run(c: DebugConsole, args: Array[String]): Unit = {
    try {
      val arg0 = args(0)

      parseNode(c, arg0) match {
        case Some(n) => {
          c.targetNode = Some(n)
          c.target = Int.MaxValue
        }
        case None => {
          val cnt = arg0.toInt
          if (cnt <= c.iter) System.out.println("* Target iteration must be bigger than "+c.iter)
          else c.target = cnt
        }
      }
    } catch {
      case e: ArrayIndexOutOfBoundsException => help()
      case e: NumberFormatException => help()
    }
  }
}
