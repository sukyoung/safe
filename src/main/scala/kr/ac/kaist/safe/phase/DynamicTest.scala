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

import scala.io.Source
import scala.util.{ Try, Failure, Success }
import kr.ac.kaist.safe.{ BASE_DIR, CmdAnalyze, SafeConfig }
import kr.ac.kaist.safe.analyzer.CallContext
import kr.ac.kaist.safe.analyzer.domain.State
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.nodes.cfg.CFG

import java.io.{ File, FilenameFilter, PrintWriter }
import spray.json._

case class TestOutput(boolVal: Option[Boolean], nullVal: Option[Boolean], numVal: Option[Int], strVal: Option[String], undefVal: Option[Boolean])

case class TestModel(name: String, input: Int, output: Option[TestOutput])

object TestJsonProtocol extends DefaultJsonProtocol {
  implicit val outputFormat = jsonFormat(TestOutput, "boolean", "null", "number", "string", "undefined")
  implicit val testFormat = jsonFormat(TestModel, "name", "input", "output")
}

import TestJsonProtocol._

// DynamicTest phase
case object DynamicTest extends PhaseObj[Unit, DynamicTestConfig, Unit] {
  val name = "dynamicTester"
  val help = "Tests for library modelings."

  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: DynamicTestConfig
  ): Try[Unit] = {
    val SEP = File.separator
    val dynamicTestDir = BASE_DIR + SEP + "tests/dynamicTest"
    val jsonFilter = new FilenameFilter() {
      def accept(dir: File, name: String): Boolean = name.endsWith(".json")
    }
    for (filename <- scala.util.Random.shuffle(new File(dynamicTestDir).list(jsonFilter).toSeq)) {
      val jsonName = dynamicTestDir + SEP + filename
      val jsonInput = scala.io.Source.fromFile(jsonName)("UTF-8").mkString.parseJson
      val testJson = jsonInput.convertTo[TestModel]
      val tempFile = new File("temp.js")
      val pw = new PrintWriter(tempFile)
      testJson.output match {
        case Some(output) => {
          output.numVal match {
            case Some(num) => pw.write(s"var __result1 = ${testJson.name}(${testJson.input}); var __expect1 = $num;")
            case _ => pw.write(s"var __result1 = ${testJson.name}(${testJson.input}); var __expect1 = undefined;")
          }
        }
        case _ => pw.write(s"var __result1 = ${testJson.name}(${testJson.input}); var __expect1 = undefined;")
      }
      pw.close
      val analysis = CmdAnalyze(List("-analyzer:testMode", "temp.js"))
      analyzeTest(analysis)
      tempFile.delete()
    }
    Try(None)
  }

  def analyzeTest(analysis: Try[(CFG, CallContext)]): Unit = {
    analysis match {
      case Failure(_) => assert(false)
      case Success((cfg, globalCallCtx)) =>
        val normalSt = cfg.globalFunc.exit.getState(globalCallCtx)
        val excSt = cfg.globalFunc.exitExc.getState(globalCallCtx)
        assert(!normalSt.heap.isBottom)
        normalSt.heap(BuiltinGlobal.loc) match {
          case None => assert(false)
          case Some(globalObj) if globalObj.isBottom => assert(false)
          case Some(globalObj) =>
            assert(globalObj("__expect1") <= globalObj("__result1"))
        }
    }
  }

  def defaultConfig: DynamicTestConfig = DynamicTestConfig()
  val options: List[PhaseOption[DynamicTestConfig]] = Nil
}

// DynamicTest phase config
case class DynamicTestConfig() extends Config
