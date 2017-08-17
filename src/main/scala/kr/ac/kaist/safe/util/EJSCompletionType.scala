/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

// The Completion Specification Type
object EJSCompletionType {
  def isNormal(t: Int): Boolean = t / 10 == 0 // 0x
  def isAbrupt(t: Int): Boolean = t / 10 == 1 // 1x

  val NORMAL = 1
  val ABRUPT = 10
  val BREAK = 11
  val RETURN = 12
  val THROW = 13
}
