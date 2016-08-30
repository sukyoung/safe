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
      case Some((pv, abs)) => abs match {
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
        case (key, (propv, absent)) => {
          s.append(key).append(absent match {
            case AbsentTop => s" @-> "
            case AbsentBot => s" |-> "
          }).append(propv.toString).append(LINE_SEP)
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
        case (key, (thisPV, thisAbs)) => thatMap.get(key) match {
          case None => false
          case Some((thatPV, thatAbs)) =>
            thisPV <= thatPV && thisAbs <= thatAbs
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
          case (m, (key, (thatPV, thatAbs))) => m.get(key) match {
            case None => m + (key -> (thatPV, thatAbs))
            case Some((thisPV, thisAbs)) =>
              m + (key -> (thisPV + thatPV, thisAbs + thatAbs))
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
            val (thisPV, thisAbs) = thisMap(key)
            val (thatPV, thatAbs) = thatMap(key)
            m + (key -> (thisPV <> thatPV, thisAbs <> thatAbs))
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
          case (m, (key, (pv, abs))) => {
            val ov = pv.objval
            val newV = ov.value.subsLoc(locR, locO)
            val newOV = DataProperty(newV, ov.writable, ov.enumerable, ov.configurable)
            val newPropV = PropValue(newOV, pv.funid)
            m + (key -> (newPropV, abs))
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
          case (m, (key, (pv, abs))) => {
            val ov = pv.objval
            val newV = ov.value.weakSubsLoc(locR, locO)
            val newOV = DataProperty(newV, ov.writable, ov.enumerable, ov.configurable)
            val newPropV = PropValue(newOV, pv.funid)
            m + (key -> (newPropV, abs))
          }
        }
        DecEnvRecord(newMap)
      }
    }
  }

  def apply(s: String): Option[PropValue] = this match {
    case DecEnvRecordBot => None
    case DecEnvRecordMap(map) => map.get(s).map { case (pv, _) => pv }
  }

  def getOrElse[T](s: String)(default: T)(f: PropValue => T): T = {
    this(s) match {
      case Some(propV) => f(propV)
      case None => default
    }
  }

  def get(s: String)(utils: Utils): PropValue = {
    this(s) match {
      case Some(propV) => propV
      case None => PropValue.Bot(utils)
    }
  }

  def -(s: String): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) => DecEnvRecord(map - s)
  }

  // strong update
  def update(x: String, propv: PropValue): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) =>
      DecEnvRecord(map.updated(x, (propv, AbsentBot)))
  }
}

object DecEnvRecord {
  private val MapBot: Map[String, (PropValue, Absent)] = HashMap()
  val Bot: DecEnvRecord = DecEnvRecordBot
  val Empty: DecEnvRecord = DecEnvRecordMap(MapBot)
  def apply(m: Map[String, (PropValue, Absent)]): DecEnvRecord = DecEnvRecordMap(m)
  def newDeclEnvRecord(outerEnv: Value)(utils: Utils): DecEnvRecord = {
    Empty.update("@outer", PropValue(utils.dataProp(outerEnv)))
  }

  def newPureLocal(envVal: Value, thisLocSet: Set[Loc])(utils: Utils): DecEnvRecord = {
    Empty
      .update("@env", PropValue(utils.dataProp(envVal)))
      .update("@this", PropValue(utils.dataProp(thisLocSet)))
      .update("@exception", PropValue.Bot(utils))
      .update("@exception_all", PropValue.Bot(utils))
      .update("@return", PropValue(utils.absUndef.Top)(utils))
  }
}

case class DecEnvRecordMap(
  val map: Map[String, (PropValue, Absent)] // TODO Just String -> Value
) extends DecEnvRecord
object DecEnvRecordBot extends DecEnvRecord
