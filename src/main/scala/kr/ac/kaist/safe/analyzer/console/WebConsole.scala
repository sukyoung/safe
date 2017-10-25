/*
 * ****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ***************************************************************************
 */

package kr.ac.kaist.safe.analyzer.console

import kr.ac.kaist.safe.analyzer.{ Semantics, Worklist }
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.phase.HeapBuildConfig

class WebConsole(
    override val cfg: CFG,
    override val worklist: Worklist,
    override val sem: Semantics,
    override val config: HeapBuildConfig,
    var iter0: Int
) extends Interactive {
  iter = iter0
  init()

  private val sb: StringBuilder = new StringBuilder()

  ////////////////////////////////////////////////////////////////
  // API
  ////////////////////////////////////////////////////////////////

  override def runFixpoint(): Unit = {
    prepareToRunFixpoint
  }

  override def moveCurCP(block: CFGBlock): Unit = {
    throw new NotImplementedError
  }

  private def init(): Unit = {
    runFixpoint()
  }
}
