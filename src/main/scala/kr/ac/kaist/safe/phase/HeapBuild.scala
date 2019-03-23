/**
 * *****************************************************************************
 * Copyright (c) 2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.phase

import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.{ LINE_SEP, SafeConfig }
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.model.JSModel
import kr.ac.kaist.safe.errors.error.NoChoiceError

// HeapBuild phase
case object HeapBuild extends PhaseObj[CFG, HeapBuildConfig, (CFG, Semantics, TracePartition, HeapBuildConfig, Int)] {
  val name: String = "heapBuilder"
  val help: String = "Build an initial heap."

  def apply(
    cfg: CFG,
    safeConfig: SafeConfig,
    config: HeapBuildConfig
  ): Try[(CFG, Semantics, TracePartition, HeapBuildConfig, Int)] = {
    // initialization
    register(
      config.AbsUndef,
      config.AbsNull,
      config.AbsBool,
      config.AbsNum,
      config.AbsStr,
      config.recencyMode,
      config.heapClone,
      config.callsiteSensitivity *
        config.loopSensitivity
    )

    // trace sensitivity
    val initTP = Sensitivity.initTP
    val entryCP = ControlPoint(cfg.globalFunc.entry, initTP)

    // initial abstract state
    var initSt = Initialize(cfg)

    // handling snapshot mode
    config.snapshot.map(str =>
      initSt = Initialize.addSnapshot(initSt, str))

    val worklist = Worklist(cfg)
    worklist.add(entryCP)

    val sem = Semantics(cfg, worklist)
    sem.setState(entryCP, initSt)

    Success((cfg, sem, initTP, config, -1))
  }

  def defaultConfig: HeapBuildConfig = HeapBuildConfig()
  val options: List[PhaseOption[HeapBuildConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during heap building are muted."),
    ("maxStrSetSize", NumOption((c, n) => if (n > 0) c.AbsStr = StringSet(n)),
      "the analyzer will use the AbsStr Set domain with given size limit n."),
    ("recency", BoolOption(c => c.recencyMode = true),
      "analysis with recency abstraction."),
    ("heap-clone", BoolOption(c => c.heapClone = true),
      "analysis with heap cloning that divides locations based on the given trace sensitivity."),
    ("callsiteSensitivity", NumOption((c, n) => if (n >= 0) c.callsiteSensitivity = CallSiteSensitivity(n)),
      "{number}-depth callsite-sensitive analysis will be executed."),
    ("loopIter", NumOption((c, n) => if (n >= 0) c.loopSensitivity = c.loopSensitivity.copy(maxIter = n)),
      "{number}-iteration loop-sensitive analysis will be executed."),
    ("loopDepth", NumOption((c, n) => if (n >= 0) c.loopSensitivity = c.loopSensitivity.copy(maxDepth = n)),
      "{number}-depth loop-sensitive analysis will be executed."),
    ("snapshot", StrOption((c, s) => c.snapshot = Some(s)),
      "analysis with an initial heap generated from a dynamic snapshot(*.json).")
  )

  // cache for JS model
  var jscache: Option[JSModel] = None
}

// HeapBuild phase config
case class HeapBuildConfig(
  var silent: Boolean = false,
  var AbsUndef: UndefDomain = DefaultUndef,
  var AbsNull: NullDomain = DefaultNull,
  var AbsBool: BoolDomain = DefaultBool,
  var AbsNum: NumDomain = DefaultNumber,
  var AbsStr: StrDomain = StringSet(0),
  var callsiteSensitivity: CallSiteSensitivity = CallSiteSensitivity(0),
  var loopSensitivity: LoopSensitivity = LoopSensitivity(0, 0),
  var snapshot: Option[String] = None,
  var recencyMode: Boolean = false,
  var heapClone: Boolean = false
) extends Config
