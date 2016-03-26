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
import kr.ac.kaist.safe.compiler.{ Hoister, Disambiguator, WithRewriter }
import kr.ac.kaist.safe.errors.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.nodes.Program
import kr.ac.kaist.safe.util.{ JSAstToConcrete, NodeUtil, Useful }

// ASTRewrite phase struct.
case class ASTRewrite(
    prev: Parse = Parse(),
    astRewriteConfig: ASTRewriteConfig = ASTRewriteConfig()
) extends Phase(Some(prev), Some(astRewriteConfig)) {
  override def apply(config: Config): Unit = rewrite(config)
  def rewrite(config: Config): Option[Program] = {
    prev.parse(config) match {
      case Some(pgm) => rewrite(config, pgm)
      case None => None
    }
  }
  def rewrite(config: Config, pgm: Program): Option[Program] = {
    // Rewrite AST.
    var program = (new Hoister(pgm).doit).asInstanceOf[Program]
    val disambiguator = new Disambiguator(program)
    program = (disambiguator.doit).asInstanceOf[Program]
    var errors: List[StaticError] = disambiguator.getErrors
    val withRewriter: WithRewriter = new WithRewriter(program, false)
    program = withRewriter.doit.asInstanceOf[Program]

    // Report errors.
    StaticErrors.reportErrors(NodeUtil.getFileName(program), errors)

    // Pretty print to file.
    astRewriteConfig.outFile match {
      case Some(out) =>
        // TODO delete try-catch.
        try {
          val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(out)
          writer.write((new JSAstToConcrete).doit(program))
          writer.close
          fw.close
          println("Dumped rewritten AST to " + out)
        } catch {
          case e: IOException =>
            throw new IOException("IOException " + e + "while writing to " + out)
        }
      case None =>
    }

    // Return program.
    Some(program)
  }
}

// ASTRewrite phase helper.
object ASTRewrite extends PhaseHelper {
  def create: ASTRewrite = ASTRewrite()
}

// Config options for ASTRewrite phase.
case class ASTRewriteConfig(
    var verbose: Boolean = false,
    var outFile: Option[String] = None
) extends ConfigOption {
  val prefix: String = "astRewrite:"
  val optMap: Map[String, OptionKind] = Map(
    "verbose" -> BoolOption(() => verbose = true),
    "out" -> StrOption((s: String) => outFile = Some(s))
  )
}
