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
import java.io._

import kr.ac.kaist.safe.analyzer.CallContext
import kr.ac.kaist.safe.analyzer.domain.State

import scala.io.Source
import scala.util.{ Failure, Success, Try }
import scala.util.Random.shuffle
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
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
object Test262Test extends Tag("Test262Test")

class CoreTest extends FlatSpec with BeforeAndAfterAll {
  val SEP = File.separator
  val testDir = BASE_DIR + SEP + "tests" + SEP
  val jsDir = testDir + "cfg" + SEP + "js" + SEP + "success" + SEP
  val resDir = testDir + "cfg" + SEP + "result" + SEP + "success" + SEP
  def walkTree(file: File): Iterable[File] = {
    val children = new Iterable[File] {
      def iterator: Iterator[File] = if (file.isDirectory) file.listFiles.iterator else Iterator.empty
    }
    Seq(file) ++: children.flatMap(walkTree(_))
  }
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }

  def normalized(s: String): String = s.replaceAll("\\s+", "").replaceAll("\\n+", "")

  def readFile(filename: String): String = {
    assert(new File(filename).exists)
    normalized(Source.fromFile(filename).getLines.mkString(LINE_SEP))
  }

  def getSafeConfig(filename: String): SafeConfig =
    SafeConfig(CmdBase, List(filename), silent = true, testMode = true)

  def getCFG(filename: String): Try[CFG] = CmdCFGBuild(List("-silent", filename), testMode = true)

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
  def assertWrap(check: Boolean): Boolean = {
    assert(check)
    check
  }
  def analyzeTest(analysis: Try[(CFG, CallContext)]): Boolean = {
    analysis match {
      case Failure(_) => assertWrap(false)
      case Success((cfg, globalCallCtx)) =>
        val normalSt = cfg.globalFunc.exit.getState(globalCallCtx)
        val excSt = cfg.globalFunc.exitExc.getState(globalCallCtx)
        assert(!normalSt.heap.isBottom)
        normalSt.heap(BuiltinGlobal.loc) match {
          case None => assertWrap(false)
          case Some(globalObj) if globalObj.isBottom => assertWrap(false)
          case Some(globalObj) => {
            val resultKeySet = globalObj.collectKeySet(resultPrefix)
            val expectKeySet = globalObj.collectKeySet(expectPrefix)
            assert(resultKeySet.size == expectKeySet.size)
            resultKeySet.foldLeft(true)((b, resultKey) => {
              val num = resultKey.substring(resultPrefix.length)
              val expectKey = expectPrefix + num
              assert(expectKeySet contains expectKey)
              assert(globalObj(expectKey) <= globalObj(resultKey))
              b && (globalObj(resultKey) <= globalObj(expectKey))
            })
          }
        }
    }
  }

  // Permute filenames for randomness
  for (filename <- scala.util.Random.shuffle(new File(jsDir).list(jsFilter).toSeq)) {
    val name = filename.substring(0, filename.length - 3)
    val jsName = jsDir + filename

    val config = getSafeConfig(jsName)

    lazy val pgm = Parse((), config)
    registerTest("[Parse] " + filename, ParseTest) { parseTest(pgm) }

    lazy val ast = pgm.flatMap(ASTRewrite(_, config))
    registerTest("[ASTRewrite] " + filename, ASTRewriteTest) {
      val astName = resDir + "astRewrite" + SEP + name + ".test"
      astRewriteTest(ast, astName)
    }

    lazy val ir = ast.flatMap(Compile(_, config))
    registerTest("[Compile]" + filename, CompileTest) {
      val compileName = resDir + "compile" + SEP + name + ".test"
      compileTest(ir, compileName)
    }

    lazy val cfg = ir.flatMap(CFGBuild(_, config))
    registerTest("[CFG]" + filename, CFGBuildTest) {
      val cfgName = resDir + "cfg" + SEP + name + ".test"
      cfgBuildTest(cfg, cfgName)
    }
  }

  var totalList = List[String]()
  var impreciseList = List[String]()
  var preciseList = List[String]()
  val analysisDeatil = BASE_DIR + SEP + "tests" + SEP + "analysis-detail"

  val analyzerTestDir = testDir + "semantics"
  val analyzeConfig = AnalyzeConfig()
  for (file <- shuffle(walkTree(new File(analyzerTestDir))) if file.getName.endsWith(".js")) {
    val jsName = file.toString
    val relPath = jsName.substring(BASE_DIR.length)
    val filename = file.getName
    registerTest("[Analyze]" + filename, AnalyzeTest) {
      val safeConfig = getSafeConfig(jsName)
      val cfg = getCFG(jsName)
      val analysis = cfg.flatMap(Analyze(_, safeConfig, analyzeConfig))
      totalList ::= relPath
      if (analyzeTest(analysis)) preciseList ::= relPath
      else impreciseList ::= relPath
    }
  }

  val test262TestDir = testDir + "test262"
  for (file <- shuffle(walkTree(new File(test262TestDir))) if file.getName.endsWith(".js")) {
    val jsName = file.toString
    val relPath = jsName.substring(BASE_DIR.length)
    val filename = file.getName
    registerTest("[Test262]" + filename, Test262Test) {
      val safeConfig = getSafeConfig(jsName)
      val cfg = getCFG(jsName)
      val analysis = cfg.flatMap(Analyze(_, safeConfig, analyzeConfig))
      totalList ::= relPath
      if (analyzeTest(analysis)) preciseList ::= relPath
      else impreciseList ::= relPath
    }
  }

  override def afterAll(): Unit = {
    val file = new File(analysisDeatil)
    val bw = new BufferedWriter(new FileWriter(file))
    val pw = new PrintWriter(bw)
    val pre = preciseList.sorted
    val impre = impreciseList.sorted
    val fail = (totalList.toSet -- pre.toSet -- impre.toSet).toList.sorted
    pw.println("#######################")
    pw.println("# SUMMARY")
    pw.println("#######################")
    pw.println("# TOTAL : " + totalList.length)
    pw.println("# FAIL : " + fail.length)
    pw.println("# IMPRECISE : " + impre.length)
    pw.println("# PRECISE : " + pre.length)
    pw.println("#######################")
    pw.println()
    pw.println("FAILE : " + fail.length)
    fail.foreach(fn => pw.println(fn))
    pw.println()
    pw.println("IMPRECISE : " + impre.length)
    impre.foreach(fn => pw.println(fn))
    pw.println()
    pw.println("PRECISE : " + pre.length)
    pre.foreach(fn => pw.println(fn))
    pw.close()
  }
}
