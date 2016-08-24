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

object BuiltinFunction extends FuncModel(
  name = "Function",
  protoProps = List(
    ("@class", PrimModel("Function"), F, F, F),
    ("length", PrimModel(0), F, F, F)
  ),
  prototypeWritable = F,
  argLen = 1,
  // TODO @function
  code = EmptyCode
) with Builtin
