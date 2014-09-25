/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing._

object JQueryAjax extends ModelData {

  val AjaxInsLoc = newSystemLoc("jQueryAjaxInstance", Recent)
  
  private val prop_const: List[(String, AbsProperty)] = List(
    ("ajax",           AbsBuiltinFunc("jQuery.ajax", 2)),
    ("ajaxPrefilter",  AbsBuiltinFunc("jQuery.ajaxPrefilter", 2)),
    ("ajaxSetup",      AbsBuiltinFunc("jQuery.ajaxSetup", 2)),
    ("ajaxTransport",  AbsBuiltinFunc("jQuery.ajaxTransport", 2)),
    ("get",            AbsBuiltinFunc("jQuery.get", 4)),
    ("getJSON",        AbsBuiltinFunc("jQuery.getJSON", 3)),
    ("getScript",      AbsBuiltinFunc("jQuery.getScript", 2)),
    ("param",          AbsBuiltinFunc("jQuery.param", 2)),
    ("post",           AbsBuiltinFunc("jQuery.post", 4))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("ajaxComplete",   AbsBuiltinFunc("jQuery.prototype.ajaxComplete", 1)),
    ("ajaxError",      AbsBuiltinFunc("jQuery.prototype.ajaxError", 1)),
    ("ajaxSend",       AbsBuiltinFunc("jQuery.prototype.ajaxSend", 1)),
    ("ajaxStart",      AbsBuiltinFunc("jQuery.prototype.ajaxStart", 1)),
    ("ajaxStop",       AbsBuiltinFunc("jQuery.prototype.ajaxStop", 1)),
    ("ajaxSuccess",    AbsBuiltinFunc("jQuery.prototype.ajaxSuccess", 1)),
    // event load ("load",           AbsBuiltinFunc("jQuery.prototype.load", 3)),
    ("serialize",      AbsBuiltinFunc("jQuery.prototype.serialize", 0)),
    ("serializeArray", AbsBuiltinFunc("jQuery.prototype.serializeArray", 0))
  )
  
  // ajax instance object
  private val prop_ajax_ins: List[(String, AbsProperty)] = List(
    ("abort", AbsBuiltinFunc("jQueryAjax.abort", 1)),
    ("always", AbsBuiltinFunc("jQueryAjax.always", 0)),
    ("complete", AbsBuiltinFunc("jQueryAjax.complete", 0)),
    ("done", AbsBuiltinFunc("jQueryAjax.done", 0)),
    ("error", AbsBuiltinFunc("jQueryAjax.error", 0)),
    ("fail", AbsBuiltinFunc("jQueryAjax.fail", 0)),
    ("getAllResponseHeaders", AbsBuiltinFunc("jQueryAjax.getAllResponseHeaders", 0)),
    ("getResponseHeader", AbsBuiltinFunc("jQueryAjax.getResponseHeader", 0)),
    ("overrideMimeType", AbsBuiltinFunc("jQueryAjax.overrideMimeType", 1)),
    ("pipe", AbsBuiltinFunc("jQueryAjax.pipe", 0)),
    ("progress", AbsBuiltinFunc("jQueryAjax.progress", 0)),
    ("setRequestHeader", AbsBuiltinFunc("jQueryAjax.setRequestHeader", 2)),
    ("state", AbsBuiltinFunc("jQueryAjax.state", 0)),
    ("statusCode", AbsBuiltinFunc("jQueryAjax.statusCode", 0)),
    ("success", AbsBuiltinFunc("jQueryAjax.success", 0)),
    ("then", AbsBuiltinFunc("jQueryAjax.then", 0))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const), (JQuery.ProtoLoc, prop_proto), (AjaxInsLoc, prop_ajax_ins) 
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "jQuery.ajax" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // rough modeling : simply returns an ajax instance object
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr = (0 until 17).foldLeft[Array[Address]](new Array(0))((a, i) => a :+ cfg.getAPIAddress(addr_env, i))
          val (h_1, ctx_1) = addr.foldLeft((h, ctx))((hc, a) => Helper.Oldify(hc._1, hc._2, a))

          // start here
          val loc: Array[Loc] = addr.map(a => addrToLoc(a, Recent))
          
          val ajax_obj = h_1(AjaxInsLoc)
          // create a new ajax object
          val (h_2, new_ajax_obj) = ajax_obj.getProps.zipWithIndex.foldLeft((h_1, Helper.NewObject(ObjProtoLoc)))((ho, si) => {
            val fun_loc = ajax_obj(si._1)._1._1._2
            val fun_obj = fun_loc.foldLeft(Obj.bottom)((o, l) => o + ho._1(l))
            val _h1 = ho._1.update(loc(si._2 + 1), fun_obj)
            (_h1, ho._2.update(si._1, PropValue(ObjectValue(Value(loc(si._2+1)), T, T, T))))
          })

          val new_ajax_obj2 = new_ajax_obj.update("readyState", PropValue(ObjectValue(Value(UInt), T, T, T))).update(
                                                  "responseText", PropValue(ObjectValue(Value(OtherStr), T, T, T))).update(
                                                  "status",     PropValue(ObjectValue(Value(UInt), T, T, T))).update(
                                                  "statusTextt", PropValue(ObjectValue(Value(OtherStr), T, T, T)))

          val h_3 = h_2.update(loc(0), new_ajax_obj2)

          ((Helper.ReturnStore(h_3, Value(loc(0))), ctx_1), (he, ctxe))
        }),
       // unsound modeling
      "jQuery.getScript" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // warning message
          println("* Warning : 'jQuery.getScript' has been called, but the argument script file is not loaded. The analysis results may be unsound.")
          
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_handler = getArgValue(h, ctx, args, "1")._2.filter(l => BoolTrue <= Helper.IsCallable(h,l))

          
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr = (0 until 17).foldLeft[Array[Address]](new Array(0))((a, i) => a :+ cfg.getAPIAddress(addr_env, i))
          val (h_1, ctx_1) = addr.foldLeft((h, ctx))((hc, a) => Helper.Oldify(hc._1, hc._2, a))

          // start here
          val loc: Array[Loc] = addr.map(a => addrToLoc(a, Recent))
          
          val ajax_obj = h_1(AjaxInsLoc)
          // create a new ajax object
          val (h_2, new_ajax_obj) = ajax_obj.getProps.zipWithIndex.foldLeft((h_1, Helper.NewObject(ObjProtoLoc)))((ho, si) => {
            val fun_loc = ajax_obj(si._1)._1._1._2
            val fun_obj = fun_loc.foldLeft(Obj.bottom)((o, l) => o + ho._1(l))
            val _h1 = ho._1.update(loc(si._2 + 1), fun_obj)
            (_h1, ho._2.update(si._1, PropValue(ObjectValue(Value(loc(si._2+1)), T, T, T))))
          })

          val new_ajax_obj2 = new_ajax_obj.update("readyState", PropValue(ObjectValue(Value(UInt), T, T, T))).update(
                                                  "responseText", PropValue(ObjectValue(Value(OtherStr), T, T, T))).update(
                                                  "status",     PropValue(ObjectValue(Value(UInt), T, T, T))).update(
                                                  "statusTextt", PropValue(ObjectValue(Value(OtherStr), T, T, T)))

          val h_3 = h_2.update(loc(0), new_ajax_obj2)

          val h_4 = JQueryHelper.addJQueryEvent(h_3, Value(loc(0)), AbsString.alpha("ajax"), Value(lset_handler), ValueBot, ValueBot)
          ((Helper.ReturnStore(h_4, Value(loc(0))), ctx_1), (he, ctxe)) 
       })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map()
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map()
  }
}
