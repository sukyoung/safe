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

object BuiltinNumber extends ModelData {

  val ConstLoc = newSystemLoc("NumberConst", Recent)
  val ProtoLoc = newSystemLoc("NumberProto", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",            AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",            AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",       AbsConstValue(PropValue(T))),
    ("@scope",            AbsConstValue(PropValueNullTop)),
    ("@function",         AbsInternalFunc("Number")),
    ("@construct",        AbsInternalFunc("Number.constructor")),
    ("@hasinstance",      AbsConstValue(PropValueNullTop)),
    ("prototype",         AbsConstValue(PropValue(ObjectValue(Value(ProtoLoc), F, F, F)))),
    ("length",            AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F)))),
    ("MAX_VALUE",         AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(Double.MaxValue), F, F, F)))),
    ("MIN_VALUE",         AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(java.lang.Double.MIN_VALUE), F, F, F)))),
    ("NaN",               AbsConstValue(PropValue(ObjectValue(NaN, F, F, F)))),
    ("NEGATIVE_INFINITY", AbsConstValue(PropValue(ObjectValue(NegInf, F, F, F)))),
    ("POSITIVE_INFINITY", AbsConstValue(PropValue(ObjectValue(PosInf, F, F, F))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",         AbsConstValue(PropValue(AbsString.alpha("Number")))),
    ("@proto",         AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",    AbsConstValue(PropValue(BoolTrue))),
    ("@primitive",     AbsConstValue(PropValue(AbsNumber.alpha(+0)))),
    ("constructor",    AbsConstValue(PropValue(ObjectValue(ConstLoc, F, F, F)))),
    ("toString",       AbsBuiltinFunc("Number.prototype.toString", 0)),
    ("toLocaleString", AbsBuiltinFunc("Number.prototype.toLocaleString", 0)),
    ("valueOf",        AbsBuiltinFunc("Number.prototype.valueOf", 0)),
    ("toFixed",        AbsBuiltinFunc("Number.prototype.toFixed", 1)),
    ("toExponential",  AbsBuiltinFunc("Number.prototype.toExponential", 1)),
    ("toPrecision",    AbsBuiltinFunc("Number.prototype.toPrecision", 1))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const), (ProtoLoc, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "Number" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // 15.7.1.1 Number( [value] )
          val v_1 = getArgValue(h, ctx, args, "0")
          val arg_length = getArgValue(h, ctx, args, "length")._1._4

          // If value is not supplied, +0 is returned.
          val value_1 =
            if (AbsNumber.alpha(0) <= arg_length) Value(AbsNumber.alpha(0))
            else ValueBot
          // Returns a Number value computed by ToNumber(value).
          val value_2 =
            if (AbsNumber.alpha(0) != arg_length && !(arg_length <= NumBot)) Value(Helper.toNumber(Helper.toPrimitive_better(h, v_1)))
            else ValueBot
          val value = value_1 + value_2

          ((Helper.ReturnStore(h, value), ctx), (he, ctxe))
        }),
      "Number.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          // 15.7.2.1 new Number( [value] )
          val v_1 = getArgValue(h, ctx, args, "0")
          val arg_length = getArgValue(h, ctx, args, "length")._1._4

          // [[PrimitiveValue]]
          val value_1 =
            if (AbsNumber.alpha(0) <= arg_length) AbsNumber.alpha(0)
            else NumBot
          val value_2 =
            if (AbsNumber.alpha(0) != arg_length && !(arg_length <= NumBot)) Helper.toNumber(Helper.toPrimitive_better(h, v_1))
            else NumBot
          val primitive_value = value_1 + value_2

          val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, Helper.NewNumber(primitive_value)))

          if (primitive_value </ NumBot)
            ((Helper.ReturnStore(h_1, Value(lset_this)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "Number.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val lset_num = lset_this.filter((l) => AbsString.alpha("Number") <= h(l)("@class")._1._2._1._5)

          val es = notGenericMethod(h, lset_this, "Number")
          val (v, es2) =
            n_arglen.getAbsCase match {
              case AbsBot => (ValueBot, ExceptionBot)
              case _ => AbsNumber.getUIntSingle(n_arglen) match {
                case Some(n_arglen) if n_arglen == 0 =>
                  (Value(Helper.defaultToString(h, lset_num)), ExceptionBot)
                case Some(n_arglen) if n_arglen > 0 => {
                  val es =
                    if (BoolTrue <= Operator.bopGreater(getArgValue(h, ctx, args, "0"), Value(AbsNumber.alpha(36)))._1._3)
                      Set[Exception](RangeError)
                    else if (BoolTrue <= Operator.bopLess(getArgValue(h, ctx, args, "0"), Value(AbsNumber.alpha(2)))._1._3)
                      Set[Exception](RangeError)
                    else
                      ExceptionBot
                  (Value(StrTop), es)
                }
                case _ => {
                  (Value(StrTop), Set[Exception](RangeError))
                }
              }
            }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es ++ es2)
          if (v </ ValueBot)
            ((Helper.ReturnStore(h, v), ctx), (he + h_e, ctxe + ctx_e))
          else
            ((HeapBot, ContextBot), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toLocaleString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val v_prim = lset_this.foldLeft(ValueBot)((_v, _l) => _v + h(_l)("@primitive")._1._2)
          val v = Value(Helper.toString(v_prim._1))
          if (v </ ValueBot)
            ((Helper.ReturnStore(h, v), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "Number.prototype.valueOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val es = notGenericMethod(h, lset_this, "Number")
          val lset_num = lset_this.filter((l) => AbsString.alpha("Number") <= h(l)("@class")._1._2._1._5)
          val n = lset_num.foldLeft[AbsNumber](NumBot)((_b, l) => _b + h(l)("@primitive")._1._2._1._4)
          val (h_1, c_1) =
            if (n == NumBot)
              (HeapBot, ContextBot)
            else
              (Helper.ReturnStore(h, Value(n)), ctx)
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((h_1, c_1), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toFixed" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, AbsNumber.alpha(0) + v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toExponential" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toPrecision" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(21)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(1)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "Number" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          // 15.7.1.1 Number( [value] )
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val arg_length = getArgValue_pre(h, ctx, args, "length", PureLocalLoc)._1._4

          // If value is not supplied, +0 is returned.
          val value_1 =
            if (AbsNumber.alpha(0) <= arg_length) Value(AbsNumber.alpha(0))
            else ValueBot
          // Returns a Number value computed by ToNumber(value).
          val value_2 =
            if (AbsNumber.alpha(0) != arg_length && !(arg_length <= NumBot)) Value(PreHelper.toNumber(PreHelper.toPrimitive(v_1)))
            else ValueBot
          val value = value_1 + value_2

          ((PreHelper.ReturnStore(h, PureLocalLoc, value), ctx), (he, ctxe))
        }),
      "Number.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          // 15.7.2.1 new Number( [value] )
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val arg_length = getArgValue_pre(h, ctx, args, "length", PureLocalLoc)._1._4

          // [[PrimitiveValue]]
          val value_1 =
            if (AbsNumber.alpha(0) <= arg_length) AbsNumber.alpha(0)
            else NumBot
          val value_2 =
            if (AbsNumber.alpha(0) != arg_length && !(arg_length <= NumBot)) PreHelper.toNumber(PreHelper.toPrimitive(v_1))
            else NumBot
          val primitive_value = value_1 + value_2

          val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, PreHelper.NewNumber(primitive_value)))

          if (primitive_value </ NumBot)
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(lset_this)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        }),
      "Number.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val n_arglen = Operator.ToUInt32(getArgValue_pre(h, ctx, args, "length", PureLocalLoc))
          val lset_num = lset_this.filter((l) => AbsString.alpha("Number") <= h(l)("@class")._1._2._1._5)
          val v_prim = lset_num.foldLeft(ValueBot)((_v, _l) => _v + h(_l)("@primitive")._1._2)
          val es = notGenericMethod(h, lset_this, "Number")

          val (v, es2) =
            n_arglen.getAbsCase match {
              case AbsBot => (ValueBot, ExceptionBot)
              case _ => AbsNumber.getUIntSingle(n_arglen) match {
                case Some(n_arglen) if n_arglen == 0 =>
                  (Value(PreHelper.toString(v_prim._1)), ExceptionBot)
                case Some(n_arglen) if n_arglen > 0 => {
                  val es =
                    if (BoolTrue <= Operator.bopGreater(getArgValue_pre(h, ctx, args, "0", PureLocalLoc), Value(AbsNumber.alpha(36)))._1._3)
                      Set[Exception](RangeError)
                    else if (BoolTrue <= Operator.bopLess(getArgValue_pre(h, ctx, args, "0", PureLocalLoc), Value(AbsNumber.alpha(2)))._1._3)
                      Set[Exception](RangeError)
                    else
                      ExceptionBot
                  (Value(StrTop), es)
                }
                case _ => {
                  (Value(StrTop), Set[Exception](RangeError))
                }
              }
            }
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es ++ es2)
          if (v </ ValueBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, v), ctx_e), (he + h_e, ctxe + ctx_e))
          else
            ((h, ctx), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toLocaleString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val v_prim = lset_this.foldLeft(ValueBot)((_v, _l) => _v + h(_l)("@primitive")._1._2)
          val v = Value(PreHelper.toString(v_prim._1))
          if (v </ ValueBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, v), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        }),
      "Number.prototype.valueOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val es = notGenericMethod(h, lset_this, "Number")
          val lset_num = lset_this.filter((l) => AbsString.alpha("Number") <= h(l)("@class")._1._2._1._5)
          val n = lset_num.foldLeft[AbsNumber](NumBot)((_b, l) => _b + h(l)("@primitive")._1._2._1._4)
          val (h_1, c_1) =
            if (n == NumBot)
              (h, ctx)
            else
              (PreHelper.ReturnStore(h, PureLocalLoc, Value(n)), ctx)
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, c_1, PureLocalLoc, es)
          ((h_1, c_1), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toFixed" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, AbsNumber.alpha(0) + v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(StrTop)), ctx_e), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toExponential" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(StrTop)), ctx_e), (he + h_e, ctxe + ctx_e))
        }),
      "Number.prototype.toPrecision" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(21)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(1)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(StrTop)), ctx_e), (he + h_e, ctxe + ctx_e))
        })
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      "Number" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }),
      "Number.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) =>
            AH.NewNumber_def.foldLeft(lpset)((_lpset, prop) => _lpset +(l, prop)))
          LP1 +(SinglePureLocalLoc, "@return")
        }),
      "Number.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es1 = notGenericMethod(h, lset_this, "Number")
          val es2 =
            n_arglen.getAbsCase match {
              case AbsBot => ExceptionBot
              case _ => AbsNumber.getUIntSingle(n_arglen) match {
                case Some(n_arglen) if n_arglen == 0 => ExceptionBot
                case Some(n_arglen) if n_arglen > 0 => {
                  if (BoolTrue <= Operator.bopGreater(getArgValue(h, ctx, args, "0"), Value(AbsNumber.alpha(36)))._1._3)
                    Set[Exception](RangeError)
                  else if (BoolTrue <= Operator.bopLess(getArgValue(h, ctx, args, "0"), Value(AbsNumber.alpha(2)))._1._3)
                    Set[Exception](RangeError)
                  else
                    ExceptionBot
                }
                case _ => Set[Exception](RangeError)
              }
            }
          AH.RaiseException_def(es1 ++ es2) +(SinglePureLocalLoc, "@return")
        }),
      "Number.prototype.toLocaleString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }),
      "Number.prototype.valueOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val es = notGenericMethod(h, lset_this, "Number")
          AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return")
        }),
      "Number.prototype.toFixed" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, AbsNumber.alpha(0) + v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return")
        }),
      "Number.prototype.toExponential" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return")
        }),
      "Number.prototype.toPrecision" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(21)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(1)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return")
        })
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      "Number" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++
            getArgValue_use(h, ctx, args, "length") +(SinglePureLocalLoc, "@return")
        }),
      "Number.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* may def */
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) =>
            AH.NewNumber_def.foldLeft(lpset)((_lpset, prop) => _lpset +(l, prop)))
          LP1 ++ getArgValue_use(h, ctx, args, "0") ++
            getArgValue_use(h, ctx, args, "length") +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        }),
      "Number.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val LP1 = getArgValue_use(h, ctx, args, "length")
          val es1 = notGenericMethod(h, lset_this, "Number")
          val lset_num = lset_this.filter((l) => AbsString.alpha("Number") <= h(l)("@class")._1._2._1._5)
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => lpset +(l, "@class"))
          val v_prim = lset_num.foldLeft(ValueBot)((_v, _l) => _v + h(_l)("@primitive")._1._2)
          val LP3 = lset_num.foldLeft(LPBot)((lpset, l) => lpset +(l, "@primitive"))
          val (es2, lpset4) =
            n_arglen.getAbsCase match {
              case AbsBot => (ExceptionBot, LPBot)
              case _ => AbsNumber.getUIntSingle(n_arglen) match {
                case Some(n_arglen) if n_arglen == 0 => (ExceptionBot, LPBot)
                case Some(n_arglen) if n_arglen > 0 => {
                  if (BoolTrue <= Operator.bopGreater(getArgValue(h, ctx, args, "0"), Value(AbsNumber.alpha(36)))._1._3)
                    (Set[Exception](RangeError), getArgValue_use(h, ctx, args, "0"))
                  else if (BoolTrue <= Operator.bopLess(getArgValue(h, ctx, args, "0"), Value(AbsNumber.alpha(2)))._1._3)
                    (Set[Exception](RangeError), getArgValue_use(h, ctx, args, "0"))
                  else
                    (ExceptionBot, getArgValue_use(h, ctx, args, "0"))
                }
                case _ => (Set[Exception](RangeError), getArgValue_use(h, ctx, args, "0"))
              }
            }
          val LP5 = AH.RaiseException_use(es1 ++ es2)
          LP1 ++ LP2 ++ LP3 ++ lpset4 ++ LP5 +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        }),
      "Number.prototype.toLocaleString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset +(l, "@primitive"))
          LP1 ++ getArgValue_use(h, ctx, args, "length") +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        }),
      "Number.prototype.valueOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_num = lset_this.filter((l) => AbsString.alpha("Number") <= h(l)("@class")._1._2._1._5)
          val es = notGenericMethod(h, lset_this, "Number")
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset +(l, "@class"))
          val LP2 = lset_num.foldLeft(LPBot)((lpset, l) => lpset +(l, "@primitive"))
          LP1 ++ LP2 ++ AH.RaiseException_use(es) +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        }),
      "Number.prototype.toFixed" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, AbsNumber.alpha(0) + v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          getArgValue_use(h, ctx, args, "0") ++ AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        }),
      "Number.prototype.toExponential" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(20)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(0)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          getArgValue_use(h, ctx, args, "0") ++ AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        }),
      "Number.prototype.toPrecision" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 =
            if (UndefTop <= v_1._1._1)
              Value(PValue(UndefBot, v_1._1._2, v_1._1._3, v_1._1._4, v_1._1._5), v_1._2)
            else
              v_1
          val es =
            if (BoolTrue <= Operator.bopGreater(v_2, Value(AbsNumber.alpha(21)))._1._3)
              Set[Exception](RangeError)
            else if (BoolTrue <= Operator.bopLess(v_2, Value(AbsNumber.alpha(1)))._1._3)
              Set[Exception](RangeError)
            else
              ExceptionBot
          getArgValue_use(h, ctx, args, "0") ++ AH.RaiseException_def(es) +(SinglePureLocalLoc, "@return") +(SinglePureLocalLoc, "@this")
        })
    )
  }
}
