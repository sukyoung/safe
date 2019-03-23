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
import kr.ac.kaist.safe.util.NodeUtil
import kr.ac.kaist.safe.phase._

object Initialize {
  def apply(cfg: CFG): AbsState = {
    val globalLocSet = LocSet(GLOBAL_LOC)
    val globalPureLocalEnv = AbsLexEnv.newPureLocal(globalLocSet)
    val initHeap = AbsHeap.Empty

    val initCtx = AbsContext(Map(
      GLOBAL_ENV -> AbsLexEnv(AbsGlobalEnvRec.Top),
      PURE_LOCAL -> globalPureLocalEnv,
      COLLAPSED -> AbsLexEnv(AbsDecEnvRec.Empty)
    ), LocSet.Bot, globalLocSet)

    val modeledHeap: AbsHeap = {
      val model = HeapBuild.jscache getOrElse {
        ModelParser.mergeJsModels(NodeUtil.jsModelsBase)
      }
      model.funcs.foreach {
        case (_, func) => cfg.addJSModel(func)
      }
      AbsHeap(model.heap)
    }

    AbsState(modeledHeap, initCtx, AllocLocSet.Empty)
  }

  def addSnapshot(st: AbsState, snapshot: String): AbsState = {
    val concreteHeap = Heap.parse(snapshot)
    val abstractHeap = AbsHeap.alpha(concreteHeap)
    st.copy(heap = st.heap ⊔ abstractHeap)
  }
}
