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

import kr.ac.kaist.safe.nodes.NodeInfo
import kr.ac.kaist.safe.util.Span

sealed abstract class SyntaxError(msg: String, span: Option[Span]) extends SafeError({
  val loc = span.map(_ toString).getOrElse("unspecified location")
  s"$loc: $msg"
})

case class ParserError(msg: String) extends SyntaxError({
  s"[Parser Error] $msg"
}, None)

case class AlreadyMergedSourceError(span: Span) extends SyntaxError({
  "Sources are already merged."
}, Some(span))

case class BeforeHoisterError(msg: String, info: NodeInfo) extends SyntaxError({
  s"$msg before the hoisting phase should not have hoisted declarations."
}, Some(info.span))

case class EvalArgSyntaxError(span: Span, str: String) extends SyntaxError({
  s"Calling the eval function with an illegal syntax: '$str'."
}, Some(span))
