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

object TIZENmessaging extends Tizen {
  private val name = "messaging"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_messaging
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_msgservice: Loc        = newSystemLoc("MessageService", Old)
  val loc_msgstorage: Loc        = newSystemLoc("MessageStorage", Old)
  val loc_msgbody: Loc        = newSystemLoc("MessageBody", Old)
  val loc_msgattach: Loc        = newSystemLoc("MessageAttachment", Old)
  val loc_msgattacharr: Loc        = newSystemLoc("MessageAttachmentArr", Old)
  val loc_msg: Loc        = newSystemLoc("Message", Old)
  val loc_msgarr: Loc        = newSystemLoc("MessageArr", Old)
  val loc_msgfolder: Loc        = newSystemLoc("MessageFolder", Old)
  val loc_msgfolderarr: Loc        = newSystemLoc("MessageFolderArr", Old)
  val loc_msgconv: Loc        = newSystemLoc("MessageConversation", Old)
  val loc_msgconvarr: Loc        = newSystemLoc("MessageConversationArr", Old)
  val loc_strarr: Loc        = newSystemLoc("StringArr", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_msgservice, prop_msgservice_ins), (loc_msgstorage, prop_msgstorage_ins),
    (loc_msgbody, prop_msgbody_ins), (loc_msgattach, prop_msgattach_ins), (loc_msgattacharr, prop_msgattacharr_ins),
    (loc_msg, prop_msg_ins), (loc_msgarr, prop_msgarr_ins), (loc_strarr, prop_strarr_ins), (loc_msgfolder, prop_msgfolder_ins),
    (loc_msgfolderarr, prop_msgfolderarr_ins), (loc_msgconv, prop_msgconv_ins), (loc_msgconvarr, prop_msgconvarr_ins)
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
    ("getMessageServices", AbsBuiltinFunc("tizen.messaging.getMessageServices", 3))
  )

  private val prop_msgservice_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENMessageService.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id",               AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("type",                 AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("messaging.sms") + AbsString.alpha("messaging.mms") +
                                                            AbsString.alpha("messaging.email")), F, T, T)))),
    ("name",               AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("messageStorage",               AbsConstValue(PropValue(ObjectValue(Value(loc_msgstorage), F, T, T))))
  )

  private val prop_msgstorage_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENMessageStorage.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T)))
  )

  private val prop_msgbody_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENMessageBody.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("messageId",          AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("loaded",          AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("plainBody",          AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("htmlBody",          AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("inlineAttachments",          AbsConstValue(PropValue(ObjectValue(Value(loc_msgattacharr), T, T, T))))
  )

  private val prop_msgattach_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENMessageAttachment.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("messageId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("mimeType", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("filePath", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_msgattacharr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_msgattach), T, T, T))))
  )

  private val prop_msg_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENMessage.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("conversationId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("folderId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("messaging.sms") + AbsString.alpha("messaging.mms") +
      AbsString.alpha("messaging.email")), F, T, T)))),
    ("timestamp", AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T)))),
    ("from", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("to", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), T, T, T)))),
    ("cc", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), T, T, T)))),
    ("bcc", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), T, T, T)))),
    ("body", AbsConstValue(PropValue(ObjectValue(Value(loc_msgbody), T, T, T)))),
    ("isRead", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("hasAttachment", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("isHighPriority", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("subject", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("inResponseTo", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("messageStatus", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("attachments", AbsConstValue(PropValue(ObjectValue(Value(loc_msgattacharr), T, T, T))))
  )

  private val prop_msgarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_msg), T, T, T))))
  )

  private val prop_msgfolder_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENMessageFolder.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("parentId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("serviceId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("contentType", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("messaging.sms") + AbsString.alpha("messaging.mms") +
      AbsString.alpha("messaging.email")), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("path", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("synchronizable", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T))))
  )

  private val prop_msgfolderarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_msgfolder), T, T, T))))
  )

  private val prop_msgconv_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENMessageConversation.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("messaging.sms") + AbsString.alpha("messaging.mms") +
      AbsString.alpha("messaging.email")), F, T, T)))),
    ("timestamp", AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T)))),
    ("messageCount", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T)))),
    ("unreadMessages", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    ("preview", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("subject", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("isRead", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("from", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("to", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("cc", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("bcc", AbsConstValue(PropValue(ObjectValue(Value(loc_strarr), F, T, T)))),
    ("lastMessageId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T))))
  )

  private val prop_msgconvarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_msgconv), T, T, T))))
  )

  private val prop_strarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.messaging.getMessageServices" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val v_1 = getArgValue(h_3, ctx_3, args, "0")
          val v_2 = getArgValue(h_3, ctx_3, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))

          val es =
            if (v_1._1._5 != AbsString.alpha("messaging.sms") && v_1._1._5 != AbsString.alpha("messaging.mms") &&
              v_1._1._5 != AbsString.alpha("messaging.email"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val o_arr = Helper.NewArrayObject(UInt).update("@default_number", PropValue(ObjectValue(Value(loc_msgservice), T, T, T)))
          val h_4 = h_3.update(l_r1, o_arr)
          val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
          val h_5 = h_4.update(l_r2, o_arr1)

          val (h_6, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("MsgServiceArrSuccessCB"), Value(v_2._2), Value(l_r2))
              (h_6, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h_5, ctx_3, args, "2")
              val es_2 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_5, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_6 = h_5.update(l_r3, o_arr2)
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("MsgServiceArrSuccessCB"), Value(v_2._2), Value(l_r2))
              val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r3))
              (h_8, es_2)
            case _ =>
              (h_5, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h_6, ctx_3), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
