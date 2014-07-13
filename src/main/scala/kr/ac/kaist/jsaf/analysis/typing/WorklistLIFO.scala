/*******************************************************************************
    Copyright (c) 2012-2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.mutable.{ LinkedHashSet => MLinkedHashSet }
import kr.ac.kaist.jsaf.analysis.lib.WorkTreeSet

class WorklistLIFO extends Worklist {
  private val worklist = new MLinkedHashSet[ControlPoint]

  def head: ControlPoint = this.synchronized { worklist.head }

  def isEmpty: Boolean = this.synchronized { worklist.isEmpty }

  def getSize: Int = this.synchronized { worklist.size }

  def getWorkList: WorkTreeSet = WorkTreeSet.Empty // TODO: return type does not match

  override def toString: String = {
    val str = new StringBuilder
    for(w <- worklist) str.append(w + "\n")
    str.toString()
  }

  protected def insertWork(work: OrderEntry): Unit = worklist.add(work._2)

  protected def removeHead: ControlPoint = {
    val cp = worklist.head
    worklist.remove(cp)
    cp
  }
}
