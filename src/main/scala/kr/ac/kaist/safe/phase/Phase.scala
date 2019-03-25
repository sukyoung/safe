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

import scala.util.Try
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.util.ArgParser

abstract class Phase {
  val name: String
  val help: String
  def getOptShapes: List[String]
  def getOptDescs: List[(String, String)]
}
abstract class PhaseObj[Input, PhaseConfig <: Config, Output] extends Phase {
  val name: String
  val help: String
  def apply(
    in: Input,
    safeConfig: SafeConfig,
    config: PhaseConfig = defaultConfig
  ): Try[Output]
  def defaultConfig: PhaseConfig
  val options: List[PhaseOption[PhaseConfig]] // TODO option conflict check

  def getRunner(
    parser: ArgParser
  ): Try[(Input, SafeConfig) => Try[Output]] = {
    val config = defaultConfig
    parser.addRule(config, name, options).map(_ => {
      (in, safeConfig) => apply(in, safeConfig, config)
    })
  }

  def getOptShapes: List[String] = options.map {
    case (opt, kind, _) => s"-$name:${opt}${kind.postfix}"
  }
  def getOptDescs: List[(String, String)] = options.map {
    case (opt, kind, desc) => (s"-$name:${opt}${kind.postfix}", desc)
  }
}

trait Config
