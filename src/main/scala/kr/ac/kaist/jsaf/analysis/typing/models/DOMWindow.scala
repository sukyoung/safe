/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5.{Navigator, DOMLocation, History, Storage}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLDocument

// Modeled based on WHATWG Living Standard
// Section 6.2 The Window object 
object DOMWindow extends DOM {

  val loc_proto = ObjProtoLoc
  val WindowLoc = GlobalLoc

  private val prop_window: List[(String, AbsProperty)] = List(
    ("window",        AbsConstValue(PropValue(ObjectValue(GlobalLoc, F, T, T)))),
    ("self",          AbsConstValue(PropValue(ObjectValue(GlobalLoc, F, T, T)))),
    ("document",      AbsConstValue(PropValue(ObjectValue(HTMLDocument.GlobalDocumentLoc, F, T, T)))),
    ("name",          AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    // Location object
    ("location",      AbsConstValue(PropValue(ObjectValue(DOMLocation.getInstance.get, F, T, T)))),
    // History object
    ("history",      AbsConstValue(PropValue(ObjectValue(History.getInstance.get, F, T, T)))),
    ("status",        AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("frames",          AbsConstValue(PropValue(ObjectValue(GlobalLoc, F, T, T)))),
    ("length",        AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    // 6.1.1.1 Navigating nested browing contexts in the DOM
    // Limitation : we do not support nested documents
    ("top",           AbsConstValue(PropValue(ObjectValue(GlobalLoc, F, T, T)))),
    ("opener",        AbsConstValue(PropValue(ObjectValue(NullTop, F, T, T)))),
    ("parent",        AbsConstValue(PropValue(ObjectValue(GlobalLoc, F, T, T)))),
    ("frameElement",        AbsConstValue(PropValue(ObjectValue(NullTop, F, T, T)))),
    // Navigator object
    ("navigator",     AbsConstValue(PropValue(ObjectValue(Navigator.getInstance.get, F, T, T)))),
    ("innerHeight",   AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("innerWidth",    AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("outerHeight",   AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("outerWidth",    AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("pageXOffset",   AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("pageYOffset",   AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("scrollMaxX",    AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("scrollMaxY",    AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("scrollX",       AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("scrollY",       AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("screenX",       AbsConstValue(PropValue(ObjectValue(UInt,T, T, T)))),
    ("screenY",       AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    // WHATWG Living Standard Section 11.2.2 The sessionStorage attribute
    // Window implements WindowSessionStorage
    ("sessionStorage",      AbsConstValue(PropValue(ObjectValue(Storage.loc_ins2, F, T, T)))),
    // WHATWG Living Standard Section 11.2.3 The localStorage attribute
    // Window implements WindowLocalStorage
    ("localStorage",      AbsConstValue(PropValue(ObjectValue(Storage.getInstance.get, F, T, T)))),
    // Window implements GlobalEventHandlers
    ("onerror",      AbsConstValue(PropValue(ObjectValue(NullTop, T, T, T)))),
    ("onload",      AbsConstValue(PropValue(ObjectValue(NullTop, T, T, T)))),
    // non-standard
    ("devicePixelRatio",      AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
    // API
    ("alert",         AbsBuiltinFunc("DOMWindow.alert", 1)),
    ("atob",          AbsBuiltinFunc("DOMWindow.atob", 1)),
    ("back",          AbsBuiltinFunc("DOMWindow.back", 0)),
    ("blur",          AbsBuiltinFunc("DOMWindow.blur", 0)),
    ("btoa",          AbsBuiltinFunc("DOMWindow.btoa", 1)),
    ("clearInterval", AbsBuiltinFunc("DOMWindow.clearInterval", 0)),
    ("clearTimeout",  AbsBuiltinFunc("DOMWindow.clearTimeout", 0)),
    ("close",         AbsBuiltinFunc("DOMWindow.close", 0)),
    ("confirm",       AbsBuiltinFunc("DOMWindow.confirm", 0)),
    ("escape",        AbsBuiltinFunc("DOMWindow.escape", 1)),
    ("focus",         AbsBuiltinFunc("DOMWindow.focus", 0)),
    ("foward",        AbsBuiltinFunc("DOMWindow.forward", 0)),
    ("home",          AbsBuiltinFunc("DOMWindow.home", 0)),
    ("maximize",      AbsBuiltinFunc("DOMWindow.maximize", 0)),
    ("minimize",      AbsBuiltinFunc("DOMWindow.minimize", 0)),
    ("moveBy",        AbsBuiltinFunc("DOMWindow.moveBy", 2)),
    ("moveTo",        AbsBuiltinFunc("DOMWindow.moveTo", 2)),
    ("open",          AbsBuiltinFunc("DOMWindow.open", 1)),
    ("postMessage",   AbsBuiltinFunc("DOMWindow.postMessage", 3)),
    ("print",         AbsBuiltinFunc("DOMWindow.print", 0)),
    ("prompt",        AbsBuiltinFunc("DOMWindow.prompt", 1)),
    ("resizeBy",      AbsBuiltinFunc("DOMWindow.resizeBy", 2)),
    ("resizeTo",      AbsBuiltinFunc("DOMWindow.resizeTo", 2)),
    ("scroll",        AbsBuiltinFunc("DOMWindow.scroll", 2)),
    ("scrollBy",      AbsBuiltinFunc("DOMWindow.scrollBy", 2)),
    ("scrollByLines", AbsBuiltinFunc("DOMWindow.scrollByLines", 1)),
    ("scrollByPages", AbsBuiltinFunc("DOMWindow.scrollByPages", 1)),
    ("scrollTo",      AbsBuiltinFunc("DOMWindow.scrollTo", 2)),
    ("setInterval",   AbsBuiltinFunc("DOMWindow.setInterval", 2)),
    ("setTimeout",    AbsBuiltinFunc("DOMWindow.setTimeout", 2)),
    ("stop",          AbsBuiltinFunc("DOMWindow.stop", 0)),
    ("unescape",      AbsBuiltinFunc("DOMWindow.unescape", 1)),
    // DOM Level 2 Style
    ("getComputedStyle",      AbsBuiltinFunc("DOMWindow.getComputedStyle", 2))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (WindowLoc, prop_window)
  )
  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Date" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        })),
      ("DOMWindow.alert" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      /*
        ("DOMWindow.atob" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.back" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.blur" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.btoa" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
          */
      ("DOMWindow.clearInterval" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_handle = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (n_handle </ NumBot)
          /* unsound semantic */
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMWindow.clearTimeout" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_handle = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (n_handle </ NumBot)
          /* unsound semantic */
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      /*
        ("DOMWindow.close" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.confirm" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
      */
        ("DOMWindow.escape" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            /* arguments */
            val str = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
            if (str </ StrBot){
              val encodedStr = str.getSingle match {
                case Some(v) =>
                  // encoding
                  StrTop
                case None => StrTop
              }
              ((Helper.ReturnStore(h, Value(encodedStr)), ctx), (he, ctxe))
            }
            else
              ((HeapBot, ContextBot), (he, ctxe))

          })),
      /*
        ("DOMWindow.focus" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.forward" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.home" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.maximize" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.minimize" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.moveBy" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.moveTo" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.open" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),*/
        ("DOMWindow.postMessage" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // unsound semantics : should handle exceptions.
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
        /*
        ("DOMWindow.print" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.prompt" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.resizeBy" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.resizeTo" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),*/ 
        ("DOMWindow.scroll" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            /* argument */
            val x_coord = getArgValue(h, ctx, args, "0")
            val y_coord = getArgValue(h, ctx, args, "1")
            if(x_coord </ ValueBot && y_coord </ ValueBot)
              ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
            else
            ((HeapBot, ContextBot), (he, ctxe))
          })),
        /*
        ("DOMWindow.scrollBy" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scrollByLines" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scrollByPages" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scrollTo" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
          */
      ("DOMWindow.setInterval" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          // ignore eval-like feature
          val argv = getArgValue(h, ctx, args, "0")
          val lset_fun = argv._2
          val n_time = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if(argv._1._5</StrBot){
            System.out.println("* Warning: the 'setInterval(string, ...)' call is detected during analysis, analysis results may not be sound.")
            val s = Helper.toString(Helper.toPrimitive_better(h, argv))
            val message = s.gamma match {
              case Some(_) => s.toString
              case _ => s.getAbsCase match {
                case AbsTop => "StrTop"
                case AbsBot => "StrBot"
                case _ if s.isAllNums => "NumStr"
                case _ => "OtherStr"
              }
            }
            System.out.println("* Warning: the string argument of 'setInterval' is in the below ...")           
            System.out.println(message)
            // unsound
            ((h, ctx), (he, ctxe))
          }
          else if (!lset_fun.isEmpty && n_time </ NumBot) {
            /* unsound semantic */
            val o_fun = h(EventFunctionTableLoc)
            val o_target = h(EventTargetTableLoc)
            val lset_f = lset_fun.filter((l) => BoolTrue <= Helper.IsCallable(h, l))
            val h1 =
              if (!lset_f.isEmpty) {
                val o_fun1 = o_fun.update("#TIME", o_fun("#TIME")._1 + PropValue(Value(lset_f)))
                val o_target1 = o_target.update("#TIME", o_target("#TIME")._1 + PropValue(Value(GlobalLoc)))
                h.update(EventFunctionTableLoc, o_fun1).update(EventTargetTableLoc, o_target1)
              }
              else
                h
            /* imprecise semantic, returns UInt */
            ((Helper.ReturnStore(h1, Value(UInt)), ctx), (he, ctxe))
          }
         
          else 
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMWindow.setTimeout" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          // ignore eval-like feature
          val argv = getArgValue(h, ctx, args, "0")
          val lset_fun = getArgValue(h, ctx, args, "0")._2
          val n_time = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if(argv._1._5</StrBot){
            System.out.println("* Warning: the 'setTimeout(string, ...)' call is detected during analysis, analysis results may not be sound.")
            val s = Helper.toString(Helper.toPrimitive_better(h, argv))
            val message = s.gamma match {
              case Some(_) => s.toString
              case _ => s.getAbsCase match {
                case AbsTop => "StrTop"
                case AbsBot => "StrBot"
                case _ if s.isAllNums => "NumStr"
                case _ => "OtherStr"
              }
            }
            System.out.println("* Warning: the string argument of 'setTimeout' is in the below ...")           
            System.out.println(message)
            // unsound
            ((h, ctx), (he, ctxe))
          }
          else if (!lset_fun.isEmpty && n_time </ NumBot) {
            /* unsound semantic */
            val o_fun = h(EventFunctionTableLoc)
            val o_target = h(EventTargetTableLoc)
            val lset_f = lset_fun.filter((l) => BoolTrue <= Helper.IsCallable(h, l))
            val h1 =
              if (!lset_f.isEmpty) {
                val o_fun1 = o_fun.update("#TIME", o_fun("#TIME")._1 + PropValue(Value(lset_f)))
                val o_target1 = o_target.update("#TIME", o_target("#TIME")._1 + PropValue(Value(GlobalLoc)))
                h.update(EventFunctionTableLoc, o_fun1).update(EventTargetTableLoc, o_target1)
              }
              else
                h
            /* imprecise semantic, returns UInt */
            ((Helper.ReturnStore(h1, Value(UInt)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      /*
        ("DOMWindow.stop" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
       */
        ("DOMWindow.unescape" -> (
         (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            /* arguments */
            val str = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
            if (str </ StrBot) {
              val encodedStr = str.getSingle match {
                case Some(v) =>
                  // encoding
                  StrTop
                case None => StrTop
              }
              ((Helper.ReturnStore(h, Value(encodedStr)), ctx), (he, ctxe))
            }
            else
              ((HeapBot, ContextBot), (he, ctxe))
          })),
         
      ("DOMWindow.getComputedStyle" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val elements = getArgValue(h, ctx, args, "0")
          val nullval = if(Value(NullTop) <= elements) Value(NullTop) else ValueBot

          val el_lset = elements._2
          val returnval = el_lset.foldLeft(ValueBot)((v, l) =>
            v + Helper.Proto(h, l, AbsString.alpha("style")))
          ((Helper.ReturnStore(h, returnval + nullval), ctx), (he, ctxe))
        }))

    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Date" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        })),
      ("DOMWindow.alert" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      /*
        ("DOMWindow.atob" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.back" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.blur" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.btoa" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
          */
      ("DOMWindow.clearInterval" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val n_handle = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (n_handle </ NumBot)
          /* unsound semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMWindow.clearTimeout" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val n_handle = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (n_handle </ NumBot)
          /* unsound semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      /*
        ("DOMWindow.close" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.confirm" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.escape" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.focus" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.forward" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.home" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.maximize" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.minimize" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.moveBy" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.moveTo" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.open" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.print" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.prompt" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.resizeBy" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.resizeTo" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scroll" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scrollBy" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scrollByLines" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scrollByPages" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.scrollTo" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
          */
      ("DOMWindow.setInterval" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          // ignore eval-like feature
          val lset_fun = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)._2
          val n_time = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (!lset_fun.isEmpty && n_time </ NumBot) {
            /* unsound semantic */
            val o_fun = h(EventFunctionTableLoc)
            val o_target = h(EventTargetTableLoc)
            val lset_f = lset_fun.filter((l) => BoolTrue <= PreHelper.IsCallable(h, l))
            val h1 =
              if (!lset_f.isEmpty) {
                val o_fun1 = o_fun.update("#TIME", o_fun("#TIME")._1 + PropValue(Value(lset_f)))
                val o_target1 = o_target.update("#TIME", o_target("#TIME")._1 + PropValue(Value(GlobalLoc)))
                h.update(EventFunctionTableLoc, o_fun1).update(EventTargetTableLoc, o_target1)
              }
              else
                h
            /* imprecise semantic, returns UInt */
            ((PreHelper.ReturnStore(h1, PureLocalLoc, Value(UInt)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMWindow.setTimeout" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          // ignore eval-like feature
          val lset_fun = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)._2
          val n_time = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (!lset_fun.isEmpty && n_time </ NumBot) {
            /* unsound semantic */
            val o_fun = h(EventFunctionTableLoc)
            val o_target = h(EventTargetTableLoc)
            val lset_f = lset_fun.filter((l) => BoolTrue <= PreHelper.IsCallable(h, l))
            val h1 =
              if (!lset_f.isEmpty) {
                val o_fun1 = o_fun.update("#TIME", o_fun("#TIME")._1 + PropValue(Value(lset_f)))
                val o_target1 = o_target.update("#TIME", o_target("#TIME")._1 + PropValue(Value(GlobalLoc)))
                h.update(EventFunctionTableLoc, o_fun1).update(EventTargetTableLoc, o_target1)
              }
              else
                h
            /* imprecise semantic, returns UInt */
            ((PreHelper.ReturnStore(h1, PureLocalLoc, Value(UInt)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        }))
      /*
        ("DOMWindow.stop" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          })),
        ("DOMWindow.unescape" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            System.err.println("* Warning: Semantics of the DOM API function '"+fun+"' are not defined.")
            ((h,ctx), (he, ctxe))
          }))
          */
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("DOMWindow.alert" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      //case "DOMWindow.atob" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.back" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.blur" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.btoa" => ((h, ctx), (he, ctxe))
      ("DOMWindow.clearInterval" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMWindow.clearTimeout" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      //case "DOMWindow.close" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.confirm" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.escape" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.focus" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.forward" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.home" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.maximize" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.minimize" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.moveBy" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.moveTo" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.open" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.print" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.prompt" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.resizeBy" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.resizeTo" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scroll" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollBy" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollByLines" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollByPages" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollTo" => ((h, ctx), (he, ctxe))
      ("DOMWindow.setInterval" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet(Set((EventFunctionTableLoc, "#TIME"), (EventTargetTableLoc, "#TIME"), (SinglePureLocalLoc, "@return")))
        })),
      ("DOMWindow.setTimeout" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet(Set((EventFunctionTableLoc, "#TIME"), (EventTargetTableLoc, "#TIME"), (SinglePureLocalLoc, "@return")))
        }))
      //case "DOMWindow.stop" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.unescape" => ((h, ctx), (he, ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("DOMWindow.alert" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      //case "DOMWindow.atob" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.back" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.blur" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.btoa" => ((h, ctx), (he, ctxe))
      ("DOMWindow.clearInterval" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMWindow.clearTimeout" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      //case "DOMWindow.close" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.confirm" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.escape" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.focus" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.forward" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.home" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.maximize" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.minimize" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.moveBy" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.moveTo" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.open" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.print" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.prompt" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.resizeBy" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.resizeTo" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scroll" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollBy" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollByLines" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollByPages" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.scrollTo" => ((h, ctx), (he, ctxe))
      ("DOMWindow.setInterval" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_fun = getArgValue(h, ctx, args, "0")._2
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val LP2 = lset_fun.foldLeft(LPBot)((lpset, l) => lpset ++ AccessHelper.IsCallable_use(h, l))
          LP1 ++ LP2 + (EventFunctionTableLoc, "#TIME") + (EventTargetTableLoc, "#TIME") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMWindow.setTimeout" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_fun = getArgValue(h, ctx, args, "0")._2
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val LP2 = lset_fun.foldLeft(LPBot)((lpset, l) => lpset ++ AccessHelper.IsCallable_use(h, l))
          LP1 ++ LP2 + (EventFunctionTableLoc, "#TIME") + (EventTargetTableLoc, "#TIME") + (SinglePureLocalLoc, "@return")
        }))
      //case "DOMWindow.stop" => ((h, ctx), (he, ctxe))
      //case "DOMWindow.unescape" => ((h, ctx), (he, ctxe))
    )
  }

  /* property list */
  def getPropList(): List[(String, AbsProperty)] = List(
    // DOM Level 0
    ("innerHeight",                AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("innerWidth",              AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("name",          AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("outerHeight",       AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("outerWidth",                 AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("pageXOffset", AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("pageYOffset",                AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("parent",               AbsConstValue(PropValue(ObjectValue(GlobalLoc, T, T, T)))),
    ("self",               AbsConstValue(PropValue(ObjectValue(GlobalLoc, T, T, T)))),
    ("scrollMaxX",          AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("scrollMaxY",      AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("scrollX",               AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("scrollY",            AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("screenX",               AbsConstValue(PropValue(ObjectValue(UInt,T, T, T)))),
    ("screenY",               AbsConstValue(PropValue(ObjectValue(UInt, T, T, T)))),
    ("status",               AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("top",                AbsConstValue(PropValue(ObjectValue(GlobalLoc, T, T, T)))),
    ("window",             AbsConstValue(PropValue(ObjectValue(GlobalLoc, T, T, T)))),
    // Navigator object
    ("navigator",          AbsConstValue(PropValue(ObjectValue(Navigator.getInstance.get, F, T, T)))),
    // Location object
    ("location",          AbsConstValue(PropValue(ObjectValue(DOMLocation.getInstance.get, F, T, T)))),
    // API
    ("alert",            AbsBuiltinFunc("DOMWindow.alert", 1)),
    ("atob",            AbsBuiltinFunc("DOMWindow.atob", 1)),
    ("back",            AbsBuiltinFunc("DOMWindow.back", 0)),
    ("blur",            AbsBuiltinFunc("DOMWindow.blur", 0)),
    ("btoa",            AbsBuiltinFunc("DOMWindow.btoa", 1)),
    ("clearInterval",            AbsBuiltinFunc("DOMWindow.clearInterval", 0)),
    ("clearTimeout",            AbsBuiltinFunc("DOMWindow.clearTimeout", 0)),
    ("close",            AbsBuiltinFunc("DOMWindow.close", 0)),
    ("confirm",            AbsBuiltinFunc("DOMWindow.confirm", 0)),
    ("escape",            AbsBuiltinFunc("DOMWindow.escape", 1)),
    ("focus",            AbsBuiltinFunc("DOMWindow.focus", 0)),
    ("foward",            AbsBuiltinFunc("DOMWindow.forward", 0)),
    ("home",            AbsBuiltinFunc("DOMWindow.home", 0)),
    ("maximize",            AbsBuiltinFunc("DOMWindow.maximize", 0)),
    ("minimize",            AbsBuiltinFunc("DOMWindow.minimize", 0)),
    ("moveBy",            AbsBuiltinFunc("DOMWindow.moveBy", 2)),
    ("moveTo",            AbsBuiltinFunc("DOMWindow.moveTo", 2)),
    ("open",            AbsBuiltinFunc("DOMWindow.open", 1)),
    ("print",            AbsBuiltinFunc("DOMWindow.print", 0)),
    ("prompt",            AbsBuiltinFunc("DOMWindow.prompt", 1)),
    ("resizeBy",            AbsBuiltinFunc("DOMWindow.resizeBy", 2)),
    ("resizeTo",            AbsBuiltinFunc("DOMWindow.resizeTo", 2)),
    ("scroll",            AbsBuiltinFunc("DOMWindow.scroll", 2)),
    ("scrollBy",            AbsBuiltinFunc("DOMWindow.scrollBy", 2)),
    ("scrollByLines",            AbsBuiltinFunc("DOMWindow.scrollByLines", 1)),
    ("scrollByPages",            AbsBuiltinFunc("DOMWindow.scrollByPages", 1)),
    ("scrollTo",            AbsBuiltinFunc("DOMWindow.scrollTo", 2)),
    ("setInterval",            AbsBuiltinFunc("DOMWindow.setInterval", 2)),
    ("setTimeout",            AbsBuiltinFunc("DOMWindow.setTimeout", 2)),
    ("stop",            AbsBuiltinFunc("DOMWindow.stop", 0)),
    ("unescape",            AbsBuiltinFunc("DOMWindow.unescape", 1))
  )
}
