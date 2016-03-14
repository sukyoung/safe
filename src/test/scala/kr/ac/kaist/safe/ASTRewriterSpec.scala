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

package kr.ac.kaist.safe

import org.scalatest._
import org.scalatest.Assertions._
import scala.io.Source
import java.io.File
import java.io.FilenameFilter
import kr.ac.kaist.safe.compiler.{ Compiler, Parser, Hoister, Disambiguator, WithRewriter }
import kr.ac.kaist.safe.exceptions.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.safe_util.{ AddressManager, JSAstToConcrete }
import kr.ac.kaist.safe.nodes.Program
import kr.ac.kaist.safe.useful.Useful

class ASTRewriterSpec extends FlatSpec {
  val SEP = File.separator
  val dir = Config.basedir + SEP + "tests/astrewriter_tests"
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }

  def assertSameResult(filename: String): Unit = {
    val jsName: String = filename + ".js"
    val testName: String = filename + ".test"

    val config = Config(List("astRewrite", jsName))
    val addrManager: AddressManager = config.addrManager

    var program: Program = Parser.fileToAST(config.FileNames)
    program = (new Hoister(program).doit).asInstanceOf[Program]
    val disambiguator = new Disambiguator(program, config)
    program = (disambiguator.doit).asInstanceOf[Program]
    var errors: List[StaticError] = disambiguator.getErrors
    val withRewriter: WithRewriter = new WithRewriter(program, false)
    program = withRewriter.doit.asInstanceOf[Program]
    errors :::= withRewriter.getErrors

    val ans = Source.fromFile(testName).getLines.mkString("\n")
      .replace("${DISAMB_TESTS_DIR}", dir)

    assert(new File(testName).exists)
    StaticErrors.getReportErrors(jsName, errors) match {
      case Some(dump) => assert(ans == dump)
      case None => assert(ans == (new JSAstToConcrete).doit(program))
    }
  }

  // Permute filenames for randomness
  for (filename <- Useful.shuffle(new File(dir).list(jsFilter))) {
    val fname = dir + SEP + filename
    val file = new File(fname)
    val name = fname.substring(0, fname.length - 3)
    registerTest(file.getName) {
      assertSameResult(name)
    }
  }
}
