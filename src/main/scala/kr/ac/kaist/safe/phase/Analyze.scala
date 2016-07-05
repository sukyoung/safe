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
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.console.Console
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._

// Analyze phase
case object Analyze extends PhaseObj[CFG, AnalyzeConfig, (CFG, CallContext)] {
  val name: String = "analyzer"
  val help: String = "Analyze the JavaScript source files."

  def apply(
    cfg: CFG,
    safeConfig: SafeConfig,
    config: AnalyzeConfig
  ): Try[(CFG, CallContext)] = {
    val utils = Utils(config.AbsUndef, config.AbsNull, config.AbsBool, config.AbsNumber, config.AbsString)
    val callCtxManager = CallContextManager(safeConfig.addrManager)

    val worklist = Worklist(cfg)
    worklist.add(ControlPoint(cfg.globalFunc.entry, callCtxManager.globalCallContext))
    val helper = Helper(utils, safeConfig.addrManager)
    val semantics = new Semantics(cfg, worklist, helper)
    val init = Initialize(helper)
    val initSt =
      if (config.testMode) init.testState
      else init.state
    cfg.globalFunc.entry.setState(callCtxManager.globalCallContext, initSt)
    val consoleOpt = config.console match {
      case true => Some(new Console(cfg, worklist, semantics, safeConfig.addrManager))
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

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("verbose", BoolOption(c => c.verbose = true),
      "messages during compilation are printed."),
    ("console", BoolOption(c => c.console = true),
      "you can use REPL-style debugger."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the analysis results will be written to the outfile."),
    ("maxStrSetSize", NumOption((c, n) => if (n > 0) c.AbsString = new DefaultStrSetUtil(n)),
      "the analyzer will use the AbsString Set domain with given size limit n."),
    ("callsiteSensitivity", NumOption((c, n) => if (n > 0) c.callsiteSensitivity = n),
      ""), // TODO
    ("testMode", BoolOption(c => c.testMode = true),
      "") // TODO
  )
}

// Analyze phase config
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
) extends Config
