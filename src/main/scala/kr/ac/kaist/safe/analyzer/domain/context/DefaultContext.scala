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

import kr.ac.kaist.safe.analyzer.model._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.cfg._

import spray.json._

// default execution context abstract domain
object DefaultContext extends ContextDomain {
  case object Bot extends Elem
  case object Top extends Elem
  case class CtxMap(
    // TODO val varEnv: LexEnv // VariableEnvironment
    val map: LayeredMap[Loc, AbsLexEnv],
    val merged: LocSet,
    override val thisBinding: AbsValue // ThisBinding
  ) extends Elem
  lazy val Empty: Elem =
    CtxMap(LayeredMap(), LocSet.Bot, LocSet(GLOBAL_LOC))

  def alpha(ctx: Context): Elem = Top // TODO more precise

  def apply(
    map: Map[Loc, AbsLexEnv],
    merged: LocSet,
    thisBinding: AbsValue
  ): Elem = CtxMap(LayeredMap(map), merged, thisBinding)

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Context] = ConInf // TODO more precise

    def getSingle: ConSingle[Context] = ConMany // TODO more precise

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (_, Top) => true
      case (Top, _) => false
      case (left: CtxMap, right: CtxMap) => {
        val mapB = left.map.compareWithPartialOrder(right.map)(_ ⊑ _)
        val mergedB = left.merged ⊑ right.merged
        val thisB = left.thisBinding ⊑ right.thisBinding
        mapB && mergedB && thisB
      }
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (Top, _) | (_, Top) => Top
      case (left: CtxMap, right: CtxMap) => {
        val newMap = left.map.unionWithIdem(right.map)(_ ⊔ _)
        val newMerged = left.merged ⊔ right.merged
        val newThisBinding = left.thisBinding ⊔ right.thisBinding
        CtxMap(newMap, newMerged, newThisBinding)
      }
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Top, _) => that
      case (_, Top) => this
      case (left: CtxMap, right: CtxMap) => {
        val newMap = left.map.unionWithIdem(right.map)(_ ⊓ _)
        val newMerged = left.merged ⊓ right.merged
        val newThisBinding = left.thisBinding ⊓ right.thisBinding
        CtxMap(newMap, newMerged, newThisBinding)
      }
    }

    def apply(loc: Loc): Option[AbsLexEnv] = this match {
      case Bot => None
      case Top => Some(AbsLexEnv.Top)
      case CtxMap(map, _, _) => map.get(loc)
    }

    def apply(locSet: Set[Loc]): AbsLexEnv = locSet.foldLeft(AbsLexEnv.Bot) {
      case (envRec, loc) => envRec ⊔ getOrElse(loc, AbsLexEnv.Bot)
    }

    def apply(locSet: LocSet): AbsLexEnv = locSet.foldLeft(AbsLexEnv.Bot) {
      case (envRec, loc) => envRec ⊔ getOrElse(loc, AbsLexEnv.Bot)
    }

    def getOrElse(loc: Loc, default: AbsLexEnv): AbsLexEnv =
      this(loc) match {
        case Some(env) => env
        case None => default
      }

    def getOrElse[T](loc: Loc)(default: T)(f: AbsLexEnv => T): T = {
      this(loc) match {
        case Some(env) => f(env)
        case None => default
      }
    }

    private def weakUpdated(m: LayeredMap[Loc, AbsLexEnv], loc: Loc, newEnv: AbsLexEnv): LayeredMap[Loc, AbsLexEnv] =
      m.get(loc) match {
        case Some(oldEnv) => m + (loc -> (oldEnv ⊔ newEnv))
        case None => m + (loc -> newEnv)
      }

    def weakUpdate(loc: Loc, env: AbsLexEnv): Elem = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, merged, thisBinding) =>
        CtxMap(weakUpdated(map, loc, env), merged, thisBinding)
    }

    def update(loc: Loc, env: AbsLexEnv): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(map, _, _) => {
        if (isConcrete(loc)) {
          cmap.copy(map = map + (loc -> env))
        } else {
          cmap.copy(map = weakUpdated(map, loc, env))
        }
      }
    }

    def subsLoc(from: Loc, to: Loc): Elem = this match {
      case Top => Top
      case Bot => Bot
      case CtxMap(map, merged, thisBinding) => {
        val (newMap, newMerged) = (map.get(from) match {
          case Some(fromEnv) => {
            val (newEnv, newMerged) = map.get(to) match {
              case Some(toEnv) => (fromEnv ⊔ toEnv, merged + to)
              case None => (fromEnv, merged)
            }
            (map - from + (to -> newEnv), newMerged)
          }
          case None => (map, merged)
        })
        CtxMap(
          newMap.valueMap(_.subsLoc(from, to)),
          newMerged.subsLoc(from, to),
          thisBinding.subsLoc(from, to)
        )
      }
    }

    def remove(locs: Set[Loc]): Elem = this match {
      case Top => Top
      case Bot => Bot
      case CtxMap(map, merged, thisBinding) => CtxMap(
        (map -- locs).valueMap(_.remove(locs)),
        merged.remove(locs),
        thisBinding.remove(locs)
      )
    }

    def alloc(loc: Loc): Elem = this match {
      case Top => Top
      case Bot => Bot
      case CtxMap(map, merged, thisBinding) => {
        val newMerged =
          if (map contains loc) merged + loc
          else merged
        CtxMap(map, newMerged, thisBinding)
      }
    }

    def getLocSet: LocSet = this match {
      case Top => LocSet.Top
      case Bot => LocSet.Bot
      case CtxMap(map, _, _) => LocSet(map.keySet)
    }

    def domIn(loc: Loc): Boolean = this match {
      case Bot => false
      case Top => true
      case CtxMap(map, _, _) => map.contains(loc)
    }

    def setThisBinding(thisBinding: AbsValue): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(_, _, _) => cmap.copy(thisBinding = thisBinding)
    }

    def getMap: Map[Loc, AbsLexEnv] = this match {
      case Bot => Map()
      case Top => Map() // TODO it is not sound
      case CtxMap(map, _, _) => Map(map.toSeq: _*)
    }

    def thisBinding: AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top => AbsValue.Top
      case CtxMap(_, _, thisBinding) => thisBinding
    }

    override def toString: String = {
      buildString(_ => true).toString
    }

    def toJSON(implicit uomap: UIdObjMap): JsValue = this match {
      case Bot => fail
      case Top => fail
      case CtxMap(map, merged, thisBinding) => JsObject(
        "map" -> JsObject(map.map {
          case (loc, env) =>
            val k = loc.toString
            val v =
              if (merged contains loc) uomap.toJSON(env)
              else env.toJSON
            k -> v
        }),
        "merged" -> JsString("__BOT__"),
        "thisBinding" -> thisBinding.toJSON
      )
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Bot => "⊥Elem"
      case Top => "Top"
      case CtxMap(map, _, thisBinding) => {
        val s = new StringBuilder
        val sortedSeq = map.keySet.toSeq.filter(filter).sorted
        sortedSeq.foreach(loc => s.append(toStringLoc(loc)).append(LINE_SEP))
        s.append(s"this: $thisBinding")
        s.toString
      }
    }

    def toStringLoc(loc: Loc): Option[String] = this match {
      case Bot => None
      case Top => Some(toStringLoc(loc, AbsLexEnv.Top, true))
      case CtxMap(map, _, _) => map.get(loc).map(toStringLoc(loc, _, isConcrete(loc)))
    }

    private def toStringLoc(loc: Loc, env: AbsLexEnv, con: Boolean): String = {
      val s = new StringBuilder
      val keyStr = loc.toString + " -> "
      s.append(keyStr)
      Useful.indentation(s, env.toString, keyStr.length)
      s.toString
    }

    ////////////////////////////////////////////////////////////////
    // delete
    ////////////////////////////////////////////////////////////////
    def delete(loc: Loc, str: String): (Elem, AbsBool) = {
      getOrElse(loc)((this, AbsBool.Bot))(_ => {
        val test = hasOwnProperty(loc, str)
        if (AbsBool.True ⊑ test)
          (this, AbsBool.False)
        else
          (Bot, AbsBool.Bot)
      })
    }

    private def hasOwnProperty(loc: Loc, str: String): AbsBool = {
      (this.getOrElse(loc, AbsLexEnv.Bot).record.decEnvRec HasBinding str)
    }

    ////////////////////////////////////////////////////////////////
    // pure local environment
    ////////////////////////////////////////////////////////////////
    def pureLocal: AbsLexEnv =
      getOrElse(PURE_LOCAL, AbsLexEnv.Bot)
    def subsPureLocal(env: AbsLexEnv): Elem =
      update(PURE_LOCAL, env)

    ////////////////////////////////////////////////////////////////
    // location concrete check
    ////////////////////////////////////////////////////////////////
    def isConcrete(loc: Loc): Boolean = this match {
      case Top => false
      case Bot => true
      case CtxMap(_, merged, _) => !(merged contains loc)
    }
  }

  def fromJSON(json: JsValue, cfg: CFG)(implicit uomap: UIdObjMap): Elem = {
    val fields = json.asJsObject().fields
    val mapFields = fields("map").asJsObject.fields
    CtxMap(
      mapFields.foldLeft[LayeredMap[Loc, AbsLexEnv]](LayeredMap())({
        case (acc, (k, v)) => acc + (Loc.parseString(k, cfg) -> AbsLexEnv.fromJSON(v, cfg))
      }),
      LocSet.Bot,
      AbsValue.fromJSON(fields("thisBinding"), cfg)
    )
  }
}
