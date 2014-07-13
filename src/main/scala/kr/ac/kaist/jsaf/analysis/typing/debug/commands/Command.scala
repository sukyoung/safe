/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug.commands

import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole
import kr.ac.kaist.jsaf.analysis.cfg._
import scala.util.matching.Regex

abstract class Command {
  val name: String

  val info: String = ""
  def help(): Unit = {}

  def run(c: DebugConsole, args: Array[String]): Unit

  def parseNode(c: DebugConsole, node: String): Option[Node] = {
    val pattern = new Regex("""(entry|exit|exitexc|block)([0-9]+)""", "block", "name")

    try {
      val pattern(block, name) = node
      val num = name.toInt
      block match {
        case "entry" => Some((num, LEntry))
        case "exit" => Some((num, LExit))
        case "exitexc" => Some((num, LExitExc))
        case "block" => {
          val label = LBlock(num)
          c.getCFG.getNodes.find(p => p._2 == label)
        }
      }
    } catch {
      case e: MatchError => None
      case e: NumberFormatException => None
    }
  }
}
