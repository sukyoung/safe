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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg.CFGBlock

trait ControlPoint {
  val node: CFGBlock
  val callContext: CallContext
  def getState: State
  def setState(s: State): Unit
}

object ControlPoint {
  type CP = FlowSensitiveCP
  def apply(node: CFGBlock, callContext: CallContext): ControlPoint = new CP(node, callContext)
}

case class FlowSensitiveCP(node: CFGBlock, callContext: CallContext) extends ControlPoint {
  def getState: State = this.node.getState(this.callContext)
  def setState(st: State): Unit = this.node.setState(this.callContext, st)
}

case class FlowInsensitiveCP(node: CFGBlock, callContext: CallContext) extends ControlPoint {
  def getState: State = State.Bot /* Global State */
  def setState(s: State): Unit = {} /* Global State = s */
}
