/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import scala.collection.immutable.HashSet

package object models {
  type SemanticFun = (Semantics, Heap, Context, Heap, Context, ControlPoint, CFG, String, CFGExpr) => ((Heap, Context),(Heap, Context))
  type AccessFun = (Heap, Context, CFG, String, CFGExpr, FunctionId) => (LPSet)

  def notGenericMethod(h: Heap, lset: LocSet, clsName: String): HashSet[Exception] = {
    val exist = lset.exists(l => {
      val v = h(l)("@class")._1._2._1._5
      v != AbsString.alpha(clsName) && v </ StrBot
    })

    if (exist)
      HashSet[Exception](TypeError)
    else
      ExceptionBot
  }
}
