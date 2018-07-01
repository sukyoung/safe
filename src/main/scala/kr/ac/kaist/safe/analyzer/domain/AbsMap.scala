/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet

// abstract map
case class AbsMap[K, V](map: HashMap[K, AbsOpt[V]], default: AbsOpt[V]) {
  // partial order
  def ⊑(order: (V, V) => Boolean)(that: AbsMap[K, V]): Boolean = {
    this.default.⊑(order)(that.default) && (this.map.compareOptionWithPartialOrder(that.map) {
      case (v1, v2) => v1.getOrElse(this.default).⊑(order)(v2.getOrElse(that.default))
    })
  }

  // join
  def ⊔(join: (V, V) => V)(that: AbsMap[K, V]): AbsMap[K, V] = {
    val default = this.default.⊔(join)(that.default)
    val map = this.map.mergeWithIdem(that.map) {
      case (v1, v2) => v1.getOrElse(this.default).⊔(join)(v2.getOrElse(that.default))
    }
    map.keySet.foreach(_ => {})
    AbsMap(map, default)
  }

  // meet
  def ⊓(meet: (V, V) => V)(that: AbsMap[K, V]): AbsMap[K, V] = {
    val default = this.default.⊓(meet)(that.default)
    val map = this.map.mergeWithIdem(that.map) {
      case (v1, v2) => v1.getOrElse(this.default).⊓(meet)(v2.getOrElse(that.default))
    }
    map.keySet.foreach(_ => {})
    AbsMap(map, default)
  }

  // lookup
  def apply(k: K): AbsOpt[V] = map.getOrElse(k, default)

  // map for values
  def mapValues(
    f: AbsOpt[V] => AbsOpt[V],
    filter: (K, AbsOpt[V]) => Boolean = (_, _) => true
  ): AbsMap[K, V] = {
    val default = f(this.default)
    val map = this.map.foldLeft[HashMap[K, AbsOpt[V]]](HashMap()) {
      case (map, (key, value)) =>
        val res =
          if (filter(key, value)) f(value)
          else value
        if (res == default) map
        else map + (key -> res)
    }
    map.keySet.foreach(_ => {})
    AbsMap(map, default)
  }
  def mapCValues(
    f: V => V,
    filter: (K, AbsOpt[V]) => Boolean = (_, _) => true
  ): AbsMap[K, V] = mapValues({
    case AbsOpt(v, a) => AbsOpt(f(v), a)
  }, filter)

  // update
  def update(k: K, v: AbsOpt[V]): AbsMap[K, V] = {
    val m =
      if (v == default) map - k
      else map + (k -> v)
    map.keySet.foreach(_ => {})
    copy(map = m)
  }

  // contain check
  def contains(f: V => Boolean)(k: K): AbsBool = this(k).exists(f)

  override def toString: String = {
    val sortedMap = map.toSeq.sortBy {
      case (key, _) => key.toString
    }
    val s = new StringBuilder
    sortedMap.foreach {
      case (k, AbsOpt(v, a)) => s.append(s"$k ${arrow(a)} $v").append(LINE_SEP)
    }
    s.append(s"DEFAULT: $default").append(LINE_SEP)
    s.toString
  }
  private def arrow(absent: AbsAbsent): String = {
    if (absent.isBottom) "-!>"
    else "-?>"
  }
}

object AbsMap {
  // bottom
  def Bot[K, V](botV: => V): AbsMap[K, V] = AbsMap(HashMap(), AbsOpt.Bot(botV))

  // top
  def Top[K, V](topV: => V): AbsMap[K, V] = AbsMap(HashMap(), AbsOpt.Top(topV))

  // empty
  def Empty[K, V](botV: => V): AbsMap[K, V] = AbsMap(HashMap(), AbsOpt(botV, AbsAbsent.Top))
}
