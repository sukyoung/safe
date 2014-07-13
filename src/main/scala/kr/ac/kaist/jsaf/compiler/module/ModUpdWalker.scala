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

class ModUpdWalker(env: Env, var path: Path, program: Any) extends Walker {
  def doit(): List[SourceElement] = {
    walk(program).asInstanceOf[List[SourceElement]]
  }

  override def walk(node: Any): Any = node match {
    case SProgram(_, STopLevel(_, _, stmts)) => walk(stmts)
    case SSourceElements(_, stmts, _) => walk(stmts)
    case SModDecl(info, name, STopLevel(_, _, stmts)) =>
      val p = name.getText :: path
      val f = SDot(info, MH.initfun(p), SId(info, "call", None, false))
      SExprStmt(info, SFunApp(info, f, List(SThis(info), MH.initarg(p))), false)
    case SModExpVarStmt(info, vds) => SVarStmt(info, vds)
    case _: ModExpFunDecl | _: FunDecl => null
    case _: Stmt => node
    case xs: List[_] =>
      val stmts = (new ModImpWalker(env, path, xs.map(walk _).filter(_ != null))).doit.asInstanceOf[List[SourceElement]]
      val f = SFunExpr(MH.defInfo, SFunctional(Nil, Nil,
                                               SSourceElements(MH.defInfo, stmts, false),
                                               SId(MH.defInfo, "", None, false), List(SId(MH.defInfo, "arguments", None, false))))
      val s1 = SExprStmt(MH.defInfo, SAssignOpApp(MH.defInfo, MH.initfun(path), SOp(MH.defInfo, "="), f), false)
      val s2 = SExprStmt(MH.defInfo, SAssignOpApp(MH.defInfo, MH.initarg(path), SOp(MH.defInfo, "="), MH.bypass), false)
      List(s1, s2)
    case _ => null
  }
}
