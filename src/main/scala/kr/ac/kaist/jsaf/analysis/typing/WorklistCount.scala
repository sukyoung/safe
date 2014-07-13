/*******************************************************************************
    Copyright (c) 2012-2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.mutable.{ HashMap => MHashMap, LinkedHashSet => MLinkedHashSet }
import scala.collection.immutable.{ SortedSet => ISortedSet }
import kr.ac.kaist.jsaf.analysis.lib.WorkTreeSet

class WorklistCount extends Worklist {
  private var analyzedCountSet = ISortedSet[Int]()
  private val analyzedCountMap1 = new MHashMap[ControlPoint, Int] // control point -> analyzed count
  private val analyzedCountMap2 = new MHashMap[Int, MLinkedHashSet[ControlPoint]] // analyzed count -> control point set
  private var worklistSize = 0
  private def leastAnalyzedWork: (Int, MLinkedHashSet[ControlPoint], ControlPoint) = {
    val minCount = analyzedCountSet.head
    val minWorkSet = analyzedCountMap2(minCount)
    (minCount, minWorkSet, minWorkSet.last)
  }

  def head: ControlPoint = this.synchronized { leastAnalyzedWork._3 }

  def isEmpty: Boolean = this.synchronized { worklistSize == 0 }

  def getSize: Int = this.synchronized { worklistSize }

  def getWorkList: WorkTreeSet = WorkTreeSet.Empty // TODO: return type does not match

  override def toString: String = {
    val str = new StringBuilder
    for(i <- analyzedCountSet) for(cp <- analyzedCountMap2(i).toList.reverse) str.append("[" + i + "] " + cp + "\n")
    str.toString()
  }

  protected def insertWork(work: OrderEntry): Unit = {
    val prevCount = analyzedCountMap1.getOrElse(work._2, -1)
    if(prevCount >= 0) {
      val prevSet = analyzedCountMap2.getOrElse(prevCount, null)
      if(prevSet != null && prevSet.contains(work._2)) return
    }
    analyzedCountSet += (prevCount + 1)
    analyzedCountMap1.put(work._2, prevCount + 1)
    analyzedCountMap2.getOrElseUpdate(prevCount + 1, new MLinkedHashSet).add(work._2)
    worklistSize+= 1
  }

  protected def removeHead: ControlPoint = {
    val (count, workSet, cp) = leastAnalyzedWork
    if(workSet.size == 1) {
      analyzedCountSet -= count
      analyzedCountMap2.remove(count)
    }
    else workSet.remove(cp)
    worklistSize-= 1
    cp
  }
}
