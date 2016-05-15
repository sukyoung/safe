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
import java.io.{ File, FilenameFilter }
import scala.io.Source
import scala.util.{ Try, Success, Failure }

import kr.ac.kaist.safe.util.NodeUtil
import kr.ac.kaist.safe.nodes.{ Program, IRRoot }
import kr.ac.kaist.safe.cfg_builder.CFG
import kr.ac.kaist.safe.config.{ Config, ArgParse }
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.phase.CFGBuild

object ParseTest extends Tag("ParseTest")
object ASTRewriteTest extends Tag("ASTRewriteTest")
object CompileTest extends Tag("CompileTest")
object CFGBuildTest extends Tag("CFGBuildTest")

class CoreTest extends FlatSpec {
  val SEP = File.separator
  val jsDir = Config.BASE_DIR + SEP + "tests/js/success"
  val resDir = Config.BASE_DIR + SEP + "tests/result/success"
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }

  def normalized(s: String): String = s.replaceAll("\\s+", "").replaceAll("\\n+", "")

  def readFile(filename: String): String = {
    assert(new File(filename).exists)
    normalized(Source.fromFile(filename).getLines.mkString(Config.LINE_SEP))
  }

  private def parseTest(pgm: Try[Program]): Unit = {
    pgm match {
      case Failure(_) => assert(false)
      case Success(program) =>
        Parser.stringToAST(program.toString(0)) match {
          case Failure(_) => assert(false)
          case Success(pgm) =>
            val pretty = pgm.toString(0)
            Parser.stringToAST(pretty) match {
              case Failure(_) => assert(false)
              case Success(p) =>
                assert(normalized(p.toString(0)) == normalized(pretty))
            }
        }
    }
  }

  def astRewriteTest(astOpt: Option[Program], testName: String): Unit = {
    astOpt match {
      case None => assert(false)
      case Some(program) =>
        val result = readFile(testName)
        assert(result == normalized(program.toString(0)))
    }
  }

  def compileTest(irOpt: Option[IRRoot], testName: String): Unit = {
    irOpt match {
      case None => assert(false)
      case Some(ir) =>
        val result = readFile(testName)
        val dump = normalized(ir.toString(0))
        assert(result == dump)
    }
  }

  def cfgBuildTest(cfgOpt: Option[CFG], testName: String): Unit = {
    cfgOpt match {
      case None => assert(false)
      case Some(cfg) =>
        val result = readFile(testName)
        val dump = normalized(cfg.dump)
        assert(result == dump)
    }
  }

  // Permute filenames for randomness
  for (filename <- scala.util.Random.shuffle(new File(jsDir).list(jsFilter).toSeq)) {
    val name = filename.substring(0, filename.length - 3)
    val jsName = jsDir + SEP + filename

    val (config, phase) = ArgParse(List("cfgBuild", jsName)).get
    val cfgBuild = phase.asInstanceOf[CFGBuild]
    val compile = cfgBuild.prev
    val astRewrite = compile.prev
    val parse = astRewrite.prev

    val pgm = parse.parse(config)
    registerTest("[Parse] " + filename, ParseTest) { parseTest(pgm) }

    val astOpt = pgm match {
      case Success(program) => astRewrite.rewrite(config, program)
      case Failure(_) => None
    }
    val astName = resDir + "/astRewrite/" + name + ".test"
    registerTest("[ASTRewrite] " + filename, ASTRewriteTest) { astRewriteTest(astOpt, astName) }

    val irOpt = astOpt match {
      case Some(program) => compile.compile(config, program)
      case None => None
    }
    val compileName = resDir + "/compile/" + name + ".test"
    registerTest("[Compile]" + filename, CompileTest) { compileTest(irOpt, compileName) }

    val cfgOpt = irOpt match {
      case Some(ir) => cfgBuild.cfgBuild(config, ir)
      case None => None
    }
    val cfgName = resDir + "/cfg/" + name + ".test"
    registerTest("[CFG]" + filename, CFGBuildTest) { cfgBuildTest(cfgOpt, cfgName) }
  }
}
