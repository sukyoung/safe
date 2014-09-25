/*******************************************************************************
    Copyright (c) 2013-2014, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing._


object TIZENpower extends Tizen {
  private val name = "power"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_power
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto)
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
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("request", AbsBuiltinFunc("tizen.power.request",2)),
    ("release", AbsBuiltinFunc("tizen.power.release",1)),
    ("setScreenStateChangeListener", AbsBuiltinFunc("tizen.power.setScreenStateChangeListener",1)),
    ("unsetScreenStateChangeListener", AbsBuiltinFunc("tizen.power.unsetScreenStateChangeListener",0)),
    ("getScreenBrightness", AbsBuiltinFunc("tizen.power.getScreenBrightness",0)),
    ("setScreenBrightness", AbsBuiltinFunc("tizen.power.setScreenBrightness",1)),
    ("isScreenOn", AbsBuiltinFunc("tizen.power.isScreenOn",0)),
    ("restoreScreenBrightness", AbsBuiltinFunc("tizen.power.restoreScreenBrightness",0)),
    ("turnScreenOn", AbsBuiltinFunc("tizen.power.turnScreenOn",0)),
    ("turnScreenOff", AbsBuiltinFunc("tizen.power.turnScreenOff",0))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.power.request" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            AbsNumber.getUIntSingle(n_arglen) match {
	      case Some(n) if n == 0 =>
                Set[WebAPIException](TypeMismatchError)
              case _ => TizenHelper.TizenExceptionBot
            }
          val es_1 =
            if (v_1._1._5 != AbsString.alpha("SCREEN") && v_1._1._5 != AbsString.alpha("CPU"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._1._5 != AbsString.alpha("SCREEN_OFF") && v_1._1._5 != AbsString.alpha("SCREEN_DIM") &&
              v_1._1._5 != AbsString.alpha("SCREEN_NORMAL") && v_1._1._5 != AbsString.alpha("SCREEN_BRIGHT") &&
              v_1._1._5 != AbsString.alpha("CPU_AWAKE"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, UnknownError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.release" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            AbsNumber.getUIntSingle(n_arglen) match {
	      case Some(n) if n == 0 =>
                Set[WebAPIException](TypeMismatchError)
              case _ => TizenHelper.TizenExceptionBot
            }
          val es_1 =
            if (v._1._5 != AbsString.alpha("SCREEN") && v._1._5 != AbsString.alpha("CPU"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.setScreenStateChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es =
            AbsNumber.getUIntSingle(n_arglen) match {
	      case Some(n) if n == 0 =>
                Set[WebAPIException](TypeMismatchError)
              case _ => TizenHelper.TizenExceptionBot
            }
          val es_1 =
            if (v._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Obj.empty.
            update("0", PropValue(ObjectValue(Value(StrTop), T, T, T))).
            update("1", PropValue(ObjectValue(Value(StrTop), T, T, T)))
          val h_2 = h_1.update(l_r1, o_arr)
          val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("ScreenStateChangeCB"), Value(v._2), Value(l_r1))
          val est = Set[WebAPIException](UnknownError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_3, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.unsetScreenStateChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.getScreenBrightness" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.setScreenBrightness" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            AbsNumber.getUIntSingle(n_arglen) match {
	      case Some(n) if n == 0 =>
                Set[WebAPIException](TypeMismatchError)
              case _ => TizenHelper.TizenExceptionBot
            }
          val es_1 =
            if (v._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError, InvalidValuesError, SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.isScreenOn" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.restoreScreenBrightness" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.turnScreenOn" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.power.turnScreenOff" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
