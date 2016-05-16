/**
 * *****************************************************************************
 * Copyright (c) 2012-2014, S-Core, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

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
}