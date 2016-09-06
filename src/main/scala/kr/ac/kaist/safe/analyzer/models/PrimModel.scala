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
    val pvGen: PValue
) extends Model {
  def init(h: Heap, cfg: CFG): (Heap, Value) = (h, ValueUtil(pvGen))
}

object PrimModel {
  def apply(pvGen: PValue): PrimModel = new PrimModel(pvGen)
  def apply(n: Double): PrimModel = PrimModel(PValueUtil.alpha(n))
  def apply(str: String): PrimModel = PrimModel(PValueUtil.alpha(str))
  def apply(): PrimModel = PrimModel(PValueUtil.alpha())
  def apply(x: Null): PrimModel = PrimModel(PValueUtil.alpha(x))
  def apply(b: Boolean): PrimModel = PrimModel(PValueUtil.alpha(b))
}
