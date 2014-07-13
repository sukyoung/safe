/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.scala_src.useful

trait WorkTrait {
  var workThreadContext: Any = null

  def doit(): Unit
}
