/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
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
    val pvalue: AbsPValue
) extends Model {
  def init(h: AbsHeap, cfg: CFG): (AbsHeap, AbsValue) = (h, AbsValue(pvalue))
}

object PrimModel {
  def apply(pvalue: AbsPValue): PrimModel = new PrimModel(pvalue)
  def apply(n: Double): PrimModel = PrimModel(AbsPValue(n))
  def apply(str: String): PrimModel = PrimModel(AbsPValue(str))
  def apply(undef: Undef): PrimModel = PrimModel(AbsPValue(Undef))
  def apply(x: Null): PrimModel = PrimModel(AbsPValue(x))
  def apply(b: Boolean): PrimModel = PrimModel(AbsPValue(b))
}
