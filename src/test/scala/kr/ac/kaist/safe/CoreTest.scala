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
import java.io.{ File, FilenameFilter }

import kr.ac.kaist.safe.analyzer.domain.State
import kr.ac.kaist.safe.cfg_builder.AddressManager

import scala.io.Source
import scala.util.{ Failure, Success, Try }
import kr.ac.kaist.safe.nodes.{ CFG, IRRoot, Program }
import kr.ac.kaist.safe.config.{ ArgParse, Config }
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.phase.{ Analyze, CFGBuild }

object ParseTest extends Tag("ParseTest")
object ASTRewriteTest extends Tag("ASTRewriteTest")
object CompileTest extends Tag("CompileTest")
object CFGBuildTest extends Tag("CFGBuildTest")
object AnalyzeTest extends Tag("AnalyzeTest")

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
          case Success((pgm, _)) =>
            val pretty = pgm.toString(0)
            Parser.stringToAST(pretty) match {
              case Failure(_) => assert(false)
              case Success((p, _)) =>
                assert(normalized(p.toString(0)) == normalized(pretty))
            }
        }
    }
  }

  def astRewriteTest(ast: Try[Program], testName: String): Unit = {
    ast match {
      case Failure(_) => assert(false)
      case Success(program) =>
        assert(readFile(testName) == normalized(program.toString(0)))
    }
  }

  def compileTest(ir: Try[IRRoot], testName: String): Unit = {
    ir match {
      case Failure(_) => assert(false)
      case Success(ir) =>
        assert(readFile(testName) == normalized(ir.toString(0)))
    }
  }

  def cfgBuildTest(cfg: Try[CFG], testName: String): Unit = {
    cfg match {
      case Failure(_) => assert(false)
      case Success(cfg) =>
        assert(readFile(testName) == normalized(cfg.toString(0)))
    }
  }

  val resultPrefix = "__result"
  val expectPrefix = "__expect"
  def analyzeTest(analysis: Try[(State, State)], addrManager: AddressManager): Unit = {
    analysis match {
      case Failure(_) => assert(false)
      case Success(states) =>
        val (normalSt, excSt) = states
        assert(!normalSt.heap.isBottom)
        normalSt.heap(addrManager.PredefLoc.GLOBAL) match {
          case None => assert(false)
          case Some(globalObj) if globalObj.isBottom => assert(false)
          case Some(globalObj) =>
            val resultKeySet = globalObj.collectKeysStartWith(resultPrefix)
            val expectKeySet = globalObj.collectKeysStartWith(expectPrefix)
            assert(resultKeySet.size == expectKeySet.size)
            for (resultKey <- resultKeySet) {
              val num = resultKey.substring(resultPrefix.length)
              val expectKey = expectPrefix + num
              assert(expectKeySet contains expectKey)
              (globalObj(resultKey), globalObj(expectKey)) match {
                case (None, _) | (_, None) => assert(false)
                case (Some(resultVal), Some(expectVal)) => assert(expectVal <= resultVal)
              }
            }
        }
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

    val ast = pgm.flatMap(astRewrite.rewrite(config, _))
    val astName = resDir + "/astRewrite/" + name + ".test"
    registerTest("[ASTRewrite] " + filename, ASTRewriteTest) { astRewriteTest(ast, astName) }

    val ir = ast.flatMap(compile.compile(config, _))
    val compileName = resDir + "/compile/" + name + ".test"
    registerTest("[Compile]" + filename, CompileTest) { compileTest(ir, compileName) }

    val cfg = ir.flatMap(cfgBuild.cfgBuild(config, _))
    val cfgName = resDir + "/cfg/" + name + ".test"
    registerTest("[CFG]" + filename, CFGBuildTest) { cfgBuildTest(cfg, cfgName) }
  }

  val analyzerTestDir = Config.BASE_DIR + SEP + "tests/js/semantics"
  for (filename <- scala.util.Random.shuffle(new File(analyzerTestDir).list(jsFilter).toSeq)) {
    val jsName = analyzerTestDir + SEP + filename

    val (config, phase) = ArgParse(List("analyze", "-analyze:testMode", jsName)).get
    val analyzer = phase.asInstanceOf[Analyze]
    val analysis = analyzer.analyze(config)

    //TODO    registerTest("[Analyze]" + filename, AnalyzeTest) { analyzeTest(analysis, config.addrManager) }
  }
}
