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

import scala.collection.immutable.{ HashMap }
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.cfg._

trait Heap {
  val map: Map[Loc, AbsObject]

  /* partial order */
  def <=(that: Heap): Boolean
  /* join */
  def +(that: Heap): Heap
  /* meet */
  def <>(that: Heap): Heap
  /* lookup */
  def apply(loc: Loc): Option[AbsObject]
  def get(loc: Loc): AbsObject
  def getOrElse[T](loc: Loc)(default: T)(f: AbsObject => T): T
  /* heap update */
  def weakUpdate(loc: Loc, obj: AbsObject): Heap
  def update(loc: Loc, obj: AbsObject): Heap
  /* remove location */
  def remove(loc: Loc): Heap
  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Heap
  def oldify(addr: Address): Heap
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
  def hasProperty(loc: Loc, absStr: AbsString): AbsBool
  def hasOwnProperty(loc: Loc, absStr: AbsString): AbsBool
  def isArray(loc: Loc): AbsBool
  def isObject(loc: Loc): AbsBool
  def canPut(loc: Loc, absStr: AbsString): AbsBool
  def canPutHelp(curLoc: Loc, absStr: AbsString, origLoc: Loc): AbsBool
  def canPutVar(x: String): AbsBool

  ////////////////////////////////////////////////////////////////
  // Proto
  ////////////////////////////////////////////////////////////////
  def protoBase(loc: Loc, absStr: AbsString): AbsLoc

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def propStore(loc: Loc, absStr: AbsString, value: AbsValue): Heap

  ////////////////////////////////////////////////////////////////
  // Update location
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, absStr: AbsString): (Heap, AbsBool)
}

object Heap {
  val Bot: Heap = new DHeap(HashMap[Loc, AbsObject]())
  def apply(map: Map[Loc, AbsObject]): Heap = new DHeap(map)
}

class DHeap(val map: Map[Loc, AbsObject]) extends Heap {
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

  private def weakUpdated(m: Map[Loc, AbsObject], loc: Loc, newObj: AbsObject): Map[Loc, AbsObject] =
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
        joinKeySet.foldLeft(HashMap[Loc, AbsObject]())((m, key) => {
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
    val newMap: Map[Loc, AbsObject] =
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
  def apply(loc: Loc): Option[AbsObject] = map.get(loc)

  def get(loc: Loc): AbsObject =
    this(loc) match {
      case Some(obj) => obj
      case None => AbsObjectUtil.Bot
    }

  def getOrElse[T](loc: Loc)(default: T)(f: AbsObject => T): T = {
    this(loc) match {
      case Some(obj) => f(obj)
      case None => default
    }
  }

  /* heap update */
  def weakUpdate(loc: Loc, obj: AbsObject): Heap = {
    if (!isBottom) new DHeap(weakUpdated(map, loc, obj))
    else this
  }

  def update(loc: Loc, obj: AbsObject): Heap = {
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
        this.map.foldLeft(Map[Loc, AbsObject]())((m, kv) => {
          val (l, obj) = kv
          m + (l -> obj.subsLoc(locR, locO))
        })
      }
    new DHeap(newMap)
  }

  def oldify(addr: Address): Heap = {
    val locR = Loc(addr, Recent)
    val locO = Loc(addr, Old)
    if (this domIn locR) {
      update(locO, get(locR)).remove(locR).subsLoc(locR, locO)
    } else {
      subsLoc(locR, locO)
    }
  }

  def domIn(loc: Loc): Boolean = map.contains(loc)

  def isBottom: Boolean = this.map.isEmpty // TODO is really bottom?

  override def toString: String = {
    buildString(loc => loc match {
      case Loc(ProgramAddr(_), _) => true
      case BuiltinGlobal.loc => true
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

  private def toStringLoc(loc: Loc, obj: AbsObject): String = {
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
    val isDomIn = this.getOrElse(loc)(absBool.False) { obj => (obj contains IConstruct) }
    val b1 =
      if (absBool.True <= isDomIn) absBool.True
      else absBool.Bot
    val b2 =
      if (absBool.False <= isDomIn) absBool.False
      else absBool.Bot
    b1 + b2
  }

  def hasInstance(loc: Loc)(absBool: AbsBoolUtil): AbsBool = {
    val isDomIn = this.getOrElse(loc)(absBool.False) { obj => (obj contains IHasInstance) }
    val b1 =
      if (absBool.True <= isDomIn) absBool.True
      else absBool.Bot
    val b2 =
      if (absBool.False <= isDomIn) absBool.False
      else absBool.Bot
    b1 + b2
  }

  def hasProperty(loc: Loc, absStr: AbsString): AbsBool = {
    var visited = AbsLoc.Bot
    def visit(currentLoc: Loc): AbsBool = {
      if (visited.contains(currentLoc)) AbsBool.Bot
      else {
        visited += currentLoc
        val test = hasOwnProperty(currentLoc, absStr)
        val b1 =
          if (AbsBool.True <= test) AbsBool.True
          else AbsBool.Bot
        val b2 =
          if (AbsBool.False <= test) {
            val protoV = this.get(currentLoc)(IPrototype).value
            val b3 = protoV.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
            b3 + protoV.locset.foldLeft[AbsBool](AbsBool.Bot)((b, protoLoc) => {
              b + visit(protoLoc)
            })
          } else {
            AbsBool.Bot
          }
        b1 + b2
      }
    }
    visit(loc)
  }

  def hasOwnProperty(loc: Loc, absStr: AbsString): AbsBool = {
    this.get(loc) contains absStr
  }

  def isArray(loc: Loc): AbsBool = {
    val className = this.get(loc)(IClass).value.pvalue.strval
    val arrayAbsStr = AbsString.alpha("Array")
    val b1 =
      if (arrayAbsStr <= className)
        AbsBool.True
      else
        AbsBool.Bot
    val b2 =
      if (arrayAbsStr != className)
        AbsBool.False
      else
        AbsBool.Bot
    b1 + b2
  }

  def isObject(loc: Loc): AbsBool = {
    this.get(loc) contains IClass
  }

  def canPut(loc: Loc, absStr: AbsString): AbsBool = canPutHelp(loc, absStr, loc)

  def canPutHelp(curLoc: Loc, absStr: AbsString, origLoc: Loc): AbsBool = {
    var visited = AbsLoc.Bot
    def visit(visitLoc: Loc): AbsBool = {
      if (visited contains visitLoc) AbsBool.Bot
      else {
        visited += visitLoc
        val domInStr = this.get(visitLoc) contains absStr
        val b1 =
          if (AbsBool.True <= domInStr) this.get(visitLoc)(absStr).writable
          else AbsBool.Bot
        val b2 =
          if (AbsBool.False <= domInStr) {
            val protoObj = this.get(visitLoc)(IPrototype)
            val protoLocSet = protoObj.value.locset
            val b3 = protoObj.value.pvalue.nullval.fold(AbsBool.Bot)(_ => {
              this.get(visitLoc)(IExtensible).value.pvalue.boolval
            })
            val b4 = protoLocSet.foldLeft(AbsBool.Bot)((absB, loc) => absB + visit(loc))
            val b5 =
              if (AbsBool.False <= b4) this.get(origLoc)(IExtensible).value.pvalue.boolval
              else AbsBool.Bot
            b3 + b4 + b5
          } else AbsBool.Bot
        b1 + b2
      }
    }
    visit(curLoc)
  }

  def canPutVar(x: String): AbsBool = {
    val globalLoc = BuiltinGlobal.loc
    val globalObj = this.get(globalLoc)
    val domIn = globalObj contains x
    val b1 =
      if (AbsBool.True <= domIn) globalObj(x).writable
      else AbsBool.Bot
    val b2 =
      if (AbsBool.False <= domIn) canPut(globalLoc, AbsString(x))
      else AbsBool.Bot
    b1 + b2
  }

  ////////////////////////////////////////////////////////////////
  // Proto
  ////////////////////////////////////////////////////////////////
  def proto(loc: Loc, absStr: AbsString): AbsValue = {
    var visited = AbsLoc.Bot
    val valueBot = AbsValue.Bot
    def visit(currentLoc: Loc): AbsValue = {
      if (visited.contains(currentLoc)) valueBot
      else {
        visited += currentLoc
        val test = this.get(currentLoc) contains absStr
        val v1 =
          if (AbsBool.True <= test) {
            this.get(currentLoc)(absStr).value
          } else {
            valueBot
          }
        val v2 =
          if (AbsBool.False <= test) {
            val protoV = this.get(currentLoc)(IPrototype).value
            val v3 = protoV.pvalue.nullval.fold(valueBot)(_ => {
              AbsValue(Undef)
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

  def protoBase(loc: Loc, absStr: AbsString): AbsLoc = {
    var visited = AbsLoc.Bot
    def visit(l: Loc): AbsLoc = {
      if (visited.contains(l)) AbsLoc.Bot
      else {
        visited += l
        val obj = this.get(l)
        val isDomIn = (obj contains absStr)
        val locSet1 =
          if (AbsBool.True <= isDomIn) AbsLoc(l)
          else AbsLoc.Bot
        val locSet2 =
          if (AbsBool.False <= isDomIn) {
            val protoLocSet = obj(IPrototype).value.locset
            protoLocSet.foldLeft(AbsLoc.Bot)((res, protoLoc) => {
              res + visit(protoLoc)
            })
          } else {
            AbsLoc.Bot
          }
        locSet1 + locSet2
      }
    }
    visit(loc)
  }

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def propStore(loc: Loc, absStr: AbsString, value: AbsValue): Heap = {
    val findingObj = this.get(loc)
    val objDomIn = findingObj contains absStr
    if (objDomIn == AbsBool.Top) {
      val oldObjV: AbsDataProp = findingObj(absStr)
      val newObjV = AbsDataProp(
        value,
        oldObjV.writable + AbsBool.True,
        oldObjV.enumerable + AbsBool.True,
        oldObjV.configurable + AbsBool.True
      )
      this.update(loc, findingObj.update(absStr, newObjV))
    } else if (objDomIn == AbsBool.True) {
      val oldObjV: AbsDataProp = findingObj(absStr)
      val newObjV = oldObjV.copyWith(value)
      this.update(loc, findingObj.update(absStr, newObjV))
    } else if (objDomIn == AbsBool.False) {
      val newObjV = AbsDataProp(value, AbsBool.True, AbsBool.True, AbsBool.True)
      this.update(loc, findingObj.update(absStr, newObjV))
    } else {
      Heap.Bot
    }
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, absStr: AbsString): (Heap, AbsBool) = {
    getOrElse[(Heap, AbsBool)](loc)((this, AbsBool.Bot))(_ => {
      val targetObj = this.get(loc)
      val (newObj, asuccess) = targetObj.Delete(absStr)
      (this.update(loc, newObj), asuccess)
    })
  }
}
