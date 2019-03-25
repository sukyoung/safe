/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe

package object util {
  def condApply[T](cond: Boolean, f: T => T)(input: T): T = {
    if (cond) f(input)
    else input
  }

  val Map = HashMap
}
