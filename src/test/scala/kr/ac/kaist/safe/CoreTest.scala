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
import kr.ac.kaist.safe.compiler.{ Compiler, Parser, Hoister, Disambiguator, WithRewriter, DefaultCFGBuilder }
import kr.ac.kaist.safe.errors.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.safe_util.{ AddressManager, JSAstToConcrete, JSIRUnparser }
import kr.ac.kaist.safe.nodes.{ Program, CFG }
import kr.ac.kaist.safe.useful.Useful
import org.scalatest.Tag

object ParseTest extends Tag("ParseTest")
object ASTRewriteTest extends Tag("ASTRewriteTest")
object CompileTest extends Tag("CompileTest")
object CFGTest extends Tag("CFGTest")

class CoreTest extends FlatSpec {
  val SEP = File.separator
  val jsDir = Config.basedir + SEP + "tests/js/success"
  val resDir = Config.basedir + SEP + "tests/result/success"
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }
  def readFile(filename: String): String = {
    assert(new File(filename).exists)
    Source.fromFile(filename).getLines.mkString("\n")
  }

  def parseTest(jsName: String): Unit = {
  }

  def astRewriteTest(jsName: String, testName: String): Unit = {
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

    val result = readFile(testName)
    // val ans = Source.fromFile(testName).getLines.mkString("\n")
    //   .replace("${DISAMB_TESTS_DIR}", dir)

    StaticErrors.getReportErrors(jsName, errors) match {
      case Some(dump) => assert(result == dump)
      case None => assert(result == (new JSAstToConcrete).doit(program))
    }
  }

  def compileTest(jsName: String, testName: String): Unit = {
    val config = Config(List("compile", jsName))
    val (ir, rc, errors) = Compiler.compile(config)
    val dump: String = new JSIRUnparser(ir).doit
    val result = readFile(testName)

    assert(errors.isEmpty)
    assert(result == dump)
  }

  def cfgTest(jsName: String, testName: String): Unit = {
    val config = Config(List("cfg", jsName))
    val addrManager: AddressManager = config.addrManager

    val (ir, rc, _) = Compiler.compile(config)
    val (cfg: CFG, errors: List[StaticError]) = DefaultCFGBuilder.build(ir, config)
    val dump: String = cfg.dump
    val result = readFile(testName)

    assert(errors.isEmpty)
    assert(result == dump)
  }

  // Permute filenames for randomness
  for (filename <- Useful.shuffle(new File(jsDir).list(jsFilter))) {
    val name = filename.substring(0, filename.length - 3)
    val jsName = jsDir + SEP + filename
    registerTest("[Parse] " + filename, ParseTest) { parseTest(jsName) }

    val astName = resDir + "/astRewrite/" + name + ".test"
    registerTest("[ASTRewrite] " + filename, ASTRewriteTest) { astRewriteTest(jsName, astName) }

    val compileName = resDir + "/compile/" + name + ".test"
    registerTest("[Compile]" + filename, CompileTest) { compileTest(jsName, compileName) }

    val cfgName = resDir + "/cfg/" + name + ".test"
    registerTest("[CFG]" + filename, CFGTest) { cfgTest(jsName, cfgName) }
  }
}
