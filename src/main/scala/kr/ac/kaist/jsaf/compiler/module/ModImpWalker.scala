/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
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

class ModImpWalker(var env: Env, var path: Path, program: Any) extends Walker {
  def doit(): List[SourceElement] = {
    walk(program).asInstanceOf[List[SourceElement]]
  }

  def bind(node: Any, bs: List[Identifier]): Any = {
    val oldEnv = env
    env = env.clone
    for (b <- bs)
      env.put(QualIntName(path, b), (Local, QualIntName(Nil, b)))
    val r = super.walk(node)
    env = oldEnv
    r
  }

  override def walk(node: Any): Any = node match {
    case SModExpVarStmt(info, vds) => walk(SVarStmt(info, vds))
    case SModExpFunDecl(info, fd) => walk(fd)
    case _: Module | _: ModImpDecl => null
    case SFunDecl(info, SFunctional(fds, vds, stmts, name, params), strict) =>
      val hoisted = Hoister.doit(stmts).asInstanceOf[List[Id]]
      val names = name :: hoisted ::: params
      val vd = SVarStmt(info, hoisted.map(x => SVarDecl(x.getInfo, x, None, strict)))
      SFunDecl(info, SFunctional(fds, vds,
                                 SSourceElements(info, vd :: bind(stmts, "arguments" :: names.map(_.getText)).asInstanceOf[List[SourceElement]], false),
                                 name, params), strict)
    case SVarStmt(info, vds) =>
      SBlock(info, walk(vds).asInstanceOf[List[Stmt]], false)
    case SForVar(info, vds, cond, action, body) =>
      SBlock(info, List(walk(SVarStmt(info, vds)).asInstanceOf[Stmt], 
        SFor(info, None, cond, action, body)), false)
    case SForVarIn(info, vd, expr, body) =>
      SBlock(info, List(walk(SVarStmt(info, List(vd))).asInstanceOf[Stmt],
        SForIn(info, walk(SVarRef(vd.getInfo, vd.getName)).asInstanceOf[LHS], expr, body)), false)
    case _: With => MH.warnWith
    case SLabelStmt(info, SLabel(_, id), stmt) => bind(node, List(id.getText))
    case SVarDecl(info, name, Some(expr), strict) =>
      SExprStmt(info, SAssignOpApp(info, walk(SVarRef(info, name)).asInstanceOf[LHS], SOp(info, "="), expr), false)
    case SVarDecl(info, name, None, strict) => null
    case SCatch(info, id, body) => bind(node, List(id.getText))
    case SVarRef(info, id) =>
      env.get(QualIntName(path, id.getText)) match {
        case (t, QualIntName(p, x)) => t match{
          case Local =>
            var e: LHS = null
            for (x <- (x :: p).reverse)
              e = if (e == null) SVarRef(info, SId(info, x, None, false))
                else SDot(info, e, SId(info, x, None, false))
            e
          case _ =>
            if (p equals path) SVarRef(info, SId(info, x, None, false))
            else MH.intmod(x :: p)
        }
        case (_, QualExtName(p, x)) =>
          SDot(info, MH.extmod(p), SId(info, x, None, false))
        case _ => node
      }
    case SGetProp(_, _, SFunctional(_, _, stmts, _, _)) =>
      bind(node, "arguments" :: Hoister.doit(stmts).map(_.getText))
    case SSetProp(_, _, SFunctional(_, _, stmts, _, params)) =>
      bind(node, "arguments" :: (Hoister.doit(stmts) ::: params).map(_.getText))
    case xs: List[_] => xs.map(walk _).filter(_ != null)
    case _: Comment => node
    case _ => super.walk(node)
  }
}
