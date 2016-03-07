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

package kr.ac.kaist.safe.scala_useful

import collection.mutable._
import java.util.{ List => JList }
import scala.collection.JavaConverters._

object Lists {
  /** Convert any collection to a Java list. */
  def toJavaList[T](elts: Buffer[T]): JList[T] = elts.asJava

  def toList[T](xs: JList[T]): List[T] = xs.asScala.toList

  val JNil = Nil.asJava
}
