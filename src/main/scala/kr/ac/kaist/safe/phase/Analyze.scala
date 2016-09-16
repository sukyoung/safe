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
import kr.ac.kaist.safe.LINE_SEP
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
      if (config.testMode) init.testState
      else init.state
    cfg.globalFunc.entry.setState(globalCC, initSt)
    val consoleOpt = config.console match {
      case true => Some(new Console(cfg, worklist, semantics))
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

    if (config.dump || config.dumpAll) {
      val state = cfg.globalFunc.exit.getState(globalCC)
      val heap = state.heap
      val context = state.context
      val old = context.old
      println("** heap **" + LINE_SEP +
        (if (config.dumpAll) heap.toStringAll else heap.toString) + LINE_SEP +
        LINE_SEP +
        "** context **" + LINE_SEP +
        context.toString + LINE_SEP +
        LINE_SEP +
        "** old address set **" + LINE_SEP +
        old.toString)
    }

    Success((cfg, globalCC))
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("verbose", BoolOption(c => c.verbose = true),
      "messages during compilation are printed."),
    ("dump", BoolOption(c => c.dump = true),
      "dump the state of the exit node of the global function."),
    ("dumpAll", BoolOption(c => c.dumpAll = true),
      "dump all locations for the state of the exit node of the global function."),
    ("console", BoolOption(c => c.console = true),
      "you can use REPL-style debugger."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the analysis results will be written to the outfile."),
    ("maxStrSetSize", NumOption((c, n) => if (n > 0) c.AbsString = StringSet(n)),
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
  var dump: Boolean = false,
  var dumpAll: Boolean = false,
  var console: Boolean = false,
  var outFile: Option[String] = None,
  var AbsUndef: AbsUndefUtil = DefaultUndef,
  var AbsNull: AbsNullUtil = DefaultNull,
  var AbsBool: AbsBoolUtil = DefaultBool,
  var AbsNumber: AbsNumberUtil = DefaultNumber,
  var AbsString: AbsStringUtil = StringSet(0),
  var callsiteSensitivity: Int = -1,
  var testMode: Boolean = false
) extends Config
