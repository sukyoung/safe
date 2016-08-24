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

    ("eval", BuiltinFuncModel(
      name = "Global.eval",
      argLen = 1,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("parseInt", BuiltinFuncModel(
      name = "Global.parseInt",
      argLen = 2,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("parseFloat", BuiltinFuncModel(
      name = "Global.parseFloat",
      argLen = 1,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("isNaN", BuiltinFuncModel(
      name = "Global.isNaN",
      argLen = 1,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("isFinite", BuiltinFuncModel(
      name = "Global.isFinite",
      argLen = 1,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("decodeURI", BuiltinFuncModel(
      name = "Global.decodeURI",
      argLen = 1,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("decodeURIComponent", BuiltinFuncModel(
      name = "Global.decodeURIComponent",
      argLen = 1,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("encodeURI", BuiltinFuncModel(
      name = "Global.encodeURI",
      argLen = 1,
      // TODO code
      code = EmptyCode
    ), T, F, T),

    ("encodeURIComponent", BuiltinFuncModel(
      name = "Global.encodeURIComponent",
      argLen = 1,
      // TODO code
      code = EmptyCode
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
) with Builtin
