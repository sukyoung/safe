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
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.nodes.cfg.CFG

// Primitive Model
class PrimModel(
    val pvalue: AbsPValue
) extends Model {
  def init(h: Heap, cfg: CFG): (Heap, AbsValue) = (h, AbsValue(pvalue))
}

object PrimModel {
  def apply(pvalue: AbsPValue): PrimModel = new PrimModel(pvalue)
  def apply(n: Double): PrimModel = PrimModel(AbsPValue.alpha(n))
  def apply(str: String): PrimModel = PrimModel(AbsPValue.alpha(str))
  def apply(undef: Undef): PrimModel = PrimModel(AbsPValue.alpha(Undef))
  def apply(x: Null): PrimModel = PrimModel(AbsPValue.alpha(x))
  def apply(b: Boolean): PrimModel = PrimModel(AbsPValue.alpha(b))
}
