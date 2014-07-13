/*******************************************************************************
    Copyright 2009,2010, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.scala_src.useful

import _root_.junit.framework.TestCase
import _root_.edu.rice.cs.plt.tuple.{Pair => JPair}

object Pairs {
  def toPair[S, T](jpair: JPair[S, T]): (S, T) = (jpair.first, jpair.second)

  def toJavaPair[S, T](pair: (S, T)): JPair[S, T] = JPair.make(pair._1, pair._2)

  def mapSome[A,B](pair: (A, B)): (Option[A], Option[B]) =
    (Some(pair._1), Some(pair._2))

  /** Return the iterator of all (i, j)-indexed pairs in xs where i < j. */
  def distinctPairsFrom[A](xs: Iterator[A]): Iterator[(A, A)] =
    for ((x, i) <- xs.zipWithIndex ;
         (y, j) <- xs.zipWithIndex ;
         if i < j)
      yield (x, y)

  /** Like the other overloading but stays within the Iterable. */
  def distinctPairsFrom[A](xs: Iterable[A]): Iterable[(A, A)] =
    for ((x, i) <- xs.zipWithIndex ;
         (y, j) <- xs.zipWithIndex ;
         if i < j)
      yield (x, y)
}


class PairTest extends TestCase {
  def testPair() = {
    val pair = Pairs.toPair(JPair.make(1, 2))
    assert(pair._1 == 1)
    assert(pair._2 == 2)
    pair match {
      case (1, 2) => assert(true)
      case _ => assert(false)
    }
  }
}
