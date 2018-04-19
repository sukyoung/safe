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

package kr.ac.kaist.safe.parser

import java.lang.{ Integer => JInteger }
import scala.util.Success
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

/* Rewrites a JavaScript source code
 * using dynamic code generation with string constants
 * to another one without using it.
 * Used by parser/Parser.scala
 */
object DynamicRewriter extends ASTWalker {
  def apply(program: Program): Program = DynRewriteWalker.walk(program)

  private def allConst(args: List[Expr]): Boolean = args.forall(_.isInstanceOf[StringLiteral])
  private def toStr(expr: Expr): String = expr.asInstanceOf[StringLiteral].escaped
  private def split(args: List[Expr], n: ASTNode): (String, String) = args.reverse match {
    case Nil => ("", "")
    case List(body) => ("", toStr(body))
    case body :: List(param) => (toStr(param), toStr(body))
    case body :: last :: front => (front.foldRight(toStr(last))((a, s) => toStr(a) + ", " + s), toStr(body))
  }

  private object DynRewriteWalker extends ASTWalker {
    override def walk(node: Expr): Expr = node match {
      // eval("e")
      // ==>
      // e
      case n @ FunApp(i1, vr @ VarRef(_, Id(_, "eval", _, _)), List(StringLiteral(_, _, expr, _))) =>
        Parser.stringToE((n.fileName,
          (new JInteger(n.line - 1), new JInteger(n.offset - 1)), expr)) match {
          case Success((result, _)) => result
          case _ => n
        }
      case _ => super.walk(node)
    }

    override def walk(node: LHS): LHS = node match {
      // eval("e")
      // ==>
      // e
      case n @ FunApp(i1, vr @ VarRef(_, Id(_, "eval", _, _)), List(StringLiteral(_, _, lhs, _))) =>
        Parser.stringToLHS((n.fileName,
          (new JInteger(n.line - 1), new JInteger(n.offset - 1)), NU.unescape(lhs))) match {
          case Success((result, _)) => result
          case _ => n
        }

      // new Function("x","d",body);
      // ==>
      // function (x,d) body;
      case n @ New(_, FunApp(_, VarRef(_, Id(_, text, _, _)), args)) if allConst(args) && text.equals("Function") =>
        val (params, body) = split(args, n)
        Parser.stringToFnE((n.fileName,
          (new JInteger(n.line - 1), new JInteger(n.offset - 1)),
          "function (" + NU.unescape(params) + ") {" + NU.unescape(body) + "};")) match {
          case Success((result, _)) => result
          case _ => n
        }

      // Function ("return this")
      // ==>
      // function () { return this }
      case n @ FunApp(i1, VarRef(i2, Id(i3, text, a, b)), args) if allConst(args) && text.equals("Function") =>
        walk(New(i1, FunApp(i1, VarRef(i2, Id(i3, text, a, b)), args)))

      // setTimeout("xqzSr()", 1);
      // ==>
      // setTimeout(function(){xqzSr()}, 1);
      case n @ FunApp(i1, vr @ VarRef(_, Id(_, text, _, _)),
        List(StringLiteral(_, _, body, _), no)) if text.equals("setTimeout") || text.equals("setInterval") =>
        Parser.stringToFnE((n.fileName,
          (new JInteger(n.line - 1), new JInteger(n.offset - 1)),
          "function () {" + NU.unescape(body) + "};")) match {
          case Success((fe, _)) => FunApp(i1, vr, List(fe, no))
          case _ => n
        }

      // window.setTimeout("xqzSr()", 1);
      // ==>
      // window.setTimeout(function(){xqzSr()}, 1);
      case n @ FunApp(i1, dot @ Dot(_, obj @ VarRef(_, Id(_, oname, _, _)), Id(_, mname, _, _)),
        List(StringLiteral(_, _, body, _), no)) if oname.equals("window") && (mname.equals("setTimeout") || mname.equals("setInterval")) =>
        Parser.stringToFnE((n.fileName,
          (new JInteger(n.line - 1), new JInteger(n.offset - 1)),
          "function () {" + NU.unescape(body) + "};")) match {
          case Success((fe, _)) => FunApp(i1, dot, List(fe, no))
          case _ => n
        }
      case _ => super.walk(node)
    }
  }
}
