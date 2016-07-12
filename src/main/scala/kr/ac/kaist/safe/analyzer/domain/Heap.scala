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

import scala.collection.immutable.HashMap
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer.models.PredefLoc.{ GLOBAL, SINGLE_PURE_LOCAL }

trait Heap {
  val map: Map[Loc, Obj]

  /* partial order */
  def <=(that: Heap): Boolean
  /* join */
  def +(that: Heap): Heap
  /* meet */
  def <>(that: Heap): Heap
  /* lookup */
  def apply(loc: Loc): Option[Obj]
  def getOrElse(loc: Loc, default: Obj): Obj
  def getOrElse[T](loc: Loc)(default: T)(f: Obj => T): T
  /* heap update */
  def update(loc: Loc, obj: Obj): Heap
  /* remove location */
  def remove(loc: Loc): Heap
  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Heap
  def domIn(loc: Loc): Boolean

  def isBottom: Boolean

  // toString
  def toStringAll: String
  def toStringLoc(loc: Loc): Option[String]

  ////////////////////////////////////////////////////////////////
  // Predicates
  ////////////////////////////////////////////////////////////////
  def hasConstruct(loc: Loc)(absBool: AbsBoolUtil): AbsBool
  def hasInstance(loc: Loc)(absBool: AbsBoolUtil): AbsBool
  def hasProperty(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool
  def hasOwnProperty(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool
  def isArray(loc: Loc)(utils: Utils): AbsBool
  def isCallable(loc: Loc)(utils: Utils): AbsBool
  def isObject(loc: Loc)(utils: Utils): AbsBool
  def canPut(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool
  def canPutHelp(curLoc: Loc, absStr: AbsString, origLoc: Loc)(utils: Utils): AbsBool
  def canPutVar(x: String)(utils: Utils): AbsBool
}

object Heap {
  val Bot: Heap = new DHeap(HashMap[Loc, Obj]())
  def apply(map: Map[Loc, Obj]): Heap = new DHeap(map)
}

class DHeap(val map: Map[Loc, Obj]) extends Heap {
  /* partial order */
  def <=(that: Heap): Boolean = {
    if (this.map eq that.map) true
    else if (this.map.size > that.map.size) false
    else if (this.map.isEmpty) true
    else if (that.map.isEmpty) false
    else if (!(this.map.keySet subsetOf that.map.keySet)) false
    else that.map.forall((kv) => {
      val (l, obj) = kv
      this.map.get(l) match {
        case Some(thisObj) => thisObj <= obj
        case None => false
      }
    })
  }

  private def weakUpdated(m: Map[Loc, Obj], loc: Loc, newObj: Obj): Map[Loc, Obj] =
    m.get(loc) match {
      case Some(oldObj) => m.updated(loc, oldObj + newObj)
      case None => m.updated(loc, newObj)
    }

  /* join */
  def +(that: Heap): Heap = {
    if (this.map eq that.map) this
    else if (this.isBottom) that
    else if (that.isBottom) this
    else {
      val joinKeySet = this.map.keySet ++ that.map.keySet
      val joinMap = joinKeySet.foldLeft(HashMap[Loc, Obj]())((m, key) => {
        val joinObj = (this.map.get(key), that.map.get(key)) match {
          case (Some(obj1), Some(obj2)) => Some(obj1 + obj2)
          case (Some(obj1), None) => Some(obj1)
          case (None, Some(obj2)) => Some(obj2)
          case (None, None) => None
        }
        joinObj match {
          case Some(obj) => m.updated(key, obj)
          case None => m
        }
      })
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
        (m, kv) => kv match {
          case (k, v) => m.get(k) match {
            case None => m - k
            case Some(vv) => m + (k -> (v <> vv))
          }
        }
      )
      new DHeap(meet)
    }
  }

  /* lookup */
  def apply(loc: Loc): Option[Obj] = map.get(loc)

  def getOrElse(loc: Loc, default: Obj): Obj =
    this(loc) match {
      case Some(obj) => obj
      case None => default
    }

  def getOrElse[T](loc: Loc)(default: T)(f: Obj => T): T = {
    this(loc) match {
      case Some(obj) => f(obj)
      case None => default
    }
  }

  /* heap update */
  def update(loc: Loc, obj: Obj): Heap = {
    // recent location
    loc.recency match {
      case Recent =>
        if (obj.isBottom) Heap.Bot
        else new DHeap(map.updated(loc, obj))
      case Old =>
        if (obj.isBottom) this.getOrElse(loc)(Heap.Bot) { _ => this }
        else new DHeap(weakUpdated(map, loc, obj))
    }
  }

  /* remove location */
  def remove(loc: Loc): Heap = {
    new DHeap(map - loc)
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Heap = {
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

  def isBottom: Boolean = this.map.isEmpty // TODO is really bottom?

  override def toString: String = {
    buildString(loc => loc match {
      case Loc(ProgramAddr(_), _) => true
      case GLOBAL | SINGLE_PURE_LOCAL => true
      case _ => false
    }).toString
  }

  def toStringAll: String = {
    buildString(_ => true).toString
  }

  private def buildString(filter: Loc => Boolean): String = {
    val s = new StringBuilder
    this match {
      case Heap.Bot => s.append("⊥Heap")
      case _ => {
        val sortedSeq =
          map.toSeq.filter { case (loc, _) => filter(loc) }
            .sortBy { case (loc, _) => loc }
        sortedSeq.map {
          case (loc, obj) => s.append(toStringLoc(loc, obj)).append(LINE_SEP)
        }
      }
    }
    s.toString
  }

  def toStringLoc(loc: Loc): Option[String] = {
    map.get(loc).map(toStringLoc(loc, _))
  }

  private def toStringLoc(loc: Loc, obj: Obj): String = {
    val s = new StringBuilder
    val keyStr = loc.toString + " -> "
    s.append(keyStr)
    Useful.indentation(s, obj.toString, keyStr.length)
    s.toString
  }

  ////////////////////////////////////////////////////////////////
  // Predicates
  ////////////////////////////////////////////////////////////////
  def hasConstruct(loc: Loc)(absBool: AbsBoolUtil): AbsBool = {
    val isDomIn = this(loc) match {
      case Some(obj) => (obj domIn "@construct")(absBool)
      case None => absBool.False
    }
    val b1 =
      if (absBool.True <= isDomIn) absBool.True
      else absBool.Bot
    val b2 =
      if (absBool.False <= isDomIn) absBool.False
      else absBool.Bot
    b1 + b2
  }

  def hasInstance(loc: Loc)(absBool: AbsBoolUtil): AbsBool = {
    val isDomIn = this(loc) match {
      case Some(obj) => (obj domIn "@hasinstance")(absBool)
      case None => absBool.False
    }
    val b1 =
      if (absBool.True <= isDomIn) absBool.True
      else absBool.Bot
    val b2 =
      if (absBool.False <= isDomIn) absBool.False
      else absBool.Bot
    b1 + b2
  }

  def hasProperty(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool = {
    var visited = LocSetEmpty
    def visit(currentLoc: Loc): AbsBool = {
      if (visited.contains(currentLoc)) utils.absBool.Bot
      else {
        visited += currentLoc
        val test = hasOwnProperty(currentLoc, absStr)(utils)
        val b1 =
          if (utils.absBool.True <= test) utils.absBool.True
          else utils.absBool.Bot
        val b2 =
          if (utils.absBool.False <= test) {
            val protoV = this.getOrElse(currentLoc, Obj.Bot(utils))
              .getOrElse("@proto")(Value.Bot(utils)) { _.objval.value }
            val b3 = protoV.pvalue.nullval.fold(utils.absBool.Bot) { _ => utils.absBool.False }
            b3 + protoV.locset.foldLeft[AbsBool](utils.absBool.Bot)((b, protoLoc) => {
              b + visit(protoLoc)
            })
          } else {
            utils.absBool.Bot
          }
        b1 + b2
      }
    }
    visit(loc)
  }

  def hasOwnProperty(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool = {
    (this.getOrElse(loc, Obj.Bot(utils)) domIn absStr)(utils.absBool)
  }

  def isArray(loc: Loc)(utils: Utils): AbsBool = {
    val className = this.getOrElse(loc, Obj.Bot(utils))
      .getOrElse("@class")(utils.absString.Bot) { _.objval.value.pvalue.strval }
    val arrayAbsStr = utils.absString.alpha("Array")
    val b1 =
      if (arrayAbsStr <= className)
        utils.absBool.True
      else
        utils.absBool.Bot
    val b2 =
      if (arrayAbsStr != className)
        utils.absBool.False
      else
        utils.absBool.Bot
    b1 + b2
  }

  def isCallable(loc: Loc)(utils: Utils): AbsBool = {
    val isDomIn = (this.getOrElse(loc, Obj.Bot(utils)) domIn "@function")(utils.absBool)
    val b1 =
      if (utils.absBool.True <= isDomIn) utils.absBool.True
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= isDomIn) utils.absBool.False
      else utils.absBool.Bot
    b1 + b2
  }

  def isObject(loc: Loc)(utils: Utils): AbsBool = {
    (this.getOrElse(loc, Obj.Bot(utils)) domIn "@class")(utils.absBool)
  }

  def canPut(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool = canPutHelp(loc, absStr, loc)(utils)

  def canPutHelp(curLoc: Loc, absStr: AbsString, origLoc: Loc)(utils: Utils): AbsBool = {
    var visited = LocSetEmpty
    def visit(visitLoc: Loc): AbsBool = {
      if (visited contains visitLoc) utils.absBool.Bot
      else {
        visited += visitLoc
        val domInStr = (this.getOrElse(visitLoc, Obj.Bot(utils)) domIn absStr)(utils.absBool)
        val b1 =
          if (utils.absBool.True <= domInStr) {
            val obj = this.getOrElse(visitLoc, Obj.Bot(utils))
            obj.getOrElse(absStr)(utils.absBool.Bot) { _.objval.writable }
          } else utils.absBool.Bot
        val b2 =
          if (utils.absBool.False <= domInStr) {
            val protoObj = this.getOrElse(visitLoc, Obj.Bot(utils)).get("@proto")(utils)
            val protoLocSet = protoObj.objval.value.locset
            val b3 = protoObj.objval.value.pvalue.nullval.gamma match {
              case ConSimpleBot => utils.absBool.Bot
              case ConSimpleTop =>
                this.getOrElse(visitLoc, Obj.Bot(utils))
                  .getOrElse("@extensible")(utils.absBool.Bot) { _.objval.value.pvalue.boolval }
            }
            val b4 = protoLocSet.foldLeft(utils.absBool.Bot)((absB, loc) => absB + visit(loc))
            val b5 =
              if (utils.absBool.False <= b4)
                this.getOrElse(origLoc, Obj.Bot(utils))
                  .getOrElse("@extensible")(utils.absBool.Bot) { _.objval.value.pvalue.boolval }
              else utils.absBool.Bot
            b3 + b4 + b5
          } else utils.absBool.Bot
        b1 + b2
      }
    }
    visit(curLoc)
  }

  def canPutVar(x: String)(utils: Utils): AbsBool = {
    val globalLoc = PredefLoc.GLOBAL
    val globalObj = this.getOrElse(globalLoc, Obj.Bot(utils))
    val domIn = (globalObj domIn x)(utils.absBool)
    val b1 =
      if (utils.absBool.True <= domIn) globalObj.getOrElse(x)(utils.absBool.Bot) { _.objval.writable }
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= domIn) canPut(globalLoc, utils.absString.alpha(x))(utils)
      else utils.absBool.Bot
    b1 + b2
  }
}
