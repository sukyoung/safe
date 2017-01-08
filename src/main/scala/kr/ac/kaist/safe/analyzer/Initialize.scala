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

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import scala.collection.immutable.{ HashMap }

object Initialize {
  def apply(cfg: CFG, jsModel: Boolean): AbsState = {
    val globalLocSet = AbsLoc(BuiltinGlobal.loc)
    val globalPureLocalEnv = AbsLexEnv.newPureLocal(globalLocSet)
    val initHeap = AbsHeap(HashMap(
      SystemLoc("Dummy", Old) -> AbsObject.Bot // TODO If delete, not working because not allowed update to bottom heap
    ))

    val initCtx = AbsContext(HashMap[Loc, AbsLexEnv](
      PredefLoc.GLOBAL_ENV -> AbsLexEnv(AbsGlobalEnvRec.Top),
      PredefLoc.PURE_LOCAL -> globalPureLocalEnv,
      PredefLoc.COLLAPSED -> AbsLexEnv(AbsDecEnvRec.Empty)
    ), OldAddrSet.Empty, globalLocSet)

    val modeledHeap: AbsHeap =
      if (jsModel) {
        val model = ModelParser.parseFile(NodeUtil.jsModelsBase + "built_in.jsmodel").get
        AbsHeap(model.heap)
      } else BuiltinGlobal.initHeap(initHeap, cfg)

    AbsState(modeledHeap, initCtx)
  }

  def addSnapshot(st: AbsState, snapshot: String): AbsState = {
    val concreteHeap = Heap.parse(snapshot)
    val abstractHeap = AbsHeap.alpha(concreteHeap)
    AbsState(st.heap + abstractHeap, st.context)
  }

  def addDOM(st: AbsState, cfg: CFG): AbsState = {
    val globalObj = st.heap.get(BuiltinGlobal.loc)
      .fold(AbsObject.Empty)(obj => obj)

    val domGlobalObj =
      globalObj
        .initializeUpdate("window", AbsDataProp(AbsValue(BuiltinGlobal.loc)))
        .initializeUpdate("document", AbsDataProp(AbsValue(Document.loc)))

    val domHeap = Document
      .initHeap(st.heap, cfg)
      .update(BuiltinGlobal.loc, domGlobalObj)
    AbsState(domHeap, st.context)
  }
}
