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

////////////////////////////////////////////////////////////////////////////////
// concrete heap type
////////////////////////////////////////////////////////////////////////////////
trait Heap // TODO

////////////////////////////////////////////////////////////////////////////////
// heap abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsHeap extends AbsDomain[Heap, AbsHeap] {
  // lookup
  def get(loc: Loc): AbsObject
  def get(locSet: AbsLoc): AbsObject

  // heap update
  def weakUpdate(loc: Loc, obj: AbsObject): AbsHeap
  def update(loc: Loc, obj: AbsObject): AbsHeap

  // remove location
  def remove(loc: Loc): AbsHeap

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): AbsHeap
  def oldify(addr: Address): AbsHeap
  def domIn(loc: Loc): Boolean

  // toString
  def toStringAll: String
  def toStringLoc(loc: Loc): Option[String]

  // predicates
  def hasConstruct(loc: Loc): AbsBool
  def hasInstance(loc: Loc): AbsBool
  def isArray(loc: Loc): AbsBool
  def isObject(loc: Loc): Boolean
  def canPutVar(x: String): AbsBool

  // proto
  def protoBase(loc: Loc, absStr: AbsString): AbsLoc

  // store
  def propStore(loc: Loc, absStr: AbsString, value: AbsValue): AbsHeap

  // update location
  def delete(loc: Loc, absStr: AbsString): (AbsHeap, AbsBool)

  // get all map
  def getMap: Option[Map[Loc, AbsObject]]
}

trait AbsHeapUtil extends AbsDomainUtil[Heap, AbsHeap] {
  def apply(map: Map[Loc, AbsObject]): AbsHeap
}

////////////////////////////////////////////////////////////////////////////////
// default heap abstract domain
////////////////////////////////////////////////////////////////////////////////

object DefaultHeap extends AbsHeapUtil {
  case object Top extends Dom
  case class HeapMap(
    val map: Map[Loc, AbsObject] = HashMap()
  ) extends Dom
  lazy val Bot: AbsHeap = HeapMap()

  def alpha(heap: Heap): AbsHeap = Top // TODO more precise

  def apply(map: Map[Loc, AbsObject]): AbsHeap = HeapMap(map)

  sealed abstract class Dom extends AbsHeap {
    def gamma: ConSet[Heap] = ConInf() // TODO more precise

    def getSingle: ConSingle[Heap] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsHeap): Boolean = (this, check(that)) match {
      case (_, Top) => true
      case (Top, _) => false
      case (left: HeapMap, right: HeapMap) =>
        if (left.map eq right.map) true
        else if (left.map.size > right.map.size) false
        else if (left.map.isEmpty) true
        else if (right.map.isEmpty) false
        else if (!(left.map.keySet subsetOf right.map.keySet)) false
        else right.map.forall((kv) => {
          val (l, obj) = kv
          left.map.get(l) match {
            case Some(leftObj) => leftObj <= obj
            case None => false
          }
        })
    }

    def +(that: AbsHeap): AbsHeap = (this, check(that)) match {
      case (Top, _) | (_, Top) => Top
      case (left: HeapMap, right: HeapMap) =>
        val newMap =
          if (left.map eq right.map) left.map
          else if (left.isBottom) right.map
          else if (right.isBottom) left.map
          else {
            val joinKeySet = left.map.keySet ++ right.map.keySet
            joinKeySet.foldLeft(HashMap[Loc, AbsObject]())((m, key) => {
              val joinObj = (left.map.get(key), right.map.get(key)) match {
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
        HeapMap(newMap)
    }

    def <>(that: AbsHeap): AbsHeap = (this, check(that)) match {
      case (left, Top) => left
      case (Top, right) => right
      case (left: HeapMap, right: HeapMap) =>
        val newMap: Map[Loc, AbsObject] =
          if (left.map eq right.map) left.map
          else if (left.map.isEmpty) HashMap()
          else if (right.map.isEmpty) HashMap()
          else {
            right.map.foldLeft(left.map)(
              (m, kv) => kv match {
                case (k, v) => m.get(k) match {
                  case None => m - k
                  case Some(vv) => m + (k -> (v <> vv))
                }
              }
            )
          }
        HeapMap(newMap)
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Top => "Top"
      case heap @ HeapMap(map) =>
        val s = new StringBuilder
        if (heap.isBottom) s.append("⊥Heap")
        else {
          val sortedSeq =
            map.toSeq.filter { case (loc, _) => filter(loc) }
              .sortBy { case (loc, _) => loc }
          sortedSeq.map {
            case (loc, obj) => s.append(toStringLoc(loc, obj)).append(LINE_SEP)
          }
        }
        s.toString
    }

    override def toString: String = {
      buildString(loc => loc match {
        case Loc(ProgramAddr(_), _) => true
        case BuiltinGlobal.loc => true
        case _ => false
      }).toString
    }

    def get(loc: Loc): AbsObject = this match {
      case Top => AbsObject.Top
      case HeapMap(map) => map.get(loc) match {
        case Some(obj) => obj
        case None => AbsObject.Bot
      }
    }

    def get(locSet: AbsLoc): AbsObject = locSet.foldLeft(AbsObject.Bot) {
      case (obj, loc) => obj + get(loc)
    }

    private def weakUpdated(m: Map[Loc, AbsObject], loc: Loc, newObj: AbsObject): Map[Loc, AbsObject] = m.get(loc) match {
      case Some(oldObj) => m.updated(loc, oldObj + newObj)
      case None => m.updated(loc, newObj)
    }

    def weakUpdate(loc: Loc, obj: AbsObject): AbsHeap = this match {
      case Top => Top
      case heap @ HeapMap(map) =>
        if (!heap.isBottom) HeapMap(weakUpdated(map, loc, obj))
        else heap
    }

    def update(loc: Loc, obj: AbsObject): AbsHeap = this match {
      case Top => Top
      case heap @ HeapMap(map) =>
        if (!heap.isBottom) {
          loc.recency match {
            case Recent =>
              if (obj.isBottom) AbsHeap.Bot
              else HeapMap(map.updated(loc, obj))
            case Old =>
              if (obj.isBottom) heap.get(loc).fold(AbsHeap.Bot) { _ => heap }
              else HeapMap(weakUpdated(map, loc, obj))
          }
        } else heap
    }

    def remove(loc: Loc): AbsHeap = this match {
      case Top => Top
      case HeapMap(map) => HeapMap(map - loc)
    }

    def subsLoc(locR: Loc, locO: Loc): AbsHeap = this match {
      case Top => Top
      case HeapMap(map) =>
        val newMap =
          if (map.isEmpty) map
          else {
            map.foldLeft(Map[Loc, AbsObject]())((m, kv) => {
              val (l, obj) = kv
              m + (l -> obj.subsLoc(locR, locO))
            })
          }
        HeapMap(newMap)
    }

    def oldify(addr: Address): AbsHeap = this match {
      case Top => Top
      case heap @ HeapMap(map) =>
        val locR = Loc(addr, Recent)
        val locO = Loc(addr, Old)
        if (heap domIn locR) {
          update(locO, get(locR)).remove(locR).subsLoc(locR, locO)
        } else {
          subsLoc(locR, locO)
        }
    }

    def domIn(loc: Loc): Boolean = this match {
      case Top => true
      case HeapMap(map) => map.contains(loc)
    }

    def toStringAll: String = {
      buildString(_ => true).toString
    }

    private def toStringLoc(loc: Loc, obj: AbsObject): String = {
      val s = new StringBuilder
      val keyStr = loc.toString + " -> "
      s.append(keyStr)
      Useful.indentation(s, obj.toString, keyStr.length)
      s.toString
    }

    def toStringLoc(loc: Loc): Option[String] = this match {
      case Top => Some(toStringLoc(loc, AbsObject.Top))
      case HeapMap(map) => map.get(loc).map(toStringLoc(loc, _))
    }

    ////////////////////////////////////////////////////////////////
    // Predicates
    ////////////////////////////////////////////////////////////////
    def hasConstruct(loc: Loc): AbsBool = {
      val isDomIn = get(loc).fold(AbsBool.False) { obj => (obj contains IConstruct) }
      val b1 =
        if (AbsBool.True <= isDomIn) AbsBool.True
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False <= isDomIn) AbsBool.False
        else AbsBool.Bot
      b1 + b2
    }

    def hasInstance(loc: Loc): AbsBool = {
      val isDomIn = get(loc).fold(AbsBool.False) { obj => (obj contains IHasInstance) }
      val b1 =
        if (AbsBool.True <= isDomIn) AbsBool.True
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False <= isDomIn) AbsBool.False
        else AbsBool.Bot
      b1 + b2
    }

    def isArray(loc: Loc): AbsBool = {
      val className = get(loc)(IClass).value.pvalue.strval
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

    def isObject(loc: Loc): Boolean = !get(loc).isBottom

    def canPutVar(x: String): AbsBool = {
      val globalLoc = BuiltinGlobal.loc
      val globalObj = get(globalLoc)
      val domIn = globalObj contains x
      val b1 =
        if (AbsBool.True <= domIn) globalObj(x).writable
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False <= domIn) get(globalLoc).CanPut(x, this)
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
    def propStore(loc: Loc, absStr: AbsString, value: AbsValue): AbsHeap = {
      //TODO: propagate type error of [[Put]] to semantics
      val findingObj = this.get(loc)
      val (obj, _) = findingObj.Put(absStr, value, true, this)
      this.update(loc, obj)
    }

    ////////////////////////////////////////////////////////////////
    // delete
    ////////////////////////////////////////////////////////////////
    def delete(loc: Loc, absStr: AbsString): (AbsHeap, AbsBool) = {
      get(loc).fold[(AbsHeap, AbsBool)]((this, AbsBool.Bot))(_ => {
        val targetObj = this.get(loc)
        val (newObj, asuccess) = targetObj.Delete(absStr)
        (this.update(loc, newObj), asuccess)
      })
    }

    def getMap: Option[Map[Loc, AbsObject]] = this match {
      case Top => None
      case HeapMap(map) => Some(map)
    }
  }
}
