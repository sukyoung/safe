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

object TIZENsysteminfo extends Tizen {
  private val name = "systeminfo"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_systeminfo
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_sysinfodevcapa: Loc = newSystemLoc("SystemInfoDeviceCapability", Old)
  val loc_sysinfobattery: Loc = newSystemLoc("SystemInfoBattery", Old)
  val loc_sysinfocpu: Loc = newSystemLoc("SystemInfoCpu", Old)
  val loc_sysinfostorage: Loc = newSystemLoc("SystemInfoStorage", Old)
  val loc_sysinfostorunit: Loc = newSystemLoc("SystemInfoStorageUnit", Old)
  val loc_sysinfostorunitarr: Loc = newSystemLoc("SystemInfoStorageUnitArr", Old)
  val loc_sysinfodisplay: Loc = newSystemLoc("SystemInfoDisplay", Old)
  val loc_sysinfodevori: Loc = newSystemLoc("SystemInfoDeviceOriendation", Old)
  val loc_sysinfobuild: Loc = newSystemLoc("SystemInfoBuild", Old)
  val loc_sysinfolocale: Loc = newSystemLoc("SystemInfoLocale", Old)
  val loc_sysinfonetwork: Loc = newSystemLoc("SystemInfoNetwork", Old)
  val loc_sysinfowifinetwork: Loc = newSystemLoc("SystemInfoWifiNetwork", Old)
  val loc_sysinfocellnetwork: Loc = newSystemLoc("SystemInfoCellularNetwork", Old)
  val loc_sysinfosim: Loc = newSystemLoc("SystemInfoSIM", Old)
  val loc_sysinfoperipheral: Loc = newSystemLoc("SystemInfoPeripheral", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_sysinfodevcapa, prop_sysinfodevcapa_ins), (loc_sysinfobattery, prop_sysinfobattery_ins),
    (loc_sysinfobuild, prop_sysinfobuild_ins), (loc_sysinfocellnetwork, prop_sysinfocellnetwork_ins), (loc_sysinfocpu, prop_sysinfocellnetwork_ins),
    (loc_sysinfodevori, prop_sysinfodevori_ins), (loc_sysinfodisplay, prop_sysinfodisplay_ins), (loc_sysinfolocale, prop_sysinfolocale_ins),
    (loc_sysinfonetwork, prop_sysinfonetwork_ins), (loc_sysinfoperipheral, prop_sysinfoperipheral_ins), (loc_sysinfosim, prop_sysinfosim_ins),
    (loc_sysinfostorage, prop_sysinfostorage_ins), (loc_sysinfostorunit, prop_sysinfostorunit_ins), (loc_sysinfostorunitarr, prop_sysinfostorunitarr_ins),
    (loc_sysinfowifinetwork, prop_sysinfowifinetwork_ins), (loc_sysinfocpu, prop_sysinfocpu_ins)
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
    ("getCapabilities", AbsBuiltinFunc("tizen.systeminfo.getCapabilities",2)),
    ("getPropertyValue", AbsBuiltinFunc("tizen.systeminfo.getPropertyValue",1)),
    ("addPropertyValueChangeListener", AbsBuiltinFunc("tizen.systeminfo.addPropertyValueChangeListener",1)),
    ("removePropertyValueChangeListener", AbsBuiltinFunc("tizen.systeminfo.removePropertyValueChangeListener",0))
  )

  private val prop_sysinfobattery_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoBattery.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("level", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("isCharging", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_sysinfocpu_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoCpu.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("load", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T))))
  )

  private val prop_sysinfostorage_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoStorage.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("units", AbsConstValue(PropValue(ObjectValue(Value(loc_sysinfostorunitarr), F, T, T))))
  )

  private val prop_sysinfostorunit_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoStorageUnit.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("capacity", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("availableCapacity", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("isRemovable", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_sysinfostorunitarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_sysinfostorunit), T, T, T))))
  )

  private val prop_sysinfodisplay_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoDisplay.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("resolutionWidth", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("resolutionHeight", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("dotsPerInchWidth", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("dotsPerInchHeight", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("physicalWidth", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("physicalHeight", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("brightness", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T))))
  )

  private val prop_sysinfodevori_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoDeviceOrientation.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("status", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("PORTRAIT_PRIMARY") + AbsString.alpha("PORTRAIT_SECONDARY") +
      AbsString.alpha("LANDSCAPE_PRIMARY") + AbsString.alpha("LANDSCAPE_SECONDARY")), F, T, T))))
  )

  private val prop_sysinfobuild_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoBuild.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("model", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("manufacturer", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_sysinfolocale_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoLocale.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("language", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("country", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_sysinfonetwork_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoNetwork.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("networkType", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("NONE") + AbsString.alpha("2G") +
      AbsString.alpha("2.5G") + AbsString.alpha("3G") + AbsString.alpha("4G") + AbsString.alpha("WIFI") +
      AbsString.alpha("ETHERNET") + AbsString.alpha("UNKNOWN")), F, T, T))))
  )

  private val prop_sysinfowifinetwork_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoWifiNetwork.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("status", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("ssid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("ipAddress", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("ipv6Address", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("signalStrength", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T))))
  )

  private val prop_sysinfocellnetwork_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoCellularNetwork.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("status", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("apn", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("ipAddress", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("ipv6Address", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("mcc", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("mnc", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("cellId", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("lac", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("isRoaming", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("isFlightMode", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("imei", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_sysinfosim_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoSIM.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("state", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("ABSENT") + AbsString.alpha("INITIALIZING") +
      AbsString.alpha("READY") + AbsString.alpha("PIN_REQUIRED") + AbsString.alpha("PUK_REQUIRED") + AbsString.alpha("NETWORK_LOCKED") +
      AbsString.alpha("SIM_LOCKED") + AbsString.alpha("UNKNOWN")), F, T, T)))),
    ("operatorName", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("msisdn", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("iccid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("mcc", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("mnc", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("msin", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("spn", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_sysinfoperipheral_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoPeripheral.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("isVideoOutputOn", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_sysinfodevcapa_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSystemInfoDeviceCapability.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("bluetooth", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("nfc", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("nfcReservedPush", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("multiTouchCount", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("inputKeyboard", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("inputKeyboardLayout", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("wifi", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("wifiDirect", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("opengles", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("openglestextureFormat", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("openglesVersion1_1", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("openglesVersion2_0", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("fmRadio", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("platformVersion", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("webApiVersion", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("nativeApiVersion", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("platformName", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("camera", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("cameraFront", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("cameraFrontFlash", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("cameraBack", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("cameraBackFlash", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("location", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("locationGps", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("locationWps", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("microphone", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("usbHost", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("usbAccessory", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("screenOutputRca", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("screenOutputHdmi", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("platformCoreCpuArch", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("platformCoreFpuArch", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("sipVoip", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("duid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("speechRecognition", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("speechSynthesis", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("accelerometer", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("accelerometerWakeup", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("barometer", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("barometerWakeup", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("gyroscope", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("gyroscopeWakeup", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("magnetometer", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("magnetometerWakeup", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("photometer", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("photometerWakeup", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("proximity", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("proximityWakeup", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("tiltmeter", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("tiltmeterWakeup", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("dataEncryption", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("graphicsAcceleration", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("push", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("telephony", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("telephonyMms", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("screenSizeNormal", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("screenSize480_800", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("screenSize720_1280", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("autoRotation", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("shellAppWidget", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("visionImageRecognition", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("visionQrcodeGeneration", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("visionQrcodeRecognition", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("visionFaceRecognition", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("secureElement", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("nativeOspCompatible", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))

  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.systeminfo.getCapabilities" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, SecurityError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(TIZENsysteminfo.loc_sysinfodevcapa)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.systeminfo.getPropertyValue" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
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
          val es_1 =
            if (v_1._1._5 != AbsString.alpha("BATTERY") && v_1._1._5 != AbsString.alpha("CPU") &&
              v_1._1._5 != AbsString.alpha("STORAGE") && v_1._1._5 != AbsString.alpha("DISPLAY") &&
              v_1._1._5 != AbsString.alpha("DEVICE_ORIENTATION") && v_1._1._5 != AbsString.alpha("BUILD") &&
              v_1._1._5 != AbsString.alpha("LOCALE") && v_1._1._5 != AbsString.alpha("NETWORK") &&
              v_1._1._5 != AbsString.alpha("WIFI_NETWORK") && v_1._1._5 != AbsString.alpha("CELLULAR_NETWORK") &&
              v_1._1._5 != AbsString.alpha("SIM") && v_1._1._5 != AbsString.alpha("PERIPHERAL"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = v_1.pvalue.strval.getAbsCase match {
            case AbsBot => ObjBot
            case _ if v_1.pvalue.strval.isAllNums => ObjBot
            case _ => v_1.pvalue.strval.gamma match {
              case None =>
                Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobattery) ++
                  LocSet(TIZENsysteminfo.loc_sysinfocpu) ++ LocSet(TIZENsysteminfo.loc_sysinfostorage) ++
                  LocSet(TIZENsysteminfo.loc_sysinfodisplay) ++ LocSet(TIZENsysteminfo.loc_sysinfodevori) ++
                  LocSet(TIZENsysteminfo.loc_sysinfobuild) ++ LocSet(TIZENsysteminfo.loc_sysinfolocale) ++
                  LocSet(TIZENsysteminfo.loc_sysinfonetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfowifinetwork) ++
                  LocSet(TIZENsysteminfo.loc_sysinfocellnetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfosim) ++
                  LocSet(TIZENsysteminfo.loc_sysinfoperipheral)), T, T, T)))
              case Some(vs) =>
                vs.foldLeft[Obj](ObjBot)((r, v) => {
                  r + (v match {
                    case "BATTERY" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                             update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobattery)), T, T, T)))
                    case "CPU" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfocpu)), T, T, T)))
                    case "STORAGE" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfostorage)), T, T, T)))
                    case "DISPLAY" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfodisplay)), T, T, T)))
                    case "DEVICE_ORIENTATION" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfodevori)), T, T, T)))
                    case "BUILD" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobuild)), T, T, T)))
                    case "LOCALE" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfolocale)), T, T, T)))
                    case "NETWORK" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfonetwork)), T, T, T)))
                    case "WIFI_NETWORK" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfowifinetwork)), T, T, T)))
                    case "CELLULAR_NETWORK" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfocellnetwork)), T, T, T)))
                    case "SIM" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfosim)), T, T, T)))
                    case "PERIPHERAL" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfoperipheral)), T, T, T)))
                    case _ =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobattery) ++
                        LocSet(TIZENsysteminfo.loc_sysinfocpu) ++ LocSet(TIZENsysteminfo.loc_sysinfostorage) ++
                        LocSet(TIZENsysteminfo.loc_sysinfodisplay) ++ LocSet(TIZENsysteminfo.loc_sysinfodevori) ++
                        LocSet(TIZENsysteminfo.loc_sysinfobuild) ++ LocSet(TIZENsysteminfo.loc_sysinfolocale) ++
                        LocSet(TIZENsysteminfo.loc_sysinfonetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfowifinetwork) ++
                        LocSet(TIZENsysteminfo.loc_sysinfocellnetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfosim) ++
                        LocSet(TIZENsysteminfo.loc_sysinfoperipheral)), T, T, T)))
                  })
                })
            }
          }
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("SystemInfoPropSuccessCB"), Value(v_2._2), Value(l_r1))
          val (h_5, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, SecurityError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.systeminfo.addPropertyValueChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val v_2 = getArgValue(h_1, ctx_1, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es_1 =
            if (v_1._1._5 != AbsString.alpha("BATTERY") && v_1._1._5 != AbsString.alpha("CPU") &&
              v_1._1._5 != AbsString.alpha("STORAGE") && v_1._1._5 != AbsString.alpha("DISPLAY") &&
              v_1._1._5 != AbsString.alpha("DEVICE_ORIENTATION") && v_1._1._5 != AbsString.alpha("BUILD") &&
              v_1._1._5 != AbsString.alpha("LOCALE") && v_1._1._5 != AbsString.alpha("NETWORK") &&
              v_1._1._5 != AbsString.alpha("WIFI_NETWORK") && v_1._1._5 != AbsString.alpha("CELLULAR_NETWORK") &&
              v_1._1._5 != AbsString.alpha("SIM") && v_1._1._5 != AbsString.alpha("PERIPHERAL"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = v_1.pvalue.strval.getAbsCase match {
            case AbsBot => ObjBot
            case _ if v_1.pvalue.strval.isAllNums => ObjBot
            case _ => v_1.pvalue.strval.gamma match {
              case None =>
                Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobattery) ++
                  LocSet(TIZENsysteminfo.loc_sysinfocpu) ++ LocSet(TIZENsysteminfo.loc_sysinfostorage) ++
                  LocSet(TIZENsysteminfo.loc_sysinfodisplay) ++ LocSet(TIZENsysteminfo.loc_sysinfodevori) ++
                  LocSet(TIZENsysteminfo.loc_sysinfobuild) ++ LocSet(TIZENsysteminfo.loc_sysinfolocale) ++
                  LocSet(TIZENsysteminfo.loc_sysinfonetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfowifinetwork) ++
                  LocSet(TIZENsysteminfo.loc_sysinfocellnetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfosim) ++
                  LocSet(TIZENsysteminfo.loc_sysinfoperipheral)), T, T, T)))
              case Some(vs) =>
                vs.foldLeft[Obj](ObjBot)((r, v) => {
                  r + (v match {
                    case "BATTERY" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobattery)), T, T, T)))
                    case "CPU" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfocpu)), T, T, T)))
                    case "STORAGE" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfostorage)), T, T, T)))
                    case "DISPLAY" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfodisplay)), T, T, T)))
                    case "DEVICE_ORIENTATION" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfodevori)), T, T, T)))
                    case "BUILD" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobuild)), T, T, T)))
                    case "LOCALE" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfolocale)), T, T, T)))
                    case "NETWORK" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfonetwork)), T, T, T)))
                    case "WIFI_NETWORK" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfowifinetwork)), T, T, T)))
                    case "CELLULAR_NETWORK" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfocellnetwork)), T, T, T)))
                    case "SIM" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfosim)), T, T, T)))
                    case "PERIPHERAL" =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfoperipheral)), T, T, T)))
                    case _ =>
                      Helper.NewArrayObject(AbsNumber.alpha(1)).
                        update("0", PropValue(ObjectValue(Value(LocSet(TIZENsysteminfo.loc_sysinfobattery) ++
                        LocSet(TIZENsysteminfo.loc_sysinfocpu) ++ LocSet(TIZENsysteminfo.loc_sysinfostorage) ++
                        LocSet(TIZENsysteminfo.loc_sysinfodisplay) ++ LocSet(TIZENsysteminfo.loc_sysinfodevori) ++
                        LocSet(TIZENsysteminfo.loc_sysinfobuild) ++ LocSet(TIZENsysteminfo.loc_sysinfolocale) ++
                        LocSet(TIZENsysteminfo.loc_sysinfonetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfowifinetwork) ++
                        LocSet(TIZENsysteminfo.loc_sysinfocellnetwork) ++ LocSet(TIZENsysteminfo.loc_sysinfosim) ++
                        LocSet(TIZENsysteminfo.loc_sysinfoperipheral)), T, T, T)))
                  })
                })
            }
          }
          val h_2 = h_1.update(l_r1, o_arr)
          val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("SystemInfoPropSuccessCB"), Value(v_2._2), Value(l_r1))
          val (h_4, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h_3, ctx_1, args, "2")
              val es_3 = v_3._2.foldLeft(TizenHelper.TizenExceptionBot)((_e, l) => {
                val v1 = Helper.Proto(h_3, l, AbsString.alpha("timeout"))
                val v2 = Helper.Proto(h_3, l, AbsString.alpha("highThreshold"))
                val v3 = Helper.Proto(h_3, l, AbsString.alpha("lowThreshold"))
                val es1 =
                  if (v1._1._4 </ NumTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es2 =
                  if (v2._1._4 </ NumTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es3 =
                  if (v3._1._4 </ NumTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                _e ++ es1 ++ es2 ++ es3
              })
              (h_3, es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, SecurityError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((Helper.ReturnStore(h_4, Value(UInt)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.systeminfo.removePropertyValueChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, SecurityError, InvalidValuesError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoBattery extends Tizen {
  private val name = "SystemInfoBattery"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoCpu extends Tizen {
  private val name = "SystemInfoCpu"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoStorage extends Tizen {
  private val name = "SystemInfoStorage"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoStorageUnit extends Tizen {
  private val name = "SystemInfoStorageUnit"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoDisplay extends Tizen {
  private val name = "SystemInfoDisplay"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoDeviceOrientation extends Tizen {
  private val name = "SystemInfoDeviceOrientation"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoBuild extends Tizen {
  private val name = "SystemInfoBuild"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoLocale extends Tizen {
  private val name = "SystemInfoLocale"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoNetwork extends Tizen {
  private val name = "SystemInfoNetwork"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoWifiNetwork extends Tizen {
  private val name = "SystemInfoWifiNetwork"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoCellularNetwork extends Tizen {
  private val name = "SystemInfoCellularNetwork"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoSIM extends Tizen {
  private val name = "SystemInfoSIM"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSystemInfoPeripheral extends Tizen {
  private val name = "SystemInfoPeripheral"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",              AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto",              AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",         AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
