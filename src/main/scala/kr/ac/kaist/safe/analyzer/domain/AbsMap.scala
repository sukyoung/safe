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
import kr.ac.kaist.safe.analyzer.domain.Utils._

import scala.collection.immutable.{ HashMap, HashSet }

object DefSet {
  val Empty: DefSet = DefSetFin(HashSet[String]())
  val Top: DefSet = DefSetTop
}

sealed abstract class DefSet {
  override def toString: String =
    this match {
      case DefSetTop => "⊤String"
      case DefSetFin(s) => s.toString
    }
  def ++(that: DefSet): DefSet =
    (this, that) match {
      case (DefSetTop, _) | (_, DefSetTop) => DefSetTop
      case (DefSetFin(a), DefSetFin(b)) => DefSetFin(a ++ b)
    }

  def --(that: DefSet): DefSet =
    (this, that) match {
      case (DefSetTop, _) | (_, DefSetTop) => DefSetFin(HashSet())
      case (DefSetFin(a), DefSetFin(b)) => DefSetFin(a -- b)
    }

  def +(elem: String): DefSet =
    this match {
      case DefSetTop => DefSetTop
      case DefSetFin(a) => DefSetFin(a + elem)
    }

  def -(elem: String): DefSet =
    this match {
      case DefSetTop => DefSetFin(HashSet())
      case DefSetFin(a) => DefSetFin(a - elem)
    }

  def subsetOf(that: DefSet): Boolean =
    (this, that) match {
      case (_, DefSetTop) => true
      case (DefSetTop, DefSetFin(_)) => false
      case (DefSetFin(a), DefSetFin(b)) => a subsetOf b
    }

  def contains(elem: String): Boolean =
    this match {
      case DefSetTop => true
      case DefSetFin(a) => a contains elem
    }

  def contains(elem: AbsString): Boolean =
    (this, elem.gamma) match {
      case (DefSetTop, _) => true
      case (DefSetFin(_), conset) if conset.isBottom => true
      case (DefSetFin(_), ConInf()) => false
      case (DefSetFin(a), ConFin(b)) => b subsetOf a
    }

  def intersect(that: DefSet): DefSet =
    (this, that) match {
      case (DefSetTop, _) => that
      case (_, DefSetTop) => this
      case (DefSetFin(a), DefSetFin(b)) => DefSetFin(a intersect b)
    }
}
case class DefSetFin(set: Set[String]) extends DefSet
case object DefSetTop extends DefSet

sealed abstract class AbsMap(
    private val map: Map[AbsString, AbsDataProp],
    private val defset: DefSet
) {
  override def toString: String = {
    val sortedMap = map.toSeq.sortBy {
      case (key, _) => key.toString
    }

    val s = new StringBuilder
    sortedMap.map {
      case (k, v) => {
        s.append(k)
          .append(s" -> ")
          .append(v.toString)
          .append(LINE_SEP)
      }
    }
    s.append(defset.toString)

    s.toString
  }

  /* partial order */
  def <=(that: AbsMap): Boolean =
    (this, that) match {
      case (AbsMapBot, _) => true
      case (_, AbsMapBot) => false
      case _ if !(that.defset subsetOf this.defset) => false
      case _ if that.defset subsetOf this.defset =>
        this.map.forall(kv1 => {
          val (key1, value1) = kv1
          val test = that.map.exists(kv2 => {
            val (key2, value2) = kv2
            (key1 <= key2) && (value1 <= value2)
          })
          if (!test) {
            val value2 = that.map.foldLeft(AbsDataProp.Bot)((adp, kv2) => {
              val (key2, value2) = kv2
              if (key1 isRelated key2) adp + value2
              else adp
            })
            value1 <= value2
          } else test
        })
    }

  /* join */
  def +(that: AbsMap): AbsMap =
    (this, that) match {
      case (AbsMapBot, _) => that
      case (_, AbsMapBot) => this
      case _ =>
        val thisKeys = this.map.keySet
        val thatKeys = that.map.keySet
        val map1 = (thisKeys -- thatKeys).foldLeft(HashMap[AbsString, AbsDataProp]())((m, key) => {
          m + (key -> this.map(key))
        })
        val map2 = (thatKeys -- thisKeys).foldLeft(map1)((m, key) => {
          m + (key -> that.map(key))
        })
        val mapCap = (thisKeys intersect thatKeys).foldLeft(map2)((m, key) => {
          m + (key -> (this.map(key) + that.map(key)))
        })
        AbsMapFin(mapCap, this.defset intersect that.defset)
    }

  /* property read */
  def lookup(str: String): (Set[AbsDataProp], Boolean) = {
    val emptySet = HashSet[AbsDataProp]()
    val astr = AbsString(str)
    val domIn = (this.map.keySet contains astr) && (this.defset contains str)
    this match {
      case AbsMapBot => (emptySet, false)
      case _ if domIn => (HashSet(this.map(astr)), true)
      case _ =>
        val dpset = this.map.foldLeft(emptySet)((dps, kv) => {
          val (key, dp) = kv
          if (key isRelated str) dps + dp
          else dps
        })
        (dpset, defset contains str)
    }
  }

  def lookup(astr: AbsString): (Set[AbsDataProp], Boolean) = {
    val emptySet = HashSet[AbsDataProp]()
    (this, astr.gamma) match {
      case (AbsMapBot, _) => (emptySet, false)
      case (_, conset) if conset.isBottom => (emptySet, false)
      case _ =>
        val dpset = this.map.foldLeft(emptySet)((dps, kv) => {
          val (key, dp) = kv
          if (key isRelated astr) dps + dp
          else dps
        })
        (dpset, defset contains astr)
    }
  }

  /* property write */
  def initializeUpdate(str: String, dp: AbsDataProp): AbsMap = {
    val astr = AbsString(str)
    val domIn = this.map.keySet contains astr

    this match {
      case AbsMapBot => AbsMapBot
      case _ if !domIn => AbsMapFin(this.map + (astr -> dp), this.defset + str)
      case _ if domIn =>
        val old = this.map(astr)
        val newMap = this.map + (astr -> (old + dp))
        AbsMapFin(newMap, this.defset + str)
    }
  }

  def update(str: String, dp: AbsDataProp, weak: Boolean = false): AbsMap = {
    // TODO: add Map[String, AbsDataProp] for performance
    val astr = AbsString(str)
    val domIn = this.map.keySet contains astr

    this match {
      case AbsMapBot => AbsMapBot
      case _ if dp.isBottom => AbsMapBot
      case _ if !domIn && !weak => AbsMapFin(this.map + (astr -> dp), this.defset + str)
      case _ if !domIn && weak => AbsMapFin(this.map + (astr -> dp), this.defset)
      case _ if domIn && !weak => // Strong update
        AbsMapFin(this.map + (astr -> dp), this.defset + str)
      case _ if domIn && weak => // Weak update
        val old = this.map(astr)
        val newMap = this.map + (astr -> (old + dp))
        AbsMapFin(newMap, this.defset)
    }
  }

  def update(astr: AbsString, dp: AbsDataProp): AbsMap = {
    val domIn = this.map.keySet contains astr

    (this, astr.gamma) match {
      case (AbsMapBot, _) => AbsMapBot
      case (_, conset) if conset.isBottom => AbsMapBot
      case _ if dp.isBottom => AbsMapBot

      case (_, ConFin(strSet)) if strSet.size == 1 => this.update(strSet.head, dp)
      case (_, ConFin(strSet)) => strSet.foldLeft(this)((am, str) => am.update(str, dp, true))
      case (_, ConInf()) if !domIn => AbsMapFin(this.map + (astr -> dp), this.defset)
      case (_, ConInf()) if domIn =>
        val old = this.map(astr)
        val newMap = this.map + (astr -> (old + dp))
        AbsMapFin(newMap, this.defset)
    }
  }

  /* property delete */
  def delete(str: String): (AbsMap, AbsBool) = {
    val astr = AbsString(str)
    val (domIn, configurable) =
      if (this.map.keySet contains astr) (true, this.map(astr).configurable)
      else (false, AbsBool.Bot)
    val newDefSet = this.defset - str

    this match {
      case AbsMapBot => (AbsMapBot, AbsBool.Bot)
      case _ if !domIn =>
        val newAbsMap = AbsMapFin(this.map, newDefSet)
        (newAbsMap, AbsBool.Top)
      case _ if domIn => {
        val (falseMap, falseB) =
          if (AbsBool.False <= configurable) (this, AbsBool.False)
          else (AbsMapBot, AbsBool.Bot)
        val (trueMap, trueB) =
          if (AbsBool.True <= configurable) {
            val newAbsMap = AbsMapFin(this.map - astr, newDefSet)
            (newAbsMap, AbsBool.True)
          } else (AbsMapBot, AbsBool.Bot)
        (falseMap + trueMap, falseB + trueB)
      }
    }
  }

  def delete(astr: AbsString): (AbsMap, AbsBool) = {
    val (domIn, configurable) =
      if (this.map.keySet contains astr) (true, this.map(astr).configurable)
      else (false, AbsBool.Bot)
    val defSetEmpty = DefSetFin(HashSet[String]())

    (this, astr.gamma) match {
      case (AbsMapBot, _) => (AbsMapBot, AbsBool.Bot)
      case (_, conset) if conset.isBottom => (AbsMapBot, AbsBool.Bot)

      case (_, ConFin(strSet)) =>
        strSet.foldLeft((this, AbsBool.Bot))((tpl, str) => {
          val (am, ab) = tpl
          am.delete(str)
        })
      case (_, ConInf()) if !domIn =>
        val newAbsMap = AbsMapFin(this.map, defSetEmpty)
        (newAbsMap, AbsBool.Top)
      case (_, ConInf()) if domIn => {
        val (falseMap, falseB) =
          if (AbsBool.False <= configurable) (this, AbsBool.Top)
          else (AbsMapBot, AbsBool.Bot)
        val (trueMap, trueB) =
          if (AbsBool.True <= configurable) {
            val newAbsMap = AbsMapFin(this.map - astr, defSetEmpty)
            (newAbsMap, AbsBool.Top)
          } else (AbsMapBot, AbsBool.Bot)
        (falseMap + trueMap, falseB + trueB)
      }
    }
  }

  /* has property */
  def contains(str: String): AbsBool = {
    if (this.isBottom) AbsBool.Bot
    else {
      val domIn = this.map.keySet.exists(_.isRelated(str))
      val defsetIn = this.defset contains str
      (domIn, defsetIn) match {
        case (true, true) => AbsBool.True
        case (true, false) => AbsBool.Top
        case (false, true) => AbsBool.Bot // Impossible case
        case (false, false) => AbsBool.False
      }
    }
  }

  def contains(astr: AbsString): AbsBool = {
    if (this.isBottom) AbsBool.Bot
    else {
      val domIn = this.map.keySet.exists(_.isRelated(astr))
      val defsetIn = this.defset contains astr
      (domIn, defsetIn) match {
        case (true, true) => AbsBool.True
        case (true, false) => AbsBool.Top
        case (false, true) => AbsBool.Bot // Impossible case
        case (false, false) => AbsBool.False
      }
    }
  }

  /* other utilities */
  def isBottom: Boolean =
    defset match {
      case DefSetTop => map.isEmpty
      case _ => false
    }

  def isEmpty: Boolean =
    defset match {
      case DefSetFin(s) if s.isEmpty => map.isEmpty
      case _ => false
    }

  def mapValue(f: AbsDataProp => AbsDataProp): AbsMap = {
    val newMap = map.foldLeft(HashMap[AbsString, AbsDataProp]())((tmp, kv) => {
      val (k, v) = kv
      tmp + (k -> f(v))
    })
    AbsMapFin(newMap, defset)
  }

  def abstractKeySet: Set[AbsString] = map.keySet

  def abstractKeySet(filter: (AbsString, AbsDataProp) => Boolean): Set[AbsString] = {
    map.foldLeft(HashSet[AbsString]()) {
      case (set, (key, dp)) => {
        if (filter(key, dp)) set + key
        else set
      }
    }
  }

  def concreteKeySet: ConSet[String] = map.keySet.foldLeft[ConSet[String]](ConFin()) {
    case (ConInf(), _) => ConInf()
    case (ConFin(keyset), astr) => astr.gamma match {
      case ConInf() => ConInf()
      case ConFin(set) => ConFin(keyset ++ set.map(_.str))
    }
  }

  def collectKeySet(prefix: String): ConSet[String] = concreteKeySet match {
    case ConInf() => ConInf()
    case ConFin(set) => ConFin(set.filter(_.startsWith(prefix)))
  }

  def isDefinite(str: AbsString): Boolean = str.gamma match {
    case ConFin(set) if set.forall(defset contains _) => true
    case _ => false
  }

  def keySetPair: (Set[String], AbsString) = map.keySet.foldLeft(
    (HashSet[String](), AbsString.Bot)
  ) {
      case ((strSet, astr), akey) => akey.gamma match {
        case ConInf() => (strSet, astr + akey)
        case ConFin(keySet) => keySet.foldLeft((strSet, astr)) {
          case ((strSet, astr), key) => {
            val isEnum = map(akey).enumerable
            if (AbsBool.True <= isEnum) {
              val isDef = defset contains key
              if (isDef && (AbsBool.Top != isEnum)) (strSet + key, astr)
              else (strSet, astr + AbsString(key))
            } else (strSet, astr)
          }
        }
      }
    }
}
case class AbsMapFin(private val map: Map[AbsString, AbsDataProp], private val defset: DefSet) extends AbsMap(map, defset)
case object AbsMapEmpty extends AbsMap(HashMap[AbsString, AbsDataProp](), DefSet.Empty)
case object AbsMapBot extends AbsMap(HashMap[AbsString, AbsDataProp](), DefSet.Top)
