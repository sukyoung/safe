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

import scala.collection.immutable.{ HashMap, HashSet }
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer.models.PredefLoc.{ GLOBAL, SINGLE_PURE_LOCAL }
import kr.ac.kaist.safe.nodes.cfg._

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
  def oldify(addr: Address)(utils: Utils): Heap
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
  def isObject(loc: Loc)(utils: Utils): AbsBool
  def canPut(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool
  def canPutHelp(curLoc: Loc, absStr: AbsString, origLoc: Loc)(utils: Utils): AbsBool
  def canPutVar(x: String)(utils: Utils): AbsBool

  ////////////////////////////////////////////////////////////////
  // Proto
  ////////////////////////////////////////////////////////////////
  def proto(loc: Loc, absStr: AbsString)(utils: Utils): Value
  def protoBase(loc: Loc, absStr: AbsString)(utils: Utils): Set[Loc]

  ////////////////////////////////////////////////////////////////
  // Lookup
  ////////////////////////////////////////////////////////////////
  def lookupGlobal(x: String)(utils: Utils): (Value, Set[Exception])
  def lookupBaseGlobal(x: String)(utils: Utils): Set[Loc]

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def propStore(loc: Loc, absStr: AbsString, value: Value)(utils: Utils): Heap
  def varStoreGlobal(x: String, value: Value)(utils: Utils): Heap

  ////////////////////////////////////////////////////////////////
  // Update location
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, absStr: AbsString)(utils: Utils): (Heap, AbsBool)
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
    val newMap =
      if (this.map eq that.map) this.map
      else if (this.isBottom) that.map
      else if (that.isBottom) this.map
      else {
        val joinKeySet = this.map.keySet ++ that.map.keySet
        joinKeySet.foldLeft(HashMap[Loc, Obj]())((m, key) => {
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
      }
    new DHeap(newMap)
  }

  /* meet */
  def <>(that: Heap): Heap = {
    val newMap: Map[Loc, Obj] =
      if (this.map eq that.map) this.map
      else if (this.map.isEmpty) HashMap()
      else if (that.map.isEmpty) HashMap()
      else {
        that.map.foldLeft(this.map)(
          (m, kv) => kv match {
            case (k, v) => m.get(k) match {
              case None => m - k
              case Some(vv) => m + (k -> (v <> vv))
            }
          }
        )
      }
    new DHeap(newMap)
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
    if (!isBottom) {
      // recent location
      loc.recency match {
        case Recent =>
          if (obj.isBottom) Heap.Bot
          else new DHeap(map.updated(loc, obj))
        case Old =>
          if (obj.isBottom) this.getOrElse(loc)(Heap.Bot) { _ => this }
          else new DHeap(weakUpdated(map, loc, obj))
      }
    } else {
      this
    }
  }

  /* remove location */
  def remove(loc: Loc): Heap = {
    new DHeap(map - loc)
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Heap = {
    val newMap =
      if (this.map.isEmpty) this.map
      else {
        this.map.foldLeft(Map[Loc, Obj]())((m, kv) => {
          val (l, obj) = kv
          m + (l -> obj.subsLoc(locR, locO))
        })
      }
    new DHeap(newMap)
  }

  def oldify(addr: Address)(utils: Utils): Heap = {
    val locR = Loc(addr, Recent)
    val locO = Loc(addr, Old)
    if (this domIn locR) {
      update(locO, getOrElse(locR, Obj.Bot(utils))).remove(locR).subsLoc(locR, locO)
    } else {
      subsLoc(locR, locO)
    }
  }

  def domIn(loc: Loc): Boolean = map.contains(loc)

  def isBottom: Boolean = this.map.isEmpty // TODO is really bottom?

  override def toString: String = {
    buildString(loc => loc match {
      case Loc(ProgramAddr(_), _) => true
      case GLOBAL => true
      case _ => false
    }).toString
  }

  def toStringAll: String = {
    buildString(_ => true).toString
  }

  private def buildString(filter: Loc => Boolean): String = {
    val s = new StringBuilder
    this match {
      case _ if isBottom => s.append("⊥Heap")
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
    val isDomIn = this.getOrElse(loc)(absBool.False) { obj => (obj domIn "@construct")(absBool) }
    val b1 =
      if (absBool.True <= isDomIn) absBool.True
      else absBool.Bot
    val b2 =
      if (absBool.False <= isDomIn) absBool.False
      else absBool.Bot
    b1 + b2
  }

  def hasInstance(loc: Loc)(absBool: AbsBoolUtil): AbsBool = {
    val isDomIn = this.getOrElse(loc)(absBool.False) { obj => (obj domIn "@hasinstance")(absBool) }
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
              .getOrElse("@proto")(utils.value.Bot) { _.objval.value }
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

  ////////////////////////////////////////////////////////////////
  // Proto
  ////////////////////////////////////////////////////////////////
  def proto(loc: Loc, absStr: AbsString)(utils: Utils): Value = {
    var visited = LocSetEmpty
    val valueBot = utils.value.Bot
    def visit(currentLoc: Loc): Value = {
      if (visited.contains(currentLoc)) valueBot
      else {
        visited += currentLoc
        val test = (this.getOrElse(currentLoc, Obj.Bot(utils)) domIn absStr)(utils.absBool)
        val v1 =
          if (utils.absBool.True <= test) {
            this.getOrElse(currentLoc, Obj.Bot(utils)).getOrElse(absStr)(valueBot) { _.objval.value }
          } else {
            valueBot
          }
        val v2 =
          if (utils.absBool.False <= test) {
            val protoV = this.getOrElse(currentLoc, Obj.Bot(utils)).getOrElse("@proto")(valueBot) { _.objval.value }
            val v3 = protoV.pvalue.nullval.fold(valueBot)(_ => {
              utils.value.alpha()
            })
            v3 + protoV.locset.foldLeft(valueBot)((v, protoLoc) => {
              v + visit(protoLoc)
            })
          } else {
            valueBot
          }
        v1 + v2
      }
    }
    visit(loc)
  }

  def protoBase(loc: Loc, absStr: AbsString)(utils: Utils): Set[Loc] = {
    var visited = LocSetEmpty
    def visit(l: Loc): Set[Loc] = {
      if (visited.contains(l)) LocSetEmpty
      else {
        visited += l
        val obj = this.getOrElse(l, Obj.Bot(utils))
        val isDomIn = (obj domIn absStr)(utils.absBool)
        val locSet1 =
          if (utils.absBool.True <= isDomIn) HashSet(l)
          else LocSetEmpty
        val locSet2 =
          if (utils.absBool.False <= isDomIn) {
            val protoLocSet = obj.getOrElse("@proto")(LocSetEmpty) { _.objval.value.locset }
            protoLocSet.foldLeft(LocSetEmpty)((res, protoLoc) => {
              res ++ visit(protoLoc)
            })
          } else {
            LocSetEmpty
          }
        locSet1 ++ locSet2
      }
    }
    visit(loc)
  }

  ////////////////////////////////////////////////////////////////
  // Lookup
  ////////////////////////////////////////////////////////////////
  def lookupGlobal(x: String)(utils: Utils): (Value, Set[Exception]) = {
    val valueBot = utils.value.Bot
    if (this domIn PredefLoc.GLOBAL) {
      val globalObj = this.getOrElse(PredefLoc.GLOBAL, Obj.Bot(utils))
      val v1 =
        if (utils.absBool.True <= (globalObj domIn x)(utils.absBool))
          globalObj.getOrElse(x)(valueBot) { _.objval.value }
        else
          valueBot
      val protoLocSet = globalObj.getOrElse("@proto")(LocSetEmpty) { _.objval.value.locset }
      val (v2, excSet) =
        if (utils.absBool.False <= (globalObj domIn x)(utils.absBool)) {
          val excSet = protoLocSet.foldLeft(ExceptionSetEmpty)((tmpExcSet, protoLoc) => {
            if (utils.absBool.False <= hasProperty(protoLoc, utils.absString.alpha(x))(utils)) {
              tmpExcSet + ReferenceError
            } else tmpExcSet
          })
          val v3 = protoLocSet.foldLeft(valueBot)((tmpVal, protoLoc) => {
            if (utils.absBool.True <= hasProperty(protoLoc, utils.absString.alpha(x))(utils)) {
              tmpVal + this.proto(protoLoc, utils.absString.alpha(x))(utils)
            } else {
              tmpVal
            }
          })
          (v3, excSet)
        } else {
          (valueBot, ExceptionSetEmpty)
        }
      (v1 + v2, excSet)
    } else {
      (valueBot, ExceptionSetEmpty)
    }
  }

  def lookupBaseGlobal(x: String)(utils: Utils): Set[Loc] = {
    val globalObj = this.getOrElse(PredefLoc.GLOBAL, Obj.Bot(utils))
    val isDomIn = (globalObj domIn x)(utils.absBool)
    val locSet1 =
      if (utils.absBool.True <= isDomIn)
        HashSet(PredefLoc.GLOBAL)
      else
        LocSetEmpty
    val locSet2 =
      if (utils.absBool.False <= isDomIn) {
        val protoLocSet = globalObj.getOrElse("@proto")(LocSetEmpty) { _.objval.value.locset }
        protoLocSet.foldLeft(LocSetEmpty)((res, protoLoc) => {
          res ++ this.protoBase(protoLoc, utils.absString.alpha(x))(utils)
        })
      } else {
        LocSetEmpty
      }
    locSet1 ++ locSet2
  }

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def propStore(loc: Loc, absStr: AbsString, value: Value)(utils: Utils): Heap = {
    val findingObj = this.getOrElse(loc, Obj.Bot(utils))
    val objDomIn = (findingObj domIn absStr)(utils.absBool)
    objDomIn.gamma match {
      case ConSingleTop() =>
        val oldObjV: DataProperty = findingObj.getOrElse(absStr)(utils.dataProp.Bot) { _.objval }
        val newObjV = utils.dataProp(value)(
          oldObjV.writable + utils.absBool.True,
          oldObjV.enumerable + utils.absBool.True,
          oldObjV.configurable + utils.absBool.True
        )
        this.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case ConSingleBot() => Heap.Bot
      case ConSingleCon(true) =>
        val oldObjV: DataProperty = findingObj.getOrElse(absStr)(utils.dataProp.Bot) { _.objval }
        val newObjV = oldObjV.copyWith(value)
        this.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case ConSingleCon(false) =>
        val newObjV = utils.dataProp(value)(utils.absBool.True, utils.absBool.True, utils.absBool.True)
        this.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
    }
  }

  def varStoreGlobal(x: String, value: Value)(utils: Utils): Heap = {
    val globalLoc = PredefLoc.GLOBAL
    val obj = this.getOrElse(globalLoc, Obj.Bot(utils))
    val h1 =
      if (utils.absBool.False <= (obj domIn x)(utils.absBool))
        this.propStore(globalLoc, utils.absString.alpha(x), value)(utils)
      else Heap.Bot
    val h2 =
      if (utils.absBool.True <= (obj domIn x)(utils.absBool)) {
        val oldObjVal = obj.getOrElse(x)(utils.dataProp.Bot) { _.objval }
        val newObjVal = oldObjVal.copyWith(value)
        this.update(globalLoc, obj.update(x, PropValue(newObjVal)))
      } else Heap.Bot
    h1 + h2
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, absStr: AbsString)(utils: Utils): (Heap, AbsBool) = {
    getOrElse[(Heap, AbsBool)](loc)((this, utils.absBool.Bot))(_ => {
      val test = hasOwnProperty(loc, absStr)(utils)
      val targetObj = this.getOrElse(loc, Obj.Bot(utils))
      val isConfigurable = targetObj.getOrElse(absStr)(utils.absBool.Bot) { _.objval.configurable }
      val (h1, b1) =
        if ((utils.absBool.True <= test) && (utils.absBool.False <= isConfigurable))
          (this, utils.absBool.False)
        else
          (Heap.Bot, utils.absBool.Bot)
      val (h2, b2) =
        if (((utils.absBool.True <= test) && (utils.absBool.False != isConfigurable))
          || utils.absBool.False <= test)
          (this.update(loc, (targetObj - absStr)(utils)), utils.absBool.True)
        else
          (Heap.Bot, utils.absBool.Bot)
      (h1 + h2, b1 + b2)
    })
  }
}
