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

package kr.ac.kaist.safe.errors.error

import kr.ac.kaist.safe.util.Span

sealed abstract class ParseError(msg: String, spanOpt: Option[Span]) extends SafeError(spanOpt match {
  case Some(span) =>
    val loc = span.toString
    s"$loc: $msg"
  case None =>
    s"$msg"
})

case class NoFileError(cmd: String) extends ParseError({
  s"Need a file to $cmd."
}, None)

case class NotJSFileError(fileName: String) extends ParseError({
  s"Need a JavaScript file instead of $fileName."
}, None)

case class ParserError(msg: String, span: Span) extends ParseError({
  s"[Parser Error] $msg"
}, Some(span))

case class AlreadyMergedSourceError(span: Span) extends ParseError({
  "Sources are already merged."
}, Some(span))
