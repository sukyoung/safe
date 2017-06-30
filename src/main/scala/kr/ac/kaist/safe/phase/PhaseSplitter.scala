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

import scala.util._
import kr.ac.kaist.safe.SafeConfig

class PhaseSplitter[Input, InputConfig <: Config, T](
  phaseOb1j: PhaseObj[Input, InputConfig, T]
)
    extends PhaseObj[Input, InputConfig, (Input, T)] {

  val name: String = "Splitter"
  val help: String = "Splits an input."
  val options = phaseOb1j.options

  val defaultConfig = phaseOb1j.defaultConfig

  def apply(
    input: Input,
    safeConfig: SafeConfig,
    config: InputConfig
  ): Try[(Input, T)] = {
    val resultTry = phaseOb1j.apply(input, safeConfig, config)
    resultTry match {
      case Success(result) =>
        Success((input, result))
      case Failure(f) =>
        Failure[(Input, T)](f)
    }
  }

}