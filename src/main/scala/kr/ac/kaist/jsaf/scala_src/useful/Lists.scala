/*******************************************************************************
    Copyright 2008,2011, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.scala_src.useful

import _root_.java.util.{ArrayList => JArrayList}
import _root_.java.util.{List => JList}
import _root_.java.util.{Collection => JCollection}
import _root_.junit.framework.TestCase
import _root_.kr.ac.kaist.jsaf.useful.ArrayBackedList
import scala.collection.JavaConversions

object Lists {

  /** Convert any collection to a Java list. */
  implicit def toJavaList[T](elts: Iterable[T]): JList[T] = {
    val result = new JArrayList[T](elts.size)
    elts.foreach(result.add(_))
    result
  }

  /* Conversion recommended by Martin Odersky, with some type trickery
     that's a bit annoying. */
  def toList[T](xs: JList[T]): List[T] =
    JavaConversions.asScalaBuffer(new ArrayBackedList(xs)).toList
    /* JavaConversions.asBuffer(Useful.list(xs)).toList */
    /* List.fromArray[T]( xs.toArray(List[T]().toArray) ) */

  def toListFromImmutable[T](xs: JList[T]): List[T] =
    JavaConversions.asScalaBuffer(xs).toList
    /* JavaConversions.asBuffer(ArrayBackedList.fromImmutable(xs)).toList */

  def map[S, T](list: JList[S], fun: S => T): JList[T] = toJavaList(toListFromImmutable(list).map(fun))

  /** Append a single element to the end of a list. */
  def snoc[A, B >: A](xs: List[A], x: B): List[B] = xs ++ List(x)

  /** Cons x onto xs if it exists. Otherwise, return xs. */
  def maybeCons[A, B >: A](x: Option[B], xs: List[A]): List[B] = x match {
    case Some(x) => x :: xs
    case None => xs
  }

  /** Append x onto the end of xs if it exists. Otherwise, return xs. */
  def maybeSnoc[A, B >: A](xs: List[A], x: Option[B]): List[B] = x match {
    case Some(x) => snoc(xs, x)
    case None => xs
  }

  /**
   * If b is true, then this list was created with maybeCons; get back the
   * consed head and the tail. If b is false, get back (None, list).
   */
  def maybeUncons[A](b: Boolean, xxs: List[A]): (Option[A], List[A]) =
    if (b) (Some(xxs.head), xxs.tail) else (None, xxs)

  /**
   * If b is true, then this list was created with maybeSnoc; get back the
   * appended last elt and the init-list. If b is false, return (None, list).
   */
  def maybeUnsnoc[A](b: Boolean, xxs: List[A]): (List[A], Option[A]) =
    if (b) (xxs.init, Some(xxs.last)) else (xxs, None)

  /** The maximum element of a list. Throws an error if empty. */
  def max[A <% Ordered[A]](xs: List[A]): A =
    xs.foldLeft[A](xs.head){ (x, y) => if (x > y) x else y }

  /** The minimum element of a list. Throws an error if empty. */
  def min[A <% Ordered[A]](xs: List[A]): A =
    xs.foldLeft[A](xs.head){ (x, y) => if (x < y) x else y }
}

class JavaList[T] {
  def apply(xs: T*) = Lists.toJavaList(xs)

  def unapplySeq(xs: JList[T]) = Some(Lists.toListFromImmutable(xs))
}

class ListsJUTest() extends TestCase {
  def testEmptyToJavaList() = {
    val xs = List[Int]()
    assert(Lists.toJavaList(xs).isEmpty,
      "Empty Scala lists are not mapped to empty Java lists")
  }
}
