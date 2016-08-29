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
import kr.ac.kaist.safe.analyzer.models.PredefLoc.{ COLLAPSED, SINGLE_PURE_LOCAL }
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap, HashSet }
import kr.ac.kaist.safe.nodes.cfg._

// 10.3 Execution Contexts
class ExecContext(
    // TODO val varEnv: LexEnv // VariableEnvironment
    // val thisBinding: Set[Loc], // ThisBinding
    // val pureLocal: DecEnvRecord, // TODO Pure Local
    // val oldAddrSet: OldAddrSet // TODO old address set
    val map: Map[Loc, DecEnvRecord],
    val old: OldAddrSet
) {
  // TODO var lexEnv: LexEnv = varEnv // LexicalEnvironment
  /* partial order */
  def <=(that: ExecContext): Boolean = {
    val mapB =
      if (this.map eq that.map) true
      else if (this.map.size > that.map.size) false
      else if (this.map.isEmpty) true
      else if (that.map.isEmpty) false
      else if (!(this.map.keySet subsetOf that.map.keySet)) false
      else that.map.forall((kv) => {
        val (l, obj) = kv
        this.map.get(l) match {
          case Some(thisEnv) => thisEnv <= obj
          case None => false
        }
      })
    val oldB = this.old <= that.old
    mapB && oldB
  }

  private def weakUpdated(m: Map[Loc, DecEnvRecord], loc: Loc, newEnv: DecEnvRecord): Map[Loc, DecEnvRecord] =
    m.get(loc) match {
      case Some(oldEnv) => m.updated(loc, oldEnv + newEnv)
      case None => m.updated(loc, newEnv)
    }

  /* join */
  def +(that: ExecContext): ExecContext = {
    val newMap =
      if (this.map eq that.map) this.map
      else if (this.isBottom) that.map
      else if (that.isBottom) this.map
      else {
        val joinKeySet = this.map.keySet ++ that.map.keySet
        joinKeySet.foldLeft(HashMap[Loc, DecEnvRecord]())((m, key) => {
          val joinEnv = (this.map.get(key), that.map.get(key)) match {
            case (Some(obj1), Some(obj2)) => Some(obj1 + obj2)
            case (Some(obj1), None) => Some(obj1)
            case (None, Some(obj2)) => Some(obj2)
            case (None, None) => None
          }
          joinEnv match {
            case Some(obj) => m.updated(key, obj)
            case None => m
          }
        })
      }
    val newOld = this.old + that.old
    ExecContext(newMap, newOld)
  }

  /* meet */
  def <>(that: ExecContext): ExecContext = {
    val newMap: Map[Loc, DecEnvRecord] =
      if (this.map eq that.map) this.map
      else if (this.map.isEmpty) HashMap()
      else if (that.map.isEmpty) HashMap()
      else {
        that.map.foldLeft(this.map)(
          (m, kv) => kv match {
            case (k, v) => m.get(k) match {
              case None => m - k
              case Some(vv) => m + (k -> (v <> vv))
            }
          }
        )
      }
    val newOld = this.old <> that.old
    ExecContext(newMap, newOld)
  }

  /* lookup */
  def apply(loc: Loc): Option[DecEnvRecord] = map.get(loc)

  def getOrElse(loc: Loc, default: DecEnvRecord): DecEnvRecord =
    this(loc) match {
      case Some(obj) => obj
      case None => default
    }

  def getOrElse[T](loc: Loc)(default: T)(f: DecEnvRecord => T): T = {
    this(loc) match {
      case Some(obj) => f(obj)
      case None => default
    }
  }

  /* heap update */
  def update(loc: Loc, obj: DecEnvRecord): ExecContext = {
    if (!isBottom) {
      // recent location
      loc.recency match {
        case Recent =>
          if (obj.isBottom) ExecContext.Bot
          else ExecContext(map.updated(loc, obj), old)
        case Old =>
          if (obj.isBottom) this.getOrElse(loc)(ExecContext.Bot) { _ => this }
          else ExecContext(weakUpdated(map, loc, obj), old)
      }
    } else {
      this
    }
  }

  /* remove location */
  def remove(loc: Loc): ExecContext = {
    ExecContext(map - loc, old)
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): ExecContext = {
    val newMap =
      if (this.map.isEmpty) this.map
      else {
        this.map.foldLeft(Map[Loc, DecEnvRecord]())((m, kv) => {
          val (l, obj) = kv
          m + (l -> obj.subsLoc(locR, locO))
        })
      }
    val newOld = this.old.subsLoc(locR, locO)
    ExecContext(newMap, newOld)
  }

  def oldify(addr: Address)(utils: Utils): ExecContext = {
    if (this.old.isBottom) ExecContext.Bot
    else {
      val locR = Loc(addr, Recent)
      val locO = Loc(addr, Old)
      if (this domIn locR) {
        update(locO, getOrElse(locR, DecEnvRecord.Bot(utils))).remove(locR).subsLoc(locR, locO)
      } else {
        subsLoc(locR, locO)
      }
    }
  }

  def domIn(loc: Loc): Boolean = map.contains(loc)

  def isBottom: Boolean =
    this.map.isEmpty && this.old.isBottom // TODO is really bottom?

  override def toString: String = {
    buildString(loc => loc match {
      case Loc(ProgramAddr(_), _) => true
      case SINGLE_PURE_LOCAL | COLLAPSED => true
      case _ => false
    }).toString
  }

  def toStringAll: String = {
    buildString(_ => true).toString
  }

  private def buildString(filter: Loc => Boolean): String = {
    val s = new StringBuilder
    this match {
      case _ if isBottom => s.append("⊥ExecContext")
      case _ => {
        val sortedSeq =
          map.toSeq.filter { case (loc, _) => filter(loc) }
            .sortBy { case (loc, _) => loc }
        sortedSeq.map {
          case (loc, obj) => s.append(toStringLoc(loc, obj)).append(LINE_SEP)
        }
      }
    }
    s.toString
  }

  def toStringLoc(loc: Loc): Option[String] = {
    map.get(loc).map(toStringLoc(loc, _))
  }

  private def toStringLoc(loc: Loc, obj: DecEnvRecord): String = {
    val s = new StringBuilder
    val keyStr = loc.toString + " -> "
    s.append(keyStr)
    Useful.indentation(s, obj.toString, keyStr.length)
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
        val env = this.getOrElse(l, DecEnvRecord.Bot(utils))
        val isDomIn = (env domIn x)(utils.absBool)
        val v1 =
          if (utils.absBool.True <= isDomIn) env.getOrElse(x)(valueBot) { _.objval.value }
          else valueBot
        val v2 =
          if (utils.absBool.False <= isDomIn) {
            val outerLocSet = env.getOrElse("@outer")(LocSetEmpty) { _.objval.value.locset }
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
        val env = this.getOrElse(l, DecEnvRecord.Bot(utils))
        val isDomIn = (env domIn x)(utils.absBool)
        val locSet1 =
          if (utils.absBool.True <= isDomIn) HashSet(l)
          else LocSetEmpty
        val locSet2 =
          if (utils.absBool.False <= isDomIn) {
            val outerLocSet = env.getOrElse("@outer")(LocSetEmpty) { _.objval.value.locset }
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
    val env = this.getOrElse(loc, DecEnvRecord.Bot(utils))
    val h1 = env(x) match {
      case Some(propV) if propV.objval.writable == utils.absBool.True =>
        val newPropV = PropValue(utils.dataProp(value)(utils.absBool.True, utils.absBool.Bot, utils.absBool.False))
        this.update(loc, env.update(x, newPropV))
      case Some(propV) if propV.objval.writable == utils.absBool.False => this
      case _ => ExecContext.Bot
    }
    val outerLocSet = env("@outer") match {
      case Some(propV) => propV.objval.value.locset
      case None => LocSetEmpty
    }
    val h2 =
      if (utils.absBool.False <= (env domIn x)(utils.absBool))
        outerLocSet.foldLeft(ExecContext.Bot)((tmpH, outerLoc) => varStoreLocal(outerLoc, x, value)(utils))
      else
        ExecContext.Bot
    h1 + h2
  }

  ////////////////////////////////////////////////////////////////
  // delete
  ////////////////////////////////////////////////////////////////
  def delete(loc: Loc, absStr: AbsString)(utils: Utils): (ExecContext, AbsBool) = {
    getOrElse(loc)((this, utils.absBool.Bot))(_ => {
      val test = hasOwnProperty(loc, absStr)(utils)
      val targetEnv = this.getOrElse(loc, DecEnvRecord.Bot(utils))
      val isConfigurable = targetEnv.getOrElse(absStr)(utils.absBool.Bot) { _.objval.configurable }
      val (h1, b1) =
        if ((utils.absBool.True <= test) && (utils.absBool.False <= isConfigurable))
          (this, utils.absBool.False)
        else
          (ExecContext.Bot, utils.absBool.Bot)
      val (h2, b2) =
        if (((utils.absBool.True <= test) && (utils.absBool.False != isConfigurable))
          || utils.absBool.False <= test)
          (this.update(loc, (targetEnv - absStr)(utils)), utils.absBool.True)
        else
          (ExecContext.Bot, utils.absBool.Bot)
      (h1 + h2, b1 + b2)
    })
  }

  private def hasOwnProperty(loc: Loc, absStr: AbsString)(utils: Utils): AbsBool = {
    (this.getOrElse(loc, DecEnvRecord.Bot(utils)) domIn absStr)(utils.absBool)
  }
}

object ExecContext {
  val Bot: ExecContext = new ExecContext(HashMap[Loc, DecEnvRecord](), OldAddrSet.Bot)
  def apply(map: Map[Loc, DecEnvRecord], old: OldAddrSet): ExecContext = new ExecContext(map, old)
}
