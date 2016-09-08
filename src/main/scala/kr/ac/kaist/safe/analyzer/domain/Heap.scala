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
  def proto(loc: Loc, absStr: AbsString): AbsValue
  def protoBase(loc: Loc, absStr: AbsString): AbsLoc

  ////////////////////////////////////////////////////////////////
  // Lookup
  ////////////////////////////////////////////////////////////////
  def lookupGlobal(x: String): (AbsValue, Set[Exception])
  def lookupBaseGlobal(x: String): AbsLoc

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def propStore(loc: Loc, absStr: AbsString, value: AbsValue): Heap
  def varStoreGlobal(x: String, value: AbsValue): Heap

  ////////////////////////////////////////////////////////////////
  // Update location
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, absStr: AbsString): (Heap, AbsBool)
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

  def oldify(addr: Address): Heap = {
    val locR = Loc(addr, Recent)
    val locO = Loc(addr, Old)
    if (this domIn locR) {
      update(locO, getOrElse(locR, AbsObjectUtil.Bot)).remove(locR).subsLoc(locR, locO)
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
    val isDomIn = this.getOrElse(loc)(absBool.False) { obj => (obj domIn IConstruct) }
    val b1 =
      if (absBool.True <= isDomIn) absBool.True
      else absBool.Bot
    val b2 =
      if (absBool.False <= isDomIn) absBool.False
      else absBool.Bot
    b1 + b2
  }

  def hasInstance(loc: Loc)(absBool: AbsBoolUtil): AbsBool = {
    val isDomIn = this.getOrElse(loc)(absBool.False) { obj => (obj domIn IHasInstance) }
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
            val protoV = this.getOrElse(currentLoc, AbsObjectUtil.Bot)
              .getOrElse(IPrototype)(AbsValue.Bot) { _.value }
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
    (this.getOrElse(loc, AbsObjectUtil.Bot) domIn absStr)
  }

  def isArray(loc: Loc): AbsBool = {
    val className = this.getOrElse(loc, AbsObjectUtil.Bot)
      .getOrElse(IClass)(AbsString.Bot) { _.value.pvalue.strval }
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
    (this.getOrElse(loc, AbsObjectUtil.Bot) domIn IClass)
  }

  def canPut(loc: Loc, absStr: AbsString): AbsBool = canPutHelp(loc, absStr, loc)

  def canPutHelp(curLoc: Loc, absStr: AbsString, origLoc: Loc): AbsBool = {
    var visited = AbsLoc.Bot
    def visit(visitLoc: Loc): AbsBool = {
      if (visited contains visitLoc) AbsBool.Bot
      else {
        visited += visitLoc
        val domInStr = (this.getOrElse(visitLoc, AbsObjectUtil.Bot) domIn absStr)
        val b1 =
          if (AbsBool.True <= domInStr) {
            val obj = this.getOrElse(visitLoc, AbsObjectUtil.Bot)
            obj.getOrElse(absStr)(AbsBool.Bot) { _.objval.writable }
          } else AbsBool.Bot
        val b2 =
          if (AbsBool.False <= domInStr) {
            val protoObj = this.getOrElse(visitLoc, AbsObjectUtil.Bot).get(IPrototype)
            val protoLocSet = protoObj.value.locset
            val b3 = protoObj.value.pvalue.nullval.gamma match {
              case ConSimpleBot() => AbsBool.Bot
              case ConSimpleTop() =>
                this.getOrElse(visitLoc, AbsObjectUtil.Bot)
                  .getOrElse(IExtensible)(AbsBool.Bot) { _.value.pvalue.boolval }
            }
            val b4 = protoLocSet.foldLeft(AbsBool.Bot)((absB, loc) => absB + visit(loc))
            val b5 =
              if (AbsBool.False <= b4)
                this.getOrElse(origLoc, AbsObjectUtil.Bot)
                  .getOrElse(IExtensible)(AbsBool.Bot) { _.value.pvalue.boolval }
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
    val globalObj = this.getOrElse(globalLoc, AbsObjectUtil.Bot)
    val domIn = (globalObj domIn x)
    val b1 =
      if (AbsBool.True <= domIn) globalObj.getOrElse(x)(AbsBool.Bot) { _.objval.writable }
      else AbsBool.Bot
    val b2 =
      if (AbsBool.False <= domIn) canPut(globalLoc, AbsString.alpha(x))
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
        val test = (this.getOrElse(currentLoc, AbsObjectUtil.Bot) domIn absStr)
        val v1 =
          if (AbsBool.True <= test) {
            this.getOrElse(currentLoc, AbsObjectUtil.Bot).getOrElse(absStr)(valueBot) { _.objval.value }
          } else {
            valueBot
          }
        val v2 =
          if (AbsBool.False <= test) {
            val protoV = this.getOrElse(currentLoc, AbsObjectUtil.Bot).getOrElse(IPrototype)(valueBot) { _.value }
            val v3 = protoV.pvalue.nullval.fold(valueBot)(_ => {
              AbsValue.alpha(Undef)
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
        val obj = this.getOrElse(l, AbsObjectUtil.Bot)
        val isDomIn = (obj domIn absStr)
        val locSet1 =
          if (AbsBool.True <= isDomIn) AbsLoc.alpha(l)
          else AbsLoc.Bot
        val locSet2 =
          if (AbsBool.False <= isDomIn) {
            val protoLocSet = obj.getOrElse(IPrototype)(AbsLoc.Bot) { _.value.locset }
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
  // Lookup
  ////////////////////////////////////////////////////////////////
  def lookupGlobal(x: String): (AbsValue, Set[Exception]) = {
    val valueBot = AbsValue.Bot
    if (this domIn BuiltinGlobal.loc) {
      val globalObj = this.getOrElse(BuiltinGlobal.loc, AbsObjectUtil.Bot)
      val v1 =
        if (AbsBool.True <= (globalObj domIn x))
          globalObj.getOrElse(x)(valueBot) { _.objval.value }
        else
          valueBot
      val protoLocSet = globalObj.getOrElse(IPrototype)(AbsLoc.Bot) { _.value.locset }
      val (v2, excSet) =
        if (AbsBool.False <= (globalObj domIn x)) {
          val excSet = protoLocSet.foldLeft(ExceptionSetEmpty)((tmpExcSet, protoLoc) => {
            if (AbsBool.False <= hasProperty(protoLoc, AbsString.alpha(x))) {
              tmpExcSet + ReferenceError
            } else tmpExcSet
          })
          val v3 = protoLocSet.foldLeft(valueBot)((tmpVal, protoLoc) => {
            if (AbsBool.True <= hasProperty(protoLoc, AbsString.alpha(x))) {
              tmpVal + this.proto(protoLoc, AbsString.alpha(x))
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

  def lookupBaseGlobal(x: String): AbsLoc = {
    val globalObj = this.getOrElse(BuiltinGlobal.loc, AbsObjectUtil.Bot)
    val isDomIn = (globalObj domIn x)
    val locSet1 =
      if (AbsBool.True <= isDomIn)
        AbsLoc.alpha(BuiltinGlobal.loc)
      else
        AbsLoc.Bot
    val locSet2 =
      if (AbsBool.False <= isDomIn) {
        val protoLocSet = globalObj.getOrElse(IPrototype)(AbsLoc.Bot) { _.value.locset }
        protoLocSet.foldLeft(AbsLoc.Bot)((res, protoLoc) => {
          res + this.protoBase(protoLoc, AbsString.alpha(x))
        })
      } else {
        AbsLoc.Bot
      }
    locSet1 + locSet2
  }

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def propStore(loc: Loc, absStr: AbsString, value: AbsValue): Heap = {
    val findingObj = this.getOrElse(loc, AbsObjectUtil.Bot)
    val objDomIn = (findingObj domIn absStr)
    if (objDomIn == AbsBool.Top) {
      val oldObjV: DataProperty = findingObj.getOrElse(absStr)(DataPropertyUtil.Bot) { _.objval }
      val newObjV = DataPropertyUtil(value)(
        oldObjV.writable + AbsBool.True,
        oldObjV.enumerable + AbsBool.True,
        oldObjV.configurable + AbsBool.True
      )
      this.update(loc, findingObj.update(absStr, PropValue(newObjV)))
    } else if (objDomIn == AbsBool.True) {
      val oldObjV: DataProperty = findingObj.getOrElse(absStr)(DataPropertyUtil.Bot) { _.objval }
      val newObjV = oldObjV.copyWith(value)
      this.update(loc, findingObj.update(absStr, PropValue(newObjV)))
    } else if (objDomIn == AbsBool.False) {
      val newObjV = DataPropertyUtil(value)(AbsBool.True, AbsBool.True, AbsBool.True)
      this.update(loc, findingObj.update(absStr, PropValue(newObjV)))
    } else {
      Heap.Bot
    }
  }

  def varStoreGlobal(x: String, value: AbsValue): Heap = {
    val globalLoc = BuiltinGlobal.loc
    val obj = this.getOrElse(globalLoc, AbsObjectUtil.Bot)
    val h1 =
      if (AbsBool.False <= (obj domIn x))
        this.propStore(globalLoc, AbsString.alpha(x), value)
      else Heap.Bot
    val h2 =
      if (AbsBool.True <= (obj domIn x)) {
        val oldObjVal = obj.getOrElse(x)(DataPropertyUtil.Bot) { _.objval }
        val newObjVal = oldObjVal.copyWith(value)
        this.update(globalLoc, obj.update(x, PropValue(newObjVal)))
      } else Heap.Bot
    h1 + h2
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, absStr: AbsString): (Heap, AbsBool) = {
    getOrElse[(Heap, AbsBool)](loc)((this, AbsBool.Bot))(_ => {
      val test = hasOwnProperty(loc, absStr)
      val targetObj = this.getOrElse(loc, AbsObjectUtil.Bot)
      val isConfigurable = targetObj.getOrElse(absStr)(AbsBool.Bot) { _.objval.configurable }
      val (h1, b1) =
        if ((AbsBool.True <= test) && (AbsBool.False <= isConfigurable))
          (this, AbsBool.False)
        else
          (Heap.Bot, AbsBool.Bot)
      val (h2, b2) =
        if (((AbsBool.True <= test) && (AbsBool.False != isConfigurable))
          || AbsBool.False <= test)
          (this.update(loc, (targetObj - absStr)), AbsBool.True)
        else
          (Heap.Bot, AbsBool.Bot)
      (h1 + h2, b1 + b2)
    })
  }
}
