/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.{ SafeConfig, CmdCFGBuild }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.model._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.phase._
import scala.collection.immutable.{ HashMap, HashSet }

object Initialize {
  def apply(cfg: CFG): AbsState = {
    val globalLocSet = LocSet(GLOBAL_LOC)
    val globalPureLocalEnv = AbsLexEnv.newPureLocal(globalLocSet)
    val initHeap = AbsHeap(HashMap(
      GLOBAL_LOC -> AbsObj.Bot
    // TODO If delete, not working because not allowed update to bottom heap
    ))

    val initCtx = AbsContext(HashMap[Loc, AbsLexEnv](
      GLOBAL_ENV -> AbsLexEnv(AbsGlobalEnvRec.Top),
      PURE_LOCAL -> globalPureLocalEnv,
      COLLAPSED -> AbsLexEnv(AbsDecEnvRec.Empty)
    ), OldASiteSet.Empty, globalLocSet)

    val modeledHeap: AbsHeap = {
      val model = HeapBuild.jscache getOrElse {
        ModelParser.mergeJsModels(NodeUtil.jsModelsBase)
      }
      model.funcs.foreach {
        case (_, func) => cfg.addJSModel(func)
      }
      AbsHeap(model.heap)
    }

    AbsState(modeledHeap, initCtx)
  }

  def addSnapshot(st: AbsState, snapshot: String): AbsState = {
    val concreteHeap = Heap.parse(snapshot)
    val abstractHeap = AbsHeap.alpha(concreteHeap)
    AbsState(st.heap ⊔ abstractHeap, st.context)
  }
}

