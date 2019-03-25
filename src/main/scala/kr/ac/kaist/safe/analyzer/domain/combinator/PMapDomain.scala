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

import kr.ac.kaist.safe.LINE_SEP

// partial map abstract domain
case class PMapDomain[K, V, VD <: AbsDomain[V]](
    AbsV: VD
) extends AbsDomain[Map[K, V]] {
  val AbsVOpt = OptionDomain[V, AbsV.type](AbsV)
  type AbsV = AbsV.Elem
  type AbsVOpt = AbsVOpt.Elem

  // abstraction function
  def alpha(map: Map[K, V]): Elem = {
    val m = map.map { case (k, v) => k -> AbsVOpt(Some(v)) }
    val d = AbsVOpt(None)
    Elem(m, d)
  }

  lazy val Bot: Elem = Elem(Map(), AbsVOpt.Bot)
  lazy val Top: Elem = Elem(Map(), AbsVOpt.Top)
  lazy val Empty: Elem = Elem(Map(), AbsVOpt(None))

  def apply(map: Map[K, AbsVOpt], default: AbsVOpt): Elem = Elem(map, default)

  // pair abstract element
  case class Elem(map: Map[K, AbsVOpt], default: AbsVOpt) extends ElemTrait {
    ////////////////////////////////////////////////////////////////////////////
    // Domain member functions
    ////////////////////////////////////////////////////////////////////////////
    // partial order
    def ⊑(that: Elem): Boolean = {
      val mapB = this.map.compareOptionWithPartialOrder(that.map) {
        case (v1, v2) => v1.getOrElse(this.default) ⊑ v2.getOrElse(that.default)
      }
      val defaultB = this.default ⊑ that.default
      mapB && defaultB
    }

    // join operator
    def ⊔(that: Elem): Elem = {
      val newMap = this.map.mergeWithIdem(that.map) {
        case (v1, v2) => v1.getOrElse(this.default) ⊔ v2.getOrElse(that.default)
      }
      val newDefault = this.default ⊔ that.default
      Elem(newMap, newDefault)
    }

    // meet operator
    def ⊓(that: Elem): Elem = {
      val newMap = this.map.mergeWithIdem(that.map) {
        case (v1, v2) => v1.getOrElse(this.default) ⊓ v2.getOrElse(that.default)
      }
      val newDefault = this.default ⊓ that.default
      Elem(newMap, newDefault)
    }

    ////////////////////////////////////////////////////////////////////////////
    // AbsDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    def gamma: ConSet[Map[K, V]] = ConInf // TODO

    def getSingle: ConSingle[Map[K, V]] = ConMany // TODO

    override def toString: String = {
      if (this == Bot) "⊥"
      else if (this == Top) "⊤"
      else {
        val sortedMap = map.toSeq.sortBy {
          case (key, _) => key.toString
        }
        val s = new StringBuilder
        def arrow(absent: AbsAbsent): String = {
          if (absent.isBottom) "-!>"
          else "-?>"
        }
        sortedMap.foreach {
          case (k, vopt) => s.append(s"$k ${arrow(vopt.absent)} ${vopt.value}").append(LINE_SEP)
        }
        s.append(s"DEFAULT: $default").append(LINE_SEP)
        s.toString
      }
    }

    ////////////////////////////////////////////////////////////////////////////
    // PMapDomain member functions
    ////////////////////////////////////////////////////////////////////////////
    // lookup
    def apply(k: K): AbsVOpt = map.getOrElse(k, default)

    // map for values
    def mapValues(
      f: AbsVOpt => AbsVOpt,
      filter: (K, AbsVOpt) => Boolean = (_, _) => true
    ): Elem = {
      val default = f(this.default)
      val map = this.map.foldLeft[Map[K, AbsVOpt]](Map()) {
        case (map, (key, value)) =>
          val res =
            if (filter(key, value)) f(value)
            else value
          if (res == default) map
          else map + (key -> res)
      }
      map.keySet.foreach(_ => {})
      Elem(map, default)
    }
    def mapCValues(
      f: AbsV => AbsV,
      filter: (K, AbsVOpt) => Boolean = (_, _) => true
    ): Elem = mapValues(vopt => AbsVOpt(f(vopt.value), vopt.absent), filter)

    // update
    def update(k: K, v: AbsVOpt): Elem = {
      val m =
        if (v == default) map - k
        else map + (k -> v)
      map.keySet.foreach(_ => {})
      copy(map = m)
    }

    // contain check
    def contains(f: AbsV => Boolean)(k: K): AbsBool = this(k).exists(f)
  }
}
