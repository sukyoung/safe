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

package kr.ac.kaist.safe.errors.warning

import kr.ac.kaist.safe.util.Span

sealed abstract class ParseWarning(msg: String, span: Span) extends SafeWarning({
  val loc = span.toString
  s"$loc: $msg"
})

case class NoMultipleUseStrict(span: Span) extends ParseWarning({
  "You may not use multiple Use Strict Directives."
}, span)
