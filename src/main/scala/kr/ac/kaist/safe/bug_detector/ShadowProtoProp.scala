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

object ShadowProtoProp extends BugDetector {
  val id = 5

  private def shadowProtoProp(inst: CFGNormalInst, obj: CFGExpr, index: CFGExpr): String = {
    val span = inst.span
    val objStr = obj.ir.ast.toString(0);
    s"[$id] $span:$LINE_SEP    [Warning] Writing property $index of $objStr might shadow its prototype."
  }

  override def checkInst(inst: CFGNormalInst, state: AbsState, semantics: Semantics): List[String] = inst match {
    case CFGStore(_, _, obj, index, rhs) => {
      val (ov, _) = semantics.V(obj, state)
      val (iv, _) = semantics.V(index, state)
      val (rv, _) = semantics.V(rhs, state) //TODO: If rv is function only, return List()
      val iStr = TypeConversionHelper.ToString(iv)
      val h = state.heap

      val shadow = ov.locset.foldLeft(false)((flag, objLoc) => flag || {
        //TODO: If own property, skip
        val proto = h.get(objLoc)(IPrototype)
        proto.value.locset.foldLeft(flag)((flag2, protoLoc) => flag2 || {
          val protoVal = h.get(protoLoc)
          AbsBool.True ⊑ protoVal.HasProperty(iStr, h)
        })
      })

      if (shadow)
        List(shadowProtoProp(inst, obj, index))
      else
        List()
    }
    case _ => List()
  }

  def checkExpr(expr: CFGExpr, state: AbsState, semantics: Semantics): List[String] = List()
}
