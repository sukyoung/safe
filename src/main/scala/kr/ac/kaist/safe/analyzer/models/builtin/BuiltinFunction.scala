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

// TODO Function
object BuiltinFunction extends FuncModel(
  name = "Function",
  // TODO @function
  code = EmptyCode(argLen = 1),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinFunctionProto, F, F, F))
)

object BuiltinFunctionProto extends FuncModel(
  name = "Function.prototype",
  // TODO @function
  code = EmptyCode(argLen = 0),
  props = List(
    ("@proto", BuiltinObjectProto, F, F, F),

    // TODO toString
    ("toString", FuncModel(
      name = "Function.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO apply
    ("apply", FuncModel(
      name = "Function.prototype.apply",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO call
    ("call", FuncModel(
      name = "Function.prototype.call",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO bind
    ("bind", FuncModel(
      name = "Function.prototype.bind",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
