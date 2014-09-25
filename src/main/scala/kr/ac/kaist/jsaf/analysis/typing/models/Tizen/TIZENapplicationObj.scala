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
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap

object TIZENapplicationObj extends Tizen {
  val name = "application"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_application
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_app: Loc = newSystemLoc("Application", Old)
  val loc_appinfo: Loc = newSystemLoc("ApplicationInformation", Old)
  val loc_appinfoarr: Loc = newSystemLoc("ApplicationInformationArr", Old)
  val loc_appctrl: Loc = newSystemLoc("ApplicationControl", Old)
  val loc_appctrldata: Loc = newSystemLoc("ApplicationControlData", Old)
  val loc_appctrldataarr: Loc = newSystemLoc("ApplicationControlDataArr", Old)
  val loc_appctxt: Loc = newSystemLoc("ApplicationContext", Old)
  val loc_appctxtarr: Loc = newSystemLoc("ApplicationContextArr", Old)
  val loc_strarr: Loc = newSystemLoc("appStrArr", Old)
  val loc_appcert: Loc = newSystemLoc("ApplicationCertificate", Old)
  val loc_appcertarr: Loc = newSystemLoc("ApplicationCertificateArr", Old)
  val loc_reqappctrl: Loc = newSystemLoc("RequestedApplicationControl", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_app, prop_app_ins), (loc_appinfo, prop_appinfo_ins),
    (loc_strarr, prop_strarr_ins), (loc_appctrldata, prop_appctrldata_ins), (loc_appctrldataarr, prop_appctrldataarr_ins),
    (loc_appinfoarr, prop_appinfoarr_ins), (loc_appctrl, prop_appctrl_ins), (loc_appctxt, prop_appctxt_ins),
    (loc_appctxtarr, prop_appctxtarr_ins), (loc_appcert, prop_appcert_ins), (loc_appcertarr, prop_appcertarr_ins),
    (loc_reqappctrl, prop_reqappctrl_ins)
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
    ("getCurrentApplication", AbsBuiltinFunc("tizen.applicationObj.getCurrentApplication",0)),
    ("kill", AbsBuiltinFunc("tizen.applicationObj.kill",3)),
    ("launch", AbsBuiltinFunc("tizen.applicationObj.launch",3)),
    ("launchAppControl", AbsBuiltinFunc("tizen.applicationObj.launchAppControl",5)),
    ("findAppControl", AbsBuiltinFunc("tizen.applicationObj.findAppControl",3)),
    ("getAppsContext", AbsBuiltinFunc("tizen.applicationObj.getAppsContext",2)),
    ("getAppContext", AbsBuiltinFunc("tizen.applicationObj.getAppContext",1)),
    ("getAppsInfo", AbsBuiltinFunc("tizen.applicationObj.getAppsInfo",2)),
    ("getAppInfo", AbsBuiltinFunc("tizen.applicationObj.getAppInfo",1)),
    ("getAppCerts", AbsBuiltinFunc("tizen.applicationObj.getAppCerts",1)),
    ("getAppSharedURI", AbsBuiltinFunc("tizen.applicationObj.getAppSharedURI",1)),
    ("addAppInfoEventListener", AbsBuiltinFunc("tizen.applicationObj.addAppInfoEventListener",1)),
    ("removeAppInfoEventListener", AbsBuiltinFunc("tizen.applicationObj.removeAppInfoEventListener",1))
  )

  private val prop_appinfo_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENApplicationInformation.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("iconPath",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("version",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("show",              AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("categories",              AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("installDate",              AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T)))),
    ("size",              AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("packageId",              AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_appinfoarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_appinfo), T, T, T))))
  )

  private val prop_app_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENApplication.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("appInfo",                 AbsConstValue(PropValue(ObjectValue(Value(loc_appinfo), F, T, T)))),
    ("contextId",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_appctrl_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENApplicationControlData.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("operation",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("uri",                 AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("mime",                 AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("category",                 AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), F, T, T)))),
    ("data",                 AbsConstValue(PropValue(ObjectValue(Value(loc_appctrldataarr), F, T, T))))
  )

  private val prop_appctrldata_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENApplicationControlData.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("key",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("value",                 AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T))))
  )

  private val prop_appctrldataarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_appctrldata), T, T, T))))
  )

  private val prop_appctxt_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENApplicationContext.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("appId",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_appctxtarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_appctxt), T, T, T))))
  )

  private val prop_appcert_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENApplicationCertificate.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("type",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("AUTHOR_ROOT") +
      AbsString.alpha("AUTHOR_INTERMEDIATE") +
      AbsString.alpha("AUTHOR_SIGNER") +
      AbsString.alpha("DISTRIBUTOR_ROOT") +
      AbsString.alpha("DISTRIBUTOR_INTERMEDIATE") +
      AbsString.alpha("DISTRIBUTOR_SIGNER") +
      AbsString.alpha("DISTRIBUTOR2_ROOT") +
      AbsString.alpha("DISTRIBUTOR2_INTERMEDIATE") +
      AbsString.alpha("DISTRIBUTOR2_SIGNER")), F, T, T)))),
    ("value",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_appcertarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_appcert), T, T, T))))
  )

  private val prop_reqappctrl_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENRequestedApplicationControl.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("appControl",                 AbsConstValue(PropValue(ObjectValue(Value(loc_appctrl), F, T, T)))),
    ("callerAppId",                 AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_strarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.applicationObj.getCurrentApplication" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(loc_app)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.kill" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val ctxid = getArgValue(h_1,ctx_1,args,"0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es =
            if (ctxid._1._1 </ UndefBot && ctxid._1._2 </ NullBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          /* register successCallback and errorCallback */
          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1,TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val sucCB = getArgValue(h_1,ctx_1,args,"1")
              val es_2 =
                if (sucCB._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n == 3 =>
              val sucCB = getArgValue(h_1,ctx_1,args,"1")
              val errCB = getArgValue(h_1,ctx_1,args,"2")
              val es_2 =
                if (sucCB._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (errCB._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case _ => {
              (HeapBot, TizenHelper.TizenExceptionBot)
            }
          }
          val est = Set[WebAPIException](SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.launch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val appid = getArgValue(h_1,ctx_1,args,"0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es =
            if (appid._1._1 </ UndefBot && appid._1._2 </ NullBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          /* register successCallback and errorCallback */
          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1,TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val sucCB = getArgValue(h_1,ctx_1,args,"1")
              val es_2 =
                if (sucCB._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n == 3 =>
              val sucCB = getArgValue(h_1,ctx_1,args,"1")
              val errCB = getArgValue(h_1,ctx_1,args,"2")
              val es_2 =
                if (sucCB._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (errCB._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++
                LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case _ => {
              (HeapBot, TizenHelper.TizenExceptionBot)
            }
          }
          val est = Set[WebAPIException](SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.launchAppControl" -> (
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
          val appctrl = getArgValue(h_2,ctx_2,args,"0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_2, appctrl, Value(TIZENApplicationControl.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          /* register successCallback and errorCallback */
          val (h_3, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_2,TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val appid = getArgValue(h_2, ctx_2, args, "1")
              val es =
                if (appid._1._1 </ UndefBot && appid._1._2 </ NullBot)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              (h_2,es)
            case Some(n) if n == 3 =>
              val appid = getArgValue(h_2, ctx_2, args, "1")
              val sucCB = getArgValue(h_2, ctx_2, args, "2")
              val es =
                if (appid._1._1 </ UndefBot && appid._1._2 </ NullBot)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (sucCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              (h_3, es ++ es_3)
            case Some(n) if n == 4 =>
              val appid = getArgValue(h_2, ctx_2, args, "1")
              val sucCB = getArgValue(h_2,ctx_2,args,"2")
              val errCB = getArgValue(h_2,ctx_2,args,"3")
              val es =
                if (appid._1._1 </ UndefBot && appid._1._2 </ NullBot)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (sucCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (errCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++
                LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              (h_5, es ++ es_3 ++ es_4)
            case Some(n) if n >= 5 =>
              val appid = getArgValue(h_2, ctx_2, args, "1")
              val sucCB = getArgValue(h_2,ctx_2,args,"2")
              val errCB = getArgValue(h_2,ctx_2,args,"3")
              val replyCB = getArgValue(h_2,ctx_2,args,"4")
              val es =
                if (appid._1._1 </ UndefBot && appid._1._2 </ NullBot)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (sucCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (errCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (h_3, es_5) = replyCB._2.foldLeft((h_2, TizenHelper.TizenExceptionBot))((_he, l) => {
                val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onsuccess"))
                val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onfailure"))
                val es1 =
                  if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es2 =
                  if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(TIZENapplicationObj.loc_appctrldataarr), T, T, T)))
                val h_3 = _he._1.update(l_r2, o_arr2)
                val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("AppCtrlDataArrayReplyCB.onsuccess"), Value(v1._2), Value(l_r2))
                val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("AppCtrlDataArrayReplyCB.onfailure"), Value(v2._2), Value(UndefTop))
                (h_5, _he._2 ++ es1 ++ es2)
              })
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++
                LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_4 = h_3.update(l_r1, o_arr)
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              (h_6, es ++ es_3 ++ es_4 ++ es_5)
            case _ => {
              (HeapBot, TizenHelper.TizenExceptionBot)
            }
          }
          val est = Set[WebAPIException](SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.findAppControl" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r = addrToLoc(addr1, Recent)
          val l_r1 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)
          val appctrl = getArgValue(h_2, ctx_2, args, "0")
          val sucCB = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_2, appctrl, Value(TIZENApplicationControl.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_3 =
            if (sucCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(2)).
            update("0", PropValue(ObjectValue(Value(TIZENapplicationObj.loc_appinfoarr), T, T, T))).
            update("1", PropValue(ObjectValue(Value(appctrl._2), T, T, T)))
          val h_3 = h_2.update(l_r, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FindAppCtrlSuccessCB"), Value(sucCB._2), Value(l_r))
          /* register success Callback and error Callback */
          val (h_5, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              (h_4,TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val errCB = getArgValue(h_4, ctx_2, args, "2")
              val es_4 =
                if (errCB._2.exists((l) =>  Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r1, o_arr1)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              (h_6, es_4)
            case _ => {
              (h_2, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ es_4)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.getAppsContext" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r = addrToLoc(addr1, Recent)
          val l_r1 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)
          val sucCB = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (sucCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENapplicationObj.loc_appctxtarr), T, T, T)))
          val h_3 = h_2.update(l_r, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("AppContextArraySuccessCB"), Value(sucCB._2), Value(l_r))
          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4,TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val errCB = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (errCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r1, o_arr1)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              (h_6, es_2)
            case _ => {
              (h_2, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.getAppContext" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val ctxid = getArgValue(h_1, ctx_1, args, "0")
          val o_new = Obj.empty.update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENApplicationContext.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("appId", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val o_new2 =
            if (ctxid._1._2 </ NullBot || ctxid._1._1 </ UndefBot)
              o_new.update("id", PropValue(ObjectValue(Value(StrTop), F, T, T)))
            else
              o_new.update("id", PropValue(ObjectValue(Value(Helper.toString(ctxid._1)), F, T, T)))
          val h_2 = h_1.update(l_r, o_new2)
          val est = Set[WebAPIException](NotFoundError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.getAppsInfo" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r = addrToLoc(addr1, Recent)
          val l_r1 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)
          val sucCB = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (sucCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENapplicationObj.loc_appinfoarr), T, T, T)))
          val h_3 = h_2.update(l_r, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("AppInfoArraySuccessCB"), Value(sucCB._2), Value(l_r))
          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4,TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val errCB = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (errCB._2.exists((l) =>  Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r1, o_arr1)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              (h_6, es_2)
            case _ => {
              (HeapBot, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.getAppInfo" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val appid = getArgValue(h_1, ctx_1, args, "0")
          val o_new = Obj.empty.update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENApplicationInformation.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("iconPath", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("version", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("show", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("categories", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("installDate", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("size", PropValue(ObjectValue(Value(NumTop), F, T, T))).
            update("packageId", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val o_new2 =
            if (appid._1._2 </ NullBot || appid._1._1 </ UndefBot)
              o_new.update("id", PropValue(ObjectValue(Value(StrTop), F, T, T)))
            else
              o_new.update("id", PropValue(ObjectValue(Value(Helper.toString(appid._1)), F, T, T)))
          val h_2 = h_1.update(l_r, o_new2)
          val est = Set[WebAPIException](NotFoundError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.getAppCerts" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val appid = getArgValue(h, ctx, args, "0")
          val est = Set[WebAPIException](SecurityError, NotFoundError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(TIZENapplicationObj.loc_appcertarr)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.getAppSharedURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val est = Set[WebAPIException](NotFoundError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.addAppInfoEventListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3)  = Helper.Oldify(h_2, ctx_2, addr3)
          val eventCB = getArgValue(h_3, ctx_3, args, "0")
          val (h_4, es) = eventCB._2.foldLeft((h_3, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("oninstalled"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("onuninstalled"))
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
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENapplicationObj.loc_appinfo), T, T, T)))
            val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENapplicationObj.loc_appinfo), T, T, T)))
            val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
            val h_2 = _he._1.update(l_r1, o_arr1).update(l_r2, o_arr2).update(l_r3, o_arr3)
            val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("AppInfoEventCB.oninstalled"), Value(v1._2), Value(l_r1))
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("AppInfoEventCB.onupdated"), Value(v2._2), Value(l_r2))
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("AppInfoEventCB.onuninstalled"), Value(v3._2), Value(l_r3))
            (h_5, _he._2 ++ es1 ++ es2 ++ es3)
          })
          val est = Set[WebAPIException](UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h_4, Value(NumTop)), ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.applicationObj.removeAppInfoEventListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](InvalidValuesError, NotFoundError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }
  override def getDefMap(): Map[String, AccessFun] = {
    Map()
  }
  override def getUseMap(): Map[String, AccessFun] = {
    Map()
  }
}
