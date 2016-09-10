/**
 * *****************************************************************************
 * Copyright With(c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.errors.error.ContextAssertionError
import kr.ac.kaist.safe.LINE_SEP
import scala.collection.immutable.{ HashMap, HashSet }

/* 10.2.1.1 Declarative Environment Records */

////////////////////////////////////////////////////////////////////////////////
// concrete declarative environment record type
////////////////////////////////////////////////////////////////////////////////
case class DecEnvRec(map: Map[String, Binding])

////////////////////////////////////////////////////////////////////////////////
// declarative environment record abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsDecEnvRec extends AbsDomain[DecEnvRec, AbsDecEnvRec] {
  // 10.2.1.1.1 HasBinding(N)
  def HasBinding(name: String): AbsBool

  // 10.2.1.1.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(
    name: String,
    del: Boolean
  ): AbsDecEnvRec

  // 10.2.1.1.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: AbsValue,
    strict: Boolean
  ): (AbsDecEnvRec, Set[Exception])

  // 10.2.1.1.4 GetBindingValue(N, S)
  def GetBindingValue(
    name: String,
    strict: Boolean
  ): (AbsValue, Set[Exception])

  // 10.2.1.1.5 DeleteBinding(N)
  def DeleteBinding(
    name: String
  ): (AbsDecEnvRec, AbsBool)

  // 10.2.1.1.6 ImplicitThisValue()
  def ImplicitThisValue: AbsValue

  // 10.2.1.1.7 CreateImmutableBinding(N)
  def CreateImmutableBinding(
    name: String
  ): AbsDecEnvRec

  // 10.2.1.1.6 InitializeImmutableBinding(N, V)
  def InitializeImmutableBinding(
    name: String,
    v: AbsValue
  ): AbsDecEnvRec

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): AbsDecEnvRec

  // weak substitute locR by locO
  def weakSubsLoc(locR: Loc, locO: Loc): AbsDecEnvRec

  // getter
  def apply(s: String): Option[AbsBinding]
  def getOrElse[T](s: String)(default: T)(f: AbsBinding => T): T
  def get(s: String): AbsBinding

  // delete
  def -(s: String): AbsDecEnvRec

  // strong update
  def update(x: String, bind: AbsBinding): AbsDecEnvRec
}

trait AbsDecEnvRecUtil extends AbsDomainUtil[DecEnvRec, AbsDecEnvRec] {
  type EnvMap = Map[String, (AbsBinding, AbsAbsent)]

  val Empty: AbsDecEnvRec

  def apply(m: EnvMap): AbsDecEnvRec
  def newDeclEnvRecord(outerEnv: AbsValue): AbsDecEnvRec
  def newPureLocal(envVal: AbsValue, thisLocSet: AbsLoc): AbsDecEnvRec
}

////////////////////////////////////////////////////////////////////////////////
// default primitive value abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultDecEnvRec extends AbsDecEnvRecUtil {
  case object Bot extends AbsDom
  case object Top extends AbsDom
  case class BindMap(map: EnvMap) extends AbsDom {
    // existence check
    def has(name: String): Existence = map.get(name) match {
      case None => MustNotExist
      case Some((bind, abs)) => abs.isBottom match {
        case true => MustExist(bind)
        case false =>
          if (bind.isBottom) MustNotExist
          else MayExist(bind)
      }
    }
  }

  abstract class Existence
  case object MustNotExist extends Existence
  case class MustExist(bind: AbsBinding) extends Existence
  case class MayExist(bind: AbsBinding) extends Existence

  private val EmptyMap: EnvMap = HashMap()
  val Empty: AbsDecEnvRec = BindMap(EmptyMap)

  def alpha(dec: DecEnvRec): AbsDecEnvRec = BindMap(dec.map.foldLeft(EmptyMap) {
    case (map, (str, bind)) => map + (str -> (AbsBinding.alpha(bind), AbsAbsent.Bot))
  })

  def apply(m: EnvMap): AbsDecEnvRec = BindMap(m)

  abstract class AbsDom extends AbsDecEnvRec {
    def gamma: ConSet[DecEnvRec] = ConInf() // TODO more precise

    def isBottom: Boolean = this == Bot

    def getSingle: ConSingle[DecEnvRec] = ConMany() // TODO more precise

    // 10.2.1.1.1 HasBinding(N)
    def HasBinding(name: String): AbsBool = this match {
      case Bot => AbsBool.Bot
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case (envRec: BindMap) => envRec.has(name) match {
        // 2. If envRec has a binding for the name that is the value of N,
        //    return true.
        case MustExist(_) => AbsBool.True
        // 3. If it does not have such a binding, return false.
        case MustNotExist => AbsBool.False
        case MayExist(_) => AbsBool.Top
      }
    }

    // 10.2.1.1.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean
    ): AbsDecEnvRec = this match {
      case Bot => Bot
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case envRec @ BindMap(map) => {
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
        val newBind = AbsBinding.alpha(MBinding(Undef))
        BindMap(map.updated(name, (newBind, AbsAbsent.Bot)))
      }
    }

    // 10.2.1.1.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean
    ): (AbsDecEnvRec, Set[Exception]) = this match {
      case Bot => (Bot, HashSet())
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case envRec @ BindMap(map) => {
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
          if (AbsBool.True <= bind.mutable) v
          else AbsValue.Bot
        // 4. Else this must be an attempt to change the value of
        //    an immutable binding so if S if true throw a TypeError
        //    exception.
        val emptyExcSet: Set[Exception] = HashSet()
        val (elseV, excSet: Set[Exception]) =
          if (AbsBool.False <= bind.mutable) {
            if (strict) (AbsValue.Bot, HashSet(TypeError))
            else (v, emptyExcSet)
          } else { (AbsValue.Bot, emptyExcSet) }
        val newBind = AbsBinding(thenV + elseV)
        (BindMap(map.updated(name, (newBind, AbsAbsent.Bot))), excSet)
      }
    }

    // 10.2.1.1.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean
    ): (AbsValue, Set[Exception]) = this match {
      case Bot => (AbsValue.Bot, HashSet())
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case envRec @ BindMap(map) => {
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
          if (AbsBool.False <= bind.mutable &&
            AbsAbsent.Top <= bind.uninit) {
            //    a. If S is false, return the value undefined,
            //       otherwise throw a ReferenceError exception.
            if (strict) (AbsValue.Bot, HashSet(ReferenceError))
            else (AbsValue(Undef), emptyExcSet)
          } else { (AbsValue.Bot, emptyExcSet) }
        // 4. Else, return the value currently bound to N in envRec.
        val elseV =
          if (AbsBool.True <= bind.mutable) bind.value
          else AbsValue.Bot
        (thenV + elseV, excSet)
      }
    }

    // 10.2.1.1.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    ): (AbsDecEnvRec, AbsBool) = this match {
      case Bot => (Bot, AbsBool.Bot)
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case envRec @ BindMap(map) => {
        // 2. If envRec does not have a binding for
        //    the name that is the value of N, return true.
        // 3. If the binding for N in envRec is cannot be deleted, return false.
        //    (we do not consider explicit design)
        // 4. Remove the binding for N from envRec.
        val newMap = map - name
        // 5. Return true.
        (BindMap(newMap), AbsBool.True)
      }
    }

    // 10.2.1.1.6 ImplicitThisValue()
    def ImplicitThisValue: AbsValue = AbsValue(Undef)

    // 10.2.1.1.7 CreateImmutableBinding(N)
    def CreateImmutableBinding(
      name: String
    ): AbsDecEnvRec = this match {
      case Bot => Bot
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case envRec @ BindMap(map) => {
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
        val newBind = AbsBinding.alpha(IBinding(None))
        BindMap(map.updated(name, (newBind, AbsAbsent.Bot)))
      }
    }

    // 10.2.1.1.6 InitializeImmutableBinding(N, V)
    def InitializeImmutableBinding(
      name: String,
      v: AbsValue
    ): AbsDecEnvRec = this match {
      case Bot => Bot
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case envRec @ BindMap(map) => {
        // 2. Assert: envRec must have an uninitialised immutable binding for N.
        val bind = envRec.has(name) match {
          case MustExist(bind) if (
            bind.mutable == AbsBool.False &&
            bind.uninit == AbsAbsent.Top
          ) => bind
          case _ => throw ContextAssertionError(
            "InitializeImmutableBinding",
            "envRec must have an uninitialised immutable binding for N."
          )
        }
        // 3. Set the bound value for N in envRec to V.
        val newBind = bind.copyWith(value = v, uninit = AbsAbsent.Bot)
        // 4. Record that the immutable binding for N
        //    in envRec has been initialised.
        BindMap(map.updated(name, (newBind, AbsAbsent.Bot)))
      }
    }

    def <=(that: AbsDecEnvRec): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (BindMap(thisMap), BindMap(thatMap)) => {
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

    def +(that: AbsDecEnvRec): AbsDecEnvRec = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (BindMap(thisMap), BindMap(thatMap)) => {
        if (thisMap eq thatMap) this
        else {
          val newMap = thatMap.foldLeft(thisMap) {
            case (m, (key, (thatB, thatAbs))) => m.get(key) match {
              case None => m + (key -> (thatB, thatAbs))
              case Some((thisB, thisAbs)) =>
                m + (key -> (thisB + thatB, thisAbs + thatAbs))
            }
          }
          BindMap(newMap)
        }
      }
    }

    // meet
    def <>(that: AbsDecEnvRec): AbsDecEnvRec = (this, that) match {
      case (Bot, _) | (_, Bot) => Bot
      case (BindMap(thisMap), BindMap(thatMap)) => {
        if (thisMap eq thatMap) this
        else {
          val keys = thisMap.keySet intersect thatMap.keySet
          val map = keys.foldLeft(EmptyMap) {
            case (m, key) => {
              val (thisB, thisAbs) = thisMap(key)
              val (thatB, thatAbs) = thatMap(key)
              m + (key -> (thisB <> thatB, thisAbs <> thatAbs))
            }
          }
          BindMap(map)
        }
      }
    }

    override def toString: String = this match {
      case Bot => "⊥"
      case BindMap(m) if m.isEmpty => "Empty"
      case BindMap(map) => {
        val sortedMap = map.toSeq.sortBy {
          case (key, _) => key
        }

        val s = new StringBuilder
        sortedMap.map {
          case (key, (bind, absent)) => {
            s.append(key).append(absent.isBottom match {
              case true => s" |-> "
              case false => s" @-> "
            }).append(bind.toString).append(LINE_SEP)
          }
        }

        s.toString
      }
    }

    // substitute locR by locO
    def subsLoc(locR: Loc, locO: Loc): AbsDecEnvRec = this match {
      case Bot => Bot
      case BindMap(map) => {
        if (map.isEmpty) this
        else {
          val newMap = map.foldLeft(EmptyMap) {
            case (m, (key, (bind, abs))) => {
              val newV = bind.value.subsLoc(locR, locO)
              val newBind = bind.copyWith(value = newV)
              m + (key -> (newBind, abs))
            }
          }
          BindMap(newMap)
        }
      }
    }

    // weak substitute locR by locO
    def weakSubsLoc(locR: Loc, locO: Loc): AbsDecEnvRec = this match {
      case Bot => Bot
      case BindMap(map) => {
        if (map.isEmpty) this
        else {
          val newMap = map.foldLeft(EmptyMap) {
            case (m, (key, (bind, abs))) => {
              val newV = bind.value.weakSubsLoc(locR, locO)
              val newBind = bind.copyWith(value = newV)
              m + (key -> (newBind, abs))
            }
          }
          BindMap(newMap)
        }
      }
    }

    def apply(s: String): Option[AbsBinding] = this match {
      case Bot => None
      case BindMap(map) => map.get(s).map { case (bind, _) => bind }
    }

    def getOrElse[T](s: String)(default: T)(f: AbsBinding => T): T = {
      this(s) match {
        case Some(bind) => f(bind)
        case None => default
      }
    }

    def get(s: String): AbsBinding = {
      this(s) match {
        case Some(bind) => bind
        case None => AbsBinding.Bot
      }
    }

    def -(s: String): AbsDecEnvRec = this match {
      case Bot => Bot
      case BindMap(map) => BindMap(map - s)
    }

    // strong update
    def update(x: String, bind: AbsBinding): AbsDecEnvRec = this match {
      case Bot => Bot
      case BindMap(map) =>
        BindMap(map.updated(x, (bind, AbsAbsent.Bot)))
    }
  }

  def newDeclEnvRecord(outerEnv: AbsValue): AbsDecEnvRec = {
    Empty.update("@outer", AbsBinding(outerEnv))
  }

  def newPureLocal(envVal: AbsValue, thisLocSet: AbsLoc): AbsDecEnvRec = {
    Empty
      .update("@env", AbsBinding(envVal))
      .update("@this", AbsBinding(thisLocSet))
      .update("@exception", AbsBinding.Bot)
      .update("@exception_all", AbsBinding.Bot)
      .update("@return", AbsBinding(MBinding(Undef)))
  }
}
