/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object BuiltinBoolean extends ModelData {

  val ConstLoc = newSystemLoc("BooleanConst", Recent)
  val ProtoLoc = newSystemLoc("BooleanProto", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("Boolean")),
    ("@construct",               AbsInternalFunc("Boolean.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(Value(ProtoLoc), F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",         AbsConstValue(PropValue(AbsString.alpha("Boolean")))),
    ("@proto",         AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",    AbsConstValue(PropValue(BoolTrue))),
    ("constructor",    AbsConstValue(PropValue(ObjectValue(ConstLoc, F, F, F)))),
    ("toString",       AbsBuiltinFunc("Boolean.prototype.toString", 0)),
    ("valueOf",        AbsBuiltinFunc("Boolean.prototype.valueOf", 0))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const), (ProtoLoc, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "Boolean" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // 15.6.1.1 Boolean(value)
          val v_1 = getArgValue(h, ctx, args, "0")
          val arg_length = getArgValue(h, ctx, args, "length")._1._4

          // Returns a Boolean value computed by ToBoolean(value).
          val value =
            if (!(arg_length <= NumBot)) Value(Helper.toBoolean(v_1))
            else ValueBot
          ((Helper.ReturnStore(h, value), ctx), (he, ctxe))
        }),
      "Boolean.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // 15.6.2.1 new Boolean(value)
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v_1 = getArgValue(h, ctx, args, "0")
          val arg_length = getArgValue(h, ctx, args, "length")._1._4

          // [[PrimitiveValue]]
          val primitive_value =
            if (!(arg_length <= NumBot)) Helper.toBoolean(v_1)
            else BoolBot

          val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, Helper.NewBoolean(primitive_value)))

          if (primitive_value </ BoolBot) {
            ((Helper.ReturnStore(h_1, Value(lset_this)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "Boolean.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val es = notGenericMethod(h, lset_this, "Boolean")

          val lset_bool = lset_this.filter((l) => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
          val s = Helper.defaultToString(h, lset_bool)
          val (h_1, c_1) =
            if (s == StrBot) {
              (HeapBot, ContextBot)
            }
            else {
              (Helper.ReturnStore(h, Value(s)), ctx)
            }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((h_1, c_1), (h_e, ctx_e))
        }),
      "Boolean.prototype.valueOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val es = notGenericMethod(h, lset_this, "Boolean")

          val lset_bool = lset_this.filter((l) => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
          val b = lset_bool.foldLeft[AbsBool](BoolBot)((_b, l) => _b + h(l)("@primitive")._2._1._3)
          val (h_1, c_1) =
            if (b == BoolBot) {
              (HeapBot, ContextBot)
            }
            else {
              (Helper.ReturnStore(h, Value(b)), ctx)
            }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((h_1, c_1), (h_e, ctx_e))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "Boolean" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // 15.6.1.1 Boolean(value)
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val arg_length = getArgValue_pre(h, ctx, args, "length", PureLocalLoc)._1._4

          // Returns a Boolean value computed by ToBoolean(value).
          val value =
            if (!(arg_length <= NumBot)) Value(PreHelper.toBoolean(v_1))
            else ValueBot
          ((PreHelper.ReturnStore(h, PureLocalLoc, value), ctx), (he, ctxe))
        }),
      "Boolean.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          // 15.6.2.1 new Boolean(value)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val arg_length = getArgValue_pre(h, ctx, args, "length", PureLocalLoc)._1._4

          // [[PrimitiveValue]]
          val primitive_value =
            if (!(arg_length <= NumBot)) PreHelper.toBoolean(v_1)
            else BoolBot

          val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, PreHelper.NewBoolean(primitive_value)))

          if (primitive_value </ BoolBot) {
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(lset_this)), ctx), (he, ctxe))
          }
          else
            ((h_1, ctx), (he, ctxe))
        }),
      "Boolean.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          val es = notGenericMethod(h, lset_this, "Boolean")

          val lset_bool = lset_this.filter((l) => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
          val s = Helper.defaultToString(h, lset_bool)
          val (h_1, c_1) =
            if (s == StrBot) {
              (h, ctx)
            }
            else {
              (PreHelper.ReturnStore(h, PureLocalLoc, Value(s)), ctx)
            }
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          ((h_1, c_1), (h_e, ctx_e))
        }),
      "Boolean.prototype.valueOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          val es = notGenericMethod(h, lset_this, "Boolean")

          val lset_bool = lset_this.filter((l) => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
          val b = lset_bool.foldLeft[AbsBool](BoolBot)((_b, l) => _b + h(l)("@primitive")._2._1._3)
          val (h_1, c_1) =
            if (b == BoolBot) {
              (h, ctx)
            }
            else {
              (PreHelper.ReturnStore(h, PureLocalLoc, Value(b)), ctx)
            }
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, c_1, PureLocalLoc, es)
          ((h_e, ctx_e), (h_e, ctx_e))
        })
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      "Boolean" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }),
      "Boolean.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) =>
            AH.NewBoolean_def.foldLeft(lpset)((_lpset, prop) => _lpset +(l, prop)))
          LP1 +(SinglePureLocalLoc, "@return")
        }),
      "Boolean.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val es = notGenericMethod(h, lset_this, "Boolean")
          AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return")
        }),
      "Boolean.prototype.valueOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val es = notGenericMethod(h, lset_this, "Boolean")
          AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return")
        })
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      "Boolean" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++
            getArgValue_use(h, ctx, args, "length") +(SinglePureLocalLoc, "@return")
        }),
      "Boolean.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          /* may def */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) =>
            AH.NewBoolean_def.foldLeft(lpset)((_lpset, prop) => _lpset +(l, prop)))
          LP1 ++ getArgValue_use(h, ctx, args, "0") ++
            getArgValue_use(h, ctx, args, "length") +(SinglePureLocalLoc, "@return") + ((SinglePureLocalLoc, "@this"))
        }),
      "Boolean.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset +(l, "@class"))
          val es = notGenericMethod(h, lset_this, "Boolean")
          val LP2 = AH.RaiseException_use(es)
          val lset_bool = lset_this.filter((l) => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
          val LP3 = lset_bool.foldLeft(LPBot)((lpset, l) => lpset +(l, "@primitive"))
          LP1 ++ LP2 ++ LP3 +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        }),
      "Boolean.prototype.valueOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset +(l, "@class"))
          val es = notGenericMethod(h, lset_this, "Boolean")
          val LP2 = AH.RaiseException_use(es)
          val lset_bool = lset_this.filter((l) => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
          val LP3 = lset_bool.foldLeft(LPBot)((lpset, l) => lpset +(l, "@primitive"))
          LP1 ++ LP2 ++ LP3 +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        })
    )
  }
}
