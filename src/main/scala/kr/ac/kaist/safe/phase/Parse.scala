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
  override def apply(config: Config): Unit = parse(config) recover {
    case ex => Console.err.print(ex.toString)
  }
  def parse(config: Config): Try[Program] = {
    config.fileNames match {
      case Nil => Failure(NoFileError("parse"))
      case _ => Parser.fileToAST(config.fileNames).flatMap(program => {
        // Pretty print to file.
        parseConfig.outFile match {
          case Some(out) => Useful.fileNameToWriters(out).map { pair =>
            {
              val (fw, writer) = pair
              writer.write(program.toString(0))
              writer.close; fw.close
              println("Dumped parsed AST to " + out)
              program
            }
          }
          case None => Try(program)
        }
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
