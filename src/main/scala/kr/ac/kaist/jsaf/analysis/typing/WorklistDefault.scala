/*******************************************************************************
    Copyright (c) 2012-2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.analysis.lib.WorkTreeSet

class WorklistDefault extends Worklist {
  private var worklist = WorkTreeSet.Empty

  def head: ControlPoint = this.synchronized { worklist.head._2 }

  def isEmpty: Boolean = this.synchronized { worklist.isEmpty }

  def getSize: Int = this.synchronized { worklist.size }

  def getWorkList: WorkTreeSet = worklist

  override def toString: String = {
    val str = new StringBuilder
    for(w <- worklist) str.append("[" + w._1 + "] " + w._2 + "\n")
    str.toString()
  }

  protected def insertWork(work: OrderEntry): Unit = worklist+= work

  protected def removeHead: ControlPoint = {
    val (head, tail) = worklist.headAndTail
    worklist = tail
    head._2
  }
}
