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
    (h, utils.value(pvGen(utils)))
}

object PrimModel {
  def apply(pvGen: Utils => PValue): PrimModel = new PrimModel(pvGen)
  def apply(n: Double): PrimModel = PrimModel(_.pvalue.alpha(n))
  def apply(str: String): PrimModel = PrimModel(_.pvalue.alpha(str))
  def apply(): PrimModel = PrimModel(_.pvalue.alpha())
  def apply(x: Null): PrimModel = PrimModel(_.pvalue.alpha(x))
  def apply(b: Boolean): PrimModel = PrimModel(_.pvalue.alpha(b))
}
