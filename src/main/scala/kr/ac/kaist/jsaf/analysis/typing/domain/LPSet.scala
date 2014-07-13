/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

/**
 * Location Property Set
 *   Loc : #1, #2, ...
 *   Set[String] : Property names
 */
case class LPSet(map: Map[Loc,Set[String]]) {
  def + (pair: (Loc,String)) = {
    LPSet(map.get(pair._1) match {
      case Some(s) => map + (pair._1 -> (s + pair._2))
      case None => map + (pair._1 -> Set(pair._2))
    })
  }

  def ++ (lpset: LPSet) = {
    LPSet(lpset.map.foldLeft(this.map)((m, kv) =>
      m.get(kv._1) match {
        case Some(s) => m + (kv._1 -> (s ++ kv._2))
        case None => m + (kv._1 -> kv._2)
      }))
  }

  def -- (lpset: LPSet) = {
    val new_map =
      lpset.map.foldLeft(this.map)((m, kv) =>
        m.get(kv._1) match {
          case Some(s) => m + (kv._1 -> (s -- kv._2))
          case None => m
        })

    LPSet(new_map.foldLeft(new_map)((m, kv) => if (kv._2.isEmpty) m - kv._1 else m))
  }

  def get(l: Loc) = map.get(l)

  def toSet = {
    map.foldLeft[Set[(Loc,String)]](Set())((S, kv) => {
      S ++ (kv._2.map((n) => (kv._1,n)))
    })
  }

  def isEmpty: Boolean = toSet.isEmpty

  def toLSet: LocSet = {
    LocSetBot ++ map.keySet
  }

  def subsetOf(lpset: LPSet) = {
    if (this.map.keySet.subsetOf(lpset.map.keySet))
      this.map.foldLeft(true)((b, kv) =>
        if (b && kv._2.subsetOf(lpset.map(kv._1))) true
        else false)
    else 
      false
  }

  def ppLoc(loc: Loc): String = {
    val name = locName(loc)
    if (isOldLoc(loc))
      "##" + name
    else
      "#" + name
    /*
    loc._2 match {
      case Recent => "#" + name
      case Old => "##" + name
    }*/
  }

  override def toString() = {
    toSet.foldLeft("")((s, kv) => s + "{"+ppLoc(kv._1)+", "+kv._2+"}, ")
  }
}

object LPSet {
  def apply(pair: (Loc,String)): LPSet = {
    LPSet(HashMap() + (pair._1 -> Set(pair._2)))
  }
  def apply(set: Set[(Loc,String)]): LPSet = {
    LPSet(set.foldLeft[Map[Loc,Set[String]]](HashMap())((m, kv) =>
      m.get(kv._1) match {
        case Some(s) => m + (kv._1 -> (s + kv._2))
        case None => m + (kv._1 -> Set(kv._2))
      }))
  }
}
