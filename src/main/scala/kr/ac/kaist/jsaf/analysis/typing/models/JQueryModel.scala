/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models

import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models.jquery._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, IRFactory}
import kr.ac.kaist.jsaf.analysis.cfg.CFGTempId
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.cfg.CFGAsyncCall
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object JQueryModel {
  val aysnc_calls : List[String] = DOMModel.async_calls
}

class JQueryModel(cfg: CFG) extends Model(cfg) {

  private val list_data = List[ModelData](JQuery, JQueryAjax, JQueryAttribute, JQueryCallback, JQueryCore,
    JQueryCSS, JQueryData, JQueryDeferred, JQueryEffect, JQueryEvent, JQueryInternal, JQueryManipulation,
    JQueryMiscsllaneous, JQueryProperty, JQueryTraversing, JQueryUtility)

  private var map_fid = Map[FunctionId, String]()
  private var map_semantic = Map[String, SemanticFun]()
  private var map_presemantic =  Map[String, SemanticFun]()
  private var map_def =  Map[String, AccessFun]()
  private var map_use =  Map[String, AccessFun]()


  def initialize(h: Heap): Heap = {
    /* init function map */
    map_semantic = list_data.foldLeft(map_semantic)((m, data) => m ++ data.getSemanticMap())
    map_presemantic = list_data.foldLeft(map_presemantic)((m, data) => m ++ data.getPreSemanticMap())
    map_def = list_data.foldLeft(map_def)((m, data) => m ++ data.getDefMap())
    map_use = list_data.foldLeft(map_use)((m, data) => m ++ data.getUseMap())


    /* init api objects */
    val h_1 = list_data.foldLeft(h)((h1, data) =>
      data.getInitList().foldLeft(h1)((h2, lp) => {
        /* List[(String, PropValue, Option[(Loc, Obj)], Option[FunctionId] */
        val list_props = lp._2.map((x) => prepareForUpdate("jQuery", x._1, x._2))
        /* update api function map */
        list_props.foreach((v) =>
          v._4 match {
            case Some((fid, name)) => {map_fid = map_fid + (fid -> name)}
            case None => Unit
          })
        /* api object */
        val obj = h2.map.get(lp._1) match {
          case Some(o) =>
            list_props.foldLeft(o)((oo, pv) =>
              if (pv._1 == "@default_number" || pv._1 == "@default_other")
                oo.update(pv._1, oo(pv._1)._1 + pv._2, AbsentTop)
              else
                oo.update(pv._1, pv._2))
          case None =>
            list_props.foldLeft(ObjEmpty)((o, pvo) =>
              if (pvo._1 == "@default_number" || pvo._1 == "@default_other")
                o.update(pvo._1, o(pvo._1)._1 + pvo._2, AbsentTop)
              else
                o.update(pvo._1, pvo._2))
        }
        /* added function object to heap if any*/
        val heap = list_props.foldLeft(h2)((h3, pvo) => pvo._3 match {
          case Some((l, o)) => Heap(h3.map.updated(l, o))
          case None => h3
        })

        /* added api obejct to heap */
        Heap(heap.map.updated(lp._1, obj))
      })
    )

    /* initialize event selector table */
    Heap(h_1.map + (EventSelectorTableLoc -> ObjEmpty))
  }

  def addAsyncCall(cfg: CFG, loop_head: Node): (List[Node],List[Node]) = {
    (List(), List())
    /*
    val fid_global = cfg.getGlobalFId
    /* dummy info for EventDispatch instruction */
    val dummy_info = IRFactory.makeInfo(IRFactory.dummySpan("jQueryEvent"))
    /* dummy var for after call */
    val dummy_id = CFGTempId(NU.ignoreName+"#AsyncCall#", PureLocalVar)
    /* add aysnc call */
    JQueryModel.aysnc_calls.foldLeft((List[Node](),List[Node]()))((nodes, ev) => {
      /* event call */
      val event_call = cfg.newBlock(fid_global)
      cfg.addInst(event_call,
        CFGAsyncCall(cfg.newInstId, dummy_info, "jQuery", ev, newProgramAddr(), newProgramAddr(), newProgramAddr()))
      /* event after call */
      val event_after = cfg.newAfterCallBlock(fid_global, dummy_id)
      val event_catch = cfg.newAfterCatchBlock(fid_global)
      cfg.addEdge(loop_head, event_call)
      cfg.addCall(event_call, event_after, event_catch)
      cfg.addEdge(event_after, loop_head)
      (event_after::nodes._1,event_catch::nodes._2)
    })
    */
  }

  def isModelFid(fid: FunctionId) = map_fid.contains(fid)
  def getFIdMap(): Map[FunctionId, String] = map_fid
  def getSemanticMap(): Map[String, SemanticFun] = map_semantic
  def getPreSemanticMap(): Map[String, SemanticFun] = map_presemantic
  def getDefMap(): Map[String, AccessFun] = map_def
  def getUseMap(): Map[String, AccessFun] = map_use

  def asyncSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                    name: String, list_addr: List[Address]): ((Heap, Context), (Heap, Context)) = {
    ((HeapBot, ContextBot),(HeapBot, ContextBot))
  }
  def asyncPreSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                       name: String, list_addr: List[Address]): (Heap, Context) = {
    (HeapBot, ContextBot)
  }
  def asyncDef(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = {
    LPBot
  }
  def asyncUse(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = {
    LPBot
  }
  def asyncCallgraph(h: Heap, inst: CFGInst, map: Map[CFGInst, Set[FunctionId]],
                     name: String, list_addr: List[Address]): Map[CFGInst, Set[FunctionId]] = {
    Map()
  }
}
