/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole
import scala.util.matching.Regex
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.cfg.{LBlock, Node}

class CmdMove extends Command {
  override val name = "move"

  override val info: String = "Change a current position."

  override def help(): Unit = {
    System.out.println("usage: move {Node}")
    System.out.println("example: move entry0")
    System.out.println("         move block23")
    System.out.println("         move exitexc2")
  }

  override def run(c: DebugConsole, args: Array[String]): Unit = {
    if (args.length > 0) {
      // parse first argument(cp)
      // TODO need to support a syntax for control point.
      val arg0 = args(0).toLowerCase
      parseNode(c, arg0) match {
        case Some(node) => {
          c.getTable.get(node) match {
            case Some(cs) => {
              val contexts = cs.keySet.toArray
              if (contexts.length == 1) {
                c.current = (node, contexts.head)
              } else {
                System.out.println("* Contexts")
                (0 to contexts.length-1).foreach(i => System.out.println("["+i+"]: "+contexts(i).toString))
                val line = c.reader.readLine("[0 to "+(contexts.length-1)+"]? ")
                val i = line.toInt
                c.current = (node, contexts(i))
              }
            }
            case None => {
              System.out.println(node+" doesn't have a state")
            }
          }
        }
        case None => {
          System.out.println("Cannot parse: "+arg0)
        }
      }
    }
  }
}
