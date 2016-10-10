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

package kr.ac.kaist.safe

import scala.collection.immutable.HashMap
import scala.util.{ Try, Failure }
import kr.ac.kaist.safe.errors.SafeException
import kr.ac.kaist.safe.errors.error.{ NoCmdError, NoInputError }
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util._

object Safe {
  ////////////////////////////////////////////////////////////////////////////////
  // Main entry point
  ////////////////////////////////////////////////////////////////////////////////
  def main(tokens: Array[String]): Unit = {
    (tokens.toList match {
      case str :: args => cmdMap.get(str) match {
        case Some(cmd) => cmd(args, false)
        case None => Failure(NoCmdError(str))
      }
      case Nil => Failure(NoInputError)
    }) recover {
      // SafeException: print the usage message.
      case ex: SafeException =>
        Console.err.println(ex.getMessage)
        println(usage)
      // Unexpected: print the stack trace.
      case ex =>
        Console.err.println("* Unexpected error occurred.")
        Console.err.println(ex.toString)
        Console.err.println(ex.getStackTrace.mkString(LINE_SEP))
    }
  }

  def apply[Result](
    runner: SafeConfig => Try[Result],
    config: SafeConfig
  ): Try[Result] = {
    // set the start time.
    val startTime = System.currentTimeMillis

    // execute the command.
    val result: Try[Result] = runner(config)

    // print the time spent if the time option is set.
    if (!config.silent) {
      val duration = System.currentTimeMillis - startTime
      val name = config.command.name
      println(s"The command '$name' took $duration ms.")
    }

    // return result
    result
  }

  // commands
  val commands: List[Command] = List(
    CmdParse,
    CmdASTRewrite,
    CmdCompile,
    CmdCFGBuild,
    CmdAnalyze,
    CmdHelp
  )
  val cmdMap = commands.foldLeft[Map[String, Command]](HashMap()) {
    case (map, cmd) => map + (cmd.name -> cmd)
  }

  // phases
  var phases: List[Phase] = List(
    Parse,
    ASTRewrite,
    Compile,
    CFGBuild,
    Analyze,
    Help
  )

  // global options
  val options: List[PhaseOption[SafeConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "all messages are muted."),
    ("testMode", BoolOption(c => c.testMode = true),
      "switch on the test mode.")
  )

  // print usage message.
  val usage: String = {
    val s: StringBuilder = new StringBuilder
    s.append("Usage:").append(LINE_SEP)
      .append("  safe {command} [-{option}]* [-{phase}:{option}[={input}]]* {filename}+").append(LINE_SEP)
      .append("  example: safe analyze -astRewriter:silent -cfgBuilder:out=out test.js").append(LINE_SEP)
      .append(LINE_SEP)
      .append("  command list:").append(LINE_SEP)
    commands foreach (cmd => s.append("    %-15s".format(cmd.name)).append(cmd).append(LINE_SEP))
    s.append(LINE_SEP)
      .append("  phase list:").append(LINE_SEP)
    phases foreach (phase => {
      s.append("    %-15s".format(phase.name))
      val names = phase.getOptShapes
      s.append(names.slice(0, 3).mkString(", "))
        .append(if (names.size > 3) ", ..." else "")
      s.append(LINE_SEP)
    })
    s.toString
  }

  // print help message.
  val help: String = {
    val s: StringBuilder = new StringBuilder
    s.append("Invoked as script: safe args").append(LINE_SEP)
      .append("Invoked by java: java ... kr.ac.kaist.safe.Safe args").append(LINE_SEP)
      .append(LINE_SEP)
      .append("command list:").append(LINE_SEP)
    commands foreach (cmd => s.append("  %-15s".format(cmd.name)).append(cmd).append(LINE_SEP))
    s.append(LINE_SEP)
      .append("phase list:").append(LINE_SEP)
    phases foreach (phase => {
      s.append("  %-15s".format(phase.name))
      Useful.indentation(s, phase.help, 17)
      s.append(LINE_SEP)
        .append(LINE_SEP)
      phase.getOptDescs foreach {
        case (name, desc) =>
          s.append(s"    If $name is given, $desc").append(LINE_SEP)
      }
      s.append(LINE_SEP)
    })
    s.toString
  }
}

case class SafeConfig(
  var command: Command,
  var fileNames: List[String] = Nil,
  var silent: Boolean = false,
  var testMode: Boolean = false
) extends Config
