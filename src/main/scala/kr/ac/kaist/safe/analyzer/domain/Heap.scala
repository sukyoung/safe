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

trait Heap {
  val map: Map[Loc, Obj]

  /* partial order */
  def <=(that: Heap): Boolean
  /* join */
  def +(that: Heap): Heap
  /* meet */
  def <>(that: Heap): Heap
  /* lookup */
  def apply(loc: Loc, utils: Utils): Obj
  /* heap update */
  def update(loc: Loc, obj: Obj): Heap
  /* remove location */
  def remove(loc: Loc): Heap
  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Heap
  def domIn(loc: Loc): Boolean

  def isBottom: Boolean
}

object Heap {
  val Bot = new DHeap(Map[Loc, Obj]())
  def apply(map: Map[Loc, Obj]): Heap = new DHeap(map)
}

class DHeap(val map: Map[Loc, Obj]) extends Heap {
  /* partial order */
  def <=(that: Heap): Boolean = {
    if (this.map eq that.map) true
    else if (this.map.size > that.map.size) false
    else if (this.map.isEmpty) true
    else if (that.map.isEmpty) false
    else this.map.forall((kv) => {
      val (l, obj) = kv
      val thatObj = that.map(l)
      obj <= thatObj
    })
  }

  private def weakUpdated(m: Map[Loc, Obj], key: Loc, newData: Obj): Map[Loc, Obj] = {
    if (m.isEmpty) m
    else {
      val currentData = m(key)
      if (currentData eq newData) m
      else if (newData <= currentData) m
      else if (currentData <= newData) m.updated(key, newData)
      else m.updated(key, currentData + newData)
    }
  }

  /* join */
  def +(that: Heap): Heap = {
    if (this.map eq that.map) this
    else if (this.isBottom) that
    else if (that.isBottom) this
    else {
      val joinMap =
        if (this.map.size < that.map.size) {
          this.map.foldLeft(that.map)((m, kv) => weakUpdated(m, kv._1, kv._2))
        } else {
          that.map.foldLeft(this.map)((m, kv) => weakUpdated(m, kv._1, kv._2))
        }
      new DHeap(joinMap)
    }
  }

  /* meet */
  def <>(that: Heap): Heap = {
    if (this.map eq that.map) this
    else if (this.map.isEmpty) Heap.Bot
    else if (that.map.isEmpty) Heap.Bot
    else {
      val meet = that.map.foldLeft(this.map)(
        (m, kv) => m.get(kv._1) match {
          case None => m - kv._1
          case Some(v) => m + (kv._1 -> (kv._2 <> v))
        }
      )
      new DHeap(meet)
    }
  }

  /* lookup */
  def apply(loc: Loc): Option[Obj] = map.get(loc)

  def apply(loc: Loc, utils: Utils): Obj = this(loc) match {
    case Some(obj) => obj
    case None => utils.ObjBot
  }

  /* heap update */
  def update(loc: Loc, obj: Obj): DHeap = {
    // recent location
    if ((loc & 1) == 0) {
      if (obj.isBottom) Heap.Bot
      else new DHeap(map.updated(loc, obj))
    } // old location
    else {
      if (obj.isBottom) this(loc) match {
        case Some(_) => this
        case None => Heap.Bot
      }
      else new DHeap(weakUpdated(map, loc, obj))
    }
  }

  /* remove location */
  def remove(loc: Loc): DHeap = {
    new DHeap(map - loc)
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): DHeap = {
    if (this.map.isEmpty) this
    else {
      val newMap =
        this.map.foldLeft(Map[Loc, Obj]())((m, kv) => {
          val (l, obj) = kv
          m + (l -> obj.subsLoc(locR, locO))
        })
      new DHeap(newMap)
    }
  }

  def domIn(loc: Loc): Boolean = map.contains(loc)

  def isBottom: Boolean = this.map.isEmpty
}