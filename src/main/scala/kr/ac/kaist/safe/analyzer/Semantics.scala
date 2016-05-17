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
import kr.ac.kaist.safe.cfg_builder.CFG
import kr.ac.kaist.safe.nodes.{ CFGExpr, CFGInst, CFGNode }

class Semantics(cfg: CFG, utils: Utils) {
  val helper: Helper = Helper(utils)

  def E(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj, s: State): State = {
    State.Bot
  }

  def C(cp: ControlPoint, cmd: CFGNode, s: State): (State, State) = {
    (State.Bot, State.Bot)
  }

  def I(cp: ControlPoint, i: CFGInst, s: State, se: State): (State, State) = {
    (State.Bot, State.Bot)
  }

  def B(expr: CFGExpr, s: State, se: State, inst: CFGInst, cfg: CFG, cp: ControlPoint): (State, State) = {
    (State.Bot, State.Bot)
  }
}