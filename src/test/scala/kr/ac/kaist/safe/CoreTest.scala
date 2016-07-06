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

import kr.ac.kaist.safe.analyzer.CallContext
import kr.ac.kaist.safe.analyzer.domain.State

import scala.io.Source
import scala.util.{ Failure, Success, Try }
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.phase._

object ParseTest extends Tag("ParseTest")
object ASTRewriteTest extends Tag("ASTRewriteTest")
object CompileTest extends Tag("CompileTest")
object CFGBuildTest extends Tag("CFGBuildTest")
object AnalyzeTest extends Tag("AnalyzeTest")

class CoreTest extends FlatSpec {
  val SEP = File.separator
  val jsDir = BASE_DIR + SEP + "tests/js/success"
  val resDir = BASE_DIR + SEP + "tests/result/success"
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }

  def normalized(s: String): String = s.replaceAll("\\s+", "").replaceAll("\\n+", "")

  def readFile(filename: String): String = {
    assert(new File(filename).exists)
    normalized(Source.fromFile(filename).getLines.mkString(LINE_SEP))
  }

  private def parseTest(pgm: Try[Program]): Unit = {
    pgm match {
      case Failure(e) =>
        println(e.toString); println(e.getStackTrace.mkString("\n")); assert(false)
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
  def analyzeTest(analysis: Try[(CFG, CallContext)]): Unit = {
    analysis match {
      case Failure(_) => assert(false)
      case Success((cfg, globalCallCtx)) =>
        val normalSt = cfg.globalFunc.exit.getState(globalCallCtx)
        val excSt = cfg.globalFunc.exitExc.getState(globalCallCtx)
        assert(!normalSt.heap.isBottom)
        normalSt.heap(PredefLoc.GLOBAL) match {
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

    val config = SafeConfig(CmdBase, List(jsName))

    val pgm = Parse((), config)
    registerTest("[Parse] " + filename, ParseTest) { parseTest(pgm) }

    val ast = pgm.flatMap(ASTRewrite(_, config))
    val astName = resDir + "/astRewrite/" + name + ".test"
    registerTest("[ASTRewrite] " + filename, ASTRewriteTest) { astRewriteTest(ast, astName) }

    val ir = ast.flatMap(Compile(_, config))
    val compileName = resDir + "/compile/" + name + ".test"
    registerTest("[Compile]" + filename, CompileTest) { compileTest(ir, compileName) }

    val cfg = ir.flatMap(CFGBuild(_, config))
    val cfgName = resDir + "/cfg/" + name + ".test"
    registerTest("[CFG]" + filename, CFGBuildTest) { cfgBuildTest(cfg, cfgName) }
  }

  val analyzerTestDir = BASE_DIR + SEP + "tests/js/semantics"
  for (filename <- scala.util.Random.shuffle(new File(analyzerTestDir).list(jsFilter).toSeq)) {
    val jsName = analyzerTestDir + SEP + filename

    val analysis = CmdAnalyze(List("-analyzer:testMode", jsName))
    registerTest("[Analyze]" + filename, AnalyzeTest) { analyzeTest(analysis) }
  }
}
