/*******************************************************************************
    Copyright (c) 2013-2014, S-Core.
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

object BuiltinError extends ModelData {
  /* error object(constructor) */
  val ErrConstLoc: Loc       = newSystemLoc("ErrConst", Recent)
  val EvalErrConstLoc: Loc   = newSystemLoc("EvalErrConst", Recent)
  val RangeErrConstLoc: Loc  = newSystemLoc("RangeErrConst", Recent)
  val RefErrConstLoc: Loc    = newSystemLoc("RefErrConst", Recent)
  val SyntaxErrConstLoc: Loc = newSystemLoc("SyntaxErrConst", Recent)
  val TypeErrConstLoc: Loc   = newSystemLoc("TypeErrConst", Recent)
  val URIErrConstLoc: Loc    = newSystemLoc("URIErrConst", Recent)

  /* error prototype object */
  val ErrProtoLoc:Loc       = newSystemLoc("ErrProto", Recent)
  val EvalErrProtoLoc:Loc   = newSystemLoc("EvalErrProto", Recent)
  val RangeErrProtoLoc: Loc = newSystemLoc("RangeErrProto", Recent)
  val RefErrProtoLoc: Loc   = newSystemLoc("RefErrProto", Recent)
  val SyntaxErrProtoLoc: Loc= newSystemLoc("SyntaxErrProto", Recent)
  val TypeErrProtoLoc: Loc  = newSystemLoc("TypeErrProto", Recent)
  val URIErrProtoLoc: Loc   = newSystemLoc("URIErrProto", Recent)

  /* error instance */
  val ErrLoc: Loc           = newSystemLoc("Err", Old)
  val EvalErrLoc: Loc       = newSystemLoc("EvalErr", Old)
  val RangeErrLoc: Loc      = newSystemLoc("RangeErr", Old)
  val RefErrLoc: Loc        = newSystemLoc("RefErr", Old)
  val SyntaxErrLoc: Loc     = newSystemLoc("SyntaxErr", Old)
  val TypeErrLoc: Loc       = newSystemLoc("TypeErr", Old)
  val URIErrLoc: Loc        = newSystemLoc("URIErr", Old)

  private val prop_err_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("Error.constructor")),
    ("@construct",               AbsInternalFunc("Error.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )
  private val prop_err_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(ErrConstLoc, F, F, F)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(AbsString.alpha("Error"), T, F, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, F, T)))),
    ("toString",             AbsBuiltinFunc("Error.prototype.toString", 0))
  )
  private val prop_err_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_eval_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("EvalError.constructor")),
    ("@construct",               AbsInternalFunc("EvalError.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(EvalErrProtoLoc, F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )
  private val prop_eval_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(EvalErrConstLoc, F, F, F)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(AbsString.alpha("EvalError"), T, F, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, F, T))))
  )
  private val prop_eval_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(EvalErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_range_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("RangeError.constructor")),
    ("@construct",               AbsInternalFunc("RangeError.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(RangeErrProtoLoc, F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )
  private val prop_range_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(RangeErrConstLoc, F, F, F)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(AbsString.alpha("RangeError"), T, F, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, F, T))))
  )
  private val prop_range_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(RangeErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_ref_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("ReferenceError.constructor")),
    ("@construct",               AbsInternalFunc("ReferenceError.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(RefErrProtoLoc, F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )
  private val prop_ref_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(RefErrConstLoc, F, F, F)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(AbsString.alpha("ReferenceError"), T, F, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, F, T))))
  )
  private val prop_ref_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(RefErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_sytax_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("SyntaxError.constructor")),
    ("@construct",               AbsInternalFunc("SyntaxError.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(SyntaxErrProtoLoc, F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )
  private val prop_sytax_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(SyntaxErrConstLoc, F, F, F)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(AbsString.alpha("SyntaxError"), T, F, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, F, T))))
  )
  private val prop_sytax_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(SyntaxErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_type_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("TypeError.constructor")),
    ("@construct",               AbsInternalFunc("TypeError.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(TypeErrProtoLoc, F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )
  private val prop_type_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(TypeErrConstLoc, F, F, F)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(AbsString.alpha("TypeError"), T, F, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, F, T))))
  )
  private val prop_typ_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TypeErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_uri_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("URIError.constructor")),
    ("@construct",               AbsInternalFunc("URIError.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(URIErrProtoLoc, F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )
  private val prop_uri_proro: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(URIErrConstLoc, F, F, F)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(AbsString.alpha("URIError"), T, F, T)))),
    ("message",              AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, F, T))))
  )
  private val prop_uri_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Error")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(URIErrProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ErrConstLoc, prop_err_const),        (ErrProtoLoc, prop_err_proto),        (ErrLoc, prop_err_ins),
    (EvalErrConstLoc, prop_eval_const),   (EvalErrProtoLoc, prop_eval_proto),   (EvalErrLoc, prop_eval_ins),
    (RangeErrConstLoc, prop_range_const), (RangeErrProtoLoc, prop_range_proto), (RangeErrLoc, prop_range_ins),
    (RefErrConstLoc, prop_ref_const),     (RefErrProtoLoc, prop_ref_proto),     (RefErrLoc, prop_ref_ins),
    (SyntaxErrConstLoc, prop_sytax_const),(SyntaxErrProtoLoc, prop_sytax_proto),(SyntaxErrLoc, prop_sytax_ins),
    (TypeErrConstLoc, prop_type_const),   (TypeErrProtoLoc, prop_type_proto),   (TypeErrLoc, prop_typ_ins),
    (URIErrConstLoc, prop_uri_const),     (URIErrProtoLoc, prop_uri_proro),     (URIErrLoc, prop_uri_ins)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Error"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = ErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("Error.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2

          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              lset_this.foldLeft(h)((_h, l) => _h.update(l, h(l).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue)))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(lset_this)), ctx), (he, ctxe))
        })),
      ("Error.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val s_empty = AbsString.alpha("")
          val v_name = lset_this.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h, l, AbsString.alpha("name")))
          val v_msg = lset_this.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h, l, AbsString.alpha("message")))
          val s_1 =
            if (v_name._1._1 </ UndefBot)
              AbsString.alpha("Error")
            else
              StrBot
          val s_2 = Helper.toString(PValue(UndefBot, v_name._1._2, v_name._1._3, v_name._1._4, v_name._1._5))
          val s_name = s_1 + s_2
          val s_3 =
            if (v_msg._1._1 </ UndefBot)
              s_empty
            else
              StrBot
          val s_4 = Helper.toString(PValue(UndefBot, v_msg._1._2, v_msg._1._3, v_msg._1._4, v_msg._1._5))
          val s_msg = s_3 + s_4
          val s_5 =
            if (s_empty <= s_name)
              s_msg
            else
              StrBot
          val s_6 =
            if (s_empty <= s_msg)
              s_name
            else
              StrBot
          val s_7 = Operator.bopPlus(Operator.bopPlus(Value(s_name), Value(AbsString.alpha(": "))), Value(s_msg))._1._5
          val s_ret = s_5 + s_6 + s_7

          if (s_ret </ StrBot)
            ((Helper.ReturnStore(h, Value(s_ret)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("EvalError"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = EvalErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("EvalError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = EvalErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),

      ("RangeError" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = RangeErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("RangeError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = RangeErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("ReferenceError" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = RefErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("ReferenceError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = RefErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),

      ("SyntaxError" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = SyntaxErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("SyntaxError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = SyntaxErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),

      ("TypeError" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = TypeErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("TypeError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = TypeErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),

      ("URIError" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = URIErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        })),
      ("URIError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val l_e = URIErrLoc
          val h_1 =
            if (Value(PValue(UndefBot, v_arg._1._2, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2) </ ValueBot) {
              val s = Helper.toString(Helper.toPrimitive_better(h, v_arg))
              h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
            }
            else
              HeapBot
          val h_2 =
            if (v_arg._1._1 </ UndefBot)
              h
            else
              HeapBot
          ((Helper.ReturnStore(h_1 + h_2, Value(l_e)), ctx), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Error.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val l_e = ErrLoc
          val s = PreHelper.toString(PreHelper.toPrimitive(v_arg))
          val h_1 =
            h.update(l_e, h(ErrLoc).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(l_e)), ctx), (he, ctxe))
        })),
      ("Error.prototype.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          val s_empty = AbsString.alpha("")
          val v_name = lset_this.foldLeft(ValueBot)((v, l) => v + PreHelper.Proto(h, l, AbsString.alpha("name")))
          val v_msg = lset_this.foldLeft(ValueBot)((v, l) => v + PreHelper.Proto(h, l, AbsString.alpha("message")))
          val s_1 =
            if (v_name._1._1 </ UndefBot)
              AbsString.alpha("Error")
            else
              StrBot
          val s_2 = PreHelper.toString(PValue(UndefBot, v_name._1._2, v_name._1._3, v_name._1._4, v_name._1._5))
          val s_name = s_1 + s_2
          val s_3 =
            if (v_msg._1._1 </ UndefBot)
              s_empty
            else
              StrBot
          val s_4 = PreHelper.toString(PValue(UndefBot, v_msg._1._2, v_msg._1._3, v_msg._1._4, v_msg._1._5))
          val s_msg = s_3 + s_4
          val s_5 =
            if (s_empty <= s_name)
              s_msg
            else
              StrBot
          val s_6 =
            if (s_empty <= s_msg)
              s_name
            else
              StrBot
          val s_7 = Operator.bopPlus(Operator.bopPlus(Value(s_name), Value(AbsString.alpha(": "))), Value(s_msg))._1._5
          val s_ret = s_5 + s_6 + s_7

          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(s_ret)), ctx), (he, ctxe))
        })),
      ("EvalError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val l_e = EvalErrLoc
          val s = PreHelper.toString(PreHelper.toPrimitive(v_arg))
          val h_1 = h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(l_e)), ctx), (he, ctxe))
        })),
      ("RangeError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val l_e = RangeErrLoc
          val s = PreHelper.toString(PreHelper.toPrimitive(v_arg))
          val h_1 = h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(l_e)), ctx), (he, ctxe))
        })),
      ("ReferenceError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val l_e = RefErrLoc
          val s = PreHelper.toString(PreHelper.toPrimitive(v_arg))
          val h_1 = h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(l_e)), ctx), (he, ctxe))
        })),
      ("SyntaxError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val l_e = SyntaxErrLoc
          val s = PreHelper.toString(PreHelper.toPrimitive(v_arg))
          val h_1 = h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(l_e)), ctx), (he, ctxe))
        })),
      ("TypeError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val l_e = TypeErrLoc
          val s = PreHelper.toString(PreHelper.toPrimitive(v_arg))
          val h_1 = h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(l_e)), ctx), (he, ctxe))
        })),
      ("URIError.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_arg = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val l_e = URIErrLoc
          val s = PreHelper.toString(PreHelper.toPrimitive(v_arg))
          val h_1 = h.update(l_e, h(l_e).update("message", PropValue(ObjectValue(s,BoolTrue,BoolFalse,BoolTrue))))
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(l_e)), ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("Error" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((ErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("Error.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((ErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("Error.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("EvalError" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((EvalErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("EvalError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((EvalErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("RangeError" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((RangeErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("RangeError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((RangeErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("ReferenceError" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((RefErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("ReferenceError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((RefErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("SyntaxError" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((SyntaxErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("SyntaxError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((SyntaxErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("TypeError" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((TypeErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("TypeError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((TypeErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("URIError"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((URIErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        })),
      ("URIError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 =
            if (Value(PValue(UndefBot, v._1._2, v._1._3, v._1._4, v._1._5), v._2) </ ValueBot)
              LPSet((URIErrLoc, "message"))
            else LPBot
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("Error.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = LPSet((ErrLoc, "message"))
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0")
        })),
      ("Error.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ AH.Proto_use(h, l, AbsString.alpha("name")))
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ AH.Proto_use(h, l, AbsString.alpha("message")))
          val LP3 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@this")
        })),
      ("EvalError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = LPSet((EvalErrLoc, "message"))
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0")
        })),
      ("RangeError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = LPSet((RangeErrLoc, "message"))
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0")
        })),
      ("ReferenceError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = LPSet((RefErrLoc, "message"))
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0")
        })),
      ("SyntaxError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = LPSet((SyntaxErrLoc, "message"))
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0")
        })),
      ("TypeError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = LPSet((TypeErrLoc, "message"))
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0")
        })),
      ("URIError.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = LPSet((URIErrLoc, "message"))
          val LP2 = LPSet((SinglePureLocalLoc, "@return"))
          LP1 ++ LP2 ++ getArgValue_use(h, ctx, args, "0")
        }))
    )
  }
}
