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

package kr.ac.kaist

import java.io.File
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

package object safe {
  // Line seperator
  val LINE_SEP = System.getProperty("line.separator")

  // Path seperator
  val SEP = File.separator

  // Number of significant bits
  val SIGNIFICANT_BITS = 13

  // Maximum length of printable instruction of CFGBlock
  val MAX_INST_PRINT_SIZE = 10000

  // Base project directory root
  val BASE_DIR = System.getenv("SAFE_HOME")

  // Base project directory root
  val CUR_DIR = System.getProperty("user.dir")

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
    NU.VAR_TRUE,
    NU.VAR_ONE,
    NU.GLOBAL_NAME
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

  // Global names for DOM
  val DOM_NAMES = List(
    "window",
    "document"
  )
}
