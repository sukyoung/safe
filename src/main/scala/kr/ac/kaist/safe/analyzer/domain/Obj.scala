/**
 * *****************************************************************************
 * Copyright (c) 2012-2015, S-Core, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

//TODO: Merge ObjMap implementation
//TODO: Handle default values, key values with "@"
class Obj(val map: Map[String, (PropValue, Absent)]) {
  /* partial order */
  def <=(that: Obj): Boolean = {
    if (this.map.isEmpty) true
    else if (that.map.isEmpty) false
    else if (!(this.map.keySet subsetOf that.map.keySet)) false
    else this.map.foldLeft(true)((b, kv) => {
      val (key, thisPVA) = kv
      that.map.get(key) match {
        case None => false
        case Some(thatPVA) =>
          val (thisPV, thisAbsent) = thisPVA
          val (thatPV, thatAbsent) = thatPVA
          b && thisPV <= thatPV && thisAbsent <= thatAbsent
      }
    })
  }

  /* not a partial order */
  def </(that: Obj): Boolean = !(this <= that)

  /* join */
  def +(that: Obj): Obj = {
    val keys = this.map.keySet ++ that.map.keySet
    val newMap = keys.foldLeft(Obj.ObjMapBot)((m, key) => {
      val thisVal = this.map.get(key)
      val thatVal = that.map.get(key)
      (thisVal, thatVal) match {
        case (None, None) => m
        case (None, Some(v)) => m + (key -> v)
        case (Some(v), None) => m + (key -> v)
        case (Some(v1), Some(v2)) =>
          val (propV1, absent1) = v1
          val (propV2, absent2) = v2
          m + (key -> (propV1 + propV2, absent1 + absent2))
      }
    })
    new Obj(newMap)
  }

  /* lookup */
  private def lookup(x: String): (Option[PropValue], Absent) = {
    this.map.get(x) match {
      case Some(pva) =>
        val (propV, absent) = pva
        (Some(propV), absent)
      case None if x.take(1) == "@" => (None, AbsentBot)
      case None if isNum(x) =>
        val (propV, absent) = map(STR_DEFAULT_NUMBER)
        (Some(propV), absent)
      case None if !isNum(x) =>
        val (propV, absent) = map(STR_DEFAULT_OTHER)
        (Some(propV), absent)
    }
  }

  /* meet */
  def <>(that: Obj): Obj = {
    if (this.map eq that.map) this
    else {
      val map1 = that.map.foldLeft(this.map)((m, kv) => {
        val (key, thatPVA) = kv
        val (thatPV, thatAbsent) = thatPVA
        val (thisPVOpt, thisAbsent) = this.lookup(key)
        thisPVOpt match {
          case Some(thisPV) if m.contains(key) => m + (key -> (thisPV <> thatPV, thisAbsent <> thatAbsent))
          case _ => m - key
        }
      })
      val map2 = this.map.foldLeft(map1)((m, kv) => {
        val (key, thisPVA) = kv
        if (that.map.contains(key)) m
        else m - key
      })
      new Obj(map2)
    }
  }

  def isBottom: Boolean = {
    if (this.map.isEmpty) true
    else if ((this.map.keySet diff DEFAULT_KEYSET).nonEmpty) false
    else
      this.map.foldLeft(true)((b, kv) => {
        val (_, pva) = kv
        val (propV, absent) = pva
        b && propV.isBottom && absent.isBottom
      })
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Obj = {
    if (this.map.isEmpty) this
    else {
      val newMap = this.map.foldLeft(Obj.ObjMapBot)((m, kv) => {
        val (key, pva) = kv
        val (propV, absent) = pva
        val newV = propV.objval.value.subsLoc(locR, locO)
        val newOV = ObjectValue(newV, propV.objval.writable, propV.objval.enumerable, propV.objval.configurable)
        val newPropV = PropValue(newOV, propV.funid)
        m + (key -> (newPropV, absent))
      })
      new Obj(newMap)
    }
  }
}

object Obj {
  val ObjMapBot: Map[String, (PropValue, Absent)] = Map[String, (PropValue, Absent)]()

  def apply(m: Map[String, (PropValue, Absent)]): Obj = new Obj(m)
}
