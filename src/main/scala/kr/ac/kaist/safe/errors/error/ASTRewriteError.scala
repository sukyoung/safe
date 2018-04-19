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

sealed abstract class ASTRewriteError(msg: String, ast: ASTNode) extends SafeError({
  s"${ast.info.span}: $msg"
})

case class EvalArgSyntaxError(msg: String, fa: FunApp) extends ASTRewriteError({
  s"Calling the eval function with an illegal syntax: '$msg'."
}, fa)

case class BeforeHoisterError(msg: String, ast: ASTNode) extends ASTRewriteError({
  s"$msg before the hoisting phase should not have hoisted declarations."
}, ast)

case class IdNotBoundError(name: String, id: Id) extends ASTRewriteError({
  s"Identifier $name is not bound."
}, id)
case class DataAccPropError(name: String, n: Member) extends ASTRewriteError({
  s"""ObjectLiteral may not have a data property and an accessor property with the same name. "$name""""
}, n)
case class GetPropError(name: String, n: Member) extends ASTRewriteError({
  s"""ObjectLiteral may not have multiple getter properties with the same name. "$name""""
}, n)
case class SetPropError(name: String, n: Member) extends ASTRewriteError({
  s"""ObjectLiteral may not have multiple setter properties with the same name. "$name""""
}, n)
case class OutsideBreakError(br: Break, kind: String) extends ASTRewriteError({
  s"Break occurs outside of $kind."
}, br)
case class OutsideContError(c: Continue, kind: String) extends ASTRewriteError({
  s"Continue occurs outside of $kind."
}, c)
case class OutsideRetrunError(rt: Return) extends ASTRewriteError({
  s"Return occurs outside of a function body."
}, rt)
case class NotReplacedByHoisterError(ast: ASTNode) extends ASTRewriteError({
  s"${ast.getClass.getSimpleName} should be replaced by the hoister."
}, ast)
case class MultipleLabelDeclError(name: String, ls: LabelStmt) extends ASTRewriteError({
  s"Multiple declarations of the label: $name."
}, ls)
case class NoWithObjError(ast: ASTNode) extends ASTRewriteError({
  "Non-empty with-rewriting environment should have at least one with-object name."
}, ast)
