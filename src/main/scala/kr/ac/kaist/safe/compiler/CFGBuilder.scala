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

package kr.ac.kaist.safe.compiler

import kr.ac.kaist.safe.Config
import kr.ac.kaist.safe.exceptions.StaticError
import kr.ac.kaist.safe.nodes.{ IRRoot, CFG }

// cfg builder
trait CFGBuilder {
  def build(ir: IRRoot, config: Config): (CFG, List[StaticError])
}
