/**
 * *****************************************************************************
 * Copyright (c) 2016-2019, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe

import java.io._
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.phase._
import org.scalatest._
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

class CFGBuildTest extends SafeTest {
  // tests for parser
  def parseTest(pgm: Try[Program]): Unit = pgm match {
    case Failure(e) => fail(s"it throws an error: $e")
    case Success(program) =>
      Parser.stringToAST(program.toString(0)) match {
        case Failure(e) => fail(s"it throws an error: $e")
        case Success((pgm, _)) =>
          val pretty = pgm.toString(0)
          Parser.stringToAST(pretty) match {
            case Failure(e) => fail(s"it throws an error: $e")
            case Success((p, _)) =>
              assert(norm(p.toString(0)) == norm(pretty))
          }
      }
  }

  // tests for AST rewriter
  def astRewriteTest(ast: Try[Program], testName: String): Unit = {
    ast match {
      case Failure(_) => assert(false)
      case Success(program) =>
        assert(readFile(testName) == norm(program.toString(0)))
    }
  }

  // tests for translator
  def translateTest(ir: Try[IRRoot], testName: String): Unit = {
    ir match {
      case Failure(_) => assert(false)
      case Success(ir) =>
        assert(readFile(testName) == norm(ir.toString(0)))
    }
  }

  // tests for CFG builder
  def cfgBuildTest(cfg: Try[CFG], testName: String): Unit = {
    cfg match {
      case Failure(_) => assert(false)
      case Success(cfg) =>
        assert(readFile(testName) == norm(cfg.toString(0)))
    }
  }

  // javascript files
  val jsDir = testDir + "cfg" + SEP + "js" + SEP + "success" + SEP

  // result files
  val resDir = testDir + "cfg" + SEP + "result" + SEP + "success" + SEP

  // test directory
  def resDir(phase: String): String = s"$resDir$phase$SEP"
  lazy val jsToTest = changeExt("js", "test")
  lazy val astRewriteResDir = resDir("astRewrite")
  lazy val translateResDir = resDir("compile")
  lazy val cfgBuildResDir = resDir("cfg")

  // registration
  for (file <- shuffle(walkTree(new File(jsDir)))) {
    val filename = file.getName
    if (jsFilter(filename)) {
      lazy val name = file.toString
      lazy val resName = jsToTest(filename)
      lazy val config = safeConfig.copy(fileNames = List(name))

      lazy val pgm = Parse((), config)
      test(s"[Parse] $filename") { parseTest(pgm) }

      lazy val ast = pgm.flatMap(ASTRewrite(_, config))
      lazy val astResName = astRewriteResDir + resName
      test(s"[ASTRewrite] $filename") { astRewriteTest(ast, astResName) }

      lazy val ir = ast.flatMap(Translate(_, config))
      lazy val translateResName = translateResDir + resName
      test(s"[Translate] $filename") { translateTest(ir, translateResName) }

      lazy val cfg = ir.flatMap(CFGBuild(_, config))
      lazy val cfgBuildResName = cfgBuildResDir + resName
      test(s"[CFGBuild] $filename") { cfgBuildTest(cfg, cfgBuildResName) }
    }
  }
}
