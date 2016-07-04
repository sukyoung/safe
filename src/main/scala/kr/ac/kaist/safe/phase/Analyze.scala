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

import kr.ac.kaist.safe.analyzer._

import scala.util.{ Failure, Success, Try }
import kr.ac.kaist.safe.config.{ BoolOption, Config, ConfigOption, NumOption, OptionKind, StrOption }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.console.Console
import kr.ac.kaist.safe.nodes.cfg.CFG

// Analyze phase struct.
case class Analyze(
    prev: CFGBuild = CFGBuild(),
    analyzeConfig: AnalyzeConfig = AnalyzeConfig()
) extends Phase(Some(prev), Some(analyzeConfig)) {
  override def apply(config: Config): Unit = analyze(config) recover {
    case ex => System.err.print(ex.toString)
  }
  def analyze(config: Config): Try[(CFG, CallContext)] =
    prev.cfgBuild(config).flatMap(analyze(config, _))

  def analyze(config: Config, cfg: CFG): Try[(CFG, CallContext)] = {
    val utils = Utils(analyzeConfig.AbsUndef, analyzeConfig.AbsNull, analyzeConfig.AbsBool, analyzeConfig.AbsNumber, analyzeConfig.AbsString)
    val callCtxManager = CallContextManager(config.addrManager)

    val worklist = Worklist(cfg)
    worklist.add(ControlPoint(cfg.globalFunc.entry, callCtxManager.globalCallContext))
    val helper = Helper(utils, config.addrManager)
    val semantics = new Semantics(cfg, worklist, helper)
    val init = Initialize(helper)
    val initSt =
      if (analyzeConfig.testMode) init.testState
      else init.state
    cfg.globalFunc.entry.setState(callCtxManager.globalCallContext, initSt)
    val consoleOpt = analyzeConfig.console match {
      case true => Some(new Console(cfg, worklist, semantics, config.addrManager))
      case false => None
    }
    val fixpoint = new Fixpoint(semantics, worklist, consoleOpt)
    fixpoint.compute()

    val excLog = semantics.excLog
    // Report errors.
    if (excLog.hasError) {
      println(cfg.fileName + ":")
      println(excLog)
    }

    Success((cfg, callCtxManager.globalCallContext))
  }
}

// Analyze phase helper.
object Analyze extends PhaseHelper {
  def create: Analyze = Analyze()
}

// Config options for Analyze phase.
case class AnalyzeConfig(
    var verbose: Boolean = false,
    var console: Boolean = false,
    var outFile: Option[String] = None,
    var AbsUndef: AbsUndefUtil = DefaultUndefUtil,
    var AbsNull: AbsNullUtil = DefaultNullUtil,
    var AbsBool: AbsBoolUtil = DefaultBoolUtil,
    var AbsNumber: AbsNumberUtil = DefaultNumUtil,
    var AbsString: AbsStringUtil = new DefaultStrSetUtil(0),
    var callsiteSensitivity: Int = -1,
    var testMode: Boolean = false
) extends ConfigOption {
  val prefix: String = "analyze:"
  val options: List[(String, OptionKind)] = List(
    ("verbose", BoolOption(() => verbose = true)),
    ("console", BoolOption(() => console = true)),
    ("out", StrOption((s: String) => outFile = Some(s))),
    ("maxStrSetSize", NumOption((n: Int) => if (n > 0) AbsString = new DefaultStrSetUtil(n))),
    ("callsiteSensitivity", NumOption((n: Int) => if (n > 0) callsiteSensitivity = n)),
    ("testMode", BoolOption(() => testMode = true))
  )
}