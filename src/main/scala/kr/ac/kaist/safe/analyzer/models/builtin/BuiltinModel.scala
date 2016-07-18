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

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.analyzer.domain.{ Heap, Utils }

abstract class BuiltinModel {
  def initHeap(h: Heap, utils: Utils): Heap
}

object BuiltinModel {
  val models: List[BuiltinModel] =
    BuiltinGlobal :: BuiltinArray :: BuiltinFunction ::
      BuiltinObject :: BuiltinBoolean :: BuiltinNumber :: BuiltinString ::
      BuiltinError :: Nil
}