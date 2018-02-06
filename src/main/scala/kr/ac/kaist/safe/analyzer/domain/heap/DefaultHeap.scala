/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.errors.error.AbsHeapParseError
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashMap
import spray.json._

// default heap abstract domain
object DefaultHeap extends HeapDomain {
  case object Top extends Elem
  case object Bot extends Elem
  case class HeapMap(map: Map[Loc, AbsObj]) extends Elem

  def alpha(heap: Heap): Elem = {
    val map = heap.map.foldLeft[Map[Loc, AbsObj]](HashMap()) {
      case (map, (loc, obj)) => map + (loc -> AbsObj(obj))
    }
    HeapMap(map)
  }

  def apply(map: Map[Loc, AbsObj]): Elem = HeapMap(map)

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case JsString("⊥") => Bot
    case _ => HeapMap(json2map(v, Loc.fromJson, AbsObj.fromJson))
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Heap] = ConInf // TODO more precise

    def getSingle: ConSingle[Heap] = ConMany() // TODO more precise

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (_, Top) | (Bot, _) => true
      case (Top, _) | (_, Bot) => false
      case (left: HeapMap, right: HeapMap) =>
        (if (left.map eq right.map) true
        else if (left.map.size > right.map.size) false
        else if (left.map.isEmpty) true
        else if (right.map.isEmpty) false
        else if (!(left.map.keySet subsetOf right.map.keySet)) false
        else right.map.forall((kv) => {
          val (l, obj) = kv
          left.map.get(l) match {
            case Some(leftObj) => leftObj ⊑ obj
            case None => false
          }
        }))
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Top, _) | (_, Top) => Top
      case (left, Bot) => left
      case (Bot, right) => right
      case (left: HeapMap, right: HeapMap) =>
        val newMap =
          if (left.map eq right.map) left.map
          else if (left.isBottom) right.map
          else if (right.isBottom) left.map
          else {
            val joinKeySet = left.map.keySet ++ right.map.keySet
            joinKeySet.foldLeft(HashMap[Loc, AbsObj]())((m, key) => {
              val joinObj = (left.map.get(key), right.map.get(key)) match {
                case (Some(obj1), Some(obj2)) => Some(obj1 ⊔ obj2)
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

    def ⊓(that: Elem): Elem = (this, that) match {
      case (left, Top) => left
      case (Top, right) => right
      case (Bot, _) | (_, Bot) => Bot
      case (left: HeapMap, right: HeapMap) =>
        val newMap: Map[Loc, AbsObj] =
          if (left.map eq right.map) left.map
          else if (left.map.isEmpty) HashMap()
          else if (right.map.isEmpty) HashMap()
          else {
            right.map.foldLeft(left.map)(
              (m, kv) => kv match {
                case (k, v) => m.get(k) match {
                  case None => m - k
                  case Some(vv) => m + (k -> (v ⊓ vv))
                }
              }
            )
          }
        HeapMap(newMap)
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Top => "Top"
      case Bot => "⊥Heap"
      case heap @ HeapMap(map) =>
        val s = new StringBuilder
        val sortedSeq =
          map.toSeq.filter { case (loc, _) => filter(loc) }
            .sortBy { case (loc, _) => loc }
        sortedSeq.map {
          case (loc, obj) => s.append(toStringLoc(loc, obj, isConcrete(loc))).append(LINE_SEP)
        }
        s.toString
    }

    override def toString: String = {
      buildString(loc => loc.isUser || loc == BuiltinGlobal.loc)
    }

    def get(loc: Loc): AbsObj = this match {
      case Top => AbsObj.Top
      case Bot => AbsObj.Bot
      case HeapMap(map) => map.get(loc) match {
        case Some(obj) => obj
        case None => AbsObj.Bot
      }
    }

    def get(locSet: AbsLoc): AbsObj = locSet.foldLeft(AbsObj.Bot) {
      case (obj, loc) => obj ⊔ get(loc)
    }

    private def weakUpdated(m: Map[Loc, AbsObj], loc: Loc, newObj: AbsObj): Map[Loc, AbsObj] = m.get(loc) match {
      case Some(oldObj) => m.updated(loc, oldObj ⊔ newObj)
      case None => m.updated(loc, newObj)
    }

    def weakUpdate(loc: Loc, obj: AbsObj): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map) => HeapMap(weakUpdated(map, loc, obj))
    }

    def update(loc: Loc, obj: AbsObj): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map) =>
        if (isConcrete(loc)) {
          if (obj.isBottom) Bot
          else HeapMap(map.updated(loc, obj))
        } else {
          if (obj.isBottom) get(loc).fold[Elem](Bot) { _ => this }
          else HeapMap(weakUpdated(map, loc, obj))
        }
    }

    def remove(loc: Loc): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map) => HeapMap(map - loc)
    }

    def subsLoc(locR: Recency, locO: Recency): Elem = this match {
      case Top => Top
      case Bot => Bot
      case HeapMap(map) =>
        val newMap = map.foldLeft(Map[Loc, AbsObj]())((m, kv) => {
          val (l, obj) = kv
          m + (l -> obj.subsLoc(locR, locO))
        })
        HeapMap(newMap)
    }

    def oldify(loc: Loc): Elem = loc match {
      case locR @ Recency(subLoc, Recent) => this match {
        case Top => Top
        case Bot => Bot
        case heap @ HeapMap(map) => {
          val locO = Recency(subLoc, Old)
          if (heap domIn locR) {
            update(locO, get(locR)).remove(locR).subsLoc(locR, locO)
          } else {
            subsLoc(locR, locO)
          }
        }
      }
      case _ => this
    }

    def domIn(loc: Loc): Boolean = this match {
      case Top => true
      case Bot => false
      case HeapMap(map) => map.contains(loc)
    }

    def toStringAll: String = {
      buildString(_ => true).toString
    }

    private def toStringLoc(loc: Loc, obj: AbsObj, con: Boolean): String = {
      val s = new StringBuilder
      val keyStr = loc.toString + " -> "
      s.append(keyStr)
      Useful.indentation(s, obj.toString, keyStr.length)
      s.toString
    }

    def toStringLoc(loc: Loc): Option[String] = this match {
      case Top => Some(toStringLoc(loc, AbsObj.Top, false))
      case Bot => None
      case HeapMap(map) => map.get(loc).map(toStringLoc(loc, _, isConcrete(loc)))
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
      val globalLoc = BuiltinGlobal.loc
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
      var visited = AbsLoc.Bot
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

    def protoBase(loc: Loc, absStr: AbsStr): AbsLoc = {
      var visited = AbsLoc.Bot
      def visit(l: Loc): AbsLoc = {
        if (visited.contains(l)) AbsLoc.Bot
        else {
          visited += l
          val obj = this.get(l)
          val isElemIn = (obj contains absStr)
          val locSet1 =
            if (AbsBool.True ⊑ isElemIn) AbsLoc(l)
            else AbsLoc.Bot
          val locSet2 =
            if (AbsBool.False ⊑ isElemIn) {
              val protoLocSet = obj(IPrototype).value.locset
              protoLocSet.foldLeft(AbsLoc.Bot)((res, protoLoc) => {
                res ⊔ visit(protoLoc)
              })
            } else {
              AbsLoc.Bot
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
      case HeapMap(map) => Some(map)
    }

    ////////////////////////////////////////////////////////////////
    // location concrete check
    ////////////////////////////////////////////////////////////////
    def isConcrete(loc: Loc): Boolean = loc match {
      case Recency(_, Recent) => true
      case l if Loc.predConSet contains l => true
      case _ => false
    }

    def toJson: JsValue = this match {
      case Top => JsString("⊤")
      case Bot => JsString("⊥")
      case HeapMap(m) => JsArray(m.toSeq.map {
        case (k, v) => JsArray(k.toJson, v.toJson)
      }: _*)
    }
  }
}
