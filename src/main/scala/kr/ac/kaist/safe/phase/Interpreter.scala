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

package main.scala.kr.ac.kaist.safe.phase

import kr.ac.kaist.safe.util.Coverage

import scala.util._
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.interpreter.InterpreterMain
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.phase._

object Interpreter extends PhaseObj[IRRoot, InterpreterConfig, Unit] {

  val name: String = "interpreter"
  val help: String = "Interprets JavaScript code."

  val defaultConfig: InterpreterConfig = InterpreterConfig(InterpreterModes.OTHER, false, false)

  def apply(input: IRRoot, safeConfig: SafeConfig, config: InterpreterConfig): Try[Unit] = {
    new InterpreterMain().doit(config, input, None[Coverage])
    Success()
  }

}

object InterpreterModes extends Enumeration {

  type Mode = Value
  val HTML, HTML_SPARSE, WEBAPP_BUG_DETECTOR, OTHER = Value

}

case class InterpreterConfig(mode: InterpreterModes.Mode, tizens: Boolean, jquery: Boolean) extends Config
