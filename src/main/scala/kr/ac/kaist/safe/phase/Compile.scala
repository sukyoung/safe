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

import scala.util.{ Try, Success }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.compiler.Translator
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.util._

// Compile phase
case object Compile extends PhaseObj[Program, CompileConfig, IRRoot] {
  val name: String = "compiler"
  val help: String = "Translates JavaScript source files to IR."

  def apply(
    program: Program,
    safeConfig: SafeConfig,
    config: CompileConfig
  ): Try[IRRoot] = {
    // Translate AST -> IR.
    val translator = new Translator(program)
    val ir = translator.result
    val excLog = translator.excLog

    // Report errors.
    if (excLog.hasError) {
      println(ir.relFileName + ":")
      println(excLog)
    }

    // Pretty print to file.
    config.outFile match {
      case Some(out) => {
        val ((fw, writer)) = Useful.fileNameToWriters(out)
        writer.write(ir.toString(0))
        writer.close; fw.close
        println("Dumped IR to " + out)
      }
      case None =>
    }
    Success(ir)
  }

  def defaultConfig: CompileConfig = CompileConfig()
  val options: List[PhaseOption[CompileConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during compilation are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the resulting IR will be written to the outfile.")
  )
}

// Compile phase config
case class CompileConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None
) extends Config
