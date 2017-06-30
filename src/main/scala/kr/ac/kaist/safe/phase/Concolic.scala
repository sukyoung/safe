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

import kr.ac.kaist.safe.concolic.ConcolicMain

import scala.util.Try
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir._

object Concolic extends PhaseObj[(IRRoot, CFG), ConcolicConfig, Int] {

  val name: String = "concolic"
  val help: String = "Performs concolic testing."
  val defaultConfig: ConcolicConfig = ConcolicConfig()
  val options: List[PhaseOption[ConcolicConfig]] = Nil

  def apply(
    input: (IRRoot, CFG),
    safeConfig: SafeConfig,
    config: ConcolicConfig
  ): Try[Int] = {
    val (root, cfg) = input
    ConcolicMain.concolic(root, cfg)
  }

}

case class ConcolicConfig() extends Config
