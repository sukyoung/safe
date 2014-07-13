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
import kr.ac.kaist.jsaf.compiler.module._
import java.util.HashMap

object ModHelper {
  val sourceLoc = new SourceLocRats(NU.freshFile("ModuleRewriter"), 0, 0, 0)
  val defSpan = new Span(sourceLoc, sourceLoc)
  val defInfo = NodeFactory.makeSpanInfo(defSpan)

  val helperId = "<>helper"
  val initFunId = "<>initfun"
  val initArgId = "<>initarg"

  val argumentsId = SId(defInfo, "arguments", None, false)
  val argumentsRef = SVarRef(defInfo, argumentsId)
  val paramId = SId(defInfo, "x", None, false)
  val paramRef = SVarRef(defInfo, paramId)
  val bypass = SObjectExpr(defInfo, List(
    SGetProp(defInfo, SPropId(defInfo, argumentsId), SFunctional(Nil, Nil,
      SSourceElements(defInfo, 
                      List(SReturn(defInfo, Some(argumentsRef))), false),
      argumentsId, List(paramId))),
    SSetProp(defInfo, SPropId(defInfo, argumentsId), SFunctional(Nil, Nil,
      SSourceElements(defInfo, 
      List(SExprStmt(defInfo, SAssignOpApp(defInfo, argumentsRef, SOp(defInfo, "="), paramRef), false)), false),
      argumentsId, List(paramId)))))

  val seal = SDot(defInfo, SVarRef(defInfo,
    SId(defInfo, "<>Object", None, false)),
    SId(defInfo, "seal", None, false))

  def intmod(p: Path): LHS = {
    var e: LHS = SVarRef(defInfo, SId(defInfo, "<>intmod", None, false))
    for (x <- p.reverse)
      e = SDot(defInfo, e, SId(defInfo, x, None, false))
    e
  }
  def extmod(p: Path): LHS = {
    var e: LHS = SVarRef(defInfo, SId(defInfo, "<>extmod", None, false))
    var s: String = ""
    for (x <- p.reverse)
      s = s+"/"+x
    SDot(defInfo, e, SId(defInfo, s, None, false))
  }

  def initfun(p: Path): LHS = {
    var e: LHS = SVarRef(defInfo, SId(defInfo, "<>initfun", None, false))
    for (x <- p.reverse)
      e = SDot(defInfo, e, SId(defInfo, x, None, false))
    e
  }
  def initarg(p: Path): LHS = {
    var e: LHS = SVarRef(defInfo, SId(defInfo, "<>initarg", None, false))
    for (x <- p.reverse)
      e = SDot(defInfo, e, SId(defInfo, x, None, false))
    e
  }

  def warnWith(): Null = {
    System.err.println("Warning: with statement should be rewritten by with rewriter.\n  Eliminating with statement.");
    null
  }

  def lookup(env: Env, p0: Path, p1: Path): (Type, QualName) = {
    // TODO: Document
    if (p1.length > 1) {
      env.get(QualIntName(p0, p1.last)) match {
        case (Module, QualIntName(p2, x)) => lookup_(env, x :: p2, p1.init)
        case _ => lookup__(env, p0, p1)
      }
    } else if (p1.length == 1) {
      env.get(QualIntName(p0, p1.last)) match {
        case (t: Type, q: QualName) => (t, q)
        case _ => lookup__(env, p0, p1)
      }
    } else null
  }
  def lookup_(env: Env, p0: Path, p1: Path): (Type, QualName) = {
    // TODO: Document
    if (p1.length > 1) {
      env.get(QualExtName(p0, p1.last)) match {
        case (Module, QualIntName(p2, x)) => lookup_(env, x :: p2, p1.init)
        case _ => null
      }
    } else if (p1.length == 1) {
      env.get(QualExtName(p0, p1.last)) match {
        case (t: Type, q: QualName) => (t, q)
        case _ => null
      }
    } else null
  }
  def lookup__(env: Env, p0: Path, p1: Path): (Type, QualName) = {
    // TODO: Document
    if (p0.length >= 1) lookup(env, p0.tail, p1)
    else null
  }
}
