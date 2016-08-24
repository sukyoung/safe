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

// TODO String
object BuiltinString extends FuncModel(
  name = "String",
  props = List(),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinStringProto, F, F, F))
)

object BuiltinStringProto extends ObjModel(
  name = "String.prototype",
  props = List(
    ("@class", PrimModel("String"), F, F, F)
  )
)
