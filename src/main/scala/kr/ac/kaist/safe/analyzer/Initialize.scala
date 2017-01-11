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
import scala.collection.immutable.HashMap

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
        val fileName = NodeUtil.jsModelsBase + "built_in.jsmodel"
        val model = ModelParser.parseFile(fileName, cfg).get
        val heap = model.heap
        val idMap: Map[Int, Int] = model.funMap.foldLeft(HashMap[Int, Int]()) {
          case (map, (fid, pgm)) => {
            val safeConfig = SafeConfig(CmdCFGBuild, silent = true)

            // rewrite AST
            val astRewriteConfig = ASTRewriteConfig()
            val rPgm = ASTRewrite(pgm, safeConfig, astRewriteConfig).get

            // compile
            val compileConfig = CompileConfig()
            val ir = Compile(rPgm, safeConfig, compileConfig).get

            // cfg build
            val cfgBuildConfig = CFGBuildConfig()
            var funCFG = CFGBuild(ir, safeConfig, cfgBuildConfig).get
            var func = funCFG.getFunc(1).get

            // add model to CFG
            cfg.addJSModel(func)

            // address mutation
            // TODO is system address good? how about incremental program address?
            def mutate(addr: Address): SystemAddr = addr match {
              case ProgramAddr(id) =>
                SystemAddr(s"JSModel<${func.id},$addr>")
              case sys: SystemAddr => sys
            }
            func.getAllBlocks.foreach(_.getInsts.foreach {
              case i: CFGAlloc => i.addr = mutate(i.addr)
              case i: CFGAllocArray => i.addr = mutate(i.addr)
              case i: CFGAllocArg => i.addr = mutate(i.addr)
              case i: CFGCallInst => i.addr = mutate(i.addr)
              case i: CFGInternalCall => i.addrOpt = i.addrOpt.map(mutate(_))
              case _ =>
            })
            map + (fid -> func.id)
          }
        }

        // function id mutation
        val result = Heap(heap.map.foldLeft(HashMap[Loc, Object]()) {
          case (map, (loc, obj)) => {
            val newIMap = obj.imap.foldLeft(HashMap[IName, IValue]()) {
              case (map, (iname, FId(id))) => map + (iname -> FId(idMap(id)))
              case (map, (iname, iv)) => map + (iname -> iv)
            }
            val newObj = Object(obj.amap, newIMap)
            map + (loc -> newObj)
          }
        })
        AbsHeap(result)
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
