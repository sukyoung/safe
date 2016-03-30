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

import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, StrOption }
import kr.ac.kaist.safe.errors.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.compiler.Translator
import kr.ac.kaist.safe.nodes.{ Program, IRRoot }
import kr.ac.kaist.safe.util.{ NodeUtil, Useful }

// Compile phase struct.
case class Compile(
    prev: ASTRewrite = ASTRewrite(),
    compileConfig: CompileConfig = CompileConfig()
) extends Phase(Some(prev), Some(compileConfig)) {
  override def apply(config: Config): Unit = compile(config)
  def compile(config: Config): Option[IRRoot] = {
    prev.rewrite(config) match {
      case Some(pgm) => compile(config, pgm)
      case None => None
    }
  }
  def compile(config: Config, program: Program): Option[IRRoot] = {
    // Translate AST -> IR.
    val translator = new Translator(program)
    val ir = translator.doit.asInstanceOf[IRRoot]
    val errs = translator.getErrors

    // Report errors.
    StaticErrors.reportErrors(NodeUtil.getFileName(program), errs)

    // Pretty print to file.
    val ircode = ir.toString(0)
    compileConfig.outFile match {
      case Some(out) =>
        val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(out)
        writer.write(ircode)
        writer.close
        fw.close
        println("Dumped IR to " + out)
      case None =>
    }

    // Return IR.
    Some(ir)
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
  val optMap: Map[String, OptionKind] = Map(
    "verbose" -> BoolOption(() => verbose = true),
    "out" -> StrOption((s: String) => outFile = Some(s))
  )
}
