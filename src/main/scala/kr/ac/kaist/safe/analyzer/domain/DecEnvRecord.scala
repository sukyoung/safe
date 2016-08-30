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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util.Loc
import scala.collection.immutable.HashMap

// 10.2.1.1 Declarative Environment Records
abstract class DecEnvRecord extends EnvRecord {
  // 10.2.1.1.1 HasBinding(N)
  def HasBinding(name: String)(boolU: AbsBoolUtil): AbsBool = this match {
    case DecEnvRecordBot => boolU.Bot
    case DecEnvRecordMap(map) => map.get(name) match {
      case None => boolU.False
      case Some((_, abs)) => abs match {
        case AbsentBot => boolU.True
        case AbsentTop => boolU.Top
      }
    }
  }

  // TODO 10.2.1.1.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(name: String, del: Boolean): Unit = {}

  // TODO 10.2.1.1.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: Value,
    strict: Boolean
  ): Set[Exception] = null

  // TODO 10.2.1.1.4 GetBindingValue(N, S)
  def GetBindingValue(name: String, strict: Boolean): Set[Exception] = null

  // TODO 10.2.1.1.5 DeleteBinding(N)
  def DeleteBinding(name: String): AbsBool = null

  // TODO 10.2.1.1.6 ImplicitThisValue()
  def ImplicitThisValue: Value = null

  // TODO 10.2.1.1.7 CreateImmutableBinding(N)
  def CreateImmutableBinding(name: String): Unit = {}

  // TODO 10.2.1.1.6 InitializeImmutableBinding(N, V)
  def InitializeImmutableBinding(name: String, v: Value): Unit = {}

  // TODO temporal copy
  override def toString: String = this match {
    case DecEnvRecordBot => "⊥"
    case DecEnvRecordMap(m) if m.isEmpty => "Empty"
    case DecEnvRecordMap(map) => {
      val sortedMap = map.toSeq.sortBy {
        case (key, _) => key
      }

      val s = new StringBuilder
      sortedMap.map {
        case (key, (bind, absent)) => {
          s.append(key).append(absent match {
            case AbsentTop => s" @-> "
            case AbsentBot => s" |-> "
          }).append(bind.toString).append(LINE_SEP)
        }
      }

      s.toString
    }
  }

  /* partial order */
  def <=(that: DecEnvRecord): Boolean = (this, that) match {
    case (DecEnvRecordBot, _) => true
    case (_, DecEnvRecordBot) => false
    case (DecEnvRecordMap(thisMap), DecEnvRecordMap(thatMap)) => {
      if (thisMap.isEmpty) true
      else if (thatMap.isEmpty) false
      else thisMap.forall {
        case (key, (thisB, thisAbs)) => thatMap.get(key) match {
          case None => false
          case Some((thatB, thatAbs)) =>
            thisB <= thatB && thisAbs <= thatAbs
        }
      }
    }
  }

  /* not a partial order */
  def </(that: DecEnvRecord): Boolean = !(this <= that)

  /* join */
  def +(that: DecEnvRecord): DecEnvRecord = (this, that) match {
    case (DecEnvRecordBot, _) => that
    case (_, DecEnvRecordBot) => this
    case (DecEnvRecordMap(thisMap), DecEnvRecordMap(thatMap)) => {
      if (this eq that) this
      else {
        val newMap = thatMap.foldLeft(thisMap) {
          case (m, (key, (thatB, thatAbs))) => m.get(key) match {
            case None => m + (key -> (thatB, thatAbs))
            case Some((thisB, thisAbs)) =>
              m + (key -> (thisB + thatB, thisAbs + thatAbs))
          }
        }
        DecEnvRecord(newMap)
      }
    }
  }

  /* meet */
  def <>(that: DecEnvRecord): DecEnvRecord = (this, that) match {
    case (DecEnvRecordBot, _) | (_, DecEnvRecordBot) => DecEnvRecordBot
    case (DecEnvRecordMap(thisMap), DecEnvRecordMap(thatMap)) => {
      if (thisMap eq thatMap) this
      else {
        val keys = thisMap.keySet intersect thatMap.keySet
        val map = keys.foldLeft(DecEnvRecord.MapBot) {
          case (m, key) => {
            val (thisB, thisAbs) = thisMap(key)
            val (thatB, thatAbs) = thatMap(key)
            m + (key -> (thisB <> thatB, thisAbs <> thatAbs))
          }
        }
        DecEnvRecord(map)
      }
    }
  }

  def isBottom: Boolean = this == DecEnvRecordBot

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) => {
      if (map.isEmpty) this
      else {
        val newMap = map.foldLeft(DecEnvRecord.MapBot) {
          case (m, (key, (bind, abs))) => {
            val newV = bind.value.subsLoc(locR, locO)
            val newBind = Binding(newV, bind.mutable)
            m + (key -> (newBind, abs))
          }
        }
        DecEnvRecord(newMap)
      }
    }
  }

  def weakSubsLoc(locR: Loc, locO: Loc): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) => {
      if (map.isEmpty) this
      else {
        val newMap = map.foldLeft(DecEnvRecord.MapBot) {
          case (m, (key, (bind, abs))) => {
            val newV = bind.value.weakSubsLoc(locR, locO)
            val newBind = Binding(newV, bind.mutable)
            m + (key -> (newBind, abs))
          }
        }
        DecEnvRecord(newMap)
      }
    }
  }

  def apply(s: String): Option[Binding] = this match {
    case DecEnvRecordBot => None
    case DecEnvRecordMap(map) => map.get(s).map { case (bind, _) => bind }
  }

  def getOrElse[T](s: String)(default: T)(f: Binding => T): T = {
    this(s) match {
      case Some(bind) => f(bind)
      case None => default
    }
  }

  def get(s: String)(utils: Utils): Binding = {
    this(s) match {
      case Some(bind) => bind
      case None => utils.binding.Bot
    }
  }

  def -(s: String): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) => DecEnvRecord(map - s)
  }

  // strong update
  def update(x: String, bind: Binding): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) =>
      DecEnvRecord(map.updated(x, (bind, AbsentBot)))
  }
}

object DecEnvRecord {
  private val MapBot: Map[String, (Binding, Absent)] = HashMap()
  val Bot: DecEnvRecord = DecEnvRecordBot
  val Empty: DecEnvRecord = DecEnvRecordMap(MapBot)
  def apply(m: Map[String, (Binding, Absent)]): DecEnvRecord = DecEnvRecordMap(m)
  def newDeclEnvRecord(outerEnv: Value)(utils: Utils): DecEnvRecord = {
    Empty.update("@outer", utils.binding(outerEnv))
  }

  def newPureLocal(envVal: Value, thisLocSet: Set[Loc])(utils: Utils): DecEnvRecord = {
    Empty
      .update("@env", utils.binding(envVal))
      .update("@this", utils.binding(utils.value(thisLocSet)))
      .update("@exception", utils.binding.Bot)
      .update("@exception_all", utils.binding.Bot)
      .update("@return", utils.binding(utils.value.alpha()))
  }
}

case class DecEnvRecordMap(
  val map: Map[String, (Binding, Absent)]
) extends DecEnvRecord
object DecEnvRecordBot extends DecEnvRecord
