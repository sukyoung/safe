/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMNode
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLDocument
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.{ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc

object JQueryEvent extends ModelData {
  // TODO: Event Object ??

  private val prop_const: List[(String, AbsProperty)] = List(
    ("proxy",          AbsBuiltinFunc("jQuery.proxy", 2))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("bind",           AbsBuiltinFunc("jQuery.prototype.bind", 3)),
    ("blur",           AbsBuiltinFunc("jQuery.prototype.blur", 2)),
    ("change",         AbsBuiltinFunc("jQuery.prototype.change", 2)),
    ("click",          AbsBuiltinFunc("jQuery.prototype.click", 2)),
    ("dbclick",        AbsBuiltinFunc("jQuery.prototype.dbclick", 2)),
    ("delegate",       AbsBuiltinFunc("jQuery.prototype.delegate", 4)),
    ("die",            AbsBuiltinFunc("jQuery.prototype.die", 2)),
    ("error",          AbsBuiltinFunc("jQuery.prototype.error", 2)),
    ("focus",          AbsBuiltinFunc("jQuery.prototype.focus", 2)),
    ("focusin",        AbsBuiltinFunc("jQuery.prototype.focusin", 2)),
    ("focusout",       AbsBuiltinFunc("jQuery.prototype.focusout", 2)),
    ("hover",          AbsBuiltinFunc("jQuery.prototype.hover", 2)),
    ("keydown",        AbsBuiltinFunc("jQuery.prototype.keydown", 2)),
    ("keypress",       AbsBuiltinFunc("jQuery.prototype.keypress", 2)),
    ("keyup",          AbsBuiltinFunc("jQuery.prototype.keyup", 2)),
    ("live",           AbsBuiltinFunc("jQuery.prototype.live", 3)),
    ("load",           AbsBuiltinFunc("jQuery.prototype.load", 3)),
    ("mousedown",      AbsBuiltinFunc("jQuery.prototype.mousedown", 2)),
    ("mouseenter",     AbsBuiltinFunc("jQuery.prototype.mouseenter", 2)),
    ("mouseleave",     AbsBuiltinFunc("jQuery.prototype.mouseleave", 2)),
    ("mousemove",      AbsBuiltinFunc("jQuery.prototype.mousemove", 2)),
    ("mouseout",       AbsBuiltinFunc("jQuery.prototype.mouseout", 2)),
    ("mouseover",      AbsBuiltinFunc("jQuery.prototype.mouseover", 2)),
    ("mouseup",        AbsBuiltinFunc("jQuery.prototype.mouseup", 2)),
    ("off",            AbsBuiltinFunc("jQuery.prototype.off", 3)),
    ("on",             AbsBuiltinFunc("jQuery.prototype.on", 5)),
    ("one",            AbsBuiltinFunc("jQuery.prototype.one", 4)),
    ("ready",          AbsBuiltinFunc("jQuery.prototype.ready", 1)),
    ("resize",         AbsBuiltinFunc("jQuery.prototype.resize", 2)),
    ("scroll",         AbsBuiltinFunc("jQuery.prototype.scroll", 2)),
    ("select",         AbsBuiltinFunc("jQuery.prototype.select", 2)),
    ("submit",         AbsBuiltinFunc("jQuery.prototype.submit", 2)),
    ("toggle",         AbsBuiltinFunc("jQuery.prototype.toggle", 3)),
    ("trigger",        AbsBuiltinFunc("jQuery.prototype.trigger", 2)),
    ("triggerHandler", AbsBuiltinFunc("jQuery.prototype.triggerHandler", 2)),
    ("unbind",         AbsBuiltinFunc("jQuery.prototype.unbind", 2)),
    ("undelegate",     AbsBuiltinFunc("jQuery.prototype.undelegate", 3)),
    ("unload",         AbsBuiltinFunc("jQuery.prototype.unload", 2))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const), (JQuery.ProtoLoc, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    val m = Map(
      ("jQuery.prototype.on" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          on(h, ctx, he, ctxe, getArgValue(h, ctx, args, "0"),
            getArgValue(h, ctx, args, "1"), getArgValue(h, ctx, args, "2"), getArgValue(h, ctx, args, "3"))
        })),
      ("jQuery.prototype.bind" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* bind( types, data, fn ) == on( types, null, data, fn ) */
          on(h, ctx, he, ctxe, getArgValue(h, ctx, args, "0"),
            Value(NullTop), getArgValue(h, ctx, args, "1"), getArgValue(h, ctx, args, "2"))
        })),
      ("jQuery.prototype.delegate" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* delegate( selector, types, data, fn )
           * == on( types, selector, data, fn ) */
          on(h, ctx, he, ctxe, getArgValue(h, ctx, args, "1"),
            getArgValue(h, ctx, args, "0"), getArgValue(h, ctx, args, "2"), getArgValue(h, ctx, args, "3"))
        })),
      ("jQuery.prototype.one" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* one( types, selector, data, fn )
           * == on( types, selector, data, fn, 1 ), ignore event removal */
          on(h, ctx, he, ctxe, getArgValue(h, ctx, args, "0"),
            getArgValue(h, ctx, args, "1"), getArgValue(h, ctx, args, "2"), getArgValue(h, ctx, args, "3"))
        })),
      ("jQuery.prototype.live" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* live(types, data, fn) == on(types, this.selector, data, fn) */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v_selector = lset_this.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h, l, AbsString.alpha("selector")))
          on(h, ctx, he, ctxe, getArgValue(h, ctx, args, "0"),
            v_selector, getArgValue(h, ctx, args, "1"), getArgValue(h, ctx, args, "2"))
        })),
      ("jQuery.prototype.ready" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_handler = getArgValue(h, ctx, args, "0")._2.filter(l => BoolTrue <= Helper.IsCallable(h,l))
          val lset_target = lset_this.foldLeft(LocSetBot)((lset, l) =>
            lset ++ h(l)(NumStr)._1._1._2
          )
          if (!lset_handler.isEmpty) {
            val h1 = if(!lset_target.isEmpty) JQueryHelper.addJQueryEvent(h, Value(lset_target), AbsString.alpha("DOMContentLoaded"), Value(lset_handler), ValueBot, ValueBot)
                     else JQueryHelper.addJQueryEvent(h, Value(lset_target), AbsString.alpha("DOMContentLoaded"), Value(HTMLDocument.GlobalDocumentLoc), ValueBot, ValueBot)
            ((Helper.ReturnStore(h1, Value(lset_this)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.hover" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_handler = (getArgValue(h, ctx, args, "0") + getArgValue(h, ctx, args, "1"))._2.filter(l => BoolTrue <= Helper.IsCallable(h,l))
          val lset_target = lset_this.foldLeft(LocSetBot)((lset, l) =>
            lset ++ h(l)(NumStr)._1._1._2
          )
          if (!lset_target.isEmpty && !lset_target.isEmpty) {
            val h1 = JQueryHelper.addJQueryEvent(h, Value(lset_target), AbsString.alpha("mousemove"), Value(lset_handler), ValueBot, ValueBot)
            ((Helper.ReturnStore(h1, Value(lset_this)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.toggle" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v_arg1 = getArgValue(h, ctx, args, "0")
          val v_arg2 = getArgValue(h, ctx, args, "1")
          val v_arg3 = getArgValue(h, ctx, args, "2")
          val lset_handler = (v_arg1 + v_arg2 + v_arg3)._2.filter(l => BoolTrue <= Helper.IsCallable(h,l))
          val lset_target = lset_this.foldLeft(LocSetBot)((lset, l) =>
            lset ++ h(l)(NumStr)._1._1._2
          )
          val h1 =
            if (!lset_target.isEmpty && !lset_target.isEmpty)
              JQueryHelper.addJQueryEvent(h, Value(lset_target), AbsString.alpha("click"), Value(lset_handler), ValueBot, ValueBot)
            else
              HeapBot

          val h2 =
            // toggle effect
            if (v_arg1._1._3 </ BoolBot || v_arg1._1._4 </ NumBot || v_arg1._1._5 </ StrBot)
              h // to nothing
            else
              HeapBot
          val h3 = h1 + h2
          if (!(h3 <= HeapBot))
            ((Helper.ReturnStore(h3, Value(lset_this)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.trigger" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* do nothing */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val arg1 = getArgValue(h, ctx, args, "0")
          if (arg1 </ ValueBot)
            ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.triggerHandler" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* do nothing */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val arg1 = getArgValue(h, ctx, args, "0")
          if (arg1 </ ValueBot)
            ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.off" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* do nothing */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val arg1 = getArgValue(h, ctx, args, "0")
          if (arg1 </ ValueBot)
            ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.unbind" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* do nothing */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
        })),
      ("jQuery.prototype.undelegate" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* do nothing */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
        })),
      ("jQuery.prototype.die" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* do nothing */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
       }))
    )
    List("blur", "change", "click", "dbclick", "error", "focus", "focusin", "focusout",
      "keydown", "keypress", "keyup", "load", "mousedown", "mouseenter", "mouseleave", "mousemove",
      "mouseout", "mouseover", "mouseup", "resize", "scroll", "select", "submit", "unload").foldLeft(m)((_m, name) =>
      _m + ("jQuery.prototype." + name -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          JQueryHelper.addEvent(h, ctx, he, ctxe, getArgValue(h, ctx, args, "0"), getArgValue(h, ctx, args, "1"), name)
        }))
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
  private def on( h: Heap, ctx: Context, he: Heap, ctxe: Context,
                  v_types: Value, v_selector: Value, v_data: Value, v_fn: Value): ((Heap, Context), (Heap, Context)) = {
    val lset_this = h(SinglePureLocalLoc)("@this")._2._2
    /* arguments */
    val v_events = v_types
    val s_type = v_events._1._5
    val lset_events = v_events._2
    val h1 =
      if (s_type </ StrBot) {
        val (v_sel1, v_fun1, v_data1) =
          if ((v_data._1._1 </ UndefBot || v_data._1._2 </ NullBot)
            && (v_fn._1._1 </ UndefBot || v_fn._1._2 </ NullBot)) {
            (Value(UndefBot), Value(v_selector._2), Value(UndefBot))
          } else
            (ValueBot, ValueBot, ValueBot)
        val (v_sel2, v_fun2, v_data2) =
          if ((v_data._1._1 <= UndefBot || v_data._1._2 <= NullBot)
            && (v_fn._1._1 </ UndefBot || v_fn._1._2 </ NullBot)
            && (v_selector._1._5 </ StrBot)) {
            (Value(v_selector._1._5), Value(v_data._2), Value(UndefBot))
          } else
            (ValueBot, ValueBot, ValueBot)
        val (v_sel3, v_fun3, v_data3) =
          if ((v_data._1._1 </ UndefBot || v_data._1._2 </ NullBot)
            && (v_fn._1._1 </ UndefBot || v_fn._1._2 </ NullBot)
            && (v_selector._1._5 <= StrBot)) {
            (Value(UndefBot), ValueBot, v_selector)
          } else
            (ValueBot, Value(v_data._2), ValueBot)
        val v_sel = v_sel1 + v_sel2 + v_sel3
        val v_fun = v_fun1 + v_fun2 + v_fun3
        val v_dat = v_data1 + v_data2 + v_data3
        val lset_target = lset_this.foldLeft(LocSetBot)((lset, l) =>
          lset ++ h(l)(NumStr)._1._1._2
        )
        if (v_fun </ ValueBot) {
          JQueryHelper.addJQueryEvent(h, Value(lset_target), s_type, v_fun, v_dat, v_sel)
        } else
          HeapBot
      } else
        HeapBot
    val h2 =
      if (!lset_events.isEmpty) {
        val (v_sel, v_dat) =
          if (v_selector._1._5 <= StrBot) {
            (Value(UndefBot), v_selector)
          } else
            (ValueBot,  ValueBot)
        val lset_target = lset_this.foldLeft(LocSetBot)((lset, l) =>
          lset ++ h(l)(NumStr)._1._1._2
        )
        lset_events.foldLeft(HeapBot)((_h, l) => {
          h(l).getProps.foldLeft(_h)((__h, prop) =>
            __h + JQueryHelper.addJQueryEvent(h, Value(lset_target), s_type, h(l)(prop)._1._1, v_dat, v_sel)
          )
        })
        HeapBot
      } else
        HeapBot
    val h3 = h1 + h2
    if (!(h3 <= HeapBot))
      ((Helper.ReturnStore(h3, Value(lset_this)), ctx), (he, ctxe))
    else
      ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
  }
}
