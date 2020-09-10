/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.phase

import scala.util.{ Success, Try }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.ast_rewriter.{ BlockIdInstrumentor }
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.util._

// BlockIdInstrument phase
case object BlockIdInstrument extends PhaseObj[Program, BlockIdInstrumentConfig, Program] {
  val name: String = "blockIdInstrumentor"
  val help: String =
    "Instruments BlockId on AST."

  def apply(
    pgm: Program,
    safeConfig: SafeConfig,
    config: BlockIdInstrumentConfig
  ): Try[Program] = {
    val (program, excLog) = rewrite(pgm)

    // Report errors.
    if (excLog.hasError && !safeConfig.testMode && !safeConfig.silent) {
      println(program.relFileName + ":")
      println(excLog)
    }

    // Pretty print to file.
    config.outFile match {
      case Some(out) => {
        val ((fw, writer)) = Useful.fileNameToWriters(out)
        writer.write(program.toString(0))
        writer.close; fw.close
        println("Dumped rewritten AST to " + out)
      }
      case None => return Try(program)
    }

    Success(program)
  }

  def rewrite(pgm: Program): (Program, ExcLog) = {
    val inst = new BlockIdInstrumentor(pgm)
    var program = inst.result
    var excLog = inst.excLog
    (program, excLog)
  }

  def defaultConfig: BlockIdInstrumentConfig = BlockIdInstrumentConfig()
  val options: List[PhaseOption[BlockIdInstrumentConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during rewriting AST are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the rewritten AST will be written to the outfile.")
  )
}

// BlockIdInstrument phase config
case class BlockIdInstrumentConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None
) extends Config
