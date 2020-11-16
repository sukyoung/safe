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
        Model.parseDir(NodeUtil.jsModelsBase)
      }
      model.funcs.foreach {
        case (_, func) => cfg.addJSModel(func)
      }

      val heap = model.heap
      heap.map.foreach {
        case (loc, obj) =>
          def add(iname: IName, isCall: Boolean): Unit = obj.imap.get(iname) match {
            case Some(FId(fid)) =>
              val str = loc.toString
              val name = str.substring(1, str.indexOf(':'))
              fidToName += fid -> FidNameCase(isCall, name)
            case _ =>
          }
          add(ICall, true)
          add(IConstruct, false)
      }
      AbsHeap(heap)
    }

    // set global CFG
    globalCFG = cfg

    AbsState(modeledHeap, initCtx, AllocLocSet.Empty)
  }

  def addSnapshot(st: AbsState, snapshot: String): AbsState = {
    val concreteHeap = Heap.parse(snapshot)
    val abstractHeap = AbsHeap.alpha(concreteHeap)
    st.copy(heap = st.heap ⊔ abstractHeap)
  }
}
