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

package kr.ac.kaist.safe.proc

import java.io.FileNotFoundException

import kr.ac.kaist.safe.Config
import kr.ac.kaist.safe.compiler.Parser
import kr.ac.kaist.safe.nodes.Program

// Parse procedure struct.
case class Parse(
    parseConfig: ParseConfig = ParseConfig()
) extends Procedure(None, Some(parseConfig)) {
  override def apply(config: Config): Unit = parse(config)
  def parse(config: Config): Option[Program] = {
    config.fileNames match {
      case Nil => error("Need a file to parse.")
      case _ =>
        // TODO Delete try-catch.
        try {
          Some(Parser.fileToAST(config.fileNames))
        } catch {
          case (f: FileNotFoundException) => error(f + " not found")
          case (e: Exception) => error(e.getCause.toString)
        }
    }
  }
  private def error(msg: String): None.type = {
    Console.err.println(msg)
    None
  }
}

// Parse procedure helper.
object Parse extends ProcedureHelper {
  def create: Parse = Parse(ParseConfig())
}

// Config options for Parse procedure.
case class ParseConfig(
    var verbose: Boolean = false,
    var outFile: Option[String] = None
) extends ConfigOption {
  val prefix: String = "parse:"
  val optMap: Map[String, OptionKind] = Map(
    "verbose" -> BoolOption(() => verbose = true),
    "out" -> StrOption((s: String) => outFile = Some(s))
  )
}
