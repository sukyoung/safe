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

import scala.util.{ Try, Success, Failure }
import java.io.{ BufferedWriter, FileWriter }
import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, StrOption }
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.nodes.Program
import kr.ac.kaist.safe.util.Useful
import kr.ac.kaist.safe.errors.error._

// Parse phase
case class Parse(
    parseConfig: ParseConfig = ParseConfig()
) extends Phase(None, Some(parseConfig)) {
  override def apply(config: Config): Unit = parse(config)
  def parse(config: Config): Try[Program] = {
    config.fileNames match {
      case Nil =>
        Failure(NoFileError("parse"))
      case _ => Parser.fileToAST(config.fileNames).map(program => {
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
        program
      })
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
