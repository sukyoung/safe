/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
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
import kr.ac.kaist.safe.cfg_builder.DotWriter
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models.JSModel
import kr.ac.kaist.safe.errors.error.NoChoiceError

// HeapBuild phase
case object HeapBuild extends PhaseObj[CFG, HeapBuildConfig, (CFG, Worklist, Semantics, TracePartition, HeapBuildConfig, Int)] {
  val name: String = "heapBuilder"
  val help: String = "Build an initial heap."

  def apply(
    cfg: CFG,
    safeConfig: SafeConfig,
    config: HeapBuildConfig
  ): Try[(CFG, Worklist, Semantics, TracePartition, HeapBuildConfig, Int)] = {
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
    var initSt = Initialize(cfg, config.jsModel, safeConfig.nodejs)

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
    sem.setState(entryCP, initSt)

    Success((cfg, worklist, sem, initTP, config, -1))
  }

  def defaultConfig: HeapBuildConfig = HeapBuildConfig()
  val options: List[PhaseOption[HeapBuildConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during heap building are muted."),
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

// HeapBuild phase config
case class HeapBuildConfig(
  var silent: Boolean = false,
  var AbsUndef: AbsUndefUtil = DefaultUndef,
  var AbsNull: AbsNullUtil = DefaultNull,
  var AbsBool: AbsBoolUtil = DefaultBool,
  var AbsNumber: AbsNumberUtil = DefaultNumber,
  var AbsString: AbsStringUtil = StringSet(0),
  var callsiteSensitivity: Int = 0,
  var loopSensitivity: Int = 0,
  var snapshot: Option[String] = None,
  var jsModel: Boolean = false,
  var aaddrType: AAddrType = RecencyAAddr
) extends Config
