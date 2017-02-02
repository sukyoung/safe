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

import scala.io.Source
import scala.util.{ Failure, Success, Try }
import scala.util.Random.shuffle
import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.errors.error.ParserError
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util._

object ParseTest extends Tag("ParseTest")
object ASTRewriteTest extends Tag("ASTRewriteTest")
object CompileTest extends Tag("CompileTest")
object CFGBuildTest extends Tag("CFGBuildTest")
object AnalyzeTest extends Tag("AnalyzeTest")
object Test262Test extends Tag("Test262Test")
object BenchTest extends Tag("BenchTest")

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
  abstract class AnalysisResult
  case object Precise extends AnalysisResult
  case object Imprecise extends AnalysisResult
  case object ParseError extends AnalysisResult
  case object Benchmark extends AnalysisResult
  case object Fail extends AnalysisResult
  def analyzeTest(analysis: Try[(CFG, Int, TracePartition, Semantics)], tag: Tag): (AnalysisResult, Int) = {
    analysis match {
      case Failure(e) => throw e
      case Success((cfg, iter, globalTP, sem)) if tag == BenchTest =>
        val normalSt = sem.getState(ControlPoint(cfg.globalFunc.exit, globalTP))
        assert(!normalSt.heap.isBottom)
        (Benchmark, iter)
      case Success((cfg, iter, globalTP, sem)) =>
        val normalSt = sem.getState(ControlPoint(cfg.globalFunc.exit, globalTP))
        val excSt = sem.getState(ControlPoint(cfg.globalFunc.exitExc, globalTP))
        assert(!normalSt.heap.isBottom)
        val ar = normalSt.heap.get(BuiltinGlobal.loc) match {
          case globalObj if globalObj.isBottom =>
            assert(false)
            Fail
          case globalObj => {
            def prefixCheck(prefix: String): (AbsString, AbsDataProp) => Boolean = {
              (str, dp) =>
                str.getSingle match {
                  case ConOne(Str(str)) => str.startsWith(prefix)
                  case _ => false
                }
            }
            val resultKeySet: Set[String] = globalObj.abstractKeySet(prefixCheck(resultPrefix)) match {
              case ConInf() =>
                assert(false); HashSet()
              case ConFin(set) => set.map(_.getSingle match {
                case ConOne(Str(str)) => str
                case _ => assert(false); ""
              })
            }
            val expectKeySet: Set[String] = globalObj.abstractKeySet(prefixCheck(expectPrefix)) match {
              case ConInf() =>
                assert(false); HashSet()
              case ConFin(set) => set.map(_.getSingle match {
                case ConOne(Str(str)) => str
                case _ => assert(false); ""
              })
            }
            assert(resultKeySet.size != 0)
            assert(resultKeySet.size == expectKeySet.size)
            if (resultKeySet.foldLeft(true)((b, resultKey) => {
              val num = resultKey.substring(resultPrefix.length)
              val expectKey = expectPrefix + num
              assert(expectKeySet contains expectKey)
              assert(globalObj(expectKey) <= globalObj(resultKey))
              b && (globalObj(resultKey) <= globalObj(expectKey))
            })) Precise
            else Imprecise
          }
        }
        (ar, iter)
    }
  }

  def analyzeHelper(prefix: String, tag: Tag, file: File): Unit = {
    val filename = file.getName
    val name = file.toString
    val relPath = name.substring(BASE_DIR.length)
    if (filename.endsWith(".js")) {
      registerTest(prefix + filename, tag) {
        val safeConfig = testSafeConfig.copy(fileNames = List(name))
        val cfg = getCFG(name)
        val analysis = cfg.flatMap(Analyze(_, safeConfig, analyzeConfig))
        testList ::= relPath
        val (ar, iter) = analyzeTest(analysis, tag)
        totalIteration += iter
        ar match {
          case Precise => preciseList ::= relPath
          case Imprecise => impreciseList ::= relPath
          case Benchmark => // Not yet decided what to do
          case Fail => // unreachable
        }
      }
    } else if (filename.endsWith(".js.todo")) {
      todoList ::= relPath
    } else if (filename.endsWith(".js.slow")) {
      slowList ::= relPath
    } else if (filename.endsWith(".js.err")) {
      registerTest(prefix + filename, tag) {
        val safeConfig = testSafeConfig.copy(fileNames = List(name))
        testList ::= relPath
        CmdParse(List("-silent", name), testMode = true) match {
          case Failure(ParserError(_, _)) => preciseList ::= relPath
          case e => assert(false)
        }
      }
    }
  }

  val testSafeConfig: SafeConfig = SafeConfig(CmdBase, Nil)

  // Permute filenames for randomness
  for (filename <- scala.util.Random.shuffle(new File(jsDir).list(jsFilter).toSeq)) {
    val name = filename.substring(0, filename.length - 3)
    val jsName = jsDir + filename

    val config = testSafeConfig.copy(fileNames = List(jsName))

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

  var testList = List[String]()
  var preciseList = List[String]()
  var impreciseList = List[String]()
  var todoList = List[String]()
  var slowList = List[String]()
  var totalIteration = 0

  val analysisDeatil = BASE_DIR + SEP + "tests" + SEP + "analysis-detail"
  val testJSON = BASE_DIR + SEP + "tests" + SEP + "test.json"

  Analyze.jscache = {
    val fileName = NodeUtil.jsModelsBase + "built_in.jsmodel"
    Some(ModelParser.parseFile(fileName).get)
  }

  val parser = new ArgParser(CmdBase, testSafeConfig)
  val analyzeConfig = Analyze.defaultConfig
  parser.addRule(analyzeConfig, Analyze.name, Analyze.options)
  parser(List(s"-json=$testJSON"))

  val analyzerTestDir = testDir + "semantics"
  for (file <- shuffle(walkTree(new File(analyzerTestDir))))
    analyzeHelper("[Analyze]", AnalyzeTest, file)

  val test262TestDir = testDir + "test262"
  for (file <- shuffle(walkTree(new File(test262TestDir))))
    analyzeHelper("[Test262]", Test262Test, file)

  val benchTestDir = testDir + "benchmarks"
  for (file <- shuffle(walkTree(new File(benchTestDir))))
    analyzeHelper("[Benchmarks]", BenchTest, file)

  override def afterAll(): Unit = {
    val file = new File(analysisDeatil)
    val bw = new BufferedWriter(new FileWriter(file))
    val pw = new PrintWriter(bw)
    val pre = preciseList.sorted
    val impre = impreciseList.sorted
    val todo = todoList.sorted
    val slow = slowList.sorted
    val fail = (testList.toSet -- pre.toSet -- impre.toSet).toList.sorted
    pw.println("#######################")
    pw.println("# SUMMARY")
    pw.println("#######################")
    pw.println("# TOTAL : " + (testList.length + todo.length + slow.length))
    pw.println("# TEST : " + testList.length)
    pw.println("# - FAIL : " + fail.length)
    pw.println("# - PRECISE : " + pre.length)
    pw.println("# - IMPRECISE : " + impre.length)
    pw.println("# - TOTAL ITERATION: " + totalIteration.toString)
    pw.println("# TODO : " + todo.length)
    pw.println("# SLOW : " + slow.length)
    pw.println("#######################")
    pw.println()
    pw.println("FAIL: " + fail.length)
    fail.foreach(fn => pw.println(fn))
    pw.println()
    pw.println("PRECISE: " + pre.length)
    pre.foreach(fn => pw.println(fn))
    pw.println()
    pw.println("IMPRECISE: " + impre.length)
    impre.foreach(fn => pw.println(fn))
    pw.println()
    pw.println("TODO: " + todo.length)
    todo.foreach(fn => pw.println(fn))
    pw.println("SLOW: " + slow.length)
    slow.foreach(fn => pw.println(fn))
    pw.close()
  }
}
