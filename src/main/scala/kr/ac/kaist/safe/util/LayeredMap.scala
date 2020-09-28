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

case class LayeredMap[A, +B](
    bottom: Map[A, B],
    top: Map[A, B],
    touched: Set[A]
) extends AbstractMap[A, B] with Map[A, B] {
  def +[V1 >: B](kv: (A, V1)): LayeredMap[A, V1] =
    LayeredMap(bottom, top + kv, touched + kv._1)
  def -(key: A): scala.collection.immutable.Map[A, B] =
    LayeredMap(bottom, top - key, touched + key)
  def get(key: A): Option[B] =
    if (touched contains key) top.get(key)
    else bottom.get(key)
  def iterator: Iterator[(A, B)] =
    top.iterator ++ (bottom -- touched).iterator

  def mergeWithIdem[B1 >: B](
    that: LayeredMap[A, B1]
  )(mergef: (Option[B1], Option[B1]) => B1): LayeredMap[A, B1] = {
    if (this eq that) this else {
      val touched = this.touched ++ that.touched
      val top = touched.foldLeft(Map[A, B1]()) {
        case (m, k) => m + (k -> mergef(this.top.get(k), that.top.get(k)))
      }
      LayeredMap(bottom, top, touched)
    }
  }

  def unionWithIdem[B1 >: B](
    that: LayeredMap[A, B1]
  )(mergef: (B1, B1) => B1): LayeredMap[A, B1] = {
    if (this eq that) this else {
      val touched = this.touched ++ that.touched
      val top = touched.foldLeft(Map[A, B1]()) {
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

  def intersectWithIdem[B1 >: B](
    that: LayeredMap[A, B1]
  )(mergef: (B1, B1) => B1): LayeredMap[A, B1] = {
    if (this eq that) this else {
      val touched = this.touched ++ that.touched
      val top = touched.foldLeft(Map[A, B1]()) {
        case (m, k) => (this.top.get(k), that.top.get(k)) match {
          case (Some(x), Some(y)) => m + (k -> mergef(x, y))
          case _ => m
        }
      }
      LayeredMap(bottom, top, touched)
    }
  }
}

object LayeredMap {
  def apply[A, B](bottom: Map[A, B]): LayeredMap[A, B] =
    LayeredMap(bottom, Map(), Set())
}
