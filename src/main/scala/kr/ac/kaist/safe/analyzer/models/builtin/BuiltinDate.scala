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

import kr.ac.kaist.safe.analyzer.models._

// TODO Date
object BuiltinDate extends FuncModel(
  name = "Date",
  // TODO @function
  code = EmptyCode(7),
  props = List(
    // TODO parse
    ("parse", FuncModel(
      name = "Date.parse",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO UTC
    ("UTC", FuncModel(
      name = "Date.UTC",
      code = EmptyCode(argLen = 7)
    ), T, F, T),

    // TODO now
    ("now", FuncModel(
      name = "Date.now",
      code = EmptyCode(argLen = 0)
    ), T, F, T)
  ),
  hasConstruct = T,
  protoModel = Some((BuiltinDateProto, F, F, F))
)

object BuiltinDateProto extends ObjModel(
  name = "Date.prototype",
  props = List(
    ("@class", PrimModel("Date"), F, F, F),
    ("@primitive", PrimModel(Double.NaN), F, F, F),

    // TODO toString
    ("toString", FuncModel(
      name = "Date.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toDateString
    ("toDateString", FuncModel(
      name = "Date.prototype.toDateString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toTimeString
    ("toTimeString", FuncModel(
      name = "Date.prototype.toTimeString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleString
    ("toLocaleString", FuncModel(
      name = "Date.prototype.toLocaleString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleDateString
    ("toLocaleDateString", FuncModel(
      name = "Date.prototype.toLocaleDateString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleTimeString
    ("toLocaleTimeString", FuncModel(
      name = "Date.prototype.toLocaleTimeString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO valueOf
    ("valueOf", FuncModel(
      name = "Date.prototype.valueOf",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getTime
    ("getTime", FuncModel(
      name = "Date.prototype.getTime",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getFullYear
    ("getFullYear", FuncModel(
      name = "Date.prototype.getFullYear",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCFullYear
    ("getUTCFullYear", FuncModel(
      name = "Date.prototype.getUTCFullYear",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getMonth
    ("getMonth", FuncModel(
      name = "Date.prototype.getMonth",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCMonth
    ("getUTCMonth", FuncModel(
      name = "Date.prototype.getUTCMonth",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getDate
    ("getDate", FuncModel(
      name = "Date.prototype.getDate",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCDate
    ("getUTCDate", FuncModel(
      name = "Date.prototype.getUTCDate",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getDay
    ("getDay", FuncModel(
      name = "Date.prototype.getDay",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCDay
    ("getUTCDay", FuncModel(
      name = "Date.prototype.getUTCDay",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getHours
    ("getHours", FuncModel(
      name = "Date.prototype.getUTCDay",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCHours
    ("getUTCHours", FuncModel(
      name = "Date.prototype.getUTCHours",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getMinutes
    ("getMinutes", FuncModel(
      name = "Date.prototype.getMinutes",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCMinutes
    ("getUTCMinutes", FuncModel(
      name = "Date.prototype.getUTCMinutes",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getSeconds
    ("getSeconds", FuncModel(
      name = "Date.prototype.getSeconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCSeconds
    ("getUTCSeconds", FuncModel(
      name = "Date.prototype.getUTCSeconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getMilliseconds
    ("getMilliseconds", FuncModel(
      name = "Date.prototype.getMilliseconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCMilliseconds
    ("getUTCMilliseconds", FuncModel(
      name = "Date.prototype.getUTCMilliseconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getTimezoneOffset
    ("getTimezoneOffset", FuncModel(
      name = "Date.prototype.getTimezoneOffset",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO setTime
    ("setTime", FuncModel(
      name = "Date.prototype.setTime",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setMilliseconds
    ("setMilliseconds", FuncModel(
      name = "Date.prototype.setMilliseconds",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setUTCMilliseconds
    ("setUTCMilliseconds", FuncModel(
      name = "Date.prototype.setUTCMilliseconds",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setSeconds
    ("setSeconds", FuncModel(
      name = "Date.prototype.setSeconds",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setUTCSeconds
    ("setUTCSeconds", FuncModel(
      name = "Date.prototype.setUTCSeconds",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setMinutes
    ("setMinutes", FuncModel(
      name = "Date.prototype.setMinutes",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO setUTCMinutes
    ("setUTCMinutes", FuncModel(
      name = "Date.prototype.setUTCMinutes",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO setHours
    ("setHours", FuncModel(
      name = "Date.prototype.setHours",
      code = EmptyCode(argLen = 4)
    ), T, F, T),

    // TODO setUTCHours
    ("setUTCHours", FuncModel(
      name = "Date.prototype.setUTCHours",
      code = EmptyCode(argLen = 4)
    ), T, F, T),

    // TODO setDate
    ("setDate", FuncModel(
      name = "Date.prototype.setDate",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setUTCDate
    ("setUTCDate", FuncModel(
      name = "Date.prototype.setUTCDate",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setMonth
    ("setMonth", FuncModel(
      name = "Date.prototype.setMonth",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setUTCMonth
    ("setUTCMonth", FuncModel(
      name = "Date.prototype.setUTCMonth",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setFullYear
    ("setFullYear", FuncModel(
      name = "Date.prototype.setFullYear",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO setUTCFullYear
    ("setUTCFullYear", FuncModel(
      name = "Date.prototype.setUTCFullYear",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO toUTCString
    ("toUTCString", FuncModel(
      name = "Date.prototype.toUTCString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toISOString
    ("toISOString", FuncModel(
      name = "Date.prototype.toISOString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toJSON
    ("toJSON", FuncModel(
      name = "Date.prototype.toJSON",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
