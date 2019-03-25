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

abstract class ASTRewriteWarning(msg: String) extends SafeWarning(msg)

case class ShadowingWarning(span: Span, kind: ShadowingKind, name: String, bySpan: Span, byKind: ShadowingKind) extends ASTRewriteWarning({
  s"$span: ${kind.toString.capitalize} '$name' is shadowed by a $byKind at '$bySpan'."
})

sealed abstract class ShadowingKind(str: String) {
  override def toString: String = str
}
case object ShadowingFunc extends ShadowingKind("function")
case object ShadowingVar extends ShadowingKind("variable")
case object ShadowingParam extends ShadowingKind("parameter")
