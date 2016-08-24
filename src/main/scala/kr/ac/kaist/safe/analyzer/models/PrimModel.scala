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

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg.CFG

// Primitive Model
class PrimModel(
    val pvGen: Utils => PValue
) extends Model {
  def init(h: Heap, cfg: CFG, utils: Utils): (Heap, Value) =
    (h, Value(pvGen(utils)))
}

object PrimModel {
  def apply(pvGen: Utils => PValue): PrimModel = new PrimModel(pvGen)
  def apply(n: Double): PrimModel =
    PrimModel(utils => PValue(utils.absNumber.alpha(n))(utils))
  def apply(str: String): PrimModel =
    PrimModel(utils => PValue(utils.absString.alpha(str))(utils))
  def apply(): PrimModel =
    PrimModel(utils => PValue(utils.absUndef.alpha)(utils))
  def apply(x: Null): PrimModel =
    PrimModel(utils => PValue(utils.absNull.alpha)(utils))
  def apply(b: Boolean): PrimModel =
    PrimModel(utils => PValue(utils.absBool.alpha(b))(utils))
}
