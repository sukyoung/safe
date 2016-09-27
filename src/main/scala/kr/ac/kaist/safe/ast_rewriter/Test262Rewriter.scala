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

package kr.ac.kaist.safe.ast_rewriter

import scala.util.Success
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

/* Rewrites a Test262 JavaScript source code for SAFE.
 */
class Test262Rewriter(program: Program) extends ASTWalker {
  lazy val result: Program = Test262RewriteWalker.walk(program)

  def makeId(info: ASTNodeInfo, name: String): Id = Id(info, name, Some(name), false)
  val resultName = "__result"
  def resultId(info: ASTNodeInfo): Id = makeId(info, resultName)
  val expectName = "__expect"
  def expectId(info: ASTNodeInfo): Id = makeId(info, expectName)
  def errLoc(info: ASTNodeInfo, exn: String): VarRef = {
    val exnLoc =
      if (exn.equals("EvalError")) "__EvalErrLoc"
      else if (exn.equals("RangeError")) "__RangeErrLoc"
      else if (exn.equals("ReferenceError")) "__RefErrLoc"
      else if (exn.equals("SyntaxError")) "__SyntaxErrLoc"
      else if (exn.equals("TypeError")) "__TypeErrLoc"
      else if (exn.equals("URIError")) "__URIErrLoc"
      else "__OtherErrLoc"
    VarRef(info, makeId(info, exnLoc))
  }
  def exnId(info: ASTNodeInfo): Id = makeId(info, "e")
  def handle(info: ASTNodeInfo): VarStmt =
    VarStmt(info, List(VarDecl(info, resultId(info), Some(VarRef(info, exnId(info))), false)))
  def falseR(info: ASTNodeInfo): VarStmt =
    VarStmt(info, List(VarDecl(info, expectId(info), Some(Bool(info, false)), false)))
  def trueR(info: ASTNodeInfo): VarStmt =
    VarStmt(info, List(VarDecl(info, expectId(info), Some(Bool(info, true)), false)))

  private object Test262RewriteWalker extends ASTWalker {
    override def walk(node: Stmt): Stmt = node match {
      // assert.sameValue(e1, e2, e3)
      // ==>
      // var __result = e1
      // var __expect = e2
      case s @ ExprStmt(info,
        FunApp(_,
          Dot(_, VarRef(_, Id(_, obj, _, _)), Id(_, member, _, _)),
          List(arg1, arg2, _)),
        _) if obj.equals("assert") && member.equals("sameValue") =>
        val info1 = arg1.info
        val info2 = arg2.info
        val stmt1 = VarStmt(info1, List(VarDecl(info1, resultId(info1), Some(arg1), false)))
        val stmt2 = VarStmt(info2, List(VarDecl(info2, expectId(info2), Some(arg2), false)))
        ABlock(info, List(stmt1, stmt2), false)

      // assert.throws(TypeError, function(){body});
      // ==>
      // var __result;
      // try { body } catch (e) { __result = e; }
      // var __expect = __TypeErrLoc
      case s @ ExprStmt(info,
        FunApp(_,
          Dot(_, VarRef(_, Id(_, obj, _, _)), Id(_, member, _, _)),
          List(VarRef(info1, Id(_, exn, _, _)),
            FunExpr(info2, Functional(_, _, _, SourceElements(_, List(body), _), _, _, _)))),
        _) if obj.equals("assert") && member.equals("throws") =>
        val stmt1 = VarStmt(info, List(VarDecl(info, resultId(info), None, false)))
        val stmt2 = Try(info2, List(body.asInstanceOf[Stmt]), Some(Catch(info2, exnId(info2), List(handle(info2)))), None)
        val stmt3 = VarStmt(info1, List(VarDecl(info, expectId(info), Some(errLoc(info1, exn)), false)))
        ABlock(info, List(stmt1, stmt2, stmt3), false)

      // assert.notSameValue(e1, e2, e3);
      // ==>
      // var __result1 = e1 == e2
      // var __expect1 = false
      case s @ ExprStmt(info,
        FunApp(_,
          Dot(_, VarRef(_, Id(_, obj, _, _)), Id(_, member, _, _)),
          List(arg1, arg2, _)),
        _) if obj.equals("assert") && member.equals("notSameValue") =>
        val info = arg1.info
        val stmt1 =
          VarStmt(info, List(VarDecl(info, resultId(info), Some(InfixOpApp(info, arg1, Op(info, "=="), arg2)), false)))
        ABlock(info, List(stmt1, falseR(info)), false)

      // assert(e1, e2)
      // ==>
      // var __result1 = e1
      // var __expect1 = true
      case s @ ExprStmt(info, FunApp(_, VarRef(_, Id(_, fun, _, _)), List(arg1, _)), _) if fun.equals("assert") =>
        val info1 = arg1.info
        val stmt1 = VarStmt(info1, List(VarDecl(info1, resultId(info1), Some(arg1), false)))
        ABlock(info, List(stmt1, trueR(info)), false)

      // if (e) { $ERROR(message) }
      // ==>
      // var __result1 = e
      // var __expect1 = false
      case s @ If(info, cond, ABlock(_, List(ExprStmt(_, FunApp(_, VarRef(_, Id(_, name, _, _)), _), _)), false), None) if name.equals("$ERROR") =>
        val stmt1 = VarStmt(info, List(VarDecl(info, resultId(info), Some(cond), false)))
        ABlock(info, List(stmt1, falseR(info)), false)

      case _ => super.walk(node)
    }
  }
}
