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
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.model._
import kr.ac.kaist.safe.errors.error.ParserError
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util._
import org.scalatest._
import scala.util.Random.shuffle
import scala.util.{ Failure, Success, Try }

abstract class AnalyzeTest extends SafeTest {
  // analysis configuration
  val heapBuildConfig = HeapBuild.defaultConfig
  val analyzeConfig = Analyze.defaultConfig
  val configFile = CONFIG_FILE

  // check prefix of abstract strings
  def prefixCheck(prefix: String): (AbsStr, AbsDataProp) => Boolean = {
    case (str, dp) => str.getSingle match {
      case ConOne(Str(str)) => str.startsWith(prefix)
      case _ => false
    }
  }

  // get abstract key set based on prefix predicate
  def getAbsKeySet(
    obj: AbsObj,
    ppred: (AbsStr, AbsDataProp) => Boolean
  ): Set[String] = {
    obj.abstractKeySet(ppred) match {
      case ConInf => fail()
      case ConFin(set) => set.map(_.getSingle match {
        case ConOne(Str(str)) => str
        case _ => assert(false); ""
      })
    }
  }

  // initialization for tests
  HeapBuild.jscache = {
    val parser = new ArgParser(CmdAnalyze, safeConfig)
    parser.addRule(heapBuildConfig, HeapBuild.name, HeapBuild.options)
    parser.addRule(analyzeConfig, Analyze.name, Analyze.options)
    parser(List(s"-config=$configFile"))

    register(
      heapBuildConfig.AbsUndef,
      heapBuildConfig.AbsNull,
      heapBuildConfig.AbsBool,
      heapBuildConfig.AbsNum,
      heapBuildConfig.AbsStr,
      heapBuildConfig.recencyMode,
      heapBuildConfig.heapClone,
      heapBuildConfig.callsiteSensitivity *
        heapBuildConfig.loopSensitivity
    )
    Some(Model.parseDir(NodeUtil.jsModelsBase))
  }

  // get CFG
  def getCFG(filename: String): Try[CFG] = CmdCFGBuild(List("-silent", filename), testMode = true)

  // tests for analyzer
  val resultPrefix = "__result"
  val expectPrefix = "__expect"
  def analyzeTest(analysis: Try[(CFG, Int, TracePartition, Semantics)]): Unit = analysis match {
    case Failure(e) => throw e
    case Success((cfg, iter, globalTP, sem)) =>
      val normalSt = sem.getState(ControlPoint(cfg.globalFunc.exit, globalTP))
      val excSt = sem.getState(ControlPoint(cfg.globalFunc.exitExc, globalTP))
      assert(!normalSt.heap.isBottom)
      normalSt.heap.get(GLOBAL_LOC) match {
        case globalObj => {
          if (globalObj.isBottom) fail()
          val resultKeySet =
            getAbsKeySet(globalObj, prefixCheck(resultPrefix))
          val expectKeySet =
            getAbsKeySet(globalObj, prefixCheck(expectPrefix))
          resultKeySet.foreach(resultKey => {
            val num = resultKey.substring(resultPrefix.length)
            val expectKey = expectPrefix + num
            assert(expectKeySet contains expectKey)
            assert(globalObj(expectKey) ⊑ globalObj(resultKey))
          })
        }
      }
  }

  // helper for analyzer tests
  def analyzeHelper(prefix: String, dirs: List[String]): Unit = dirs.foreach(dir => {
    for (file <- shuffle(walkTree(new File(dir)))) {
      val filename = file.getName
      lazy val name = file.toString
      lazy val config = safeConfig.copy(fileNames = List(name))
      if (jsFilter(filename) || htmlFilter(filename)) {
        lazy val cfg = getCFG(name)
        lazy val heapBuild = cfg.flatMap(HeapBuild(_, config, heapBuildConfig))
        lazy val analysis = heapBuild.flatMap(Analyze(_, config, analyzeConfig))
        test(s"[$prefix] $filename") { analyzeTest(analysis) }
      } else if (errFilter(filename)) {
        test(s"[$prefix] $filename") {
          CmdParse(List("-silent", name), testMode = true) match {
            case Failure(ParserError(_, _)) =>
            case _ => fail()
          }
        }
      }
    }
  })
}
