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

  private def weakUpdated(m: Map[Loc, DecEnvRecord], loc: Loc, newEnv: DecEnvRecord): Map[Loc, DecEnvRecord] =
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
  def apply(loc: Loc): Option[DecEnvRecord] = this match {
    case ExecContextBot => None
    case ExecContextMap(map, old) => map.get(loc)
  }

  def getOrElse(loc: Loc, default: DecEnvRecord): DecEnvRecord =
    this(loc) match {
      case Some(env) => env
      case None => default
    }

  def getOrElse[T](loc: Loc)(default: T)(f: DecEnvRecord => T): T = {
    this(loc) match {
      case Some(env) => f(env)
      case None => default
    }
  }

  /* heap update */
  def update(loc: Loc, env: DecEnvRecord): ExecContext = this match {
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

  def oldify(addr: Address)(utils: Utils): ExecContext = this match {
    case ExecContextBot => ExecContextBot
    case ExecContextMap(map, old) => {
      val locR = Loc(addr, Recent)
      val locO = Loc(addr, Old)
      val newCtx = if (this domIn locR) {
        update(locO, getOrElse(locR, DecEnvRecord.Bot)).remove(locR)
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

  private def toStringLoc(loc: Loc, env: DecEnvRecord): String = {
    val s = new StringBuilder
    val keyStr = loc.toString + " -> "
    s.append(keyStr)
    Useful.indentation(s, env.toString, keyStr.length)
    s.toString
  }

  ////////////////////////////////////////////////////////////////
  // Lookup
  ////////////////////////////////////////////////////////////////
  def lookupLocal(loc: Loc, x: String)(utils: Utils): Value = {
    var visited = LocSetEmpty
    val valueBot = utils.value.Bot
    def visit(l: Loc): Value = {
      if (visited.contains(l)) valueBot
      else {
        visited += l
        val env = this.getOrElse(l, DecEnvRecord.Bot)
        val isIn = (env HasBinding x)(utils.absBool)
        val v1 =
          if (utils.absBool.True <= isIn) env.getOrElse(x)(valueBot) { _.value }
          else valueBot
        val v2 =
          if (utils.absBool.False <= isIn) {
            val outerLocSet = env.getOrElse("@outer")(LocSetEmpty) { _.value.locset }
            outerLocSet.foldLeft(valueBot)((tmpVal, outerLoc) => tmpVal + visit(outerLoc))
          } else {
            valueBot
          }
        v1 + v2
      }
    }
    visit(loc)
  }

  def lookupBaseLocal(loc: Loc, x: String)(utils: Utils): Set[Loc] = {
    var visited = LocSetEmpty
    def visit(l: Loc): Set[Loc] = {
      if (visited.contains(l)) LocSetEmpty
      else {
        visited += l
        val env = this.getOrElse(l, DecEnvRecord.Bot)
        val isIn = (env HasBinding x)(utils.absBool)
        val locSet1 =
          if (utils.absBool.True <= isIn) HashSet(l)
          else LocSetEmpty
        val locSet2 =
          if (utils.absBool.False <= isIn) {
            val outerLocSet = env.getOrElse("@outer")(LocSetEmpty) { _.value.locset }
            outerLocSet.foldLeft(LocSetEmpty)((res, outerLoc) => res ++ visit(outerLoc))
          } else {
            LocSetEmpty
          }
        locSet1 ++ locSet2
      }
    }
    visit(loc)
  }

  ////////////////////////////////////////////////////////////////
  // Store
  ////////////////////////////////////////////////////////////////
  def varStoreLocal(loc: Loc, x: String, value: Value)(utils: Utils): ExecContext = {
    val env = this.getOrElse(loc, DecEnvRecord.Bot)
    val AT = utils.absBool.True
    val AF = utils.absBool.False
    val ctx1 = env(x) match {
      case Some(bind) => {
        val trueV =
          if (AT <= bind.mutable) value
          else utils.value.Bot
        val falseV =
          if (AF <= bind.mutable) bind.value
          else utils.value.Bot
        val newBind = utils.binding(trueV + falseV)
        update(loc, env.update(x, newBind))
      }
      case None => ExecContext.Bot
    }
    val outerLocSet = env("@outer") match {
      case Some(bind) => bind.value.locset
      case None => LocSetEmpty
    }
    val ctx2 =
      if (utils.absBool.False <= (env HasBinding x)(utils.absBool))
        outerLocSet.foldLeft(ExecContext.Empty)((tmpH, outerLoc) => varStoreLocal(outerLoc, x, value)(utils))
      else
        ExecContext.Bot
    ctx1 + ctx2
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, str: String)(utils: Utils): (ExecContext, AbsBool) = {
    getOrElse(loc)((this, utils.absBool.Bot))(_ => {
      val test = hasOwnProperty(loc, str)(utils)
      val targetEnv = this.getOrElse(loc, DecEnvRecord.Bot)
      if (utils.absBool.True <= test)
        (this, utils.absBool.False)
      else
        (ExecContext.Bot, utils.absBool.Bot)
    })
  }

  private def hasOwnProperty(loc: Loc, str: String)(utils: Utils): AbsBool = {
    (this.getOrElse(loc, DecEnvRecord.Bot) HasBinding str)(utils.absBool)
  }

  ////////////////////////////////////////////////////////////////
  // pure local environment
  ////////////////////////////////////////////////////////////////
  def pureLocal: DecEnvRecord = getOrElse(PURE_LOCAL, DecEnvRecord.Bot)
  def subsPureLocal(env: DecEnvRecord): ExecContext = update(PURE_LOCAL, env)
}

object ExecContext {
  private val EmptyMap: Map[Loc, DecEnvRecord] = HashMap()
  val Bot: ExecContext = ExecContextBot
  val Empty: ExecContext = ExecContextMap(EmptyMap, OldAddrSet.Empty)
  def apply(
    map: Map[Loc, DecEnvRecord],
    old: OldAddrSet
  ): ExecContext = new ExecContextMap(map, old)
}

case object ExecContextBot extends ExecContext
case class ExecContextMap(
  // TODO val varEnv: LexEnv // VariableEnvironment
  // val thisBinding: Set[Loc], // ThisBinding
  // val oldAddrSet: OldAddrSet // TODO old address set
  val map: Map[Loc, DecEnvRecord],
  override val old: OldAddrSet
) extends ExecContext
