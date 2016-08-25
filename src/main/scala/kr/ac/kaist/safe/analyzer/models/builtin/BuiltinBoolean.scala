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

// TODO Boolean
object BuiltinBoolean extends FuncModel(
  name = "Boolean",
  props = List(),
  code = EmptyCode(1),
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinBooleanProto, F, F, F))
)

object BuiltinBooleanProto extends ObjModel(
  name = "Boolean.prototype",
  props = List(
    ("@class", PrimModel("Boolean"), F, F, F)
  )
)
