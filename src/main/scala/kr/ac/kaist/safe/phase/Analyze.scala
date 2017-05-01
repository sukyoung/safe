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
import kr.ac.kaist.safe.analyzer.models.JSModel
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.console.Console
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter
import kr.ac.kaist.safe.errors.error.NoChoiceError
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._

// Analyze phase
case object Analyze extends PhaseObj[CFG, AnalyzeConfig, (CFG, Int, TracePartition, Semantics)] {
  val name: String = "analyzer"
  val help: String = "Analyze JavaScript source files."

  def apply(
    cfg: CFG,
    safeConfig: SafeConfig,
    config: AnalyzeConfig
  ): Try[(CFG, Int, TracePartition, Semantics)] = {
    // initialization
    Utils.register(
      config.AbsUndef,
      config.AbsNull,
      config.AbsBool,
      config.AbsNumber,
      config.AbsString,
      DefaultLoc,
      config.aaddrType
    )
    var initSt = Initialize(cfg, config.jsModel)

    // handling snapshot mode
    config.snapshot.map(str =>
      initSt = Initialize.addSnapshot(initSt, str))

    val sens =
      CallSiteSensitivity(config.callsiteSensitivity) *
        LoopSensitivity(config.loopSensitivity)
    val initTP = sens.initTP
    val entryCP = ControlPoint(cfg.globalFunc.entry, initTP)

    val worklist = Worklist(cfg)
    worklist.add(entryCP)
    val sem = new Semantics(cfg, worklist)
    val consoleOpt = config.console match {
      case true => Some(new Console(cfg, worklist, sem))
      case false => None
    }

    sem.setState(entryCP, initSt)
    val fixpoint = new Fixpoint(sem, worklist, consoleOpt)
    val iters = fixpoint.compute()

    val excLog = sem.excLog
    // Report errors.
    if (excLog.hasError) {
      println(cfg.fileName + ":")
      println(excLog)
    }

    // print html file: {htmlName}.html
    config.htmlName.map(name => {
      HTMLWriter.writeHTMLFile(cfg, sem, None, s"$name.html")
    })

    // dump exit state
    if (config.exitDump) {
      val exitCP = ControlPoint(cfg.globalFunc.exit, initTP)
      val state = sem.getState(exitCP)
      println(state.toString)
    }

    Success((cfg, iters, initTP, sem))
  }

  def defaultConfig: AnalyzeConfig = AnalyzeConfig()
  val options: List[PhaseOption[AnalyzeConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during analysis are muted."),
    ("console", BoolOption(c => c.console = true),
      "REPL-style console debugger."),
    ("exitDump", BoolOption(c => c.exitDump = true),
      "dump the state of the exit state of a given CFG"),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the analysis results will be written to the outfile."),
    ("maxStrSetSize", NumOption((c, n) => if (n > 0) c.AbsString = StringSet(n)),
      "the analyzer will use the AbsString Set domain with given size limit n."),
    ("aaddrType", StrOption((c, s) => s match {
      case "normal" => c.aaddrType = NormalAAddr
      case "recency" => c.aaddrType = RecencyAAddr
      case "concrete" => c.aaddrType = ConcreteAAddr
      case str => throw NoChoiceError(s"there is no address abstraction type with name '$str'.")
    }), "address abstraction type."),
    ("callsiteSensitivity", NumOption((c, n) => if (n >= 0) c.callsiteSensitivity = n),
      "{number}-depth callsite-sensitive analysis will be executed."),
    ("loopSensitivity", NumOption((c, n) => if (n >= 0) c.loopSensitivity = n),
      "{number}-depth loop-sensitive analysis will be executed."),
    ("html", StrOption((c, s) => c.htmlName = Some(s)),
      "the resulting CFG with states will be drawn to the {string}.html"),
    ("snapshot", StrOption((c, s) => c.snapshot = Some(s)),
      "analysis with an initial heap generated from a dynamic snapshot(*.json)."),
    ("number", StrOption((c, s) => s match {
      case "default" => c.AbsNumber = DefaultNumber
      case "flat" => c.AbsNumber = FlatNumber
      case str => throw NoChoiceError(s"there is no abstract number domain with name '$str'.")
    }), "analysis with a selected number domain."),
    ("jsModel", BoolOption(c => c.jsModel = true),
      "analysis with JavaScript models.")
  )

  // cache for JS model
  var jscache: Option[JSModel] = None
}

// Analyze phase config
case class AnalyzeConfig(
  var silent: Boolean = false,
  var console: Boolean = false,
  var exitDump: Boolean = false,
  var outFile: Option[String] = None,
  var AbsUndef: AbsUndefUtil = DefaultUndef,
  var AbsNull: AbsNullUtil = DefaultNull,
  var AbsBool: AbsBoolUtil = DefaultBool,
  var AbsNumber: AbsNumberUtil = DefaultNumber,
  var AbsString: AbsStringUtil = StringSet(0),
  var callsiteSensitivity: Int = 0,
  var loopSensitivity: Int = 0,
  var htmlName: Option[String] = None,
  var snapshot: Option[String] = None,
  var jsModel: Boolean = false,
  var aaddrType: AAddrType = RecencyAAddr
) extends Config
