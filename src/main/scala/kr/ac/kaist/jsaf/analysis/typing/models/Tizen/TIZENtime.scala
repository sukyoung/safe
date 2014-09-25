/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
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
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue


object TIZENtime extends Tizen {
  private val name = "time"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_time
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_tzdate: Loc        = newSystemLoc("TZDate", Old)
  val loc_timeduration: Loc = newSystemLoc("TimeDuration", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_tzdate, prop_tzdate_ins), (loc_timeduration, prop_timeduration_ins)
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
    ("getCurrentDateTime", AbsBuiltinFunc("tizen.time.getCurrentDateTime",4)),
    ("getLocalTimezone", AbsBuiltinFunc("tizen.time.getLocalTimezone",4)),
    ("getAvailableTimezones", AbsBuiltinFunc("tizen.time.getAvailableTimezones",4)),
    ("getDateFormat", AbsBuiltinFunc("tizen.time.getDateFormat",4)),
    ("getTimeFormat", AbsBuiltinFunc("tizen.time.getTimeFormat",4)),
    ("isLeapYear", AbsBuiltinFunc("tizen.time.isLeapYear",4))
  )

  private val prop_tzdate_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENTZDate.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_timeduration_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENTimeDuration.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    ("unit", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("MSECS") + AbsString.alpha("SECS") +
      AbsString.alpha("MINS") + AbsString.alpha("HOURS") + AbsString.alpha("DAYS")), T, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.time.getCurrentDateTime" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(TIZENtime.loc_tzdate)), ctx),(he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.time.getLocalTimezone" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx),(he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.time.getAvailableTimezones" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val o_arr = Helper.NewArrayObject(UInt).
            update(Str_default_number, PropValue(ObjectValue(Value(StrTop), T, T, T)))
          val h_2 = h_1.update(l_r1, o_arr)
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1),(he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.time.getDateFormat" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n >= 1 =>
              val v = getArgValue(h, ctx, args, "0")
              val es_1 =
                if (v._1._3 </ BoolTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              es_1
            case _ => TizenHelper.TizenExceptionBot
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx),(he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.time.getTimeFormat" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx),(he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.time.isLeapYear" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es_1 =
            if (v._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx),(he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
