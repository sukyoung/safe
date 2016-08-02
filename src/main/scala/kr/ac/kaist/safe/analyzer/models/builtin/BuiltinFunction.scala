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
  "Function",
  EmptyCode,
  ("length", PrimModel(1), F, F, F) :: Nil,
  ("@class", PrimModel("Function"), F, F, F) ::
    ("length", PrimModel(0), F, F, F) :: Nil
) with Builtin
