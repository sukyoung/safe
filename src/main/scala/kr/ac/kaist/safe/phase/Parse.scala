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

package kr.ac.kaist.safe.phase

import java.io.FileNotFoundException

import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, StrOption }
import kr.ac.kaist.safe.compiler.Parser
import kr.ac.kaist.safe.nodes.Program

// Parse phase struct.
case class Parse(
    parseConfig: ParseConfig = ParseConfig()
) extends Phase(None, Some(parseConfig)) {
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

// Parse phase helper.
object Parse extends PhaseHelper {
  def create: Parse = Parse(ParseConfig())
}

// Config options for Parse phase.
case class ParseConfig() extends ConfigOption {
  val prefix: String = "parse:"
  val optMap: Map[String, OptionKind] = Map()
}
