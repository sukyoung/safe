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

import kr.ac.kaist.safe.analyzer.Semantics
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.phase.HeapBuildConfig

class WebConsole(
    override val cfg: CFG,
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

  override def runFixpoint: Unit = {
    prepareToRunFixpoint
  }

  override def moveCurCP(block: CFGBlock): Unit = {
    throw new NotImplementedError
  }

  override def getPrompt: String = {
    val block = cur.block
    val func = block.func.simpleName
    val span = block.span
    val tp = cur.tracePartition
    s"<$func: $block, $tp> @${span.toString}"
  }

  ////////////////////////////////////////////////////////////////
  // Private Methods
  ////////////////////////////////////////////////////////////////

  private def init(): Unit = {
    runFixpoint
  }
}
