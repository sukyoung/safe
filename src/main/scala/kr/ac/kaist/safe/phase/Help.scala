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

import scala.util.{ Try, Success }
import kr.ac.kaist.safe.{ Safe, SafeConfig }

// Help phase
case object Help extends PhaseObj[Unit, HelpConfig, Unit] {
  val name = "helper"
  val help = ""

  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: HelpConfig
  ): Try[Unit] = Success(println(Safe.help))
  def defaultConfig: HelpConfig = HelpConfig()
  val options: List[PhaseOption[HelpConfig]] = Nil
}

case class HelpConfig() extends Config
