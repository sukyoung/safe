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

import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.errors.error.AbsContextParseError
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.cfg._
import scala.collection.immutable.{ HashMap, HashSet }
import spray.json._

// default execution context abstract domain
object DefaultContext extends ContextDomain {
  private val EmptyMap: Map[Loc, AbsLexEnv] = HashMap()

  case object Bot extends Elem
  case object Top extends Elem
  case class CtxMap(
    // TODO val varEnv: LexEnv // VariableEnvironment
    val map: Map[Loc, AbsLexEnv],
    override val old: OldASiteSet,
    override val thisBinding: AbsValue // ThisBinding
  ) extends Elem
  lazy val Empty: Elem =
    CtxMap(EmptyMap, OldASiteSet.Empty, AbsLoc(BuiltinGlobal.loc))

  def alpha(ctx: Context): Elem = Top // TODO more precise

  def apply(
    map: Map[Loc, AbsLexEnv],
    old: OldASiteSet,
    thisBinding: AbsValue
  ): Elem = CtxMap(map, old, thisBinding)

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case JsObject(m) => (
      m.get("map").map(json2map[Loc, AbsLexEnv](_, Loc.fromJson, AbsLexEnv.fromJson)),
      m.get("old").map(OldASiteSet.fromJson(_)),
      m.get("thisBinding").map(AbsValue.fromJson(_))
    ) match {
        case (Some(m), Some(o), Some(t)) => CtxMap(m, o, t)
        case _ => throw AbsContextParseError(v)
      }
    case JsString("⊥") => Bot
    case _ => throw AbsContextParseError(v)
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Context] = ConInf // TODO more precise

    def getSingle: ConSingle[Context] = ConMany() // TODO more precise

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (_, Top) => true
      case (Top, _) => false
      case (CtxMap(thisMap, thisOld, thisThis),
        CtxMap(thatMap, thatOld, thatThis)) => {
        val mapB =
          if (thisMap.isEmpty) true
          else if (thatMap.isEmpty) false
          else thisMap.forall {
            case (loc, thisEnv) => thatMap.get(loc) match {
              case None => false
              case Some(thatEnv) => thisEnv ⊑ thatEnv
            }
          }
        val oldB = thisOld <= thatOld
        val thisB = thisThis ⊑ thatThis
        mapB && oldB && thisB
      }
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (Top, _) | (_, Top) => Top
      case (CtxMap(thisMap, thisOld, thisThis),
        CtxMap(thatMap, thatOld, thatThis)) => {
        if (this eq that) this
        else {
          val newMap = thatMap.foldLeft(thisMap) {
            case (m, (loc, thatEnv)) => m.get(loc) match {
              case None => m + (loc -> thatEnv)
              case Some(thisEnv) =>
                m + (loc -> (thisEnv ⊔ thatEnv))
            }
          }
          val newOld = thisOld + thatOld
          val newThis = thisThis ⊔ thatThis
          CtxMap(newMap, newOld, newThis)
        }
      }
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Top, _) => that
      case (_, Top) => this
      case (CtxMap(thisMap, thisOld, thisThis),
        CtxMap(thatMap, thatOld, thatThis)) => {
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
          val newOld = thisOld ⊓ thatOld
          val newThis = thisThis ⊓ thatThis
          CtxMap(newMap, newOld, newThis)
        }
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

    def apply(locSet: AbsLoc): AbsLexEnv = locSet.foldLeft(AbsLexEnv.Bot) {
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
      case CtxMap(map, old, thisBinding) =>
        CtxMap(weakUpdated(map, loc, env), old, thisBinding)
    }

    def update(loc: Loc, env: AbsLexEnv): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(map, _, _) => {
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
      case CtxMap(map, old, thisBinding) => CtxMap(map - loc, old, thisBinding)
    }

    def subsLoc(locR: Recency, locO: Recency): Elem = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, old, thisBinding) => {
        val newMap = map.foldLeft(EmptyMap) {
          case (m, (loc, env)) =>
            m + (loc -> env.subsLoc(locR, locO))
        }
        val newOld = old.subsLoc(locR, locO)
        val newThis = thisBinding.subsLoc(locR, locO)
        CtxMap(newMap, newOld, newThis)
      }
    }

    def oldify(loc: Loc): Elem = loc match {
      case locR @ Recency(subLoc, Recent) => this match {
        case Bot => Bot
        case Top => Top
        case CtxMap(map, _, _) => {
          val locO = Recency(subLoc, Old)
          val newCtx = if (this domIn locR) {
            update(locO, getOrElse(locR, AbsLexEnv.Bot)).remove(locR)
          } else this
          newCtx.subsLoc(locR, locO)
        }
      }
      case _ => this
    }

    def domIn(loc: Loc): Boolean = this match {
      case Bot => false
      case Top => true
      case CtxMap(map, _, _) => map.contains(loc)
    }

    def setOldASiteSet(old: OldASiteSet): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(_, _, _) => cmap.copy(old = old)
    }

    def setThisBinding(thisBinding: AbsValue): Elem = this match {
      case Bot => Bot
      case Top => Top
      case cmap @ CtxMap(_, _, _) => cmap.copy(thisBinding = thisBinding)
    }

    def getMap: Map[Loc, AbsLexEnv] = this match {
      case Bot => HashMap()
      case Top => HashMap() // TODO it is not sound
      case CtxMap(map, _, _) => map
    }

    def old: OldASiteSet = this match {
      case Bot => OldASiteSet.Bot
      case Top => OldASiteSet.Bot // TODO it is not sound
      case CtxMap(_, old, _) => old
    }

    def thisBinding: AbsValue = this match {
      case Bot => AbsValue.Bot
      case Top => AbsValue.Top
      case CtxMap(_, _, thisBinding) => thisBinding
    }

    override def toString: String = {
      buildString(_ => true).toString
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Bot => "⊥Elem"
      case Top => "Top"
      case CtxMap(map, old, thisBinding) => {
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
      getOrElse(PredAllocSite.PURE_LOCAL, AbsLexEnv.Bot)
    def subsPureLocal(env: AbsLexEnv): Elem =
      update(PredAllocSite.PURE_LOCAL, env)

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
      case CtxMap(map, old, bind) => JsObject(
        ("map", JsArray(map.toSeq.map {
          case (loc, env) => JsArray(loc.toJson, env.toJson)
        }: _*)),
        ("old", old.toJson),
        ("thisBinding", bind.toJson)
      )
      case Bot => JsString("⊥")
    }
  }
}
