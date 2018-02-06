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

import java.io.OutputStream

import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.LINE_SEP

abstract class Command(
    val name: String,
    val info: String = ""
) {
  private val sb: StringBuilder = new StringBuilder()

  def run(c: Interactive, args: List[String]): Option[Target]
  val help: String = "usage: " + name

  protected def grep(key: String, str: String): String = {
    str.split(LINE_SEP)
      .filter(_.contains(key))
      .mkString(LINE_SEP)
  }

  protected def printResult(s: String = ""): Unit = {
    sb.append(s + LINE_SEP)
  }
  protected def flushTo(outputStream: OutputStream): Unit = {
    outputStream.write(sb.toString.getBytes)
    outputStream.flush()
    sb.clear()
  }
  def result(): String = {
    val s = sb.toString
    sb.clear()
    s
  }
}

object Command {
  val commands: List[Command] = List(
    CmdHelp,
    CmdNext,
    CmdJump,
    CmdPrint,
    CmdPrintResult,
    CmdRunInsts,
    CmdMove,
    CmdHome,
    CmdRun,
    CmdBreak,
    CmdBreakList,
    CmdBreakRemove,
    CmdFindBot,
    // TODO CmdDump,
    CmdRestart
  )
  val cmdMap: Map[String, Command] = commands.foldLeft(
    Map[String, Command]()
  ) { case (map, cmd) => map + (cmd.name -> cmd) }
}
