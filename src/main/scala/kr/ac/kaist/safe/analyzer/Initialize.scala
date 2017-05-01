/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
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
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.phase._
import scala.collection.immutable.{ HashMap, HashSet }

object Initialize {
  def apply(cfg: CFG, jsModel: Boolean): AbsState = {
    val globalLocSet = AbsLoc(BuiltinGlobal.loc)
    val globalPureLocalEnv = AbsLexEnv.newPureLocal(globalLocSet)
    val initHeap = AbsHeap(HashMap(
      BuiltinGlobal.loc -> AbsObject.Bot
    // TODO If delete, not working because not allowed update to bottom heap
    ), HashSet[Concrete]())

    val initCtx = AbsContext(HashMap[Loc, AbsLexEnv](
      PredAllocSite.GLOBAL_ENV -> AbsLexEnv(AbsGlobalEnvRec.Top),
      PredAllocSite.PURE_LOCAL -> globalPureLocalEnv,
      PredAllocSite.COLLAPSED -> AbsLexEnv(AbsDecEnvRec.Empty)
    ), HashSet[Concrete](), OldASiteSet.Empty, globalLocSet)

    val modeledHeap: AbsHeap =
      if (jsModel) {
        val model = Analyze.jscache getOrElse {
          // val fileName = NodeUtil.jsModelsBase + "snapshot_and_built_in.jsmodel"
          // ModelParser.parseFile(fileName).get
          ModelParser.mergeJsModels(NodeUtil.jsModelsBase)
        }
        model.funcs.foreach {
          case (_, func) => cfg.addJSModel(func)
        }
        AbsHeap(model.heap)
      } else BuiltinGlobal.initHeap(initHeap, cfg)

    AbsState(modeledHeap, initCtx)
  }

  def addSnapshot(st: AbsState, snapshot: String): AbsState = {
    val concreteHeap = Heap.parse(snapshot)
    val abstractHeap = AbsHeap.alpha(concreteHeap)
    AbsState(st.heap + abstractHeap, st.context)
  }
}

