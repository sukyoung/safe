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
import kr.ac.kaist.safe.ast_rewriter.Test262Rewriter
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.util._

// TestRewrite phase
case object TestRewrite extends PhaseObj[Program, TestRewriteConfig, Program] {
  val name: String = "testRewriter"
  val help: String =
    "Rewrites AST in Test262 JavaScript source files for SAFE testing"

  def apply(
    pgm: Program,
    safeConfig: SafeConfig,
    config: TestRewriteConfig
  ): Try[Program] = {
    val rewriter = new Test262Rewriter(pgm)
    var program = rewriter.result

    // Pretty print to file.
    config.outFile match {
      case Some(out) => {
        val ((fw, writer)) = Useful.fileNameToWriters(out)
        writer.write(program.toString(0))
        writer.close; fw.close
        println("Dumped rewritten AST to " + out)
      }
      case None => Try(program)
    }

    Success(program)
  }

  def defaultConfig: TestRewriteConfig = TestRewriteConfig()
  val options: List[PhaseOption[TestRewriteConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during rewriting AST are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the rewritten AST will be written to the outfile.")
  )
}

// TestRewrite phase config
case class TestRewriteConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None
) extends Config
