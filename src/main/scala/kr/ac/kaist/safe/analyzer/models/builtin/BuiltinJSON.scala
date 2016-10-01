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

import kr.ac.kaist.safe.analyzer.domain.IClass
import kr.ac.kaist.safe.analyzer.models._

// TODO JSON
object BuiltinJSON extends ObjModel(
  name = "JSON",
  props = List(
    InternalProp(IClass, PrimModel("JSON")),

    // TODO parse
    NormalProp("parse", FuncModel(
      name = "JSON.parse",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO stringify
    NormalProp("stringify", FuncModel(
      name = "JSON.stringify",
      code = EmptyCode(argLen = 3)
    ), T, F, T)
  )
)
