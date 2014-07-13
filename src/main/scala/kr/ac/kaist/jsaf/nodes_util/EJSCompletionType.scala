/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

// The Completion Specification Type
object EJSCompletionType {
  def isNormal(t: Int): Boolean = t / 10 == 0 // 0x
  def isAbrupt(t: Int): Boolean = t / 10 == 1 // 1x

  val NORMAL = 01
  val ABRUPT = 10
    val BREAK = 11
    val RETURN = 12
    val THROW = 13
}
