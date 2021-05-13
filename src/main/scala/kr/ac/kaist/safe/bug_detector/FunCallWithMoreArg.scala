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

import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._

object FunCallWithMoreArg extends BugDetector {
  val id = 4

  private def funCallWithMoreArg(i: CFGCallInst): String = {
    val span = i.span
    val call = i.ir.ast.toString(0)
    s"[$id] $span:$LINE_SEP    [Warning] Too may arguments are passed at function call: $call."
  }

  override def checkCallInst(inst: CFGCallInst, state: AbsState): List[String] = {
    //TODO: If function is not native
    val (argV, _) = state.lookup(inst.arguments.asInstanceOf[CFGVarRef].id)
    val (funV, _) = state.lookup(inst.fun.asInstanceOf[CFGVarRef].id)
    val h = state.heap

    val argLen = argV.locset.foldLeft(AbsNum.Bot)((len, objLoc) => {
      val argObj = h.get(objLoc)
      len ⊔ argObj("length").value.pvalue.numval
    })
    val funLen = funV.locset.foldLeft(AbsNum.Bot)((len, objLoc) => {
      val funObj = h.get(objLoc)
      len ⊔ funObj("length").value.pvalue.numval
    })

    val moreArgs = funLen < argLen
    if (AT ⊑ moreArgs)
      List(funCallWithMoreArg(inst))
    else
      List()
  }

  def checkExpr(expr: CFGExpr, state: AbsState, semantics: Semantics): List[String] = List()
}
