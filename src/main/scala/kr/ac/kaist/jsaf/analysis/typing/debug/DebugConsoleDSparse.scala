/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug

import kr.ac.kaist.jsaf.analysis.cfg._
import scala.collection.mutable.{HashMap => MHashMap}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.debug.commands._

class DebugConsoleDSparse(cfg: CFG, worklist: Worklist, sem: Semantics, table: Table, env: DSparseEnv)
  extends DebugConsole(cfg, worklist, sem, table) {

  def getEnv = env

  /**
   * Initialize
   */
  override def initialize() = {
    register(List(new CmdHelp, new CmdNext, new CmdJump, new CmdPrint, new CmdHome, new CmdMove, new CmdPrintResult, new CmdDU))
    updateCompletor()
    runCmd("help", Array[String]())
  }
}

object DebugConsoleDSparse {
  /**
   * Singleton object
   */
  var console: DebugConsoleDSparse = null

  def initialize(cfg: CFG, worklist: Worklist, sem: Semantics, table: Table, env: DSparseEnv) = {
    console = new DebugConsoleDSparse(cfg, worklist, sem, table, env)
    console.initialize()
  }

  def runFixpoint(count: Int) = console.runFixpoint(count)
  def runFinished() = {
    console.target = -1
    console.runFixpoint(console.iter)
  }
}
