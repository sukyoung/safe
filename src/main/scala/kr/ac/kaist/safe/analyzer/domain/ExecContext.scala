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

// 10.3 Execution Contexts
abstract class ExecContext {
  // TODO var lexEnv: LexEnv = varEnv // LexicalEnvironment
  // partial order
  def <=(that: ExecContext): Boolean = (this, that) match {
    case (ExecContextBot, _) => true
    case (_, ExecContextBot) => false
    case (ExecContextMap(thisMap, thisOld), ExecContextMap(thatMap, thatOld)) => {
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

  // not a partial order
  def </(that: ExecContext): Boolean = !(this <= that)

  private def weakUpdated(m: Map[Loc, AbsDecEnvRec], loc: Loc, newEnv: AbsDecEnvRec): Map[Loc, AbsDecEnvRec] =
    m.get(loc) match {
      case Some(oldEnv) => m.updated(loc, oldEnv + newEnv)
      case None => m.updated(loc, newEnv)
    }

  // join
  def +(that: ExecContext): ExecContext = (this, that) match {
    case (ExecContextBot, _) => that
    case (_, ExecContextBot) => this
    case (ExecContextMap(thisMap, thisOld), ExecContextMap(thatMap, thatOld)) => {
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
        ExecContext(newMap, newOld)
      }
    }
  }

  // meet
  def <>(that: ExecContext): ExecContext = (this, that) match {
    case (ExecContextBot, _) | (_, ExecContextBot) => ExecContextBot
    case (ExecContextMap(thisMap, thisOld), ExecContextMap(thatMap, thatOld)) => {
      if (thisMap eq thatMap) this
      else {
        val locSet = thisMap.keySet intersect thatMap.keySet
        val newMap = locSet.foldLeft(ExecContext.EmptyMap) {
          case (m, loc) => {
            val thisEnv = thisMap(loc)
            val thatEnv = thatMap(loc)
            m + (loc -> (thisEnv <> thatEnv))
          }
        }
        val newOld = thisOld <> thatOld
        ExecContext(newMap, newOld)
      }
    }
  }

  /* lookup */
  def apply(loc: Loc): Option[AbsDecEnvRec] = this match {
    case ExecContextBot => None
    case ExecContextMap(map, old) => map.get(loc)
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

  /* heap update */
  def update(loc: Loc, env: AbsDecEnvRec): ExecContext = this match {
    case ExecContextBot => ExecContextBot
    case ExecContextMap(map, old) => {
      // recent location
      loc.recency match {
        case Recent => ExecContext(map.updated(loc, env), old)
        case Old => ExecContext(weakUpdated(map, loc, env), old)
      }
    }
  }

  /* remove location */
  def remove(loc: Loc): ExecContext = this match {
    case ExecContextBot => ExecContextBot
    case ExecContextMap(map, old) => ExecContext(map - loc, old)
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): ExecContext = this match {
    case ExecContextBot => ExecContextBot
    case ExecContextMap(map, old) => {
      val newMap = map.foldLeft(ExecContext.EmptyMap) {
        case (m, (loc, env)) =>
          m + (loc -> env.subsLoc(locR, locO))
      }
      val newOld = old.subsLoc(locR, locO)
      ExecContext(newMap, newOld)
    }
  }

  def oldify(addr: Address): ExecContext = this match {
    case ExecContextBot => ExecContextBot
    case ExecContextMap(map, old) => {
      val locR = Loc(addr, Recent)
      val locO = Loc(addr, Old)
      val newCtx = if (this domIn locR) {
        update(locO, getOrElse(locR, AbsDecEnvRec.Bot)).remove(locR)
      } else this
      newCtx.subsLoc(locR, locO)
    }
  }

  def domIn(loc: Loc): Boolean = this match {
    case ExecContextBot => false
    case ExecContextMap(map, old) => map.contains(loc)
  }

  def isBottom: Boolean = this match {
    case ExecContextBot => true
    case _ => false
  }

  def setOldAddrSet(old: OldAddrSet): ExecContext = this match {
    case ExecContextBot => ExecContextBot
    case ExecContextMap(map, _) => ExecContext(map, old)
  }

  def old: OldAddrSet = this match {
    case ExecContextBot => OldAddrSet.Bot
    case ExecContextMap(_, old) => old
  }

  override def toString: String = {
    buildString(_ => true).toString
  }

  def toStringAll: String = {
    buildString(_ => true).toString
  }

  private def buildString(filter: Loc => Boolean): String = this match {
    case ExecContextBot => "⊥ExecContext"
    case ExecContextMap(map, old) => {
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
  def varStoreLocal(loc: Loc, x: String, value: AbsValue): ExecContext = {
    val env = this.getOrElse(loc, AbsDecEnvRec.Bot)
    val AT = AbsBool.True
    val AF = AbsBool.False
    val (newEnv, _) = env.SetMutableBinding(x, value)
    val ctx1 = update(loc, newEnv)
    val (outerV, _) = env.GetBindingValue("@outer")
    val ctx2 =
      if (AbsBool.False <= (env HasBinding x))
        outerV.locset.foldLeft(ExecContext.Empty)((tmpH, outerLoc) => varStoreLocal(outerLoc, x, value))
      else
        ExecContext.Bot
    ctx1 + ctx2
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, str: String): (ExecContext, AbsBool) = {
    getOrElse(loc)((this, AbsBool.Bot))(_ => {
      val test = hasOwnProperty(loc, str)
      val targetEnv = this.getOrElse(loc, AbsDecEnvRec.Bot)
      if (AbsBool.True <= test)
        (this, AbsBool.False)
      else
        (ExecContext.Bot, AbsBool.Bot)
    })
  }

  private def hasOwnProperty(loc: Loc, str: String): AbsBool = {
    (this.getOrElse(loc, AbsDecEnvRec.Bot) HasBinding str)
  }

  ////////////////////////////////////////////////////////////////
  // pure local environment
  ////////////////////////////////////////////////////////////////
  def pureLocal: AbsDecEnvRec = getOrElse(PURE_LOCAL, AbsDecEnvRec.Bot)
  def subsPureLocal(env: AbsDecEnvRec): ExecContext = update(PURE_LOCAL, env)
}

object ExecContext {
  private val EmptyMap: Map[Loc, AbsDecEnvRec] = HashMap()
  val Bot: ExecContext = ExecContextBot
  val Empty: ExecContext = ExecContextMap(EmptyMap, OldAddrSet.Empty)
  def apply(
    map: Map[Loc, AbsDecEnvRec],
    old: OldAddrSet
  ): ExecContext = new ExecContextMap(map, old)
}

case object ExecContextBot extends ExecContext
case class ExecContextMap(
  // TODO val varEnv: LexEnv // VariableEnvironment
  // val thisBinding: AbsLoc, // ThisBinding
  // val oldAddrSet: OldAddrSet // TODO old address set
  val map: Map[Loc, AbsDecEnvRec],
  override val old: OldAddrSet
) extends ExecContext
