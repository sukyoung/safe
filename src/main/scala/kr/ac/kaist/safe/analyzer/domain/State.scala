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

case class State(heap: Heap) {
  /* partial order */
  def <=(that: State): Boolean = this.heap <= that.heap

  /* not a partial order */
  def </(that: State): Boolean = !(this.heap <= that.heap)

  /* join */
  def +(that: State): State = new State(this.heap + that.heap)

  /* meet */
  def <>(that: State): State = new State(this.heap <> that.heap)

  def raiseException(excSet: Set[Exception])(utils: Utils): State = {
    if (excSet.isEmpty) State.Bot
    else {
      val localLoc = PredefLoc.SINGLE_PURE_LOCAL
      val localObj = heap.getOrElse(localLoc, Obj.Bot(utils))
      val oldValue = localObj.getOrElse("@exception_all")(utils.value.Bot) { _.objval.value }
      val newExcSet = excSet.foldLeft(LocSetEmpty)((locSet, exc) => locSet + exc.getLoc)
      val excValue = Value(utils.pvalue.Bot, newExcSet)
      val newExcObjV = utils.dataProp(excValue)
      val newExcSetObjV = utils.dataProp(excValue + oldValue)
      val h1 = heap.update(
        localLoc,
        localObj.update("@exception", PropValue(newExcObjV)).
          update("@exception_all", PropValue(newExcSetObjV))
      )
      State(h1)
    }
  }

  def oldify(addr: Address)(utils: Utils): State = {
    if (heap.old.isBottom) State.Bot
    else {
      val locR = Loc(addr, Recent)
      val locO = Loc(addr, Old)
      val h1 =
        if (heap domIn locR)
          heap.update(locO, heap.getOrElse(locR, Obj.Bot(utils))).remove(locR).subsLoc(locR, locO)
        else
          heap.subsLoc(locR, locO)
      State(h1)
    }
  }
}

object State {
  val Bot: State = State(Heap.Bot)
}
