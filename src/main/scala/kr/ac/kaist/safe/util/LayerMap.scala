/**
 * *****************************************************************************
 * Copyright (c) 2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import scala.collection.immutable.{ MapLike, HashMap, HashSet }

class LayerMap[K, +V](
    private val m: Map[K, V],
    private val touched: Set[K],
    private val parent: Option[LayerMap[K, V]],
    private val depth: Long
) extends Map[K, V] with MapLike[K, V, LayerMap[K, V]] {
  // Adds key/value pairs to this map, returning a new map.
  def +[V1 >: V](kv: (K, V1)): LayerMap[K, V1] = {
    val (k, v) = kv
    new LayerMap(m + kv, touched + k, parent, depth)
  }

  // Removes a key from this map, returning a new map.
  def -(key: K): LayerMap[K, V] = new LayerMap(m - key, touched + key, parent, depth)

  // Optionally returns the value associated with a key.
  def get(key: K): Option[V] = m.get(key)

  // Creates a new iterator over all key/value pairs of this map
  def iterator: Iterator[(K, V)] = m.iterator

  // The empty map of the same type as this map
  override def empty: LayerMap[K, V] = LayerMap()

  // The size of this traversable or iterator.
  override def size: Int = m.size

  // Creates a new map obtained by updating this map with a given key/value pair.
  override def updated[V1 >: V](key: K, value: V1): LayerMap[K, V1] = this + (key -> value)

  // Transforms this map by applying a function to every retrieved value.
  def mapValues[V1 >: V](f: V1 => V1): LayerMap[K, V1] = {
    val (newM, newTouched) = ((m: Map[K, V1], touched) /: m) {
      case ((m, t), (k, v)) => {
        val newV = f(v)
        if (v == newV) (m, t)
        else (m + (k -> newV), t + k)
      }
    }
    new LayerMap(newM, newTouched, parent, depth)
  }

  ////////////////////////////////////////////////////////////////
  // helper functions using advantages of layers
  ////////////////////////////////////////////////////////////////
  // Union with a given merge function.
  // XXX condition: forall x and y, if x == y then f(x, y) == x
  def unionWith[V1 >: V](that: LayerMap[K, V1])(f: (V1, V1) => V1): LayerMap[K, V1] = {
    mergeWith(that)((m, k, l, r) => (l, r) match {
      case (Some(lv), Some(rv)) => m + (k -> f(lv, rv))
      case (Some(lv), None) => m + (k -> lv)
      case (None, Some(rv)) => m + (k -> rv)
      case (None, None) => m - k
    })
  }

  // Intersect with a given merge function.
  // XXX condition: forall x and y, if x == y then f(x, y) == x
  def intersectWith[V1 >: V](that: LayerMap[K, V1])(f: (V1, V1) => V1): LayerMap[K, V1] = {
    mergeWith(that)((m, k, l, r) => (l, r) match {
      case (Some(lv), Some(rv)) => m + (k -> f(lv, rv))
      case _ => m - k
    })
  }

  // Do with a given function.
  // XXX condition: forall x and y, if x == y then f(x, y) == true
  def forallWith[V1 >: V, R](
    that: LayerMap[K, V1]
  )(f: (Option[V1], Option[V1]) => Boolean) = {
    val (newTouched, newParent) = findDiff(that)
    newTouched.forall(k => f(this.m.get(k), that.m.get(k)))
  }

  // Introduce a new layer
  def createLayer: LayerMap[K, V] = new LayerMap(m, HashSet(), Some(this), depth + 1)

  // get the original map
  def getMap: Map[K, V] = m

  ////////////////////////////////////////////////////////////////
  // private helper functions
  ////////////////////////////////////////////////////////////////
  // Merge with a given function.
  private def mergeWith[V1 >: V](
    that: LayerMap[K, V1]
  )(f: (Map[K, V1], K, Option[V1], Option[V1]) => Map[K, V1]) = {
    val (newTouched, newParent) = findDiff(that)
    val newM = ((m: Map[K, V1]) /: newTouched) {
      case (m, k) => f(m, k, this.m.get(k), that.m.get(k))
    }
    new LayerMap(newM, newTouched, parent, depth)
  }

  // Optionally find the set of keys different with each other
  // and optionally find the common ancestor.
  private def findDiff[V1 >: V](that: LayerMap[K, V1]): (Set[K], Option[LayerMap[K, V1]]) = {
    def find(
      l: Option[LayerMap[K, V1]],
      r: Option[LayerMap[K, V1]],
      t: Set[K]
    ): (Set[K], Option[LayerMap[K, V1]]) = (l, r) match {
      case (Some(LayerMap(lm, lt, lp, ld)), Some(LayerMap(rm, rt, rp, rd))) => {
        if (ld < rd) find(l, rp, t ++ rt)
        else if (ld > rd) find(lp, r, t ++ lt)
        else if (!(l eq r)) find(lp, rp, t ++ lt ++ rt)
        else (t, l)
      }
      case _ => (t, None)
    }
    find(Some(this), Some(that), HashSet())
  }
}

object LayerMap {
  def apply[K, V](elems: (K, V)*): LayerMap[K, V] = apply(HashMap(elems: _*))

  def apply[K, V](m: Map[K, V]): LayerMap[K, V] = {
    new LayerMap(m, m.keySet, None, 0)
  }

  def unapply[K, V](
    map: LayerMap[K, V]
  ): Option[(Map[K, V], Set[K], Option[LayerMap[K, V]], Long)] = {
    Some(map.m, map.touched, map.parent, map.depth)
  }
}
