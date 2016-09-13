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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.PredefLoc
import kr.ac.kaist.safe.analyzer.models.PredefLoc.{ COLLAPSED, PURE_LOCAL }
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap, HashSet }
import kr.ac.kaist.safe.nodes.cfg._

/* 10.3 Execution Contexts */

////////////////////////////////////////////////////////////////////////////////
// concrete execution context type
////////////////////////////////////////////////////////////////////////////////
trait Context // TODO

////////////////////////////////////////////////////////////////////////////////
// execution context abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsContext extends AbsDomain[Context, AbsContext] {
  // lookup
  def apply(loc: Loc): Option[AbsDecEnvRec]
  def getOrElse(loc: Loc, default: AbsDecEnvRec): AbsDecEnvRec
  def getOrElse[T](loc: Loc)(default: T)(f: AbsDecEnvRec => T): T

  // context update
  def update(loc: Loc, env: AbsDecEnvRec): AbsContext

  // remove location
  def remove(loc: Loc): AbsContext

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): AbsContext

  def oldify(addr: Address): AbsContext

  def domIn(loc: Loc): Boolean

  def isBottom: Boolean

  def setOldAddrSet(old: OldAddrSet): AbsContext

  def old: OldAddrSet

  def toStringAll: String

  def toStringLoc(loc: Loc): Option[String]

  // Lookup
  def lookupLocal(loc: Loc, x: String): AbsValue
  def lookupBaseLocal(loc: Loc, x: String): AbsLoc

  // Store
  def varStoreLocal(loc: Loc, x: String, value: AbsValue): AbsContext

  // delete
  def delete(loc: Loc, str: String): (AbsContext, AbsBool)

  // pure local environment
  def pureLocal: AbsDecEnvRec
  def subsPureLocal(env: AbsDecEnvRec): AbsContext
}

trait AbsContextUtil extends AbsDomainUtil[Context, AbsContext] {
  val Empty: AbsContext
  def apply(
    map: Map[Loc, AbsDecEnvRec],
    old: OldAddrSet
  ): AbsContext
}

////////////////////////////////////////////////////////////////////////////////
// default execution context abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultContext extends AbsContextUtil {
  private val EmptyMap: Map[Loc, AbsDecEnvRec] = HashMap()

  case object Bot extends Dom
  case object Top extends Dom
  case class CtxMap(
    // TODO val varEnv: LexEnv // VariableEnvironment
    // val thisBinding: AbsLoc, // ThisBinding
    // val oldAddrSet: OldAddrSet // TODO old address set
    val map: Map[Loc, AbsDecEnvRec],
    override val old: OldAddrSet
  ) extends Dom
  val Empty: AbsContext = CtxMap(EmptyMap, OldAddrSet.Empty)

  def alpha(ctx: Context): AbsContext = Top // TODO more precise

  def apply(
    map: Map[Loc, AbsDecEnvRec],
    old: OldAddrSet
  ): AbsContext = new CtxMap(map, old)

  abstract class Dom extends AbsContext {
    def gamma: ConSet[Context] = ConInf() // TODO more precise

    def getSingle: ConSingle[Context] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsContext): Boolean = (this, check(that)) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (_, Top) => true
      case (Top, _) => false
      case (CtxMap(thisMap, thisOld), CtxMap(thatMap, thatOld)) => {
        val mapB =
          if (thisMap.isEmpty) true
          else if (thatMap.isEmpty) false
          else thisMap.forall {
            case (loc, thisEnv) => thatMap.get(loc) match {
              case None => false
              case Some(thatEnv) => thisEnv <= thatEnv
            }
          }
        val oldB = thisOld <= thatOld
        mapB && oldB
      }
    }

    def +(that: AbsContext): AbsContext = (this, check(that)) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (Top, _) | (_, Top) => Top
      case (CtxMap(thisMap, thisOld), CtxMap(thatMap, thatOld)) => {
        if (this eq that) this
        else {
          val newMap = thatMap.foldLeft(thisMap) {
            case (m, (loc, thatEnv)) => m.get(loc) match {
              case None => m + (loc -> thatEnv)
              case Some(thisEnv) =>
                m + (loc -> (thisEnv + thatEnv))
            }
          }
          val newOld = thisOld + thatOld
          AbsContext(newMap, newOld)
        }
      }
    }

    def <>(that: AbsContext): AbsContext = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => Bot
      case (Top, _) => that
      case (_, Top) => this
      case (CtxMap(thisMap, thisOld), CtxMap(thatMap, thatOld)) => {
        if (thisMap eq thatMap) this
        else {
          val locSet = thisMap.keySet intersect thatMap.keySet
          val newMap = locSet.foldLeft(EmptyMap) {
            case (m, loc) => {
              val thisEnv = thisMap(loc)
              val thatEnv = thatMap(loc)
              m + (loc -> (thisEnv <> thatEnv))
            }
          }
          val newOld = thisOld <> thatOld
          AbsContext(newMap, newOld)
        }
      }
    }

    def apply(loc: Loc): Option[AbsDecEnvRec] = this match {
      case Bot => None
      case Top => Some(AbsDecEnvRec.Top)
      case CtxMap(map, old) => map.get(loc)
    }

    def getOrElse(loc: Loc, default: AbsDecEnvRec): AbsDecEnvRec =
      this(loc) match {
        case Some(env) => env
        case None => default
      }

    def getOrElse[T](loc: Loc)(default: T)(f: AbsDecEnvRec => T): T = {
      this(loc) match {
        case Some(env) => f(env)
        case None => default
      }
    }

    private def weakUpdated(m: Map[Loc, AbsDecEnvRec], loc: Loc, newEnv: AbsDecEnvRec): Map[Loc, AbsDecEnvRec] =
      m.get(loc) match {
        case Some(oldEnv) => m.updated(loc, oldEnv + newEnv)
        case None => m.updated(loc, newEnv)
      }

    def update(loc: Loc, env: AbsDecEnvRec): AbsContext = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, old) => {
        // recent location
        loc.recency match {
          case Recent => AbsContext(map.updated(loc, env), old)
          case Old => AbsContext(weakUpdated(map, loc, env), old)
        }
      }
    }

    def remove(loc: Loc): AbsContext = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, old) => AbsContext(map - loc, old)
    }

    def subsLoc(locR: Loc, locO: Loc): AbsContext = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, old) => {
        val newMap = map.foldLeft(EmptyMap) {
          case (m, (loc, env)) =>
            m + (loc -> env.subsLoc(locR, locO))
        }
        val newOld = old.subsLoc(locR, locO)
        AbsContext(newMap, newOld)
      }
    }

    def oldify(addr: Address): AbsContext = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, old) => {
        val locR = Loc(addr, Recent)
        val locO = Loc(addr, Old)
        val newCtx = if (this domIn locR) {
          update(locO, getOrElse(locR, AbsDecEnvRec.Bot)).remove(locR)
        } else this
        newCtx.subsLoc(locR, locO)
      }
    }

    def domIn(loc: Loc): Boolean = this match {
      case Bot => false
      case Top => true
      case CtxMap(map, old) => map.contains(loc)
    }

    def setOldAddrSet(old: OldAddrSet): AbsContext = this match {
      case Bot => Bot
      case Top => Top
      case CtxMap(map, _) => AbsContext(map, old)
    }

    def old: OldAddrSet = this match {
      case Bot => OldAddrSet.Bot
      case Top => OldAddrSet.Bot // TODO it is not sound
      case CtxMap(_, old) => old
    }

    override def toString: String = {
      buildString(_ => true).toString
    }

    def toStringAll: String = {
      buildString(_ => true).toString
    }

    private def buildString(filter: Loc => Boolean): String = this match {
      case Bot => "⊥AbsContext"
      case Top => "Top"
      case CtxMap(map, old) => {
        val s = new StringBuilder
        val sortedSeq =
          map.toSeq.filter { case (loc, _) => filter(loc) }
            .sortBy { case (loc, _) => loc }
        sortedSeq.map {
          case (loc, env) => s.append(toStringLoc(loc, env)).append(LINE_SEP)
        }
        s.toString
      }
    }

    def toStringLoc(loc: Loc): Option[String] = {
      apply(loc).map(toStringLoc(loc, _))
    }

    private def toStringLoc(loc: Loc, env: AbsDecEnvRec): String = {
      val s = new StringBuilder
      val keyStr = loc.toString + " -> "
      s.append(keyStr)
      Useful.indentation(s, env.toString, keyStr.length)
      s.toString
    }

    ////////////////////////////////////////////////////////////////
    // Lookup
    ////////////////////////////////////////////////////////////////
    def lookupLocal(loc: Loc, x: String): AbsValue = {
      var visited = AbsLoc.Bot
      val valueBot = AbsValue.Bot
      def visit(l: Loc): AbsValue = {
        if (visited.contains(l)) valueBot
        else {
          visited += l
          val env = this.getOrElse(l, AbsDecEnvRec.Bot)
          val isIn = (env HasBinding x)
          isIn.map[AbsValue](thenV = {
            val (value, _) = env.GetBindingValue(x)
            value
          }, elseV = {
            val (outerV, _) = env.GetBindingValue("@outer")
            outerV.locset.foldLeft(valueBot)((tmpVal, outerLoc) => tmpVal + visit(outerLoc))
          })(AbsValue)
        }
      }
      visit(loc)
    }

    def lookupBaseLocal(loc: Loc, x: String): AbsLoc = {
      var visited = AbsLoc.Bot
      def visit(l: Loc): AbsLoc = {
        if (visited.contains(l)) AbsLoc.Bot
        else {
          visited += l
          val env = this.getOrElse(l, AbsDecEnvRec.Bot)
          val isIn = (env HasBinding x)
          isIn.map[AbsLoc](
            thenV = AbsLoc(l),
            elseV = {
              val (outerV, _) = env.GetBindingValue("@outer")
              outerV.locset.foldLeft(AbsLoc.Bot)((res, outerLoc) => res + visit(outerLoc))
            }
          )(AbsLoc)
        }
      }
      visit(loc)
    }

    ////////////////////////////////////////////////////////////////
    // Store
    ////////////////////////////////////////////////////////////////
    def varStoreLocal(loc: Loc, x: String, value: AbsValue): AbsContext = {
      val env = this.getOrElse(loc, AbsDecEnvRec.Bot)
      val AT = AbsBool.True
      val AF = AbsBool.False
      val (newEnv, _) = env.SetMutableBinding(x, value)
      val ctx1 = update(loc, newEnv)
      val (outerV, _) = env.GetBindingValue("@outer")
      val ctx2 =
        if (AbsBool.False <= (env HasBinding x))
          outerV.locset.foldLeft(Empty)((tmpH, outerLoc) => varStoreLocal(outerLoc, x, value))
        else
          AbsContext.Bot
      ctx1 + ctx2
    }

    ////////////////////////////////////////////////////////////////
    // delete
    ////////////////////////////////////////////////////////////////
    def delete(loc: Loc, str: String): (AbsContext, AbsBool) = {
      getOrElse(loc)((this, AbsBool.Bot))(_ => {
        val test = hasOwnProperty(loc, str)
        val targetEnv = this.getOrElse(loc, AbsDecEnvRec.Bot)
        if (AbsBool.True <= test)
          (this, AbsBool.False)
        else
          (Bot, AbsBool.Bot)
      })
    }

    private def hasOwnProperty(loc: Loc, str: String): AbsBool = {
      (this.getOrElse(loc, AbsDecEnvRec.Bot) HasBinding str)
    }

    ////////////////////////////////////////////////////////////////
    // pure local environment
    ////////////////////////////////////////////////////////////////
    def pureLocal: AbsDecEnvRec = getOrElse(PURE_LOCAL, AbsDecEnvRec.Bot)
    def subsPureLocal(env: AbsDecEnvRec): AbsContext = update(PURE_LOCAL, env)
  }
}
