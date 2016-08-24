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
  props = List(),
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinDateProto, F, F, F))
)

object BuiltinDateProto extends ObjModel(
  name = "Date.prototype",
  props = List(
    ("@class", PrimModel("Date"), F, F, F)
  )
)
