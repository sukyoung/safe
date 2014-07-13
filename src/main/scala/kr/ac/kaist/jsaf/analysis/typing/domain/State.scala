/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

case class State(heap: Heap, context: Context) {
  /* tuple-like accessor */
  val _1 = heap
  val _2 = context

  /* partial order */
  def <= (that: State) = {
    this.heap <= that.heap && this.context <= that.context
  }

  /* not a partial order */
  def </ (that: State) = {
    !(this.heap <= that.heap) || !(this.context <= that.context)
  }

  /* join */
  def + (that: State) = {
    new State(this.heap + that.heap, this.context + that.context)
  }

  /* meet */
  def <> (that: State) = {
    new State(this.heap <> that.heap, this.context <> that.context)
  }

  def restrict(set: LPSet) = {
    State(this.heap.restrict(set), this.context.restrict(set))
  }

  def restrict(set: LocSet) = {
    State(this.heap.restrict(set), this.context.restrict(set))
  }

  /* for temporal pre-analysis result, make all the properties absentTop. */
  def absentTop() = {
    State(this.heap.absentTop(), this.context)
  }
}
