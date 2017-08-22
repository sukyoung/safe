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
import kr.ac.kaist.safe.interpreter.Interpreter
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{ BoolOption, StrOption }

object Interpret extends PhaseObj[IRRoot, InterpretConfig, Unit] {

  val name: String = "interpreter"
  val help: String = "Interprets JavaScript code."

  val defaultConfig: InterpretConfig = InterpretConfig(InterpreterModes.OTHER, false, false)

  val options: List[PhaseOption[InterpretConfig]] = List(
    ("mode", StrOption((c: InterpretConfig, s) => c.mode = s match {
      case "html" | "HTML" => InterpreterModes.HTML
      case "html-sparse" => InterpreterModes.HTML_SPARSE
      case "webapp-bug-detector" => InterpreterModes.WEBAPP_BUG_DETECTOR
      case _ => InterpreterModes.OTHER
    }),
      "The mode to interpret the program in: html, html-sparse, webapp-bug-detector. Currently not supported."),
    ("tizen", BoolOption((c: InterpretConfig) => c.tizens = true),
      "Interpret a Tizen application. Currently not supported."),
    ("jquery", BoolOption((c: InterpretConfig) => c.jquery = true),
      "Currently not supported."),
    ("ecma", BoolOption((c: InterpretConfig) => c.ECMASpecTest = true),
      "Verify the interpreter's correctness by running ECMA spec tests.")
  )

  def apply(input: IRRoot, safeConfig: SafeConfig, config: InterpretConfig): Try[Unit] = {
    val interpreter = new Interpreter(config, safeConfig)
    Success(interpreter.doit(input, None))
  }

}

object InterpreterModes extends Enumeration {

  type Mode = Value
  val HTML, HTML_SPARSE, WEBAPP_BUG_DETECTOR, OTHER = Value

}

case class InterpretConfig(
  var mode: InterpreterModes.Mode = InterpreterModes.OTHER,
  var tizens: Boolean = false,
  var jquery: Boolean = false,
  var ECMASpecTest: Boolean = false
) extends Config
