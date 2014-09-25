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

import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinArray
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context

object TIZENdatasync extends Tizen {
  private val name = "datasync"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_datasync
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_syncinfo: Loc = newSystemLoc("SyncInfo", Old)
  val loc_syncservinfo: Loc = newSystemLoc("SyncServiceInfo", Old)
  val loc_syncservinfoarr: Loc = newSystemLoc("SyncServiceInfoArr", Old)
  val loc_syncstats: Loc = newSystemLoc("SyncStatistics", Old)

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
    ("add", AbsBuiltinFunc("tizen.datasync.add", 1)),
    ("update", AbsBuiltinFunc("tizen.datasync.update", 1)),
    ("remove", AbsBuiltinFunc("tizen.datasync.remove", 1)),
    ("getMaxProfilesNum", AbsBuiltinFunc("tizen.datasync.getMaxProfilesNum", 0)),
    ("getProfilesNum", AbsBuiltinFunc("tizen.datasync.getProfilesNum", 0)),
    ("get", AbsBuiltinFunc("tizen.datasync.get", 1)),
    ("getAll", AbsBuiltinFunc("tizen.datasync.getAll", 0)),
    ("startSync", AbsBuiltinFunc("tizen.datasync.startSync", 2)),
    ("stopSync", AbsBuiltinFunc("tizen.datasync.stopSync", 1)),
    ("getLastSyncStatistics", AbsBuiltinFunc("tizen.datasync.getLastSyncStatistics", 1))
  )

  private val prop_syncinfo_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSyncInfo.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("url", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("password", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("mode", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("MANUAL") + AbsString.alpha("PERIODIC") +
                                                       AbsString.alpha("PUSH")), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, AbsString.alpha("TWO_WAY") +
                                                       AbsString.alpha("SLOW") + AbsString.alpha("ONE_WAY_FROM_CLIENT") +
                                                       AbsString.alpha("REFRESH_FROM_CLIENT") + AbsString.alpha("ONE_WAY_FROM_SERVER") +
                                                       AbsString.alpha("REFRESH_FROM_SERVER"))), T, T, T)))),
    ("interval", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, AbsString.alpha("5_MINUTES") +
                                                       AbsString.alpha("15_MINUTES") + AbsString.alpha("1_HOUR") +
                                                       AbsString.alpha("4_HOURS") + AbsString.alpha("12_HOURS") +
                                                       AbsString.alpha("1_DAY") + AbsString.alpha("1_WEEK") +
                                                       AbsString.alpha("1_MONTH"))), T, T, T))))
  )

  private val prop_syncservinfoarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_syncservinfo), T, T, T))))
  )

  private val prop_syncservinfo_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSyncServiceInfo.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("enable", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("serviceType", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("CONTACT") + AbsString.alpha("EVENT")), T, T, T)))),
    ("serverDatabaseUri", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T)))),
    ("password", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))))
  )

  private val prop_syncstats_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSyncStatistics.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("syncStatus", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("SUCCESS") + AbsString.alpha("FAIL") +
                                                             AbsString.alpha("STOP") + AbsString.alpha("NONE")), F, T, T)))),
    ("serviceType", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("CONTACT") + AbsString.alpha("EVENT")), F, T, T)))),
    ("lastSyncTime", AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T)))),
    ("serverToClientTotal", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("serverToClientAdded", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("serverToClientUpdated", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("serverToClientRemoved", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("clientToServerTotal", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("clientToServerAdded", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("clientToServerUpdated", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("clientToServerRemoved", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T))))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_syncinfo, prop_syncinfo_ins), (loc_syncservinfo, prop_syncservinfo_ins),
    (loc_syncservinfoarr, prop_syncservinfoarr_ins), (loc_syncstats, prop_syncstats_ins)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.datasync.add" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            AbsNumber.getUIntSingle(n_arglen) match {
	      case Some(n) if n == 0 =>
                Set[WebAPIException](TypeMismatchError)
              case _ => TizenHelper.TizenExceptionBot
            }
          val (b_1, es_1) = TizenHelper.instanceOf(h, v, Value(TIZENSyncProfileInfo.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val h_1 = v._2.foldLeft(h)((_h, l) => {
            Helper.PropStore(_h, l, AbsString.alpha("profileId"), Value(StrTop))
          })
          val est = Set[WebAPIException](SecurityError, UnknownError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h_1, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.update" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            AbsNumber.getUIntSingle(n_arglen) match {
	      case Some(n) if n == 0 =>
                Set[WebAPIException](TypeMismatchError)
              case _ => TizenHelper.TizenExceptionBot
            }
          val (b_1, es_1) = TizenHelper.instanceOf(h, v, Value(TIZENSyncProfileInfo.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, UnknownError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.remove" -> (
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
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, UnknownError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.getMaxProfilesNum" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.getProfilesNum" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.get" -> (
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
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENSyncProfileInfo.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("profileId", PropValue(ObjectValue(Value(v._1._5), F, T, T))).
            update("profileName", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("syncInfo", PropValue(ObjectValue(Value(TIZENdatasync.loc_syncinfo), F, T, T))).
            update("serviceInfo", PropValue(ObjectValue(Value(TIZENdatasync.loc_syncservinfoarr), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.getAll" -> (
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

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENSyncProfileInfo.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("profileId", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("profileName", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("syncInfo", PropValue(ObjectValue(Value(TIZENdatasync.loc_syncinfo), F, T, T))).
            update("serviceInfo", PropValue(ObjectValue(Value(TIZENdatasync.loc_syncservinfoarr), F, T, T)))
          val h_3 = h_2.update(l_r1, o_new)
          val o_arr = Helper.NewArrayObject(UInt)
          val o_arr2 = o_arr.update(Str_default_number, PropValue(ObjectValue(Value(l_r1), T, T, T)))
          val h_4 = h_3.update(l_r2, o_arr2)
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_4, Value(l_r2)), ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.startSync" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val l_r4 = addrToLoc(addr4, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
          val v_1 = getArgValue(h_4, ctx_4, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_4, ctx_4, args, "length"))
          val es_1 =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_4, ctx_4, args, "1")
              val (h_5, es) = v_2._2.foldLeft((h_4, TizenHelper.TizenExceptionBot))((_he, l) => {
                val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onprogress"))
                val v2 = Helper.Proto(_he._1, l, AbsString.alpha("oncompleted"))
                val v3 = Helper.Proto(_he._1, l, AbsString.alpha("onstopped"))
                val v4 = Helper.Proto(_he._1, l, AbsString.alpha("onfailed"))
                val es1 =
                  if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es2 =
                  if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es3 =
                  if (v3._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es4 =
                  if (v4._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(5)).
                  update("0", PropValue(ObjectValue(Value(v_1._1._5), T, T, T))).
                  update("1", PropValue(ObjectValue(Value(AbsString.alpha("CONTACT") + AbsString.alpha("EVENT")), T, T, T))).
                  update("2", PropValue(ObjectValue(Value(BoolTop), T, T, T))).
                  update("3", PropValue(ObjectValue(Value(UInt), T, T, T))).
                  update("4", PropValue(ObjectValue(Value(UInt), T, T, T)))
                val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(v_1._1._5), T, T, T)))
                val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(v_1._1._5), T, T, T)))
                val o_arr4 = Helper.NewArrayObject(AbsNumber.alpha(2)).
                  update("0", PropValue(ObjectValue(Value(v_1._1._5), T, T, T))).
                  update("1", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                val h_5 = _he._1.
                  update(l_r1, o_arr1).
                  update(l_r2, o_arr2).
                  update(l_r3, o_arr3).
                  update(l_r4, o_arr4)
                val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("SyncProgressCB.onprogress"), Value(v1._2), Value(l_r1))
                val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("SyncProgressCB.oncompleted"), Value(v2._2), Value(l_r2))
                val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("SyncProgressCB.onstopped"), Value(v3._2), Value(l_r3))
                val h_9 = TizenHelper.addCallbackHandler(h_8, AbsString.alpha("SyncProgressCB.onfailed"), Value(v4._2), Value(l_r4))
                (h_9, _he._2 ++ es1 ++ es2 ++ es3 ++ es4)
              })
              (h_5, es)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_4), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.stopSync" -> (
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
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.datasync.getLastSyncStatistics" -> (
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
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(UInt)
          val o_arr2 = o_arr.update(Str_default_number, PropValue(ObjectValue(Value(TIZENdatasync.loc_syncstats), T, T, T)))
          val h_2 = h_1.update(l_r1, o_arr2)
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError, NotFoundError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSyncStatistics extends Tizen {
  private val name = "SyncStatistics"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  override def getSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
