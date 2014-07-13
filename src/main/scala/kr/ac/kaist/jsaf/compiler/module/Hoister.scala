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

object Hoister extends Walker {
  var l: List[Id] = Nil

  def doit(node: Any): List[Id] = {
    l = Nil
    walk(node).asInstanceOf[List[Id]]
    l
  }

  override def walk(node: Any): Any = node match {
    case SFunDecl(_, SFunctional(_, _, _, name, _), _) =>
      l ::= name
      node
    case SVarDecl(_, name, _, _) =>
      l ::= name
      node
    case SSourceElements(_, stmts, _) => walk(stmts)
    case xs: List[_] => xs.map(walk _)
    case _: Stmt => super.walk(node)
    case _ => node
  }
}
