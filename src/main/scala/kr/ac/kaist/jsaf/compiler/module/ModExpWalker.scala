/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler.module

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, _}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.compiler.module.{ModHelper => MH}
import java.util.HashMap

class ModExpWalker(env: Env, var path: Path, program: Any) extends Walker {
  def doit(): List[SourceElement] = {
    walk(program).asInstanceOf[List[SourceElement]]
  }

  def export(info: ASTSpanInfo, name: String, getset: String, ftn: Functional): Stmt = {
    val s1 = SVarStmt(info, List(SVarDecl(info, SId(info, "<>desc", None, false), Some(SFunApp(info, SDot(info, SVarRef(info, SId(info, "<>Object", None, false)), SId(info, "getOwnPropertyDescriptor", None, false)), List(SVarRef(info, SId(info, "<>this", None, false)), SStringLiteral(info, "\"", name)))), false)))
    val ss = SExprStmt(info, SFunApp(info, SVarRef(info, SId(info, "_<>_print", None, false)), List(SDot(info, SVarRef(info, SId(info, "<>desc", None, false)), SId(info, "get", None, false)))), false)
    val s2 = SExprStmt(info, SPrefixOpApp(info, SOp(info, "delete"), SDot(info, SVarRef(info, SId(info, "<>this", None, false)), SId(info, name, None, false))), false)
    val s3 = SIf(info, SInfixOpApp(info, SPrefixOpApp(info, SOp(info, "typeof"), SVarRef(info, SId(info, "<>desc", None, false))), SOp(info, "=="), SStringLiteral(info, "\"", "undefined")), SExprStmt(info, SAssignOpApp(info, SVarRef(info, SId(info, "<>desc", None, false)), SOp(info, "="), SObjectExpr(info, List(SField(info, SPropId(info, SId(info, "configurable", None, false)), SBool(info, true))))), false), None)
    val s4 = SExprStmt(info, SAssignOpApp(info, SDot(info, SVarRef(info, SId(info, "<>desc", None, false)), SId(info, getset, None, false)), SOp(info, "="), SFunExpr(info, ftn)), false)
    val s5 = SExprStmt(info, SFunApp(info, SDot(info, SVarRef(info, SId(info, "<>Object", None, false)), SId(info, "defineProperty", None, false)), List(SVarRef(info, SId(info, "<>this", None, false)), SStringLiteral(info, "\"", name), SVarRef(info, SId(info, "<>desc", None, false)))), false)
    SExprStmt(info, SFunApp(info, SFunExpr(info, SFunctional(Nil, Nil, SSourceElements(info, List(s1, s2, s3, s4, s5), false), SId(info, "", None, false), List(SId(info, "<>this", None, false)))), List(SThis(info))), false)
  }
  def export(info: ASTSpanInfo, name: Identifier, alias: Path): Stmt = {
    val e = MH.lookup(env, path, alias) match {
      case (_, QualIntName(p, x)) => MH.intmod(x :: p)
      case (_, QualExtName(p, x)) => SDot(info, MH.extmod(p), SId(info, x, None, false))
    }
    val ftn = SFunctional(Nil, Nil, SSourceElements(info, List(SReturn(info, Some(e))), false), SId(info, name, None, false), Nil)
    export(info, name, "get", ftn)
  }

  override def walk(node: Any): Any = node match {
    case SProgram(_, STopLevel(_, _, stmts)) => walk(stmts)
    case SSourceElements(_, stmts, _) => walk(stmts)
    case SModExpVarStmt(info, vds) =>
      var l: List[Stmt] = Nil
      for (x <- vds.reverse)
        l ::= export(x.getName.getInfo, x.getName.getText, List(x.getName.getText))
      SBlock(info, l, false)
    case SModExpFunDecl(info, SFunDecl(_, SFunctional(_, _, _, name, _), _)) =>
      export(info, name.getText, List(name.getText))
    case SModExpGetter(info, SGetProp(_, SPropId(_, name), ftn)) =>
      val newFtn = (new ModImpWalker(env, path, List(ftn))).doit.head.asInstanceOf[Functional]
      export(info, name.getText, "get", newFtn)
    case SModExpSetter(info, SSetProp(_, SPropId(_, name), ftn)) =>
      val newFtn = (new ModImpWalker(env, path, List(ftn))).doit.head.asInstanceOf[Functional]
      export(info, name.getText, "set", newFtn)
    case SModExpSpecifiers(info, xs) => SBlock(info, walk(xs).asInstanceOf[List[Stmt]], false)
    case SModExpStarFromPath(info, SPath(_, names)) =>
      // TODO: Document
      var l: List[Stmt] = Nil
      val p: Path = names.map(x => x.getText).reverse
      ModHelper.lookup(env, path, p) match {
        case (Module, q) =>
          for (QualExtName(p1, x) <- env.namesIn(q.x :: q.p))
            l ::= export(info, x, x :: p)
        // TODO: else
      }
      SBlock(info, l, false)
    case SModExpStar(info) =>
      // TODO: Document
      var l: List[Stmt] = Nil
      for (QualIntName(p, x) <- env.namesIn(path))
        l ::= export(info, x, List(x))
      SBlock(info, l, false)
    case SModExpAlias(info, name, SPath(_, names)) =>
      export(info, name.getText, names.map(_.getText).reverse)
    case SModExpName(info, SPath(_, names)) =>
      export(info, names.last.getText, names.map(_.getText).reverse)
    case xs:List[_] => xs.map(walk _).filter(_ != null)
    case _ => null
  }
}
