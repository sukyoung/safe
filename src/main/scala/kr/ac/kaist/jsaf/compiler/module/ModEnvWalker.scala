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
import kr.ac.kaist.jsaf.compiler.module._
import java.util.HashMap

class ModEnvWalker(program: Program) extends Walker {
  /*
   * Static resolution of module environment
   */
  var env: Env = new Env()
  var path: Path = Nil
  var impPath: Path = Nil
  var export: Boolean = false
  var done: Boolean = false

  def doit(): Env = {
    env = new Env()
    path = Nil
    impPath = Nil
    export = false
    done = false
    while (!done) {
      done = true
      walk(program)
    }
    env
  }

  override def walk(node: Any): Any = node match {
    case SModDecl(_, name, body) =>
      env.put(QualIntName(path, name.getText), (Module, QualIntName(path, name.getText)))
      path = name.getText :: path
      walk(body)
      path = path.tail
      node
    case SModExpVarStmt(_, vds) =>
      export = true
      walk(vds)
      export = false
      node
    case SModExpFunDecl(_, fd) =>
      export = true
      walk(fd)
      export = false
      node
    case SModExpGetter(_, SGetProp(_, SPropId(_, name), _)) =>
      // TODO:
      env.put(QualExtName(path, name.getText), (GetSet, QualExtName(path, name.getText)))
      node
    case SModExpSetter(_, SSetProp(_, SPropId(_, name), _)) =>
      // TODO:
      env.put(QualExtName(path, name.getText), (GetSet, QualExtName(path, name.getText)))
      node
    case SModExpStarFromPath(_, SPath(_, names)) =>
      // TODO: Document
      val value: (Type, QualName) = ModHelper.lookup(env, path, names.map(_.getText).reverse)
      if (value == null) done = false
      else if (value._1 == Module) {
        val p = value._2.x :: value._2.p
        for (QualExtName(p, x) <- env.namesIn(p))
          env.put(QualExtName(path, x), env.get(QualExtName(p, x)))
      }
      // TODO: else
      node
    case SModExpStar(_) =>
      // TODO: Document
      for (QualIntName(p, x) <- env.namesIn(path))
        env.put(QualExtName(path, x), env.get(QualIntName(p, x)))
      node
    case SModExpAlias(_, name, SPath(_, names)) =>
      val value: (Type, QualName) = ModHelper.lookup(env, path, names.map(_.getText).reverse)
      if (value == null) done = false
      else env.put(QualExtName(path, name.getText), value)
      node
    case SModExpName(_, SPath(_, names)) =>
      val value: (Type, QualName) = ModHelper.lookup(env, path, names.map(_.getText).reverse)
      if (value == null) done = false
      else env.put(QualExtName(path, names.last.getText), value)
      node
    case SModImpSpecifierSet(_, imports, SPath(_, names)) =>
      impPath = names.map(_.getText).reverse
      walk(imports)
      impPath = Nil
      node
    case SModImpAliasClause(_, SPath(_, names), alias) =>
      val value: (Type, QualName) = ModHelper.lookup(env, path, names.map(_.getText).reverse)
      if (value == null) done = false
      else if (value._1 == Module) {
        env.put(QualIntName(path, alias.getText), value)
      }
      // TODO: else
      node
    case SModImpAlias(_, name, alias) =>
      val value: (Type, QualName) = ModHelper.lookup(env, path, name.getText :: impPath)
      if (value == null) done = false
      else env.put(QualIntName(path, alias.getText), value)
      node
    case SModImpName(_, name) =>
      val value: (Type, QualName) = ModHelper.lookup(env, path, name.getText :: impPath)
      if (value == null) done = false
      else env.put(QualIntName(path, name.getText), value)
      node
    case SFunctional(_, _, _, name, _) =>
      env.put(QualIntName(path, name.getText), (Var, QualIntName(path, name.getText)))
      if (export) env.put(QualExtName(path, name.getText), (Var, QualIntName(path, name.getText)))
      node
    case SVarDecl(_, name, _, _) =>
      env.put(QualIntName(path, name.getText), (Var, QualIntName(path, name.getText)))
      if (export) env.put(QualExtName(path, name.getText), (Var, QualIntName(path, name.getText)))
      node
    case _: With => node
    case _: Catch => node
    case _: Expr => node
    case _: Comment => node
    case _ => super.walk(node)
  }
}
