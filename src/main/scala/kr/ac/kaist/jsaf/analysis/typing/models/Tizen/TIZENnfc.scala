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
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc

object TIZENnfc extends Tizen {
  private val name = "nfc"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_nfc
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_nfcadapter: Loc = newSystemLoc("NFCAdapter", Old)
  val loc_nfctag: Loc = newSystemLoc("NFCTag", Old)
  val loc_nfcpeer: Loc = newSystemLoc("NFCPeer", Old)
  val loc_ndefmsg: Loc = newSystemLoc("NDEFMessage", Old)
  val loc_ndefrecord: Loc = newSystemLoc("NDEFRecord", Old)
  val loc_ndefrecordarr: Loc = newSystemLoc("NDEFRecordArr", Old)
  val loc_bytearr: Loc = newSystemLoc("ByteArr", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_nfcadapter, prop_nfcadapter_ins), (loc_nfctag, prop_nfctag_ins),
    (loc_nfcpeer, prop_nfcpeer_ins), (loc_ndefmsg, prop_ndefmsg_ins), (loc_ndefrecord, prop_ndefrecord_ins),
    (loc_ndefrecordarr, prop_ndefrecordarr_ins), (loc_bytearr, prop_bytearr_ins)
  )
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("NFC_RECORD_TNF_EMPTY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), F, T, T)))),
    ("NFC_RECORD_TNF_WELL_KNOWN", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, T, T)))),
    ("NFC_RECORD_TNF_MIME_MEDIA", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, T, T)))),
    ("NFC_RECORD_TNF_URI", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(3), F, T, T)))),
    ("NFC_RECORD_TNF_EXTERNAL_RTD", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(4), F, T, T)))),
    ("NFC_RECORD_TNF_UNKNOWN", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(5), F, T, T)))),
    ("NFC_RECORD_TNF_UNCHANGED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(6), F, T, T))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("getDefaultAdapter", AbsBuiltinFunc("tizen.nfc.getDefaultAdapter",0))
  )

  private val prop_nfcadapter_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENNFCAdapter.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("powered", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_nfctag_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENNFCTag.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("GENERIC_TARGET") + AbsString.alpha("ISO14443_A") +
      AbsString.alpha("ISO14443_4A") + AbsString.alpha("ISO14443_3A") + AbsString.alpha("MIFARE_MINI") +
      AbsString.alpha("MIFARE_1K") + AbsString.alpha("MIFARE_4K") + AbsString.alpha("MIFARE_ULTRA") +
      AbsString.alpha("MIFARE_DESFIRE") + AbsString.alpha("ISO14443_B") + AbsString.alpha("ISO14443_4B") +
      AbsString.alpha("ISO14443_BPRIME") + AbsString.alpha("FELICA") + AbsString.alpha("JEWEL") +
      AbsString.alpha("ISO15693") + AbsString.alpha("UNKNOWN_TARGET")), F, T, T)))),
    ("isSupportedNDEF", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("ndefSize", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("properties", AbsConstValue(PropValue(ObjectValue(Value(UndefTop), F, T, T)))),
    ("isConnected", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_nfcpeer_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENNFCPeer.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("isConnected", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_ndefmsg_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENNDEFMessage.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("recordCount", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, T, T)))),
    ("records", AbsConstValue(PropValue(ObjectValue(Value(loc_ndefrecordarr), T, T, T))))
  )

  private val prop_ndefrecord_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENNDEFRecord.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("tnf", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(loc_bytearr), F, T, T)))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(loc_bytearr), F, T, T)))),
    ("payload", AbsConstValue(PropValue(ObjectValue(Value(loc_bytearr), F, T, T))))
  )

  private val prop_ndefrecordarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_ndefrecord), T, T, T))))
  )

  private val prop_bytearr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T))))
  )


  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.nfc.getDefaultAdapter" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(loc_nfcadapter)),ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}