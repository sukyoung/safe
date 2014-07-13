/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.{DebugConsole, DebugConsoleDSparse}
import kr.ac.kaist.jsaf.analysis.typing.domain.DomainPrinter

class CmdDU extends Command {
  override val name = "du"
  override val info: String = "Print out the def/use information for a specific node."

  override def help(): Unit = {
    System.out.println("usage: du {node}")
  }

  override def run(_c: DebugConsole, args: Array[String]): Unit = {
    val c: DebugConsoleDSparse = _c.asInstanceOf[DebugConsoleDSparse]

    // TODO need to support a syntax for control point.
    val arg0 =
      if (args.length > 0) args(0).toLowerCase
      else ""

    val n = parseNode(c, arg0) match {
      case Some(node) => node
      case None => c.current._1
    }

    System.out.println("target: "+n)
    val map = c.getEnv.getIntraDefuse(n._1)
    map.get(n) match {
      case Some((d,u)) => {
        System.out.println("defset: "+DomainPrinter.printLocSet(d))
        System.out.println("useset: "+DomainPrinter.printLocSet(u))
      }
      case None => System.out.println("cannot find def/use set.")
    }
  }
}
