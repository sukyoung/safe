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

import kr.ac.kaist.safe.errors.error.ContextAssertionError
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util.Loc
import scala.collection.immutable.{ HashMap, HashSet }

// 10.2.1.1 Declarative Environment Records
abstract class DecEnvRecord extends EnvRecord {
  // 10.2.1.1.1 HasBinding(N)
  def HasBinding(name: String)(absBool: AbsBoolUtil): AbsBool = this match {
    case DecEnvRecordBot => absBool.Bot
    // 1. Let envRec be the declarative environment record for
    //    which the method was invoked.
    case (envRec: DecEnvRecordMap) => envRec.has(name) match {
      // 2. If envRec has a binding for the name that is the value of N,
      //    return true.
      case MustExist(_) => absBool.True
      // 3. If it does not have such a binding, return false.
      case MustNotExist => absBool.False
      case MayExist(_) => absBool.Top
    }
  }

  // 10.2.1.1.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(
    name: String,
    del: Boolean
  )(utils: Utils): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    // 1. Let envRec be the declarative environment record for
    //    which the method was invoked.
    case envRec @ DecEnvRecordMap(map) => {
      envRec.has(name) match {
        case MustNotExist =>
        // 2. Assert: envRec does not already have a binding for N.
        case _ => throw ContextAssertionError(
          "CreateMutableBinding",
          "envRec does not already have a binding for N."
        )
      }
      // 3. Create a mutable binding in envRec for N and
      //    set its bound value to undefined.
      val newV = utils.value.alpha()
      val newBind = utils.binding(newV)
      DecEnvRecordMap(map.updated(name, (newBind, AbsentBot)))
    }
  }

  // 10.2.1.1.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: Value,
    strict: Boolean
  )(utils: Utils): (DecEnvRecord, Set[Exception]) = this match {
    case DecEnvRecordBot => (DecEnvRecordBot, HashSet())
    // 1. Let envRec be the declarative environment record for
    //    which the method was invoked.
    case envRec @ DecEnvRecordMap(map) => {
      // 2. Assert: envRec must have a binding for N.
      val bind = envRec.has(name) match {
        case MustExist(bind) => bind
        case _ => throw ContextAssertionError(
          "SetMutableBinding",
          "envRec must have a binding for N."
        )
      }
      // 3. If the binding for N in envRec is a mutable binding,
      //    change its bound value to V.
      val thenV =
        if (utils.absBool.True <= bind.mutable) v
        else utils.value.Bot
      // 4. Else this must be an attempt to change the value of
      //    an immutable binding so if S if true throw a TypeError
      //    exception.
      val emptyExcSet: Set[Exception] = HashSet()
      val (elseV, excSet: Set[Exception]) =
        if (utils.absBool.False <= bind.mutable) {
          if (strict) (utils.value.Bot, HashSet(TypeError))
          else (v, emptyExcSet)
        } else { (utils.value.Bot, emptyExcSet) }
      val newBind = utils.binding(thenV + elseV)
      (DecEnvRecordMap(map.updated(name, (newBind, AbsentBot))), excSet)
    }
  }

  // 10.2.1.1.4 GetBindingValue(N, S)
  def GetBindingValue(
    name: String,
    strict: Boolean
  )(utils: Utils): (Value, Set[Exception]) = this match {
    case DecEnvRecordBot => (utils.value.Bot, HashSet())
    // 1. Let envRec be the declarative environment record for
    //    which the method was invoked.
    case envRec @ DecEnvRecordMap(map) => {
      val bind = envRec.has(name) match {
        case MustExist(bind) => bind
        // 2. Assert: envRec has a binding for N.
        case _ => throw ContextAssertionError(
          "GetBindingValue",
          "envRec has a binding for N."
        )
      }
      // 3. If the binding for N in envRec is
      //    an uninitialised immutable binding, then
      val emptyExcSet: Set[Exception] = HashSet()
      val (thenV, excSet: Set[Exception]) =
        if (utils.absBool.False <= bind.mutable &&
          utils.absBool.False <= bind.initialized) {
          //    a. If S is false, return the value undefined,
          //       otherwise throw a ReferenceError exception.
          if (strict) (utils.value.Bot, HashSet(ReferenceError))
          else (utils.value.alpha(), emptyExcSet)
        } else { (utils.value.Bot, emptyExcSet) }
      // 4. Else, return the value currently bound to N in envRec.
      val elseV =
        if (utils.absBool.True <= bind.mutable ||
          utils.absBool.True <= bind.initialized) bind.value
        else utils.value.Bot
      (thenV + elseV, excSet)
    }
  }

  // 10.2.1.1.5 DeleteBinding(N)
  def DeleteBinding(
    name: String
  )(utils: Utils): (DecEnvRecord, AbsBool) = this match {
    case DecEnvRecordBot => (DecEnvRecordBot, utils.absBool.Bot)
    // 1. Let envRec be the declarative environment record for
    //    which the method was invoked.
    case envRec @ DecEnvRecordMap(map) => {
      // 2. If envRec does not have a binding for
      //    the name that is the value of N, return true.
      // 3. If the binding for N in envRec is cannot be deleted, return false.
      //    (we do not consider explicit design)
      // 4. Remove the binding for N from envRec.
      val newMap = map - name
      // 5. Return true.
      (DecEnvRecordMap(newMap), utils.absBool.True)
    }
  }

  // 10.2.1.1.6 ImplicitThisValue()
  def ImplicitThisValue(utils: Utils): Value = utils.value.alpha()

  // 10.2.1.1.7 CreateImmutableBinding(N)
  def CreateImmutableBinding(
    name: String
  )(utils: Utils): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    // 1. Let envRec be the declarative environment record for
    //    which the method was invoked.
    case envRec @ DecEnvRecordMap(map) => {
      envRec.has(name) match {
        case MustNotExist =>
        // 2. Assert: envRec does not already have a binding for N.
        case _ => throw ContextAssertionError(
          "CreateImmutableBinding",
          "envRec does not already have a binding for N."
        )
      }
      // 3. Create an immutable binding in envRec for N and
      //    record that it is uninitialised.
      val newBind = utils.binding(
        value = utils.value.Bot,
        initialized = utils.absBool.False,
        mutable = utils.absBool.False
      )
      DecEnvRecordMap(map.updated(name, (newBind, AbsentBot)))
    }
  }

  // 10.2.1.1.6 InitializeImmutableBinding(N, V)
  def InitializeImmutableBinding(
    name: String,
    v: Value
  )(utils: Utils): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    // 1. Let envRec be the declarative environment record for
    //    which the method was invoked.
    case envRec @ DecEnvRecordMap(map) => {
      // 2. Assert: envRec must have an uninitialised immutable binding for N.
      val bind = envRec.has(name) match {
        case MustExist(bind) if (
          bind.mutable == utils.absBool.False &&
          bind.initialized == utils.absBool.False
        ) => bind
        case _ => throw ContextAssertionError(
          "InitializeImmutableBinding",
          "envRec must have an uninitialised immutable binding for N."
        )
      }
      // 3. Set the bound value for N in envRec to V.
      val newBind = bind.copy(value = v, initialized = utils.absBool.True)
      // 4. Record that the immutable binding for N
      //    in envRec has been initialised.
      DecEnvRecordMap(map.updated(name, (newBind, AbsentBot)))
    }
  }

  // toString
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

  // partial order
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

  // not a partial order
  def </(that: DecEnvRecord): Boolean = !(this <= that)

  // join
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

  // meet
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

  // bottom check
  def isBottom: Boolean = this == DecEnvRecordBot

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) => {
      if (map.isEmpty) this
      else {
        val newMap = map.foldLeft(DecEnvRecord.MapBot) {
          case (m, (key, (bind, abs))) => {
            val newV = bind.value.subsLoc(locR, locO)
            val newBind = bind.copy(value = newV)
            m + (key -> (newBind, abs))
          }
        }
        DecEnvRecord(newMap)
      }
    }
  }

  // weak substitute locR by locO
  def weakSubsLoc(locR: Loc, locO: Loc): DecEnvRecord = this match {
    case DecEnvRecordBot => DecEnvRecordBot
    case DecEnvRecordMap(map) => {
      if (map.isEmpty) this
      else {
        val newMap = map.foldLeft(DecEnvRecord.MapBot) {
          case (m, (key, (bind, abs))) => {
            val newV = bind.value.weakSubsLoc(locR, locO)
            val newBind = bind.copy(value = newV)
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

object DecEnvRecordBot extends DecEnvRecord
case class DecEnvRecordMap(
    val map: Map[String, (Binding, Absent)]
) extends DecEnvRecord {
  // existence check
  def has(name: String): Existence = map.get(name) match {
    case None => MustNotExist
    case Some((bind, abs)) => abs match {
      case AbsentBot => MustExist(bind)
      case AbsentTop =>
        if (bind.isBottom) MustNotExist
        else MayExist(bind)
    }
  }
}

abstract class Existence
case object MustNotExist extends Existence
case class MustExist(bind: Binding) extends Existence
case class MayExist(bind: Binding) extends Existence
