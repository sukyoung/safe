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

// default execution context abstract domain
object DefaultContext extends ContextDomain {
  private val EmptyMap: Map[Loc, AbsLexEnv] = Map()

  case object Bot extends Elem
  case object Top extends Elem
  case class CtxMap(
    // TODO val varEnv: LexEnv // VariableEnvironment
    val map: Map[Loc, AbsLexEnv],
    val merged: LocSet,
    val changed: LocSet,
    override val thisBinding: AbsValue // ThisBinding
  ) extends Elem
  lazy val Empty: Elem =
    CtxMap(EmptyMap, LocSet.Bot, LocSet.Bot, LocSet(GLOBAL_LOC))

  def alpha(ctx: Context): Elem = Top // TODO more precise

  def apply(
    map: Map[Loc, AbsLexEnv],
    merged: LocSet,
    thisBinding: AbsValue
  ): Elem = CtxMap(Map(map.toSeq: _*), merged, LocSet.Bot, thisBinding)

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
        val newChanged = left.changed ⊔ right.changed
        val newThisBinding = left.thisBinding ⊔ right.thisBinding
        CtxMap(newMap, newMerged, newChanged, newThisBinding)
      }
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Top, _) => that
      case (_, Top) => this
      case (left: CtxMap, right: CtxMap) => {
        val newMap = left.map.unionWithIdem(right.map)(_ ⊓ _)
        val newMerged = left.merged ⊓ right.merged
        val newChanged = left.changed ⊓ right.changed
        val newThisBinding = left.thisBinding ⊓ right.thisBinding
        CtxMap(newMap, newMerged, newChanged, newThisBinding)
      }
    }

    def apply(loc: Loc): Option[AbsLexEnv] = this match {
      case Bot => None
      case Top => Some(AbsLexEnv.Top)
      case CtxMap(map, _, _, _) => map.get(loc)
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

    private def weakUpdated(m: Map[Loc, AbsLexEnv], loc: Loc, newEnv: AbsLexEnv): Map[Loc, AbsLexEnv] =
      m.get(loc) match {
        case Some(oldEnv) => m.updated(loc, oldEnv ⊔ newEnv)
        case None => m.updated(loc, newEnv)
      }

    def weakUpdate(loc: Loc, env: AbsLexEnv): Elem = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, merged, changed, thisBinding) =>
        CtxMap(weakUpdated(map, loc, env), merged, changed + loc, thisBinding)
    }

    def update(loc: Loc, env: AbsLexEnv): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(map, _, changed, _) => {
        if (isConcrete(loc)) {
          cmap.copy(map = map.updated(loc, env), changed = changed + loc)
        } else {
          cmap.copy(map = weakUpdated(map, loc, env), changed = changed + loc)
        }
      }
    }

    def subsLoc(from: Loc, to: Loc): Elem = this match {
      case Top => Top
      case Bot => Bot
      case CtxMap(map, merged, changed, thisBinding) => {
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
          newMap.map { case (k, v) => k -> v.subsLoc(from, to) },
          newMerged.subsLoc(from, to),
          changed + from + to,
          thisBinding.subsLoc(from, to)
        )
      }
    }

    def remove(locs: Set[Loc]): Elem = this match {
      case Top => Top
      case Bot => Bot
      case CtxMap(map, merged, changed, thisBinding) => CtxMap(
        (map -- locs).map { case (k, v) => k -> v.remove(locs) },
        merged.remove(locs),
        changed ⊔ LocSet(locs),
        thisBinding.remove(locs)
      )
    }

    def alloc(loc: Loc): Elem = this match {
      case Top => Top
      case Bot => Bot
      case CtxMap(map, merged, changed, thisBinding) => {
        val newMerged =
          if (map contains loc) merged + loc
          else merged
        CtxMap(map, newMerged, changed + loc, thisBinding)
      }
    }

    def getLocSet: LocSet = this match {
      case Top => LocSet.Top
      case Bot => LocSet.Bot
      case CtxMap(map, _, _, _) => LocSet(map.keySet)
    }

    def domIn(loc: Loc): Boolean = this match {
      case Bot => false
      case Top => true
      case CtxMap(map, _, _, _) => map.contains(loc)
    }

    def setThisBinding(thisBinding: AbsValue): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(_, _, _, _) => cmap.copy(thisBinding = thisBinding)
    }

    def getMap: Map[Loc, AbsLexEnv] = this match {
      case Bot => Map()
      case Top => Map() // TODO it is not sound
      case CtxMap(map, _, _, _) => map
    }

    def thisBinding: AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top => AbsValue.Top
      case CtxMap(_, _, _, thisBinding) => thisBinding
    }

    override def toString: String = {
      buildString(_ => true).toString
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Bot => "⊥Elem"
      case Top => "Top"
      case CtxMap(map, _, _, thisBinding) => {
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
      case CtxMap(map, _, _, _) => map.get(loc).map(toStringLoc(loc, _, isConcrete(loc)))
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
      case CtxMap(_, merged, _, _) => !(merged contains loc)
    }

    def isChanged(loc: Loc): Boolean = this match {
      case Top => true
      case Bot => false
      case CtxMap(_, _, changed, _) => changed contains loc
    }

    // delete changed info
    def cleanChanged: Elem = this match {
      case CtxMap(map, merged, _, thisBinding) =>
        CtxMap(map, merged, LocSet(PURE_LOCAL), thisBinding)
      case _ => this
    }

    // applied changed information
    def <<(that: Elem): Elem = (this, that) match {
      case (left: CtxMap, right: CtxMap) => {
        val newMap = right.changed.foldLeft(left.map) {
          case (map, loc) => right.map.get(loc) match {
            case Some(env) => map + (loc -> env)
            case None => map - loc
          }
        }
        val newMerged = right.merged
        val newChanged = left.changed ⊔ right.changed
        val newThisBinding = right.thisBinding
        CtxMap(newMap, newMerged, newChanged, newThisBinding)
      }
      case _ => that
    }
  }
}
