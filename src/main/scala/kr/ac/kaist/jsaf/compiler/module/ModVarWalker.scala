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

class ModVarWalker(env: Env, path: Path, program: Any) extends Walker {
  def doit(): List[SourceElement] = {
    var l: List[VarDecl] = Nil
    for (x <- env.namesIn(path) if x.isInstanceOf[QualIntName])
      l ::= SVarDecl(MH.defInfo, SId(MH.defInfo, x.x, None, false), None, false)
    path match {
      case Nil =>
        l ::= SVarDecl(MH.defInfo, SId(MH.defInfo, "<>Object", None, false),
          Some(SVarRef(MH.defInfo, SId(MH.defInfo, "Object", None, false))), false)
        l ::= SVarDecl(MH.defInfo, SId(MH.defInfo, "<>intmod", None, false),
          Some(SObjectExpr(MH.defInfo, Nil)), false)
        l ::= SVarDecl(MH.defInfo, SId(MH.defInfo, "<>extmod", None, false),
          Some(SObjectExpr(MH.defInfo, Nil)), false)
        l ::= SVarDecl(MH.defInfo, SId(MH.defInfo, "<>initfun", None, false),
          Some(SObjectExpr(MH.defInfo, Nil)), false)
        l ::= SVarDecl(MH.defInfo, SId(MH.defInfo, "<>initarg", None, false),
          Some(SObjectExpr(MH.defInfo, Nil)), false)
        List(SVarStmt(MH.defInfo, l))
      case _ =>
        var m: List[Member] = Nil
        for (x <- env.namesIn(path) if x.isInstanceOf[QualIntName]) env.get(x) match {
          case (t: Type, QualIntName(p1, x1)) => if (path equals p1) {
            if (t equals Var) {
              m ::= SGetProp(MH.defInfo,
                SPropId(MH.defInfo, SId(MH.defInfo, x.x, None, false)),
                SFunctional(Nil, Nil,
                  SSourceElements(MH.defInfo,
                  List(SReturn(MH.defInfo, Some(
                    SVarRef(MH.defInfo, SId(MH.defInfo, x1, None, false))))), false),
                  SId(MH.defInfo, x.x, None, false), Nil))
            }
          } else {
            m ::= SGetProp(MH.defInfo,
              SPropId(MH.defInfo, SId(MH.defInfo, x.x, None, false)),
              SFunctional(Nil, Nil,
                SSourceElements(MH.defInfo,
                List(SReturn(MH.defInfo, Some(
                  SDot(MH.defInfo, MH.intmod(p1), SId(MH.defInfo, x1, None, false))))), false),
                SId(MH.defInfo, x.x, None, false), Nil))
          }
          case (t: Type, QualExtName(p1, x1)) =>
            m ::= SGetProp(MH.defInfo,
              SPropId(MH.defInfo, SId(MH.defInfo, x.x, None, false)),
              SFunctional(Nil, Nil,
                SSourceElements(MH.defInfo,
                List(SReturn(MH.defInfo, Some(
                  SDot(MH.defInfo, MH.extmod(p1), SId(MH.defInfo, x1, None, false))))), false),
                SId(MH.defInfo, x.x, None, false), Nil))
        }
        List(SVarStmt(MH.defInfo, l),
          SExprStmt(MH.defInfo, SAssignOpApp(MH.defInfo,
            MH.intmod(path),
            SOp(MH.defInfo, "="),
            SObjectExpr(MH.defInfo, m)), false))
    }
  }
}
