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

import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context

object TIZENpush extends Tizen {
  private val name = "push"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_push
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_pushmsg: Loc = newSystemLoc("PushMessage", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_pushmsg, prop_pushmsg_ins)
  )
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T)))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("registerService", AbsBuiltinFunc("tizen.push.registerService",3)),
    ("unregisterService", AbsBuiltinFunc("tizen.push.unregisterService",2)),
    ("connectService", AbsBuiltinFunc("tizen.push.connectService",1)),
    ("disconnectService", AbsBuiltinFunc("tizen.push.disconnectService",0)),
    ("getRegistrationId", AbsBuiltinFunc("tizen.push.getRegistrationId",0))
  )

  private val prop_pushmsg_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENNotification.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("appData", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("alertMessage", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("date", AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), T, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.push.registerService" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val v_2 = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENApplicationControl.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_3 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("PushRegisterSuccessCB"), Value(v_2._2), Value(l_r1))

          val (h_5, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 => (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es_4 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es_4)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError, SecurityError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ es_4 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.push.unregisterService" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 => (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 1 =>
              val v_1 = getArgValue(h_1, ctx_1, args, "0")
              val es1 =
                if (v_1._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_1._2), Value(UndefTop))
              (h_2, es1)
            case Some(n) if n >= 2 =>
              val v_1 = getArgValue(h_1, ctx_1, args, "0")
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es1 =
                if (v_1._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_1._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r1))
              (h_4, es1 ++ es2)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError, SecurityError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.push.connectService" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENpush.loc_pushmsg), T, T, T)))
          val h_2 = h_1.update(l_r1, o_arr)
          val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("PushNotiCB"), Value(v_1._2), Value(l_r1))
          val est = Set[WebAPIException](UnknownError, NotSupportedError, SecurityError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((h_3, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.push.disconnectService" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.push.getRegistrationId" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
