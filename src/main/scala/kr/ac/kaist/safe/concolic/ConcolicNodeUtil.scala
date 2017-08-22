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

package kr.ac.kaist.safe.concolic

object ConcolicNodeUtil {

  val concolicPrefix = "<>Concolic<>"

  def freshConcolicName(n: String): String = concolicPrefix + n

}
