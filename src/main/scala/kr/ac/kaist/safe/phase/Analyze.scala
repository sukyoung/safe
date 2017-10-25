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
import kr.ac.kaist.safe.json.NodeProtocol
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.web.WebServer

import scala.util.{ Success, Try }

// Analyze phase
case object Analyze extends PhaseObj[(CFG, Worklist, Semantics, TracePartition, HeapBuildConfig, Int), AnalyzeConfig, (CFG, Int, TracePartition, Semantics)] {
  val name: String = "analyzer"
  val help: String = "Analyze JavaScript source files."

  def apply(
    in: (CFG, Worklist, Semantics, TracePartition, HeapBuildConfig, Int),
    safeConfig: SafeConfig,
    config: AnalyzeConfig
  ): Try[(CFG, Int, TracePartition, Semantics)] = {
    val (cfg, worklist, sem, initTP, heapConfig, iter) = in

    var interOpt: Option[Interactive] = null
    if (config.web) {
      interOpt = Some(new WebConsole(cfg, worklist, sem, heapConfig, iter))
    } else {
      interOpt = Some(new Console(cfg, worklist, sem, heapConfig, iter))
    }

    NodeProtocol.test = safeConfig.testMode

    // set the start time.
    val startTime = System.currentTimeMillis

    // run web server
    if (config.web) {
      WebServer.run(interOpt.get)
    }

    // calculate fixpoint
    val fixpoint = new Fixpoint(sem, worklist, interOpt)
    val iters = fixpoint.compute(iter + 1)

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
      "the analysis results will be written to the outfile."),
    ("html", StrOption((c, s) => c.htmlName = Some(s)),
      "the resulting CFG with states will be drawn to the {string}.html"),
    ("web", BoolOption(c => c.web = true),
      "run analytics web server")
  )
}

// Analyze phase config
case class AnalyzeConfig(
  var silent: Boolean = false,
  var console: Boolean = false,
  var web: Boolean = false,
  var time: Boolean = false,
  var exitDump: Boolean = false,
  var outFile: Option[String] = None,
  var htmlName: Option[String] = None
) extends Config
