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

package kr.ac.kaist.safe.config

import kr.ac.kaist.safe.util.{ NodeUtil => NU, AddressManager, DefaultAddressManager, Useful }

case class Config(
    var command: Command,
    var fileNames: List[String] = Nil,
    var time: Boolean = false,
    var verbose: Boolean = false,
    var addrManager: AddressManager = new DefaultAddressManager
) extends ConfigOption {
  val prefix: String = ""
  val optMap: Map[String, OptionKind] = Map(
    "time" -> BoolOption(() => time = true),
    "verbose" -> BoolOption(() => verbose = true)
  )
}

object Config {
  // Maximum length of printable instruction of CFGNode
  val MAX_INST_PRINT_SIZE = 10000

  // Base project directory root
  val BASE_DIR = System.getenv("SAFE_HOME") match {
    case null | "" => throw new Error("Could not find SAFE_HOME.")
    case s => s
  }

  // Predefined variables
  val PRED_VARS = List(
    // 4.2 Language Overview
    "Object",
    "Function",
    "Array",
    "String",
    "Boolean",
    "Number",
    "Math",
    "Date",
    "RegExp",
    "JSON",
    "Error",
    "EvalError",
    "RangeError",
    "ReferenceError",
    "SyntaxError",
    "TypeError",
    "URIError",
    // 15.1.1 Value Properties of the Global Object
    "NaN",
    "Infinity",
    "undefined",
    // predefined constant variables from IR
    NU.varTrue,
    NU.varOne,
    NU.freshGlobalName("global")
  )

  // Predefined functions
  val PRED_FUNS = List(
    // 15.1.2 Function Properties of the Global Object
    "eval",
    "parseInt",
    "parseFloat",
    "isNaN",
    "isFinite",
    // 15.1.3 URI Handling Function Properties
    "decodeURI",
    "decodeURIComponent",
    "encodeURI",
    "encodeURIComponent"
  )

  // All predefined variables and functions
  val PRED_ALL = PRED_VARS ++ PRED_FUNS
}
