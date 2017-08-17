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

import scala.util._
import kr.ac.kaist.safe.SafeConfig

/**
 * Can be used to split Phases. A PhaseSplitter has a PhaseObj o. When the Splitter is applied, it passes its given
 * input i to to the PhaseObj o, and returns a tuple consisting of both the output of o and the original input i.
 * @param phaseObj The PhaseObj o to which to pass the input and from which to return the output.
 * @tparam Input The type of input to pass to o.
 * @tparam InputConfig THe type of the configuration parameter to pass to o.
 * @tparam Result The type of the result of o.
 */
case class PhaseSplitter[Input, InputConfig <: Config, Result](phaseObj: PhaseObj[Input, InputConfig, Result])
    extends PhaseObj[Input, InputConfig, (Input, Result)] {

  val name: String = "Splitter"
  val help: String = "Passes a given input to a PhaseObject and returns a tuple consisting of the object's " +
    "output and the original input."
  val options = phaseObj.options

  val defaultConfig = phaseObj.defaultConfig

  def apply(
    input: Input,
    safeConfig: SafeConfig,
    config: InputConfig
  ): Try[(Input, Result)] = {
    val resultTry = phaseObj.apply(input, safeConfig, config)
    resultTry match {
      case Success(result) =>
        Success((input, result))
      case Failure(f) =>
        Failure[(Input, Result)](f)
    }
  }

}