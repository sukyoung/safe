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
  props = List(),
  // TODO @function
  code = EmptyCode(argLen = 1),
  hasConstruct = T,
  protoModel = Some((BuiltinFunctionProto, F, F, F))
)

object BuiltinFunctionProto extends ObjModel(
  name = "Function.prototype",
  props = List(
    ("@class", PrimModel("Function"), F, F, F),
    ("length", PrimModel(0), F, F, F)
  )
)
