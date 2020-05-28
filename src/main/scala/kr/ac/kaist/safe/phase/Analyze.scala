/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.phase

import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.console.{ Console, Interactive, WebConsole }
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.web.WebServer
import scala.util.{ Success, Try }

// Analyze phase
case object Analyze extends PhaseObj[(CFG, Semantics, TracePartition, HeapBuildConfig, Int), AnalyzeConfig, (CFG, Int, TracePartition, Semantics)] {
  val name: String = "analyzer"
  val help: String = "Analyze JavaScript source files."

  def apply(
    in: (CFG, Semantics, TracePartition, HeapBuildConfig, Int),
    safeConfig: SafeConfig,
    config: AnalyzeConfig
  ): Try[(CFG, Int, TracePartition, Semantics)] = {
    val (cfg, sem, initTP, heapConfig, iter) = in

    var interOpt: Option[Interactive] =
      if (config.console) Some(new Console(cfg, sem, heapConfig, iter))
      else None

    // calculate fixpoint
    val fixpoint =
      if (config.timeLog) new FixpointTime(sem, interOpt, "block.csv", "func.csv")
      else new Fixpoint(sem, interOpt)
    val (iters, duration) = fixpoint.compute(iter + 1)

    // display duration time
    if (config.time) {
      println(s"iteration number: $iters")
      println(f"The analysis took $duration%.9f s.")
    }

    // Report errors.
    val excLog = sem.excLog
    if (!safeConfig.testMode && excLog.hasError) {
      println(cfg.fileName + ":")
      println(excLog)
    }

    // print html file: {htmlName}.html
    val exitCP = ControlPoint(cfg.globalFunc.exit, initTP)
    config.htmlName.foreach(name => {
      HTMLWriter.writeHTMLFile(cfg, sem, None, s"$name.html")
    })

    // dump exit state
    if (config.exitDump) {
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
    ("time", BoolOption(c => c.time = true),
      "display duration time."),
    ("time-log", BoolOption(c => c.timeLog = true),
      "log duration time for each function."),
    ("exitDump", BoolOption(c => c.exitDump = true),
      "dump the state of the exit state of a given CFG"),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the analysis results will be written to the outfile."),
    ("html", StrOption((c, s) => c.htmlName = Some(s)),
      "the resulting CFG with states will be drawn to the {string}.html")
  )
}

// Analyze phase config
case class AnalyzeConfig(
  var silent: Boolean = false,
  var console: Boolean = false,
  var time: Boolean = false,
  var timeLog: Boolean = false,
  var exitDump: Boolean = false,
  var outFile: Option[String] = None,
  var htmlName: Option[String] = None
) extends Config
