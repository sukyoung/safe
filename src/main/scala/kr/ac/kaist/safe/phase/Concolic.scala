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

import kr.ac.kaist.safe.analyzer.{TracePartition, Semantics, Worklist}
import kr.ac.kaist.safe.concolic.ConcolicMain

import scala.util.Try
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg._

object Concolic extends PhaseObj[(CFG, Worklist, Semantics, TracePartition, HeapBuildConfig, Int), ConcolicConfig, Int] {

  val name: String = "concolic"
  val help: String = "Performs concolic testing."
  val defaultConfig: ConcolicConfig = ConcolicConfig()
  val options: List[PhaseOption[ConcolicConfig]] = Nil

  def apply(in: (CFG, Worklist, Semantics, TracePartition, HeapBuildConfig, Int),
    safeConfig: SafeConfig,
    config: ConcolicConfig
  ): Try[Int] = {
    ConcolicMain.concolic(in, safeConfig)
  }

}

/**
  * The configuration parameters for the concolic tester.
  * Currently, the concolic tester does not use any configuration parameters.
  */
case class ConcolicConfig() extends Config
