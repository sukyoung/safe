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

import kr.ac.kaist.safe.analyzer.domain.{ Heap, Utils }
import kr.ac.kaist.safe.analyzer.models.Model
import kr.ac.kaist.safe.nodes.cfg.CFG

abstract class BuiltinModel extends Model

object BuiltinModel {
  val models: List[BuiltinModel] =
    BuiltinGlobal :: BuiltinArray :: BuiltinFunction ::
      BuiltinObject :: BuiltinBoolean :: BuiltinNumber :: BuiltinString ::
      BuiltinError :: BuiltinMath :: Nil
}
