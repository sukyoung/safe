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

// TODO Array
object BuiltinArray extends FuncModel(
  name = "Array",
  props = List(),
  code = EmptyCode(argLen = 1),
  hasConstruct = T,
  protoModel = Some((BuiltinArrayProto, F, F, F))
)

object BuiltinArrayProto extends ObjModel(
  name = "Array.prototype",
  props = List(
    ("@class", PrimModel("Array"), F, F, F)
  )
)
