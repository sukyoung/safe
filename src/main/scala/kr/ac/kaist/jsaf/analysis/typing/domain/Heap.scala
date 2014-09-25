/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import kr.ac.kaist.jsaf.analysis.typing.Config
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

case class Heap(map: HeapMap) {
  /* partial order */
  def <= (that: Heap): Boolean = {
    this.map.submapOf(that.map)
  }
  
  /* join */
  def + (that: Heap): Heap = {
    if (this.map eq that.map) this
    else if (this eq HeapBot) that
    else if (that eq HeapBot) this
    else {
      val this_map = this.map
      val that_map = that.map
      val join_map = 
        if (this_map.size < that_map.size) {
          this_map.foldLeft(that_map)((m, kv) => m.weakUpdated(kv._1, kv._2))
        } else {
          that_map.foldLeft(this_map)((m, kv) => m.weakUpdated(kv._1, kv._2))
        }
      Heap(join_map)
    }
  }

  /* meet */
  def <> (that: Heap): Heap = {
    if (this.map eq that.map) this
    else if (this.map.isEmpty) HeapBot
    else if (that.map.isEmpty) HeapBot
    else {
      val meet = that.map.foldLeft(this.map)(
        (m, kv) => m.get(kv._1) match {
          case None => m - kv._1
          case Some(v) => m + (kv._1 -> (kv._2 <> v))
        })
      Heap(meet)
    }
  }

  /* lookup */
  def apply(loc: Loc): Obj = map.get(loc) match {
    case Some(obj) => obj
    case None => Obj.bottom
  }

  /* heap update */
  def update(loc: Loc, obj: Obj): Heap = {
    // A heap holding bottom object represents no valid concrete heap.
    // For pre-analysis, input heap is unchanged when attempting such update.
    // For main analysis, HeapBot is returned, indicating dead state.
    if (Config.preAnalysis) {
      if (obj.isBottom) this
      else Heap(map.weakUpdated(loc, obj))
    } 
    else {
      // recent location
      if ((loc & 1) == 0) {
        if (obj.isBottom) HeapBot
        else Heap(map.updated(loc, obj))
      }
      // old location
      else { 
        if (obj.isBottom) {
          if (this(loc).isBottom) HeapBot
          else this
        }
        else Heap(map.weakUpdated(loc, obj))
      }
    }
  }

  /* remove location */
  def remove(loc: Loc): Heap = {
    Heap(map - loc)
  }

  /* substitute l_r by l_o */
  def subsLoc(l_r: Loc, l_o: Loc): Heap = {
    Heap(this.map.subsLoc(l_r, l_o))
  }

  def domIn(loc: Loc) = { map.contains(loc) }

  def restrict(lp: LPSet) = {
    val m = map.foldLeft(map)((m, kv) =>
      lp.get(kv._1) match {
        case None => m - kv._1
        case Some(s) => m + (kv._1 -> m(kv._1).restrict(s))
      })

    Heap(m)
  }

  def restrict(lset: LocSet) =
  posMask match {
    case Some(posmask) => Heap(this.map.filter((kv) => lset.contains(if (kv._1 < 0) (kv._1 | negMask.get) else (kv._1 & posmask))))
    case None => Heap(this.map.filter((kv) => lset.contains(kv._1)))
  }

  /* for temporal pre-analysis result, make all the properties absentTop. */
  def absentTop() = {
    Heap(this.map.map((kv) => (kv._1 -> (kv._2.absentTop()))))
  }

  /* to make old locations after preanalysis */
  def oldify() = {
    Heap(map.foldLeft(HeapMapBot)((maps, kv) => {
      val (loc, obj) = kv
      val oldifiedObj = obj.oldify()
      // internal object should be only one
      if(locToAddr(loc).toInt < 0) {
        maps + (loc -> oldifiedObj)
      } else {
        maps + (addrToLoc(locToAddr(loc), Recent) -> oldifiedObj) + (addrToLoc(locToAddr(loc), Old) -> oldifiedObj)
      }
    }))
  }
}

