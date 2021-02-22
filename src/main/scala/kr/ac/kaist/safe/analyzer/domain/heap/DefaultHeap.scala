/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.model.GLOBAL_LOC
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.cfg.{ CFGId, GlobalVar, CFG }

import spray.json._

// default heap abstract domain
object DefaultHeap extends HeapDomain {
  case object Top extends Elem
  case object Bot extends Elem
  val Empty: Elem = HeapMap(Map(), LocSet.Bot)
  case class HeapMap(map: Map[Loc, AbsObj], merged: LocSet) extends Elem

  def alpha(heap: Heap): Elem = {
    val map = heap.map.foldLeft(Map[Loc, AbsObj]()) {
      case (map, (loc, obj)) => map + (loc -> AbsObj(obj))
    }
    HeapMap(map, LocSet.Bot)
  }

  def apply(map: Map[Loc, AbsObj], merged: LocSet): Elem =
    HeapMap(map, merged)

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Heap] = ConInf // TODO more precise

    def getSingle: ConSingle[Heap] = ConMany // TODO more precise

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) | (Bot, _) => true
      case (Top, _) | (_, Bot) => false
      case (left: HeapMap, right: HeapMap) => {
        val mapB = left.map.compareWithPartialOrder(right.map)(_ ⊑ _)
        val mergedB = left.merged ⊑ right.merged
        mapB && mergedB
      }
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (left, Bot) => left
      case (Bot, right) => right
      case (left: HeapMap, right: HeapMap) =>
        val newMap = left.map.unionWithIdem(right.map)(_ ⊔ _)
        val newMerged = left.merged ⊔ right.merged
        HeapMap(newMap, newMerged)
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (left, Top) => left
      case (Top, right) => right
      case (Bot, _) | (_, Bot) => Bot
      case (left: HeapMap, right: HeapMap) =>
        val newMap = left.map.intersectWithIdem(right.map)(_ ⊓ _)
        val newMerged = left.merged ⊓ right.merged
        HeapMap(newMap, newMerged)
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Top => "Top"
      case Bot => "⊥Heap"
      case heap @ HeapMap(map, _) =>
        val s = new StringBuilder
        val sortedSeq = map.keySet.toSeq.filter(filter).sorted
        sortedSeq.foreach(loc => s.append(toStringLoc(loc)).append(LINE_SEP))
        s.toString
    }

    override def toString: String = {
      buildString(loc => loc.isUser || loc == GLOBAL_LOC)
      //buildString(loc => !(AbsStr("Arguments") ⊑ this.get(loc)(IClass).value.pvalue.strval))
    }

    def toJSON(implicit uomap: UIdObjMap): JsValue = this match {
      case Top => fail
      case Bot => fail
      case HeapMap(m, merged) => JsObject(m.map {
        case (loc, obj) =>
          val k = loc.toString
          val v =
            if (merged contains loc) uomap.toJSON(obj)
            else if (loc == GLOBAL_LOC) globalLocJSON
            else obj.toJSON
          k -> v
      })
    }

    def get(loc: Loc): AbsObj = this match {
      case Top => AbsObj.Top
      case Bot => AbsObj.Bot
      case HeapMap(map, _) => map.get(loc) match {
        case Some(obj) => obj
        case None => AbsObj.Bot
      }
    }

    def get(locSet: LocSet): AbsObj = locSet.foldLeft(AbsObj.Bot) {
      case (obj, loc) => obj ⊔ get(loc)
    }

    private def weakUpdated(m: Map[Loc, AbsObj], loc: Loc, newObj: AbsObj): Map[Loc, AbsObj] = m.get(loc) match {
      case Some(oldObj) => m + (loc -> (oldObj ⊔ newObj))
      case None => m + (loc -> newObj)
    }

    def weakUpdate(loc: Loc, obj: AbsObj): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map, merged) => HeapMap(weakUpdated(map, loc, obj), merged)
    }

    def update(loc: Loc, obj: AbsObj): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map, merged) =>
        if (isConcrete(loc)) {
          if (obj.isBottom) Bot
          else HeapMap(map + (loc -> obj), merged)
        } else {
          if (obj.isBottom) get(loc).fold[Elem](Bot) { _ => this }
          else HeapMap(weakUpdated(map, loc, obj), merged)
        }
    }

    def subsLoc(from: Loc, to: Loc): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map, merged) => {
        val (newMap, newMerged) = (map.get(from) match {
          case Some(fromObj) => {
            val (newObj, newMerged) = map.get(to) match {
              case Some(toObj) => (fromObj ⊔ toObj, merged + to)
              case None => (fromObj, merged)
            }
            (map - from + (to -> newObj), newMerged)
          }
          case None => (map, merged)
        })
        HeapMap(
          newMap.map { case (k, v) => k -> v.subsLoc(from, to) },
          newMerged.subsLoc(from, to)
        )
      }
    }

    def remove(locs: Set[Loc]): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map, merged) => HeapMap(
        (map -- locs).map { case (k, v) => k -> v.remove(locs) },
        merged.remove(locs)
      )
    }

    def alloc(loc: Loc): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map, merged) => {
        val newMerged =
          if (map contains loc) merged + loc
          else merged
        HeapMap(map, newMerged)
      }
    }

    def getLocSet: LocSet = this match {
      case Top => LocSet.Top
      case Bot => LocSet.Bot
      case HeapMap(map, _) => LocSet(map.keySet)
    }

    def getMerged: LocSet = this match {
      case Top => LocSet.Top
      case Bot => LocSet.Bot
      case HeapMap(_, merged) => merged
    }

    def domIn(loc: Loc): Boolean = this match {
      case Top => true
      case Bot => false
      case HeapMap(map, _) => map.contains(loc)
    }

    def toStringAll: String = {
      buildString(_ => true).toString
    }

    private def toStringLoc(loc: Loc, obj: AbsObj, con: Boolean): String = {
      val s = new StringBuilder
      val keyStr = loc.toString + (if (con) " -> " else " => ")
      s.append(keyStr)
      Useful.indentation(s, obj.toString, keyStr.length)
      s.toString
    }

    def toStringLoc(loc: Loc): Option[String] = this match {
      case Top => Some(toStringLoc(loc, AbsObj.Top, false))
      case Bot => None
      case HeapMap(map, _) => map.get(loc).map(toStringLoc(loc, _, isConcrete(loc)))
    }

    ////////////////////////////////////////////////////////////////
    // Predicates
    ////////////////////////////////////////////////////////////////
    def hasConstruct(loc: Loc): AbsBool = {
      val isElemIn = get(loc).fold(AbsBool.False) { obj => (obj contains IConstruct) }
      val b1 =
        if (AbsBool.True ⊑ isElemIn) AbsBool.True
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False ⊑ isElemIn) AbsBool.False
        else AbsBool.Bot
      b1 ⊔ b2
    }

    def hasInstance(loc: Loc): AbsBool = {
      val isElemIn = get(loc).fold(AbsBool.False) { obj => (obj contains IHasInstance) }
      val b1 =
        if (AbsBool.True ⊑ isElemIn) AbsBool.True
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False ⊑ isElemIn) AbsBool.False
        else AbsBool.Bot
      b1 ⊔ b2
    }

    def isArray(loc: Loc): AbsBool = {
      val className = get(loc)(IClass).value.pvalue.strval
      val arrayAbsStr = AbsStr.alpha("Array")
      val b1 =
        if (arrayAbsStr ⊑ className)
          AbsBool.True
        else
          AbsBool.Bot
      val b2 =
        if (arrayAbsStr != className)
          AbsBool.False
        else
          AbsBool.Bot
      b1 ⊔ b2
    }

    def isObject(loc: Loc): Boolean = !get(loc).isBottom

    def canPutVar(x: String): AbsBool = {
      val globalLoc = GLOBAL_LOC
      val globalObj = get(globalLoc)
      val domIn = globalObj contains x
      val b1 =
        if (AbsBool.True ⊑ domIn) globalObj(x).writable
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False ⊑ domIn) get(globalLoc).CanPut(x, this)
        else AbsBool.Bot
      b1 ⊔ b2
    }

    ////////////////////////////////////////////////////////////////
    // Proto
    ////////////////////////////////////////////////////////////////
    def proto(loc: Loc, absStr: AbsStr): AbsValue = {
      var visited = LocSet.Bot
      val valueBot = AbsValue.Bot
      def visit(currentLoc: Loc): AbsValue = {
        if (visited.contains(currentLoc)) valueBot
        else {
          visited += currentLoc
          val test = this.get(currentLoc) contains absStr
          val v1 =
            if (AbsBool.True ⊑ test) {
              this.get(currentLoc)(absStr).value
            } else {
              valueBot
            }
          val v2 =
            if (AbsBool.False ⊑ test) {
              val protoV = this.get(currentLoc)(IPrototype).value
              val v3 = protoV.pvalue.nullval.fold(valueBot)(_ => {
                AbsValue(Undef)
              })
              v3 ⊔ protoV.locset.foldLeft(valueBot)((v, protoLoc) => {
                v ⊔ visit(protoLoc)
              })
            } else {
              valueBot
            }
          v1 ⊔ v2
        }
      }
      visit(loc)
    }

    def protoBase(loc: Loc, absStr: AbsStr): LocSet = {
      var visited = LocSet.Bot
      def visit(l: Loc): LocSet = {
        if (visited.contains(l)) LocSet.Bot
        else {
          visited += l
          val obj = this.get(l)
          val isElemIn = (obj contains absStr)
          val locSet1 =
            if (AbsBool.True ⊑ isElemIn) LocSet(l)
            else LocSet.Bot
          val locSet2 =
            if (AbsBool.False ⊑ isElemIn) {
              val protoLocSet = obj(IPrototype).value.locset
              protoLocSet.foldLeft(LocSet.Bot)((res, protoLoc) => {
                res ⊔ visit(protoLoc)
              })
            } else {
              LocSet.Bot
            }
          locSet1 ⊔ locSet2
        }
      }
      visit(loc)
    }

    ////////////////////////////////////////////////////////////////
    // Store
    ////////////////////////////////////////////////////////////////
    def propStore(loc: Loc, absStr: AbsStr, value: AbsValue): Elem = {
      //TODO: propagate type error of [[Put]] to semantics
      val findingObj = this.get(loc)
      val (obj, _) = findingObj.Put(absStr, value, true, this)
      this.update(loc, obj)
    }

    ////////////////////////////////////////////////////////////////
    // delete
    ////////////////////////////////////////////////////////////////
    def delete(loc: Loc, absStr: AbsStr): (Elem, AbsBool) = {
      get(loc).fold[(Elem, AbsBool)]((this, AbsBool.Bot))(_ => {
        val targetObj = this.get(loc)
        val (newObj, asuccess, _) = targetObj.Delete(absStr)
        (this.update(loc, newObj), asuccess)
      })
    }

    def getMap: Option[Map[Loc, AbsObj]] = this match {
      case Top => None
      case Bot => None
      case HeapMap(map, _) => Some(Map(map.toSeq: _*))
    }

    ////////////////////////////////////////////////////////////////
    // location status check
    ////////////////////////////////////////////////////////////////
    def isConcrete(loc: Loc): Boolean = this match {
      case Top => false
      case Bot => true
      case HeapMap(_, merged) => !(merged contains loc)
    }
  }

  def fromJSON(
    json: JsValue,
    cfg: CFG,
    prev: AbsState,
    locset: LocSet
  )(implicit uomap: UIdObjMap): Elem = {
    val fields = json.asJsObject.fields
    val mapFields = fields("map").asJsObject.fields
    var merged = prev.heap.getMerged ⊔ (prev.heap.getLocSet ⊓ locset)
    val prevMap: Map[Loc, AbsObj] = prev.heap match {
      case HeapMap(prevMap, _) => prevMap
      case _ => Map()
    }
    HeapMap(
      mapFields.foldLeft[Map[Loc, AbsObj]](prevMap)({
        case (acc, (k, v)) => acc + {
          val loc = Loc.parseString(k, cfg)
          val elems = v.asInstanceOf[JsArray].elements
          if (elems.length > 1) merged += loc
          val obj = elems.foldLeft(AbsObj.Bot) {
            case (x, y) => x ⊔ AbsObj.fromJSON(y, cfg)
          }
          loc -> obj
        }
      }),
      merged
    )
  }
}
