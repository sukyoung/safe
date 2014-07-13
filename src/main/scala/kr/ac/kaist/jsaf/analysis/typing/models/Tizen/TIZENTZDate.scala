/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsInternalFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinDate

object TIZENTZDate extends Tizen {
  private val name = "TZDate"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")


  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.TZDate.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("getDate", AbsBuiltinFunc("tizen.TZDate.getDate", 0)),
    ("setDate", AbsBuiltinFunc("tizen.TZDate.setDate", 1)),
    ("getDay", AbsBuiltinFunc("tizen.TZDate.getDay", 0)),
    ("getFullYear", AbsBuiltinFunc("tizen.TZDate.getFullYear", 0)),
    ("setFullYear", AbsBuiltinFunc("tizen.TZDate.setFullYear", 1)),
    ("getHours", AbsBuiltinFunc("tizen.TZDate.getHours", 0)),
    ("setHours", AbsBuiltinFunc("tizen.TZDate.setHours", 1)),
    ("getMilliseconds", AbsBuiltinFunc("tizen.TZDate.getMilliseconds", 0)),
    ("setMilliseconds", AbsBuiltinFunc("tizen.TZDate.setMilliseconds", 1)),
    ("getMinutes", AbsBuiltinFunc("tizen.TZDate.getMinutes", 0)),
    ("setMinutes", AbsBuiltinFunc("tizen.TZDate.setMinutes", 1)),
    ("getMonth", AbsBuiltinFunc("tizen.TZDate.getMonth", 0)),
    ("setMonth", AbsBuiltinFunc("tizen.TZDate.setMonth", 1)),
    ("getSeconds", AbsBuiltinFunc("tizen.TZDate.getSeconds", 0)),
    ("setSeconds", AbsBuiltinFunc("tizen.TZDate.setSeconds", 1)),
    ("getUTCDate", AbsBuiltinFunc("tizen.TZDate.getUTCDate", 0)),
    ("setUTCDate", AbsBuiltinFunc("tizen.TZDate.setUTCDate", 1)),
    ("getUTCDay", AbsBuiltinFunc("tizen.TZDate.getUTCDay", 0)),
    ("getUTCFullYear", AbsBuiltinFunc("tizen.TZDate.getUTCFullYear", 0)),
    ("setUTCFullYear", AbsBuiltinFunc("tizen.TZDate.setUTCFullYear", 1)),
    ("getUTCHours", AbsBuiltinFunc("tizen.TZDate.getUTCHours", 0)),
    ("setUTCHours", AbsBuiltinFunc("tizen.TZDate.setUTCHours", 0)),
    ("getUTCMilliseconds", AbsBuiltinFunc("tizen.TZDate.getUTCMilliseconds", 0)),
    ("setUTCMilliseconds", AbsBuiltinFunc("tizen.TZDate.setUTCMilliseconds", 1)),
    ("getUTCMinutes", AbsBuiltinFunc("tizen.TZDate.getUTCMinutes", 0)),
    ("setUTCMinutes", AbsBuiltinFunc("tizen.TZDate.setUTCMinutes", 1)),
    ("getUTCMonth", AbsBuiltinFunc("tizen.TZDate.getUTCMonth", 0)),
    ("setUTCMonth", AbsBuiltinFunc("tizen.TZDate.setUTCMonth", 1)),
    ("getUTCSeconds", AbsBuiltinFunc("tizen.TZDate.getUTCSeconds", 0)),
    ("setUTCSeconds", AbsBuiltinFunc("tizen.TZDate.setUTCSeconds", 1)),
    ("getTimezone", AbsBuiltinFunc("tizen.TZDate.getTimezone", 0)),
    ("toTimezone", AbsBuiltinFunc("tizen.TZDate.toTimezone", 1)),
    ("toLocalTimezone", AbsBuiltinFunc("tizen.TZDate.toLocalTimezone", 0)),
    ("toUTC", AbsBuiltinFunc("tizen.TZDate.toUTC", 0)),
    ("difference", AbsBuiltinFunc("tizen.TZDate.difference", 1)),
    ("equalsTo", AbsBuiltinFunc("tizen.TZDate.equalsTo", 1)),
    ("earlierThan", AbsBuiltinFunc("tizen.TZDate.earlierThan", 1)),
    ("laterThan", AbsBuiltinFunc("tizen.TZDate.laterThan", 1)),
    ("addDuration", AbsBuiltinFunc("tizen.TZDate.addDuration", 1)),
    ("toLocaleDateString", AbsBuiltinFunc("tizen.TZDate.toLocaleDateString", 0)),
    ("toLocaleTimeString", AbsBuiltinFunc("tizen.TZDate.toLocaleTimeString", 0)),
    ("toLocaleString", AbsBuiltinFunc("tizen.TZDate.toLocaleString", 0)),
    ("toDateString", AbsBuiltinFunc("tizen.TZDate.toDateString", 0)),
    ("toTimeString", AbsBuiltinFunc("tizen.TZDate.toTimeString", 0)),
    ("toString", AbsBuiltinFunc("tizen.TZDate.toString", 0)),
    ("getTimezoneAbbreviation", AbsBuiltinFunc("tizen.TZDate.getTimezoneAbbreviation", 0)),
    ("secondsFromUTC", AbsBuiltinFunc("tizen.TZDate.secondsFromUTC", 0)),
    ("isDST", AbsBuiltinFunc("tizen.TZDate.isDST", 0)),
    ("getPreviousDSTTransition", AbsBuiltinFunc("tizen.TZDate.getPreviousDSTTransition", 0)),
    ("getNextDSTTransition", AbsBuiltinFunc("tizen.TZDate.getNextDSTTransition", 0))
  )



  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.TZDate.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))

          val (h_1, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              (h, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 1 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2)
            case Some(n) if n == 2 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val v_2 = getArgValue(h, ctx, args, "1")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_2._1._4 </ NumTop && v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3)
            case Some(n) if n == 3 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val v_2 = getArgValue(h, ctx, args, "1")
              val v_3 = getArgValue(h, ctx, args, "2")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_2._1._4 </ NumTop && v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_3._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3 ++ es_4)
            case Some(n) if n == 4 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val v_2 = getArgValue(h, ctx, args, "1")
              val v_3 = getArgValue(h, ctx, args, "2")
              val v_4 = getArgValue(h, ctx, args, "3")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_2._1._4 </ NumTop && v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_3._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_5 =
                if (v_4._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5)
            case Some(n) if n == 5 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val v_2 = getArgValue(h, ctx, args, "1")
              val v_3 = getArgValue(h, ctx, args, "2")
              val v_4 = getArgValue(h, ctx, args, "3")
              val v_5 = getArgValue(h, ctx, args, "4")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_2._1._4 </ NumTop && v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_3._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_5 =
                if (v_4._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_6 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6)
            case Some(n) if n == 6 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val v_2 = getArgValue(h, ctx, args, "1")
              val v_3 = getArgValue(h, ctx, args, "2")
              val v_4 = getArgValue(h, ctx, args, "3")
              val v_5 = getArgValue(h, ctx, args, "4")
              val v_6 = getArgValue(h, ctx, args, "5")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_2._1._4 </ NumTop && v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_3._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_5 =
                if (v_4._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_6 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_7 =
                if (v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7)
            case Some(n) if n == 7 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val v_2 = getArgValue(h, ctx, args, "1")
              val v_3 = getArgValue(h, ctx, args, "2")
              val v_4 = getArgValue(h, ctx, args, "3")
              val v_5 = getArgValue(h, ctx, args, "4")
              val v_6 = getArgValue(h, ctx, args, "5")
              val v_7 = getArgValue(h, ctx, args, "6")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_2._1._4 </ NumTop && v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_3._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_5 =
                if (v_4._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_6 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_7 =
                if (v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_8 =
                if (v_7._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8)
            case Some(n) if n >= 8 =>
              val v_1 = getArgValue(h, ctx, args, "0")
              val v_2 = getArgValue(h, ctx, args, "1")
              val v_3 = getArgValue(h, ctx, args, "2")
              val v_4 = getArgValue(h, ctx, args, "3")
              val v_5 = getArgValue(h, ctx, args, "4")
              val v_6 = getArgValue(h, ctx, args, "5")
              val v_7 = getArgValue(h, ctx, args, "6")
              val v_8 = getArgValue(h, ctx, args, "7")
              val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(BuiltinDate.ProtoLoc))
              val es_2 =
                if (b_1._1._3 <= F && v_1._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_2._1._4 </ NumTop && v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_3._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_5 =
                if (v_4._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_6 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_7 =
                if (v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_8 =
                if (v_7._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_9 =
                if (v_8._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h, es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENTZDate.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))
          val h_2 = lset_this.foldLeft(h_1)((_h, l) => _h.update(l, o_new))
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h_2, Value(lset_this)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getDate" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setDate" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getDay" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.getFullYear" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setFullYear" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getHours" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setHours" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getMilliseconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setMilliseconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getMinutes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setMinutes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getMonth" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setMonth" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getSeconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setSeconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getUTCDate" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setUTCDate" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getUTCDay" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.getUTCFullYear" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setUTCFullYear" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getUTCHours" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setUTCHours" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getUTCMilliseconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setUTCMilliseconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getUTCMinutes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setUTCMinutes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getUTCMonth" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setUTCMonth" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getUTCSeconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.setUTCSeconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.getTimezone" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.toTimezone" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((Helper.ReturnStore(h, Value(TIZENtime.loc_tzdate)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.toLocalTimezone" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(TIZENtime.loc_tzdate)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.toUTC" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(TIZENtime.loc_tzdate)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.difference" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(TIZENtime.loc_timeduration)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.equalsTo" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v, Value(TIZENTZDate.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.earlierThan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v, Value(TIZENTZDate.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.laterThan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v, Value(TIZENTZDate.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.addDuration" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v, Value(TIZENTimeDuration.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1)
          ((Helper.ReturnStore(h, Value(TIZENtime.loc_tzdate)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TZDate.toLocaleDateString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.toLocaleTimeString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.toLocaleString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.toDateString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.toTimeString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.getTimezoneAbbreviation" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.secondsFromUTC" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.isDST" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.getPreviousDSTTransition" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(PValue(NullTop), LocSet(TIZENtime.loc_tzdate))), ctx), (he, ctxe))
        }
        )),
      ("tizen.TZDate.getNextDSTTransition" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(PValue(NullTop), LocSet(TIZENtime.loc_tzdate))), ctx), (he, ctxe))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}