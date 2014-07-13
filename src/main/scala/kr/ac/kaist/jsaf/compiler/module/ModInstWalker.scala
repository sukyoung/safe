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

class ModInstWalker(env: Env, var path: Path, program: Any) extends Walker {
  def doit(): List[SourceElement] = {
    walk(program).asInstanceOf[List[SourceElement]]
  }

  override def walk(node: Any): Any = node match {
    case SProgram(_, STopLevel(_, _, stmts)) => walk(stmts)
    case SSourceElements(_, stmts, _) => walk(stmts)
    case SModDecl(_, name, STopLevel(_, _, stmts)) =>
      // TODO: Arguments
      path = name.getText :: path
      val fds: List[SourceElement] = (new ModFunWalker(env, path, stmts)).doit
      val vds: List[SourceElement] = (new ModVarWalker(env, path, stmts)).doit
      val inst: List[SourceElement] = walk(stmts).asInstanceOf[List[SourceElement]]
      val exp: List[SourceElement] = (new ModExpWalker(env, path, stmts)).doit
      val upd: List[SourceElement] = (new ModUpdWalker(env, path, stmts)).doit
      path = path.tail
      val f: LHS = SFunExpr(MH.defInfo, SFunctional(Nil, Nil,
                                                    SSourceElements(MH.defInfo, fds ::: vds ::: exp ::: upd ::: inst, false),
                                                    SId(MH.defInfo, "", None, false), List(MH.argumentsId)))
      SBlock(MH.defInfo, List(
        SExprStmt(MH.defInfo, SAssignOpApp(MH.defInfo,
          SVarRef(MH.defInfo, SId(MH.defInfo, name.getText, None, false)),
          SOp(MH.defInfo, "="),
          SAssignOpApp(MH.defInfo,
            MH.extmod(name.getText :: path),
            SOp(MH.defInfo, "="),
            SNew(MH.defInfo, SFunApp(MH.defInfo, f, List(MH.bypass))))), false),
        SExprStmt(MH.defInfo, SFunApp(MH.defInfo, MH.seal, List(SVarRef(MH.defInfo, SId(MH.defInfo, name.getText, None, false)))), false)), false)
    case xs:List[_] => xs.map(walk _).filter(_ != null)
    case _ => null
  }
}
