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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.util.{ Address, Loc, Old, Recent }

case class State(heap: Heap, context: Context) {
  /* partial order */
  def <=(that: State): Boolean = {
    this.heap <= that.heap && this.context <= that.context
  }

  /* not a partial order */
  def </(that: State): Boolean = {
    !(this.heap <= that.heap) || !(this.context <= that.context)
  }

  /* join */
  def +(that: State): State = {
    new State(this.heap + that.heap, this.context + that.context)
  }

  /* meet */
  def <>(that: State): State = {
    new State(this.heap <> that.heap, this.context <> that.context)
  }

  def raiseException(excSet: Set[Exception])(utils: Utils): State = {
    if (excSet.isEmpty) State.Bot
    else {
      val localLoc = PredefLoc.SINGLE_PURE_LOCAL
      val localObj = heap.getOrElse(localLoc, Obj.Bot(utils))
      val oldValue = localObj.getOrElse("@exception_all")(utils.value.Bot) { _.objval.value }
      val newExcSet = excSet.foldLeft(LocSetEmpty)((locSet, exc) => locSet + exc.getLoc)
      val excValue = Value(utils.pvalue.Bot, newExcSet)
      val newExcObjV = ObjectValue(excValue)(utils)
      val newExcSetObjV = ObjectValue(excValue + oldValue)(utils)
      val h1 = heap.update(
        localLoc,
        localObj.update("@exception", PropValue(newExcObjV)).
          update("@exception_all", PropValue(newExcSetObjV))
      )
      State(h1, context)
    }
  }

  def oldify(addr: Address)(utils: Utils): State = {
    if (context.isBottom) State.Bot
    else {
      val locR = Loc(addr, Recent)
      val locO = Loc(addr, Old)
      val h1 =
        if (heap domIn locR)
          heap.update(locO, heap.getOrElse(locR, Obj.Bot(utils))).remove(locR).subsLoc(locR, locO)
        else
          heap.subsLoc(locR, locO)
      val ctx1 = context.subsLoc(locR, locO)
      State(h1, ctx1)
    }
  }
}

object State {
  val Bot: State = State(Heap.Bot, Context.Bot)
}
