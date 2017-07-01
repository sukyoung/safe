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
import kr.ac.kaist.safe.errors.error.NoChoiceError

// HeapBuild phase
case object HeapBuild extends PhaseObj[CFG, HeapBuildConfig, (CFG, Worklist, Semantics, TracePartition)] {
  val name: String = "HeapBuild"
  val help: String = "Build an initial heap."

  def apply(
    cfg: CFG,
    safeConfig: SafeConfig,
    config: HeapBuildConfig
  ): Try[(CFG, Worklist, Semantics, TracePartition)] = {
    Success((cfg, null, null, null))
  }

  def defaultConfig: HeapBuildConfig = HeapBuildConfig()
  val options: List[PhaseOption[HeapBuildConfig]] = List()
}

// HeapBuild phase config
case class HeapBuildConfig() extends Config
