/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util.useful

import _root_.java.util.{ HashSet => JHashSet }
import _root_.java.util.{ Set => JavaSet }
import scala.collection.JavaConversions

object Sets {

  /** Takes in any kind of collection. */
  def toJavaSet[T](elts: Iterable[T]): JavaSet[T] = {
    val temp = new JHashSet[T]()
    elts.foreach(temp.add)
    temp
  }

  /** Creates an immutable set. */
  def toSet[T](jset: JavaSet[T]): Set[T] =
    Set(JavaConversions.asScalaSet(jset).toSeq: _*)

}
