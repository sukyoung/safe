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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain.Loc

package object model {
  ////////////////////////////////////////////////////////////////
  // pre-defined allocation sites (for heap)
  ////////////////////////////////////////////////////////////////
  // 15.1 The Global Object
  lazy val GLOBAL_LOC: Loc = Loc("Global")

  // 15.2 Object Objects
  lazy val OBJ_LOC: Loc = Loc("Object")
  lazy val OBJ_PROTO_LOC: Loc = Loc("Object.prototype")

  // 15.3 Function Objects
  lazy val FUNC_LOC: Loc = Loc("Function")
  lazy val FUNC_PROTO_LOC: Loc = Loc("Function.prototype")

  // 15.4 Array Objects
  lazy val ARR_LOC: Loc = Loc("Array")
  lazy val ARR_PROTO_LOC: Loc = Loc("Array.prototype")

  // 15.5 String Objects
  lazy val STR_LOC: Loc = Loc("String")
  lazy val STR_PROTO_LOC: Loc = Loc("String.prototype")

  // 15.6 Boolean Objects
  lazy val BOOL_LOC: Loc = Loc("Boolean")
  lazy val BOOL_PROTO_LOC: Loc = Loc("Boolean.prototype")

  // 15.7 Number Objects
  lazy val NUM_LOC: Loc = Loc("Number")
  lazy val NUM_PROTO_LOC: Loc = Loc("Number.prototype")

  // 15.8 The Math Object
  lazy val MATH_LOC: Loc = Loc("Math")

  // 15.9 Date Objects
  lazy val DATE_LOC: Loc = Loc("Date")

  // 15.10 RegExp (Regular Expression) Objects
  lazy val REG_EXP_LOC: Loc = Loc("RegExp")

  // 15.11 Error Objects
  lazy val ERROR_LOC: Loc = Loc("Error")
  lazy val ERROR_PROTO_LOC: Loc = Loc("Error.prototype")

  // 15.11.6 Native Error Types Used in This Standard
  lazy val EVAL_ERROR_LOC: Loc = Loc("EvalError")
  lazy val EVAL_ERROR_PROTO_LOC: Loc = Loc("EvalError.prototype")
  lazy val RANGE_ERROR_LOC: Loc = Loc("RangeError")
  lazy val RANGE_ERROR_PROTO_LOC: Loc = Loc("RangeError.prototype")
  lazy val REF_ERROR_LOC: Loc = Loc("ReferenceError")
  lazy val REF_ERROR_PROTO_LOC: Loc = Loc("ReferenceError.prototype")
  lazy val SYNTAX_ERROR_LOC: Loc = Loc("SyntaxError")
  lazy val SYNTAX_ERROR_PROTO_LOC: Loc = Loc("SyntaxError.prototype")
  lazy val TYPE_ERROR_LOC: Loc = Loc("TypeError")
  lazy val TYPE_ERROR_PROTO_LOC: Loc = Loc("TypeError.prototype")
  lazy val URI_ERROR_LOC: Loc = Loc("URIError")
  lazy val URI_ERROR_PROTO_LOC: Loc = Loc("URIError.prototype")

  ////////////////////////////////////////////////////////////////
  // pre-defined allocation sites (for context)
  ////////////////////////////////////////////////////////////////
  // global environment
  lazy val GLOBAL_ENV: Loc = Loc("GlobalEnv")

  // pure local environment
  lazy val PURE_LOCAL: Loc = Loc("PureLocal")

  // collapsed environment for try-catch
  lazy val COLLAPSED: Loc = Loc("Collapsed")
}
