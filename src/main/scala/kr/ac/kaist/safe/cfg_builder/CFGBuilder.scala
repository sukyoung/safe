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

package kr.ac.kaist.safe.cfg_builder

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.nodes.IRRoot

// cfg builder
trait CFGBuilder {
  val cfg: CFG
  val excLog: ExcLog
}
