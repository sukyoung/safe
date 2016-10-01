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

// Model
abstract class Model {
  def init(h: Heap, cfg: CFG): (Heap, AbsValue)
}

sealed abstract class PropDesc
case class NormalProp(name: String, key: Model, w: Boolean, e: Boolean, c: Boolean) extends PropDesc
case class InternalProp(name: InternalName, key: Model) extends PropDesc
