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

import kr.ac.kaist.safe.analyzer.domain.{ IClass, IPrototype }
import kr.ac.kaist.safe.analyzer.models._

object BuiltinError extends FuncModel(
  name = "Error",
  // TODO @function
  code = EmptyCode(1),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinErrorProto, F, F, F))
)

object BuiltinErrorProto extends ObjModel(
  name = "Error.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    NormalProp("name", PrimModel("Error"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "Error.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T)
  )
)

////////////////////////////////////////////////////////////////////////////////
// Native Errors
////////////////////////////////////////////////////////////////////////////////

// EvalError
object BuiltinEvalError extends FuncModel(
  name = "EvalError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("EvalError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinEvalErrorProto, F, F, F))
)

object BuiltinEvalErrorProto extends ObjModel(
  name = "EvalError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("EvalError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// RangeError
object BuiltinRangeError extends FuncModel(
  name = "RangeError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("RangeError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinRangeErrorProto, F, F, F))
)

object BuiltinRangeErrorProto extends ObjModel(
  name = "RangeError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("RangeError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// ReferenceError
object BuiltinReferenceError extends FuncModel(
  name = "ReferenceError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("ReferenceError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinReferenceErrorProto, F, F, F))
)

object BuiltinReferenceErrorProto extends ObjModel(
  name = "ReferenceError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("ReferenceError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// SyntaxError
object BuiltinSyntaxError extends FuncModel(
  name = "SyntaxError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("SyntaxError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinSyntaxErrorProto, F, F, F))
)

object BuiltinSyntaxErrorProto extends ObjModel(
  name = "SyntaxError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("SyntaxError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// TypeError
object BuiltinTypeError extends FuncModel(
  name = "TypeError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("TypeError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinTypeErrorProto, F, F, F))
)

object BuiltinTypeErrorProto extends ObjModel(
  name = "TypeError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("TypeError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)

// URIError
object BuiltinURIError extends FuncModel(
  name = "URIError",
  // TODO @function
  code = EmptyCode(1),
  props = List(
    NormalProp("name", PrimModel("URIError"), T, F, T)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinURIErrorProto, F, F, F))
)

object BuiltinURIErrorProto extends ObjModel(
  name = "URIError.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Error")),
    InternalProp(IPrototype, BuiltinErrorProto),
    NormalProp("name", PrimModel("URIError"), T, F, T),
    NormalProp("message", PrimModel(""), T, F, T)
  )
)
