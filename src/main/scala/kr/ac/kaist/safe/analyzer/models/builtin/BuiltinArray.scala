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

// TODO Array
object BuiltinArray extends FuncModel(
  name = "Array",
  props = List(),
  protoProps = List(
    ("@class", PrimModel("Array"), F, F, F)
  ),
  prototypeWritable = F,
  argLen = 1,
  code = EmptyCode
) with Builtin
