/*******************************************************************************
    Copyright (c) 2013, S-Core.
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
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinArray
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue

object TIZENbluetooth extends Tizen {
  val name = "bluetooth"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_bluetooth
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_devMajor = newSystemRecentLoc(name + "DevMajor")
  val loc_devMinor = newSystemRecentLoc(name + "DevMinor")
  val loc_devService = newSystemRecentLoc(name + "DevService")

  val loc_btadapter: Loc = newSystemLoc("BluetoothAdapterObj", Old)
  val loc_btdevice: Loc = newSystemLoc("BluetoothDevice", Old)
  val loc_btdevicearr: Loc = newSystemLoc("BluetoothDeviceArr", Old)
  val loc_btclass: Loc = newSystemLoc("BluetoothClass", Old)
  val loc_btservhandler: Loc = newSystemLoc("BluetoothServiceHandler", Old)
  val loc_btsocket: Loc = newSystemLoc("BluetoothSocket", Old)
  val loc_strarr: Loc = newSystemLoc("bluetoothStrArr", Old)
  val loc_shortarr: Loc = newSystemLoc("bluetoothShortArr", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_btadapter, prop_btadapter_ins), (loc_btdevice, prop_btdevice_ins),
    (loc_btdevicearr, prop_btdevicearr_ins), (loc_btclass, prop_btclass_ins), (loc_strarr, prop_strarr_ins),
    (loc_shortarr, prop_shortarr_ins), (loc_btservhandler, prop_btservhandler_ins), (loc_btsocket, prop_btsocket_ins)
  )
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("deviceMajor", AbsConstValue(PropValue(ObjectValue(Value(loc_devMajor), F, F, F)))),
    ("deviceMinor", AbsConstValue(PropValue(ObjectValue(Value(loc_devMinor), F, F, F)))),
    ("deviceService", AbsConstValue(PropValue(ObjectValue(Value(loc_devService), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("getDefaultAdapter", AbsBuiltinFunc("tizen.bluetooth.getDefaultAdapter",0))
  )

  private val prop_btadapter_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENBluetoothAdapter.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("address", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("powered", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("visible", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_btdevice_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENBluetoothDevice.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("address", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("deviceClass", AbsConstValue(PropValue(ObjectValue(Value(loc_btclass), F, T, T)))),
    ("isBonded", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("isTrusted", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("isConnected", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("uuids", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T))))
  )

  private val prop_btdevicearr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_btdevice), T, T, T))))
  )

  private val prop_btclass_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENBluetoothClass.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("major", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("minor", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("services", AbsConstValue(PropValue(ObjectValue(Value(loc_shortarr), F, T, T))))
  )

  private val prop_btservhandler_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENBluetoothServiceHandler.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("uuid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("isConnected", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("onconnect", AbsConstValue(PropValue(ObjectValue(Value(NullTop), F, T, T))))
  )

  private val prop_btsocket_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENBluetoothSocket.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("uuid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("state", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("CLOSED") + AbsString.alpha("OPEN")), F, T, T)))),
    ("peer", AbsConstValue(PropValue(ObjectValue(Value(loc_btdevice), F, T, T)))),
    ("onmessage", AbsConstValue(PropValue(ObjectValue(Value(NullTop), T, T, T)))),
    ("onclose", AbsConstValue(PropValue(ObjectValue(Value(NullTop), T, T, T)))),
    ("onerror", AbsConstValue(PropValue(ObjectValue(Value(NullTop), T, T, T))))
  )

  private val prop_strarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )
  private val prop_shortarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.bluetooth.getDefaultAdapter" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(loc_btadapter)),ctx), (he + h_e, ctxe + ctx_e))
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

object TIZENBluetoothClassDeviceMajor extends Tizen {
  val name = "BluetoothClassDeviceMajor"
  /* predefined locations */
  val loc_obj = TIZENbluetooth.loc_devMajor
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("MISC", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00), F, T, T)))),
    ("COMPUTER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("PHONE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("NETWORK", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("AUDIO_VIDEO", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("PERIPHERAL", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("IMAGING", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x06), F, T, T)))),
    ("WEARABLE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x07), F, T, T)))),
    ("TOY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x08), F, T, T)))),
    ("HEALTH", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x09), F, T, T)))),
    ("UNCATEGORIZED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x1F), F, T, T))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
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

object TIZENBluetoothClassDeviceMinor extends Tizen {
  val name = "BluetoothClassDeviceMinor"
  /* predefined locations */
  val loc_obj = TIZENbluetooth.loc_devMinor
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("COPUTER_UNCATEGORIZED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00), F, T, T)))),
    ("COPUTER_DESKTOP", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("COPUTER_SERVER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("COPUTER_LAPTOP", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("COPUTER_HANDHELD_PC_OR_PDA", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("COPUTER_PALM_PC_OR_PDA", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("COPUTER_WEARABLE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x06), F, T, T)))),
    ("PHONE_UNCATEGORIZED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00), F, T, T)))),
    ("PHONE_CELLULAR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("PHONE_CORDLESS", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("PHONE_SMARTPHONE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("PHONE_MODEM_OR_GATEWAY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("PHONE_ISDN", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("AV_UNRECOGNIZED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00), F, T, T)))),
    ("AV_WEARABLE_HEADSET", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("AV_HANDSFREE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("AV_MICROPHONE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("AV_LOUDSPEAKER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("AV_HEADPHONES", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("AV_PORTABLE_AUDIO", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x06), F, T, T)))),
    ("AV_CAR_AUDIO", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x07), F, T, T)))),
    ("AV_SETTOP_BOX", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x08), F, T, T)))),
    ("AV_HIFI", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x09), F, T, T)))),
    ("AV_VCR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0a), F, T, T)))),
    ("AV_VIDEO_CAMERA", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0b), F, T, T)))),
    ("AV_CAMCORDER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0c), F, T, T)))),
    ("AV_MONITOR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0d), F, T, T)))),
    ("AV_DISPLAY_AND_LOUDSPEAKER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0e), F, T, T)))),
    ("AV_VIDEO_CONFERENCING", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0f), F, T, T)))),
    ("AV_GAMING_TOY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x10), F, T, T)))),
    ("AV_HEADPHONES", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x12), F, T, T)))),
    ("PERIPHERAL_UNCATEGORIZED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), F, T, T)))),
    ("PERIPHERAL_KEYBOARD", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x10), F, T, T)))),
    ("PERIPHERAL_POINTING_DEVICE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x20), F, T, T)))),
    ("PERIPHERAL_KEYBOARD_AND_POINTING_DEVICE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x30), F, T, T)))),
    ("PERIPHERAL_JOYSTICK", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("PERIPHERAL_GAMEPAD", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("PERIPHERAL_REMOTE_CONTROL", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("PERIPHERAL_SENSING_DEVICE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("PERIPHERAL_DEGITIZER_TABLET", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("PERIPHERAL_CARD_READER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x06), F, T, T)))),
    ("PERIPHERAL_DIGITAL_PEN", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x07), F, T, T)))),
    ("PERIPHERAL_HANDHELD_SCANNER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x08), F, T, T)))),
    ("PERIPHERAL_HANDHELD_INPUT_DEVICE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x09), F, T, T)))),
    ("IMAGING_UNCATEGORIZED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00), F, T, T)))),
    ("IMAGING_DISPLAY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("IMAGING_CAMERA", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x08), F, T, T)))),
    ("IMAGING_SCANNER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x10), F, T, T)))),
    ("IMAGING_PRINTER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x20), F, T, T)))),
    ("WEARABLE_WRITST_WATCH", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("WEARABLE_PAGER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("WEARABLE_JACKET", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("WEARABLE_HELMET", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("WEARABLE_GLASSES", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("TOY_ROBOT", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("TOY_VEHICLE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("TOY_DOLL", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("TOY_CONTROLLER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("TOY_GAME", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("HEALTH_UNDEFINED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00), F, T, T)))),
    ("HEALTH_BLOOD_PRESSURE_MONITOR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x01), F, T, T)))),
    ("HEALTH_THERMOMETER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x02), F, T, T)))),
    ("HEALTH_WEIGHING_SCALE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x03), F, T, T)))),
    ("HEALTH_GLUCOSE_METER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x04), F, T, T)))),
    ("HEALTH_PULSE_OXIMETER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x05), F, T, T)))),
    ("HEALTH_PULSE_RATE_MONITOR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x06), F, T, T)))),
    ("HEALTH_DATA_DISPLAY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x07), F, T, T)))),
    ("HEALTH_STEP_COUNTER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x08), F, T, T)))),
    ("HEALTH_BODY_COMPOISITION_ANALYZER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x09), F, T, T)))),
    ("HEALTH_PEAK_FLOW_MONITOR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0a), F, T, T)))),
    ("HEALTH_MEDICATION_MONITOR", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0b), F, T, T)))),
    ("HEALTH_KNEE_PROSTHESIS", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0c), F, T, T)))),
    ("HEALTH_ANKLE_PROSTHESIS", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0d), F, T, T))))
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
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

object TIZENBluetoothClassDeviceService extends Tizen {
  val name = "BluetoothClassDeviceService"
  /* predefined locations */
  val loc_obj = TIZENbluetooth.loc_devService
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("LIMITED_DISCOVERABILITY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0001), F, T, T)))),
    ("POSITIONING", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0008), F, T, T)))),
    ("NETWORKING", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0010), F, T, T)))),
    ("RENDERING", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0020), F, T, T)))),
    ("CAPTURING", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0040), F, T, T)))),
    ("OBJECT_TRANSFER", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0080), F, T, T)))),
    ("AUDIO", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0100), F, T, T)))),
    ("TELEPHONY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0200), F, T, T)))),
    ("INFORMATION", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x0400), F, T, T))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
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