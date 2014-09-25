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
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper => AH, _}
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError}

object JQueryCSS extends ModelData {
  
  val OffsetLoc = newSystemLoc("jQueryOffset", Recent)
  
  private val prop_const: List[(String, AbsProperty)] = List(
    ("cssHooks",    AbsBuiltinFunc("jQuery.cssHooks", 2)),
    ("offset",            AbsConstValue(PropValue(ObjectValue(Value(OffsetLoc), T, T, T))))
  )
  
  private val prop_offset: List[(String, AbsProperty)] = List(
    ("@class",       AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",       AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",  AbsConstValue(PropValue(BoolTrue))),
    ("setOffset",         AbsBuiltinFunc("jQuery.offset.setOffset", 3))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("css",         AbsBuiltinFunc("jQuery.prototype.css", 2)),
    ("height",      AbsBuiltinFunc("jQuery.prototype.height", 2)),
    ("innerHeight", AbsBuiltinFunc("jQuery.prototype.innerHeight", 2)),
    ("innerWidth",  AbsBuiltinFunc("jQuery.prototype.innerWidth", 2)),
    ("offset",      AbsBuiltinFunc("jQuery.prototype.offset", 1)),
    ("outerHeight", AbsBuiltinFunc("jQuery.prototype.outerHeight", 2)),
    ("outerWidth",  AbsBuiltinFunc("jQuery.prototype.outerWidth", 2)),
    ("position",    AbsBuiltinFunc("jQuery.prototype.position", 0)),
    ("scrollLeft",  AbsBuiltinFunc("jQuery.prototype.scrollLeft", 1)),
    ("scrollTop",   AbsBuiltinFunc("jQuery.prototype.scrollTop", 1)),
    ("width",       AbsBuiltinFunc("jQuery.prototype.width", 2))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const), (JQuery.ProtoLoc, prop_proto), (OffsetLoc, prop_offset)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("jQuery.prototype.css" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 2nd argument */
          var v_arg2 = getArgValue(h, ctx, args, "1")

          val v_set =
            if (v_arg2._1._1 <= UndefBot && v_arg2 </ ValueBot)
              Value(lset_this)
            else
              ValueBot
          val v_ret = Value(StrTop) + v_set
          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.height" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          var v_arg1 = getArgValue(h, ctx, args, "0")

          val v_set =
            if (v_arg1._1._1 <= UndefBot && v_arg1 </ ValueBot)
              Value(lset_this)
            else
              ValueBot
          val v_ret = Value(NumTop) + v_set
          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.innerHeight" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        })),
      ("jQuery.prototype.innerWidth" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        })),
      "jQuery.prototype.offset" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)

          /* 1st argument */
          var v_arg1 = getArgValue(h, ctx, args, "0")

          /* new loc */
          val (h_ret1, ctx_ret1, v_ret1) =
            if (v_arg1._1._1 </ UndefBot) {
              val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
              val o_new = Helper.NewObject(ObjProtoLoc).update("left", PropValue(ObjectValue(Value(NumTop), T, T, T)))
                .update("top", PropValue(ObjectValue(Value(NumTop), T, T, T)))
              val l_ret = addrToLoc(addr1, Recent)
              val h_2 = h_1.update(l_ret, o_new)
              (h_2, ctx_1, Value(l_ret))
            }
            else
              (HeapBot, ContextBot, ValueBot)

          val v_ret2 =
            if (v_arg1._1._1 <= UndefBot && v_arg1 </ ValueBot)
              Value(lset_this)
            else
              ValueBot

          val h_ret = h + h_ret1
          val ctx_ret = ctx_ret1 + ctx
          val v_ret = v_ret1 + v_ret2

          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h_ret, v_ret), ctx_ret), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      ("jQuery.prototype.outerHeight" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        })),
      ("jQuery.prototype.outerWidth" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        })),
      "jQuery.prototype.position" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val o_new = Helper.NewObject(ObjProtoLoc).update("left", PropValue(ObjectValue(Value(NumTop), T, T, T)))
            .update("top", PropValue(ObjectValue(Value(NumTop), T, T, T)))
          val h_2 = h_1.update(l_ret, o_new)
          ((Helper.ReturnStore(h_2, Value(l_ret)), ctx_1), (he, ctxe))
        }),
      ("jQuery.prototype.scrollLeft" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          var v_arg1 = getArgValue(h, ctx, args, "0")

          val v_set =
            if (v_arg1._1._1 <= UndefBot && v_arg1 </ ValueBot)
              Value(lset_this)
            else
              ValueBot
          val v_ret = Value(NumTop) + v_set
          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.scrollTop" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          var v_arg1 = getArgValue(h, ctx, args, "0")

          val v_set =
            if (v_arg1._1._1 <= UndefBot && v_arg1 </ ValueBot)
              Value(lset_this)
            else
              ValueBot
          val v_ret = Value(NumTop) + v_set
          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.width" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          var v_arg1 = getArgValue(h, ctx, args, "0")

          val v_set =
            if (v_arg1._1._1 <= UndefBot && v_arg1 </ ValueBot)
              Value(lset_this)
            else
              ValueBot
          val v_ret = Value(NumTop) + v_set
          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
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
}
