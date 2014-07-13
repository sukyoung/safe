/*******************************************************************************
    Copyright (c) 2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler

import _root_.java.util.{List => JList}
import _root_.java.lang.{Integer => JInteger}
import kr.ac.kaist.jsaf.exceptions.JSAFError.error
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{ NodeFactory => NF }
import kr.ac.kaist.jsaf.nodes_util.{ NodeUtil => NU }
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.ErrorLog
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.scala_src.useful.Sets._
import kr.ac.kaist.jsaf.useful.HasAt
import kr.ac.kaist.jsaf.useful.Triple

/* Rewrites a JavaScript source code
 * using dynamic code generation with string constants
 * to another one without using it.
 */
object DynamicRewriter extends Walker {

  /* Error handling
   * The signal function collects errors during the AST->IR translation.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg:String, hasAt:HasAt) = errors.signal(msg, hasAt)
  def signal(hasAt:HasAt, msg:String) = errors.signal(msg, hasAt)
  def signal(error: StaticError) = errors.signal(error)
  def getErrors(): JList[StaticError] = toJavaList(errors.errors)

  def doit(program: Program) = walk(program).asInstanceOf[Program]

  def allConst(args: List[Expr]): Boolean = args.forall(_.isInstanceOf[StringLiteral])
  def toStr(expr: Expr): String = expr.asInstanceOf[StringLiteral].getEscaped
  def split(args: List[Expr], n: Node): (String, String) = args.reverse match {
    case Nil => ("", "")
    case List(body) => ("", toStr(body))
    case body::List(param) => (toStr(param), toStr(body))
    case body::last::front => (front.foldRight(toStr(last))((a, s) => toStr(a)+", "+s), toStr(body))
  }

  override def walk(node: Any): Any = node match {
    // new Function("x","d",body);
    // ==>
    // function (x,d) body;
    case n@SNew(_, SFunApp(_, SVarRef(_, SId(_, text, _, _)), args))
         if allConst(args) && text.equals("Function") =>
      val (params, body) = split(args, n)
      Parser.stringToFnE(new Triple(NU.getFileName(n), new JInteger(NU.getLine(n)-1),
                                    "function ("+params+") {"+body+"};")) match {
        case Some(result) => result
        case _ => n
      }
    // Function ("return this")
    // ==>
    // function () { return this }
    case n@SFunApp(i1, SVarRef(i2, SId(i3, text, a, b)), args)
         if allConst(args) && text.equals("Function") =>
      walk(SNew(i1, SFunApp(i1, SVarRef(i2, SId(i3, text, a, b)), args)))
    // setTimeout("xqz_sr()", 1);
    // ==>
    // setTimeout(function(){xqz_sr()}, 1);
    case n@SFunApp(i1, vr@SVarRef(_, SId(_, text, _, _)),
                   List(SStringLiteral(_, _, body), no))
         if text.equals("setTimeout") || text.equals("setInterval") =>
      Parser.stringToFnE(new Triple(NU.getFileName(n), new JInteger(NU.getLine(n)-1),
                                    "function () {"+body+"};")) match {
        case Some(fe) => SFunApp(i1, vr, List(fe, no))
        case _ => n
      }
    // window.setTimeout("xqz_sr()", 1);
    // ==>
    // window.setTimeout(function(){xqz_sr()}, 1);
    case n@SFunApp(i1, dot@SDot(_, obj@SVarRef(_, SId(_, oname, _, _)), SId(_, mname, _, _)),
                   List(SStringLiteral(_, _, body), no))
      if oname.equals("window") && (mname.equals("setTimeout") || mname.equals("setInterval")) =>
      Parser.stringToFnE(new Triple(NU.getFileName(n), new JInteger(NU.getLine(n)-1),
                                    "function () {"+body+"};")) match {
        case Some(fe) => SFunApp(i1, dot, List(fe, no))
        case _ => n
      }

    case xs:List[_] => xs.map(walk)
    case xs:Option[_] => xs.map(walk)
    case xs:Comment => node
    case _ => super.walk(node)
  }
}
