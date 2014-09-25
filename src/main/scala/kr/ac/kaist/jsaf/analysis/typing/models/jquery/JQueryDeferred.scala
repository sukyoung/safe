/*******************************************************************************
    Copyright (c) 2013-2014, S-Core.
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

object JQueryDeferred extends ModelData {
  
  val DeferredLoc = newSystemLoc("jQueryDeferred", Recent)
  val DeferredInsLoc = newSystemLoc("jQueryDeferredInstance", Recent)
  
  //val PromiseInsLoc = newSystemLoc("jQueryPromiseInstance", Recent)
  
  private val prop_const: List[(String, AbsProperty)] = List(
    ("Deferred",       AbsConstValue(PropValue(ObjectValue(Value(DeferredLoc), T, T, T))))
  )

  private val prop_deferred: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("@scope",               AbsConstValue(PropValueNullTop)),
    ("@function",            AbsInternalFunc("jQuery.Deferred")),
    ("@construct",           AbsInternalFunc("jQuery.Deferred")),
    ("@hasinstance",         AbsConstValue(PropValueNullTop)),
    ("length",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("promise",         AbsBuiltinFunc("jQuery.prototype.promise", 2))
  )
  // deferred instance object
  private val prop_deferred_ins: List[(String, AbsProperty)] = List(
    ("always", AbsBuiltinFunc("jQueryDeferred.always", 0)),
    ("done",       AbsBuiltinFunc("jQueryDeferred.done", 0)),
    ("fail",    AbsBuiltinFunc("jQueryDeferred.fail", 0)),
    ("notify",      AbsBuiltinFunc("jQueryDeferred.notify", 0)),
    ("notifyWith",      AbsBuiltinFunc("jQueryDeferred.notifyWith", 2)),
    ("pipe",      AbsBuiltinFunc("jQueryDeferred.pipe", 0)),
    ("progress",      AbsBuiltinFunc("jQueryDeferred.progress", 0)),
    ("promise",      AbsBuiltinFunc("jQueryDeferred.promise", 1)),
    ("reject",      AbsBuiltinFunc("jQueryDeferred.reject", 0)),
    ("rejectWith",      AbsBuiltinFunc("jQueryDeferred.rejectWith", 2)),
    ("resolve",      AbsBuiltinFunc("jQueryDeferred.resolve", 0)),
    ("resolveWith",      AbsBuiltinFunc("jQueryDeferred.resolveWith", 2)),
    ("isResolved",      AbsBuiltinFunc("jQueryDeferred.isResolved", 0)),
    ("state",      AbsBuiltinFunc("jQueryDeferred.state", 0)),
    ("then",      AbsBuiltinFunc("jQueryDeferred.then", 0))
  )
 
  // promise instance object
  private val prop_promise_ins: List[(String, AbsProperty)] = List(
    ("always", AbsBuiltinFunc("jQueryPromise.always", 0)),
    ("done", AbsBuiltinFunc("jQueryPromise.done", 0)),
    ("fail", AbsBuiltinFunc("jQueryPromise.fail", 0)),
    ("pipe", AbsBuiltinFunc("jQueryPromise.pipe", 0)),
    ("progress", AbsBuiltinFunc("jQueryPromise.progress", 0)),
    ("promise", AbsBuiltinFunc("jQueryPromise.promise", 1)),
    ("state", AbsBuiltinFunc("jQueryPromise.state", 0)),
    ("then", AbsBuiltinFunc("jQueryPromise.then", 0))
  )


  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const), (JQuery.ProtoLoc, prop_proto), (DeferredLoc, prop_deferred), (DeferredInsLoc, prop_deferred_ins)
    //,(PromiseInsLoc, prop_promise_ins)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      // jQuery.Deferred([beforeStart])
      // beforeStart : Function(Deferred deferred)
      ("jQuery.Deferred" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr = (0 until 16).foldLeft[Array[Address]](new Array(0))((a, i) => a :+ cfg.getAPIAddress(addr_env, i))
          val (h_1, ctx_1) = addr.foldLeft((h, ctx))((hc, a) => Helper.Oldify(hc._1, hc._2, a))

          // start here
          val loc: Array[Loc] = addr.map(a => addrToLoc(a, Recent))
          
          /* optional argument */
          val v_beforeStart = getArgValue(h_1, ctx_1, args, "0")
          
          if (v_beforeStart </ Value(UndefTop))
            System.err.println("* Warning : 'jQuery.Deferred(function)' has not beed modeled, the argument is ignored.")

          val deferred_obj = h_1(DeferredInsLoc)
          // create a new deferred object
          val (h_2, new_deferred_obj) = deferred_obj.getProps.zipWithIndex.foldLeft((h_1, Helper.NewObject(ObjProtoLoc)))((ho, si) => {
            val fun_loc = deferred_obj(si._1)._1._1._2
            val fun_obj = fun_loc.foldLeft(Obj.bottom)((o, l) => o + ho._1(l))
            val _h1 = ho._1.update(loc(si._2 + 1), fun_obj)
            (_h1, ho._2.update(si._1, PropValue(ObjectValue(Value(loc(si._2+1)), T, T, T))))
          })

          val h_3 = h_2.update(loc(0), new_deferred_obj)

          ((Helper.ReturnStore(h_3, Value(loc(0))), ctx_1), (he, ctxe))
        }))
        /*
        ,
   ("jQueryDeferred.isResolved" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
        
        })),
      "jQueryDeferred.promise" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // rough modeling : simply returns aa promise instance object
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr = (0 until 9).foldLeft[Array[Address]](new Array(0))((a, i) => a :+ cfg.getAPIAddress(addr_env, i))
          val (h_1, ctx_1) = addr.foldLeft((h, ctx))((hc, a) => Helper.Oldify(hc._1, hc._2, a))

          // start here
          val loc: Array[Loc] = addr.map(a => addrToLoc(a, Recent))
          
          val promise_obj = h_1(PromiseInsLoc)
          // create a new ajax object
          val (h_2, new_promise_obj) = promise_obj.getProps.zipWithIndex.foldLeft((h_1, Helper.NewObject(ObjProtoLoc)))((ho, si) => {
            val fun_loc = promise_obj(si._1)._1._1._2
            val fun_obj = fun_loc.foldLeft(Obj.bottom)((o, l) => o + ho._1(l))
            val _h1 = ho._1.update(loc(si._2 + 1), fun_obj)
            (_h1, ho._2.update(si._1, PropValue(ObjectValue(Value(loc(si._2+1)), T, T, T))))
          })

          val h_3 = h_2.update(loc(0), new_promise_obj)

          ((Helper.ReturnStore(h_3, Value(loc(0))), ctx_1), (he, ctxe))
        }),
      "jQueryPromise.promise" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // rough modeling : simply returns aa promise instance object
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
        }),
      "jQueryPromise.done" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // rough modeling : simply returns aa promise instance object
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
        }),
      "jQueryPromise.fail" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // rough modeling : simply returns aa promise instance object
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
        }),
      "jQueryPromise.progress" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // rough modeling : simply returns aa promise instance object
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
        })*/
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
