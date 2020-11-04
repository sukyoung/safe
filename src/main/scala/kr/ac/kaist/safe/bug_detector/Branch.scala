/**
 * *****************************************************************************
 * Copyright (c) 2016-2020, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.bug_detector

import scala.util.{ Failure, Success, Try }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._

object Branch extends BugDetector {
  val id = 99

  type Map[K, V] = HashMap[K, V]
  val Map = HashMap

  var branch: Map[ASTNode, (Boolean, Boolean)] = Map()

  override def apply(cfg: CFG, semantics: Semantics): Unit = {
    super.apply(cfg, semantics)
    branch.foreach(p => {
      val (ast, cover) = p
      if (cover._1)
        println(s"${ast.span} (${ast.toString(0).replaceAll(LINE_SEP, "")}) ==> True")
      if (cover._2)
        println(s"${ast.span} (${ast.toString(0).replaceAll(LINE_SEP, "")}) ==> False")
    })
    val cnt = branch.size
    println(s"total 2 * $cnt = ${2 * cnt} number of branches")
  }

  override def checkInst(i: CFGNormalInst, state: AbsState, semantics: Semantics): List[String] = i match {
    case i @ CFGAssert(_, _, cond, true) =>
      //<>cond<> is only created by "new Obj(arg, ...)", <>cond1<> is only created by "for in", which we should ignore
      def ignore(cfg: CFGNode): Boolean = cfg match {
        case CFGVarRef(_, _) => cfg.toString().startsWith("<>cond<>") || cfg.toString().startsWith("<>cond1<>")
        case _ => false
      }
      if (ignore(cond))
        return List()

      val (v, _) = semantics.V(cond, state)
      val bv = TypeConversionHelper.ToBoolean(v)
      val ast = cond.ir.ast

      var (t, f) = branch.getOrElse(ast, (false, false))
      if (AbsBool.True ⊑ bv)
        t = true
      if (AbsBool.False ⊑ bv)
        f = true
      branch += (ast -> (t, f))
      List()
    case _ => List()
  }

  def checkExpr(expr: CFGExpr, state: AbsState, semantics: Semantics): List[String] = List()
}
