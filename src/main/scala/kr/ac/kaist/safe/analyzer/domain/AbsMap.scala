/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap, HashSet }
import spray.json._

// abstract map
case class AbsMap[K, V](map: Map[K, AbsOpt[V]], default: AbsOpt[V]) {
  // partial order
  def ⊑(order: (V, V) => Boolean)(that: AbsMap[K, V]): Boolean = {
    val keySet = this.map.keySet ++ that.map.keySet
    this.default.⊑(order)(that.default) && keySet.forall(k => {
      this(k).⊑(order)(that(k))
    })
  }

  // join
  def ⊔(join: (V, V) => V)(that: AbsMap[K, V]): AbsMap[K, V] = {
    val keySet = this.map.keySet ++ that.map.keySet
    val default = this.default.⊔(join)(that.default)
    val map = keySet.foldLeft[Map[K, AbsOpt[V]]](HashMap()) {
      case (map, k) =>
        val opt = this(k).⊔(join)(that(k))
        if (opt == default) map
        else map + (k -> opt)
    }
    AbsMap(map, default)
  }

  // meet
  def ⊓(meet: (V, V) => V)(that: AbsMap[K, V]): AbsMap[K, V] = {
    val keySet = this.map.keySet ++ that.map.keySet
    val default = this.default.⊓(meet)(that.default)
    val map = keySet.foldLeft[Map[K, AbsOpt[V]]](HashMap()) {
      case (map, k) =>
        val opt = this(k).⊓(meet)(that(k))
        if (opt == default) map
        else map + (k -> opt)
    }
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
    val map = this.map.foldLeft[Map[K, AbsOpt[V]]](HashMap()) {
      case (map, (key, value)) =>
        val res =
          if (filter(key, value)) f(value)
          else value
        if (res == default) map
        else map + (key -> res)
    }
    AbsMap(map, default)
  }
  def mapCValues(
    f: V => V,
    filter: (K, AbsOpt[V]) => Boolean = (_, _) => true
  ): AbsMap[K, V] = mapValues({
    case AbsOpt(v, a) => AbsOpt(f(v), a)
  }, filter)

  // update
  def update(k: K, v: AbsOpt[V]): AbsMap[K, V] = copy(
    map =
    if (v == default) map - k
    else map + (k -> v)
  )

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

  def toJson(kf: K => JsValue, vf: V => JsValue): JsValue = JsObject(
    ("map", JsArray(map.toSeq.map {
      case (k, opt) => JsArray(kf(k), opt.toJson(vf))
    }: _*)),
    ("default", default.toJson(vf))
  )
}

object AbsMap {
  // bottom
  def Bot[K, V](botV: => V): AbsMap[K, V] = AbsMap(HashMap(), AbsOpt.Bot(botV))

  // top
  def Top[K, V](topV: => V): AbsMap[K, V] = AbsMap(HashMap(), AbsOpt.Top(topV))

  // empty
  def Empty[K, V](botV: => V): AbsMap[K, V] = AbsMap(HashMap(), AbsOpt(botV, AbsAbsent.Top))

  def fromJson[K, V](k: JsValue => K, v: JsValue => V)(value: JsValue): AbsMap[K, V] = value match {
    case JsObject(m) => (
      m.get("map").map(json2map(_, k, AbsOpt.fromJson(v))),
      m.get("default").map(AbsOpt.fromJson(v))
    ) match {
        case (Some(m), Some(d)) => AbsMap(m, d)
        case _ => throw AbsMapParseError(value)
      }
    case _ => throw AbsMapParseError(value)
  }
}
