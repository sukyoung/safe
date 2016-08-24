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

//TODO Error
object BuiltinError extends FuncModel(
  name = "Error",
  props = Nil,
  protoProps = List(
    ("name", PrimModel("Error"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin

// TODO EvalError
object BuiltinEvalError extends FuncModel(
  name = "EvalError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("EvalError"), T, F, T)
  ),
  protoProps = List(
    ("@proto", BuiltinError.protoModel, F, F, F),
    ("name", PrimModel("EvalError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin

// TODO RangeError
object BuiltinRangeError extends FuncModel(
  name = "RangeError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("RangeError"), T, F, T)
  ),
  protoProps = List(
    ("@proto", BuiltinError.protoModel, F, F, F),
    ("name", PrimModel("RangeError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin

// TODO ReferenceError
object BuiltinReferenceError extends FuncModel(
  name = "ReferenceError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("ReferenceError"), T, F, T)
  ),
  protoProps = List(
    ("@proto", BuiltinError.protoModel, F, F, F),
    ("name", PrimModel("ReferenceError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin

// TODO SyntaxError
object BuiltinSyntaxError extends FuncModel(
  name = "SyntaxError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("SyntaxError"), T, F, T)
  ),
  protoProps = List(
    ("@proto", BuiltinError.protoModel, F, F, F),
    ("name", PrimModel("SyntaxError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin

// TODO TypeError
object BuiltinTypeError extends FuncModel(
  name = "TypeError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("TypeError"), T, F, T)
  ),
  protoProps = List(
    ("@proto", BuiltinError.protoModel, F, F, F),
    ("name", PrimModel("TypeError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin

// TODO URIError
object BuiltinURIError extends FuncModel(
  name = "URIError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("URIError"), T, F, T)
  ),
  protoProps = List(
    ("@proto", BuiltinError.protoModel, F, F, F),
    ("name", PrimModel("URIError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin
