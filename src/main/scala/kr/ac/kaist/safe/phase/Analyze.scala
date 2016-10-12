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

import scala.util.{ Failure, Success, Try }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.console.Console
import kr.ac.kaist.safe.cfg_builder.HTMLWriter
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._

// Analyze phase
case object Analyze extends PhaseObj[CFG, AnalyzeConfig, (CFG, Int, CallContext)] {
  val name: String = "analyzer"
  val help: String = "Analyze the JavaScript source files."

  def apply(
    cfg: CFG,
    safeConfig: SafeConfig,
    config: AnalyzeConfig
  ): Try[(CFG, Int, CallContext)] = {
    Utils.register(
      config.AbsUndef,
      config.AbsNull,
      config.AbsBool,
      config.AbsNumber,
      config.AbsString
    )
    val globalCC = CallContextManager().globalCallContext

    val worklist = Worklist(cfg)
    worklist.add(ControlPoint(cfg.globalFunc.entry, globalCC))
    val semantics = new Semantics(cfg, worklist)
    val init = Initialize(cfg)
    val initSt =
      if (safeConfig.testMode) init.testState
      else init.state
    cfg.globalFunc.entry.setState(globalCC, initSt)
    val consoleOpt = config.console match {
      case true => Some(new Console(cfg, worklist, semantics))
      case false => None
    }
    val fixpoint = new Fixpoint(semantics, worklist, consoleOpt)
    val iters = fixpoint.compute()

    val excLog = semantics.excLog
    // Report errors.
    if (excLog.hasError) {
      println(cfg.fileName + ":")
      println(excLog)
    }

    // print html file: {htmlName}.html
    config.htmlName.map(name => {
      HTMLWriter.writeHTMLFile(cfg, None, s"$name.html")
    })

    Success((cfg, iters, globalCC))
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during analysis are muted."),
    ("console", BoolOption(c => c.console = true),
      "REPL-style console debugger."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the analysis results will be written to the outfile."),
    ("maxStrSetSize", NumOption((c, n) => if (n > 0) c.AbsString = StringSet(n)),
      "the analyzer will use the AbsString Set domain with given size limit n."),
    ("callsiteSensitivity", NumOption((c, n) => if (n > 0) c.callsiteSensitivity = n),
      "{number}-depth callsite-sensitive analysis will be executed."),
    ("html", StrOption((c, s) => c.htmlName = Some(s)),
      "the resulting CFG with states will be drawn to the {string}.html")
  )
}

// Analyze phase config
case class AnalyzeConfig(
  var silent: Boolean = false,
  var console: Boolean = false,
  var outFile: Option[String] = None,
  var AbsUndef: AbsUndefUtil = DefaultUndef,
  var AbsNull: AbsNullUtil = DefaultNull,
  var AbsBool: AbsBoolUtil = DefaultBool,
  var AbsNumber: AbsNumberUtil = DefaultNumber,
  var AbsString: AbsStringUtil = StringSet(0),
  var callsiteSensitivity: Int = -1,
  var htmlName: Option[String] = None
) extends Config
