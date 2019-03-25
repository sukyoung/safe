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

import kr.ac.kaist.safe.nodes.ast._

sealed abstract class TranslateError(msg: String, ast: ASTNode) extends SafeError({
  s"${ast.info.span}: $msg"
})

case class IRIdNotBoundError(name: String, ast: ASTNode) extends TranslateError({
  s"Identifier $name is not bound."
}, ast)
case class NotUniqueIdError(id: Id) extends TranslateError({
  s"Identifiers should have a unique name after the disambiguation phase: ${id.text}."
}, id)
case class NotUniqueLabelError(l: Label) extends TranslateError({
  s"Labels should have a unique name after the disambiguation phase: ${l.id.text}."
}, l)
case class VarDeclNotHaveInitExprError(vd: VarDecl) extends TranslateError({
  "Variable declarations should not have any initialization expressions after the disambiguation phase."
}, vd)
case class InvalidUnAssignOpError(u: UnaryAssignOpApp) extends TranslateError({
  s"Invalid UnaryAssignOpApp operator: ${u.op.text}."
}, u)
case class InvalidInfixOpAppError(infix: InfixOpApp) extends TranslateError({
  s"Infix operator ${infix.op.text} should have at least two arguments."
}, infix)
case class InvalidStringError(str: StringLiteral) extends TranslateError({
  s"Incomplete escape sequence ${str.escaped}."
}, str)
