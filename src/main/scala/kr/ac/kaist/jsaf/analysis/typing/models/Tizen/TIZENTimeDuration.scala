/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._

import kr.ac.kaist.jsaf.analysis.typing.models.AbsInternalFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc

object TIZENTimeDuration extends Tizen {
  private val name = "TimeDuration"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.TimeDuration.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("difference", AbsBuiltinFunc("tizen.TimeDuration.difference", 1)),
    ("equalsTo", AbsBuiltinFunc("tizen.TimeDuration.equalsTo", 1)),
    ("lessThan", AbsBuiltinFunc("tizen.TimeDuration.lessThan", 1)),
    ("greaterThan", AbsBuiltinFunc("tizen.TimeDuration.greaterThan", 1))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
       ("tizen.TimeDuration.constructor" -> (
         (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
           val lset_env = h(SinglePureLocalLoc)("@env")._2._2
           val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
           if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
           val addr_env = (cp._1._1, set_addr.head)
           val addr1 = cfg.getAPIAddress(addr_env, 0)
           val l_r1 = addrToLoc(addr1, Recent)
           val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
           val v_1 = getArgValue(h_1, ctx_1, args, "0")
           val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

           val o_new = Obj.empty.
             update("@class", PropValue(AbsString.alpha("Object"))).
             update("@proto", PropValue(ObjectValue(Value(TIZENTimeDuration.loc_proto), F, F, F))).
             update("@extensible", PropValue(T))

           val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
             case Some(n) if n == 1 =>
               val o_new2 = o_new.
                 update("length", PropValue(ObjectValue(Value(v_1._1._4), T, T, T))).
                 update("unit", PropValue(ObjectValue(Value(AbsString.alpha("MSECS")), T, T, T)))
               val h_2 = h_1.update(l_r1, o_new2)
               (h_2, TizenHelper.TizenExceptionBot)
             case Some(n) if n >= 2 =>
               val v_2 = getArgValue(h_1, ctx_1, args, "1")
               val es =
                 if (v_2._1._5 != AbsString.alpha("MSECS") && v_2._1._5 != AbsString.alpha("SECS") &&
                   v_2._1._5 != AbsString.alpha("MINS") && v_2._1._5 != AbsString.alpha("HOURS") && v_2._1._5 != AbsString.alpha("DAYS"))
                   Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_new2 = o_new.
                 update("length", PropValue(ObjectValue(Value(v_1._1._4), T, T, T))).
                 update("unit", PropValue(ObjectValue(Value(v_2._1._5), T, T, T)))
               val h_2 = h_1.update(l_r1, o_new2)
               (h_2, es)
             case _ => (h_1, TizenHelper.TizenExceptionBot)
           }
           val est = Set[WebAPIException](UnknownError, NotSupportedError)
           val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
           ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
         }
         )),
      ("tizen.TimeDuration.difference" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          //val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val (b_1, es_1) = TizenHelper.instanceOf(h_1, v_1, Value(TIZENTimeDuration.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENTimeDuration.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("length", PropValue(ObjectValue(Value(NumTop), T, T, T))).
            update("unit", PropValue(ObjectValue(Value(AbsString.alpha("MSECS") + AbsString.alpha("SECS") +
            AbsString.alpha("MINS") + AbsString.alpha("HOURS") + AbsString.alpha("DAYS")), T, T, T)))

          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TimeDuration.equalsTo" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          //val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(TIZENTimeDuration.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TimeDuration.lessThan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          //val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(TIZENTimeDuration.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.TimeDuration.greaterThan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          //val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(TIZENTimeDuration.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
