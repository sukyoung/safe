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

import java.io.{ BufferedWriter, FileWriter }

import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, StrOption }
import kr.ac.kaist.safe.compiler.Parser
import kr.ac.kaist.safe.nodes.Program
import kr.ac.kaist.safe.util.Useful

// Parse phase
case class Parse(
    parseConfig: ParseConfig = ParseConfig()
) extends Phase(None, Some(parseConfig)) {
  override def apply(config: Config): Unit = parse(config)
  def parse(config: Config): Option[Program] = {
    config.fileNames match {
      case Nil =>
        Console.err.println("Need a file to parse."); None
      case _ =>
        val program = Parser.fileToAST(config.fileNames)

        // Pretty print to file.
        parseConfig.outFile match {
          case Some(out) =>
            val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(out)
            writer.write(program.toString(0))
            writer.close
            fw.close
            println("Dumped parsed AST to " + out)
          case None =>
        }

        // Return program.
        Some(program)
    }
  }
}

// Parse phase helper.
object Parse extends PhaseHelper {
  def create: Parse = Parse(ParseConfig())
}

// Config options for the Parse phase.
case class ParseConfig(
    var outFile: Option[String] = None
) extends ConfigOption {
  val prefix: String = "parse:"
  val optMap: Map[String, OptionKind] = Map(
    "out" -> StrOption((s: String) => outFile = Some(s))
  )
}
