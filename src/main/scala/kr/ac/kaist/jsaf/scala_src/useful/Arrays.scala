/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.scala_src.useful

import _root_.java.util.{ArrayList => JArrayList}
import _root_.java.util.{List => JList}
import _root_.java.util.{Collection => JCollection}
import _root_.java.util.Random
import _root_.junit.framework.TestCase
import _root_.kr.ac.kaist.jsaf.useful.ArrayBackedList
import scala.collection.JavaConversions

object Arrays {

  def shuffle[T](array: Array[T]): Array[T] = {
    val rnd = new Random
    for (n <- Iterator.range(array.length - 1, 0, -1)) {
      val k = rnd.nextInt(n + 1)
      val t = array(k); array(k) = array(n); array(n) = t
        }
    array
  }
}
