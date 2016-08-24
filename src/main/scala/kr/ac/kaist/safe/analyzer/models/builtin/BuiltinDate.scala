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

import kr.ac.kaist.safe.analyzer.models.{ PrimModel, FuncModel, EmptyCode }

// TODO Date
object BuiltinDate extends FuncModel(
  name = "Date",
  props = List(),
  protoProps = List(
    ("@class", PrimModel("Date"), F, F, F)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin
