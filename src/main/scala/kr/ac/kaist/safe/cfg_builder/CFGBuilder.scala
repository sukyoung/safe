/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.cfg_builder

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.phase.CFGBuildConfig
import kr.ac.kaist.safe.SafeConfig

// cfg builder
abstract class CFGBuilder(
    ir: IRRoot,
    safeConfig: SafeConfig,
    config: CFGBuildConfig
) {
  val cfg: CFG
  val excLog: ExcLog
}
