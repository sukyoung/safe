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
import kr.ac.kaist.safe.nodes.CFGNode

trait ControlPoint {
  val node: CFGNode
  val callContext: CallContext
  def getState: State
  def setState(s: State): Unit
}

object ControlPoint {
  type CP = FlowSensitiveCP
  def apply(node: CFGNode, callContext: CallContext): ControlPoint = new CP(node, callContext)
}

case class FlowSensitiveCP(node: CFGNode, callContext: CallContext) extends ControlPoint {
  private var state: State = State.Bot
  def getState: State = this.state
  def setState(s: State): Unit = this.state = s
}

case class FlowInsensitiveCP(node: CFGNode, callContext: CallContext) extends ControlPoint {
  def getState: State = State.Bot /* Global State */
  def setState(s: State): Unit = {} /* Global State = s */
}