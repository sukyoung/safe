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
import scala.collection.immutable.{ HashMap, HashSet }

// default execution context abstract domain
object DefaultContext extends ContextDomain {
  private val EmptyMap: Map[Loc, AbsLexEnv] = HashMap()

  case object Bot extends Elem
  case object Top extends Elem
  case class CtxMap(
    // TODO val varEnv: LexEnv // VariableEnvironment
    val map: Map[Loc, AbsLexEnv],
    override val thisBinding: AbsValue // ThisBinding
  ) extends Elem
  lazy val Empty: Elem =
    CtxMap(EmptyMap, LocSet(GLOBAL_LOC))

  def alpha(ctx: Context): Elem = Top // TODO more precise

  def apply(
    map: Map[Loc, AbsLexEnv],
    thisBinding: AbsValue
  ): Elem = CtxMap(map, thisBinding)

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Context] = ConInf // TODO more precise

    def getSingle: ConSingle[Context] = ConMany() // TODO more precise

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (_, Top) => true
      case (Top, _) => false
      case (CtxMap(thisMap, thisThis),
        CtxMap(thatMap, thatThis)) => {
        val mapB =
          if (thisMap.isEmpty) true
          else if (thatMap.isEmpty) false
          else thisMap.forall {
            case (loc, thisEnv) => thatMap.get(loc) match {
              case None => false
              case Some(thatEnv) => thisEnv ⊑ thatEnv
            }
          }
        val thisB = thisThis ⊑ thatThis
        mapB && thisB
      }
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (Top, _) | (_, Top) => Top
      case (CtxMap(thisMap, thisThis),
        CtxMap(thatMap, thatThis)) => {
        if (this eq that) this
        else {
          val newMap = thatMap.foldLeft(thisMap) {
            case (m, (loc, thatEnv)) => m.get(loc) match {
              case None => m + (loc -> thatEnv)
              case Some(thisEnv) =>
                m + (loc -> (thisEnv ⊔ thatEnv))
            }
          }
          val newThis = thisThis ⊔ thatThis
          CtxMap(newMap, newThis)
        }
      }
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Top, _) => that
      case (_, Top) => this
      case (CtxMap(thisMap, thisThis),
        CtxMap(thatMap, thatThis)) => {
        if (thisMap eq thatMap) this
        else {
          val locSet = thisMap.keySet intersect thatMap.keySet
          val newMap = locSet.foldLeft(EmptyMap) {
            case (m, loc) => {
              val thisEnv = thisMap(loc)
              val thatEnv = thatMap(loc)
              m + (loc -> (thisEnv ⊓ thatEnv))
            }
          }
          val newThis = thisThis ⊓ thatThis
          CtxMap(newMap, newThis)
        }
      }
    }

    def apply(loc: Loc): Option[AbsLexEnv] = this match {
      case Bot => None
      case Top => Some(AbsLexEnv.Top)
      case CtxMap(map, _) => map.get(loc)
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
      case CtxMap(map, thisBinding) =>
        CtxMap(weakUpdated(map, loc, env), thisBinding)
    }

    def update(loc: Loc, env: AbsLexEnv): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(map, _) => {
        if (isConcrete(loc)) {
          cmap.copy(map = map.updated(loc, env))
        } else {
          cmap.copy(map = weakUpdated(map, loc, env))
        }
      }
    }

    def remove(loc: Loc): Elem = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, thisBinding) => CtxMap(map - loc, thisBinding)
    }

    def subsLoc(from: Loc, to: Loc): Elem = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, thisBinding) => {
        val newMap = (map.get(from) match {
          case Some(env) => map - from + (to -> (map.getOrElse(to, AbsLexEnv.Bot) ⊔ env))
          case None => map
        }).foldLeft(EmptyMap) {
          case (m, (loc, env)) =>
            m + (loc -> env.subsLoc(from, to))
        }
        val newThis = thisBinding.subsLoc(from, to)
        CtxMap(newMap, newThis)
      }
    }

    def domIn(loc: Loc): Boolean = this match {
      case Bot => false
      case Top => true
      case CtxMap(map, _) => map.contains(loc)
    }

    def setThisBinding(thisBinding: AbsValue): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(_, _) => cmap.copy(thisBinding = thisBinding)
    }

    def getMap: Map[Loc, AbsLexEnv] = this match {
      case Bot => HashMap()
      case Top => HashMap() // TODO it is not sound
      case CtxMap(map, _) => map
    }

    def thisBinding: AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top => AbsValue.Top
      case CtxMap(_, thisBinding) => thisBinding
    }

    override def toString: String = {
      buildString(_ => true).toString
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Bot => "⊥Elem"
      case Top => "Top"
      case CtxMap(map, thisBinding) => {
        val s = new StringBuilder
        val sortedSeq =
          map.toSeq.filter { case (loc, _) => filter(loc) }
            .sortBy { case (loc, _) => loc }
        sortedSeq.map {
          case (loc, env) => s.append(toStringLoc(loc, env, isConcrete(loc))).append(LINE_SEP)
        }
        s.append(s"this: $thisBinding")
        s.toString
      }
    }

    def toStringLoc(loc: Loc): Option[String] = this match {
      case Bot => None
      case Top => Some(toStringLoc(loc, AbsLexEnv.Top, true))
      case CtxMap(map, _) => map.get(loc).map(toStringLoc(loc, _, isConcrete(loc)))
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
    def isConcrete(loc: Loc): Boolean = loc match {
      case Recency(_, Recent) => true
      case _ => false
    }
  }
}
