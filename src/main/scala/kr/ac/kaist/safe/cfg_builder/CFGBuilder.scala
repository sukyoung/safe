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
import kr.ac.kaist.safe.nodes.{ IRRoot, CFG }
import kr.ac.kaist.safe.phase.CFGBuildConfig
import kr.ac.kaist.safe.config.Config

// cfg builder
abstract class CFGBuilder(
    ir: IRRoot,
    config: Config,
    cfgConfig: CFGBuildConfig
) {
  val cfg: CFG
  val excLog: ExcLog
}
