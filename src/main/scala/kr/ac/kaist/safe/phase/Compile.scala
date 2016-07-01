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

import java.io.{ BufferedWriter, FileWriter, IOException }
import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, StrOption }
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.compiler.Translator
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.util.{ NodeUtil, Useful }

// Compile phase struct.
case class Compile(
    prev: ASTRewrite = ASTRewrite(),
    compileConfig: CompileConfig = CompileConfig()
) extends Phase(Some(prev), Some(compileConfig)) {
  override def apply(config: Config): Unit = compile(config) recover {
    case ex => Console.err.print(ex.toString)
  }
  def compile(config: Config): Try[IRRoot] =
    prev.rewrite(config).flatMap(compile(config, _))
  def compile(config: Config, program: Program): Try[IRRoot] = {
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
    compileConfig.outFile match {
      case Some(out) => Useful.fileNameToWriters(out).map { pair =>
        {
          val ((fw, writer)) = pair
          writer.write(ir.toString(0))
          writer.close; fw.close
          println("Dumped IR to " + out)
          ir
        }
      }
      case None => Try(ir)
    }
  }
}

// Compile phase helper.
object Compile extends PhaseHelper {
  def create: Compile = Compile()
}

// Config options for Compile phase.
case class CompileConfig(
    var verbose: Boolean = false,
    var outFile: Option[String] = None
) extends ConfigOption {
  val prefix: String = "compile:"
  val options: List[(String, OptionKind)] = List(
    ("verbose", BoolOption(() => verbose = true)),
    ("out", StrOption((s: String) => outFile = Some(s)))
  )
}
