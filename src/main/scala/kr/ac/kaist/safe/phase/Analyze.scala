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

    // set the start time.
    val startTime = System.currentTimeMillis
    var iters: Int = 0

    var interOpt: Option[Interactive] =
      if (config.console) Some(new Console(cfg, sem, heapConfig, iter))
      else None

    // calculate fixpoint
    val fixpoint = new Fixpoint(sem, interOpt)
    iters = fixpoint.compute(iter + 1)

    // display duration time
    if (config.time) {
      val duration = System.currentTimeMillis - startTime
      println(s"The analysis took $duration ms.")
    }

    // Report errors.
    val excLog = sem.excLog
    if (excLog.hasError) {
      println(cfg.fileName + ":")
      println(excLog)
    }

    // print html file: {htmlName}.html
    config.htmlName.foreach(name => {
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
    ("time", BoolOption(c => c.time = true),
      "display duration time."),
    ("exitDump", BoolOption(c => c.exitDump = true),
      "dump the state of the exit state of a given CFG"),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the analysis results will be written to the outfile.")
  // TODO ("html", StrOption((c, s) => c.htmlName = Some(s)),
  // TODO   "the resulting CFG with states will be drawn to the {string}.html")
  )
}

// Analyze phase config
case class AnalyzeConfig(
  var silent: Boolean = false,
  var console: Boolean = false,
  var time: Boolean = false,
  var exitDump: Boolean = false,
  var outFile: Option[String] = None,
  var htmlName: Option[String] = None
) extends Config
