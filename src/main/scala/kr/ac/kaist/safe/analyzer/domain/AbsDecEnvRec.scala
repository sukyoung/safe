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
case class DecEnvRec(map: Map[String, Binding]) extends EnvRec

////////////////////////////////////////////////////////////////////////////////
// declarative environment record abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsDecEnvRec extends AbsDomain[DecEnvRec, AbsDecEnvRec] {
  // 10.2.1.1.1 HasBinding(N)
  def HasBinding(name: String): AbsBool

  // 10.2.1.1.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(
    name: String,
    del: Boolean = true
  ): AbsDecEnvRec

  // 10.2.1.1.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: AbsValue,
    strict: Boolean = false
  ): (AbsDecEnvRec, Set[Exception])

  // 10.2.1.1.4 GetBindingValue(N, S)
  def GetBindingValue(
    name: String,
    strict: Boolean = false
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
}

trait AbsDecEnvRecUtil extends AbsDomainUtil[DecEnvRec, AbsDecEnvRec] {
  type EnvMap = Map[String, (AbsBinding, AbsAbsent)]

  val Empty: AbsDecEnvRec

  def apply(m: EnvMap, upper: Boolean = false): AbsDecEnvRec
  def newPureLocal(envVal: AbsValue, thisLocSet: AbsLoc): AbsDecEnvRec
}

////////////////////////////////////////////////////////////////////////////////
// default declarative environment record abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultDecEnvRec extends AbsDecEnvRecUtil {
  private val EmptyMap: EnvMap = HashMap()

  case object Bot extends Dom
  case class LBindMap(map: EnvMap) extends Dom
  case class UBindMap(map: EnvMap) extends Dom
  lazy val Empty: Dom = LBindMap(EmptyMap)
  lazy val Top: Dom = UBindMap(EmptyMap)

  def alpha(envRec: DecEnvRec): AbsDecEnvRec = LBindMap(envRec.map.foldLeft(EmptyMap) {
    case (map, (str, bind)) => map + (str -> (AbsBinding.alpha(bind), AbsAbsent.Bot))
  })

  def apply(m: EnvMap, upper: Boolean): AbsDecEnvRec = upper match {
    case false => LBindMap(m)
    case true => UBindMap(m)
  }

  abstract class Dom extends AbsDecEnvRec {
    def gamma: ConSet[DecEnvRec] = ConInf() // TODO more precise

    def getSingle: ConSingle[DecEnvRec] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsDecEnvRec): Boolean = {
      val right = check(that)
      (this, right) match {
        case (Bot, _) => true
        case (LBindMap(lmap), _) => lmap.forall {
          case (name, (lbind, labs)) => {
            val (rbind, rabs) = right.get(name)
            lbind <= rbind && labs <= rabs
          }
        }
        case (_, UBindMap(rmap)) => rmap.forall {
          case (name, (rbind, rabs)) => {
            val (lbind, labs) = this.get(name)
            lbind <= rbind && labs <= rabs
          }
        }
        case _ => false
      }
    }

    def +(that: AbsDecEnvRec): AbsDecEnvRec = {
      val right = check(that)
      (this, right) match {
        case _ if this eq right => this
        case (Bot, _) => right
        case (_, Bot) => this
        case (LBindMap(lmap), LBindMap(_)) => lmap.foldLeft(right) {
          case (envRec, (name, (lbind, labs))) => {
            val (rbind, rabs) = right.get(name)
            envRec.update(name, (lbind + rbind, labs + rabs))
          }
        }
        case (LBindMap(_), _) => right + this
        case (UBindMap(lmap), _) => lmap.foldLeft(this) {
          case (envRec, (name, (lbind, labs))) => {
            val (rbind, rabs) = right.get(name)
            envRec.update(name, (lbind + rbind, labs + rabs))
          }
        }
      }
    }

    def <>(that: AbsDecEnvRec): AbsDecEnvRec = {
      val right = check(that)
      (this, right) match {
        case _ if this eq right => this
        case (Bot, _) | (_, Bot) => Bot
        case (UBindMap(lmap), UBindMap(_)) => lmap.foldLeft(right) {
          case (envRec, (name, (lbind, labs))) => {
            val (rbind, rabs) = right.get(name)
            envRec.update(name, (lbind <> rbind, labs <> rabs))
          }
        }
        case (UBindMap(_), _) => right + this
        case (LBindMap(lmap), _) => {
          val nameSet = (right match {
            case Bot => HashSet() // XXX: not fisible
            case LBindMap(rmap) => rmap.keySet
            case UBindMap(rmap) => rmap.keySet
          }) ++ lmap.keySet
          nameSet.foldLeft(Empty) {
            case (envRec, name) => {
              val (lbind, labs) = this.get(name)
              val (rbind, rabs) = right.get(name)
              envRec.update(name, (lbind <> rbind, labs <> rabs))
            }
          }
        }
      }
    }

    override def toString: String = {
      val dataOpt = this match {
        case Bot => None
        case LBindMap(map) => Some((map, AbsValue.Bot, AbsAbsent.Top))
        case UBindMap(map) => Some((map, AbsValue.Top, AbsAbsent.Top))
      }
      if (isTop) "Top(declarative environment)"
      else dataOpt match {
        case None => "⊥(declarative environment)"
        case Some((map, obind, oabs)) => {
          val sortedMap = (map.toSeq.sortBy {
            case (key, _) => key
          }) :+ ("others", (obind, oabs))
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
    }

    // 10.2.1.1.1 HasBinding(N)
    def HasBinding(name: String): AbsBool = get(name) match {
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case (bind, absent) => (bind.isBottom, absent.isBottom) match {
        case (true, true) => AbsBool.Bot
        // 2. If envRec has a binding for the name that is the value of N,
        //    return true.
        case (false, true) => AbsBool.True
        // 3. If it does not have such a binding, return false.
        case (true, false) => AbsBool.False
        case (false, false) => AbsBool.Top
      }
    }

    // 10.2.1.1.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean
    ): AbsDecEnvRec = get(name) match {
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case (bind, absent) => absent.isBottom match {
        // 2. Assert: envRec does not already have a binding for N.
        case true => Bot
        case false => {
          // 3. Create a mutable binding in envRec for N and
          //    set its bound value to undefined.
          val newBind = AbsBinding(MBinding(Undef))
          update(name, (newBind, AbsAbsent.Bot))
        }
      }
    }

    // 10.2.1.1.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean
    ): (AbsDecEnvRec, Set[Exception]) = get(name) match {
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case (bind, _) => bind.isBottom match {
        // 2. Assert: envRec must have a binding for N.
        case true => (Bot, ExcSetEmpty)
        case false => {
          var excSet = ExcSetEmpty
          val envRec = bind.mutable.map[AbsDecEnvRec](
            // 3. If the binding for N in envRec is a mutable binding,
            //    change its bound value to V.
            thenV =
              update(name, (bind.copyWith(value = v), AbsAbsent.Bot)),
            // 4. Else this must be an attempt to change the value of
            //    an immutable binding so if S is true, throw a TypeError
            //    exception.
            elseV =
              if (strict) { excSet += TypeError; Bot }
              else this
          )(AbsDecEnvRec)
          (envRec, excSet)
        }
      }
    }

    // 10.2.1.1.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean
    ): (AbsValue, Set[Exception]) = get(name) match {
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case (bind, _) => bind.isBottom match {
        // 2. Assert: envRec has a binding for N.
        case true => (AbsValue.Bot, ExcSetEmpty)
        case false => {
          var excSet = ExcSetEmpty
          val retV = (bind.mutable.negate && AbsBool(!bind.uninit.isBottom)).map(
            // 3. If the binding for N in envRec is
            //    an uninitialised immutable binding, then
            //    a. If S is false, return the value undefined,
            //       otherwise throw a ReferenceError exception.
            thenV =
            if (strict) { excSet += ReferenceError; AbsValue.Bot }
            else AbsValue(Undef),
            // 4. Else, return the value currently bound to N in envRec.
            elseV =
              bind.value
          )(AbsValue)
          (retV, excSet)
        }
      }
    }

    // 10.2.1.1.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    ): (AbsDecEnvRec, AbsBool) = HasBinding(name).map[AbsDecEnvRec, AbsBool](
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      // 2. If envRec does not have a binding for
      //    the name that is the value of N, return true.
      elseV = (this, AbsBool.True),
      // 3. If the binding for N in envRec is cannot be deleted, return false.
      //    (XXX: we do not consider explicit design)
      // 4. Remove the binding for N from envRec.
      // 5. Return true.
      thenV = ((this - name), AbsBool.True)
    )(AbsDecEnvRec, AbsBool)

    // 10.2.1.1.6 ImplicitThisValue()
    def ImplicitThisValue: AbsValue = {
      // 1. Return undefined.
      AbsUndef.Top
    }

    // 10.2.1.1.7 CreateImmutableBinding(N)
    def CreateImmutableBinding(
      name: String
    ): AbsDecEnvRec = get(name) match {
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case (_, absent) => {
        absent.isBottom match {
          // 2. Assert: envRec does not already have a binding for N.
          case true => Bot
          case false =>
            update(name, (AbsBinding(IBinding(None)), AbsAbsent.Bot))
        }
      }
    }

    // 10.2.1.1.6 InitializeImmutableBinding(N, V)
    def InitializeImmutableBinding(
      name: String,
      v: AbsValue
    ): AbsDecEnvRec = get(name) match {
      // 1. Let envRec be the declarative environment record for
      //    which the method was invoked.
      case (bind, absent) => {
        (AbsBool.False <= bind.mutable && bind.uninit.isTop) match {
          // 2. Assert: envRec must have an uninitialised immutable binding for N.
          case false => Bot
          case true => {
            // 3. Set the bound value for N in envRec to V.
            // 4. Record that the immutable binding for N
            //    in envRec has been initialised.
            val newBind = AbsBinding(v, AbsAbsent.Bot, AbsBool.False)
            update(name, (newBind, AbsAbsent.Bot))
          }
        }
      }
    }

    // substitute locR by locO
    def subsLoc(locR: Loc, locO: Loc): Dom = {
      def subs(map: EnvMap): EnvMap = map.foldLeft(EmptyMap) {
        case (m, (key, (bind, abs))) => {
          val newV = bind.value.subsLoc(locR, locO)
          val newBind = bind.copyWith(value = newV)
          m + (key -> (newBind, abs))
        }
      }
      this match {
        case Bot => Bot
        case LBindMap(map) => LBindMap(subs(map))
        case UBindMap(map) => UBindMap(subs(map))
      }
    }

    // weak substitute locR by locO
    def weakSubsLoc(locR: Loc, locO: Loc): Dom = {
      def subs(map: EnvMap): EnvMap = map.foldLeft(EmptyMap) {
        case (m, (key, (bind, abs))) => {
          val newV = bind.value.weakSubsLoc(locR, locO)
          val newBind = bind.copyWith(value = newV)
          m + (key -> (newBind, abs))
        }
      }
      this match {
        case Bot => Bot
        case LBindMap(map) => LBindMap(subs(map))
        case UBindMap(map) => UBindMap(subs(map))
      }
    }

    // accessor
    def get(name: String): (AbsBinding, AbsAbsent) = this match {
      case Bot => (AbsBinding.Bot, AbsAbsent.Bot)
      case LBindMap(map) => map.get(name) match {
        case Some(pair) => pair
        case None => (AbsBinding.Bot, AbsAbsent.Top)
      }
      case UBindMap(map) => map.get(name) match {
        case Some(pair) => pair
        case None => (AbsBinding.Top, AbsAbsent.Top)
      }
    }

    // strong update
    def update(name: String, pair: (AbsBinding, AbsAbsent)): Dom = {
      val (bind, absent) = pair
      this match {
        case Bot => Bot
        case LBindMap(map) =>
          if (bind.isBottom && absent.isBottom) Bot
          if (bind.isBottom && absent.isTop) LBindMap(map - name)
          else LBindMap(map.updated(name, pair))
        case UBindMap(map) =>
          if (bind.isBottom && absent.isBottom) Bot
          if (bind.isTop && absent.isTop) UBindMap(map - name)
          else UBindMap(map.updated(name, pair))
      }
    }

    // delete
    def -(name: String): Dom =
      update(name, (AbsBinding.Bot, AbsAbsent.Top))
  }

  def newPureLocal(envVal: AbsValue, thisLocSet: AbsLoc): AbsDecEnvRec = {
    Empty
      .update("@env", (AbsBinding(envVal), AbsAbsent.Bot))
      .update("@this", (AbsBinding(thisLocSet), AbsAbsent.Bot))
      .update("@exception", (AbsBinding(AbsUndef.Top), AbsAbsent.Top))
      .update("@exception_all", (AbsBinding(AbsUndef.Top), AbsAbsent.Top))
      .update("@return", (AbsBinding(AbsUndef.Top), AbsAbsent.Bot))
  }
}
