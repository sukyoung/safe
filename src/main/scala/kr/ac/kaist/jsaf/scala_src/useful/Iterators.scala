/*******************************************************************************
    Copyright 2009,2011, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.scala_src.useful

import _root_.java.util.{Iterator => JIterator}
import _root_.java.lang.{Iterable => JIterable}
import scala.collection.JavaConversions

object Iterators {
  implicit def wrapIterator[T](iter: JIterator[T]): Iterator[T] =
   JavaConversions.asScalaIterator(iter)

  implicit def wrapIterable[T](iter: JIterable[T]): Iterator[T] =
    JavaConversions.asScalaIterator(iter.iterator)
}
