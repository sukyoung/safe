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
  props = List(),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinErrorProto, F, F, F))
)

object BuiltinErrorProto extends ObjModel(
  name = "Error.prototype",
  props = List(
    ("name", PrimModel("Error"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  )
)

// TODO EvalError
object BuiltinEvalError extends FuncModel(
  name = "EvalError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("EvalError"), T, F, T)
  ),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinEvalErrorProto, F, F, F))
)

object BuiltinEvalErrorProto extends ObjModel(
  name = "EvalError.prototype",
  props = List(
    ("@proto", BuiltinErrorProto, F, F, F),
    ("name", PrimModel("EvalError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  )
)

// TODO RangeError
object BuiltinRangeError extends FuncModel(
  name = "RangeError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("RangeError"), T, F, T)
  ),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinRangeErrorProto, F, F, F))
)

object BuiltinRangeErrorProto extends ObjModel(
  name = "RangeError.prototype",
  props = List(
    ("@proto", BuiltinErrorProto, F, F, F),
    ("name", PrimModel("RangeError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  )
)

// TODO ReferenceError
object BuiltinReferenceError extends FuncModel(
  name = "ReferenceError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("ReferenceError"), T, F, T)
  ),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinReferenceErrorProto, F, F, F))
)

object BuiltinReferenceErrorProto extends ObjModel(
  name = "ReferenceError.prototype",
  props = List(
    ("@proto", BuiltinErrorProto, F, F, F),
    ("name", PrimModel("ReferenceError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  )
)

// TODO SyntaxError
object BuiltinSyntaxError extends FuncModel(
  name = "SyntaxError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("SyntaxError"), T, F, T)
  ),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinSyntaxErrorProto, F, F, F))
)

object BuiltinSyntaxErrorProto extends ObjModel(
  name = "SyntaxError.prototype",
  props = List(
    ("@proto", BuiltinErrorProto, F, F, F),
    ("name", PrimModel("SyntaxError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  )
)

// TODO TypeError
object BuiltinTypeError extends FuncModel(
  name = "TypeError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("TypeError"), T, F, T)
  ),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinTypeErrorProto, F, F, F))
)

object BuiltinTypeErrorProto extends ObjModel(
  name = "TypeError.prototype",
  props = List(
    ("@proto", BuiltinErrorProto, F, F, F),
    ("name", PrimModel("TypeError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  )
)

// TODO URIError
object BuiltinURIError extends FuncModel(
  name = "URIError",
  props = List(
    ("@proto", BuiltinError, F, F, F),
    ("name", PrimModel("URIError"), T, F, T)
  ),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinURIErrorProto, F, F, F))
)

object BuiltinURIErrorProto extends ObjModel(
  name = "URIError.prototype",
  props = List(
    ("@proto", BuiltinErrorProto, F, F, F),
    ("name", PrimModel("URIError"), T, F, T),
    ("message", PrimModel(""), T, F, T)
  )
)
