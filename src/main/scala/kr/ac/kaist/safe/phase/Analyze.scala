/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
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
import kr.ac.kaist.safe.analyzer.console.Console
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._

// Analyze phase
case object Analyze extends PhaseObj[(CFG, Worklist, Semantics, TracePartition), AnalyzeConfig, (CFG, Int, TracePartition, Semantics)] {
  val name: String = "analyzer"
  val help: String = "Analyze JavaScript source files."

  def apply(
    in: (CFG, Worklist, Semantics, TracePartition),
    safeConfig: SafeConfig,
    config: AnalyzeConfig
  ): Try[(CFG, Int, TracePartition, Semantics)] = {
    val (cfg, worklist, sem, initTP) = in

    val consoleOpt = config.console match {
      case true => Some(new Console(cfg, worklist, sem))
      case false => None
    }

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
    ("html", StrOption((c, s) => c.htmlName = Some(s)),
      "the resulting CFG with states will be drawn to the {string}.html")
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
  var htmlName: Option[String] = None
) extends Config
