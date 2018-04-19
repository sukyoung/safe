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

package kr.ac.kaist.safe.errors.error

sealed abstract class ConsoleError(msg: String) extends SafeError(msg)

case class NoRecencyTag(str: String) extends ConsoleError({
  s"The given prefix '$str' is not recency tag(R, O)."
})

case class NoLoc(str: String) extends ConsoleError({
  s"The given string '$str' is not location format."
})
