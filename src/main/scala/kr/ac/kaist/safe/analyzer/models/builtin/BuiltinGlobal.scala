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

package kr.ac.kaist.safe.analyzer.models.builtin

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.NodeUtil

object BuiltinGlobal extends ObjModel(
  name = "Global",
  props = List(
    ("NaN", PrimModel(Double.NaN), F, F, F),
    ("Infinity", PrimModel(Double.PositiveInfinity), F, F, F),
    ("undefined", PrimModel(), F, F, F),

    ("eval", FuncModel(
      name = "Global.eval",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("parseInt", FuncModel(
      name = "Global.parseInt",
      // TODO code
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    ("parseFloat", FuncModel(
      name = "Global.parseFloat",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("isNaN", FuncModel(
      name = "Global.isNaN",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("isFinite", FuncModel(
      name = "Global.isFinite",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("decodeURI", FuncModel(
      name = "Global.decodeURI",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("decodeURIComponent", FuncModel(
      name = "Global.decodeURIComponent",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("encodeURI", FuncModel(
      name = "Global.encodeURI",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("encodeURIComponent", FuncModel(
      name = "Global.encodeURIComponent",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    ("Object", BuiltinObject, T, F, T),
    ("Function", BuiltinFunction, T, F, T),
    ("Array", BuiltinArray, T, F, T),
    ("String", BuiltinString, T, F, T),
    ("Boolean", BuiltinBoolean, T, F, T),
    ("Number", BuiltinNumber, T, F, T),
    ("Date", BuiltinDate, T, F, T),
    ("RegExp", BuiltinRegExp, T, F, T),
    ("Error", BuiltinError, T, F, T),
    ("EvalError", BuiltinEvalError, T, F, T),
    ("RangeError", BuiltinRangeError, T, F, T),
    ("ReferenceError", BuiltinReferenceError, T, F, T),
    ("SyntaxError", BuiltinSyntaxError, T, F, T),
    ("TypeError", BuiltinTypeError, T, F, T),
    ("URIError", BuiltinURIError, T, F, T),
    ("Math", BuiltinMath, T, F, T),
    ("JSON", BuiltinJSON, T, F, T),
    (NodeUtil.GLOBAL_NAME, SelfModel, F, F, F),
    (NodeUtil.VAR_TRUE, PrimModel(true), F, F, F)
  )
)
