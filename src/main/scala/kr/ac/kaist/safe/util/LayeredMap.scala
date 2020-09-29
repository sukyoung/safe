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

import scala.collection.immutable.AbstractMap

case class LayeredMap[A, B](
    bottom: HashMap[A, B] = HashMap[A, B](),
    top: HashMap[A, B] = HashMap[A, B](),
    touched: Set[A] = Set[A]()
) extends AbstractMap[A, B] with Map[A, B] {
  type PartialOrder[B1] = (B1, B1) => Boolean
  type OptionPartialOrder[B1] = (Option[B1], Option[B1]) => Boolean

  def +[V1 >: B](kv: (A, V1)): LayeredMap[A, V1] =
    LayeredMap(bottom, top + kv, touched + kv._1)
  def -(key: A): LayeredMap[A, B] =
    LayeredMap(bottom, top - key, touched + key)
  def --(keys: Iterable[A]): LayeredMap[A, B] =
    keys.foldLeft(this)(_ - _)
  def get(key: A): Option[B] =
    if (touched contains key) top.get(key)
    else bottom.get(key)
  def iterator: Iterator[(A, B)] =
    top.iterator ++ (bottom -- touched).iterator

  def compareOptionWithPartialOrder(
    that: LayeredMap[A, B]
  )(order: OptionPartialOrder[B]): Boolean = {
    if (this eq that) true
    else touched.forall(k => order(this.top.get(k), that.top.get(k)))
  }

  def compareWithPartialOrder(
    that: LayeredMap[A, B]
  )(order: PartialOrder[B]): Boolean = {
    if (this eq that) true
    else touched.forall(k => (this.top.get(k), that.top.get(k)) match {
      case (Some(x), Some(y)) => order(x, y)
      case (x, _) => x == None
    })
  }

  def mergeWithIdem(
    that: LayeredMap[A, B]
  )(mergef: (Option[B], Option[B]) => B): LayeredMap[A, B] = {
    if (this eq that) this else {
      val touched = this.touched ++ that.touched
      val top = touched.foldLeft(Map[A, B]()) {
        case (m, k) => m + (k -> mergef(this.top.get(k), that.top.get(k)))
      }
      LayeredMap(bottom, top, touched)
    }
  }

  def unionWithIdem(
    that: LayeredMap[A, B]
  )(mergef: (B, B) => B): LayeredMap[A, B] = {
    if (this eq that) this else {
      val touched = this.touched ++ that.touched
      val top = touched.foldLeft(Map[A, B]()) {
        case (m, k) => (this.top.get(k), that.top.get(k)) match {
          case (Some(x), Some(y)) => m + (k -> mergef(x, y))
          case (Some(x), None) => m + (k -> x)
          case (None, Some(y)) => m + (k -> y)
          case (None, None) => m
        }
      }
      LayeredMap(bottom, top, touched)
    }
  }

  def intersectWithIdem(
    that: LayeredMap[A, B]
  )(mergef: (B, B) => B): LayeredMap[A, B] = {
    if (this eq that) this else {
      val touched = this.touched ++ that.touched
      val top = touched.foldLeft(Map[A, B]()) {
        case (m, k) => (this.top.get(k), that.top.get(k)) match {
          case (Some(x), Some(y)) => m + (k -> mergef(x, y))
          case _ => m
        }
      }
      LayeredMap(bottom, top, touched)
    }
  }

  def valueMap(f: B => B): LayeredMap[A, B] = foldLeft(this) {
    case (m, (k, v)) =>
      val w = f(v)
      if (w != v) m + (k -> w)
      else m
  }
}
